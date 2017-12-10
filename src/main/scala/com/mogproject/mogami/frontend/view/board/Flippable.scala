package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.Player
import com.mogproject.mogami.util.Implicits._

/**
  * Flip
  */
trait Flippable {

  private[this] var isFlippedVal: Boolean = false

  def setFlip(flip: Boolean): Unit = {
    isFlippedVal = flip
  }

  def isFlipped: Boolean = isFlippedVal

  def getFlippedPlayer(player: Player): Player = isFlippedVal.when[Player](!_)(player)
}
