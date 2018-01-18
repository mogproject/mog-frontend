package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.Ptype
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.SVGImageCache
import com.mogproject.mogami.frontend.view.board.SVGStandardLayout
import org.scalajs.dom.Element

import scalatags.JsDom.{Modifier, svgAttrs, svgTags}
import scalatags.JsDom.all._

/**
  * Piece face button
  */
case class PieceFaceButton(pieceFace: PieceFace, ptype: Ptype, rotated: Boolean, modifier: Modifier*)(implicit imageCache: SVGImageCache) extends WebComponent {

  final val pieceSize = SVGStandardLayout.mediumPiece

  override lazy val element: Element = button(
    tpe := "button",
    cls := "btn " + classButtonDefaultBlock,
    modifier,
    svgTags.svg(
      svgAttrs.width := 100.pct,
      svgAttrs.height := 100.pct,
      svgAttrs.viewBox := s"0 0 ${pieceSize}",
      Rect(Coord(0, 0), pieceSize.x, pieceSize.y).toSVGImage(pieceFace.getImagePath(ptype), rotated)
    )
  ).render
}
