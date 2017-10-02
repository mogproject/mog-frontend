package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.Player

/**
  * Control attributes
  */
case class SVGAreaControl(isFlipped: Boolean, selectablePlayers: Set[Player], playerNameSelectable: Boolean, isViewMode: Boolean) {
  def boardCursorEnabled: Boolean = !isViewMode && selectablePlayers.nonEmpty
}
