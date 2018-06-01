package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.{GameStatus, IllegalMove}
import com.mogproject.mogami.core.move.DeclareWin

/**
  * Record messages in Japanese
  */
case object RecordMessagesJapanese extends RecordMessages {
  override def INITIAL_STATE: String = "初期局面"

  override def SPECIAL_MOVES: Map[GameStatus.GameStatus, String] = Map(
    GameStatus.Mated -> "詰み",
    GameStatus.Drawn -> "千日手",
    GameStatus.PerpetualCheck -> "連続王手の千日手",
    GameStatus.Uchifuzume -> "打ち歩詰め",
    GameStatus.Jishogi -> DeclareWin().toJapaneseNotationString,
    GameStatus.IllegallyMoved -> IllegalMove.kifKeyword
  )
}
