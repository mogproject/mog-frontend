package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.State
import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.model.{English, Japanese}

/**
  * Message definitions
  */
trait Messages {
  //
  // Nav
  //
  def FLIP: String

  def COMMENT_CLEAR: String

  def COMMENT_CLEAR_TOOLTIP: String

  def COMMENT_UPDATE: String

  def COMMENT_UPDATE_TOOLTIP: String


  def MENU: String

  def SHARE: String
  def RECORD_URL: String
  def SNAPSHOT_URL: String
  def SHORTEN_URL: String
  def SNAPSHOT_IMAGE: String
  def SNAPSHOT_SFEN_STRING: String
  def NOTES_VIEW: String

  def MANAGE: String
  def LOAD_FROM_FILE: String
  def LOAD_FROM_TEXT: String
  def SAVE_TO_FILE_CLIPBOARD: String

  //
  // Branch
  //
  def BRANCH: String

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

  def COUNT_POINT: String

  def COUNT_POINT_LABEL: String

  def COUNT_POINT_TOOLTIP: String

  //
  // Reset
  //
  def RESET: String
  def INITIAL_STATE: Map[State, String]


  def SETTINGS: String
  def BOARD_SIZE: String
  def LAYOUT: String
  def PIECE_GRAPHIC: String
  def DOUBLE_BOARD_MODE: String
  def VISUAL_EFFECTS: String
  def SOUND_EFFECTS: String
  def MESSAGE_LANG: String
  def RECORD_LANG: String
  def SETTINGS_INFO: String

  def HELP: String
  def ABOUT_THIS_SITE: String


  def MOVES: String
  def TRUNK: String
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