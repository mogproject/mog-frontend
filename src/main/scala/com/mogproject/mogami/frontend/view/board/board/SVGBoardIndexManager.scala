package com.mogproject.mogami.frontend.view.board.board

import com.mogproject.mogami.frontend.view.{SVGImageCache, WebComponent}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag

/**
  *
  */
trait SVGBoardIndexManager {
  self: SVGBoard =>

  private[this] val textClass = "board-index-text"

  // local variables
  private[this] var currentStatus: Option[Boolean] = None
  private[this] var currentFileElements: Seq[SVGElement] = Seq.empty
  private[this] var currentRankElements: Seq[SVGElement] = Seq.empty

  //
  // Utility
  //
  private[this] def generateFileIndex(index: Int): TypedTag[SVGElement] = {
    layout.getFileIndexRect(index, isFlipped).toSVGText(index.toString, false, true, None, cls := textClass)
  }

  private[this] def generateJapaneseRankIndex(index: Int)(implicit imageCache: SVGImageCache): TypedTag[SVGElement] = {
    layout.getRankIndexRect(index, isFlipped).toSVGImage(layout.getJapaneseRankIndexImagePath(index), rotated = false)
  }

  private[this] def generateWesternRankIndex(index: Int): TypedTag[SVGElement] = {
    layout.getRankIndexRect(index, isFlipped).toSVGText(('a' + (index - 1)).toChar.toString, false, true, None, cls := textClass)
  }


  //
  // Operation
  //
  def drawIndexes(useJapanese: Boolean = true)(implicit imageCache: SVGImageCache): Unit = if (!currentStatus.contains(useJapanese)) {
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

  def refreshIndexes()(implicit imageCache: SVGImageCache): Unit = {
    val c = currentStatus
    currentStatus = None
    c.foreach(drawIndexes)
  }
}
