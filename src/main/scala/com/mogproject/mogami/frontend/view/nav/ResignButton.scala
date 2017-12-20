package com.mogproject.mogami.frontend.view.nav

import com.mogproject.mogami.frontend.action.game.ResignAction
import com.mogproject.mogami.frontend.model.Mode
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.{English, WebComponent}
import com.mogproject.mogami.frontend.view.button.SingleButton
import com.mogproject.mogami.frontend.view.modal.YesNoDialog
import com.mogproject.mogami.frontend.view.observer.ModeObserver
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/**
  * Resign button
  */
class ResignButton extends WebComponent with ModeObserver {
  private[this] val button = SingleButton(
    Map(English -> span("Resign ", span(cls := s"glyphicon glyphicon-flag", aria.hidden := true)).render),
    Seq("btn-default", "thin-btn", "btn-resign"),
    clickAction = Some(() => clickAction())
  )

  def clickAction(): Unit = {
    YesNoDialog(English, div("Do you really want to resign?"), () => PlaygroundSAM.doAction(ResignAction)).show()
  }

  override def handleUpdate(mode: Mode): Unit = setDisabled(!mode.canMakeMove)

  override val element: HTMLElement = button.element
}
