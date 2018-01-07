package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element

import scalatags.JsDom.Frag
import scalatags.JsDom.all._

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

object DynamicComponent {
  /**
    * Simple multi-lingual text label
    */
  def apply(f: Messages => String): DynamicComponentLike = new WebComponent with DynamicComponentLike {
    override lazy val element: Element = span().render

    override def getDynamicElements(messages: Messages): Seq[Frag] = Seq(StringFrag(f(messages)))
  }

  /**
    * Multi-lingual text label with a glyphicon
    */
  def apply(f: Messages => String, glyphicon: String): DynamicComponentLike = new WebComponent with DynamicComponentLike {
    override lazy val element: Element = span().render

    override def getDynamicElements(messages: Messages): Seq[Frag] = {
      Seq(StringFrag(f(messages) + " "), span(cls := s"glyphicon glyphicon-${glyphicon}", aria.hidden := true))
    }
  }

}