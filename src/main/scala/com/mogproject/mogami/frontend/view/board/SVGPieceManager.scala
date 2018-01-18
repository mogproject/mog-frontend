package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.model.{JapaneseOneCharFace, PieceFace}
import com.mogproject.mogami.frontend.view.{SVGImageCache, WebComponent}
import com.mogproject.mogami.frontend.view.board.effect.EffectorTarget
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{SVGImageElement, SVGTextElement}

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait SVGPieceManager[Key, Value] {
  self: EffectorTarget =>

  //
  // Local Variables
  //
  private[this] var currentPieces: Map[Key, Value] = Map.empty

  private[this] var currentPieceFace: PieceFace = JapaneseOneCharFace

  private[this] var pieceMap: Map[Key, (Element, Option[Element])] = Map.empty

  //
  // Utility
  //
  def getPieceRect(key: Key): Rect

  protected def getNumberRect(key: Key): Rect

  protected def isFlipped(key: Key, value: Value): Boolean

  protected def getPtype(key: Key, value: Value): Ptype

  protected def shouldDrawNumber(value: Value): Boolean

  protected def shouldDrawPiece(value: Value): Boolean

  protected def shouldRemoveSameKey: Boolean

  protected def resetPieceEffect(keepLastMove: Boolean = false): Unit

  def generatePieceElement(key: Key, value: Value, pieceFace: PieceFace, modifiers: Modifier*)(implicit imageCache: SVGImageCache): TypedTag[SVGImageElement] = {
    getPieceRect(key).toSVGImage(pieceFace.getImagePath(getPtype(key, value)), isFlipped(key, value), modifiers)
  }

  def generateNumberElement(key: Key, value: Value, modifiers: Modifier*): TypedTag[SVGTextElement] = {
    getNumberRect(key).toSVGText(value.toString, isFlipped(key, value), true, None, cls := "hand-number-text")
  }


  //
  // Operations
  //
  /**
    * Draw pieces
    *
    * @param pieces
    * @param pieceFace
    * @param keepLastMove
    */
  def drawPieces(pieces: Map[Key, Value], pieceFace: PieceFace = JapaneseOneCharFace, keepLastMove: Boolean = false)(implicit imageCache: SVGImageCache): Unit = {
    resetPieceEffect(keepLastMove)

    // get diffs
    val faceChanged = currentPieceFace != pieceFace

    val nextPieces = pieces.toSet.filter(kv => shouldDrawPiece(kv._2))

    val (xs, ys) = (currentPieces.toSet, nextPieces)

    val removedPieces = faceChanged.fold(xs, xs -- ys)
    val newPieces = faceChanged.fold(ys, ys -- xs)

    // clear old pieces
    removedPieces.foreach { case (k, _) =>
      if (faceChanged || shouldRemoveSameKey || !newPieces.exists(_._1 == k)) WebComponent.removeElement(pieceMap(k)._1)
      pieceMap(k)._2.foreach(WebComponent.removeElement)
    }

    // update local variables
    currentPieces = nextPieces.toMap
    currentPieceFace = pieceFace

    // render and materialize
    val newPieceMap = newPieces.map { case (k, v) =>
      val elem1 = if (!faceChanged && !shouldRemoveSameKey && removedPieces.exists(_._1 == k)) {
        pieceMap(k)._1
      } else {
        materializeForeground(generatePieceElement(k, v, pieceFace).render)
      }
      val elem2 = shouldDrawNumber(v).option(materializeForeground(generateNumberElement(k, v).render))
      k -> (elem1, elem2)
    }

    pieceMap = pieceMap -- removedPieces.map(_._1) ++ newPieceMap
  }

  private[this] def getElements(key: Key): Seq[Element] = {
    pieceMap.get(key) match {
      case Some((elem, Some(optElem))) => Seq(elem, optElem)
      case Some((elem, None)) => Seq(elem)
      case _ => Seq.empty
    }
  }

  def showPiece(key: Key): Unit = getElements(key).foreach(WebComponent.showElement)

  def hidePiece(key: Key): Unit = getElements(key).foreach(WebComponent.hideElement)

  /**
    * Refresh pieces
    */
  def refreshPieces()(implicit imageCache: SVGImageCache): Unit = {
    val cp = currentPieces
    clearPieces()
    drawPieces(cp, currentPieceFace, keepLastMove = true)
  }

  private[this] def clearPieces(): Unit = {
    for {(x1, x2) <- pieceMap.values} WebComponent.removeElements(Seq(x1) ++ x2)
    pieceMap = Map.empty
    currentPieces = Map.empty
  }
}
