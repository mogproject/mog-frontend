package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button.CopyButtonLike
import org.scalajs.dom.Element
import org.scalajs.dom.html.Input
import scalatags.JsDom.all._

class NotesViewButton extends ShareUnit("notes", true) {
  private[this] lazy val withCommentOnlyText = WebComponent(span(cls := "checkbox-text")).withDynamicTextContent(_.WITH_COMMENT_ONLY)

  private[this] lazy val optionButton = div(cls := "input-group-btn",
    a(cls := "btn btn-default dropdown-toggle", tpe := "button", data("toggle") := "dropdown", aria.haspopup := true, aria.expanded := false,
      span(cls := "glyphicon glyphicon-cog"), " ", span(cls := "caret")),
    ul(cls := "dropdown-menu dropdown-menu-right checkbox-wrapper",
      li(withCommentOnlyCheckbox, withCommentOnlyText.element)
    )
  ).render

  private[this] lazy val withCommentOnlyCheckbox: Input = input(
    tpe := "checkbox", onchange := { () => copyBar.updateValueWithOption() }
  ).render

  class NotesViewCopyButton extends CopyButtonLike {
    override protected def ident: String = "notes-copy"

    override protected def rightButton: Option[Element] = Some(optionButton)

    override protected def viewButtonEnabled: Boolean = true

    /**
      * Called when the URL is modified from outside.
      *
      * @param value new URL
      */
    override def updateValue(value: String): Unit = updateValueWithOption(Some(value))

    def updateValueWithOption(baseUrl: Option[String] = None): Unit = {
      val withCommentOnlyOption = if (withCommentOnlyCheckbox.checked) "&wc=true" else ""
      val base = baseUrl.getOrElse(getValue)
      val url = base.replaceAll("[&]wc=\\w+", "") + withCommentOnlyOption

      super.updateValue(url)
    }
  }

  override val copyBar: NotesViewCopyButton = new NotesViewCopyButton

}