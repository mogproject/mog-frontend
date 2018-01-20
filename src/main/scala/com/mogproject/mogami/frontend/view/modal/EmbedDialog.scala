package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.model.board.FlipEnabled
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, English, GameControl, Japanese, Language}
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}
import com.mogproject.mogami.frontend.view.button.{DropdownMenu, RadioButton}
import com.mogproject.mogami.frontend.view.i18n.Messages
import com.mogproject.mogami.frontend.view.modal.embed.EmbedCodeArea
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  *
  */
case class EmbedDialog(gameControl: GameControl, config: BasePlaygroundConfiguration) extends ModalLike {
  // Message lang is determined when the class is instantiated.
  private[this] val msgs: Messages = Messages.get


  // local classes
  case class DropdownSelector[A](labelFunc: Messages => String,
                                 items: Vector[A],
                                 itemLabelFunc: Messages => Map[A, String]) extends WebComponent {
    private[this] val labelElem = WebComponent.dynamicLabel(labelFunc)

    private[this] val button: DropdownMenu[A] = DropdownMenu[A](items, itemLabelFunc, menuClass = "left", clickAction = v => {
      button.select(v)
      updateEmbedCode()
    })

    override def element: Element = div(
      cls := "row",
      marginBottom := 10.px,
      div(cls := "col-xs-3 col-xs-offset-1 small-padding", labelElem.element),
      div(cls := "col-xs-4", marginTop := (-8).px, button.element)
    ).render

    def select(item: A): Unit = button.select(item)

    def getValue: A = button.getValue
  }

  case class BooleanSelector(labelFunc: Messages => String) extends WebComponent {
    private[this] val labelElem = WebComponent.dynamicLabel(labelFunc, marginTop := 6.px)

    private[this] val button: RadioButton[Boolean] = RadioButton(Seq(false, true), (_: Messages) => Map(false -> "Off", true -> "On"),
      (v: Boolean) => {
        button.select(v)
        updateEmbedCode()
      }
    )

    override def element: Element = div(cls := "row",
      //      marginLeft := (-10).px,
      marginBottom := 3.px,
      div(cls := "col-xs-3 col-xs-offset-1 small-padding", labelElem.element),
      div(cls := "col-xs-4", button.element)
    ).render

    def select(item: Boolean): Unit = button.select(item)

    def getValue: Boolean = button.getValue
  }

  case class LanguageSelector(labelFunc: Messages => String) extends WebComponent {
    private[this] val labelElem = WebComponent.dynamicLabel(labelFunc, marginTop := 6.px)

    private[this] val button: RadioButton[Option[Language]] = RadioButton(
      Seq(Some(Japanese), Some(English), None),
      (m: Messages) => Map(Some(English) -> m.ENGLISH, Some(Japanese) -> m.JAPANESE, None -> m.AUTO_DETECT),
      (l: Option[Language]) => {
        button.select(l)
        updateEmbedCode()
      }
    )

    override val element: Div = div(cls := "row",
      marginBottom := 3.px,
      div(cls := "row",
        div(cls := "col-xs-3 col-xs-offset-1 small-padding", labelElem.element),
        div(cls := "col-xs-7", button.element)
      )
    ).render

    def select(item: Option[Language]): Unit = button.select(item)

    def getValue: Option[Language] = button.getValue
  }


  override def getTitle(messages: Messages): Frag = StringFrag(messages.EMBED_CODE)


  private[this] val contentButton: RadioButton[Boolean] = RadioButton(Seq(false, true), m => Map(false -> m.RECORD, true -> m.SNAPSHOT), b => {
    contentButton.select(b)
    updateEmbedCode()
  })

  private[this] val sizeSelector = DropdownSelector[Int](
    _.BOARD_SIZE, Vector(22, 25, 30), m => Map(22 -> m.SMALL, 25 -> m.MEDIUM, 30 -> m.LARGE)
  )
  private[this] val layoutSelector = DropdownSelector[SVGAreaLayout](
    _.LAYOUT, Vector(SVGStandardLayout, SVGCompactLayout, SVGWideLayout), _.LAYOUT_OPTIONS
  )
  private[this] val pieceFaceSelector = DropdownSelector[PieceFace](
    _.PIECE_GRAPHIC, PieceFace.all.toVector, _.PIECE_GRAPHIC_OPTIONS
  )

  private[this] val flipSelector = BooleanSelector(_.FLIP)
  private[this] val visualEffectSelector = BooleanSelector(_.VISUAL_EFFECTS)
  private[this] val soundEffectSelector = BooleanSelector(_.SOUND_EFFECTS)
  private[this] val messageLanguageSelector = LanguageSelector(_.MESSAGE_LANG)
  private[this] val recordLanguageSelector = LanguageSelector(_.RECORD_LANG)

  private[this] val embedCodeArea = EmbedCodeArea("embed-code")

  override val modalBody: ElemType = div(bodyDefinition,
    embedCodeArea.element,
    h4(msgs.EMBED_OPTIONS),
    div(cls := "row",
      marginBottom := 15.px,
      div(cls := "col-xs-3 col-xs-offset-1", label(msgs.EMBED_CONTENT)),
      div(cls := "col-xs-4", contentButton.element)
    ),
    sizeSelector.element,
    layoutSelector.element,
    pieceFaceSelector.element,
    flipSelector.element,
    visualEffectSelector.element,
    soundEffectSelector.element,
    messageLanguageSelector.element,
    recordLanguageSelector.element
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
      contentButton.getValue,
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
    contentButton.select(false)
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
