package com.mogproject.mogami.frontend.view.button

import org.scalajs.dom.Element

/**
  * Command button
  */
case class CommandButton(labelElem: Element,
                         clickAction: () => Unit,
                         buttonClass: Seq[String] = Seq("btn-default"),
                         isBlock: Boolean = true,
                         isDismiss: Boolean = false) extends CommandButtonLike(labelElem, clickAction, buttonClass, isBlock, isDismiss) {

}

