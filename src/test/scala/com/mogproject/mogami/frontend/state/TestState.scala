package com.mogproject.mogami.frontend.state

import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.TestView
import com.mogproject.mogami.frontend.view.board.{BoardCursor, HandCursor}

/**
  *
  */
case class TestState(model: BoardModel, view: TestView) extends SAMState[BoardModel] {
  override def render(newModel: BoardModel): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = {
    if (model.activeCursor != newModel.activeCursor) {
      // clear current cursor
      model.activeCursor match {
        case Some(BoardCursor(_)) => view.boardTest.board.effect.cursorEffector.stop()
        case Some(HandCursor(_)) => view.boardTest.hand.effect.cursorEffector.stop()
        case _ => // todo
      }

      // draw new cursor
      newModel.activeCursor match {
        case Some(BoardCursor(sq)) => view.boardTest.board.effect.cursorEffector.start(view.boardTest.board.getRect(sq))
        case Some(HandCursor(h)) => view.boardTest.hand.effect.cursorEffector.start(view.boardTest.hand.getRect(h))
        case _ => // todo
      }
    }

    if (model.isFlipped != newModel.isFlipped) {
      view.boardTest.area.setFlip(newModel.isFlipped)
    }

    (copy(model = newModel), None)
  }
}
