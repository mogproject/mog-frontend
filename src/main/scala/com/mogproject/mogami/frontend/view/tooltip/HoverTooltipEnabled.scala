package com.mogproject.mogami.frontend.view.tooltip

import com.mogproject.mogami.frontend.api.bootstrap.Tooltip
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.state.ObserveFlag
import com.mogproject.mogami.frontend.view.{BrowserInfo, WebComponent}
import com.mogproject.mogami.frontend.{BasePlaygroundModel, Language, SAMObserver}


/**
  *
  */
trait HoverTooltipEnabled extends TooltipEnabled with SAMObserver[BasePlaygroundModel] {
  self: WebComponent =>

  def tooltipMessages: Map[Language, String]

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  def refresh(lang: Language): Unit = {
    tooltipMessages.get(lang).foreach { s => element.setAttribute("data-original-title", s) }
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    refresh(model.config.messageLang)
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
