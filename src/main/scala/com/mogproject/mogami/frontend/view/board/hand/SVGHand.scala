package com.mogproject.mogami.frontend.view.board.hand

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.Piece
import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.board.effect._
import com.mogproject.mogami.frontend.view.coordinate.Rect
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{SVGElement, SVGRect}
import org.scalajs.dom.svg.{Circle, Line, RectElement}

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Hand layout
  */
case class SVGHandLayout(whiteOffset: Coord, blackOffset: Coord, pieceWidth: Int, pieceHeight: Int, pieceInterval: Int, numRows: Int, numColumns: Int) {

  final val PIECE_FACE_SIZE: Int = pieceWidth * 20 / 21

  final val NUMBER_SIZE: Int = (pieceWidth + pieceInterval) * 2 / 3

  def getRect(piece: Piece, isFlipped: Boolean): Rect = {
    val isBlackArea = piece.owner.isBlack ^ isFlipped
    val index = piece.ptype.sortId - 1
    val (row, column) = (index / numColumns, index % numColumns)
    val (os, c, r) = isBlackArea.fold((blackOffset, column, row), (whiteOffset, numColumns - 1 - column, numRows - 1 - row))
    Rect(os + Coord(c * (pieceWidth + pieceInterval), r * pieceHeight), pieceWidth + pieceInterval, pieceHeight)
  }

  private[this] def generateBorder(offset: Coord): TypedTag[RectElement] = Rect(offset, (pieceWidth + pieceInterval) * numColumns, pieceHeight * numRows).toSVGRect(cls := "board-border")

  def whiteBorder: TypedTag[RectElement] = generateBorder(whiteOffset)

  def blackBorder: TypedTag[RectElement] = generateBorder(blackOffset)

}

case class SVGHand(layout: SVGHandLayout) extends SVGHandPieceManager with EffectorTarget {

  protected def self: SVGHand = this

  private[this] var isFlipped: Boolean = false

  def setIsFlipped(isFlipped: Boolean): Unit = this.isFlipped = isFlipped

  def getIsFlipped: Boolean = isFlipped

  //
  // Elements
  //
  private[this] val borderElements: Seq[RectElement] = Seq(layout.whiteBorder, layout.blackBorder).map(_.render)

  override protected def thresholdElement: Element = borderElements.head

  val elements: Seq[SVGElement] = borderElements

  // Utility
  def getRect(piece: Piece): Rect = layout.getRect(piece, isFlipped)

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = {
    isFlipped = flip
    refreshPieces()
  }

  def unselect(): Unit = {
    effect.selectedEffector.stop()
    effect.cursorEffector.stop()
    effect.selectingEffector.stop()
  }

  //
  // Effect
  //
  object effect {
    lazy val cursorEffector = CursorEffector(self)
    lazy val selectedEffector = SelectedEffector(self)
    lazy val lastMoveEffector = LastMoveEffector(self)
    lazy val flashEffector = FlashEffector(self)
    lazy val selectingEffector = SelectingEffector(self)
  }

}
