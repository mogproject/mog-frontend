package com.mogproject.mogami.frontend.view.action

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.game.ResignAction
import com.mogproject.mogami.frontend.view.button.{CommandButton, SingleButton}
import com.mogproject.mogami.frontend.view.i18n.DynamicLabel
import com.mogproject.mogami.frontend.view.modal.YesNoDialog
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/**
  * Resign button
  */
case class ResignButton(isSmall: Boolean, confirm: Boolean) extends WebComponent with SAMObserver[BasePlaygroundModel] {

  private[this] val button = CommandButton(
    DynamicLabel(_.RESIGN, Some("flag")).element,
    () => clickAction(),
    "btn-default" +: Seq("thin-btn", "btn-resign").filter(_ => isSmall),
    isBlock = !isSmall,
    isDismiss = !isSmall
  )

  def clickAction(): Unit = if (confirm) {
    YesNoDialog(English, div("Do you really want to resign?"), () => clickActionImpl()).show()
  } else {
    clickActionImpl()
  }

  def clickActionImpl(): Unit = {
    doAction(ResignAction, 100)
  }

  override val element: HTMLElement = button.element

  //
  // Observer
  //
  override val samObserveMask: Int = {
    import ObserveFlag._
    MODE_TYPE | GAME_BRANCH | GAME_POSITION
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = setDisabled(!model.mode.canMakeMove)

}
