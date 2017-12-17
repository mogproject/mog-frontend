package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  *
  */
case class MakeMoveAction(move: Move) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    Some(model)
  }

}
