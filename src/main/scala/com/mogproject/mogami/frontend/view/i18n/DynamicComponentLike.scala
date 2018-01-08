package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element

import scalatags.JsDom.Frag

/**
  *
  */
trait DynamicComponentLike extends DynamicElementLike[Element] {
  self: WebComponent =>

  def getDynamicElements(messages: Messages): Seq[Frag]

  override def refresh(): Unit = {
    WebComponent.replaceChildElements(element, getDynamicElements(Messages.get).map(_.render))
  }
}
