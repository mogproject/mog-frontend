package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend._
import org.scalajs.dom.html.Element

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Yes-no dialog
  */
case class YesNoDialog(message: TypedTag[Element], callback: () => Unit) extends ModalLike {

  //
  // modal traits
  //
  override def isStatic: Boolean = true

  override def getTitle(messages: Messages): String = messages.CONFIRMATION

  override val modalBody: ElemType = div(bodyDefinition, message)

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4 col-xs-offset-4 col-md-3 col-md-offset-6",
        button(tpe := "button", cls := "btn btn-default btn-block", dismiss, Messages.get.NO)
      ),
      div(cls := "col-xs-4 col-md-3",
        button(tpe := "button", cls := "btn btn-primary btn-block", onclick := callback, dismiss, Messages.get.YES)
      )
    )
  )

}
