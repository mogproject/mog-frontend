package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.view.board.{Cursor, Flippable}
import com.mogproject.mogami.frontend.view.board.effect.EffectorTarget
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.SVGElement
import org.scalajs.dom.svg.RectElement

/**
  *
  */
case class SVGPlayer(layout: SVGPlayerLayout) extends SVGPlayerSymbolManager with EffectorTarget with Flippable {

  protected def self: SVGPlayer = this

  //
  // Elements
  //
  private[this] val borderElements: Seq[RectElement] = Seq(layout.whiteBorder, layout.blackBorder).map(_.render)

  val elements: Seq[SVGElement] = borderElements

  override protected def thresholdElement: Element = borderElements.head

  override def clientPos2Cursor(clientX: Double, clientY: Double): Option[Cursor] = {
    ???
  }


  //
  // Operation
  //
  override def setFlip(flip: Boolean): Unit = {
    super.setFlip(flip)
    drawSymbols()
  }

}
