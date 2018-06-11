package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.GameInfo
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.PlaygroundModel
import com.mogproject.mogami.frontend.util.PlayerUtil

/**
  * Update GameInfo
  */
case class UpdateGameInfoAction(gameInfo: GameInfo) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    // replace empty player names with None's
    val newGameInfo = PlayerUtil.normalizeGameInfo(gameInfo)

    model.mode.updateGameControl(gc => gc.copy(game = gc.game.copy(newGameInfo = newGameInfo))).map(m => model.copy(mode = m))
  }
}
