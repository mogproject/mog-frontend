package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.board.UpdateGameInfoAction
import org.scalajs.dom.html.Input
import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  * Game information dialog
  */
case class GameInfoDialog(messageLang: Language, gameInfo: GameInfo) extends ModalLike {

  //
  // game info specific
  //
  private[this] val nameLabel = messageLang match {
    case Japanese => "対局者名"
    case English => "Player Names"
  }

  private[this] val defaultNames: Map[Player, String] = messageLang match {
    case Japanese => Map(BLACK -> "先手", WHITE -> "後手")
    case English => Map(BLACK -> "Black", WHITE -> "White")
  }

  private[this] val tagNames: Map[Player, Symbol] = Map(BLACK -> 'blackName, WHITE -> 'whiteName)

  private[this] val inputNames: Map[Player, Input] = List(BLACK, WHITE).map { p =>
    p -> input(
      tpe := "text",
      cls := "form-control",
      maxlength := 12,
      onfocus := { () => inputNames(p).select() },
      value := gameInfo.tags.get(tagNames(p)).filter(_.nonEmpty).getOrElse(defaultNames(p))
    ).render
  }.toMap

  private[this] def getGameInfo: GameInfo =
    gameInfo.copy(tags = tagNames.map { case (p, t) => t -> inputNames(p).value })

  //
  // modal traits
  //
  override val title: String = messageLang match {
    case Japanese => "対局情報"
    case English => "Game Information"
  }

  override val modalBody: ElemType = div(bodyDefinition,
    label(nameLabel),
    div(cls := "row",
      marginBottom := 3,
      div(cls := "col-xs-4 small-padding", textAlign := "right", marginTop := 6, label("☗")),
      div(cls := "col-xs-8", inputNames(BLACK))
    ),
    div(cls := "row",
      div(cls := "col-xs-4 small-padding", textAlign := "right", marginTop := 6, label("☖")),
      div(cls := "col-xs-8", inputNames(WHITE))
    )
  )

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-4 col-xs-offset-8 col-md-3 col-md-offset-9",
        button(
          tpe := "submit", cls := "btn btn-default btn-block", dismiss,
          onclick := { () => PlaygroundSAM.doAction(UpdateGameInfoAction(getGameInfo)) },
          "Update"
        )
      )
    )
  )

  override def initialize(dialog: JQuery): Unit = {
    dialog.on("show.bs.modal", () ⇒ {
      // todo: this doesn't work
      inputNames(BLACK).focus()
    })
  }

}
