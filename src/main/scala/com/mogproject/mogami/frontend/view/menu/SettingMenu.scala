package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.{PlaygroundAction, UpdateConfigurationAction}
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled}
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, PieceFace, _}
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import com.mogproject.mogami.frontend.view.button.{DropdownMenu, RadioButton}
import com.mogproject.mogami.frontend.view.{English, Language, WebComponent}
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

  //
  // Utility
  //
  private[this] def createToggleButton(f: Boolean => BasePlaygroundConfiguration => BasePlaygroundConfiguration) = RadioButton(
    Seq(false, true), Map(English -> Seq("Off", "On")),
    onClick = { v: Boolean => PlaygroundSAM.doAction(UpdateConfigurationAction(f(v))) }
  )

  private[this] def renderToggleButtonElement(title: String, component: WebComponent) = div(cls := "row",
    marginLeft := (-10).px,
    marginBottom := 3.px,
    div(cls := "col-xs-7 col-sm-9 small-padding", label(marginTop := 6, title)),
    div(cls := "col-xs-5 col-sm-3", component.element)
  ).render

  private[this] def renderDropdownButtonElement[A, M <: BasePlaygroundModel](title: String, button: DropdownMenu[A, M]) = div(
    marginBottom := 15.px,
    div(
      cls := "pull-right",
      marginTop := (-8).px,
      button.element
    ),
    label(title)
  ).render

  //
  // Dropdown Buttons
  //
  private[this] lazy val sizeSettings = Seq(
    None -> "Automatic",
    Some(15) -> "15 - Extra Small",
    Some(30) -> "30 - Small",
    Some(40) -> "40 - Medium",
    Some(50) -> "50 - Large",
    Some(60) -> "60 - Extra Large"
  )

  private[this] lazy val boardSizeButton = DropdownMenu[Option[Int], BasePlaygroundModel](
    sizeSettings.map(_._1).toVector,
    sizeSettings.map { case (k, v) => k -> Map[Language, String](English -> v) }.toMap,
    pw => UpdateConfigurationAction(_.copy(pieceWidth = pw)),
    menuClass = "left",
    separatorIndexes = Seq(1)
  )

  private[this] lazy val layoutButton = DropdownMenu[SVGAreaLayout, BasePlaygroundModel](
    Vector(SVGStandardLayout, SVGCompactLayout, SVGWideLayout),
    Map(SVGStandardLayout -> Map(English -> "Standard"), SVGCompactLayout -> Map(English -> "Compact"), SVGWideLayout -> Map(English -> "Wide")),
    l => UpdateConfigurationAction(_.copy(layout = l))
  )

  private[this] lazy val pieceFaceButton = DropdownMenu[PieceFace, BasePlaygroundModel](
    Vector(JapaneseOneCharFace),
    Map(JapaneseOneCharFace -> Map(English -> "Japanese 1")),
    pf => UpdateConfigurationAction(_.copy(pieceFace = pf))
  )

  //
  // Toggle Buttons
  //
  private[this] lazy val doubleBoardButton: RadioButton[Boolean] = createToggleButton(v => _.copy(flipType = v.fold(DoubleBoard, FlipDisabled)))
  private[this] lazy val visualEffectButton: RadioButton[Boolean] = createToggleButton(v => _.copy(visualEffectEnabled = v))
  private[this] lazy val soundEffectButton: RadioButton[Boolean] = createToggleButton(v => _.copy(soundEffectEnabled = v))

//  private[this] val messageLanguageSelector = LanguageSelector("Messages", onClick = { v => PlaygroundSAM.doAction(UpdateConfigurationAction(_.copy(messageLang = v))) })
//  private[this] val recordLanguageSelector = LanguageSelector("Record", onClick = { v => PlaygroundSAM.doAction(UpdateConfigurationAction(_.copy(recordLang = v))) })
  //  private[this] val pieceLanguageSelector = LanguageSelector("Pieces", onClick = { v => SAM.doAction(UpdateConfigurationAction(_.copy(pieceLang = v))) })

  override lazy val content: JsDom.TypedTag[Div] = div(
    renderDropdownButtonElement("Board Size", boardSizeButton),
    renderDropdownButtonElement("Layout", layoutButton),
    renderDropdownButtonElement("Piece Graphic", pieceFaceButton),
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
    boardSizeButton.select(config.pieceWidth)
    layoutButton.select(config.layout)

    doubleBoardButton.updateValue(config.flipType == DoubleBoard)
    visualEffectButton.updateValue(config.visualEffectEnabled)
    soundEffectButton.updateValue(config.soundEffectEnabled)
//    messageLanguageSelector.updateValue(config.messageLang)
//    recordLanguageSelector.updateValue(config.recordLang)
//    pieceLanguageSelector.updateValue(config.pieceLang)
  }


}