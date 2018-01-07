package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.api.google.URLShortener
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.i18n.DynamicHoverTooltip
import org.scalajs.dom.Element

/**
  * Shorten Button
  */
trait ShortenButtonLike extends CopyButtonLike {

  def target: String

  override protected def divClass: String = "shorten-bar"

  private[this] val shortenButton = DynamicHoverTooltip(CommandButton(
    DynamicComponent(_.SHORTEN_URL, "arrow-right").element,
    () => clickAction(),
    isBlock = false
  ), _.SHORTEN_URL_TOOLTIP)

  private[this] def clickAction(): Unit = {
    updateValue(Messages.get.SHORTEN_URL_CREATING + "...", completed = false)
    URLShortener.makeShortenedURL(target, updateValue(_, completed = true), updateValue(_, completed = false))
  }

  override protected def leftButton: Option[Element] = Some(shortenButton.element)

  def updateValue(value: String, completed: Boolean): Unit = {
    updateValue(value)
    shortenButton.setDisabled(completed)
    copyButton.disabled = !completed
  }

  def clear(): Unit = {
    updateValue("", completed = false)
  }

  // initialize
  clear()
}
