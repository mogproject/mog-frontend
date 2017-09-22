package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.core.Piece
import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.{Square, State}
import com.mogproject.mogami.frontend.view.board.{SVGArea, SVGStandardLayout}
import com.mogproject.mogami.frontend.view.board.effect.PieceFlipAttribute
import com.mogproject.mogami.frontend.view.coordinate.Coord
import org.scalajs.dom.Element
import org.scalajs.dom.html.Input

import scala.util.Try
import scalatags.JsDom.all.{div, _}

/**
  *
  */
class BoardTestView extends WebComponent {
  val area = SVGArea(SVGStandardLayout)

  def board = area.board

  // HTML parts
  val resizeInput: Input = input(tpe := "text", cls := "form-control", value := "400").render

  val squareInput: Input = input(tpe := "text", cls := "form-control", value := "7776").render

  val fromPieceInput: Input = input(tpe := "text", cls := "form-control", value := "+FU").render

  val toPieceInput: Input = input(tpe := "text", cls := "form-control", value := "+TO").render

  private[this] def getSquare: Option[Square] = getSquares.headOption

  private[this] def getSquareRect: Option[Rect] = getSquare.map(sq => board.getRect(sq))

  private[this] def getSquares: Seq[Square] = squareInput.value.grouped(2).flatMap(s => Try(Square.parseCsaString(s)).toOption).toSeq

  private[this] def getSquareRects: Seq[Rect] = getSquares.map(sq => board.getRect(sq))

  private[this] def getPieceFlipAttribute: Option[PieceFlipAttribute] = for {
    sq <- getSquare
    from <- Try(Piece.parseCsaString(fromPieceInput.value)).toOption
    to <- Try(Piece.parseCsaString(toPieceInput.value)).toOption
  } yield PieceFlipAttribute(sq, from, to, "jp1")

  // Base element
  override def element: Element = div(
    cls := "container-fluid",
    div(cls := "row",
      div(cls := "col-md-6",
        area.element
      ),
      div(cls := "col-md-6",
        div(
          cls := "container-fluid",

          h3("Feature Test"),
          div(cls := "row",
            div(cls := "col-md-3", label("Resize")),
            div(cls := "col-md-3", resizeInput),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => Try(resizeInput.value.toInt).foreach(area.resize) }, "Resize"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Draw pieces")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.drawPieces(State.HIRATE.board) }, "HIRATE")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.drawPieces(State.HIRATE.board.mapValues(_.promoted)) }, "HIRATE Promoted")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.drawPieces(State.MATING_BLACK.board) }, "Mate")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.drawPieces(Map.empty) }, "Clear"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Index")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.drawIndexes(true) }, "Japanese")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.drawIndexes(false) }, "Western")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.clearIndexes() }, "Clear"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Flip")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => area.setFlip(true) }, "Flip:true")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => area.setFlip(false) }, "Flip:false"))
          ),
          h3("Effect Test"),
          div(cls := "row",
            div(cls := "col-md-3", label("Square (CSA Format)")),
            div(cls := "col-md-3", squareInput)
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("From Piece (CSA Format)")),
            div(cls := "col-md-3", fromPieceInput)
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("To Piece (CSA Format)")),
            div(cls := "col-md-3", toPieceInput)
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Cursor")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.cursorEffector.start) }, "Start")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.cursorEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Flash")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.flashEffector.start) }, "Start"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Selected")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.selectedEffector.start) }, "Start")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.selectedEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Selecting")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.selectingEffector.start) }, "Start")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.selectingEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Move")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.moveEffector.start) }, "Start"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Last Move")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.lastMoveEffector.start(getSquareRects) }, "Start")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.lastMoveEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Legal Moves")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.legalMoveEffector.start(getSquares) }, "Start")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.legalMoveEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Piece Flip")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getPieceFlipAttribute.foreach(board.effect.pieceFlipEffector.start) }, "Start")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.pieceFlipEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Forward")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.forwardEffector.start(true) }, "Forward")),
            div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.forwardEffector.start(false) }, "Backward"))
          )

        )
      )
    )
  ).render

}
