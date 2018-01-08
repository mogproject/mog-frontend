package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.api.google.URLShortener
import com.mogproject.mogami.frontend._
import org.scalajs.dom.Element
import scalatags.JsDom.all._

/**
  * Shorten Button
  */
trait ShortenButtonLike extends CopyButtonLike {

  def target: String

  override protected def divClass: String = "shorten-bar"

  private[this] val shortenButton = CommandButton(classButtonDefault, onclick := { () => clickAction() })
    .withDynamicTextContent(_.SHORTEN_URL, "arrow-right")
    .withDynamicHoverTooltip(_.SHORTEN_URL_TOOLTIP)

  private[this] def clickAction(): Unit = {
    updateValue(Messages.get.SHORTEN_URL_CREATING + "...", completed = false)
    URLShortener.makeShortenedURL(target, updateValue(_, completed = true), updateValue(_, completed = false))
  }

  override protected def leftButton: Option[Element] = Some(shortenButton.element)

  def updateValue(value: String, completed: Boolean): Unit = {
    updateValue(value)
    shortenButton.setDisabled(completed)
    copyButton.setDisabled(!completed)
  }

  def clear(): Unit = {
    updateValue("", completed = false)
  }

  // initialize
  clear()
}
