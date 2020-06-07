package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.action.board.BoardCursorEventAction
import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.frontend.view.event.{PointerHoldSensor, PointerMoveSensor}

/**
  * Mouse/mobile event handler
  */
trait SVGAreaEventHandler extends PointerMoveSensor with PointerHoldSensor {
  self: SVGArea =>

  /**
    * Convert Mouse position to Cursor
    *
    * @param clientX x-coordinate
    * @param clientY y-coordinate
    * @return None if the position is out of interests
    */
  private[this] def getCursor(clientX: Double, clientY: Double): Option[Cursor] = {
    Seq(board, hand, player, box).to(LazyList).flatMap(t => t.clientPos2Cursor(clientX, clientY)).headOption
  }

  override protected def pointerMoveAction(clientX: Double, clientY: Double): Unit = doAction(BoardCursorEventAction(MouseMoveEvent(areaId, getCursor(clientX, clientY))))

  override protected def pointerDownAction(clientX: Double, clientY: Double): Unit = doAction(BoardCursorEventAction(MouseDownEvent(areaId, getCursor(clientX, clientY))))

  override protected def pointerUpAction(clientX: Double, clientY: Double): Unit = doAction(BoardCursorEventAction(MouseUpEvent(getCursor(clientX, clientY))))

  override protected def pointerHoldAction(): Boolean = {
    doAction(BoardCursorEventAction(MouseHoldEvent))
    true
  }
}
