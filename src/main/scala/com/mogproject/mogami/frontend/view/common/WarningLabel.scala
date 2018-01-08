package com.mogproject.mogami.frontend.view.common

import com.mogproject.mogami.frontend._
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  * Warning Label
  */
case class WarningLabel(f: Messages => String) extends WebComponent {

  override lazy val element: Element = WebComponent.dynamicDivElements({ (messages: Messages) =>
    Seq(
      strong(messages.WARNING + "!"),
      br(),
      StringFrag(f(messages))
    )
  },
    cls := "alert alert-warning",
    display := display.none.v
  ).element

}
