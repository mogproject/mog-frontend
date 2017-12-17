package com.mogproject.mogami.frontend.model.board.cursor

sealed trait CursorEvent {
}

case class MouseMoveEvent(areaId: Int, cursor: Option[Cursor]) extends CursorEvent {
}

case class MouseDownEvent(areaId: Int, cursor: Option[Cursor]) extends CursorEvent {
}

case object MouseHoldEvent extends CursorEvent {
}

case class MouseUpEvent(cursor: Option[Cursor]) extends CursorEvent {
}
