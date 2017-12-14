package com.mogproject.mogami.frontend.view.board.box

import com.mogproject.mogami.frontend.view.board.Cursor
import com.mogproject.mogami.frontend.view.board.effect._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.SVGElement


/**
  *
  */
case class SVGBox(layout: SVGBoxLayout) extends SVGBoxPieceManager with EffectorTarget {

  protected def self: SVGBox = this


  //
  // Elements
  //
  private[this] val borderElements: Seq[SVGElement] = Seq(layout.boxBorder, layout.boxLabel, layout.boxLabelText).map(_.render)

  override def clientPos2Cursor(clientX: Double, clientY: Double): Option[Cursor] = ???

  override protected def thresholdElement: Element = borderElements.head

  val elements: Seq[SVGElement] = borderElements

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
    lazy val flashEffector = FlashEffector(self)
    lazy val selectingEffector = SelectingEffector(self)
  }

}
