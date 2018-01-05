package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._

/**
  *
  */
class CommandButtonLike(label: HTMLElement,
                        clickAction: () => Unit,
                        buttonClass: Seq[String],
                        isBlock: Boolean,
                        isDismiss: Boolean) extends WebComponent {

  private[this] val btn: Button = button(
    cls := (("btn" +: buttonClass) ++ isBlock.option("btn-block")).mkString(" "),
    onclick := clickAction,
    isDismiss.option(data("dismiss") := "modal"),
    label
  ).render

  override lazy val element: HTMLElement = btn
}
