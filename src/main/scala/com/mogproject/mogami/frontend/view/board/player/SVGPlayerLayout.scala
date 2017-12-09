package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.Player
import com.mogproject.mogami.frontend.view.coordinate.Rect
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
case class SVGPlayerLayout(whiteNameRect: Rect,
                           whiteSymbolArea: Rect,
                           whiteNameArea: Rect,
                           whiteIndicatorArea: Rect,
                           whiteIndicatorBackground: Seq[Rect],
                           blackNameRect: Rect,
                           blackSymbolArea: Rect,
                           blackNameArea: Rect,
                           blackIndicatorArea: Rect,
                           blackIndicatorBackground: Seq[Rect]) {

  private[this] def generateBorder(rect: Rect): TypedTag[RectElement] = rect.toSVGRect(cls := "board-border")

  // Utility
  def getSymbolArea(player: Player): Rect = player.isBlack.fold(blackSymbolArea, whiteSymbolArea)

  def getNameArea(player: Player): Rect = player.isBlack.fold(blackNameArea, whiteNameArea)

  def getIndicatorArea(player: Player): Rect = player.isBlack.fold(blackIndicatorArea, whiteIndicatorArea)

  def getIndicatorBackground(player: Player): Seq[Rect] = player.isBlack.fold(blackIndicatorBackground, whiteIndicatorBackground)

  // Elements
  def whiteBorder: TypedTag[RectElement] = generateBorder(whiteNameRect)

  def blackBorder: TypedTag[RectElement] = generateBorder(blackNameRect)

}
