package com.mogproject.mogami.frontend.view.board.canvas

import com.mogproject.mogami.Ptype
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.view.coordinate.Coord
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLImageElement

import scala.annotation.tailrec
import scala.collection.mutable
import scalatags.JsDom.all._

/**
  *
  */
trait CanvasRenderer {
  self: CanvasBoard =>

  private[this] val processingImages: mutable.Set[HTMLImageElement] = mutable.Set.empty[HTMLImageElement]

  def isImageReady: Boolean = processingImages.isEmpty

  def renderRect(rect: Rect, fillColor: Option[String], strokeColor: Option[String] = None, strokeWidth: Int = 0): Unit = {
    ctx.beginPath()
    ctx.rect(rect.left, rect.top, rect.width, rect.height)
    fillColor.foreach { fc => ctx.fillStyle = fc; ctx.fill() }
    strokeColor.foreach { sc => ctx.lineWidth = strokeWidth; ctx.strokeStyle = sc; ctx.stroke() }
  }

  def renderLine(rect: Rect, strokeColor: String, strokeWidth: Int): Unit = {
    ctx.beginPath()
    ctx.moveTo(rect.left, rect.top)
    ctx.lineTo(rect.right, rect.bottom)
    ctx.lineWidth = strokeWidth
    ctx.strokeStyle = strokeColor
    ctx.stroke()
  }

  def renderCircle(coord: Coord, radius: Int, fillColor: String): Unit = {
    ctx.beginPath()
    ctx.arc(coord.x, coord.y, radius, 0, math.Pi * 2, anticlockwise = true)
    ctx.fillStyle = fillColor
    ctx.fill()
  }

  def renderImage(rect: Rect, url: String): Unit = {
    val image = img(src := url, width := 1.px, height := 1.px).render
    processingImages.add(image)
    image.onload = { _: dom.Event =>
      ctx.drawImage(image, rect.left, rect.top, rect.width, rect.height)
      processingImages.remove(image)
    }
  }

  def renderPiece(rect: Rect, ptype: Ptype, flipped: Boolean): Unit = {
    renderImage(rect, config.pieceFace.getImagePath(ptype, flipped))
  }

  def rotateCanvas(): Unit = {
    ctx.translate(cv.width / 2, cv.height / 2)
    ctx.rotate(Math.PI)
    ctx.translate(-cv.width / 2, -cv.height / 2)
  }

  //
  // Text Render
  //
  def renderText(rect: Rect, text: String, fontSize: Int, fontFamily: String, textColor: String,
                 isBold: Boolean = false, alignCenter: Boolean = false, strokeColor: Option[String] = None, strokeWidth: Int = 0, trim: Boolean = true): Unit = {
    // workaround
    val adjustedFontSize = fontSize * 8 / 10
    val fontSpec = s"${isBold.fold("bold ", "")}${adjustedFontSize}pt ${fontFamily}"

    // trim text
    val trimmedText = trim.fold(binarySearchForTrim(text, fontSpec, rect.width), text)

    ctx.font = fontSpec
    ctx.fillStyle = textColor
    ctx.textBaseline = "alphabetic"

    // center text
    val x = rect.left + alignCenter.fold((rect.width - ctx.measureText(text).width) / 2, 0)
    val y = rect.bottom - (rect.height - adjustedFontSize) / 2

    // draw
    ctx.fillText(trimmedText, x, y) //rect.bottom - adjustedFontSize / 10)

    strokeColor.foreach { st =>
      ctx.lineWidth = strokeWidth
      ctx.strokeStyle = st
      ctx.strokeText(trimmedText, x, y)
    }
  }

  /**
    * Trim text if it is too long for the space
    *
    * @return
    */
  private[this] def binarySearchForTrim(text: String, fontSpec: String, areaWidth: Int): String = {
    def f(s: String): Boolean = {
      ctx.font = fontSpec
      ctx.measureText(s).width <= areaWidth
    }

    @tailrec
    def g(lo: Int, hi: Int): Int = if (lo < hi - 1) {
      val m = (lo + hi) / 2
      if (f(text.take(m))) g(m, hi) else g(lo, m)
    } else {
      lo
    }

    text.take(g(0, text.length + 1))
  }

}
