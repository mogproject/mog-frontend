package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.i18n.DynamicLabel
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement
import org.scalajs
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Button, Div, Input}

import scalatags.JsDom.all._

/**
  * Create a textbox and a copy button that copies the value of the text box.
  */
trait CopyButtonLike extends WebComponent {
  protected def ident: String

  protected def divClass: String = ""

  protected def viewButtonEnabled: Boolean = false

  protected def leftButton: Option[Element] = None

  protected def rightButton: Option[Element] = None

  private[this] lazy val viewButtonOpt: Option[ViewButton] = if (viewButtonEnabled) Some(new ViewButton) else None

  private[this] lazy val inputElem: Input = input(
    tpe := "text", id := ident, cls := "form-control", readonly := "readonly"
  ).render

  protected lazy val copyButton: Button = defaultButtonWithManualTooltip(
    TooltipPlacement.Bottom,
    onclick := { () => scalajs.dom.window.setTimeout({ () => copyButton.focus() }, 0) },
    data("clipboard-target") := s"#${ident}",
    DynamicLabel(_.COPY).element
  ).render

  override lazy val element: Div = div(
    cls := "input-group " + divClass,
    leftButton.map(div(cls := "input-group-btn", _)),
    inputElem,
    rightButton.map(div(cls := "input-group-btn", _)),
    div(cls := "input-group-btn", viewButtonOpt.map(_.element).toSeq :+ copyButton)
  ).render

  def updateValue(value: String): Unit = {
    inputElem.value = value
    viewButtonOpt.foreach(_.updateViewUrl(value))
  }

  def getValue: String = inputElem.value
}
