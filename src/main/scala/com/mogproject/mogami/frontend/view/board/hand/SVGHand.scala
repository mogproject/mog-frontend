package com.mogproject.mogami.frontend.view.board.hand

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami._
import com.mogproject.mogami.frontend.view.board.{Cursor, HandCursor}
import com.mogproject.mogami.frontend.view.board.effect._
import com.mogproject.mogami.frontend.view.coordinate.Rect
import org.scalajs.dom.{ClientRect, Element}
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.RectElement

/**
  * Hand
  */
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

  def getRect(hand: Hand): Rect = getRect(hand.toPiece)

  override def clientPos2Cursor(clientX: Double, clientY: Double): Option[Cursor] = {
    (borderElements.zipWithIndex.toStream.flatMap { case (r, i) =>
      clientRect2Index(clientX, clientY, r.getBoundingClientRect()).map(i -> _)
    }.headOption match {
      case (Some((0, sortId))) => Some(Player.WHITE -> Ptype.inHand(6 - sortId))
      case (Some((1, sortId))) => Some(Player.BLACK -> Ptype.inHand(sortId))
      case _ => None
    }).map { case (pl, pt) => HandCursor(Hand(isFlipped.when[Player](!_)(pl), pt)) }
  }

  private def clientRect2Index(clientX: Double, clientY: Double, r: ClientRect): Option[Int] = {
    (r.left <= clientX && clientX <= r.right && r.top <= clientY && clientY <= r.bottom).option {
      val xi = math.floor((clientX - r.left) / (r.width / layout.numColumns)).toInt
      val yi = math.floor((clientY - r.top) / (r.height / layout.numRows)).toInt
      yi * layout.numRows + xi
    }
  }

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
