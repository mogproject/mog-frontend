package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.frontend.action.dialog.MenuDialogAction
import com.mogproject.mogami.frontend.{BootstrapJQuery, _}
import com.mogproject.mogami.frontend.view.menu.MenuPane
import org.scalajs.dom
import org.scalajs.jquery.jQuery

import scalatags.JsDom.all._

/**
  * Menu dialog
  */
case class MenuDialog(menuPane: MenuPane) extends ModalLike with SAMObserver[BasePlaygroundModel] {

  override def getTitle(messages: Messages): Frag = WebComponent.dynamicSpan(_.MENU).element

  override lazy val modalBody: ElemType = div(bodyDefinition, menuPane.element)

  override lazy val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
        button(tpe := "button", cls := "btn btn-default btn-block", data("dismiss") := "modal", "OK")
      )
    )
  )

  private[this] var dialogElem: Option[BootstrapJQuery] = None

  private[this] def createDialog(): BootstrapJQuery = {
    val e = jQuery(elem)

    e.on("hidden.bs.modal", () ⇒ {
      // Hide all tooltips
      Tooltip.hideAllToolTip()

      // Reset scroll
      dom.window.scrollTo(0, 0)

      // Report to the model
      PlaygroundSAM.doAction(MenuDialogAction(false))
    })

    val ret = e.asInstanceOf[BootstrapJQuery]
    dialogElem = Some(ret)
    ret
  }

  override def show(): Unit = {
    dialogElem.getOrElse(createDialog()).modal("show")
  }

  override def hide(): Unit = {
    dialogElem.foreach(_.modal("hide"))
  }

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.MENU_DIALOG

  override def refresh(model: BasePlaygroundModel, flag: Long): Unit = {
    if (model.menuDialogOpen) show() else hide()
  }
}