package com.mogproject.mogami.frontend.view.control

import com.mogproject.mogami.frontend.action.{OpenCommentDialogAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button.SingleButton
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.html.{Div, TextArea}

import scalatags.JsDom.all._

/**
  *
  */
case class CommentComponent(isDisplayOnly: Boolean, isModal: Boolean, text: String = "") {

  //
  // Elements
  //
  lazy val textCommentInput: TextArea = textarea(
    cls := "form-control input-small",
    rows := 10,
    placeholder := "Comment",
    data("toggle") := "tooltip",
    data("trigger") := "manual",
    data("placement") := "top",
    isDisplayOnly.option(readonly := true),
    if (isDisplayOnly) {
      onclick := { () => PlaygroundSAM.doAction(OpenCommentDialogAction) }
    } else {
      onfocus := { () =>
        textClearButton.enableElement()
        textUpdateButton.enableElement()
      }
    },
    text
  ).render

  lazy val textClearButton: SingleButton = SingleButton(
    Map(English -> "Clear".render),
    clickAction = Some { () => clickAction("") },
    tooltip = isModal.fold(Map.empty, Map(English -> "Clear this comment")),
    tooltipPlacement = "top",
    isBlockButton = true,
    dismissModal = true
  )

  lazy val textUpdateButton: SingleButton = SingleButton(
    Map(English -> "Update".render),
    clickAction = Some { () => clickAction(textCommentInput.value) },
    tooltip = isModal.fold(Map.empty, Map(English -> "Update this comment")),
    tooltipPlacement = "top",
    isBlockButton = true,
    dismissModal = true
  )

  private[this] def clickAction(text: String): Unit = {
    val act = UpdateGameControlAction(gc => gc.copy(game = gc.game.updateComment(gc.gamePosition, text).getOrElse(gc.game)))

    if (isModal) {
      dom.window.setTimeout(() => PlaygroundSAM.doAction(act), 1)
    } else {
      PlaygroundSAM.doAction(act)
      displayCommentInputTooltip(text.isEmpty)
      refreshButtonDisabled()
    }
  }

  //
  // Tooltip
  //
  def displayCommentInputTooltip(isClear: Boolean): Unit = {
    Tooltip.display(textCommentInput, isClear.fold("Cleared!", "Updated!"), 2000)
  }

  def refreshButtonDisabled(): Unit = {
    Tooltip.hideToolTip(textClearButton.element)
    Tooltip.hideToolTip(textUpdateButton.element)
    textClearButton.setDisabled(textCommentInput.value.isEmpty)
    textUpdateButton.disableElement()
  }

}
