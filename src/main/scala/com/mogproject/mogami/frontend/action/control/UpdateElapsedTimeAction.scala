package com.mogproject.mogami.frontend.action.control

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.PlaygroundModel

/**
  * Update elapsed time information at the currently displaying position.
  *
  * @param elapsedTime elapsed time or None (when removing)
  */
case class UpdateElapsedTimeAction(elapsedTime: Option[Int]) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.mode.updateGameControl { gc =>
      gc.setElapsedTime(elapsedTime).getOrElse(gc) // recover to the original GameControl
    }.map(m => model.copy(mode = m))
  }
}
