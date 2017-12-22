package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.board.board.SVGBoardLayout
import com.mogproject.mogami.frontend.view.board.box.SVGBoxLayout
import com.mogproject.mogami.frontend.view.board.hand.SVGHandLayout
import com.mogproject.mogami.frontend.view.board.player.SVGPlayerLayout
import com.mogproject.mogami.frontend.view.coordinate.Rect

/**
  *
  */
sealed trait SVGAreaLayout {

  val mediumPiece: Coord = Coord(210, 230)
  val largePiece: Coord = mediumPiece.map(_ * 3 / 2)
  val smallPiece: Coord = mediumPiece.map(_ * 6 / 7)

  def board: SVGBoardLayout

  def hand: SVGHandLayout

  def player: SVGPlayerLayout

  def box: SVGBoxLayout = SVGBoxLayout(board.center.x, smallPiece)

  def viewBoxBottomRight: Coord

  def areaWidth(pieceWidth: Int): Int = pieceWidth * viewBoxBottomRight.x / mediumPiece.x
}

// fixme: offsets
case object SVGStandardLayout extends SVGAreaLayout {
  private[this] val boardMargin = 79
  private[this] val topMargin = 30
  private[this] val boardWidth = 9 * mediumPiece.x
  private[this] val handWidth = 7 * smallPiece.x
  private[this] val playerWidth = boardWidth - handWidth
  private[this] val symbolOffset = 30
  private[this] val symbolSize = 150
  private[this] val indicatorHeight = 87
  private[this] val playerBorderStroke = 5
  private[this] val playerNameHeight = smallPiece.y - indicatorHeight - playerBorderStroke
  private[this] val indicatorBackgroundStroke = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(0, topMargin + smallPiece.y), mediumPiece.x, mediumPiece.y)

  private[this] val blackHandTopLeft = Coord(boardMargin + playerWidth, board.offset.y + board.VIEW_BOX_HEIGHT)

  override val hand: SVGHandLayout = SVGHandLayout(board.center, blackHandTopLeft, smallPiece.x, smallPiece.y, 1, 7)

  private[this] val blackPlayerArea = Rect(Coord(boardMargin, blackHandTopLeft.y), playerWidth, smallPiece.y)
  private[this] val blackSymbolArea = Rect(blackPlayerArea.leftTop - Coord(0, symbolOffset), symbolSize, symbolSize)
  private[this] val blackPlayerNameArea = Rect(blackPlayerArea.leftTop + Coord(symbolSize, playerBorderStroke), playerWidth - symbolSize, playerNameHeight)
  private[this] val blackIndicatorArea = Rect(
    blackPlayerArea.leftBottom + Coord(playerBorderStroke, -indicatorHeight - playerBorderStroke),
    playerWidth - 2 * playerBorderStroke, indicatorHeight
  )
  private[this] val blackIndicatorBackground = Seq(
    Rect(
      Coord(boardMargin - playerBorderStroke, blackHandTopLeft.y - playerBorderStroke - indicatorBackgroundStroke),
      boardWidth + indicatorBackgroundStroke + playerBorderStroke * 2, indicatorBackgroundStroke
    ),
    Rect(
      Coord(boardMargin + boardWidth + playerBorderStroke, blackHandTopLeft.y - playerBorderStroke - 1),
      indicatorBackgroundStroke, smallPiece.y + playerBorderStroke * 2 + 1
    )
  )

  override def player: SVGPlayerLayout = SVGPlayerLayout(
    board.center, blackPlayerArea, blackSymbolArea, blackPlayerNameArea, blackIndicatorArea, blackIndicatorBackground,
    playerNameFontSize = 80,
    indicatorFontSize = 80
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH, board.VIEW_BOX_HEIGHT + smallPiece.y + topMargin)
}

