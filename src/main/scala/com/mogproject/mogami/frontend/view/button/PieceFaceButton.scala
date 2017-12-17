package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.Ptype
import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.coordinate.Rect
import com.mogproject.mogami.frontend.view.piece.PieceFace
import org.scalajs.dom.Element

import scalatags.JsDom.{Modifier, svgAttrs, svgTags}
import scalatags.JsDom.all._

/**
  * Piece face button
  */
case class PieceFaceButton(pieceFace: PieceFace, pieceSize: Coord, ptype: Ptype, rotated: Boolean, modifier: Modifier*) extends WebComponent {
  override def element: Element = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    modifier,
    svgTags.svg(
      svgAttrs.width := 100.pct,
      svgAttrs.height := 100.pct,
      svgAttrs.viewBox := s"0 0 ${pieceSize}",
      Rect(Coord(0, 0), pieceSize.x, pieceSize.y).toSVGImage(pieceFace.getImagePath(ptype), rotated)
    )
  ).render
}
