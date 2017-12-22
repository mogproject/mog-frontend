package com.mogproject.mogami.frontend.view.board.canvas

import com.mogproject.mogami.Ptype
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
    val image = img(src := url).render
    processingImages.add(image)
    image.onload = { _: dom.Event =>
      ctx.drawImage(img(src := url).render, rect.left, rect.top, rect.width, rect.height)
      processingImages.remove(image)
    }
  }

  def renderPiece(rect: Rect, ptype: Ptype): Unit = {
    renderImage(rect, config.pieceFace.getImagePath(ptype))
  }

  def rotateCanvas(): Unit = {
    ctx.translate(cv.width / 2, cv.height / 2)
    ctx.rotate(Math.PI)
    ctx.translate(-cv.width / 2, -cv.height / 2)
  }

  //
  // Text Render
  //
  def renderText(rect: Rect, text: String, fontSize: Int, fontFamily: String, textColor: String): Unit = {
    // workaround
    val adjustedFontSize = fontSize * 8 / 10
    val fontSpec = s"${adjustedFontSize}pt ${fontFamily}"

    // trim text
    val trimmedText = binarySearchForTrim(text, fontSpec, rect.width)

    ctx.font = fontSpec
    ctx.fillStyle = textColor
    ctx.textBaseline = "bottom"

    ctx.fillText(trimmedText, rect.left, rect.bottom - adjustedFontSize / 10)
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
