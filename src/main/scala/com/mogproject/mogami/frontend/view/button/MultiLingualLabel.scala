package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.model.{English, Japanese}
import com.mogproject.mogami.frontend.state.ObserveFlag
import com.mogproject.mogami.frontend.{BasePlaygroundModel, Language, SAMObserver}
import org.scalajs.dom.raw.HTMLElement

/**
  *
  */
case class MultiLingualLabel(elem: HTMLElement, labels: Map[Language, String]) extends SAMObserver[BasePlaygroundModel] {
  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    labels.get(model.config.messageLang).foreach(elem.textContent = _)
  }
}

object MultiLingualLabel {
  def apply(elem: HTMLElement, english: String, japanese: String): MultiLingualLabel = new MultiLingualLabel(elem, Map(English -> english, Japanese -> japanese))
}