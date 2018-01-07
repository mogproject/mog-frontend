package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.WebComponent
import org.scalajs.dom.html.Input

import scalatags.JsDom.TypedTag

/**
  *
  */
trait DynamicPlaceholderLike extends DynamicElementLike[Input] {
  self: WebComponent =>

  def getPlaceholder: Messages => String

  def refresh(): Unit = {
    element match {
      case elem: Input => elem.placeholder = getPlaceholder(Messages.get)
      case _ =>
    }
  }
}

object DynamicPlaceholder {
  def apply(elem: Input, placeholderFunc: Messages => String): DynamicPlaceholderLike = new WebComponent with DynamicPlaceholderLike {
    override def element: Input = elem

    override def getPlaceholder: Messages => String = placeholderFunc
  }

  def apply(elemTags: TypedTag[Input], placeholderFunc: Messages => String): DynamicPlaceholderLike = apply(elemTags.render, placeholderFunc)
}