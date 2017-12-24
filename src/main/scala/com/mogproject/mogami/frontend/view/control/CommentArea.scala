package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami.frontend.action.{OpenCommentDialogAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button.SingleButton
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.{Div, TextArea}

import scalatags.JsDom.all._

/**
  *
  */
case class CommentArea(isDisplayOnly: Boolean, isModal: Boolean, text: String = "") extends WebComponent with SAMObserver[BasePlaygroundModel] {

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
        textClearButton.enableElement()
        textUpdateButton.enableElement()
      }
    },
    text
  ).render

  lazy val textClearButton: SingleButton = SingleButton(
    Map(English -> "Clear".render),
    clickAction = Some { () =>
      textCommentInput.value = ""
      doAction(getAction(""))
      if (!isModal) {
        textClearButton.disableElement()
        textUpdateButton.disableElement()
        displayCommentInputTooltip("Cleared!")
      }
    },
    tooltip = Map(English -> "Clear this comment"),
    tooltipPlacement = "top",
    isBlockButton = true,
    dismissModal = true
  )

  lazy val textUpdateButton: SingleButton = SingleButton(
    Map(English -> "Update".render),
    clickAction = Some { () =>
      val text = textCommentInput.value
      PlaygroundSAM.doAction(getAction(text))
      if (!isModal) {
        textUpdateButton.disableElement()
        displayCommentInputTooltip("Updated!")
      }
    },
    tooltip = Map(English -> "Update this comment"),
    tooltipPlacement = "top",
    isBlockButton = true,
    dismissModal = true
  )

  // Layout
  override lazy val element: Div = div(
    cls := "center-block comment-" + isDisplayOnly.fold("mobile", "pc"),
    textCommentInput,
    if (isDisplayOnly) "" else div(
      cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4", textClearButton.element),
      div(cls := "col-xs-offset-4 col-xs-4", textUpdateButton.element)
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
  // Observer
  //
  override val samObserveMask: Int = {
    import ObserveFlag._
    MODE_EDIT | GAME_COMMENT | GAME_POSITION
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    if (model.mode.isEditMode) {
      hide()
    } else {
      show()
      val comment = model.mode.getGameControl.flatMap(_.getComment).getOrElse("")
      textCommentInput.value = comment
      if (!isDisplayOnly) textClearButton.setDisabled(comment.isEmpty)
    }
  }
}
