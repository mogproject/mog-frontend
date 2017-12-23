package com.mogproject.mogami.frontend.view.action

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.game.ResignAction
import com.mogproject.mogami.frontend.view.button.SingleButton
import com.mogproject.mogami.frontend.view.modal.YesNoDialog
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.dialog.MenuDialogAction
import com.mogproject.mogami.frontend.model.Mode
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/**
  * Resign button
  */
case class ResignButton(isSmall: Boolean, confirm: Boolean) extends WebComponent {
  private[this] def createLabel(messageLang: Language) = {
    val s = Map[Language, String](English -> "Resign", Japanese -> "投了")(messageLang)
    if (isSmall) {
      Seq(StringFrag(s + " "), span(cls := s"glyphicon glyphicon-flag", aria.hidden := true)).render
    } else {
      s.render
    }
  }

  /** @note `dismissModal = true` might not work because of `handleUpdate` */
  private[this] val button = SingleButton(
    Map(English -> createLabel(English), Japanese -> createLabel(Japanese)),
    "btn-default" +: Seq("thin-btn", "btn-resign").filter(_ => isSmall),
    clickAction = Some(() => clickAction()),
    tooltip = if (isSmall) Map.empty else Map(English -> "Resign this game", Japanese -> "投了します"),
    isBlockButton = !isSmall
  )

  def clickAction(): Unit = if (confirm) {
    YesNoDialog(English, div("Do you really want to resign?"), () => clickActionImpl()).show()
  } else {
    clickActionImpl()
  }

  def clickActionImpl(): Unit = {
    doAction(ResignAction)
    if (!isSmall) doAction(MenuDialogAction(false))
  }

//  override def handleUpdate(mode: Mode): Unit = setDisabled(!mode.canMakeMove)

  override val element: HTMLElement = button.element
}
