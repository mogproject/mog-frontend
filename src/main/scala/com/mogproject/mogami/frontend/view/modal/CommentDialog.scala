package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.control.CommentArea

import scalatags.JsDom.all._

/**
  * Game information dialog
  */
case class CommentDialog(messageLang: Language, text: String) extends ModalLike {

  private[this] lazy val commentButton = CommentArea(isDisplayOnly = false, isModal = true, text = text)

  override val title: String = messageLang match {
    case Japanese => "コメント"
    case English => "Comment"
  }

  override val modalBody: ElemType = div(bodyDefinition, commentButton.textCommentInput)

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4", commentButton.textClearButton),
      div(cls := "col-xs-offset-4 col-xs-4", commentButton.textUpdateButton)
    )
  )

}

