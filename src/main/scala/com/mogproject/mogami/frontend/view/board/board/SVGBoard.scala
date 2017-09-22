package com.mogproject.mogami.frontend.view.board.board

import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.view.board.effect._
import com.mogproject.mogami.frontend.view.coordinate.Rect
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.{Circle, Line, RectElement}
import org.scalajs.dom.{ClientRect, Element}

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
case class SVGBoard(layout: SVGBoardLayout) extends SVGBoardPieceManager with SVGBoardIndexManager with EffectorTarget {

  import layout._

  protected def self: SVGBoard = this

  private[this] var isFlipped: Boolean = false

  def setIsFlipped(isFlipped: Boolean): Unit = this.isFlipped = isFlipped

  def getIsFlipped: Boolean = isFlipped

  //
  // Elements
  //
  private[this] val boardBoarder = Rect(getCoord(0, 0), BOARD_WIDTH, BOARD_HEIGHT).toSVGRect(cls := "board-border")

  private[this] val boardLines: Seq[TypedTag[Line]] = for {
    i <- 1 to 8
    r <- Seq(Rect(getCoord(0, i), BOARD_WIDTH, 0), Rect(getCoord(i, 0), 0, BOARD_HEIGHT))
  } yield r.toSVGLine(cls := "board-line")

  private[this] val boardCircles: Seq[TypedTag[Circle]] = (0 to 3).map { i =>
    getCoord(3 << (i & 1), 3 << ((i >> 1) & 1)).toSVGCircle(CIRCLE_SIZE, cls := "board-circle")
  }

  private[this] val borderElement: RectElement = boardBoarder.render

  override protected def thresholdElement: Element = borderElement

  val elements: Seq[SVGElement] = borderElement +: (boardLines ++ boardCircles).map(_.render)

  // Utility
  def getBorderClientRect: ClientRect = borderElement.getBoundingClientRect()

  def getRect(square: Square): Rect = layout.getRect(square, isFlipped)

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = {
    isFlipped = flip
    refreshPieces()
    refreshIndexes()
  }

  def unselect(): Unit = {
    effect.selectedEffector.stop() // a selected square is released here
    effect.cursorEffector.stop()
    effect.selectingEffector.stop()
    effect.legalMoveEffector.stop()
    effect.pieceFlipEffector.stop()
  }

  //
  // Effect
  //
  object effect {
    lazy val cursorEffector = CursorEffector(self)
    lazy val selectedEffector = SelectedEffector(self)
    lazy val lastMoveEffector = LastMoveEffector(self)
    lazy val flashEffector = FlashEffector(self)
    lazy val moveEffector = MoveEffector(self)
    lazy val selectingEffector = SelectingEffector(self)
    lazy val legalMoveEffector = LegalMoveEffector(self)
    lazy val pieceFlipEffector = PieceFlipEffector(self)
    lazy val forwardEffector = ForwardEffector(self)
  }

}
