package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.{BranchNo, State}
import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.model.board.BoardIndexType
import com.mogproject.mogami.frontend.model.io.RecordFormat
import com.mogproject.mogami.frontend.model.{English, Japanese, PieceFace}
import com.mogproject.mogami.frontend.view.board.SVGAreaLayout
import com.mogproject.mogami.frontend.view.share.ImageSize
import org.scalajs.dom.html.LI

import scalatags.JsDom.{Frag, TypedTag}

/**
  * Message definitions
  */
trait Messages {

  //
  // Nav
  //
  def FLIP: String

  def COMMENT: String

  def COMMENT_CLEAR: String

  def COMMENT_CLEARED: String

  def COMMENT_CLEAR_TOOLTIP: String

  def COMMENT_UPDATE: String

  def COMMENT_UPDATED: String

  def COMMENT_UPDATE_TOOLTIP: String


  def MENU: String

  def PLAYGROUND_LINK_TOOLTIP: String

  //
  // Edit
  //
  def EDIT: String

  def ATTRIBUTES: String

  def SELECT_PIECE_ON_BOARD: String

  def EDIT_HELP: Seq[TypedTag[LI]]

  //
  // Share
  //
  def SHARE: String

  def COPY: String

  def RECORD: String

  def SNAPSHOT: String

  def RECORD_URL: String

  def SNAPSHOT_URL: String

  def SHORTEN_URL: String

  def SHORTEN_URL_TOOLTIP: String

  def SHORTEN_URL_CREATING: String

  def SNAPSHOT_IMAGE: String

  def IMAGE_SIZE: String

  def SMALL: String

  def MEDIUM: String

  def LARGE: String

  def VIEW: String

  def SNAPSHOT_SFEN_STRING: String

  def NOTES_VIEW: String

  def COPY_SUCCESS: String

  def COPY_FAILURE: String

  def WARNING: String

  def SHARE_WARNING: String

  def EMBED_LABEL: String

  def EMBED_BUTTON: String

  def EMBED_CODE: String

  def EMBED_OPTIONS: String

  def EMBED_CONTENT: String

  def EMBED_REFERENCE: String

  def AUTO_DETECT: String

  //
  // Manage
  //
  def MANAGE: String

  def LOAD: String

  def SAVE: String

  def LOAD_FROM_FILE: String

  def LOAD_FROM_TEXT: String

  def LOAD_FROM_TEXT_PLACEHOLDER: String

  def LOAD_FROM_TEXT_TOOLTIP: String

  def TEXT_CLEAR: String

  def TEXT_CLEAR_TOOLTIP: String

  def TEXT_CLEARED: String

  def SAVE_TO_FILE_CLIPBOARD: String

  def SAVE_TO_FILE_TOOLTIP: String

  def FILE_NAME: String

  def BROWSE: String

  def LOADING: String

  def LOADING_TEXT(format: RecordFormat): String

  def FILE_TOO_LARGE(maxFileSizeKB: Int): String

  def ERROR_OPEN_FILE: String

  def ERROR_SELECT_FILE: String

  def ERROR: String

  def UNKNOWN_TYPE: String

  def LOAD_SUCCESS: String

  def LOAD_INFO_MOVES: String

  def LOAD_INFO_BRANCHES: String

  def LOAD_FAILURE: String

  //
  // Branch
  //
  def BRANCH: String

  def BRANCH_NO(branchNo: BranchNo): String = if (branchNo == 0) TRUNK else BRANCH + "#" + branchNo

  def DELETE: String

  def DELETE_BRANCH: String

  def DELETE_BRANCH_TOOLTIP: String

  def NEW_BRANCH_MODE: String

  def NEW_BRANCH_HELP: String

  def NEW_BRANCH_TOOLTIP: String

  def CHANGE_BRANCH: String

  def FORKS: String

  def NO_FORKS: String


  //
  // Action
  //
  def ACTION: String

  def RESIGN: String

  //
  // Analyze
  //
  def ANALYZE: String

  def ANALYZE_FOR_CHECKMATE: String

  def ANALYZE_CHECKMATE_TOOLTIP: String

  def ADD_CHECKMATE_MOVES: String

  def ADD_CHECKMATE_MOVES_TOOLTIP: String

  def CHECKMATE_MOVES_ADDED: String

  def TIMEOUT: String

  def SEC: String

  def ANALYZING: String

  def CHECKMATE_ANALYZE_TIMEOUT: String

  def NO_CHECKMATES: String

  def CHECKMATE_FOUND: String

  def COUNT_POINT: String

  def COUNT_POINT_LABEL: String

  def COUNT_POINT_TOOLTIP: String

  def COUNT_POINT_RESULT(point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int): String

  //
  // Reset
  //
  def RESET: String

  def INITIAL_STATE: Map[State, String]


  //
  // Settings
  //
  def SETTINGS: String

  def BOARD_SIZE: String

  def LAYOUT: String

  def PIECE_GRAPHIC: String

  def BOARD_INDEX_TYPE: String

  def DOUBLE_BOARD_MODE: String

  def VISUAL_EFFECTS: String

  def SOUND_EFFECTS: String

  def MESSAGE_LANG: String

  def RECORD_LANG: String

  def SETTINGS_INFO: String

  def ENGLISH: String

  def JAPANESE: String

  def IMAGE_SIZE_OPTIONS: Map[ImageSize, String]

  def FORMAT: String

  def BOARD_SIZE_OPTIONS: Map[Option[Int], String]

  def LAYOUT_OPTIONS: Map[SVGAreaLayout, String]

  def PIECE_GRAPHIC_OPTIONS: Map[PieceFace, String]

  def BOARD_INDEX_TYPE_OPTIONS: Map[BoardIndexType, String]

  //
  // Help
  //
  def HELP: String

  def HELP_CONTENT: Seq[TypedTag[LI]]

  def ABOUT_THIS_SITE: String

  def ABOUT_CONTENT: Seq[Frag]

  def MOVES: String

  def TRUNK: String

  //
  // Modal
  //
  def CONFIRMATION: String
  def GAME_INFORMATION: String
  def PLAYER_NAMES: String
  def UPDATE: String
  def ASK_PROMOTE: String
  def YES: String
  def NO: String
  def ASK_RESIGN: String
  def IMAGE_DOWNLOAD_FAILURE: String
  def ASK_EDIT: String
  def ASK_DELETE_BRANCH(branchNo: BranchNo): String
  def INVALID_STATE: String
}

object Messages {
  private[this] var currentLanguage: Language = English

  private[this] var messageSet: Messages = MessagesEnglish

  def setLanguage(language: Language): Unit = {
    currentLanguage = language

    messageSet = language match {
      case Japanese => MessagesJapanese
      case _ => MessagesEnglish
    }
  }

  def getLanguage: Language = currentLanguage

  def get: Messages = messageSet
}