package com.mogproject.mogami.frontend.view.reset

import com.mogproject.mogami.State
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.board.EditResetAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.view.button.CommandButton
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
    State.HANDICAP_NAKED_KING,
    State.empty
  )

  private[this] lazy val buttons = keys.map { st =>
    CommandButton(
      classButtonDefaultBlock,
      onclick := { () => doAction(EditResetAction(st)) },
      dismissModal
    ).withDynamicTextContent(_.INITIAL_STATE(st))
  }

  override lazy val element: Div = div(cls := "row").render

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.MODE_EDIT

  override def refresh(model: BasePlaygroundModel, flag: Long): Unit = {
    if (model.mode.isEditMode) {
      if (!initialized) {
        buttons.foreach(e => element.appendChild(div(cls := "col-xs-6 col-sm-4", e.element).render))
        initialized = true
      }
    }
  }
}
