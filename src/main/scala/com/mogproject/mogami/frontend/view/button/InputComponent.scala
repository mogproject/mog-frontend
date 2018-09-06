package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.api.bootstrap.Tooltip
import com.mogproject.mogami.frontend.{ObserveFlag, PlaygroundModel, PlaygroundSAMObserver, WebComponent}
import com.mogproject.mogami.frontend.view.i18n.Messages
import org.scalajs.dom.Event
import org.scalajs.dom.html.{Div, Input, Span}
import scalatags.JsDom.all._

import scala.util.Try

/**
  * Generic text input
  */
case class InputComponent(placeholderFunc: Messages => String,
                          onChange: String => Unit,
                          onClear: () => Unit,
                          modifier: Modifier*) extends WebComponent with PlaygroundSAMObserver {

  private[this] val tooltipInterval = 2000 // in milli seconds

  private[this] lazy val textInput: Input = input(
    tpe := "text",
    cls := "form-control input-small",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top",
    modifier
  ).render

  private[this] lazy val textClear: Span = WebComponent.glyph("remove-circle", onclick := { () =>
    clearValue()
    showTooltip(Messages.get.TEXT_CLEARED)
    onClear()
    invokeOnChange()
  }).render

  override lazy val element: Div = {
    val elem = div(
      cls := "btn-group btn-block text-input",
      textInput,
      textClear
    ).render

    Try {
      elem.addEventListener("paste", (_: Event) => invokeOnChange(), useCapture = false)
      elem.addEventListener("keyup", (_: Event) => invokeOnChange(), useCapture = false)
    }
    elem
  }

  def updateValue(value: String): Unit = textInput.value = value

  def showTooltip(message: String): Unit = Tooltip.display(textInput, message, tooltipInterval)

  def clearValue(): Unit = updateValue("")

  def getValue: String = textInput.value

  private[this] def invokeOnChange(): Unit = {
    onChange(textInput.value)
  }

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.CONF_MSG_LANG

  private[this] def refresh(): Unit = {
    textInput.placeholder = placeholderFunc(Messages.get)
  }

  override def refresh(model: PlaygroundModel, flag: Long): Unit = {
    refresh()
  }

  refresh()
}
