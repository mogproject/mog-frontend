package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.UpdateConfigurationAction
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled}
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, EditModeType, PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.button.RadioButton
import com.mogproject.mogami.frontend.view.menu.setting.BoardSizeButton
import com.mogproject.mogami.frontend.view.{English, WebComponent}
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class SettingMenu extends AccordionMenu {
  override lazy val ident: String = "Settings"
  override lazy val title: String = ident
  override lazy val icon: String = "wrench"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType, EditModeType)

  private[this] lazy val boardSizeButton = new BoardSizeButton

  private[this] def createToggleButton(f: Boolean => BasePlaygroundConfiguration => BasePlaygroundConfiguration) = RadioButton(
    Seq(false, true), Map(English -> Seq("Off", "On")),
    onClick = { v: Boolean => PlaygroundSAM.doAction(UpdateConfigurationAction(f(v))) }
  )

  private[this] lazy val doubleBoardButton: RadioButton[Boolean] = createToggleButton(v => _.copy(flipType = v.fold(DoubleBoard, FlipDisabled)))
  private[this] lazy val visualEffectButton: RadioButton[Boolean] = createToggleButton(v => _.copy(visualEffectEnabled = v))
  private[this] lazy val soundEffectButton: RadioButton[Boolean] = createToggleButton(v => _.copy(soundEffectEnabled = v))

//  private[this] val messageLanguageSelector = LanguageSelector("Messages", onClick = { v => PlaygroundSAM.doAction(UpdateConfigurationAction(_.copy(messageLang = v))) })
//  private[this] val recordLanguageSelector = LanguageSelector("Record", onClick = { v => PlaygroundSAM.doAction(UpdateConfigurationAction(_.copy(recordLang = v))) })
  //  private[this] val pieceLanguageSelector = LanguageSelector("Pieces", onClick = { v => SAM.doAction(UpdateConfigurationAction(_.copy(pieceLang = v))) })

  private[this] def renderToggleButtonElement(title: String, component: WebComponent) = div(cls := "row",
    marginLeft := (-10).px,
    marginBottom := 3.px,
    div(cls := "col-xs-7 col-sm-9 small-padding", label(marginTop := 6, title)),
    div(cls := "col-xs-5 col-sm-3", component.element)
  ).render

  override lazy val content: JsDom.TypedTag[Div] = div(
    div(
      marginBottom := 10.px,
      boardSizeButton.element,
      label("Board Size")
    ),
    renderToggleButtonElement("Double Board Mode", doubleBoardButton),
    renderToggleButtonElement("Visual Effects", visualEffectButton),
    renderToggleButtonElement("Sound Effects", soundEffectButton),
//    messageLanguageSelector.element,
//    recordLanguageSelector.element,
    div(
      cls := "alert alert-success setting-alert",
      "These settings will be saved for your browser."
    )
  )

  def refresh(config: BasePlaygroundConfiguration): Unit = {
//    boardSizeButton.updateValue(config.canvasWidth)
//    doubleBoardButton.updateValue(config.flip == DoubleBoard)
//    visualEffectButton.updateValue(config.visualEffectEnabled)
//    soundEffectButton.updateValue(config.soundEffectEnabled)
//    messageLanguageSelector.updateValue(config.messageLang)
//    recordLanguageSelector.updateValue(config.recordLang)
//    pieceLanguageSelector.updateValue(config.pieceLang)
  }
}