package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.frontend.model.board.BoardIndicator
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.board.{Cursor, Flippable}
import com.mogproject.mogami.frontend.view.board.effect.EffectorTarget
import com.mogproject.mogami.util.Implicits._
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

  //
  // Variables
  //
  private[this] val playerNames: mutable.Map[Player, Option[String]] = mutable.Map(Player.constructor.map(_ -> None): _*)

  private[this] val indicators:  mutable.Map[Player, Option[BoardIndicator]] = mutable.Map(Player.constructor.map(_ -> None): _*)

  //
  // Utility
  //
  private[this] def getSymbolImagePath(player: Player): String = s"assets/img/p/common/${player.toString.take(2)}.svg"

  //
  // Elements
  //
  private[this] val borderElements: Seq[RectElement] = Seq(layout.whiteBorder, layout.blackBorder).map(_.render)

  private[this] val symbolElements: Map[Player, SVGImageElement] = Player.constructor.map { pl =>
    pl -> layout.getSymbolArea(pl).toSVGImage(getSymbolImagePath(isFlipped.when[Player](!_)(pl)), rotated = pl.isWhite).render
  }.toMap

  private[this] val nameElements: Map[Player, svg.Text] = Player.constructor.map { pl =>
    pl -> layout.getNameArea(pl).toSVGText("", pl.isWhite, cls := "player-name-text").render
  }.toMap

  private[this] val indicatorBackgrounds: Map[Player, Seq[RectElement]] = Player.constructor.map { pl =>
    pl -> layout.getIndicatorBackground(pl).map(_.toSVGRect().render)
  }.toMap

  private[this] val indicatorTextElements: Map[Player, svg.Text] = Player.constructor.map { pl =>
    pl -> layout.getIndicatorArea(pl).toSVGText("", pl.isWhite, cls := "indicator-text").render
  }.toMap

  val elements: Seq[SVGElement] = indicatorBackgrounds.values.flatten.toSeq ++ borderElements ++ symbolElements.values ++ nameElements.values ++ indicatorTextElements.values

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
      elem.setAttribute("xlink:href", getSymbolImagePath(isFlipped.when[Player](!_)(pl)))
    }
  }

  def drawNames(blackName: Option[String], whiteName: Option[String]): Unit = {
    playerNames(BLACK) = blackName
    playerNames(WHITE) = whiteName
    refreshNames()
  }

  def refreshNames(): Unit = {
    playerNames.foreach { case (pl, s) => nameElements(isFlipped.when[Player](!_)(pl)).textContent = s.getOrElse("")}
  }

  def drawIndicators(blackIndicator: Option[BoardIndicator], whiteIndicator: Option[BoardIndicator]): Unit = {
    indicators(BLACK) = blackIndicator
    indicators(WHITE) = whiteIndicator
    refreshIndicators()
  }

  def refreshIndicators(): Unit = {
    indicators.foreach {
      case (pl, ind) =>
        indicatorTextElements(isFlipped.when[Player](!_)(pl)).textContent = ind.map(_.text).getOrElse("")
        indicatorBackgrounds(isFlipped.when[Player](!_)(pl)).foreach(WebComponent.setClass(_, ind.map(_.className).getOrElse("indicator-none")))
    }

  }
}
