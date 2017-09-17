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
        view.board.effect.startCursorEffect(sq)
        view.board.effect.startFlashCursorEffect(sq)
      case None =>
        view.board.effect.stopCursorEffect()
    }

    (copy(model = newModel), None)
  }
}
