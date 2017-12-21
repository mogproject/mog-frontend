package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.CopyButtonLike
import org.scalajs.dom.html.Anchor

import scalatags.JsDom.all._


/**
  *
  */
trait ViewButtonLike {
  self: CopyButtonLike =>

  protected val viewButton: Anchor = a(
    cls := "btn btn-default",
    tpe := "button",
    target := "_blank",
    "View"
  ).render

  protected def updateViewUrl(url: String): Unit = viewButton.href = url
}
