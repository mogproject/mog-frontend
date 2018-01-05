package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._

/**
  *
  */
class CommandButton(label: MultiLingualLike,
                    clickAction: () => Unit,
                    buttonClass: Seq[String] = Seq("btn-default"),
                    isBlock: Boolean = false,
                    isDismiss: Boolean = false) extends WebComponent {

  private[this] val btn: Button = button(
    cls := (("btn" +: buttonClass) ++ isBlock.option("btn-block")).mkString(" "),
    onclick := clickAction,
    isDismiss.option(data("dismiss") := "modal"),
    label.elem
  ).render

  override lazy val element: HTMLElement = btn
}
