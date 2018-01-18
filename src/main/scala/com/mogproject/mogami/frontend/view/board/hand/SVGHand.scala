package com.mogproject.mogami.frontend.view.board.hand

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.board.cursor.{Cursor, HandCursor}
import com.mogproject.mogami.frontend.view.SVGImageCache
import com.mogproject.mogami.frontend.view.board.Flippable
import com.mogproject.mogami.frontend.view.board.effect._
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.{ClientRect, Element}
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.RectElement

/**
  * Hand
  */
case class SVGHand(layout: SVGHandLayout, foremostElement: SVGElement)(implicit imageCache: SVGImageCache) extends SVGHandPieceManager with EffectorTarget with Flippable {

  protected def self: SVGHand = this

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
    val numRooms = layout.numRows * layout.numColumns

    (borderElements.zipWithIndex.toStream.flatMap { case (r, i) =>
      clientRect2Index(clientX, clientY, r.getBoundingClientRect()).map(i -> _)
    }.headOption match {
      case (Some((0, sortId))) if sortId >= numRooms - 7 => Some(Player.WHITE -> Ptype.inHand(numRooms - sortId - 1))
      case (Some((1, sortId))) if sortId <= 6 => Some(Player.BLACK -> Ptype.inHand(sortId))
      case _ => None
    }).map { case (pl, pt) => HandCursor(Hand(isFlipped.when[Player](!_)(pl), pt)) }
  }

  private[this] def clientRect2Index(clientX: Double, clientY: Double, r: ClientRect): Option[Int] = {
    (r.left <= clientX && clientX <= r.right && r.top <= clientY && clientY <= r.bottom).option {
      val xi = math.floor((clientX - r.left) / (r.width / layout.numColumns)).toInt
      val yi = math.floor((clientY - r.top) / (r.height / layout.numRows)).toInt
      xi + layout.numColumns * yi
    }
  }

  //
  // Operation
  //
  override def setFlip(flip: Boolean): Unit = {
    super.setFlip(flip)
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
