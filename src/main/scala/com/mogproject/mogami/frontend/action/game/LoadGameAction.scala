package com.mogproject.mogami.frontend.action.game

import com.mogproject.mogami.Game
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, GameControl, ViewMode}

/**
  *
  */
case class LoadGameAction(game: Game) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = Some(model.copy(newMode = ViewMode(GameControl(game))))
}
