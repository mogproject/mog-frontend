package com.mogproject.mogami.frontend.model.board

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.core.game.GameStatus
import com.mogproject.mogami.core.game.GameStatus.GameStatus

/**
  *
  */
sealed abstract class BoardIndicator(val text: String, classSuffix: String) {
  def className: String = s"indicator-${classSuffix}"
}


case object IndicatorTurn extends BoardIndicator("TURN", "turn")

case object IndicatorWin extends BoardIndicator("WIN", "win")

case object IndicatorLose extends BoardIndicator("LOSE", "lose")

case object IndicatorDraw extends BoardIndicator("DRAW", "draw")

object BoardIndicator {
  def fromGameStatus(turn: Player, gameStatus: GameStatus): Map[Player, BoardIndicator] = {
    gameStatus match {
      case GameStatus.Playing => Map(turn -> IndicatorTurn)
      case GameStatus.Mated | GameStatus.Resigned | GameStatus.TimedUp => Map(turn -> IndicatorLose, !turn -> IndicatorWin)
      case GameStatus.PerpetualCheck | GameStatus.Uchifuzume | GameStatus.Jishogi | GameStatus.IllegallyMoved => Map(turn -> IndicatorWin, !turn -> IndicatorLose)
      case GameStatus.Drawn => Map(BLACK -> IndicatorDraw, WHITE -> IndicatorDraw)
      case _ => Map.empty
    }
  }
}