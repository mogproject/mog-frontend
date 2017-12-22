package com.mogproject.mogami.frontend.view.reset

import com.mogproject.mogami.State
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.board.EditResetAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.view.button.SingleButton
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  * state, English label, Japanese label
  */
class EditResetButton extends WebComponent {
  private[this] val keys = Seq(
    (State.HIRATE, "Even", "平手"),
    (State.MATING_BLACK, "Mate (Black)", "詰将棋 (先手)"),
    (State.MATING_WHITE, "Mate (White)", "詰将棋 (後手)"),
    (State.HANDICAP_LANCE, "Lance", "香落ち"),
    (State.HANDICAP_BISHOP, "Bishop", "角落ち"),
    (State.HANDICAP_ROOK, "Rook", "飛車落ち"),
    (State.HANDICAP_ROOK_LANCE, "Rook-Lance", "飛香落ち"),
    (State.HANDICAP_2_PIECE, "2-Piece", "二枚落ち"),
    (State.HANDICAP_3_PIECE, "3-Piece", "三枚落ち"),
    (State.HANDICAP_4_PIECE, "4-Piece", "四枚落ち"),
    (State.HANDICAP_5_PIECE, "5-Piece", "五枚落ち"),
    (State.HANDICAP_6_PIECE, "6-Piece", "六枚落ち"),
    (State.HANDICAP_8_PIECE, "8-Piece", "八枚落ち"),
    (State.HANDICAP_10_PIECE, "10-Piece", "十枚落ち"),
    (State.HANDICAP_THREE_PAWNS, "Three Pawns", "歩三兵"),
    (State.HANDICAP_NAKED_KING, "Naked King", "裸玉")
  )

  private[this] val buttons = keys.map { case (st, en, ja) =>
    SingleButton(
      Map(English -> en.render, Japanese -> ja.render),
      clickAction = Some(() => doAction(EditResetAction(st))),
      isBlockButton = true,
      dismissModal = true
    )
  }

  override val element: Div = div(
    cls := "row", buttons.map(e => div(cls := "col-xs-6 col-sm-4", e.element))
  ).render

  def handleUpdate(model: BasePlaygroundModel, flags: Int): Unit = {
    if ((flags & 1) != 0) buttons.foreach(_.updateLabel(model.config.messageLang))
  }
}
