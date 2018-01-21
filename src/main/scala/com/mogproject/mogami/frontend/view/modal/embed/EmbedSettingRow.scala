package com.mogproject.mogami.frontend.view.modal.embed

import com.mogproject.mogami.frontend.Messages
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element
import scalatags.JsDom.all._

/**
  *
  */
trait EmbedSettingRow[A] extends WebComponent {
  def labelFunc: Messages => String

  def labelClass: String

  def buttonClass: String

  private[this] val labelElem = WebComponent.dynamicLabel(labelFunc)

  protected def buttonElem: Element

  override lazy val element: Element = div(cls := "row setting-row",
    div(cls := "setting-row-label " + labelClass, labelElem.element),
    div(cls := buttonClass, buttonElem)
  ).render

  def select(item: A): Unit

  def getValue: A
}
