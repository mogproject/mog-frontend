package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.core.{Piece, Player}
import com.mogproject.mogami.frontend.Rect
import com.mogproject.mogami.frontend.action.board.{BoardSetConfigAction, BoardSetPlayerNameAction, BoardSetStateAction}
import com.mogproject.mogami.frontend.model.board.{BoardModel, FlipDisabled, FlipEnabled}
import com.mogproject.mogami.frontend.sam.SAM
import com.mogproject.mogami.frontend.view.board._
import com.mogproject.mogami.{Square, State}
import com.mogproject.mogami.frontend.view.board.effect.PieceFlipAttribute
import com.mogproject.mogami.frontend.view.button.DropdownMenu
import com.mogproject.mogami.frontend.view.coordinate.Coord
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Div, Input}

import scala.util.Try
import scalatags.JsDom.all.{div, button => btn, _}

/**
  *
  */
class BoardTestView extends WebComponent {
  var area: SVGArea = SVGArea(SVGStandardLayout)

  def board = area.board

  def hand = area.hand

  def player = area.player

  // HTML parts
  val resizeInput: Input = input(tpe := "text", cls := "form-control", value := "400").render

  val squareInput: Input = input(tpe := "text", cls := "form-control", value := "7776").render

  val fromPieceInput: Input = input(tpe := "text", cls := "form-control", value := "+FU").render

  val toPieceInput: Input = input(tpe := "text", cls := "form-control", value := "+TO").render

  val blackNameInput: Input = input(tpe := "text", cls := "form-control", value := "Black").render

  val whiteNameInput: Input = input(tpe := "text", cls := "form-control", value := "White").render

  private[this] def getSquare: Option[Square] = getSquares.headOption

  private[this] def getSquareRect: Option[Rect] = getSquare.map(sq => board.getRect(sq))

  private[this] def getSquares: Seq[Square] = squareInput.value.grouped(2).flatMap(s => Try(Square.parseCsaString(s)).toOption).toSeq

  private[this] def getSquareRects: Seq[Rect] = getSquares.map(sq => board.getRect(sq))

  private[this] def getPieceFlipAttribute: Option[PieceFlipAttribute] = for {
    sq <- getSquare
    from <- Try(Piece.parseCsaString(fromPieceInput.value)).toOption
    to <- Try(Piece.parseCsaString(toPieceInput.value)).toOption
  } yield PieceFlipAttribute(sq, from, to, "jp1")


  val layoutChangeButton: DropdownMenu[SVGAreaLayout, BoardModel] = DropdownMenu(Vector(SVGStandardLayout, SVGCompactLayout, SVGWideLayout), Map(
    SVGStandardLayout -> Map(English -> "Standard"),
    SVGCompactLayout -> Map(English -> "Compact"),
    SVGWideLayout -> Map(English -> "Wide")
  ), layout => BoardSetConfigAction(_.copy(layout = layout))
  )

  val boardArea: Div = div().render

  def drawBoardArea(layout: SVGAreaLayout): Unit = {
    area = SVGArea(layout)
    WebComponent.removeAllChildElements(boardArea)
    boardArea.appendChild(area.element)
  }

  // Base element
  override def element: Element = div(
    cls := "container-fluid",
    div(cls := "row",
      div(cls := "col-md-6",
        boardArea
      ),
      div(cls := "col-md-6",
        div(
          cls := "container-fluid",

          h3("Feature Test"),
          div(cls := "row",
            div(cls := "col-md-3", label("Layout")),
            div(cls := "col-md-3", layoutChangeButton.element)
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Resize")),
            div(cls := "col-md-3", resizeInput),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => Try(resizeInput.value.toInt).foreach(sz => SAM.doAction(BoardSetConfigAction(_.copy(boardWidth = sz)))) }, "Resize"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Draw pieces")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetStateAction(State.HIRATE)) }, "HIRATE")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetStateAction(State.HIRATE.copy(board = State.HIRATE.board.mapValues(_.promoted)))) }, "HIRATE Promoted")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetStateAction(State.MATING_BLACK)) }, "Mate")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetStateAction()) }, "Clear"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Index")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetConfigAction(_.copy(recordLang = Japanese))) }, "Japanese")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetConfigAction(_.copy(recordLang = English))) }, "Western")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.clearIndexes() }, "Clear"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Flip")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetConfigAction(_.copy(flipType = FlipEnabled))) }, "Flip:true")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetConfigAction(_.copy(flipType = FlipDisabled))) }, "Flip:false"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Draw symbols")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => player.drawSymbols() }, "Draw"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Player names")),
            div(cls := "col-md-6", blackNameInput),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetPlayerNameAction(Map(Player.BLACK -> Some(blackNameInput.value)))) }, "Update"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", ""),
            div(cls := "col-md-6", whiteNameInput),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => SAM.doAction(BoardSetPlayerNameAction(Map(Player.WHITE -> Some(whiteNameInput.value)))) }, "Update"))
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
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.cursorEffector.start) }, "Start")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.cursorEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Flash")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.flashEffector.start) }, "Start"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Selected")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.selectedEffector.start) }, "Start")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.selectedEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Selecting")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.selectingEffector.start) }, "Start")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.selectingEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Move")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => getSquareRect.foreach(board.effect.moveEffector.start) }, "Start"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Last Move")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.lastMoveEffector.start(getSquareRects) }, "Start")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.lastMoveEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Legal Moves")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.legalMoveEffector.start(getSquares) }, "Start")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.legalMoveEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Piece Flip")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => getPieceFlipAttribute.foreach(board.effect.pieceFlipEffector.start) }, "Start")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.pieceFlipEffector.stop() }, "Stop"))
          ),
          div(cls := "row",
            div(cls := "col-md-3", label("Forward")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.forwardEffector.start(true) }, "Forward")),
            div(cls := "col-md-3", btn(cls := "btn btn-default", onclick := { () => board.effect.forwardEffector.start(false) }, "Backward"))
          )

        )
      )
    )
  ).render

}
