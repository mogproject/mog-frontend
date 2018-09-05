package com.mogproject.mogami.frontend.view.manage

import com.mogproject.mogami.frontend.view.button.CopyButtonLike

/**
  *
  */
class ExternalUrlCopyButton extends CopyButtonLike {
  override protected val ident = "external-url-copy"

  override def updateValue(value: String): Unit = {
    super.updateValue(value)
    copyButton.enableElement()
  }

  // initialization
  copyButton.disableElement()

}