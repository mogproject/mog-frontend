package com.mogproject.mogami.frontend.model.board

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