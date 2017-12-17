package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.{Move, MoveFrom, Square}
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model._

/**
  *
  */
case class MakeMoveAction(move: Move) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    model.mode match {
      case PlayMode(gc, newBranchMode) => Some(model.copy(model.mode.setGameControl(gc.makeMove(move, newBranchMode, moveForward = true).get)))
      case LiveMode(_, gc) => Some(model.copy(model.mode.setGameControl(gc.makeMove(move, newBranchMode = false, moveForward = true).get)))
      case _ => None
    }
  }
}
