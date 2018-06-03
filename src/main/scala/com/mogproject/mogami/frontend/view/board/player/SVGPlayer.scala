package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.BoardIndicator
import com.mogproject.mogami.frontend.model.board.cursor.{Cursor, PlayerCursor}
import com.mogproject.mogami.frontend.view.{SVGImageCache, WebComponent}
import com.mogproject.mogami.frontend.view.board.{Flippable, SymmetricElement}
import com.mogproject.mogami.frontend.view.board.effect.{CursorEffector, EffectorTarget, FlashEffector}
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.{Element, svg}
import org.scalajs.dom.raw.{SVGElement, SVGImageElement}
import org.scalajs.dom.svg.RectElement

import scala.collection.mutable
import scalatags.JsDom.all._

/**
  *
  */
case class SVGPlayer(layout: SVGPlayerLayout, foremostElement: SVGElement)(implicit imageCache: SVGImageCache) extends EffectorTarget with Flippable {

  protected def self: SVGPlayer = this

  private implicit val flippable: Flippable = this

  //
  // Variables
  //
  private[this] val playerNames: mutable.Map[Player, String] = mutable.Map(Player.constructor.map(_ -> ""): _*)

  private[this] val indicators: mutable.Map[Player, Option[BoardIndicator]] = mutable.Map(Player.constructor.map(_ -> None): _*)

  //
  // Utility
  //
  def getRect(player: Player): Rect = layout.getRectByPlayer(getFlippedPlayer(player), layout.blackNameRect)

  //
  // Elements
  //
  private[this] val borderElements: Seq[RectElement] = layout.borders.map(_.render)

  private[this] val symbolElements: SymmetricElement[SVGImageElement] = SymmetricElement { pl =>
    layout.getSymbolArea(pl).toSVGImage("", rotated = pl.isWhite).render
  }

  private[this] val nameElements: SymmetricElement[svg.Text] = SymmetricElement { pl =>
    val fs = layout.playerNameFontSize
    val fc = fs * 7 / 10 // center of the font

    val classNames = "player-name-text" + layout.playerNameTopToBottom.fold(" text-tb", "")

    layout.getNameArea(pl).copy(leftTop = Coord(0, 0))
      .toSVGText("", pl.isWhite, false, Some((fs, fc, layout.playerNameTopToBottom)), cls := classNames).render
  }

  private[this] val nameElementsWrapper: SymmetricElement[svg.SVG] = SymmetricElement { pl =>
    layout.getNameArea(pl).toSVGWrapper(nameElements.get(pl)).render
  }

  private[this] val indicatorBackgrounds: SymmetricElement[RectElement] = SymmetricElement(
    layout.getIndicatorBackground(BLACK).map(_.toSVGRect().render),
    layout.getIndicatorBackground(WHITE).map(_.toSVGRect().render)
  )

  private[this] val indicatorTextElements: SymmetricElement[svg.Text] = SymmetricElement { pl =>
    val fs = layout.indicatorFontSize
    val fc = fs * 67 / 100 // center of the font

    layout.getIndicatorTextArea(pl).toSVGText("", pl.isWhite, true, Some((fs, fc, false)), cls := "indicator-text").render
  }

  val elements: Seq[SVGElement] = borderElements ++ indicatorBackgrounds.values ++ symbolElements.values ++ nameElementsWrapper.values ++ indicatorTextElements.values

  override protected def thresholdElement: Element = symbolElements.values.head

  override def clientPos2Cursor(clientX: Double, clientY: Double): Option[Cursor] = {
    (for {
      (pl, elem) <- Player.constructor.zip(borderElements)
      r = elem.getBoundingClientRect()
      if r.left <= clientX && clientX <= r.right && r.top <= clientY && clientY <= r.bottom
    } yield PlayerCursor(getFlippedPlayer(pl))).headOption
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
      elem.setAttribute("xlink:href", imageCache.getURL(layout.getSymbolImagePath(getFlippedPlayer(pl))))
    }
  }

  def drawNames(blackName: String, whiteName: String): Unit = {
    playerNames(BLACK) = blackName
    playerNames(WHITE) = whiteName
    refreshNames()
  }

  def drawNames(names: Map[Player, String]): Unit = {
    drawNames(names(BLACK), names(WHITE))
  }

  def refreshNames(): Unit = {
    playerNames.foreach { case (pl, s) => nameElements.getFirst(pl).textContent = s }
  }

  def drawIndicators(blackIndicator: Option[BoardIndicator], whiteIndicator: Option[BoardIndicator]): Unit = {
    indicators(BLACK) = blackIndicator
    indicators(WHITE) = whiteIndicator
    refreshIndicators()
  }

  def drawIndicators(indicators: Map[Player, BoardIndicator]): Unit = {
    drawIndicators(indicators.get(BLACK), indicators.get(WHITE))
  }

  def refreshIndicators(): Unit = {
    indicators.foreach {
      case (pl, ind) =>
        indicatorTextElements.getFirst(pl).textContent = ind.map(_.text).getOrElse("") // Notes: possibly a performance bottleneck
        indicatorBackgrounds.get(pl).foreach(WebComponent.setClass(_, ind.map(_.className).getOrElse("indicator-none")))
    }
  }

  //
  // Effects
  //
  object effect {
    lazy val cursorEffector = CursorEffector(self)
    lazy val flashEffector = FlashEffector(self)
  }

}
