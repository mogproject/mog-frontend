package com.mogproject.mogami.frontend.view.coordinate

import com.mogproject.mogami.frontend.view.SVGImageCache
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

  def +(coord: Coord): Rect = copy(leftTop = leftTop + coord)

  def -(coord: Coord): Rect = this.+(-coord)

  def toSVGRect(modifier: Modifier*): TypedTag[RectElement] =
    svgTags.rect(Seq(svgAttrs.x := left, svgAttrs.y := top, svgAttrs.width := width, svgAttrs.height := height) ++ modifier: _*)

  def toSVGLine(modifier: Modifier*): TypedTag[SVGLineElement] =
    svgTags.line(Seq(svgAttrs.x1 := left, svgAttrs.y1 := top, svgAttrs.x2 := right, svgAttrs.y2 := bottom) ++ modifier: _*)

  def toSVGImage(url: String, rotated: Boolean, modifier: Modifier*)(implicit imageCache: SVGImageCache): TypedTag[SVGImageElement] = {
    val r = rotated.fold(-this, this)
//    val as = Seq(svgAttrs.xLinkHref := url) ++ rotated.option(Coord.rotateAttribute)
    val as = Seq(svgAttrs.xLinkHref := imageCache.getURL(url)) ++ rotated.option(Coord.rotateAttribute)
    svgTags.image(Seq(svgAttrs.x := r.left, svgAttrs.y := r.top, svgAttrs.width := width, svgAttrs.height := height) ++ as ++ modifier: _*)
  }

  /**
    *
    * @param text
    * @param rotated
    * @param alignCenter true to be centered
    * @param fontSetting (font_size, vertical_center, is_top_to_bottom)
    * @param modifier
    * @return
    */
  def toSVGText(text: String, rotated: Boolean, alignCenter: Boolean, fontSetting: Option[(Int, Int, Boolean)], modifier: Modifier*): TypedTag[svg.Text] = {
    val c = getSVGTextStartPoint(rotated, alignCenter, fontSetting)
    val as = Seq(svgAttrs.x := c.x, svgAttrs.y := c.y) ++ rotated.option(Coord.rotateAttribute) ++ fontSetting.map(fontSize := _._1.px) ++ modifier
    svgTags.text(text, as)
  }

  protected[coordinate] def getSVGTextStartPoint(rotated: Boolean, alignCenter: Boolean, fontSetting: Option[(Int, Int, Boolean)]): Coord = {
    val topToBottom = fontSetting.exists(_._3)
    val baseX = (rotated, alignCenter, topToBottom) match {
      case (_, _, true) => center.x
      case (_, true, _) => center.x
      case (false, _, _) => left
      case (true, _, _) => right
    }
    val baseY = (rotated ^ topToBottom).fold(top, bottom)
    val offsetY = fontSetting.map(fs => height / 2 + fs._2 - fs._1).filter(_ => !topToBottom).getOrElse(0)

    rotated.fold(Coord(-baseX - 1, -baseY - 1 - offsetY), Coord(baseX, baseY - offsetY))
  }

  /**
    * for limiting the text length
    *
    * @param modifier modifier
    * @return svg typed tag
    */
  def toSVGWrapper(modifier: Modifier*): TypedTag[svg.SVG] = {
    svgTags.svg(svgAttrs.x := left, svgAttrs.y := top, svgAttrs.width := width, svgAttrs.height := height, modifier)
  }

  def toInnerRect(width: Int, height: Int) = Rect(Coord(left + (this.width - width) / 2, top + (this.height - height) / 2), width, height)

  def unary_- : Rect = copy(leftTop = -rightBottom)

  /**
    * Create a smaller rect.
    *
    * @param d px
    * @return
    */
  def shrink(d: Int): Rect = Rect(Coord(left + d, top + d), width - 2 * d + 1, height - 2 * d + 1)

  def resize(widthChange: Int, heightChange: Int, isOriginLeftTop: Boolean): Rect = {
    Rect(isOriginLeftTop.fold(leftTop, Coord(left - widthChange, top - heightChange)), width + widthChange, height + heightChange)
  }

  def rotate(pivot: Coord): Rect = copy(leftTop = rightBottom.rotate(pivot))
}
