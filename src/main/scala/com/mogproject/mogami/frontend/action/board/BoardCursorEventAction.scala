package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.model.board.cursor.{CursorEvent, MouseMoveEvent}
import com.mogproject.mogami.frontend.view.board._

/**
  *
  */
case class BoardCursorEventAction(cursorEvent: CursorEvent) extends PlaygroundAction {

  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    cursorEvent match {
      case MouseMoveEvent(areaId, c) =>
        c.map(areaId -> _) match {
          case ac if ac == model.activeCursor => None
          case ac@Some((_, cc)) if cursorActivatable(model, cc) => Some(model.copy(newActiveCursor = ac))
          case _ => Some(model.copy(newActiveCursor = None))
        }
      case _ =>
        None
    }
  }

  private[this] def cursorSelectable(model: BasePlaygroundModel, cursor: Cursor): Boolean = {
    cursor match {
      case BoardCursor(sq) => model.mode.getBoardPieces.get(sq).exists(p => model.mode.playable(p.owner))
      case HandCursor(h) => model.mode.playable(h.owner) && model.mode.getHandPieces.get(h).exists(_ > 0)
      case BoxCursor(pt) => model.mode.boxAvailable && model.mode.getBoxPieces.get(pt).exists(_ > 0)
      case _ => false
    }
  }

  private[this] def cursorActivatable(model: BasePlaygroundModel, cursor: Cursor): Boolean = {
    cursor match {
      case BoardCursor(_) => model.mode.boardCursorAvailable
      case HandCursor(_) => model.mode.boardCursorAvailable
      case PlayerCursor(_) => model.mode.playerSelectable
      case BoxCursor(_) => model.mode.boxAvailable
      case _ => false
    }
  }
}
