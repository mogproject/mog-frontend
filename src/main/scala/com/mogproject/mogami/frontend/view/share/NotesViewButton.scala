package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.{CopyButtonLike, ShortenButtonLike}
import org.scalajs.dom.html.{Div, Input}

import scalatags.JsDom.all._

/**
  *
  */
class NotesViewButton extends CopyButtonLike with ViewButtonLike {
  override protected val ident = "notes-view"

  override protected val labelString = "Notes View"

  private[this] def getTargetValue: String = getValue

  private[this] lazy val shortenButton = new ShortenButtonLike {
    override def target: String = getTargetValue

    override protected def ident: String = "notes-short"
  }

  override lazy val element: Div = div(
    label(labelString),
    div(cls := "input-group",
      inputElem,
      div(
        cls := "input-group-btn",
        viewButton,
        copyButton
      )
    ),
    shortenButton.element
  ).render

  override def updateValue(value: String): Unit = {
    super.updateValue(value)
    updateViewUrl(value)
    shortenButton.clear()
  }
}
