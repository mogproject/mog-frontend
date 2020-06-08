package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend.{BootstrapJQuery, _}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import org.scalajs.jquery.{JQuery, jQuery}

import scalatags.JsDom
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  *
  */
trait ModalLike {
  def getTitle(messages: Messages): Frag

  type ElemType = TypedTag[Div]

  def dismiss: Modifier = data("dismiss") := "modal"

  protected val bodyDefinition: scalatags.generic.AttrPair[Element, String] = cls := "modal-body"

  protected val footerDefinition: scalatags.generic.AttrPair[Element, String] = cls := "modal-footer"

  def modalBody: ElemType

  def modalFooter: ElemType

  def isStatic: Boolean = false

  private[this] def displayCloseButton: Boolean = !isStatic

  def initialize(dialog: JQuery): Unit = {}

  private[this] lazy val closeButton = button(tpe := "button", cls := "close", data("dismiss") := "modal", aria.label := "Close",
    span(aria.hidden := true, raw("&times;"))
  )

  protected lazy val elem: Div =
    div(cls := "modal fade", tabindex := "-1", role := "dialog", isStatic.option(data("backdrop") := "static"),
      div(cls := "modal-dialog", role := "document",
        div(cls := "modal-content",
          // header
          div(cls := "modal-header",
            h4(cls := "modal-title", float := "left", getTitle(Messages.get)),
            displayCloseButton.option(closeButton)
          ),
          // body
          modalBody,

          // footer
          modalFooter
        )
      )
    ).render

  def show(): Unit = {
    val dialog = jQuery(elem)
    dialog.on("hidden.bs.modal", () => {
      // Hide all tooltips
      Tooltip.hideAllToolTip()

      // Reset scroll
      dom.window.scrollTo(0, 0)

      // Remove from DOM
      dialog.remove()
    })

    initialize(dialog)

    dialog.asInstanceOf[BootstrapJQuery].modal("show")
  }

  def hide(): Unit = {
    jQuery(elem).asInstanceOf[BootstrapJQuery].modal("hide")
  }

}

