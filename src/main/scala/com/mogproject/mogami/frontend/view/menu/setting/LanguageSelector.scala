package com.mogproject.mogami.frontend.view.menu.setting

import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, English, Japanese, Language}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button.RadioButton
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
case class LanguageSelector(labelString: String, f: Language => BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends WebComponent {

  private[this] val button: RadioButton[Language] = RadioButton(
    Seq(Japanese, English), Map(English -> Seq("Japanese", "English")),
    onClick = { l: Language => PlaygroundSAM.doAction(UpdateConfigurationAction(f(l))) }
  )

  override val element: Div = div(cls := "form-group",
    marginBottom := 3.px,
    div(cls := "row",
      marginLeft := (-10).px,
      div(cls := "col-xs-4 small-padding", label(marginTop := 6, labelString)),
      div(cls := "col-xs-8", button.element)
    )
  ).render

  def updateLabel(lang: Language): Unit = button.updateLabel(lang)

  def updateValue(newValue: Language): Unit = button.updateValue(newValue)

}

