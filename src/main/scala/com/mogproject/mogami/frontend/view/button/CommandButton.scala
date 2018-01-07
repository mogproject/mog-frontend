package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend._
import org.scalajs.dom.Element

import scalatags.JsDom.all._


/**
  * Command button
  */
case class CommandButton(className: String,  modifier: Modifier*) extends WebComponent {
  override def element: Element = button(
    cls := "btn " + className,
    tpe := "button",
    modifier
  ).render
}
