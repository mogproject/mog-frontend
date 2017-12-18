package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.GameInfo
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel

/**
  * Update GameInfo
  */
case class UpdateGameInfoAction(gameInfo: GameInfo) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    model.mode.updateGameControl(gc => gc.copy(game = gc.game.copy(newGameInfo = gameInfo))).map(m => model.copy(newMode = m))
  }
}
