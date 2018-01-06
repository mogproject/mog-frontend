package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/**
  * Warning Label
  */
class WarningLabel extends WebComponent {

  override lazy val element: HTMLElement = div(
    cls := "alert alert-warning",
    display := display.none.v,
    strong("Warning!"),
    " Comments will not be shared due to the URL length limit."
  ).render

}
