package com.mogproject.mogami.frontend.view.board.box

import com.mogproject.mogami.core.Ptype
import com.mogproject.mogami.frontend.model.board.cursor.{BoxCursor, Cursor}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.board.effect._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.RectElement


/**
  *
  */
case class SVGBox(layout: SVGBoxLayout, foremostElement: SVGElement) extends SVGBoxPieceManager with EffectorTarget {

  protected def self: SVGBox = this

  private[this] val boxPtypes: Seq[Ptype] = Ptype.KING +: Ptype.inHand

  //
  // Elements
  //
  private[this] val boxBackgroundElement: RectElement = layout.boxBackground.render

  private[this] val borderElements: Seq[SVGElement] = (layout.boxShadow ++ Seq(layout.boxBorder, layout.boxLabelText)).map(_.render)

  override def clientPos2Cursor(clientX: Double, clientY: Double): Option[Cursor] = {
    val r = borderElements(2).getBoundingClientRect()
    (r.left <= clientX && clientX <= r.right && r.top <= clientY && clientY <= r.bottom).option {
      BoxCursor(boxPtypes(math.min(7, math.floor((clientX - r.left) / (r.width / 8)).toInt)))
    }
  }

  override protected def thresholdElement: Element = borderElements.head

  val elements: Seq[SVGElement] = boxBackgroundElement +: borderElements

  //
  // Operation
  //
  def unselect(): Unit = {
    effect.selectedEffector.stop()
    effect.cursorEffector.stop()
    effect.selectingEffector.stop()
  }

  def drawBackgroundColor(color: String): Unit = {
    boxBackgroundElement.style.fill = color
  }

  //
  // Effect
  //
  object effect {
    lazy val cursorEffector = CursorEffector(self)
    lazy val selectedEffector = SelectedEffector(self)
    lazy val flashEffector = FlashEffector(self)
    lazy val selectingEffector = SelectingEffector(self)
  }

}
