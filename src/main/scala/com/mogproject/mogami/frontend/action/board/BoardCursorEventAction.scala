package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.model.board.cursor.CursorEvent

/**
  *
  */
case class BoardCursorEventAction[Model <: BasePlaygroundModel](cursorEvent: CursorEvent) extends PlaygroundAction[Model] {
  override def execute(model: Model): Option[Model] = {
    ??? //Some(model.copy(cursorEvent = Some(cursorEvent)))
  }
}
