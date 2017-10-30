package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.model.board.BoardModel

/**
  *
  */
case class BoardSetPlayerNameAction(playerNames: Map[Player, Option[String]]) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(playerNames = model.playerNames ++ playerNames))
}
