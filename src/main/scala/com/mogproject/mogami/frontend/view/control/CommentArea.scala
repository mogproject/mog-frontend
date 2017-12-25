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

  private[this] def clickAction(text: String): Unit = {
    val act = UpdateGameControlAction(gc => gc.copy(game = gc.game.updateComment(gc.gamePosition, text).getOrElse(gc.game)))

    if (isModal) {
      doAction(act, 1)
      /** @note Tooltip does not work on read-only elements */
    } else {
      doAction(act)
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
      refreshButtonDisabled()
    }
  }
}
