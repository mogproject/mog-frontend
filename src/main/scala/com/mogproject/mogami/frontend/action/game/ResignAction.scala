package com.mogproject.mogami.frontend.action.game

import com.mogproject.mogami.core.move.Resign
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.PlaygroundModel

/**
  * Resign
  */
object ResignAction extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    for {
      gc <- model.mode.getGameControl
      g <- gc.makeSpecialMove(Resign(), moveForward = true)
    } yield {
      model.copy(mode = model.mode.setGameControl(g))
    }
  }
}
