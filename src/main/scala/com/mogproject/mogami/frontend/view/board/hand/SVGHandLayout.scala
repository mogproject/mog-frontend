package com.mogproject.mogami.frontend.view.board.hand


import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.Piece
import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.coordinate.Rect
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Hand layout
  */
case class SVGHandLayout(whiteOffset: Coord, blackOffset: Coord, pieceWidth: Int, pieceHeight: Int, numRows: Int, numColumns: Int) {

  final val PIECE_FACE_SIZE: Int = pieceWidth * 20 / 21

  final val numberAdjustment: Coord = Coord(-pieceWidth / 18, pieceWidth / 9)

  def getRect(piece: Piece, isFlipped: Boolean): Rect = {
    val isBlackArea = piece.owner.isBlack ^ isFlipped
    val index = piece.ptype.sortId - 1
    val (row, column) = (index / numColumns, index % numColumns)
    val (os, c, r) = isBlackArea.fold((blackOffset, column, row), (whiteOffset, numColumns - 1 - column, numRows - 1 - row))
    Rect(os + Coord(c * pieceWidth, r * pieceHeight), pieceWidth, pieceHeight)
  }

  private[this] def generateBorder(offset: Coord): TypedTag[RectElement] = Rect(offset, pieceWidth * numColumns, pieceHeight * numRows).toSVGRect(cls := "board-border")

  // Elements
  def whiteBorder: TypedTag[RectElement] = generateBorder(whiteOffset)

  def blackBorder: TypedTag[RectElement] = generateBorder(blackOffset)

}

