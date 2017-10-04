package com.mogproject.mogami.frontend.model.board

/**
  * Flip
  */
sealed trait FlipType {
  def unary_! : FlipType = this match {
    case FlipDisabled => FlipEnabled
    case FlipEnabled => FlipDisabled
    case DoubleBoard => DoubleBoard
  }
}

case object FlipDisabled extends FlipType

case object FlipEnabled extends FlipType

case object DoubleBoard extends FlipType
