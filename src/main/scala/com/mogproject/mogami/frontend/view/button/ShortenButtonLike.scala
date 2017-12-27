package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.api.google.URLShortener
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait ShortenButtonLike extends CopyButtonLike {
  override protected val labelString = ""

  def isMobile: Boolean

  def target: String

  private[this] val shortenButton = SingleButton(
    Map(English -> Seq(StringFrag("Shorten URL "), span(cls := s"glyphicon glyphicon-arrow-right", aria.hidden := true)).render),
    clickAction = Some({ () => clickAction() }),
    tooltip = isMobile.fold(Map.empty, Map(English -> "Create a short URL by Google URL Shortener"))
  )

  private[this] def clickAction(): Unit = {
    updateValue("creating...", completed = false)
    URLShortener.makeShortenedURL(target, updateValue(_, completed = true), updateValue(_, completed = false))
  }

  override lazy val element: Div = div(
    div(cls := "input-group",
      marginTop := 3,
      div(cls := "input-group-btn",
        shortenButton.element
      ),
      inputElem,
      div(cls := "input-group-btn",
        copyButton
      )
    )
  ).render

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
