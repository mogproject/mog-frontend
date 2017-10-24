package com.mogproject.mogami.frontend.view.coordinate

import org.scalajs.dom.raw.{SVGCircleElement, SVGPolygonElement}

import scalatags.JsDom.{Modifier, TypedTag, svgAttrs}
import scalatags.JsDom.svgTags.{circle, polygon}
import scalatags.JsDom.all._

/**
  * 2-D coordinates
  */
case class Coord(x: Int = 0, y: Int = 0) {
  def unary_- : Coord = Coord(-x, -y)

  def +(coord: Coord): Coord = Coord(this.x + coord.x, this.y + coord.y)

  def -(coord: Coord): Coord = this.+(-coord)

  def mkString(separator: String): String = x.toString + separator + y.toString

  override def toString: String = mkString(",")

  def toSVGCircle(radius: Int, modifier: Modifier*): TypedTag[SVGCircleElement] =
    circle(Seq(svgAttrs.cx := x, svgAttrs.cy := y, svgAttrs.r := radius) ++ modifier: _*)

  def toSVGPolygon(nodes: Seq[Coord], modifier: Modifier*): TypedTag[SVGPolygonElement] =
    polygon(Seq(svgAttrs.points := (this +: nodes).mkString(" ")) ++ modifier: _*)

}

object Coord {
  val rotateAttribution: Modifier = svgAttrs.transform := "rotate(180)"
}