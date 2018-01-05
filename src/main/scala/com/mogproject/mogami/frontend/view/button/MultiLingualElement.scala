package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.model.{English, Japanese}
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.{BasePlaygroundModel, Language}
import org.scalajs.dom.Node
import org.scalajs.dom.raw.HTMLElement

/**
  *
  */
case class MultiLingualElement(elem: HTMLElement, labels: Map[Language, Node]) extends MultiLingualLike {

  override def isMulti: Boolean = labels.size > 1

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    labels.get(model.config.messageLang).foreach { lbl =>
      WebComponent.removeAllChildElements(elem)
      elem.appendChild(lbl)
    }
  }

}

object MultiLingualElement {
  def apply(elem: HTMLElement, english: Node, japanese: Node): MultiLingualElement = {
    new MultiLingualElement(elem, Map(English -> english, Japanese -> japanese))
  }
}