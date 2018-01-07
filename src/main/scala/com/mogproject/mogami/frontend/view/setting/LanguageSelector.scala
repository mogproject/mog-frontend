package com.mogproject.mogami.frontend.view.setting

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, English, Japanese, Language}
import com.mogproject.mogami.frontend.view.button.RadioButtonOld
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
case class LanguageSelector(labelFunc: Messages => String, f: Language => BasePlaygroundConfiguration => BasePlaygroundConfiguration) extends WebComponent {
  private[this] val labelElem = WebComponent.dynamicLabel(labelFunc, marginTop := 6.px)

  private[this] val button: RadioButtonOld[Language] = RadioButtonOld(
    Seq(Japanese, English), Map(English -> Seq("Japanese", "English")),
    onClick = { l: Language => PlaygroundSAM.doAction(UpdateConfigurationAction(f(l))) }
  )

  override val element: Div = div(cls := "form-group",
    marginBottom := 3.px,
    div(cls := "row",
      marginLeft := (-10).px,
      div(cls := "col-xs-4 small-padding", labelElem.element),
      div(cls := "col-xs-8", button.element)
    )
  ).render

  def updateLabel(lang: Language): Unit = button.updateLabel(lang)

  def updateValue(newValue: Language): Unit = button.updateValue(newValue)

}

