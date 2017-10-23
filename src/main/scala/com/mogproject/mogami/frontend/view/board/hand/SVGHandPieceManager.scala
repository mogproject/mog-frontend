package com.mogproject.mogami.frontend.view.board.hand

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{SVGImageElement, SVGTextElement}

import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag

/**
  *
  */
// todo: refactor w/ SVGBoardPieceManager
trait SVGHandPieceManager {
  self: SVGHand =>

  // Local variables
  private[this] var currentPieces: HandType = Map.empty

  private[this] var currentPieceFace: String = "jp1"

  private[this] var pieceMap: Map[Hand, (Element, Option[Element])] = Map.empty

  //
  // Utility
  //
  private[this] def getImagePath(ptype: Ptype, pieceFace: String): String = s"assets/img/p/${pieceFace}/${ptype.toCsaString}.svg"

  private[this] def isPieceFlipped(piece: Piece): Boolean = piece.owner.isWhite ^ isFlipped

  def getPieceRect(piece: Piece): Rect =
    isPieceFlipped(piece).when[Rect](-_)(getRect(piece).toInnerRect(layout.PIECE_FACE_SIZE, layout.PIECE_FACE_SIZE))

  def generatePieceElement(piece: Piece, pieceFace: String, modifiers: Modifier*): TypedTag[SVGImageElement] = {
    getPieceRect(piece).toSVGImage(getImagePath(piece.ptype, pieceFace), isPieceFlipped(piece), modifiers)
  }

  def generateNumberElement(piece: Piece, number: Int, modifiers: Modifier*): TypedTag[SVGTextElement] = {
    val rc = getPieceRect(piece)
    val coord = rc.rightBottom + layout.numberAdjustment
    coord.toSVGText(number.toString, isPieceFlipped(piece), cls := "hand-number-text")
  }

  //
  // Operation
  //
  /**
    * Draw pieces in hand
    *
    * @param pieces
    * @param pieceFace
    * @param keepLastMove
    */
  def drawPieces(pieces: HandType, pieceFace: String = "jp1", keepLastMove: Boolean = false): Unit = {
    // unselect and stop/restart effects
    unselect()
    keepLastMove.fold(effect.lastMoveEffector.restart(), effect.lastMoveEffector.stop())

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
    val newPieceMap = newPieces.map { case (p, n) =>
      val elem1 = materializeBackground(generatePieceElement(p.toPiece, pieceFace).render)
      val elem2 = (n > 1).option(materializeForeground(generateNumberElement(p.toPiece, n).render))
      p -> (elem1, elem2)
    }

    pieceMap = pieceMap -- removedPieces.map(_._1) ++ newPieceMap
  }

  /**
    * Refresh pieces on board
    */
  def refreshPieces(): Unit = {
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
