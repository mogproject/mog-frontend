package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.model.board.cursor.CursorEvent

/**
  *
  */
case class BoardCursorEventAction(cursorEvent: CursorEvent) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(cursorEvent = Some(cursorEvent)))
}
