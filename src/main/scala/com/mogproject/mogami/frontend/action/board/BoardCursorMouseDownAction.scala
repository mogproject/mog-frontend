package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.model.board.BoardModel

/**
  *
  */
case class BoardCursorMouseDownAction(square: Square) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = ???
}
