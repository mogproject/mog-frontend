package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.model.board.BoardModel

/**
  *
  */
case class BoardCursorMoveAction(square: Option[Square]) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = {
    model match {
      case m: BoardModel => Some(m.copy(activeCursor = square))
      case _ => None
    }
  }
}
