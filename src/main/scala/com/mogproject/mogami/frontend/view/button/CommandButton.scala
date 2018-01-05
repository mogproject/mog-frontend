package com.mogproject.mogami.frontend.view.button

/**
  * Command button
  */
case class CommandButton(label: MultiLingualLike,
                         clickAction: () => Unit,
                         buttonClass: Seq[String] = Seq("btn-default"),
                         isBlock: Boolean = false,
                         isDismiss: Boolean = false) extends CommandButtonLike(label, clickAction, buttonClass, isBlock, isDismiss) {

}

