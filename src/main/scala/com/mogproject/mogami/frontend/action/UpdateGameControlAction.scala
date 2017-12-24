package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, GameControl}

/**
  *
  */
case class UpdateGameControlAction(f: GameControl => GameControl) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    model.mode.updateGameControl(f).map(m => model.copy(newMode = m, newSelectedCursor = None))
  }
}
