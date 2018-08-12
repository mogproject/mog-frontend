package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.model.{LiveMode, PlaygroundModel}

/**
  * Sets the player's turn in Live Mode.
  */
case class UpdateLiveStatusAction(yourTurn: Option[Player], online: Map[Player, Boolean]) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.mode match {
      case LiveMode(_, _, gc, _) => Some(model.copy(mode = LiveMode(yourTurn, online, gc, Some(model.mode.isHandicapped))))
      case _ => None
    }
  }
}
