package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.state.ObserveFlag
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.{BasePlaygroundModel, SAMObserver}
import org.scalajs.dom.Element

import scalatags.JsDom.TypedTag

/**
  *
  */
trait DynamicElement extends SAMObserver[BasePlaygroundModel] {
  self: WebComponent =>

  def getMessage(messages: Messages): TypedTag[Element]

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  private[this] def refresh(): Unit = {
    WebComponent.removeAllChildElements(element)
    element.appendChild(getMessage(Messages.get).render)
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    refresh()
  }

  refresh()
}
