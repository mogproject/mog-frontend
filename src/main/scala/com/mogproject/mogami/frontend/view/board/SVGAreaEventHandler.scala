package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.action.board.BoardCursorMoveAction
import com.mogproject.mogami.frontend.sam.SAM
import org.scalajs.dom.MouseEvent

/**
  * Mouse/mobile event handler
  */
trait SVGAreaEventHandler {
  self: SVGArea =>

  //
  // Utility
  //

  /**
    * Convert Mouse position to Cursor
    *
    * @param clientX x-coordinate
    * @param clientY y-coordinate
    * @return None if the position is out of interests
    */
  private[this] def getCursor(clientX: Double, clientY: Double): Option[Cursor] = {
    Seq(board, hand).toStream.flatMap(t => t.clientPos2Cursor(clientX, clientY)).headOption
  }

  protected def mouseMove(evt: MouseEvent): Unit = {
    SAM.doAction(BoardCursorMoveAction(getCursor(evt.clientX, evt.clientY)))
  }

}
