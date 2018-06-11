package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.state.ObserveFlag
import org.scalajs.dom.Element

/**
  * Changes contents depending on the language setting.
  */
trait DynamicElementLike[T <: Element] extends PlaygroundSAMObserver {
  def element: T

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.CONF_MSG_LANG

  def refresh(): Unit

  override def refresh(model: PlaygroundModel, flag: Long): Unit = refresh()

  // initialization
  refresh()
}
