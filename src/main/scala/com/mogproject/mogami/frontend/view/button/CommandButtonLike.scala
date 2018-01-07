package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.Element
import org.scalajs.dom.html.Button

import scalatags.JsDom.all._

/**
  *
  */
class CommandButtonLike(labelElem: Element,
                        clickAction: () => Unit,
                        buttonClass: Seq[String],
                        isBlock: Boolean,
                        isDismiss: Boolean) extends WebComponent {

  private[this] val btn: Button = button(
    cls := (("btn" +: buttonClass) ++ isBlock.option("btn-block")).mkString(" "),
    tpe := "button",
    onclick := clickAction,
    isDismiss.option(data("dismiss") := "modal"),
    labelElem
  ).render

  override lazy val element: Element = btn
}
