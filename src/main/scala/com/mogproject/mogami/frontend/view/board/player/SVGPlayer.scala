package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.BoardIndicator
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.{Cursor, Flippable, SymmetricElement}
import com.mogproject.mogami.frontend.view.board.effect.EffectorTarget
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.{Element, svg}
import org.scalajs.dom.raw.{SVGElement, SVGImageElement}
import org.scalajs.dom.svg.RectElement

import scala.collection.mutable
import scalatags.JsDom.all._

/**
  *
  */
case class SVGPlayer(layout: SVGPlayerLayout) extends EffectorTarget with Flippable {

  protected def self: SVGPlayer = this

  private implicit val flippable: Flippable = this

  //
  // Variables
  //
  private[this] val playerNames: mutable.Map[Player, Option[String]] = mutable.Map(Player.constructor.map(_ -> None): _*)

  private[this] val indicators: mutable.Map[Player, Option[BoardIndicator]] = mutable.Map(Player.constructor.map(_ -> None): _*)

  //
  // Utility
  //
  private[this] def getSymbolImagePath(player: Player): String = s"assets/img/p/common/${player.toString.take(2)}.svg"

  //
  // Elements
  //
  private[this] val borderElements: Seq[RectElement] = Seq(layout.whiteBorder, layout.blackBorder).map(_.render)

  private[this] val symbolElements: SymmetricElement[SVGImageElement] = SymmetricElement { pl =>
    layout.getSymbolArea(pl).toSVGImage(getSymbolImagePath(getFlippedPlayer(pl)), rotated = pl.isWhite).render
  }

  private[this] val nameElements: SymmetricElement[svg.Text] = SymmetricElement { pl =>
    val area = layout.getNameArea(pl)
    val r = Rect(pl.isBlack.fold(Coord(0, 0), Coord(-10, 30)), area.width, area.height)
    r.toSVGText("", pl.isWhite, cls := "player-name-text").render
  }

  private[this] val nameElementsWrapper: SymmetricElement[svg.SVG] = SymmetricElement { pl =>
    val area = layout.getNameArea(pl)
    val r = Rect(pl.isBlack.fold(area.leftTop, area.leftTop.copy(area.left + 10, area.top - 30)), area.width - 10, area.height + 30)
    r.toSVGWrapper(nameElements.get(pl)).render
  }

  private[this] val indicatorBackgrounds: SymmetricElement[RectElement] = SymmetricElement(
    layout.getIndicatorBackground(BLACK).map(_.toSVGRect().render),
    layout.getIndicatorBackground(WHITE).map(_.toSVGRect().render)
  )

  private[this] val indicatorTextElements: SymmetricElement[svg.Text] = SymmetricElement { pl =>
    layout.getIndicatorArea(pl).toSVGText("", pl.isWhite, cls := "indicator-text").render
  }

  val elements: Seq[SVGElement] = indicatorBackgrounds.values ++ borderElements ++ symbolElements.values ++ nameElementsWrapper.values ++ indicatorTextElements.values

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
    refreshNames()
    refreshIndicators()
  }

  def drawSymbols(): Unit = {
    symbolElements.foreach { case (pl, elem) =>
      elem.setAttribute("xlink:href", getSymbolImagePath(getFlippedPlayer(pl)))
    }
  }

  def drawNames(blackName: Option[String], whiteName: Option[String]): Unit = {
    playerNames(BLACK) = blackName
    playerNames(WHITE) = whiteName
    refreshNames()
  }

  def refreshNames(): Unit = {
    playerNames.foreach { case (pl, s) => nameElements.getFirst(pl).textContent = s.getOrElse("") }
  }

  def drawIndicators(blackIndicator: Option[BoardIndicator], whiteIndicator: Option[BoardIndicator]): Unit = {
    indicators(BLACK) = blackIndicator
    indicators(WHITE) = whiteIndicator
    refreshIndicators()
  }

  def refreshIndicators(): Unit = {
    indicators.foreach {
      case (pl, ind) =>
        indicatorTextElements.getFirst(pl).textContent = ind.map(_.text).getOrElse("")
        indicatorBackgrounds.get(pl).foreach(WebComponent.setClass(_, ind.map(_.className).getOrElse("indicator-none")))
    }

  }
}
