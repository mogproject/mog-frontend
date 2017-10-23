package com.mogproject.mogami.frontend.view.coordinate

import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.{SVGImageElement, SVGLineElement}
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.all._
import scalatags.JsDom.svgTags._
import scalatags.JsDom.svgAttrs
import scalatags.JsDom.Modifier
import scalatags.JsDom.TypedTag

/**
  * Rectangle
  */
case class Rect(leftTop: Coord, width: Int, height: Int) {
  def left: Int = leftTop.x

  def right: Int = left + width

  def top: Int = leftTop.y

  def bottom: Int = top + height

  def leftBottom: Coord = Coord(left, bottom)

  def rightTop: Coord = Coord(right, top)

  def rightBottom: Coord = Coord(right, bottom)

  def center: Coord = Coord(left + width / 2, top + height / 2)

  def toSVGRect(modifier: Modifier*): TypedTag[RectElement] =
    rect(Seq(svgAttrs.x := left, svgAttrs.y := top, svgAttrs.width := width, svgAttrs.height := height) ++ modifier: _*)

  def toSVGLine(modifier: Modifier*): TypedTag[SVGLineElement] =
    line(Seq(svgAttrs.x1 := left, svgAttrs.y1 := top, svgAttrs.x2 := right, svgAttrs.y2 := bottom) ++ modifier: _*)

  def toSVGImage(url: String, rotated: Boolean, modifier: Modifier*): TypedTag[SVGImageElement] = {
    val as = Seq(svgAttrs.xLinkHref := url) ++ rotated.option(svgAttrs.transform := "rotate(180)")
    image(Seq(svgAttrs.x := left, svgAttrs.y := top, svgAttrs.width := width, svgAttrs.height := height) ++ as ++ modifier: _*)
  }

  def toInnerRect(width: Int, height: Int) = Rect(Coord(left + (this.width - width) / 2, top + (this.height - height) / 2), width, height)

  def unary_- : Rect = copy(leftTop = -rightBottom)

  /**
    * Create a smaller rect.
    * @param d px
    * @return
    */
  def shrink(d: Int): Rect = Rect(Coord(left + d, top + d), width - 2 * d + 1, height - 2 * d + 1)
}
