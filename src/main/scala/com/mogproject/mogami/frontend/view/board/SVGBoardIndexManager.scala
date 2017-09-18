package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.{TypedTag, svgAttrs}
import scalatags.JsDom.svgTags.text

/**
  *
  */
trait SVGBoardIndexManager {
  self: SVGBoard =>

  import SVGBoard._

  // local variables
  private[this] var currentStatus: Option[Boolean] = None

  private[this] var currentElements: Set[SVGElement] = Set.empty

  //
  // Utility
  //
  private[this] def getImagePath(index: Int): String = s"assets/img/n/N${index}.svg"

  private[this] def generateJapaneseRankIndex(index: Int): TypedTag[SVGElement] = {
    val base = getRect(Square(1, index))
    val r = Rect(Coord(MARGIN_SIZE + BOARD_WIDTH, base.top), MARGIN_SIZE, base.height)
    r.toInnerRect(INDEX_SIZE, INDEX_SIZE).toSVGImage(svgAttrs.xLinkHref := getImagePath(index))
  }

  private[this] def generateWesternRankIndex(index: Int): TypedTag[SVGElement] = {
    val base = getRect(Square(1, index))
    val left = SVGBoard.MARGIN_SIZE * 3 / 2 + SVGBoard.BOARD_WIDTH
    val top = base.top + (SVGBoard.PIECE_HEIGHT + SVGBoard.INDEX_SIZE) / 2
    text(svgAttrs.x := left, svgAttrs.y := top, cls := "board-index-text", ('a' + (index - 1)).toChar.toString)
  }


  //
  // Operation
  //
  def drawIndexes(useJapanese: Boolean = true): Unit = if (!currentStatus.contains(useJapanese)) {
    clearIndexes()

    // filewise
    //todo: consider diff when JP <-> Western
    val xs = (1 to 9).map { n =>
      val base = getRect(Square(n, 1))
      val left = base.left + SVGBoard.PIECE_WIDTH / 2
      val top = SVGBoard.MARGIN_SIZE * 5 / 6
      text(svgAttrs.x := left, svgAttrs.y := top, cls := "board-index-text", n.toString)
    }

    // rankwise
    val ys = (1 to 9).map(useJapanese.fold(generateJapaneseRankIndex(_), generateWesternRankIndex(_)))

    val elems = (xs.toSet ++ ys.toSet).map(_.render)
    elems.foreach(svgElement.appendChild)

    currentStatus = Some(useJapanese)
    currentElements = elems
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
