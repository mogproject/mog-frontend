package com.mogproject.mogami.frontend.view.board.box

import com.mogproject.mogami.Ptype
import com.mogproject.mogami.frontend.{Coord, Rect}
import org.scalajs.dom.raw.SVGTextElement
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
case class SVGBoxLayout(centerX: Int,
                        offsetY: Int,
                        pieceSize: Coord,
                        bottomMargin: Int = 30,
                        labelHeight: Int = 87,
                        strokeWidth: Int = 5,
                        labelFontSize: Int = 80
                       ) {

  final val TOP_MARGIN: Int = 2

  final val numberSize: Coord = Coord(120, 120)

  final val PIECE_FACE_SIZE: Int = pieceSize.x * 20 / 21

  final val numberAdjustment: Coord = Coord(pieceSize.x * 7 / 8, pieceSize.x * 8 / 7 - numberSize.y)

  val boxWidth: Int = 7 * pieceSize.x

  val labelRect: Rect = Rect(Coord(centerX - boxWidth / 2 - strokeWidth, offsetY + TOP_MARGIN), boxWidth + 2 * strokeWidth, labelHeight)

  val boxRect: Rect = Rect(Coord(centerX - boxWidth / 2, offsetY + labelHeight + strokeWidth + TOP_MARGIN), boxWidth, pieceSize.y)

  val boxBorder: TypedTag[RectElement] = boxRect.toSVGRect(cls := "board-border")

  val boxLabel: TypedTag[RectElement] = labelRect.toSVGRect(cls := "box-label")

  val boxLabelText: TypedTag[SVGTextElement] = {
    val r = labelRect.copy(leftTop = Coord(labelRect.center.x, labelRect.top), width = 0)
    val fs = labelFontSize
    val fc = fs * 60 / 100 // center of the font
    r.toSVGText("UNUSED PIECES", false, Some((fs, fc, false)), cls := "indicator-text")
  }

  val extendedHeight: Int = TOP_MARGIN + labelHeight + boxRect.height + 2 * strokeWidth + bottomMargin

  def getRect(ptype: Ptype): Rect = {
    Rect(boxRect.leftTop + Coord(ptype.sortId * pieceSize.y), pieceSize.x, pieceSize.y)
  }
}
