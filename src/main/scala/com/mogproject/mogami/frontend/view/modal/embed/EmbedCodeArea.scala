package com.mogproject.mogami.frontend.view.modal.embed

import com.mogproject.mogami.frontend.Messages
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.button.{CommandButton, TextAreaComponent}
import org.scalajs
import org.scalajs.dom.Element
import org.scalajs.dom.html.Button

import scalatags.JsDom.all._


/**
  *
  */
case class EmbedCodeArea(ident: String) extends WebComponent {
  private[this] val textArea: TextAreaComponent = TextAreaComponent("", 3, (_: Messages) => "",
    readonly := true,
    id := ident
  )

  private[this] val textCopyButton: CommandButton = CommandButton(
    classButtonDefaultBlock,
    Messages.get.COPY,
    clipboardTarget(ident),
    onclick := { () => scalajs.dom.window.setTimeout({ () => textCopyButton.element.asInstanceOf[Button].focus() }, 100) }
  )

  override def element: Element = div(
    textArea.element,
    div(cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4 col-xs-offset-8", textCopyButton.element)
    )
  ).render

  def updateValue(value: String): Unit = textArea.element.value = value

}
