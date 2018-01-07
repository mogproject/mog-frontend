package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.api.bootstrap.Tooltip
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.view.tooltip.{TooltipEnabled, TooltipPlacement}
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement
import com.mogproject.mogami.frontend.view.{BrowserInfo, WebComponent}
import org.scalajs.dom.Element

import scalatags.JsDom.TypedTag


/**
  *
  */
trait DynamicHoverTooltipLike extends TooltipEnabled with DynamicElementLike[Element] {
  self: WebComponent =>

  def getTooltipMessage: Messages => String

  override def refresh(): Unit = {
    element.setAttribute("data-original-title", getTooltipMessage(Messages.get))
  }

  private[this] def initialize(): Unit = {
    // disable if the browser detects touch features
    if (BrowserInfo.hasTouchEvent) {
      element.removeAttribute("data-toggle")
      SAM.removeObserver(this)
    } else {
      Tooltip.enableHoverToolTip(element)
    }
  }

  initialize()
}

object DynamicHoverTooltip {
  def apply(elem: Element, tooltipFunc: Messages => String, tooltipPlacement: TooltipPlacement): WebComponent with DynamicHoverTooltipLike = new WebComponent with DynamicHoverTooltipLike {
    override def element: Element = elem

    override def placement: TooltipPlacement = tooltipPlacement

    override def getTooltipMessage: Messages => String = tooltipFunc
  }

  //  def apply(elemTags: TypedTag[Element], tooltipFunc: Messages => String, tooltipPlacement: TooltipPlacement): DynamicHoverTooltipLike = {
  //    apply(elemTags.render, tooltipPlacement, tooltipFunc)
  //  }

  def apply(component: WebComponent, tooltipFunc: Messages => String, tooltipPlacement: TooltipPlacement = TooltipPlacement.Bottom): WebComponent with DynamicHoverTooltipLike = {
    apply(component.element, tooltipFunc, tooltipPlacement)
  }
}