package com.mogproject.mogami.frontend.view.reset

import com.mogproject.mogami.State
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.board.EditResetAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.view.button.{CommandButton, MultiLingualLabel, SingleButton}
import com.mogproject.mogami.frontend.view.i18n.DynamicLabel
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  * state, English label, Japanese label
  */
class EditResetButton extends WebComponent with SAMObserver[BasePlaygroundModel] {

  private[this] var initialized: Boolean = false

  /** This can be costly, so delay the initialization */
  private[this] lazy val keys = Seq(
    State.HIRATE,
    State.MATING_BLACK,
    State.MATING_WHITE,
    State.HANDICAP_LANCE,
    State.HANDICAP_BISHOP,
    State.HANDICAP_ROOK,
    State.HANDICAP_ROOK_LANCE,
    State.HANDICAP_2_PIECE,
    State.HANDICAP_3_PIECE,
    State.HANDICAP_4_PIECE,
    State.HANDICAP_5_PIECE,
    State.HANDICAP_6_PIECE,
    State.HANDICAP_8_PIECE,
    State.HANDICAP_10_PIECE,
    State.HANDICAP_THREE_PAWNS,
    State.HANDICAP_NAKED_KING
  )

  private[this] lazy val buttons = keys.map { st =>
    CommandButton(
      DynamicLabel(_.INITIAL_STATE(st)).element,
      () => doAction(EditResetAction(st)),
      isDismiss = true
    )
  }

  override lazy val element: Div = div(cls := "row").render

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.MODE_EDIT

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    if (model.mode.isEditMode) {
      if (!initialized) {
        buttons.foreach(e => element.appendChild(div(cls := "col-xs-6 col-sm-4", e.element).render))
        SAM.notifyObservers(ObserveFlag.CONF_MSG_LANG)
        initialized = true
      }
    }
  }
}
