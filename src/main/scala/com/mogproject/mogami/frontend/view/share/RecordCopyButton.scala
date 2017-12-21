package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.CopyButtonLike
import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

/**
  *
  */
class RecordCopyButton extends CopyButtonLike with WarningLabelLike {
  override protected val ident = "record-copy"

  override protected val labelString = "Record URL"

  override lazy val element: Div = div(
    warningLabel,
    label(labelString),
    div(cls := "input-group",
      inputElem,
      div(
        cls := "input-group-btn",
        copyButton
      )
    )
  ).render

}
