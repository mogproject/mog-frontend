package com.mogproject.mogami.frontend.view.nav

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, Language}
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipEnabled, FlipType}
import com.mogproject.mogami.frontend.view.button.ButtonLike
import org.scalajs.dom.html.{Button, Div}

import scalatags.JsDom.all._

/**
  *
  */
class FlipButton extends ButtonLike[FlipType, Button, Div] with SAMObserver[BasePlaygroundModel] {
  override protected lazy val keys = Seq(FlipEnabled)

  override protected def generateInput(key: FlipType): Button = button(
    tpe := "button",
    cls := "btn btn-toggle thin-btn",
    DynamicComponent(_.FLIP).element,
    " ",
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

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_FLIP_TYPE

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = updateValue(model.config.flipType)
}
