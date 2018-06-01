package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.GameStatus
import com.mogproject.mogami.core.move.DeclareWin

/**
  * Record messages in English
  */
case object RecordMessagesEnglish extends RecordMessages {
  override def INITIAL_STATE: String = "Start"

  override def SPECIAL_MOVES: Map[GameStatus.GameStatus, String] = Map(
    GameStatus.Mated -> "Mated",
    GameStatus.Drawn -> "Drawn",
    GameStatus.PerpetualCheck -> "Perpetual Check",
    GameStatus.Uchifuzume -> "Uchifuzume",
    GameStatus.Jishogi -> DeclareWin().toWesternNotationString,
    GameStatus.IllegallyMoved -> "Illegal Move"
  )
}
