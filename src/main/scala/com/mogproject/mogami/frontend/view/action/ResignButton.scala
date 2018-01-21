package com.mogproject.mogami.frontend.view.action

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.game.ResignAction
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.modal.YesNoDialog
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  * Resign button
  */
case class ResignButton(isSmall: Boolean, confirm: Boolean) extends WebComponent with SAMObserver[BasePlaygroundModel] {

  private[this] val button = CommandButton(
    classButtonDefault + " " + isSmall.fold("thin-btn btn-resign", classButtonBlock),
    onclick := { () => clickAction() },
    (!isSmall).option(dismissModal)
  )
    .withDynamicTextContent(_.RESIGN, "flag")

  def clickAction(): Unit = if (confirm) {
    YesNoDialog(div(Messages.get.ASK_RESIGN), () => clickActionImpl()).show()
  } else {
    clickActionImpl()
  }

  def clickActionImpl(): Unit = {
    doAction(ResignAction, 100)
  }

  override val element: Element = button.element

  //
  // Observer
  //
  override val samObserveMask: Long = {
    import ObserveFlag._
    MODE_TYPE | GAME_BRANCH | GAME_POSITION
  }

  override def refresh(model: BasePlaygroundModel, flag: Long): Unit = setDisabled(!model.mode.canMakeMove)

}
