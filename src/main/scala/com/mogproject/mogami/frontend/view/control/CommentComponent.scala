package com.mogproject.mogami.frontend.view.control

import com.mogproject.mogami.frontend.action.{OpenCommentDialogAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button._
import com.mogproject.mogami.frontend.view.i18n.DynamicLabel
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.html.{Div, TextArea}

import scalatags.JsDom.all._

/**
  *
  */
case class CommentComponent(isDisplayOnly: Boolean, isModal: Boolean, text: String = "") extends SAMObserver[BasePlaygroundModel] {

  //
  // Elements
  //
  lazy val textCommentInput: TextArea = textarea(
    cls := "form-control input-small",
    rows := 10,
    placeholder := "",
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

  private[this] val placeholders: Map[Language, String] = Map(English -> "Comment", Japanese -> "コメント")

  /** Must be 'val' to initialize the label */
  val textClearButton = CommandButtonHoverable(
    DynamicLabel(_.COMMENT_CLEAR).element,
    () => clickAction(""),
    _.COMMENT_CLEAR_TOOLTIP,
    TooltipPlacement.Top,
    isDismiss = true
  )

  val textUpdateButton = CommandButtonHoverable(
    DynamicLabel(_.COMMENT_UPDATE).element,
    () => clickAction(textCommentInput.value),
    _.COMMENT_UPDATE_TOOLTIP,
    TooltipPlacement.Top,
    isDismiss = true
  )

  private[this] def clickAction(text: String): Unit = {
    val act = UpdateGameControlAction(gc => gc.copy(game = gc.game.updateComment(gc.gamePosition, text).getOrElse(gc.game)))

    if (isModal) {
      dom.window.setTimeout(() => PlaygroundSAM.doAction(act), 1)
    } else {
      if (text.isEmpty) textCommentInput.value = "" // necessary: there might be unsaved text
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

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_MSG_LANG

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    placeholders.get(model.config.messageLang).foreach(textCommentInput.placeholder = _)
  }
}
