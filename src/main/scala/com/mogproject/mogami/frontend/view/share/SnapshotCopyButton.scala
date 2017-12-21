package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.{CopyButtonLike, ShortenButtonLike}
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
class SnapshotCopyButton extends CopyButtonLike {
  override protected val ident = "snapshot-copy"

  override protected val labelString = "Snapshot URL"

  private[this] def getTargetValue: String = getValue

  private[this] lazy val shortenButton = new ShortenButtonLike {
    override def target: String = getTargetValue

    override protected def ident: String = "snapshot-short"
  }

  override lazy val element: Div = div(
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
