package com.mogproject.mogami.frontend.view.board.board

import com.mogproject.mogami.Square
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.text
import scalatags.JsDom.{TypedTag, svgAttrs}

/**
  *
  */
trait SVGBoardIndexManager {
  self: SVGBoard =>
  
  import layout._

  private[this] val textClass = "board-index-text"

  // local variables
  private[this] var currentStatus: Option[Boolean] = None
  private[this] var currentFileElements: Seq[SVGElement] = Seq.empty
  private[this] var currentRankElements: Seq[SVGElement] = Seq.empty

  //
  // Utility
  //
  private[this] def getImagePath(index: Int): String = s"assets/img/n/N${index}.svg"

  private[this] def generateFileIndex(index: Int): TypedTag[SVGElement] = {
    val base = getRect(Square(index, 1))
    val left = base.left + pieceWidth / 2
    val top = offset.y + MARGIN_SIZE * 5 / 6
    text(svgAttrs.x := left, svgAttrs.y := top, cls := textClass, index.toString)
  }

  private[this] def generateJapaneseRankIndex(index: Int): TypedTag[SVGElement] = {
    val base = getRect(Square(1, index))
    val r = Rect(Coord(offset.x + MARGIN_SIZE + BOARD_WIDTH, base.top), MARGIN_SIZE, base.height)
    r.toInnerRect(INDEX_SIZE, INDEX_SIZE).toSVGImage(getImagePath(index), rotated = false)
  }

  private[this] def generateWesternRankIndex(index: Int): TypedTag[SVGElement] = {
    val base = getRect(Square(1, index))
    val left = offset.x + MARGIN_SIZE * 3 / 2 + BOARD_WIDTH
    val top = base.top + (pieceHeight + INDEX_SIZE) / 2
    text(svgAttrs.x := left, svgAttrs.y := top, cls := textClass, ('a' + (index - 1)).toChar.toString)
  }


  //
  // Operation
  //
  def drawIndexes(useJapanese: Boolean = true): Unit = if (!currentStatus.contains(useJapanese)) {
    clearIndexes()

    // filewise
    if (currentStatus.isEmpty) {
      val fileElems = (1 to 9).map(generateFileIndex(_).render)
      currentFileElements = materializeBackground(fileElems)
    }

    // rankwise
    val rankElems = (1 to 9).map(useJapanese.fold(generateJapaneseRankIndex(_), generateWesternRankIndex(_))).map(_.render)
    currentRankElements = materializeBackground(rankElems)

    // update local variables
    currentStatus = Some(useJapanese)
  }

  def clearIndexes(): Unit = {
    WebComponent.removeElements(currentFileElements)
    WebComponent.removeElements(currentRankElements)
    currentStatus = None
    currentFileElements = Seq.empty
    currentRankElements = Seq.empty
  }

  def refreshIndexes(): Unit = {
    val c = currentStatus
    currentStatus = None
    c.foreach(drawIndexes)
  }
}
