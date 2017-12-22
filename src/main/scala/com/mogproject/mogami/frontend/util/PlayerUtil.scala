package com.mogproject.mogami.frontend.util

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.model.{English, Japanese}

/**
  *
  */
object PlayerUtil {

  val blackTag = 'blackName
  val whiteTag = 'whiteName
  val tagNames: Map[Player, Symbol] = Map(BLACK -> blackTag, WHITE -> whiteTag)

  def normalizeGameInfo(gameInfo: GameInfo): GameInfo = {
    gameInfo.copy(tags = gameInfo.tags.filter {
      case (t, s) if t == blackTag || t == whiteTag => s.nonEmpty
      case _ => true
    })
  }

  def getPlayerNames(gameInfo: GameInfo): Map[Player, String] = {
    (gameInfo.tags.get(blackTag).map(BLACK -> _) ++ gameInfo.tags.get(whiteTag).map(WHITE -> _)).toMap
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

  def getPlayerName(gameInfo: GameInfo, player: Player, messageLang: Language, isHandicapped: Boolean): String = {
    gameInfo.tags.get(tagNames(player)).filter(_.nonEmpty).getOrElse(PlayerUtil.getDefaultPlayerName(player, messageLang, isHandicapped))
  }

}
