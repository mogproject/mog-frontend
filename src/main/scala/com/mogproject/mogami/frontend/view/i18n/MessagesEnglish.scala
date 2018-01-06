package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.State
import com.mogproject.mogami.util.Implicits._

/**
  * English message definitions
  */
case object MessagesEnglish extends Messages {

  override val FLIP: String = "Flip"

  override val COMMENT: String = "Comment"
  override val COMMENT_CLEAR: String = "Clear"
  override val COMMENT_CLEARED: String = "Cleared!"
  override val COMMENT_CLEAR_TOOLTIP: String = "Clear this comment"
  override val COMMENT_UPDATE: String = "Update"
  override val COMMENT_UPDATED: String = "Updated!"
  override val COMMENT_UPDATE_TOOLTIP: String = "Update this comment"

  override val MENU: String = "Menu"
  override val EDIT: String = "Edit"

  override val SHARE: String = "Share"
  override val COPY: String = "Copy"
  override val RECORD_URL: String = "Record URL"
  override val SNAPSHOT_URL: String = "Snapshot URL"
  override val SHORTEN_URL: String = "Shorten URL"
  override val SHORTEN_URL_TOOLTIP: String = "Create a short URL by Google URL Shortener"
  override val SHORTEN_URL_CREATING: String = "creating"
  override val SNAPSHOT_IMAGE: String = "Snapshot Image"
  override val IMAGE_SIZE: String = "Image Size"
  override val SMALL: String = "Small"
  override val MEDIUM: String = "Medium"
  override val LARGE: String = "Large"
  override val VIEW: String = "View"
  override val SNAPSHOT_SFEN_STRING: String = "Snapshot SFEN String"
  override val NOTES_VIEW: String = "Notes View"
  override val COPY_SUCCESS: String = "Copied!"
  override val COPY_FAILURE: String = "Failed!"

  override val WARNING: String = "Warning"
  override val SHARE_WARNING: String = "Comments will not be shared due to the URL length limit."

  override val MANAGE: String = "Manage"
  override val LOAD_FROM_FILE: String = "Load from File"
  override val LOAD_FROM_TEXT: String = "Load from Text"
  override val SAVE_TO_FILE_CLIPBOARD: String = "Save to File / Clipboard"
  override val BROWSE: String = "Browse"
  override val LOADING: String = "Loading"

  override val ACTION: String = "Action"
  override val RESIGN: String = "Resign"

  override val ANALYZE: String = "Analyze"
  override val ANALYZE_FOR_CHECKMATE: String = "Analyze for Checkmate"
  override val ANALYZE_CHECKMATE_TOOLTIP: String = "Analyze this position for checkmate"

  override val ADD_CHECKMATE_MOVES: String ="Add Moves to Game"
  override val ADD_CHECKMATE_MOVES_TOOLTIP: String ="Add this solution to the current game record"

  override val CHECKMATE_MOVES_ADDED: String = "Moves are added."
  override val TIMEOUT: String = "Timeout"
  override val SEC: String = "sec"
  override val ANALYZING: String = "Analyzing"
  override val CHECKMATE_ANALYZE_TIMEOUT: String = "This position is too difficult to solve."
  override val NO_CHECKMATES: String = "No checkmates."
  override val CHECKMATE_FOUND: String = "Found a checkmate"
  override val BRANCH: String = "Branch"

  override val COUNT_POINT: String = "Count"
  override val COUNT_POINT_LABEL: String = "Count Points"
  override val COUNT_POINT_TOOLTIP: String = "Count points for this position"

  override def COUNT_POINT_RESULT(point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int): String = {
    val plural = (1 < numPiecesInPromotionZone).fold("s", "")
    Seq(
      s"Points: ${point}",
      "In the promotion zone: " + isKingInPromotionZone.fold("King + ", "") + s"${numPiecesInPromotionZone} piece${plural}"
    ).mkString("\n")
  }

  override val RESET: String = "Reset"
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

  override val SETTINGS: String = "Settings"
  override val BOARD_SIZE: String = "Board Size"
  override val LAYOUT: String = "Layout"
  override val PIECE_GRAPHIC: String = "Piece Graphic"
  override val DOUBLE_BOARD_MODE: String = "Double Board Mode"
  override val VISUAL_EFFECTS: String = "Visual Effects"
  override val SOUND_EFFECTS: String = "Sound Effects"
  override val MESSAGE_LANG: String = "Messages"
  override val RECORD_LANG: String = "Record"
  override val SETTINGS_INFO: String = "These settings will be saved for your browser."

  override val HELP: String = "Help"
  override val ABOUT_THIS_SITE: String = "About This Site"


  override val MOVES: String = "Moves"
  override val TRUNK: String = "Trunk"
}
