package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.view.i18n.{DynamicHoverTooltip, Messages}
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement
import org.scalajs.dom.raw.HTMLElement

/**
  * Command button with a hover tooltip
  */
case class CommandButtonHoverable(labelElem: HTMLElement,
                                  clickAction: () => Unit,
                                  getTooltipMessage: Messages => String,
                                  placement: TooltipPlacement = TooltipPlacement.Bottom,
                                  buttonClass: Seq[String] = Seq("btn-default"),
                                  isBlock: Boolean = true,
                                  isDismiss: Boolean = false) extends CommandButtonLike(labelElem, clickAction, buttonClass, isBlock, isDismiss) with DynamicHoverTooltip {

}
