package com.mogproject.mogami.frontend.view.menu.setting

import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.BasePlaygroundConfiguration
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
import com.mogproject.mogami.frontend.view.button.RadioButton
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
case class BooleanSelector(labelString: String, f: Boolean => BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends WebComponent {
  private[this] val button = RadioButton(
    Seq(false, true), Map(English -> Seq("Off", "On")),
    onClick = { v: Boolean => PlaygroundSAM.doAction(UpdateConfigurationAction(f(v))) }
  )

  override def element: Element = div(cls := "row",
    marginLeft := (-10).px,
    marginBottom := 3.px,
    div(cls := "col-xs-7 col-sm-9 small-padding", label(marginTop := 6, labelString)),
    div(cls := "col-xs-5 col-sm-3", button.element)
  ).render

  def updateLabel(lang: Language): Unit = button.updateLabel(lang)

  def updateValue(newValue: Boolean): Unit = button.updateValue(newValue)
}
