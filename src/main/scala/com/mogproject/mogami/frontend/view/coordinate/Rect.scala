package com.mogproject.mogami.frontend.view.coordinate

import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.{SVGImageElement, SVGLineElement}
import org.scalajs.dom.svg
import org.scalajs.dom.svg.RectElement

import scalatags.JsDom.all._

import scalatags.JsDom.svgTags
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
    svgTags.rect(Seq(svgAttrs.x := left, svgAttrs.y := top, svgAttrs.width := width, svgAttrs.height := height) ++ modifier: _*)

  def toSVGLine(modifier: Modifier*): TypedTag[SVGLineElement] =
    svgTags.line(Seq(svgAttrs.x1 := left, svgAttrs.y1 := top, svgAttrs.x2 := right, svgAttrs.y2 := bottom) ++ modifier: _*)

  def toSVGImage(url: String, rotated: Boolean, modifier: Modifier*): TypedTag[SVGImageElement] = {
    val r = rotated.fold(-this, this)
    val as = Seq(svgAttrs.xLinkHref := url) ++ rotated.option(Coord.rotateAttribution)
    svgTags.image(Seq(svgAttrs.x := r.left, svgAttrs.y := r.top, svgAttrs.width := width, svgAttrs.height := height) ++ as ++ modifier: _*)
  }

  def toSVGText(text: String, rotated: Boolean, modifier: Modifier*): TypedTag[svg.Text] = {
    val c = rotated.fold(-rightTop, leftBottom)
    val as = Seq(svgAttrs.x := c.x, svgAttrs.y := c.y) ++ rotated.option(Coord.rotateAttribution) ++ modifier
    svgTags.text(text, as)
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
