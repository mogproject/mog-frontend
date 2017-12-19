package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami.Game
import com.mogproject.mogami.frontend.model.BasePlaygroundConfiguration
import com.mogproject.mogami.frontend.view.{English, Japanese}
import com.mogproject.mogami.frontend.view.menu.MenuPane
import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  * Menu dialog
  */
case class MenuDialog(gameID: String, config: BasePlaygroundConfiguration, game: Game) extends ModalLike {

  override def isStatic: Boolean = false

  override def displayCloseButton: Boolean = true

  override val title: String = config.messageLang match {
    case Japanese => "メニュー"
    case English => "Menu"
  }

  val menuPane = MenuPane(true)

  override val modalBody: ElemType = div(bodyDefinition, menuPane.element)

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
        button(tpe := "button", cls := "btn btn-default btn-block", data("dismiss") := "modal", "OK")
      )
    )
  )

  override def initialize(dialog: JQuery): Unit = {
//    menuPane.initialize()
//    menuPane.linksMenu.setGameURL(config, gameID)
//    menuPane.linksMenu.setPlaygroundURL(config.toPlaygroundUrl(game))
//    menuPane.settingsMenu.refresh(config)
  }

}