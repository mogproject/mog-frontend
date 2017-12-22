package com.mogproject.mogami.frontend.util

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.model.{English, Japanese}

/**
  *
  */
object PlayerUtil {

  def normalizeGameInfo(gameInfo: GameInfo): GameInfo = {
    gameInfo.copy(tags = gameInfo.tags.filter {
      case ('blackName, s) => s.nonEmpty
      case ('whiteName, s) => s.nonEmpty
      case _ => true
    })
  }

  def getDefaultPlayerName(player: Player, messageLang: Language, isHandicapped: Boolean): String = {
    (player, messageLang, isHandicapped) match {
      case (BLACK, Japanese, true) => "下手"
      case (BLACK, Japanese, false) => "先手"
      case (WHITE, Japanese, true) => "上手"
      case (WHITE, Japanese, false) => "後手"
      case (BLACK, English, _) => "Black"
      case (WHITE, English, _) => "White"
    }
  }

}
