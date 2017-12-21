package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Element

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Alert dialog
  */
case class AlertDialog(messageLang: Language, message: TypedTag[Element]) extends ModalLike {

  override def displayCloseButton: Boolean = false

  override val title: String = messageLang match {
    case Japanese => "確認"
    case English => "Confirmation"
  }

  override val modalBody: ElemType = div(bodyDefinition, message)

  override val modalFooter: ElemType = div(footerDefinition, div(cls := "row",
    div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
      button(tpe := "button", cls := "btn btn-default btn-block", dismiss, "OK")
    )
  ))

}
