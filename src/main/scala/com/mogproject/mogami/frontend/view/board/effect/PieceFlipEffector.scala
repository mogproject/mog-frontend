package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.{Piece, Square}
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.{animateTransform, g}
import scalatags.JsDom.svgAttrs


case class PieceFlipAttribute(square: Square, fromPiece: Piece, toPiece: Piece, pieceFace: String)

/**
  *
  */
case class PieceFlipEffector(svgBoard: SVGBoard) extends ForegroundEffectorLike[PieceFlipAttribute] {

  override def autoDestruct: Option[Int] = Some(600)

  private[this] def generateTransformElem(transformType: String, values: String) = {
    animateTransform(
      svgAttrs.attributeName := "transform",
      svgAttrs.`type` := transformType,
      svgAttrs.values := values,
      svgAttrs.dur := "0.6s",
      svgAttrs.begin := "indefinite",
      svgAttrs.fill := "freeze",
      svgAttrs.repeatCount := 1,
      svgAttrs.additive := "sum"
    )
  }

  private[this] def generateTransformElemSet(centerX: Int, scales: Seq[Double]) = {
    Seq(
      generateTransformElem("translate", scales.map(d => s"${round3((1.0 - d) * centerX)} 0").mkString(";")),
      generateTransformElem("scale", scales.map(d => s"${round3(d)} 1").mkString(";"))
    )
  }

  private[this] def round3(x: Double): Double = (x * 1000).round / 1000.0

  /** Create curve constants */
  private[this] lazy val (ls, rs) = {
    val d = 10
    val n = 5

    def f(sign: Int) = (0 to n).map { i =>
      val theta = math.Pi * i / 2 / n
      d * math.cos(theta) / (d + sign * math.sin(theta))
    } ++ Seq.fill(n)(0.0)

    (f(-1), f(1))
  }

  override def generateElements(x: PieceFlipAttribute): Seq[TypedTag[SVGElement]] = {
    val cx = svgBoard.getRect(x.square).center.x

    val x1 = svgBoard.generatePieceElement(x.square, x.fromPiece, x.pieceFace, cls := "left-half", generateTransformElemSet(cx, ls))
    val x2 = svgBoard.generatePieceElement(x.square, x.fromPiece, x.pieceFace, cls := "right-half", generateTransformElemSet(cx, rs))
    val y1 = svgBoard.generatePieceElement(x.square, x.toPiece, x.pieceFace, cls := "left-half", generateTransformElemSet(cx, rs.reverse))
    val y2 = svgBoard.generatePieceElement(x.square, x.toPiece, x.pieceFace, cls := "right-half", generateTransformElemSet(cx, ls.reverse))
    Seq(x1, x2, y1, y2)
  }

}
