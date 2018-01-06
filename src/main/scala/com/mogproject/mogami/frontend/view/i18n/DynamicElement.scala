package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.state.ObserveFlag
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.{BasePlaygroundModel, SAMObserver}
import scalatags.JsDom.Frag

/**
  *
  */
trait DynamicElement extends SAMObserver[BasePlaygroundModel] {
  self: WebComponent =>

  def getDynamicElements(messages: Messages): Seq[Frag]

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  private[this] def refresh(): Unit = {
    WebComponent.replaceChildElements(element, getDynamicElements(Messages.get).map(_.render))
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    refresh()
  }

  refresh()
}
