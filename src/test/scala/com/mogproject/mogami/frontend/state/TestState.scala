package com.mogproject.mogami.frontend.state

import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.model.board.cursor.{CursorEvent, MouseMoveEvent}
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.{Japanese, TestView}
import com.mogproject.mogami.frontend.view.board.{BoardCursor, HandCursor}

/**
  *
  */
case class TestState(model: BoardModel, view: TestView) extends SAMState[BoardModel] {
  override def render(newModel: BoardModel): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = renderImpl(newModel)

  private[this] def renderImpl(newModel: BoardModel, renderAll: Boolean = false): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = {

    val fs: Seq[(Boolean, BoardModel => BoardModel)] = Seq(
      (renderAll || isUpdated(newModel, _.config.layout), renderLayout),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.boardWidth), renderSize),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.recordLang), renderIndex),
      (renderAll || isUpdated(newModel, _.config.layout, _.isFlipped), renderFlip),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceFace, _.activeBoard), renderBoard),
      (renderAll || isUpdated(newModel, _.config.layout, _.config.pieceFace, _.activeHand), renderHand),
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
    view.boardTest.area.resize(newModel.config.boardWidth)
    newModel
  }

  private[this] def renderIndex(newModel: BoardModel): BoardModel = {
    view.boardTest.area.board.drawIndexes(newModel.config.recordLang == Japanese)
    newModel
  }

  private[this] def renderFlip(newModel: BoardModel): BoardModel = {
    view.boardTest.area.setFlip(newModel.isFlipped) // todo: fix
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

  private[this] def renderMouseEvent(newModel: BoardModel): BoardModel = {
    val m = newModel.cursorEvent match {
      case Some(MouseMoveEvent(c)) =>
        // clear current cursor
        model.activeCursor match {
          case Some(BoardCursor(_)) => view.boardTest.board.effect.cursorEffector.stop()
          case Some(HandCursor(_)) => view.boardTest.hand.effect.cursorEffector.stop()
          case _ => // todo
        }

        // draw new cursor
        c match {
          case Some(BoardCursor(sq)) => view.boardTest.board.effect.cursorEffector.start(view.boardTest.board.getRect(sq))
          case Some(HandCursor(h)) => view.boardTest.hand.effect.cursorEffector.start(view.boardTest.hand.getRect(h))
          case _ => // todo
        }
        newModel.copy(activeCursor = c)
      // todo: impl more mouse events
      case _ => newModel // todo
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
