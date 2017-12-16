package com.mogproject.mogami.frontend.model.board

/**
  * Flip
  */
sealed abstract class FlipType(val numAreas: Int) {
  def unary_! : FlipType = this match {
    case FlipDisabled => FlipEnabled
    case FlipEnabled => FlipDisabled
    case DoubleBoard => DoubleBoard
  }
}

case object FlipDisabled extends FlipType(1)

case object FlipEnabled extends FlipType(1)

case object DoubleBoard extends FlipType(2)
