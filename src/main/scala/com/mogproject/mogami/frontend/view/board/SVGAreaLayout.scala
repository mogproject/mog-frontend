package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.board.board.SVGBoardLayout
import com.mogproject.mogami.frontend.view.board.hand.SVGHandLayout
import com.mogproject.mogami.frontend.view.board.player.SVGPlayerLayout
import com.mogproject.mogami.frontend.view.coordinate.Rect

/**
  *
  */
sealed trait SVGAreaLayout {
  def board: SVGBoardLayout

  def hand: SVGHandLayout

  def player: SVGPlayerLayout

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
  private[this] val boardWidth = 9 * boardPieceWidth
  private[this] val handWidth = 7 * handPieceWidth
  private[this] val playerWidth = boardWidth - handWidth
  private[this] val symbolSize = 150
  private[this] val playerNameHeight = 120

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(0, topMargin + handPieceHeight), boardPieceWidth, boardPieceHeight)

  private[this] val whiteHandTopLeft = Coord(boardMargin, topMargin)
  private[this] val blackHandTopLeft = Coord(boardMargin + playerWidth, board.offset.y + board.VIEW_BOX_HEIGHT)

  override val hand: SVGHandLayout = SVGHandLayout(whiteHandTopLeft, blackHandTopLeft, handPieceWidth, handPieceHeight, 1, 7)

  // todo: refactor
  override def player: SVGPlayerLayout = SVGPlayerLayout(
    Rect(Coord(boardMargin + handWidth, whiteHandTopLeft.y), playerWidth, handPieceHeight),
    Rect(Coord(boardMargin + boardWidth - symbolSize, whiteHandTopLeft.y + handPieceHeight - playerNameHeight), symbolSize, symbolSize),
    Rect(Coord(boardMargin + handWidth, whiteHandTopLeft.y + handPieceHeight - playerNameHeight + 40), playerWidth - symbolSize, playerNameHeight - 36),
    Rect(Coord(boardMargin + handWidth, whiteHandTopLeft.y), playerWidth, handPieceHeight - playerNameHeight),
    Seq(
      Rect(Coord(boardMargin - 35, whiteHandTopLeft.y + handPieceHeight + 5), boardWidth + 40, 30),
      Rect(Coord(boardMargin - 35, whiteHandTopLeft.y - 5), 30, handPieceHeight + 11)
    ),
    Rect(Coord(boardMargin, blackHandTopLeft.y), playerWidth, handPieceHeight),
    Rect(Coord(boardMargin, blackHandTopLeft.y + playerNameHeight - symbolSize), symbolSize, symbolSize),
    Rect(Coord(boardMargin + symbolSize, blackHandTopLeft.y), playerWidth - symbolSize, playerNameHeight - 36),
    Rect(Coord(boardMargin, blackHandTopLeft.y + playerNameHeight), playerWidth, handPieceHeight - playerNameHeight),
    Seq(
      Rect(Coord(boardMargin - 5, blackHandTopLeft.y - 35), boardWidth + 40, 30),
      Rect(Coord(boardMargin + boardWidth + 5, blackHandTopLeft.y - 5), 30, handPieceHeight + 11)
    )
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

  override def player: SVGPlayerLayout = ???

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

  override def player: SVGPlayerLayout = ???

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH + 2 * handPieceWidth + boardMargin + topMargin, board.VIEW_BOX_HEIGHT)
}