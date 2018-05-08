package com.mogproject.mogami.frontend.view.board.board


import com.mogproject.mogami.Square
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.{Coord, Rect}
import org.scalajs.dom.svg.{Circle, Line, RectElement}

import scalatags.JsDom.{TypedTag, svgAttrs}
import scalatags.JsDom.all._

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

  def getPieceRect(square: Square, isFlipped: Boolean): Rect = getRect(square, isFlipped).toInnerRect(PIECE_FACE_WIDTH, PIECE_FACE_HEIGHT)

  // Index
  def getJapaneseRankIndexImagePath(index: Int): String = s"assets/img/n/N${index}.svg"

  def getFileIndexRect(index: Int, isFlipped: Boolean): Rect = {
    val base = getRect(Square(isFlipped.fold(10 - index, index), 1), isFlipped = false)
    base.copy(leftTop = base.leftTop + Coord(0, -MARGIN_SIZE), height = MARGIN_SIZE).toInnerRect(INDEX_SIZE, INDEX_SIZE)
  }

  def getRankIndexRect(index: Int, isFlipped: Boolean): Rect = {
    val base = getRect(Square(1, isFlipped.fold(10 - index, index)), isFlipped = false)
    base.copy(leftTop = base.leftTop + Coord(pieceWidth, 0), width = MARGIN_SIZE).toInnerRect(INDEX_SIZE, INDEX_SIZE)
  }

  def center: Coord = offset + Coord(MARGIN_SIZE + pieceWidth * 9 / 2, MARGIN_SIZE + pieceHeight * 9 / 2)

  final val VIEW_BOX_WIDTH: Int = 2048
  final val PIECE_FACE_WIDTH: Int = pieceWidth * 20 / 21
  final val PIECE_FACE_HEIGHT: Int = pieceHeight * 20 / 21
  final val BOARD_WIDTH: Int = pieceWidth * 9
  final val BOARD_HEIGHT: Int = pieceHeight * 9
  final val MARGIN_SIZE: Int = (VIEW_BOX_WIDTH - BOARD_WIDTH) / 2
  final val CIRCLE_SIZE: Int = 14

  /** @note Corresponds to CSS board-index-text:font-size */
  final val INDEX_SIZE: Int = 60

  final val VIEW_BOX_HEIGHT: Int = BOARD_HEIGHT + MARGIN_SIZE * 2

  // Elements
  def boardBorderRect = Rect(getCoord(0, 0), BOARD_WIDTH, BOARD_HEIGHT)

  def boardBorder: TypedTag[RectElement] = boardBorderRect.toSVGRect(cls := "board-border")

  def boardBackground: TypedTag[RectElement] = boardBorderRect.toSVGRect()

  def boardLineRects: Seq[Rect] = for {
    i <- 1 to 8
    r <- Seq(Rect(getCoord(0, i), BOARD_WIDTH, 0), Rect(getCoord(i, 0), 0, BOARD_HEIGHT))
  } yield r

  def boardLines: Seq[TypedTag[Line]] = boardLineRects.map(_.toSVGLine(cls := "board-line"))

  def boardCircleCoords: Seq[Coord] = (0 to 3).map(i => getCoord(3 << (i & 1), 3 << ((i >> 1) & 1)))

  def boardCircles: Seq[TypedTag[Circle]] = boardCircleCoords.map(_.toSVGCircle(CIRCLE_SIZE, cls := "board-circle"))

}
