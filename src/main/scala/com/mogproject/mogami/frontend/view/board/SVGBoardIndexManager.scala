package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.core.Square
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.svgAttrs
import scalatags.JsDom.svgTags.text

/**
  *
  */
trait SVGBoardIndexManager {
  self: SVGBoard =>

  // local variables
  private[this] var currentStatus: Option[Boolean] = None

  private[this] var currentElements: Set[SVGElement] = Set.empty

  def drawIndexes(useJapanese: Boolean = true): Unit = if (!currentStatus.contains(useJapanese)) {
    clearIndexes()

    // filewise
    val xs = (1 to 9).map { n =>
      val base = getRect(Square(n, 1))
      val left = base.left + SVGBoard.PIECE_WIDTH / 2
      val top = SVGBoard.MARGIN_SIZE * 5 / 6
      text(svgAttrs.x := left, svgAttrs.y := top, cls := "board-index-text", n.toString)
    }

    // rankwise
    val ys = (1 to 9).map { n =>
      val base = getRect(Square(1, n))
      val left = SVGBoard.MARGIN_SIZE * 3 / 2 + SVGBoard.BOARD_WIDTH
      val top = base.top + (SVGBoard.PIECE_HEIGHT + 60) / 2 // font-size: 60
      text(svgAttrs.x := left, svgAttrs.y := top, cls := "board-index-text", ('a' + (n - 1)).toChar.toString)
    }

    val elems = (xs.toSet ++ ys.toSet).map(_.render)
    elems.foreach(svgElement.appendChild)

    currentStatus = Some(useJapanese)
    currentElements = elems.toSet
  }

  def clearIndexes(): Unit = {
    currentElements.foreach(WebComponent.removeElement)
    currentStatus = None
    currentElements = Set.empty
  }

  def refreshIndexes(): Unit = {
    val c = currentStatus
    currentStatus = None
    c.foreach(drawIndexes)
  }
}
