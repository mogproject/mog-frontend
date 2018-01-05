package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.view.tooltip.{HoverTooltipEnabled, TooltipPlacement}
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement
import org.scalajs.dom.raw.HTMLElement

/**
  * Command button with a hover tooltip
  */
case class HoverCommandButton(label: HTMLElement,
                              clickAction: () => Unit,
                              tooltipMessages: Map[Language, String],
                              placement: TooltipPlacement = TooltipPlacement.Bottom,
                              buttonClass: Seq[String] = Seq("btn-default"),
                              isBlock: Boolean = true,
                              isDismiss: Boolean = false) extends CommandButtonLike(label, clickAction, buttonClass, isBlock, isDismiss) with HoverTooltipEnabled {

}
