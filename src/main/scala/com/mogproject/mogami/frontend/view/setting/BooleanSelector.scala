package com.mogproject.mogami.frontend.view.setting

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.BasePlaygroundConfiguration
import com.mogproject.mogami.frontend.view.button.RadioButton
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
case class BooleanSelector(labelFunc: Messages => String, f: Boolean => BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends WebComponent {
  private[this] val labelElem = WebComponent.dynamicLabel(labelFunc, marginTop := 6.px)

  private[this] val button = RadioButton(
    Seq(false, true),
    (_: Messages) => Map(false -> "Off", true -> "On"),
    (v: Boolean) => doAction(UpdateConfigurationAction(f(v)))
  )

  override def element: Element = div(cls := "row",
    marginLeft := (-10).px,
    marginBottom := 3.px,
    div(cls := "col-xs-7 col-sm-9 small-padding", labelElem.element),
    div(cls := "col-xs-5 col-sm-3", button.element)
  ).render

  def updateValue(newValue: Boolean): Unit = button.select(newValue)
}
