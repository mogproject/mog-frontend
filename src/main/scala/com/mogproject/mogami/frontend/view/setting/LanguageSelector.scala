package com.mogproject.mogami.frontend.view.setting

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, English, Japanese, Language}
import com.mogproject.mogami.frontend.view.button.RadioButton
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
case class LanguageSelector(labelFunc: Messages => String, f: Language => BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends WebComponent {
  private[this] val labelElem = WebComponent.dynamicLabel(labelFunc, marginTop := 6.px)

  private[this] val button: RadioButton[Language] = RadioButton(
    Seq(Japanese, English),
    (m: Messages) => Map(English -> m.ENGLISH, Japanese -> m.JAPANESE),
    { l: Language => doAction(UpdateConfigurationAction(f(l))) }
  )

  override val element: Div = div(cls := "form-group",
    marginBottom := 3.px,
    div(cls := "row",
      marginLeft := (-10).px,
      div(cls := "col-xs-4 small-padding", labelElem.element),
      div(cls := "col-xs-8", button.element)
    )
  ).render

  def updateValue(newValue: Language): Unit = button.select(newValue)

}

