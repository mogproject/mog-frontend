package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.board.board.SVGBoardLayout
import com.mogproject.mogami.frontend.view.board.hand.SVGHandLayout

/**
  *
  */
sealed trait SVGAreaLayout {
  def board: SVGBoardLayout

  def hand: SVGHandLayout

  def viewBoxBottomRight: Coord
}

// fixme: offsets
case object SVGStandardLayout extends SVGAreaLayout {
  private[this] val boardPieceWidth = 210
  private[this] val boardPieceHeight = 230
  private[this] val boardMargin = 79
  private[this] val handPieceWidth = boardPieceWidth * 6 / 7
  private[this] val handPieceHeight = handPieceWidth * boardPieceHeight / boardPieceWidth
  private[this] val topMargin = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(0, topMargin + handPieceHeight), boardPieceWidth, boardPieceHeight)

  override val hand: SVGHandLayout = SVGHandLayout(
    Coord(boardMargin, topMargin),
    Coord(boardMargin + 9 * boardPieceWidth - 7 * handPieceWidth, board.offset.y + board.VIEW_BOX_HEIGHT),
    handPieceWidth, handPieceHeight, 1, 7
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH, board.VIEW_BOX_HEIGHT + (handPieceHeight + topMargin) * 2)
}

case object SVGCompactLayout extends SVGAreaLayout {
  private[this] val boardPieceWidth = 210
  private[this] val boardPieceHeight = 230
  private[this] val boardMargin = 79
  private[this] val handPieceWidth = boardPieceWidth * 6 / 7
  private[this] val handPieceHeight = handPieceWidth * boardPieceHeight / boardPieceWidth
  private[this] val topMargin = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(boardMargin + handPieceWidth + topMargin, 0), boardPieceWidth, boardPieceHeight)

  override val hand: SVGHandLayout = SVGHandLayout(
    Coord(boardMargin, boardMargin),
    board.offset + Coord(board.VIEW_BOX_WIDTH + topMargin, boardMargin + 3 * boardPieceHeight),
    handPieceWidth, handPieceHeight, 7, 1
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH + handPieceWidth + boardMargin + topMargin, board.VIEW_BOX_HEIGHT)
}

case object SVGWideLayout extends SVGAreaLayout {
  private[this] val boardPieceWidth = 210
  private[this] val boardPieceHeight = 230
  private[this] val boardMargin = 79
  private[this] val handPieceWidth = boardPieceWidth
  private[this] val handPieceHeight = boardPieceHeight
  private[this] val topMargin = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(boardMargin + 2 * handPieceWidth + topMargin, 0), boardPieceWidth, boardPieceHeight)

  override val hand: SVGHandLayout = SVGHandLayout(
    Coord(topMargin, boardMargin),
    board.offset + Coord(board.VIEW_BOX_WIDTH + topMargin, boardMargin + 5 * boardPieceHeight),
    handPieceWidth, handPieceHeight, 4, 2
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH + 2 * handPieceWidth + boardMargin + topMargin, board.VIEW_BOX_HEIGHT)
}