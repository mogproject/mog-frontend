package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.{Square, State}
import com.mogproject.mogami.frontend.view.board.SVGBoard
import org.scalajs.dom.Element
import org.scalajs.dom.html.Input

import scala.util.Try
import scalatags.JsDom.all.{div, _}

/**
  *
  */
class BoardTestView extends WebComponent {
  val board = new SVGBoard

  // HTML parts
  val resizeInput: Input = input(tpe := "text", cls := "form-control").render

  val squareInput: Input = input(tpe := "text", cls := "form-control").render

  private[this] def getSquare: Option[Square] = getSquares.headOption

  private[this] def getSquares: Set[Square] = squareInput.value.grouped(2).flatMap(s => Try(Square.parseCsaString(s)).toOption).toSet

  // Base element
  override def element: Element = div(
    board.element,
    div(
      cls := "container",

      h3("Feature Test"),
      div(cls := "row",
        div(cls := "col-md-3", label("Resize")),
        div(cls := "col-md-3", resizeInput),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => Try(resizeInput.value.toInt).foreach(board.resize) }, "Resize"))
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
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.drawIndexes(false) }, "Western"))
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Flip")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.setFlip(true) }, "Flip:true")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.setFlip(false) }, "Flip:false"))
      ),
      h3("Effect Test"),
      div(cls := "row",
        div(cls := "col-md-3", label("Square (CSA Format)")),
        div(cls := "col-md-3", squareInput)
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Cursor")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquare.foreach(board.effect.cursorEffector.start) }, "Start")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.cursorEffector.stop() }, "Stop"))
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Flash")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquare.foreach(board.effect.flashEffector.start) }, "Start"))
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Selected")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquare.foreach(board.effect.selectedEffector.start) }, "Start")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.selectedEffector.stop() }, "Stop"))
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Selecting")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquare.foreach(board.effect.selectingEffector.start) }, "Start")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.selectingEffector.stop() }, "Stop"))
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Move")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => getSquare.foreach(board.effect.moveEffector.start) }, "Start"))
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Last Move")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.lastMoveEffector.start(getSquares) }, "Start")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.lastMoveEffector.stop() }, "Stop"))
      ),
      div(cls := "row",
        div(cls := "col-md-3", label("Legal Moves")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.legalMoveEffector.start(getSquares) }, "Start")),
        div(cls := "col-md-3", button(cls := "btn btn-default", onclick := { () => board.effect.legalMoveEffector.stop() }, "Stop"))
      )

    )
  ).render

}
