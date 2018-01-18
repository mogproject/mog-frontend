package com.mogproject.mogami.frontend.view.board.board

import com.mogproject.mogami.frontend.model.PieceFace
import com.mogproject.mogami.frontend.model.board.cursor.{BoardCursor, Cursor}
import com.mogproject.mogami.frontend.view.SVGImageCache
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Piece, Square}
import com.mogproject.mogami.frontend.view.board.Flippable
import com.mogproject.mogami.frontend.view.board.effect._
import com.mogproject.mogami.frontend.view.coordinate.Rect
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.RectElement
import org.scalajs.dom.Element
import org.scalajs.dom

/**
  *
  */
case class SVGBoard(layout: SVGBoardLayout, foremostElement: SVGElement)(implicit imageCache: SVGImageCache) extends SVGBoardPieceManager with SVGBoardIndexManager with EffectorTarget with Flippable {

  import layout._

  protected def self: SVGBoard = this

  //
  // Elements
  //
  private[this] val borderElement: RectElement = boardBorder.render

  override protected def thresholdElement: Element = borderElement

  val elements: Seq[SVGElement] = borderElement +: (boardLines ++ boardCircles).map(_.render)

  //
  // Utility
  //
  override def clientPos2Cursor(clientX: Double, clientY: Double): Option[Cursor] = {
    val r = borderElement.getBoundingClientRect()
    val (x, y) = (clientX - r.left, clientY - r.top)
    val xi = math.floor(x / (r.width / 9)).toInt
    val yi = math.floor(y / (r.height / 9)).toInt

    (0 <= xi && xi < 9 && 0 <= yi && yi < 9).option(BoardCursor(isFlipped.when[Square](!_)(Square(9 - xi, 1 + yi))))
  }

  def getRect(square: Square): Rect = layout.getRect(square, isFlipped)

  //
  // Operation
  //
  override def setFlip(flip: Boolean): Unit = {
    super.setFlip(flip)
    refreshPieces()
    refreshIndexes()
  }

  def unselect(): Unit = {
    effect.selectedEffector.stop()
    effect.cursorEffector.stop()
    effect.selectingEffector.stop()
    effect.legalMoveEffector.stop()
    effect.pieceFlipEffector.stop()
  }

  def startPromotionEffect(square: Square, oldPiece: Piece, pieceFace: PieceFace): Unit = {
    hidePiece(square)
    effect.pieceFlipEffector.start(PieceFlipAttribute(square, oldPiece, oldPiece.promoted, pieceFace), () => showPiece(square))
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
