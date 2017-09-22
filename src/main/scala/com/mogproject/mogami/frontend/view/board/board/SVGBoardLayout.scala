package com.mogproject.mogami.frontend.view.board.board


import com.mogproject.mogami.Square
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.{Coord, Rect}

/**
  *
  */
case class SVGBoardLayout(offset: Coord) {

  def getCoord(fileIndex: Int, rankIndex: Int): Coord = offset + Coord(MARGIN_SIZE + fileIndex * PIECE_WIDTH, MARGIN_SIZE + rankIndex * PIECE_HEIGHT)

  def getRect(fileIndex: Int, rankIndex: Int): Rect = Rect(getCoord(fileIndex, rankIndex), PIECE_WIDTH, PIECE_HEIGHT)

  def getRect(square: Square, isFlipped: Boolean): Rect = {
    val s = isFlipped.when[Square](!_)(square)
    getRect(9 - s.file, s.rank - 1)
  }

  final val VIEW_BOX_WIDTH: Int = 2048
  final val PIECE_WIDTH: Int = 210
  final val PIECE_HEIGHT: Int = 230
  final val PIECE_FACE_SIZE: Int = 200
  final val BOARD_WIDTH: Int = PIECE_WIDTH * 9
  final val BOARD_HEIGHT: Int = PIECE_HEIGHT * 9
  final val MARGIN_SIZE: Int = (VIEW_BOX_WIDTH - BOARD_WIDTH) / 2
  final val CIRCLE_SIZE: Int = 14

  /** @note Corresponds to CSS board-index-text:font-size */
  final val INDEX_SIZE: Int = 60

  final val VIEW_BOX_HEIGHT: Int = BOARD_HEIGHT + MARGIN_SIZE * 2
}
