package com.mogproject.mogami.frontend.view.board.hand

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami._
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

  def clientPos2Hand(clientX: Double, clientY: Double): Option[Hand] = {
    val wSortId = clientRect2Index(clientX, clientY, borderElements.head.getBoundingClientRect())
    if (wSortId.isDefined) {
      Some(Hand(isFlipped.fold(Player.BLACK, Player.WHITE), Ptype.inHand(6 - wSortId.get)))
    } else {
      val bSortId = clientRect2Index(clientX, clientY, borderElements(1).getBoundingClientRect())
      if (bSortId.isDefined) {
        Some(Hand(isFlipped.fold(Player.WHITE, Player.BLACK), Ptype.inHand(bSortId.get)))
      } else {
        None
      }
    }
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
