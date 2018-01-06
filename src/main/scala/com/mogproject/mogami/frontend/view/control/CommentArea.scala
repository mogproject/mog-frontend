package com.mogproject.mogami.frontend.view.control


import com.mogproject.mogami.frontend._
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
case class CommentArea(isDisplayOnly: Boolean, text: String = "") extends WebComponent with SAMObserver[BasePlaygroundModel] {

  //
  // Elements
  //

  val commentComponent = CommentComponent(isDisplayOnly, isModal = false, text)

  // Layout
  override lazy val element: Div = div(
    cls := "center-block comment-" + isDisplayOnly.fold("mobile", "pc"),
    commentComponent.textCommentInput.element,
    if (isDisplayOnly) "" else div(
      cls := "row",
      marginTop := 3,
      div(cls := "col-xs-4", commentComponent.textClearButton.element),
      div(cls := "col-xs-offset-4 col-xs-4", commentComponent.textUpdateButton.element)
    )
  ).render

  //
  // Observer
  //
  override val samObserveMask: Int = {
    import ObserveFlag._
    CONF_DEVICE | MODE_EDIT | GAME_COMMENT | GAME_POSITION
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    if (isFlagUpdated(flag, ObserveFlag.CONF_DEVICE)) {
      // set text input height
      commentComponent.textCommentInput.element.rows = isDisplayOnly.fold(model.config.deviceType.isLandscape.fold(10, 2), 5)
    }

    if (model.mode.isEditMode) {
      hide()
    } else {
      show()
      val comment = model.mode.getGameControl.flatMap(_.getComment).getOrElse("")
      commentComponent.textCommentInput.element.value = comment
      commentComponent.refreshButtonDisabled()
    }
  }
}
