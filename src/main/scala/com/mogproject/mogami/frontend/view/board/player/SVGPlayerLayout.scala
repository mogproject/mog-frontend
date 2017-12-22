package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
case class SVGPlayerLayout(center: Coord,
                           blackNameRect: Rect,
                           blackSymbolArea: Rect,
                           blackNameArea: Rect,
                           blackIndicatorArea: Rect,
                           blackIndicatorBackground: Seq[Rect],
                           playerNameFontSize: Int,
                           indicatorFontSize: Int,
                           playerNameTopToBottom: Boolean = false
                          ) {

  private[this] def generateBorder(rect: Rect): TypedTag[RectElement] = rect.toSVGRect(cls := "player-border")

  def getSymbolImagePath(player: Player): String = s"assets/img/p/common/${player.toString.take(2)}.svg"

  def getRectByPlayer(player: Player, rect: Rect): Rect = player.isWhite.when[Rect](_.rotate(center))(rect)

  // Utility
  def getSymbolArea(player: Player): Rect = getRectByPlayer(player, blackSymbolArea)

  def getNameArea(player: Player): Rect = getRectByPlayer(player, blackNameArea)

  def getIndicatorTextArea(player: Player): Rect = {
    val r = blackIndicatorArea.copy(leftTop = Coord(blackIndicatorArea.center.x, blackIndicatorArea.top), width = 0, height = blackIndicatorArea.height)
    getRectByPlayer(player, r)
  }

  def getIndicatorBackground(player: Player): Seq[Rect] = (blackIndicatorBackground :+ blackIndicatorArea).map(getRectByPlayer(player, _))

  // Elements
  def borders: Seq[TypedTag[RectElement]] = Player.constructor.map(pl => generateBorder(getRectByPlayer(pl, blackNameRect)))

}
