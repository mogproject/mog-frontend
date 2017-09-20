package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.effect._
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.{Circle, Line, RectElement}
import org.scalajs.dom.{ClientRect, Element}

import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.svg
import scalatags.JsDom.{TypedTag, svgAttrs}

/**
  *
  */
case class SVGBoard(offset: Coord, getBoardFlipped: () => Boolean) extends WebComponent with SVGBoardPieceManager with SVGBoardIndexManager  {

  private[this] val self = this

  import SVGBoard._

  // Local variables



  //
  // Utility
  //
  protected def getCoord(fileIndex: Int, rankIndex: Int): Coord = offset + Coord(MARGIN_SIZE + fileIndex * PIECE_WIDTH, MARGIN_SIZE + rankIndex * PIECE_HEIGHT)

  protected def getRect(fileIndex: Int, rankIndex: Int): Rect = Rect(getCoord(fileIndex, rankIndex), PIECE_WIDTH, PIECE_HEIGHT)

  def getRect(square: Square): Rect = {
    val s = getBoardFlipped().when[Square](!_)(square)
    getRect(9 - s.file, s.rank - 1)
  }

  def getBorderClientRect: ClientRect = borderElement.getBoundingClientRect()

  def materializeBackground[A <: Element](elem: A): A = {
    svgElement.insertBefore(elem, borderElement)
    elem
  }

  def materializeBackground[A <: Element](elems: Seq[A]): Seq[A] = {
    elems.map(materializeBackground[A])
  }

  def materializeForeground[A <: Element](elem: A): A = {
    svgElement.appendChild(elem)
    elem
  }

  def materializeForeground[A <: Element](elems: Seq[A]): Seq[A] = {
    elems.map(materializeForeground[A])
  }

  //
  // Operation
  //
  def setFlip(flip: Boolean): Unit = {
    refreshPieces()
    refreshIndexes()
  }

  def resize(newWidth: Int): Unit = element.asInstanceOf[Div].style.width = newWidth.px

  def unselect(): Unit = {
    effect.selectedEffector.stop() // a selected square is released here
    effect.cursorEffector.stop()
    effect.selectingEffector.stop()
    effect.legalMoveEffector.stop()
    effect.pieceFlipEffector.stop()
  }

  //
  // View
  //
  private[this] val boardBoarder = Rect(getCoord(0, 0), BOARD_WIDTH, BOARD_HEIGHT).toSVGRect(cls := "board-border")

  private[this] val boardLines: Seq[TypedTag[Line]] = for {
    i <- 1 to 8
    r <- Seq(Rect(getCoord(0, i), BOARD_WIDTH, 0), Rect(getCoord(i, 0), 0, BOARD_HEIGHT))
  } yield r.toSVGLine(cls := "board-line")

  private[this] lazy val boardCircles: Seq[TypedTag[Circle]] = (0 to 3).map { i =>
    getCoord(3 << (i & 1), 3 << ((i >> 1) & 1)).toSVGCircle(CIRCLE_SIZE, cls := "board-circle")
  }

  protected val borderElement: RectElement = boardBoarder.render

  private[this] val svgElement: SVGElement = svg(
    svgAttrs.width := 100.pct,
    svgAttrs.height := 100.pct,
    svgAttrs.viewBox := s"0 0 ${offset.x + VIEW_BOX_WIDTH} ${offset.y + BOARD_HEIGHT + MARGIN_SIZE * 2}",
    borderElement
  )(boardLines ++ boardCircles: _*).render

  override lazy val element: Element = div(
    svgElement
  ).render

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

object SVGBoard {
  val VIEW_BOX_WIDTH: Int = 2048
  val PIECE_WIDTH: Int = 210
  val PIECE_HEIGHT: Int = 230
  val PIECE_FACE_SIZE: Int = 200
  val BOARD_WIDTH: Int = PIECE_WIDTH * 9
  val BOARD_HEIGHT: Int = PIECE_HEIGHT * 9
  val MARGIN_SIZE: Int = (VIEW_BOX_WIDTH - BOARD_WIDTH) / 2
  val CIRCLE_SIZE: Int = 14
  val INDEX_SIZE: Int = 60 /** @note Corresponds to CSS board-index-text:font-size */
}