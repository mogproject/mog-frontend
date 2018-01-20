package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend.{Language, Messages}
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, GameControl}

import scalatags.JsDom.all._

/**
  *
  */
case class EmbedDialog(gameControl: GameControl, config: BasePlaygroundConfiguration) extends ModalLike {
  // Message lang is determined when the class is instantiated.
  val messages: Messages = Messages.get

  override def getTitle(messages: Messages): Frag = StringFrag(messages.EMBED_LABEL)

  override val modalBody: ElemType = div(bodyDefinition, "xxx")

  override val modalFooter: ElemType = div(footerDefinition, div(cls := "row",
    div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
      button(tpe := "button", cls := "btn btn-default btn-block", dismiss, "OK")
    )
  ))


}
