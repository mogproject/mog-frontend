package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.{ObserveFlag, PlaygroundModel, PlaygroundSAMObserver, WebComponent}
import com.mogproject.mogami.frontend.view.i18n.Messages
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._

/**
  * Generic text input
  */
case class InputComponent(placeholderFunc: Messages => String, modifier: Modifier*) extends WebComponent with PlaygroundSAMObserver {

  override lazy val element: Input = input(
    tpe := "text",
    cls := "form-control input-small",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top",
    modifier
  ).render

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.CONF_MSG_LANG

  private[this] def refresh(): Unit = {
    element.placeholder = placeholderFunc(Messages.get)
  }

  override def refresh(model: PlaygroundModel, flag: Long): Unit = {
    refresh()
  }

  refresh()
}
