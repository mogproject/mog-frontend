package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.State

/**
  * Japanese message definitions
  */
case object MessagesJapanese extends Messages {

  override val FLIP: String = "反転"

  override val RESIGN: String = "投了"

  override val ANALYZE: String = "解析"

  override val COUNT_POINT: String = "点数計算"

  override val COMMENT_CLEAR: String = "削除"
  override val COMMENT_UPDATE: String = "更新"

  override val INITIAL_STATE: Map[State, String] = Map(
    State.HIRATE -> "平手",
    State.MATING_BLACK -> "詰将棋 (先手)",
    State.MATING_WHITE -> "詰将棋 (後手)",
    State.HANDICAP_LANCE -> "香落ち",
    State.HANDICAP_BISHOP -> "角落ち",
    State.HANDICAP_ROOK -> "飛車落ち",
    State.HANDICAP_ROOK_LANCE -> "飛香落ち",
    State.HANDICAP_2_PIECE -> "二枚落ち",
    State.HANDICAP_3_PIECE -> "三枚落ち",
    State.HANDICAP_4_PIECE -> "四枚落ち",
    State.HANDICAP_5_PIECE -> "五枚落ち",
    State.HANDICAP_6_PIECE -> "六枚落ち",
    State.HANDICAP_8_PIECE -> "八枚落ち",
    State.HANDICAP_10_PIECE -> "十枚落ち",
    State.HANDICAP_THREE_PAWNS -> "歩三兵",
    State.HANDICAP_NAKED_KING -> "裸玉"
  )

}
