package com.mogproject.mogami.frontend.view.coordinate

import org.scalajs.dom.raw.SVGCircleElement

import scalatags.JsDom.{Modifier, TypedTag, svgAttrs}
import scalatags.JsDom.svgTags.circle
import scalatags.JsDom.all._

/**
  * 2-D coordinates
  */
case class Coord(x: Int, y: Int) {
  def unary_- : Coord = Coord(-x, -y)

  def toSVGCircle(radius: Int, modifier: Modifier*): TypedTag[SVGCircleElement] =
    circle(Seq(svgAttrs.cx := x, svgAttrs.cy := y, svgAttrs.r := radius) ++ modifier: _*)

}
