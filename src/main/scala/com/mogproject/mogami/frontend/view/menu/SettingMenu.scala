package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.{BoardIndexType, DoubleBoard, FlipDisabled}
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import com.mogproject.mogami.frontend.view.setting.{BooleanSelector, DropdownSelector, LanguageSelector}
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
class SettingMenu extends AccordionMenu with PlaygroundSAMObserver {
  override lazy val ident: String = "Settings"

  override def getTitle(messages: Messages): String = messages.SETTINGS

  override lazy val icon: String = "wrench"
  override lazy val visibleMode = Set(PlayModeType, ViewModeType, EditModeType)

  //
  // Elements
  //
  private[this] lazy val boardSizeSelector = DropdownSelector[Option[Int]](_.BOARD_SIZE, Vector(
    None, Some(15), Some(20), Some(25), Some(30), Some(40), Some(50), Some(60)
  ), _.BOARD_SIZE_OPTIONS, v => _.copy(pieceWidth = v), Seq(1))

  private[this] lazy val layoutSelector = DropdownSelector[SVGAreaLayout](_.LAYOUT, Vector(
    SVGStandardLayout, SVGCompactLayout, SVGWideLayout
  ), _.LAYOUT_OPTIONS, v => _.copy(layout = v))

  private[this] lazy val pieceFaceSelector = DropdownSelector[PieceFace](
    _.PIECE_GRAPHIC, PieceFace.all.toVector, _.PIECE_GRAPHIC_OPTIONS, v => _.copy(pieceFace = v)
  )

  private[this] lazy val boardColorSelector = DropdownSelector[(String, String, String)](
    _.BOARD_BACKGROUND, Vector(
      FrontendSettings.color.defaultTheme,
      FrontendSettings.color.naturalTheme
    ), _.BOARD_BACKGROUND_OPTIONS, v => _.copy(colorBackground = v._1, colorCursor = v._2, colorLastMove = v._3)
  )

  private[this] lazy val boardIndexTypeSelector = DropdownSelector[BoardIndexType](
    _.BOARD_INDEX_TYPE, BoardIndexType.all.toVector, _.BOARD_INDEX_TYPE_OPTIONS, v => _.copy(boardIndexType = v)
  )

  private[this] lazy val doubleBoardSelector = BooleanSelector(_.DOUBLE_BOARD_MODE, v => _.copy(flipType = v.fold(DoubleBoard, FlipDisabled)))
  private[this] lazy val visualEffectSelector = BooleanSelector(_.VISUAL_EFFECTS, v => _.copy(visualEffectEnabled = v))
  private[this] lazy val soundEffectSelector = BooleanSelector(_.SOUND_EFFECTS, v => _.copy(soundEffectEnabled = v))

  private[this] lazy val messageLanguageSelector = LanguageSelector(_.MESSAGE_LANG, v => _.copy(messageLang = v))
  private[this] lazy val recordLanguageSelector = LanguageSelector(_.RECORD_LANG, v => _.copy(recordLang = v))

  private[this] lazy val selectors = Seq(
    boardSizeSelector,
    layoutSelector,
    pieceFaceSelector,
    boardColorSelector,
    boardIndexTypeSelector,
    doubleBoardSelector,
    visualEffectSelector,
    soundEffectSelector,
    messageLanguageSelector,
    recordLanguageSelector
  )

  override lazy val content: JsDom.TypedTag[Div] = div(
    selectors.map(_.element),
    WebComponent.dynamicDiv(_.SETTINGS_INFO, cls := "alert alert-success setting-alert").element
  )

  //
  // Observer
  //
  override val samObserveMask: Long = super.samObserveMask | ObserveFlag.CONF

  override def refresh(model: PlaygroundModel, flag: Long): Unit = {
    super.refresh(model, flag)

    val config = model.config

    boardSizeSelector.select(config.pieceWidth)
    layoutSelector.select(config.layout)
    pieceFaceSelector.select(config.pieceFace)
    boardColorSelector.select((config.colorBackground, config.colorCursor, config.colorLastMove))
    boardIndexTypeSelector.select(config.boardIndexType)

    doubleBoardSelector.updateValue(config.flipType == DoubleBoard)
    visualEffectSelector.updateValue(config.visualEffectEnabled)
    soundEffectSelector.updateValue(config.soundEffectEnabled)

    messageLanguageSelector.updateValue(config.messageLang)
    recordLanguageSelector.updateValue(config.recordLang)
  }

}