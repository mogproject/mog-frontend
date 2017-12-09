package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.model.board.{BoardIndicator, BoardModel}

/**
  *
  */
case class BoardSetIndicatorAction(turn: Player, indicator: Option[BoardIndicator]) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = {
    Some(model.copy(indicators = model.indicators + (turn -> indicator)))
  }
}
