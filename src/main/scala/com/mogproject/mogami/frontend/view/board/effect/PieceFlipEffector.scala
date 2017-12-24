package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.model.PieceFace
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{Piece, Square}
import com.mogproject.mogami.frontend.view.board.board.SVGBoard
import org.scalajs.dom.raw.SVGElement

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import scalatags.JsDom.svgTags.animateTransform
import scalatags.JsDom.svgAttrs


case class PieceFlipAttribute(square: Square, fromPiece: Piece, toPiece: Piece, pieceFace: PieceFace)

/**
  * Piece flip effect
  */
case class PieceFlipEffector(target: SVGBoard) extends BackgroundEffectorLike[PieceFlipAttribute, SVGBoard] {

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

    def f(sign: Int) = Seq(1.0) ++ (0 to n).map { i =>
      val theta = math.Pi * i / 2 / n
      d * math.cos(theta) / (d + sign * math.sin(theta))
    } ++ Seq.fill(n)(0.0)

    (f(-1), f(1))
  }

  override def generateElements(x: PieceFlipAttribute): Seq[TypedTag[SVGElement]] = {
    for {
      isFrom <- Seq(true, false)
      piece = isFrom.fold(x.fromPiece, x.toPiece)
      isFlipped = target.isFlipped ^ piece.owner.isWhite
      centerX = target.getPieceRect(x.square).center.x * isFlipped.fold(-1, 1) + isFlipped.fold(1, 0)
      isLeft <- Seq(true, false)
      className = isLeft.fold("left-half", "right-half")
      scales = isFrom.fold(isLeft.fold(ls, rs), isLeft.fold(rs.reverse, ls.reverse))
    } yield {
      target.generatePieceElement(x.square, piece, x.pieceFace, cls := className, generateTransformElemSet(centerX, scales))
    }
  }

}
