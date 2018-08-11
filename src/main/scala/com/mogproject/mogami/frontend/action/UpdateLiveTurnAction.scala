package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.model.{LiveMode, PlaygroundModel}

/**
  * Sets the player's turn in Live Mode.
  */
case class UpdateLiveTurnAction(yourTurn: Player) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.mode match {
      case LiveMode(None, gc, _) => Some(model.copy(mode = LiveMode(Some(yourTurn), gc, Some(model.mode.isHandicapped))))
      case _ => None
    }
  }
}
