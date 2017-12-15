package com.mogproject.mogami.frontend.state

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.{BoardModel, DoubleBoard, FlipDisabled, FlipEnabled}
import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.{Japanese, TestView}
import com.mogproject.mogami.frontend.view.board._

/**
  *
  */
case class TestState(model: BoardModel, view: TestView) extends SAMState[BoardModel] {
  override def render(newModel: BoardModel): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = renderImpl(newModel)

  private[this] def renderImpl(newModel: BoardModel, renderAll: Boolean = false): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = {

    val fs: Seq[(Boolean, BoardModel => BoardModel)] = Seq(
      (isUpdated(newModel, _.mode, _.config.layout, _.config.flipType, _.activeBoard, _.activeHand), unselect),
      (renderAll || isUpdated(newModel, _.config.layout), renderLayout),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceWidth), renderSize),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.recordLang), renderIndex),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.flipType), renderFlip),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceFace, _.activeBoard), renderBoard),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceFace, _.activeHand), renderHand),
      (renderAll || isUpdated(newModel, _.config.layout, _.playerNames), renderPlayerNames),
      (renderAll || isUpdated(newModel, _.config.layout, _.indicators), renderIndicators),
      (renderAll || isUpdated(newModel, _.config.layout, _.mode.boxAvailable), renderBox),
      (newModel.mode.boxAvailable && (renderAll || isUpdated(newModel, _.config.layout, _.mode.boxAvailable, _.config.pieceFace, _.activeBoard, _.activeHand)), renderBoxPieces),
      (renderAll || isUpdated(newModel, _.cursorEvent), renderMouseEvent)
    )

    val nextModel = fs.foldLeft(newModel) { case (m, (cond, f)) => if (cond) f(m) else m }
    (this.copy(model = nextModel), None)
  }

  private[this] def unselect(newModel: BoardModel): BoardModel = {
    view.boardTest.area.unselect()
    newModel.copy(selectedCursor = None)
  }

  private[this] def renderLayout(newModel: BoardModel): BoardModel = {
    view.setAreaLayout(newModel.config.layout)
    newModel
  }

  private[this] def renderSize(newModel: BoardModel): BoardModel = {
    view.boardTest.area.resize(newModel.config.pieceWidth)
    newModel
  }

  private[this] def renderIndex(newModel: BoardModel): BoardModel = {
    view.boardTest.area.board.drawIndexes(newModel.config.recordLang == Japanese)
    newModel
  }

  private[this] def renderFlip(newModel: BoardModel): BoardModel = {
    newModel.config.flipType match {
      case FlipEnabled => view.boardTest.area.setFlip(true)
      case FlipDisabled => view.boardTest.area.setFlip(false)
      case DoubleBoard => // todo
    }

    newModel
  }

  private[this] def renderBoard(newModel: BoardModel): BoardModel = {
    view.boardTest.board.drawPieces(newModel.activeBoard, newModel.config.pieceFace, keepLastMove = false)
    newModel
  }

  private[this] def renderHand(newModel: BoardModel): BoardModel = {
    view.boardTest.hand.drawPieces(newModel.activeHand, newModel.config.pieceFace, keepLastMove = false)
    newModel
  }

  private[this] def renderPlayerNames(newModel: BoardModel): BoardModel = {
    view.boardTest.player.drawNames(
      newModel.playerNames.getOrElse(BLACK, model.playerNames.getOrElse(BLACK, None)),
      newModel.playerNames.getOrElse(WHITE, model.playerNames.getOrElse(WHITE, None))
    )
    newModel
  }

  private[this] def renderIndicators(newModel: BoardModel): BoardModel = {
    view.boardTest.player.drawIndicators(
      newModel.indicators.getOrElse(BLACK, None),
      newModel.indicators.getOrElse(WHITE, None)
    )
    newModel
  }

  private[this] def renderBox(newModel: BoardModel): BoardModel = {
    if (newModel.mode.boxAvailable) {
      view.boardTest.area.showBox()
    } else {
      view.boardTest.area.hideBox()
    }
    newModel
  }

  private[this] def renderBoxPieces(newModel: BoardModel): BoardModel = {
    view.boardTest.box.drawPieces(newModel.boxPieces)
    newModel
  }

  private[this] def renderMouseEvent(newModel: BoardModel): BoardModel = {
    val m: BoardModel = newModel.cursorEvent match {
      //
      // Mouse Move
      //
      case Some(MouseMoveEvent(c)) if c != newModel.activeCursor =>
        // clear current cursor
        view.boardTest.area.clearActiveCursor()

        // draw new cursor
        if (cursorActivatable(newModel, c)) {
          c.foreach(view.boardTest.area.drawCursor)
          newModel.copy(activeCursor = c)
        } else {
          newModel.copy(activeCursor = None)
        }

      //
      // Mouse Down
      //
      case Some(MouseDownEvent(c)) => renderMouseDown(newModel, c)

      //
      // Mouse Up
      //
      case Some(MouseUpEvent(Some(c))) if newModel.selectedCursor.exists(_ != c && !c.isPlayer) =>
        // todo: adjust movement
        val adjusted = Some(c) // todo
        renderMouseDown(newModel, adjusted)

      //
      // Mouse Hold
      //
      case Some(MouseHoldEvent) if newModel.mode.forwardAvailable => {
        newModel.selectedCursor match {
          case Some(BoardCursor(sq)) => invokeViewForward(sq)
          case _ =>
        }
        newModel
      }
      case _ =>
        newModel
    }
    m.copy(cursorEvent = None)
  }

  override def initialize(): Unit = {
    renderImpl(model, renderAll = true)
  }

  private[this] def isUpdated[A](newModel: BoardModel, fs: (BoardModel => A)*): Boolean = {
    fs.exists(f => f(model) != f(newModel))
  }

  private[this] def invokeViewForward(square: Square): Unit = {
    if (square.file != 5) {
      view.boardTest.board.effect.forwardEffector.start(view.boardTest.board.isFlipped ^ square.file < 5)
    }
  }


  private[this] def renderMouseDown(newModel: BoardModel, cursor: Option[Cursor]): BoardModel = {
    if (cursorActivatable(newModel, cursor)) cursor.foreach(view.boardTest.area.flashCursor)

    cursor match {
      //
      // Player
      //
      case Some(PlayerCursor(pl)) if model.mode.playerSelectable =>
        unselect(newModel)
      // todo: invoke player select

      //
      // Forward/Backward
      //
      case Some(BoardCursor(c)) if newModel.mode.forwardAvailable =>
        invokeViewForward(c)
        newModel.copy(selectedCursor = cursor)

      //
      // Select
      //
      case Some(c) if newModel.selectedCursor.isEmpty && cursorSelectable(newModel, c) =>
        view.boardTest.area.select(c)
        newModel.copy(selectedCursor = cursor)

      //
      // Invoke
      //
      case Some(c) if newModel.selectedCursor.isDefined =>
        // todo: invoke move
        unselect(newModel)

      //
      // Reset selection
      //
      case None if newModel.selectedCursor.isDefined =>
        unselect(newModel)

      case _ =>
        newModel
    }
  }

  private[this] def cursorSelectable(newModel: BoardModel, cursor: Cursor): Boolean = {
    cursor match {
      case BoardCursor(sq) => newModel.activeBoard.get(sq).exists(p => newModel.mode.playable(p.owner))
      case HandCursor(h) => newModel.mode.playable(h.owner) && newModel.activeHand.get(h).exists(_ > 0)
      case BoxCursor(pt) => newModel.mode.boxAvailable && newModel.boxPieces.get(pt).exists(_ > 0)
      case _ => false
    }
  }

  private[this] def cursorActivatable(newModel: BoardModel, cursor: Option[Cursor]): Boolean = {
    cursor match {
      case Some(BoardCursor(_)) => newModel.mode.boardCursorAvailable
      case Some(HandCursor(_)) => model.mode.boardCursorAvailable
      case Some(PlayerCursor(_)) => model.mode.playerSelectable
      case Some(BoxCursor(_)) => model.mode.boxAvailable
      case _ => false
    }
  }
}