package com.mogproject.mogami.frontend.view.tooltip

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement

/**
  *
  */
trait TooltipEnabled {
  self: WebComponent =>

  def placement: TooltipPlacement

  private[this] def initialize(): Unit = {
    element.setAttribute("data-toggle", "tooltip")
    element.setAttribute("data-placement", placement.toString)
  }

  initialize()
}
