package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami.frontend.action.{OpenCommentDialogAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.model.GameControl
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.bootstrap.Tooltip
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.{Button, Div, TextArea}

import scalatags.JsDom.all._

/**
  *
  */
case class CommentArea(isDisplayOnly: Boolean, isModal: Boolean, text: String = "") extends WebComponent {

  //
  // Elements
  //
  lazy val textCommentInput: TextArea = textarea(
    cls := "form-control input-small",
    rows := isDisplayOnly.fold(2, isModal.fold(10, 5)),
    placeholder := "Comment",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top",
    if (isDisplayOnly) {
      readonly := true
    } else "",
    if (isDisplayOnly) {
      onclick := { () => PlaygroundSAM.doAction(OpenCommentDialogAction) }
    } else {
      onfocus := { () =>
        textClearButton.disabled = false
        textUpdateButton.disabled = false
      }
    },
    text
  ).render

  lazy val textClearButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "top",
    data("original-title") := s"Clear this comment",
    data("dismiss") := "modal",
    onclick := { () =>
      textCommentInput.value = ""
      PlaygroundSAM.doAction(getAction(""))
      if (!isModal) {
        textClearButton.disabled = true
        textUpdateButton.disabled = true
        displayCommentInputTooltip("Cleared!")
      }
    },
    "Clear"
  ).render

  lazy val textUpdateButton: Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "top",
    data("original-title") := s"Update this comment",
    data("dismiss") := "modal",
    onclick := { () =>
      val text = textCommentInput.value
      PlaygroundSAM.doAction(getAction(text))
      if (!isModal) {
        textUpdateButton.disabled = true
        displayCommentInputTooltip("Updated!")
      }
    },
    "Update"
  ).render

  // Layout
  override lazy val element: Div = div(
    cls := "center-block comment-" + isDisplayOnly.fold("mobile", "pc"),
    textCommentInput,
    if (isDisplayOnly) "" else div(
      cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4", textClearButton),
      div(cls := "col-xs-offset-4 col-xs-4", textUpdateButton)
    )
  ).render

  private[this] def getAction(text: String): UpdateGameControlAction = {
    UpdateGameControlAction(gc => gc.copy(game = gc.game.updateComment(gc.gamePosition, text).getOrElse(gc.game)))
  }

  //
  // Tooltip
  //
  def displayCommentInputTooltip(message: String): Unit = {
    Tooltip.display(textCommentInput, message, 2000)
  }

  //
  // Operations
  //
  def updateComment(comment: String): Unit = {
    textCommentInput.value = comment
    if (!isDisplayOnly) textClearButton.disabled = comment.isEmpty
  }
}
