package com.mogproject.mogami.frontend.state

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.{BoardModel, DoubleBoard, FlipDisabled, FlipEnabled}
import com.mogproject.mogami.frontend.model.board.cursor.{CursorEvent, MouseDownEvent, MouseMoveEvent}
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.{Japanese, TestView}
import com.mogproject.mogami.frontend.view.board.{BoardCursor, BoxCursor, HandCursor, PlayerCursor}

/**
  *
  */
case class TestState(model: BoardModel, view: TestView) extends SAMState[BoardModel] {
  override def render(newModel: BoardModel): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = renderImpl(newModel)

  private[this] def renderImpl(newModel: BoardModel, renderAll: Boolean = false): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = {

    val fs: Seq[(Boolean, BoardModel => BoardModel)] = Seq(
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
    val m = newModel.cursorEvent match {
      //
      // Mouse Move
      //
      case Some(MouseMoveEvent(c)) =>
        // clear current cursor
        model.activeCursor match {
          case Some(BoardCursor(_)) => view.boardTest.board.effect.cursorEffector.stop()
          case Some(HandCursor(_)) => view.boardTest.hand.effect.cursorEffector.stop()
          case Some(PlayerCursor(_)) => view.boardTest.player.effect.cursorEffector.stop()
          case Some(BoxCursor(_)) => view.boardTest.box.effect.cursorEffector.stop()
          case _ => // do nothing
        }

        // draw new cursor
        val isValid = c match {
          case Some(BoardCursor(sq)) if model.mode.boardCursorAvailable =>
            view.boardTest.board.effect.cursorEffector.start(view.boardTest.board.getRect(sq))
            true
          case Some(HandCursor(h)) if model.mode.boardCursorAvailable =>
            view.boardTest.hand.effect.cursorEffector.start(view.boardTest.hand.getRect(h))
            true
          case Some(PlayerCursor(pl)) if model.mode.playerSelectable =>
            view.boardTest.player.effect.cursorEffector.start(view.boardTest.player.getRect(pl))
            true
          case Some(BoxCursor(pt)) if model.mode.boxAvailable =>
            view.boardTest.box.effect.cursorEffector.start(view.boardTest.box.layout.getRect(pt))
            true
          case _ =>
            false
        }
        newModel.copy(activeCursor = if (isValid) c else None)

      //
      // Mouse Down (Select)
      //
      case Some(MouseDownEvent(c)) if newModel.selectedCursor.isEmpty =>
        val isValid = c match {
          case Some(BoardCursor(sq)) if model.activeBoard.get(sq).exists(p => model.mode.playable(p.owner)) =>
            view.boardTest.board.effect.selectedEffector.start(view.boardTest.board.getRect(sq))
            view.boardTest.board.effect.selectingEffector.start(view.boardTest.board.getRect(sq))
            true
          case Some(HandCursor(h)) if model.mode.playable(h.owner) && model.activeHand.get(h).exists(_ > 0) =>
            view.boardTest.hand.effect.selectedEffector.start(view.boardTest.hand.getRect(h))
            view.boardTest.hand.effect.selectingEffector.start(view.boardTest.hand.getRect(h))
            true
          case Some(BoxCursor(pt)) if model.mode.boxAvailable && model.boxPieces.get(pt).exists(_ > 0) =>
            view.boardTest.box.effect.selectedEffector.start(view.boardTest.box.getPieceRect(pt))
            view.boardTest.box.effect.selectingEffector.start(view.boardTest.box.getPieceRect(pt))
            true
          case Some(PlayerCursor(_)) if model.mode.playerSelectable =>
            // todo: invoke player select
            false
          case _ =>
            false
        }
        newModel.copy(selectedCursor = isValid.fold(c, None))

      // todo: impl more mouse events
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
}
