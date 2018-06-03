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

    // purge unused state hashes
    if (model.config.isDebug) printStateCacheInfo(game, "Refreshing state hashes. Before: numKeys=")
    val validHashes = (Set(game) ++ model.mode.getGameControl.map(_.game)).flatMap(_.getStateHashSet)
    game.stateCache.refresh(validHashes)
    if (model.config.isDebug) printStateCacheInfo(game, "Refreshed state hashes. After: numKeys=")

    Some(model.copy(newMode = ViewMode(GameControl(newGame), None)))
  }

  private[this] def printStateCacheInfo(game: Game, message: String):Unit = {
    println(message + game.stateCache.numKeys)
  }
}
