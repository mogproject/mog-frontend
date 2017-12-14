package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.frontend.model.Mode
import com.mogproject.mogami.frontend.model.board.BoardModel
import com.mogproject.mogami.frontend.model.board.cursor.{CursorEvent, MouseMoveEvent}
import com.mogproject.mogami.frontend.view.board.{BoardCursor, BoxCursor, HandCursor, PlayerCursor}

/**
  *
  */
case class BoardCursorEventAction(cursorEvent: CursorEvent) extends BoardAction {
  override def execute(model: BoardModel): Option[BoardModel] = Some(model.copy(cursorEvent = Some(cursorEvent.validate(isValid(model.mode)))))

  private[this] def isValid(mode: Mode): Boolean = {
    cursorEvent match {
      case MouseMoveEvent(c) =>
        c match {
          case Some(PlayerCursor(_)) => mode.playerSelectable
          case Some(BoxCursor(_)) => mode.boxAvailable
          case _ => mode.boardCursorAvailable
        }
      case _ => false
    }
  }

}
