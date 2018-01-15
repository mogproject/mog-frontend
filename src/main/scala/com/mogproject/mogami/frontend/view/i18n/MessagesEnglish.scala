package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.frontend.model.io.RecordFormat
import com.mogproject.mogami.{BranchNo, State}
import com.mogproject.mogami.frontend.{FrontendSettings, PieceFace}
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import com.mogproject.mogami.frontend.view.share.ImageSize
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.LI

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

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
  override val PLAYGROUND_LINK_TOOLTIP: String = "Open Shogi Playground"

  override val EDIT: String = "Edit"
  override val ATTRIBUTES: String = "Attributes"
  override val SELECT_PIECE_ON_BOARD: String = "Select a piece on board."
  override val EDIT_HELP: Seq[TypedTag[LI]] = Seq(
    li("Click on a player name to set the turn to move."),
    li("Double-click on a piece on board to change its attributes:",
      ul(
        li("Black Unpromoted ->"),
        li("Black Promoted ->"),
        li("White Unpromoted ->"),
        li("White Promoted ->"),
        li("Black Unpromoted")
      )
    )
  )

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
  override val LOAD: String = "Load"
  override val SAVE: String = "Save"
  override val LOAD_FROM_FILE: String = "Load from File"
  override val LOAD_FROM_TEXT: String = "Load from Text"
  override val LOAD_FROM_TEXT_PLACEHOLDER: String = "Paste your record here."
  override val LOAD_FROM_TEXT_TOOLTIP: String = "Load record from the text area"
  override val TEXT_CLEAR: String = "Clear"
  override val TEXT_CLEAR_TOOLTIP: String = "Clear the text area"
  override val TEXT_CLEARED: String = "Cleared!"
  override val SAVE_TO_FILE_CLIPBOARD: String = "Save to File / Clipboard"
  override val SAVE_TO_FILE_TOOLTIP: String = "Save record as a file"
  override val FILE_NAME: String = "File Name"
  override val BROWSE: String = "Browse"
  override val LOADING: String = "Loading"

  override def LOADING_TEXT(format: RecordFormat): String = s"Loading as ${format} Format..."

  override def FILE_TOO_LARGE(maxFileSizeKB: Int): String = s"File too large. (must be <= ${maxFileSizeKB}KB)"

  override val ERROR_OPEN_FILE: String = "Failed to open the file."
  override val ERROR_SELECT_FILE: String = "Failed to select the file."
  override val ERROR: String = "Error"

  override val UNKNOWN_TYPE: String = "Unknown file type"
  override val LOAD_SUCCESS: String = "Loaded!"
  override val LOAD_INFO_MOVES: String = "moves"
  override val LOAD_INFO_BRANCHES: String = "branch(es)"
  override val LOAD_FAILURE: String = "Failed!"

  override val ACTION: String = "Action"
  override val RESIGN: String = "Resign"

  override val ANALYZE: String = "Analyze"
  override val ANALYZE_FOR_CHECKMATE: String = "Analyze for Checkmate"
  override val ANALYZE_CHECKMATE_TOOLTIP: String = "Analyze this position for checkmate"

  override val ADD_CHECKMATE_MOVES: String = "Add Moves to Game"
  override val ADD_CHECKMATE_MOVES_TOOLTIP: String = "Add this solution to the current game record"

  override val CHECKMATE_MOVES_ADDED: String = "Moves are added."
  override val TIMEOUT: String = "Timeout"
  override val SEC: String = "sec"
  override val ANALYZING: String = "Analyzing"
  override val CHECKMATE_ANALYZE_TIMEOUT: String = "This position is too difficult to solve."
  override val NO_CHECKMATES: String = "No checkmates."
  override val CHECKMATE_FOUND: String = "Found a checkmate"
  override val BRANCH: String = "Branch"
  override val DELETE: String = "Delete"
  override val DELETE_BRANCH: String = "Delete This Branch"
  override val DELETE_BRANCH_TOOLTIP: String = "Delete this branch"
  override val NEW_BRANCH_MODE: String = "New Branch Mode"
  override val NEW_BRANCH_HELP: String = "Creates a new branch whenever you make a different move."
  override val NEW_BRANCH_TOOLTIP: String = "Creates a new branch"
  override val CHANGE_BRANCH: String = "Change Branch"
  override val FORKS: String = "Forks"
  override val NO_FORKS: String = "No forks."

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
    State.HANDICAP_NAKED_KING -> "Naked King",
    State.empty -> "Empty"
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

  override val ENGLISH: String = "English"
  override val JAPANESE: String = "Japanese"

  override val IMAGE_SIZE_OPTIONS: Map[ImageSize, String] = ImageSize.all.map { s => s -> s.toString }.toMap

  override val FORMAT: String = "Format"

  override val BOARD_SIZE_OPTIONS: Map[Option[Int], String] = Map(
    None -> "Automatic",
    Some(15) -> "15 - Extra Small",
    Some(20) -> "20",
    Some(25) -> "25",
    Some(30) -> "30 - Small",
    Some(40) -> "40 - Medium",
    Some(50) -> "50 - Large",
    Some(60) -> "60 - Extra Large"
  )

  override val LAYOUT_OPTIONS: Map[SVGAreaLayout, String] = Map(
    SVGStandardLayout -> "Standard",
    SVGCompactLayout -> "Compact",
    SVGWideLayout -> "Wide"
  )

  override val PIECE_GRAPHIC_OPTIONS: Map[PieceFace, String] = PieceFace.all.map(p => p -> p.displayName).toMap

  override val HELP: String = "Help"

  override val HELP_CONTENT: Seq[TypedTag[LI]] = Seq(
    li("Click on a player name to set game information."),
    li("In Play Mode, you can move pieces by a flick."),
    li("In View Mode, click (or hold) on any squares on the right-hand side of the board to move to the next position, and click (or hold) the left-hand side to the previous position."),
    li("If you click and hold ", WebComponent.glyph("backward"), " or ", WebComponent.glyph("forward"), " button, the position changes continuously.")
  )

  override val ABOUT_THIS_SITE: String = "About This Site"

  override val ABOUT_CONTENT: Seq[Frag] = Seq(
    p(i(""""Run anywhere. Needs NO installation."""")),
    p("Shogi Playground is a platform for all shogi --Japanese chess-- fans in the world." +
      " This mobile-friendly website enables you to manage, analyze, and share shogi games as well as mate problems."),
    p("If you have any questions, trouble, or suggestion, please tell the ",
      a(href := FrontendSettings.url.authorContactUrl, target := "_blank", "author"),
      ". Your voice matters."),
    br(),
    label("Special Thanks"),
    ul(
      li(
        "Piece Graphics - ",
        a(href := FrontendSettings.url.credit.shineleckomaUrl, target := "_blank", "shineleckoma")
      ),
      li(
        "Piece Fonts - ",
        a(href := FrontendSettings.url.credit.loraFontsUrl, target := "_blank", "Lora Fonts")
      )
    ),
    br(),
    label("Supported Browsers"),
    ul(
      li("Firefox"),
      li("Chrome"),
      li("Safari")
    )
  )

  override val MOVES: String = "Moves"
  override val TRUNK: String = "Trunk"

  override val CONFIRMATION: String = "Confirmation"
  override val GAME_INFORMATION: String = "Game Information"
  override val PLAYER_NAMES: String = "Player Names"
  override val UPDATE: String = "Update"
  override val ASK_PROMOTE: String = "Do you want to promote?"
  override val YES: String = "Yes"
  override val NO: String = "No"
  override val ASK_RESIGN: String = "Do you really want to resign?"
  override val IMAGE_DOWNLOAD_FAILURE: String = "Failed to download image(s):"
  override val ASK_EDIT: String = "The record and comments will be discarded. Are you sure?"

  override def ASK_DELETE_BRANCH(branchNo: BranchNo): String = s"Branch#${branchNo} will be deleted. Comments on this branch will also be removed. Are you sure?"

  override val INVALID_STATE: String = "Invalid state."
}
