package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.api.bootstrap.Tooltip
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.state.ObserveFlag
import com.mogproject.mogami.frontend.view.tooltip.TooltipEnabled
import com.mogproject.mogami.frontend.view.{BrowserInfo, WebComponent}
import com.mogproject.mogami.frontend.{BasePlaygroundModel, SAMObserver}


/**
  *
  */
trait DynamicHoverTooltip extends TooltipEnabled with SAMObserver[BasePlaygroundModel] {
  self: WebComponent =>

  def getTooltipMessage: Messages => String

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  private[this] def refresh(): Unit = {
    element.setAttribute("data-original-title", getTooltipMessage(Messages.get))
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    refresh()
  }

  private[this] def initialize(): Unit = {
    // disable if the browser detects touch features
    if (BrowserInfo.hasTouchEvent) {
      element.removeAttribute("data-toggle")
      SAM.removeObserver(this)
    } else {
      Tooltip.enableHoverToolTip(element)
      refresh()
    }
  }

  initialize()
}
