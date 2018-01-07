package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.{BasePlaygroundModel, SAMObserver}
import com.mogproject.mogami.frontend.state.ObserveFlag
import org.scalajs.dom.Element

/**
  * Changes contents depending on the language setting.
  */
trait DynamicElementLike[T <: Element] extends SAMObserver[BasePlaygroundModel] {
  def element: T

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  def refresh(): Unit

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = refresh()

  // initialization
  refresh()
}
