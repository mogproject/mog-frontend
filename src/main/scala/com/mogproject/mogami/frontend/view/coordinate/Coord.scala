package com.mogproject.mogami.frontend.view.coordinate

import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.svg
import org.scalajs.dom.raw.{SVGCircleElement, SVGPolygonElement}

import scalatags.JsDom.{Modifier, TypedTag, svgAttrs, svgTags}
import scalatags.JsDom.svgTags.{circle, polygon}
import scalatags.JsDom.all._

/**
  * 2-D coordinates
  */
case class Coord(x: Int = 0, y: Int = 0) {
  def unary_- : Coord = Coord(-x, -y)

  def +(coord: Coord): Coord = Coord(this.x + coord.x, this.y + coord.y)

  def mkString(separator: String): String = x.toString + separator + y.toString

  override def toString: String = mkString(",")

  def toSVGCircle(radius: Int, modifier: Modifier*): TypedTag[SVGCircleElement] =
    circle(Seq(svgAttrs.cx := x, svgAttrs.cy := y, svgAttrs.r := radius) ++ modifier: _*)

  def toSVGPolygon(nodes: Seq[Coord], modifier: Modifier*): TypedTag[SVGPolygonElement] =
    polygon(Seq(svgAttrs.points := (this +: nodes).mkString(" ")) ++ modifier: _*)

  def toSVGText(text: String, rotated: Boolean, modifier: Modifier*): TypedTag[svg.Text] = {
    val as = Seq(svgAttrs.x := x, svgAttrs.y := y) ++ rotated.option(Coord.rotateAttribution) ++ modifier
    svgTags.text(text, as)
  }

}

object Coord {
  val rotateAttribution: Modifier = svgAttrs.transform := "rotate(180)"
}