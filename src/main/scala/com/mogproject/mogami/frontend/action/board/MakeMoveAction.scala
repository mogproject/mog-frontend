package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.sam.PlaygroundSAM

/**
  *
  */
case class MakeMoveAction(move: Move) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    val m = model.mode
    m.getGameControl.map { gc =>
      PlaygroundSAM.callbackMakeMove(move)
      model.copy(m.setGameControl(gc.makeMove(move, model.config.newBranchMode, moveForward = true).get))
    }
  }
}
