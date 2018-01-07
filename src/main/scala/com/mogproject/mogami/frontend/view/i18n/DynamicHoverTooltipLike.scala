package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.api.bootstrap.Tooltip
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.view.tooltip.TooltipEnabled
import com.mogproject.mogami.frontend.view.{BrowserInfo, WebComponent}
import org.scalajs.dom.Element


/**
  *
  */
trait DynamicHoverTooltipLike extends TooltipEnabled with DynamicElementLike[Element] {
  self: WebComponent =>

  def getTooltipMessage(messages: Messages): String

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
