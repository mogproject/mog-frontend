package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.{CopyButtonLike, ShortenButtonLike}
import org.scalajs.dom.html.{Div, Input}

import scalatags.JsDom.all._

/**
  *
  */
class RecordCopyButton extends CopyButtonLike with WarningLabelLike {
  override protected val ident = "record-copy"

  override protected val labelString = "Record URL"

  private[this] def getTargetValue: String = getValue

  private[this] lazy val shortenButton = new ShortenButtonLike {
    override def target: String = getTargetValue

    override protected def ident: String = "record-short"
  }

  override lazy val element: Div = div(
    warningLabel,
    label(labelString),
    div(cls := "input-group",
      inputElem,
      div(
        cls := "input-group-btn",
        copyButton
      )
    ),
    shortenButton.element
  ).render

  override def updateValue(value: String): Unit = {
    super.updateValue(value)
    shortenButton.clear()
  }
}
