package com.mogproject.mogami.frontend.action.game

import com.mogproject.mogami.core.move.Resign
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  *
  */
object ResignAction extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    for {
      gc <- model.mode.getGameControl
      g <- gc.game.updateBranch(gc.displayBranchNo)(b => Some(b.updateFinalAction(Some(Resign()))))
    } yield {
      model.copy(newMode = model.mode.setGameControl(gc.copy(game = g).withNextDisplayPosition))
    }
  }
}
