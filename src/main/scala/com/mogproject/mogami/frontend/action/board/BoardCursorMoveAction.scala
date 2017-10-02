package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.view.board.Cursor

/**
  *
  */
case class BoardCursorMoveAction(cursor: Option[Cursor]) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = {
    Some(model.copy(activeCursor = cursor))
  }
}
