package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.i18n.Messages
import org.scalajs.dom.html.TextArea

import scalatags.JsDom.all._

/**
  *
  */
case class TextAreaComponent(text: String, numRows: Int, placeholderFunc: Messages => String, modifier: Modifier*) extends WebComponent with PlaygroundSAMObserver {

  // todo: refactor tooltip options
  override lazy val element: TextArea = textarea(
    cls := "form-control input-small",
    rows := numRows,
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top",
    text,
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
