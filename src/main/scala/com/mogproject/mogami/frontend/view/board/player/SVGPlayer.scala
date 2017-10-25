package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.frontend.view.board.{Cursor, Flippable}
import com.mogproject.mogami.frontend.view.board.effect.EffectorTarget
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{SVGElement, SVGImageElement}
import org.scalajs.dom.svg.RectElement

/**
  *
  */
case class SVGPlayer(layout: SVGPlayerLayout) extends EffectorTarget with Flippable {

  protected def self: SVGPlayer = this

  //
  // Utility
  //
  private[this] def getSymbolImagePath(player: Player): String = s"assets/img/p/common/${player.toString.take(2)}.svg"

  //
  // Elements
  //
  private[this] val borderElements: Seq[RectElement] = Seq(layout.whiteBorder, layout.blackBorder).map(_.render)

  private[this] val symbolElements: Map[Player, SVGImageElement] = Seq(BLACK, WHITE).map { pl =>
    pl -> layout.getSymbolArea(pl).toSVGImage(getSymbolImagePath(isFlipped.when[Player](!_)(pl)), rotated = pl.isWhite).render
  }.toMap

  val elements: Seq[SVGElement] = borderElements ++ symbolElements.values

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

  def drawSymbols(): Unit = {
    symbolElements.foreach { case (pl, elem) =>
      elem.setAttribute("xlink:href", getSymbolImagePath(isFlipped.when[Player](!_)(pl)))
    }
  }

}
