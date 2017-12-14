package com.mogproject.mogami.frontend.model

import com.mogproject.mogami._

/**
  *
  */
sealed abstract class Mode(val playable: Set[Player],
                           val playerSelectable: Boolean,
                           val forwardAvailable: Boolean,
                           val boxAvailable: Boolean) {
  def boardCursorAvailable: Boolean = playable.nonEmpty
}

case object ViewMode extends Mode(Set.empty, false, true, false)

case object EditMode extends Mode(Player.constructor.toSet, true, false, true)

case object PlayMode extends Mode(Player.constructor.toSet, true, false, false)

case class LiveMode(player: Option[Player]) extends Mode(player.toSet, false, false, false)
