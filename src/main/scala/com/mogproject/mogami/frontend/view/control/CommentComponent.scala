package com.mogproject.mogami.frontend.view.control

import com.mogproject.mogami.frontend.action.{OpenCommentDialogAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button._
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
case class CommentComponent(isDisplayOnly: Boolean, isModal: Boolean, text: String = "") extends WebComponent {

  override def element: Element = ???

  //
  // Elements
  //
  lazy val textCommentInput: TextAreaComponent = TextAreaComponent(text, 10, _.COMMENT,
    isDisplayOnly.option(readonly := true),
    if (isDisplayOnly) {
      onclick := { () => PlaygroundSAM.doAction(OpenCommentDialogAction) }
    } else {
      onfocus := { () =>
        textClearButton.enableElement()
        textUpdateButton.enableElement()
      }
    }
  )

  /** Must be 'val' to initialize the label */
  val textClearButton: WebComponent = CommandButton(
    classButtonDefaultBlock,
    onclick := { () => clickAction("") },
    dismissModal
  )
    .withDynamicTextContent(_.COMMENT_CLEAR)
    .withDynamicHoverTooltip(_.COMMENT_CLEAR_TOOLTIP, TooltipPlacement.Top)

  val textUpdateButton: WebComponent = CommandButton(
    classButtonDefaultBlock,
    onclick := { () => clickAction(textCommentInput.element.value) },
    dismissModal
  )
    .withDynamicTextContent(_.COMMENT_UPDATE)
    .withDynamicHoverTooltip(_.COMMENT_UPDATE_TOOLTIP, TooltipPlacement.Top)

  private[this] def clickAction(text: String): Unit = {
    val act = UpdateGameControlAction(gc => gc.copy(game = gc.game.updateComment(gc.gamePosition, text).getOrElse(gc.game)))

    if (isModal) {
      dom.window.setTimeout(() => PlaygroundSAM.doAction(act), 1)
    } else {
      if (text.isEmpty) textCommentInput.element.value = "" // necessary: there might be unsaved text
      PlaygroundSAM.doAction(act)
      displayCommentInputTooltip(text.isEmpty)
      refreshButtonDisabled()
    }
  }

  //
  // Tooltip
  //
  def displayCommentInputTooltip(isClear: Boolean): Unit = {
    Tooltip.display(textCommentInput.element, isClear.fold(Messages.get.COMMENT_CLEARED, Messages.get.COMMENT_UPDATED), 2000)
  }

  def refreshButtonDisabled(): Unit = {
    Tooltip.hideToolTip(textClearButton.element)
    Tooltip.hideToolTip(textUpdateButton.element)
    textClearButton.setDisabled(textCommentInput.element.value.isEmpty)
    textUpdateButton.disableElement()
  }
}