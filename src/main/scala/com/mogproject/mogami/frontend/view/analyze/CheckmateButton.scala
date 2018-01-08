package com.mogproject.mogami.frontend.view.analyze

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.analyze.AnalyzeCheckmateAction
import com.mogproject.mogami.frontend.action.board.AddMovesAction
import com.mogproject.mogami.frontend.action.dialog.MenuDialogAction
import com.mogproject.mogami.frontend.view.button.CommandButton
import org.scalajs.dom
import org.scalajs.dom.html.{Div, Input}

import scala.util.Try
import scalatags.JsDom.all._

/**
  *
  */
class CheckmateButton(isMobile: Boolean) extends WebComponent {

  private[this] final val DEFAULT_TIMEOUT = 5

  private[this] val timeoutInput: Input = input(
    tpe := "text",
    cls := "form-control",
    placeholder := DEFAULT_TIMEOUT,
    value := DEFAULT_TIMEOUT
  ).render

  private[this] lazy val analyzeButton = CommandButton(classButtonDefaultBlock, onclick := { () => clickAction() })
    .withDynamicTextContent(_.ANALYZE)
    .withDynamicHoverTooltip(_.ANALYZE_CHECKMATE_TOOLTIP)

  private[this] lazy val solverMessage: Div = div(
    cls := "col-xs-8 col-sm-9 text-muted",
    marginTop := 6
  ).render

  private[this] def generateAddMovesButton(moves: Seq[Move]) = CommandButton(
    classButtonDefaultBlock,
    onclick := { () =>
      doAction(AddMovesAction(moves))
      displayCheckmateMessage(Messages.get.CHECKMATE_MOVES_ADDED)
      doAction(MenuDialogAction(false), 1000) // close menu modal after 1 sec (mobile)
    }
  )
    .withDynamicTextContent(_.ADD_CHECKMATE_MOVES)
    .withDynamicHoverTooltip(_.ADD_CHECKMATE_MOVES_TOOLTIP)

  private[this] def validateTimeout(): Int = {
    val n = Try(timeoutInput.value.toInt).getOrElse(DEFAULT_TIMEOUT)
    timeoutInput.value = n.toString
    n
  }

  override lazy val element: Div = div(
    div(
      cls := "row",
      WebComponent.dynamicDiv(_.TIMEOUT, cls := "col-xs-6 col-sm-8 text-right").element,
      div(cls := "col-xs-6 col-sm-4",
        marginTop := (-8).px,
        div(cls := "input-group",
          timeoutInput,
          WebComponent.dynamicSpan(_.SEC, cls := "input-group-addon", padding := 6.px).element
        )
      )
    ),
    div(cls := "row",
      div(cls := "col-xs-5 col-sm-3",
        analyzeButton.element
      ),
      solverMessage
    )
  ).render


  //
  // messaging
  //
  private[this] def clickAction(): Unit = {
    disableAnalyzeButton()
    displayCheckmateMessage(Messages.get.ANALYZING + "...")
    dom.window.setTimeout(() => doAction(AnalyzeCheckmateAction(validateTimeout())), 100)
  }

  def displayResult(result: Option[Seq[Move]], recordLang: Language): Unit = {
    result match {
      case None => displayCheckmateMessage(Messages.get.CHECKMATE_ANALYZE_TIMEOUT)
      case Some(Nil) => displayCheckmateMessage(Messages.get.NO_CHECKMATES)
      case Some(moves) =>
        val s = moves.map(m => m.player.toSymbolString() + (recordLang match {
          case Japanese => m.toJapaneseNotationString
          case English => m.toWesternNotationString
        }))
        displayCheckmateMessage(Messages.get.CHECKMATE_FOUND + ":\n" + s.mkString(" "))
        solverMessage.appendChild(generateAddMovesButton(moves).element)
    }
    dom.window.setTimeout(() => enableAnalyzeButton(), 500)
  }

  private[this] def displayCheckmateMessage(message: String): Unit = {
    solverMessage.innerHTML = message.replace("\n", br().toString())
  }

  private[this] def clearCheckmateMessage(): Unit = {
    solverMessage.innerHTML = ""
  }

  private[this] def disableAnalyzeButton(): Unit = analyzeButton.disableElement()

  private[this] def enableAnalyzeButton(): Unit = analyzeButton.enableElement()
}
