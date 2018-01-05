package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.{BasePlaygroundModel, SAMObserver}
import com.mogproject.mogami.frontend.state.ObserveFlag
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/**
  *
  */
case class DynamicLabel(f: Messages => String) extends SAMObserver[BasePlaygroundModel] {
  val element: HTMLElement = span().render

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = element.innerHTML = f(Messages.get)
}
