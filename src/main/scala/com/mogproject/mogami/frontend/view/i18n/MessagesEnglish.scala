package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.State

/**
  * English message definitions
  */
case object MessagesEnglish extends Messages {

  override val FLIP: String = "Flip"

  override val COMMENT_CLEAR: String = "Clear"
  override val COMMENT_CLEAR_TOOLTIP: String = "Clear this comment"
  override val COMMENT_UPDATE: String = "Update"
  override val COMMENT_UPDATE_TOOLTIP: String = "Update this comment"

  override val RESIGN: String = "Resign"

  override val ANALYZE: String = "Analyze"
  override val ANALYZE_CHECKMATE_TOOLTIP: String = "Analyze this position for checkmate"

  override val COUNT_POINT: String = "Count"
  override val COUNT_POINT_TOOLTIP: String = "Count points for this position"

  override val INITIAL_STATE: Map[State, String] = Map(
    State.HIRATE -> "Even",
    State.MATING_BLACK -> "Mate (Black)",
    State.MATING_WHITE -> "Mate (White)",
    State.HANDICAP_LANCE -> "Lance",
    State.HANDICAP_BISHOP -> "Bishop",
    State.HANDICAP_ROOK -> "Rook",
    State.HANDICAP_ROOK_LANCE -> "Rook-Lance",
    State.HANDICAP_2_PIECE -> "2-Piece",
    State.HANDICAP_3_PIECE -> "3-Piece",
    State.HANDICAP_4_PIECE -> "4-Piece",
    State.HANDICAP_5_PIECE -> "5-Piece",
    State.HANDICAP_6_PIECE -> "6-Piece",
    State.HANDICAP_8_PIECE -> "8-Piece",
    State.HANDICAP_10_PIECE -> "10-Piece",
    State.HANDICAP_THREE_PAWNS -> "Three Pawns",
    State.HANDICAP_NAKED_KING -> "Naked King"
  )
}