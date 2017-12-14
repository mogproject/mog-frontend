package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.model.Mode
import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.model.board.cursor.{CursorEvent, MouseMoveEvent}
import com.mogproject.mogami.frontend.view.board.{BoardCursor, HandCursor, PlayerCursor}

/**
  *
  */
case class BoardCursorEventAction(cursorEvent: CursorEvent) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(cursorEvent = Some(validate(model.mode))))

  private[this] def validate(mode: Mode): CursorEvent = {
    val check = cursorEvent match {
      case MouseMoveEvent(c) =>
        c match {
          case Some(PlayerCursor(_)) => mode.playerSelectable
          case _ => mode.boardCursorAvailable
        }
      case _ => false
    }
    cursorEvent.validate(check)
  }

}
