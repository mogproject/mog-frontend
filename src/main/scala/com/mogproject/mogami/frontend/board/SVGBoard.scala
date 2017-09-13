package com.mogproject.mogami.frontend.board

import com.mogproject.mogami.core.{Piece, Square}
import com.mogproject.mogami.frontend.WebComponent
import org.scalajs.dom.Element
import org.scalajs.dom.svg.Line

import scalatags.JsDom.TypedTag
import scalatags.JsDom.implicits._
import scalatags.JsDom.svgTags._
import scalatags.JsDom.svgAttrs._

/**
  *
  */
case class SVGBoard(boardWidth: Int) extends WebComponent {

  val pieceWidth: Int = boardWidth * 107 / 1027
  val pieceHeight: Int = boardWidth * 113 / 1027
  val margin: Int = boardWidth * 32 / 1027

  private[this] val boardBoarder = rect(x := margin, y := margin, width := pieceWidth * 9, height := pieceHeight * 9, fill := "white", stroke := "black")

  private[this] val boardLines: Seq[TypedTag[Line]] = (1 to 8).flatMap { i =>
    val x = margin + pieceWidth * i
    val y = margin + pieceHeight * i
    Seq(
      line(x1 := margin, y1 := y, x2 := margin + pieceWidth * 9, y2 := y, stroke := "black"),
      line(x1 := x, y1 := margin, x2 := x, y2 := margin + pieceHeight * 9, stroke := "black")
    )
  }

  override lazy val element: Element = svg(
    width := boardWidth,
    height := pieceHeight * 9 + margin * 2
  )(
    (
      boardBoarder
        +: boardLines):
      _*).render

  def drawPieces(pieces: Map[Square, Piece]): Unit = {

  }
}
