package com.mogproject.mogami.frontend.view.analyze

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.analyze.AnalyzeCheckmateAction
import com.mogproject.mogami.frontend.action.board.AddMovesAction
import com.mogproject.mogami.frontend.action.dialog.MenuDialogAction
import com.mogproject.mogami.frontend.view.button.{HoverCommandButton, MultiLingualLabel}
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

  private[this] lazy val analyzeButton = HoverCommandButton(
    MultiLingualLabel("Analyze", "解析"),
    () => clickAction(),
    Map(English -> "Analyze this position for checkmate", Japanese -> "この局面の詰み手順を解析")
  )

  private[this] lazy val solverMessage: Div = div(
    cls := "col-xs-8 col-sm-9 text-muted",
    marginTop := 6
  ).render

  private[this] def generateAddMovesButton(moves: Seq[Move]) = HoverCommandButton(
    MultiLingualLabel("Add Moves to Game", "手順を棋譜に追記"),
    { () =>
      doAction(AddMovesAction(moves))
      displayCheckmateMessage("Moves are added.")
      doAction(MenuDialogAction(false), 1000) // close menu modal after 1 sec (mobile)
    },
    Map(English -> "Add this solution to the current game record", Japanese -> "この手順を現在の棋譜に追記")
  )

  private[this] def validateTimeout(): Int = {
    val n = Try(timeoutInput.value.toInt).getOrElse(DEFAULT_TIMEOUT)
    timeoutInput.value = n.toString
    n
  }

  override lazy val element: Div = div(
    div(
      cls := "row",
      div(cls := "col-xs-6 col-sm-8 text-right",
        "Timeout"
      ),
      div(cls := "col-xs-6 col-sm-4",
        marginTop := (-8).px,
        div(cls := "input-group",
          timeoutInput,
          span(cls := "input-group-addon", padding := 6.px, "sec")
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
    displayCheckmateMessage("Analyzing...")
    dom.window.setTimeout(() => doAction(AnalyzeCheckmateAction(validateTimeout())), 100)
  }

  def displayResult(result: Option[Seq[Move]], messageLang: Language, recordLang: Language): Unit = {
    (result, messageLang) match {
      case (None, Japanese) =>
        displayCheckmateMessage("制限時間内に解析できませんでした。")
      case (None, _) =>
        displayCheckmateMessage("This position is too difficult to solve.")
      case (Some(Nil), Japanese) =>
        displayCheckmateMessage("詰みはありません。")
      case (Some(Nil), _) =>
        displayCheckmateMessage("No checkmates.")
      case (Some(moves), _) =>
        val s = moves.map(m => m.player.toSymbolString() + (recordLang match {
          case Japanese => m.toJapaneseNotationString
          case English => m.toWesternNotationString
        }))
        displayCheckmateMessage(s"Found a checkmate:\n${s.mkString(" ")}")
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
