package com.mogproject.mogami.frontend.action.game

import com.mogproject.mogami.core.move.Resign
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  * Resign
  */
object ResignAction extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    for {
      gc <- model.mode.getGameControl
      g <- gc.makeSpecialMove(Resign(), moveForward = true)
    } yield {
      model.copy(newMode = model.mode.setGameControl(g))
    }
  }
}
