package com.mogproject.mogami.frontend.state

import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.sam.{SAMAction, SAMState}
import com.mogproject.mogami.frontend.view.TestView

/**
  *
  */
case class TestState(model: BoardModel, view: TestView) extends SAMState[BoardModel] {
  override def render(newModel: BoardModel): (SAMState[BoardModel], Option[SAMAction[BoardModel]]) = {
    newModel.activeCursor match {
      case x if x == model.activeCursor =>
      case Some(sq) =>
        view.boardTest.board.effect.cursorEffector.start(view.boardTest.board.getRect(sq))
      case None =>
        view.boardTest.board.effect.cursorEffector.stop()
    }

    if (model.isFlipped != newModel.isFlipped) {
      view.boardTest.area.setFlip(newModel.isFlipped)
    }

    (copy(model = newModel), None)
  }
}
