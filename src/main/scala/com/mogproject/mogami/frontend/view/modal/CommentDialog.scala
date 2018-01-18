package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend.{BootstrapJQuery, _}
import com.mogproject.mogami.frontend.view.control.CommentComponent
import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  * Game information dialog
  */
case class CommentDialog(text: String, embeddedMode: Boolean) extends ModalLike {

  override def getTitle(messages: Messages): Frag = StringFrag(messages.COMMENT)

  private[this] val commentArea = CommentComponent(isDisplayOnly = false, isModal = true, text = text)

  override val modalBody: ElemType = div(bodyDefinition, commentArea.textCommentInput.element)

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4", commentArea.textClearButton.element),
      div(cls := "col-xs-offset-4 col-xs-4", commentArea.textUpdateButton.element)
    )
  )

  override def initialize(dialog: JQuery): Unit = {
    commentArea.refreshButtonDisabled()
  }
}

