package com.mogproject.mogami.frontend.view.tooltip


object TooltipPlacement {

  sealed abstract class TooltipPlacement(placement: String) {
    override def toString: String = placement
  }

  case object Top extends TooltipPlacement("top")

  case object Bottom extends TooltipPlacement("bottom")

  case object Left extends TooltipPlacement("left")

  case object Right extends TooltipPlacement("right")

}
