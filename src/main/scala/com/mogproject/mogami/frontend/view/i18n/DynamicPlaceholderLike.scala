package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.WebComponent
import org.scalajs.dom.html.Input

/**
  *
  */
trait DynamicPlaceholderLike extends DynamicElementLike[Input] {
  self: WebComponent =>

  def getPlaceholder(messages: Messages): String

  override def refresh(): Unit = {
    element match {
      case elem: Input => elem.placeholder = getPlaceholder(Messages.get)
      case _ =>
    }
  }
}
