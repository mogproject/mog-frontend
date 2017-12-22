package com.mogproject.mogami.frontend.action.game

import com.mogproject.mogami.Game
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, GameControl, ViewMode}
import com.mogproject.mogami.frontend.util.PlayerUtil

/**
  *
  */
case class LoadGameAction(game: Game) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    // replace empty player names with None's
    val newGame = game.copy(newGameInfo = PlayerUtil.normalizeGameInfo(game.gameInfo))

    Some(model.copy(newMode = ViewMode(GameControl(newGame))))
  }
}
