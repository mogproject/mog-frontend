package com.mogproject.mogami.frontend.model.board.cursor

import com.mogproject.mogami.frontend.view.board.Cursor

sealed trait CursorEvent

case class MouseMoveEvent(cursor: Option[Cursor]) extends CursorEvent

case class MouseDownEvent(cursor: Option[Cursor]) extends CursorEvent

case object MouseHoldEvent extends CursorEvent

case class MouseUpEvent(cursor: Option[Cursor]) extends CursorEvent
