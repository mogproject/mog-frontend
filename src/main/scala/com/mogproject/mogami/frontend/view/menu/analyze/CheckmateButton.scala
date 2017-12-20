package com.mogproject.mogami.frontend.view.menu.analyze

import com.mogproject.mogami.frontend.action.analyze.AnalyzeCheckmateAction
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
import com.mogproject.mogami.frontend.view.button.SingleButton
import org.scalajs.dom.html.{Button, Div, Input}

import scala.util.Try
import scalatags.JsDom.all._

/**
  *
  */
class CheckmateButton extends WebComponent {

  private[this] final val DEFAULT_TIMEOUT = 5

  private[this] val timeoutInput: Input = input(
    tpe := "text",
    cls := "form-control",
    placeholder := DEFAULT_TIMEOUT,
    value := DEFAULT_TIMEOUT
  ).render

  private[this] lazy val analyzeButton: SingleButton = SingleButton(
    Map(English -> span("Analyze").render),
    clickAction = Some({ () => disableAnalyzeButton(); PlaygroundSAM.doAction(AnalyzeCheckmateAction(validateTimeout(), started = false)) }),
    isBlockButton = true
  )

  private[this] lazy val solverMessage: Div = div(
    cls := "col-xs-8 col-sm-9 text-muted",
    marginTop := 6
  ).render

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
        marginTop := -8,
        div(cls := "input-group",
          timeoutInput,
          span(cls := "input-group-addon", padding := 6, "sec")
        )
      )
    ),
    div(cls := "row",
      div(cls := "col-xs-4 col-sm-3",
        analyzeButton.element
      ),
      solverMessage
    )
  ).render


  //
  // messaging
  //
  def displayCheckmateMessage(message: String): Unit = {
    solverMessage.innerHTML = message.replace("\n", br().toString())
  }

  def clearCheckmateMessage(): Unit = {
    solverMessage.innerHTML = ""
  }

  def disableAnalyzeButton(): Unit = analyzeButton.disableElement()

  def enableAnalyzeButton(): Unit = analyzeButton.enableElement()
}
