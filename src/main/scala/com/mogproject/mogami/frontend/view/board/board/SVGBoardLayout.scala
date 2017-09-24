package com.mogproject.mogami.frontend.view.board.board


import com.mogproject.mogami.Square
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.{Coord, Rect}

/**
  *
  */
case class SVGBoardLayout(offset: Coord, pieceWidth: Int, pieceHeight: Int) {

  // todo: consistency refactoring
  def getCoord(fileIndex: Int, rankIndex: Int): Coord = offset + Coord(MARGIN_SIZE + fileIndex * pieceWidth, MARGIN_SIZE + rankIndex * pieceHeight)

  def getRect(fileIndex: Int, rankIndex: Int): Rect = Rect(getCoord(fileIndex, rankIndex), pieceWidth, pieceHeight)

  def getRect(square: Square, isFlipped: Boolean): Rect = {
    val s = isFlipped.when[Square](!_)(square)
    getRect(9 - s.file, s.rank - 1)
  }

  final val VIEW_BOX_WIDTH: Int = 2048
  final val PIECE_FACE_SIZE: Int = pieceWidth * 20 / 21
  final val BOARD_WIDTH: Int = pieceWidth * 9
  final val BOARD_HEIGHT: Int = pieceHeight * 9
  final val MARGIN_SIZE: Int = (VIEW_BOX_WIDTH - BOARD_WIDTH) / 2
  final val CIRCLE_SIZE: Int = 14

  /** @note Corresponds to CSS board-index-text:font-size */
  final val INDEX_SIZE: Int = 60

  final val VIEW_BOX_HEIGHT: Int = BOARD_HEIGHT + MARGIN_SIZE * 2
}
