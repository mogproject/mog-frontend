package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Element

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Alert dialog
  */
case class AlertDialog(message: TypedTag[Element], embeddedMode: Boolean) extends ModalLike {

  override def getTitle(messages: Messages): Frag = StringFrag(messages.CONFIRMATION)

  override val modalBody: ElemType = div(bodyDefinition, message)

  override val modalFooter: ElemType = div(footerDefinition, div(cls := "row",
    div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
      button(tpe := "button", cls := "btn btn-default btn-block", dismiss, "OK")
    )
  ))

}
