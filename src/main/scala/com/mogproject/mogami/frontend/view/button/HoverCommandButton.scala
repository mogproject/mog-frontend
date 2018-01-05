package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.view.tooltip.HoverTooltipEnabled
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement

/**
  * Command button with a hover tooltip
  */
case class HoverCommandButton(label: MultiLingualLike,
                              clickAction: () => Unit,
                              tooltipMessages: Map[Language, String],
                              placement: TooltipPlacement,
                              buttonClass: Seq[String] = Seq("btn-default"),
                              isBlock: Boolean = false,
                              isDismiss: Boolean = false) extends CommandButton(label, clickAction, buttonClass, isBlock, isDismiss) with HoverTooltipEnabled {

}
