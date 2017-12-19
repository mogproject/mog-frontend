package com.mogproject.mogami.frontend.view.nav

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipEnabled, FlipType}
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.Language
import com.mogproject.mogami.frontend.view.button.ButtonLike
import com.mogproject.mogami.frontend.view.observer.FlipTypeObserver
import org.scalajs.dom.html.{Button, Div}

import scalatags.JsDom.all._

/**
  *
  */
class FlipButton extends ButtonLike[FlipType, Button, Div] with FlipTypeObserver {
  override protected lazy val keys = Seq(FlipEnabled)

  override protected def generateInput(key: FlipType): Button = button(
    tpe := "button",
    cls := "btn btn-toggle thin-btn",
    "Flip ",
    span(cls := s"glyphicon glyphicon-retweet", aria.hidden := true)
  ).render

  override lazy val element: Div = div(cls := "input-group",
    inputs
  ).render

  override def updateLabel(lang: Language): Unit = {}

  override def updateValue(newValue: FlipType): Unit = {
    val elem = inputs.head
    elem.disabled = newValue == DoubleBoard
    updateLabelColor(elem, newValue == FlipEnabled)
  }

  override protected def invoke(key: FlipType): Unit = PlaygroundSAM.doAction(UpdateConfigurationAction(c => c.copy(flipType = !c.flipType)))

  override def handleUpdate(flipType: FlipType): Unit = updateValue(flipType)
}
