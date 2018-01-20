package com.mogproject.mogami.frontend.view.modal.embed

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.model.board.FlipEnabled
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, English, GameControl, Japanese, Language}
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import com.mogproject.mogami.frontend.view.i18n.Messages
import com.mogproject.mogami.frontend.view.modal.ModalLike
import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  *
  */
case class EmbedDialog(gameControl: GameControl, config: BasePlaygroundConfiguration) extends ModalLike {
  // Message lang is determined when the class is instantiated.
  private[this] val msgs: Messages = Messages.get


  private[this] final val DEFAULT_LABEL_CLASS = "col-xs-5 col-sm-3 col-sm-offset-1"
  private[this] final val DEFAULT_BUTTON_CLASS = "col-xs-7 col-sm-4"


  // local classes
  case class DropdownSelector[A](labelFunc: Messages => String,
                                 items: Vector[A],
                                 itemLabelFunc: Messages => Map[A, String],
                                 labelClass: String = DEFAULT_LABEL_CLASS,
                                 buttonClass: String = DEFAULT_BUTTON_CLASS
                                ) extends EmbedDropdownSelector[A](items, itemLabelFunc, () => updateEmbedCode())

  case class BooleanSelector(labelFunc: Messages => String,
                             messageFunc: Messages => Map[Boolean, String] = (_: Messages) => Map(false -> "Off", true -> "On"),
                             labelClass: String = DEFAULT_LABEL_CLASS,
                             buttonClass: String = DEFAULT_BUTTON_CLASS
                            ) extends EmbedRadioSelector[Boolean](Seq(false, true), messageFunc, () => updateEmbedCode())

  case class LanguageSelector(labelFunc: Messages => String,
                              labelClass: String = DEFAULT_LABEL_CLASS,
                              buttonClass: String = "col-xs-11 col-xs-offset-1 col-sm-7 col-sm-offset-0"
                             ) extends EmbedRadioSelector[Option[Language]](
    Seq(Some(Japanese), Some(English), None),
    (m: Messages) => Map(Some(English) -> m.ENGLISH, Some(Japanese) -> m.JAPANESE, None -> m.AUTO_DETECT),
    () => updateEmbedCode()
  )

  override def getTitle(messages: Messages): Frag = StringFrag(messages.EMBED_CODE)

  private[this] val contentSelector = BooleanSelector(_.EMBED_CONTENT, m => Map(false -> m.RECORD, true -> m.SNAPSHOT))
  private[this] val sizeSelector = DropdownSelector[Int](_.BOARD_SIZE, Vector(22, 25, 30), m => Map(22 -> m.SMALL, 25 -> m.MEDIUM, 30 -> m.LARGE))
  private[this] val layoutSelector = DropdownSelector[SVGAreaLayout](_.LAYOUT, Vector(SVGStandardLayout, SVGCompactLayout, SVGWideLayout), _.LAYOUT_OPTIONS)
  private[this] val pieceFaceSelector = DropdownSelector[PieceFace](_.PIECE_GRAPHIC, PieceFace.all.toVector, _.PIECE_GRAPHIC_OPTIONS)
  private[this] val flipSelector = BooleanSelector(_.FLIP)
  private[this] val visualEffectSelector = BooleanSelector(_.VISUAL_EFFECTS)
  private[this] val soundEffectSelector = BooleanSelector(_.SOUND_EFFECTS)
  private[this] val messageLanguageSelector = LanguageSelector(_.MESSAGE_LANG)
  private[this] val recordLanguageSelector = LanguageSelector(_.RECORD_LANG)

  private[this] val embedCodeArea = EmbedCodeArea("embed-code")

  override val modalBody: ElemType = div(bodyDefinition,
    embedCodeArea.element,
    h4(msgs.EMBED_OPTIONS),
    Seq(
      contentSelector,
      sizeSelector,
      layoutSelector,
      pieceFaceSelector,
      flipSelector,
      visualEffectSelector,
      soundEffectSelector,
      messageLanguageSelector,
      recordLanguageSelector
    ).map(_.element)
  )

  override val modalFooter: ElemType = div(footerDefinition, div(cls := "row",
    div(cls := "col-xs-8 col-md-9", textAlign := textAlign.left.v,
      a(target := "_blank", href := FrontendSettings.url.queryParamDocUrl, msgs.EMBED_REFERENCE)
    ),
    div(cls := "col-xs-4 col-md-3",
      button(tpe := "button", cls := "btn btn-default btn-block", dismiss, "OK")
    )
  ))

  private[this] val builder = ArgumentsBuilderEmbed(gameControl)

  private[this] def updateEmbedCode(): Unit = {
    val s = builder.toEmbedCode(
      contentSelector.getValue,
      sizeSelector.getValue,
      layoutSelector.getValue,
      pieceFaceSelector.getValue,
      flipSelector.getValue,
      visualEffectSelector.getValue,
      soundEffectSelector.getValue,
      messageLanguageSelector.getValue,
      recordLanguageSelector.getValue
    )
    embedCodeArea.updateValue(s)
  }

  override def initialize(dialog: JQuery): Unit = {
    contentSelector.select(false)
    sizeSelector.select(25)
    layoutSelector.select(config.layout)
    pieceFaceSelector.select(config.pieceFace)
    flipSelector.select(config.flipType == FlipEnabled)
    visualEffectSelector.select(config.visualEffectEnabled)
    soundEffectSelector.select(config.soundEffectEnabled)
    messageLanguageSelector.select(None)
    recordLanguageSelector.select(None)

    updateEmbedCode()
  }
}
