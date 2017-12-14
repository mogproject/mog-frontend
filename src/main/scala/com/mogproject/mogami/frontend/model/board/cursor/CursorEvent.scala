package com.mogproject.mogami.frontend.model.board.cursor

import com.mogproject.mogami.frontend.view.board.Cursor

sealed trait CursorEvent {
  def validate(checked: Boolean): CursorEvent = this
}

case class MouseMoveEvent(cursor: Option[Cursor]) extends CursorEvent {
  override def validate(checked: Boolean): CursorEvent = if (checked) this else copy(cursor = None)
}

case class MouseDownEvent(cursor: Option[Cursor]) extends CursorEvent {
  override def validate(checked: Boolean): CursorEvent = if (checked) this else copy(cursor = None)
}

case object MouseHoldEvent extends CursorEvent {
}

case class MouseUpEvent(cursor: Option[Cursor]) extends CursorEvent {
  override def validate(checked: Boolean): CursorEvent = if (checked) this else copy(cursor = None)
}
