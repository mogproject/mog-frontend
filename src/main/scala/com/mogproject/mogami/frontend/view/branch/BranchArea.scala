package com.mogproject.mogami.frontend.view.branch

import com.mogproject.mogami.{BranchNo, Game, Move}
import com.mogproject.mogami.core.game.Game.GamePosition
import com.mogproject.mogami.frontend.action.{UpdateConfigurationAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.action.dialog.AskDeleteBranchAction
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.bootstrap.Tooltip
import com.mogproject.mogami.frontend.view.{English, Japanese, Language, WebComponent}
import com.mogproject.mogami.frontend.view.button.RadioButton
import org.scalajs.dom.html.{Button, Div}
import org.scalajs.dom.raw.HTMLSelectElement
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Event

import scalatags.JsDom.all._

/**
  * Branch buttons on Left Sidebar for PC/tablet, or Menu Modal for mobile
  */
case class BranchArea(isMobile: Boolean) extends WebComponent {

  /** HTML elements */
  private[this] lazy val changeBranchButton: HTMLSelectElement = select(
    cls := "form-control",
    width := "100%",
    onchange := { e: Event =>
      e.target match {
        case elem: HTMLSelectElement => PlaygroundSAM.doAction(UpdateGameControlAction(_.changeDisplayBranch(elem.selectedIndex)))
        case _ => // do nothing
      }
    }
  ).render

  private[this] lazy val newBranchButton: RadioButton[Boolean] = RadioButton(
    Seq(false, true),
    Map(English -> Seq("Off", "On")),
    (isNewBranchMode: Boolean) => PlaygroundSAM.doAction(UpdateConfigurationAction(_.copy(newBranchMode = isNewBranchMode))),
    tooltip = (!isMobile).option("Creates a new branch")
  )

  private[this] lazy val deleteBranchButton = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := "bottom",
    data("original-title") := "Delete this branch",
    data("dismiss") := "modal",
    onclick := { () => PlaygroundSAM.doAction(AskDeleteBranchAction) },
    isMobile.fold("Delete", span(cls := "glyphicon glyphicon-trash"))
  ).render

  private[this] lazy val forksButtons = div("").render

  /** Utility functions */
  private[this] def branchNoToString(branchNo: BranchNo): String = (branchNo == 0).fold("Trunk", s"Branch#${branchNo}")

  private[this] def updateBranchList(numBranches: Int, displayBranch: BranchNo): Unit = {
    val s = (0 to numBranches).map(s => option(branchNoToString(s))).toString
    changeBranchButton.innerHTML = s
    changeBranchButton.selectedIndex = displayBranch
  }

  private[this] def createForkButton(move: Move, branchNo: BranchNo, language: Language, tooltipPlacement: String): Button = button(
    tpe := "button",
    cls := "btn btn-default btn-block",
    data("toggle") := "tooltip",
    data("placement") := tooltipPlacement,
    data("original-title") := branchNoToString(branchNo),
    data("dismiss") := "modal",
    onclick := {
      () =>
        dom.window.setTimeout(
          () => PlaygroundSAM.doAction(UpdateGameControlAction(_.changeDisplayBranch(branchNo).withNextDisplayPosition)),
          0
        )
    },
    move.player.toSymbolString() + (language match {
      case English => move.toWesternNotationString
      case Japanese => move.toJapaneseNotationString
    })
  ).render

  //
  // layout
  //
  private[this] val playModeMenu: Div = div(
    display := display.none.v,
    br(),
    label("New Branch Mode"),
    div(cls := "row",
      div(cls := "col-xs-7 col-sm-9", p(paddingTop := "6px", "Creates a new branch whenever you make a different move.")),
      div(cls := "col-xs-5 col-sm-3", newBranchButton.element)
    ),
    br(),
    div(cls := "row",
      div(cls := "col-xs-7 col-sm-9", label(paddingTop := "6px", "Delete This Branch")),
      div(cls := "col-xs-5 col-sm-3", deleteBranchButton)
    )
  ).render

  override lazy val element: Div = isMobile.fold(outputOnMenu, outputCompact).render

  private[this] def outputOnMenu = div(
    div(cls := "row",
      div(cls := "col-xs-6 col-sm-8", label(paddingTop := "6px", "Change Branch")),
      div(cls := "col-xs-6 col-sm-4", changeBranchButton)
    ),
    label("Forks"),
    br(),
    forksButtons,
    playModeMenu
  )

  private[this] def outputCompact = div(
    marginTop := 20.px,
    div(cls := "row",
      marginRight := 12.px,
      marginBottom := 10.px,
      div(cls := "col-xs-6", label("Branch")),
      div(cls := "col-xs-6", marginTop := (-6).px,
        newBranchButton.element)
    ),
    div(
      marginLeft := 14.px,
      marginBottom := 20.px,
      div(cls := "btn-group", role := "group",
        div(cls := "btn-group", width := 130.px, marginBottom := 10.px, changeBranchButton),
        div(cls := "btn-group", deleteBranchButton)
      ),
      forksButtons
    )
  )

  //
  // actions
  //
  private[this] def playModeElements = Seq(
    playModeMenu
  ) ++ isMobile.fold(Seq.empty, Seq(newBranchButton.element, deleteBranchButton))

  def showEditMenu(): Unit = playModeElements.foreach(WebComponent.showElement)

  def hideEditMenu(): Unit = playModeElements.foreach(WebComponent.hideElement)

  private[this] def updateButtons(game: Game, gamePosition: GamePosition, recordLang: Language): Unit = {
    updateBranchList(game.branches.length, gamePosition.branch)

    deleteBranchButton.disabled = gamePosition.isTrunk

    val forks = game.getForks(gamePosition)

    if (forks.isEmpty) {
      forksButtons.innerHTML = isMobile.fold("No forks.", "")
    } else {
      val nextMove = game.getMove(gamePosition).map(_ -> gamePosition.branch)

      val buttons = (nextMove.toSeq ++ forks).map { case (m, b) => createForkButton(m, b, recordLang, isMobile.fold("bottom", "right")) }
      Tooltip.enableHoverToolTip(buttons)

      val elem = (if (isMobile) {
        div(
          cls := "row",
          buttons.map(div(cls := "col-sm-4 col-xs-6", _))
        )
      } else {
        div(
          cls := "row",
          marginLeft := 0.px,
          buttons.map(div(cls := "col-xs-8", paddingLeft := 0.px, _))
        )
      }).render

      forksButtons.innerHTML = ""
      forksButtons.appendChild(elem)
    }
  }

  def refresh(gameControl: Option[GameControl], recordLang: Language, modeType: ModeType, newBranchMode: Boolean): Unit = {
    gameControl match {
      case Some(gc) =>
        show()
        updateButtons(gc.game, gc.gamePosition, recordLang)
        newBranchButton.updateValue(newBranchMode)
        if (modeType == PlayModeType) showEditMenu() else hideEditMenu()
      case None =>
        hide()
    }
  }
}
