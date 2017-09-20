package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.action.board.BoardCursorMoveAction
import com.mogproject.mogami.frontend.sam.SAM
import org.scalajs.dom.MouseEvent

/**
  * Mouse/mobile event handler
  */
trait SVGBoardEventHandler {
  self: SVGArea =>

  //
  // Utility
  //

  /**
    * Convert MouseEvent to Square
    *
    * @return Square if the mouse position is inside the board
    */
  private[this] def getSquare(clientX: Double, clientY: Double): Option[Square] = {
    val r = svgBoard.getBorderClientRect
    val (x, y) = (clientX - r.left, clientY - r.top)
    val xi = math.floor(x / (r.width / 9)).toInt
    val yi = math.floor(y / (r.height / 9)).toInt

    if (0 <= xi && xi < 9 && 0 <= yi && yi < 9) {
      Some(boardFlipped.when[Square](!_)(Square(9 - xi, 1 + yi)))
    } else {
      None
    }
  }

  protected def mouseMove(evt: MouseEvent): Unit = SAM.doAction(BoardCursorMoveAction(getSquare(evt.clientX, evt.clientY)))

}
