package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.state.ObserveFlag
import com.mogproject.mogami.frontend.{BasePlaygroundModel, SAMObserver}
import org.scalajs.dom.raw.HTMLElement

/**
  *
  */
trait MultiLingualLike extends SAMObserver[BasePlaygroundModel] {
  def elem: HTMLElement

  def isMulti: Boolean

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  private[this] def initialize(): Unit = {
    if (!isMulti) SAM.removeObserver(this)
  }

  initialize()
}
