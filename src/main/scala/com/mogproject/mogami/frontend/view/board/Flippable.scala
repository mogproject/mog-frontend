package com.mogproject.mogami.frontend.view.board

/**
  * Flip
  */
trait Flippable {

  protected var isFlipped: Boolean = false

  def setFlip(flip: Boolean): Unit = {
    isFlipped = flip
  }

}
