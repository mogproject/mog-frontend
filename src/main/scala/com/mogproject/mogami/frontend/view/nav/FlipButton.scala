package com.mogproject.mogami.frontend.view.nav

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.PlaygroundModel
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled, FlipEnabled, FlipType}
import com.mogproject.mogami.frontend.view.button.CommandButton
import org.scalajs.dom.html.Button

import scalatags.JsDom.all._

/**
  *
  */
class FlipButton extends WebComponent with PlaygroundSAMObserver {

  private[this] var value: FlipType = FlipDisabled

  override lazy val element: Button = CommandButton(
    "btn-toggle " + classButtonThin,
    onclick := { () => doAction(UpdateConfigurationAction(c => c.copy(flipType = !c.flipType))) }
  ).withDynamicTextContent(_.FLIP, "retweet").element.asInstanceOf[Button]

  def updateValue(newValue: FlipType): Unit = {
    value = newValue
    element.disabled = newValue == DoubleBoard
    replaceClass(element, "ctive", (newValue == FlipEnabled).fold("active", "notActive"))
  }

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.CONF_FLIP_TYPE

  override def refresh(model: PlaygroundModel, flag: Long): Unit = updateValue(model.config.flipType)
}