case object SVGCompactLayout extends SVGAreaLayout {
  private[this] val boardMargin = 79
  private[this] val topMargin = 30
  private[this] val symbolSize = 150
  private[this] val symbolOffset = 20
  private[this] val playerNameFontSize = 100
  private[this] val indicatorHeight = 76
  private[this] val indicatorFontSize = 66
  private[this] val playerBorderStroke = 5
  private[this] val indicatorBackgroundStroke = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(boardMargin + smallPiece.x, 0), mediumPiece.x, mediumPiece.y)

  private[this] val symbolTopMargin = 5
  private[this] val playerNameHeight = board.offset.y + board.MARGIN_SIZE + mediumPiece.y * 3 - symbolSize - indicatorHeight + symbolOffset - playerBorderStroke - symbolTopMargin
  private[this] val indicatorBackgroundHeight = mediumPiece.y * 6 + playerNameHeight + 2 * playerBorderStroke + 1

  override val hand: SVGHandLayout = SVGHandLayout(
    board.center,
    board.offset + Coord(board.VIEW_BOX_WIDTH + topMargin, boardMargin + 3 * mediumPiece.y),
    smallPiece.x, smallPiece.y, 7, 1
  )

  private[this] val blackSymbolArea = Rect(Coord(hand.blackOffset.x + (smallPiece.x - symbolSize) / 2, symbolTopMargin), symbolSize, symbolSize)
  private[this] val blackIndicatorArea = Rect(Coord(hand.blackOffset.x - playerBorderStroke, blackSymbolArea.bottom - symbolOffset), smallPiece.x + indicatorBackgroundStroke + playerBorderStroke * 2, indicatorHeight)
  private[this] val blackPlayerNameArea = Rect(blackIndicatorArea.leftBottom + Coord(playerBorderStroke, playerBorderStroke), smallPiece.x, playerNameHeight)
  private[this] val blackPlayerNameTextArea = (blackPlayerNameArea + Coord(0, 20)).copy(height = playerNameHeight - 20)
  private[this] val blackIndicatorBackground = Seq(
    Rect(blackIndicatorArea.rightBottom - Coord(indicatorBackgroundStroke, 1), indicatorBackgroundStroke, indicatorBackgroundHeight)
  )

  override def player: SVGPlayerLayout = SVGPlayerLayout(
    board.center, blackPlayerNameArea, blackSymbolArea, blackPlayerNameTextArea, blackIndicatorArea, blackIndicatorBackground,
    playerNameFontSize = playerNameFontSize,
    indicatorFontSize = indicatorFontSize,
    playerNameTopToBottom = true
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH + smallPiece.x + boardMargin, board.VIEW_BOX_HEIGHT)
}

case object SVGWideLayout extends SVGAreaLayout {
  private[this] val boardMargin = 79
  private[this] val topMargin = 30
  private[this] val symbolSize = 150
  private[this] val symbolOffset = 30
  private[this] val indicatorHeight = 87
  private[this] val playerBorderStroke = 5
  private[this] val indicatorBackgroundStroke = 30
  private[this] val textOffset = 20
  private[this] val playerHeight = smallPiece.y

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(boardMargin + mediumPiece.x * 2, 0), mediumPiece.x, mediumPiece.y)

  private[this] val blackHandTopLeft = board.offset + Coord(board.VIEW_BOX_WIDTH + topMargin, boardMargin + 5 * mediumPiece.y)

  override val hand: SVGHandLayout = SVGHandLayout(board.center, blackHandTopLeft, mediumPiece.x, mediumPiece.y, 4, 2)

  private[this] val blackPlayerArea = Rect(blackHandTopLeft - Coord(0, playerHeight), 2 * mediumPiece.x, playerHeight)
  private[this] val blackSymbolArea = Rect(blackPlayerArea.leftTop + Coord(textOffset, symbolOffset - symbolSize), symbolSize, symbolSize)
  private[this] val blackPlayerNameArea = Rect(blackPlayerArea.leftTop + Coord(playerBorderStroke + textOffset, playerBorderStroke), blackPlayerArea.width - 2 * playerBorderStroke - textOffset, blackPlayerArea.height - 2 * playerBorderStroke - indicatorHeight)
  private[this] val blackIndicatorArea = Rect(blackPlayerNameArea.leftBottom - Coord(textOffset, 0), blackPlayerNameArea.width + textOffset, indicatorHeight)
  private[this] val blackIndicatorBackground = Seq(
    Rect(
      blackPlayerArea.leftTop - Coord(playerBorderStroke, playerBorderStroke + indicatorBackgroundStroke),
      blackPlayerArea.width + 2 * playerBorderStroke + indicatorBackgroundStroke,
      indicatorBackgroundStroke
    ),
    Rect(
      blackPlayerArea.rightTop + Coord(playerBorderStroke, -playerBorderStroke - 1),
      indicatorBackgroundStroke, 4 * mediumPiece.y + playerHeight + playerBorderStroke * 2 + 1
    )
  )

  override def player: SVGPlayerLayout = SVGPlayerLayout(
    board.center, blackPlayerArea, blackSymbolArea, blackPlayerNameArea, blackIndicatorArea, blackIndicatorBackground,
    playerNameFontSize = 80,
    indicatorFontSize = 80
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH + 2 * mediumPiece.x + boardMargin, board.VIEW_BOX_HEIGHT)
}