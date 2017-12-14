package com.mogproject.mogami.frontend.view.board.box

import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.{Piece, Ptype}
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{SVGImageElement, SVGTextElement}

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait SVGBoxPieceManager {
  self: SVGBox =>

  //
  // Local Variables
  //

  private[this] var currentPieces: Map[Ptype, Int] = Map.empty

  private[this] var currentPieceFace: String = "jp1"

  private[this] var pieceMap: Map[Ptype, (Element, Option[Element])] = Map.empty

  //
  // Utility
  //
  def getRect(ptype: Ptype): Rect = layout.getRect(ptype)

  def getNumberRect(ptype: Ptype): Rect = {
    Rect(getRect(ptype).leftTop + layout.numberAdjustment, layout.numberSize.x, layout.numberSize.y)
  }

  private[this] def getImagePath(ptype: Ptype, pieceFace: String): String = s"assets/img/p/${pieceFace}/${ptype.toCsaString}.svg"

  def getPieceRect(ptype: Ptype): Rect = getRect(ptype).toInnerRect(layout.PIECE_FACE_SIZE, layout.PIECE_FACE_SIZE)

  def generatePieceElement(ptype: Ptype, pieceFace: String, modifiers: Modifier*): TypedTag[SVGImageElement] = {
    getPieceRect(ptype).toSVGImage(getImagePath(ptype, pieceFace), false, modifiers)
  }

  def generateNumberElement(ptype: Ptype, number: Int, modifiers: Modifier*): TypedTag[SVGTextElement] = {
    getNumberRect(ptype).toSVGText(number.toString, false, None, cls := "hand-number-text")
  }

  //
  // Operation
  //
  /**
    * Draw pieces in the piece box
    *
    * @param pieces
    * @param pieceFace
    */
  def drawPieces(pieces: Map[Ptype, Int], pieceFace: String = "jp1"): Unit = {
    // unselect and stop/restart effects
    unselect()

    val nextPieces = pieces.toSet.filter(_._2 > 0)

    // get diffs
    val (xs, ys) = (currentPieces.toSet, nextPieces)

    val removedPieces = xs -- ys
    val newPieces = ys -- xs

    // clear old pieces
    removedPieces.foreach { case (h, _) =>
      if (!newPieces.exists(_._1 == h)) WebComponent.removeElement(pieceMap(h)._1)
      pieceMap(h)._2.foreach(WebComponent.removeElement)
    }

    // update local variables
    currentPieces = nextPieces.toMap
    currentPieceFace = pieceFace

    // render and materialize
    val newPieceMap = newPieces.map { case (pt, n) =>
      val elem1 = if (removedPieces.exists(_._1 == pt)) pieceMap(pt)._1 else materializeBackground(generatePieceElement(pt, pieceFace).render)
      val elem2 = (n > 1).option(materializeForeground(generateNumberElement(pt, n).render))
      pt -> (elem1, elem2)
    }

    pieceMap = pieceMap -- removedPieces.map(_._1) ++ newPieceMap
  }

  /**
    * Refresh pieces on board
    */
  def refreshPieces(): Unit = {
    val cp = currentPieces
    clearPieces()
    drawPieces(cp, currentPieceFace)
  }

  private[this] def clearPieces(): Unit = {
    for {(x1, x2) <- pieceMap.values} WebComponent.removeElements(Seq(x1) ++ x2)

    pieceMap = Map.empty
    currentPieces = Map.empty
  }
}
