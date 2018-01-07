package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Anchor

import scalatags.JsDom.all._


/**
  * View Button
  */
class ViewButton extends WebComponent {

  override lazy val element: Anchor = a(
    cls := "btn btn-default",
    tpe := "button",
    target := "_blank",
    DynamicComponent(_.VIEW).element
  ).render

  def updateViewUrl(url: String): Unit = element.href = url
}
