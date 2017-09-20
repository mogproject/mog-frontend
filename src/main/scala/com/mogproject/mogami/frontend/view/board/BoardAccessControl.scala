package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.Player

/**
  *
  */
case class BoardAccessControl(selectablePlayers: Set[Player], playerNameSelectable: Boolean, isViewMode: Boolean) {

}
