package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.GameStatus
import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.model.{English, Japanese}


/**
  * Multi-lingual messages for record
  */
trait RecordMessages {
  def INITIAL_STATE: String

  def SPECIAL_MOVES: Map[GameStatus.GameStatus, String]
}

object RecordMessages {
  def get(recordLang: Language): RecordMessages = recordLang match {
    case Japanese => RecordMessagesJapanese
    case English => RecordMessagesEnglish
  }
}