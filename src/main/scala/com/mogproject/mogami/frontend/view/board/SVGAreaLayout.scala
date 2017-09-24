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
  private[this] val handPieceWidth = 168
  private[this] val handPieceHeight = 184
  private[this] val handInterval = 12
  private[this] val topMargin = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(0, topMargin + handPieceHeight), boardPieceWidth, boardPieceHeight)

  override val hand: SVGHandLayout = SVGHandLayout(
    Coord(boardMargin, topMargin),
    Coord(boardMargin + 9 * boardPieceWidth - 7 * (handPieceWidth + handInterval), board.offset.y + board.VIEW_BOX_HEIGHT),
    handPieceWidth, handPieceHeight, handInterval, 1, 7
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH, board.VIEW_BOX_HEIGHT + (handPieceHeight + topMargin) * 2)
}

case object CompactLayout extends SVGAreaLayout {
  override val board: SVGBoardLayout = ??? //SVGBoardLayout(Coord(200, 0))

  override val hand: SVGHandLayout = ???

  override def viewBoxBottomRight: Coord = ??? // Coord(2048, 2048)
}

case object WideLayout extends SVGAreaLayout {
  override val board: SVGBoardLayout = ??? //SVGBoardLayout(Coord(400, 0))

  override val hand: SVGHandLayout = ???

  override def viewBoxBottomRight: Coord = ??? // Coord(2048, 2048)
}