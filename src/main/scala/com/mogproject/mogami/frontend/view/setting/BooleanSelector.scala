package com.mogproject.mogami.frontend.view.setting

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, English, Language}
import com.mogproject.mogami.frontend.view.button.RadioButtonOld
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
case class BooleanSelector(labelFunc: Messages => String, f: Boolean => BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends WebComponent {
  private[this] val labelElem = WebComponent.dynamicLabel(labelFunc, marginTop := 6.px)

  private[this] val button = RadioButtonOld(
    Seq(false, true), Map(English -> Seq("Off", "On")),
    onClick = { v: Boolean => PlaygroundSAM.doAction(UpdateConfigurationAction(f(v))) }
  )



  override def element: Element = div(cls := "row",
    marginLeft := (-10).px,
    marginBottom := 3.px,
    div(cls := "col-xs-7 col-sm-9 small-padding", labelElem.element),
    div(cls := "col-xs-5 col-sm-3", button.element)
  ).render

  def updateLabel(lang: Language): Unit = button.updateLabel(lang)

  def updateValue(newValue: Boolean): Unit = button.updateValue(newValue)
}
