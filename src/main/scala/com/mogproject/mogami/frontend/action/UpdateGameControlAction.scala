package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.{PlaygroundModel, GameControl}

/**
  *
  */
case class UpdateGameControlAction(f: GameControl => GameControl) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.mode.updateGameControl { gc =>
      model.mode.isLivePlaying.when[GameControl](_.withLastDisplayPosition)(f(gc))
    }.map(m => model.copy(mode = m, selectedCursor = None))
  }
}
