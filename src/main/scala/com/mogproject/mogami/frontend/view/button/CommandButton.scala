package com.mogproject.mogami.frontend.view.button

import org.scalajs.dom.raw.HTMLElement

/**
  * Command button
  */
case class CommandButton(label: HTMLElement,
                         clickAction: () => Unit,
                         buttonClass: Seq[String] = Seq("btn-default"),
                         isBlock: Boolean = true,
                         isDismiss: Boolean = false) extends CommandButtonLike(label, clickAction, buttonClass, isBlock, isDismiss) {

}

