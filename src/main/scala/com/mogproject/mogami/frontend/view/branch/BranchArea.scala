package com.mogproject.mogami.frontend.view.branch

import com.mogproject.mogami.{BranchNo, Game, Move}
import com.mogproject.mogami.core.game.Game.GamePosition
import com.mogproject.mogami.frontend.action.{UpdateConfigurationAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.action.dialog.AskDeleteBranchAction
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.button.{CommandButton, RadioButton}
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLSelectElement
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom
import org.scalajs.dom.Event

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/**
  * Branch buttons on Left Sidebar for PC/tablet, or Menu Modal for mobile
  */
case class BranchArea(isMobile: Boolean) extends WebComponent with SAMObserver[BasePlaygroundModel] {

  /** HTML elements */
  private[this] lazy val changeBranchButton: HTMLSelectElement = select(
    cls := "form-control",
    width := 100.pct,
    onchange := { e: Event =>
      e.target match {
        case elem: HTMLSelectElement => doAction(UpdateGameControlAction(_.changeDisplayBranch(elem.selectedIndex)))
        case _ => // do nothing
      }
    }
  ).render

  private[this] lazy val newBranchButton: RadioButton[Boolean] = RadioButton(
    Seq(false, true),
    (_: Messages) => Map(false -> "Off", true -> "On"),
    (isNewBranchMode: Boolean) => doAction(UpdateConfigurationAction(_.copy(newBranchMode = isNewBranchMode)))
  )
    .withDynamicHoverTooltip(_.NEW_BRANCH_TOOLTIP)

  private[this] lazy val deleteBranchButton: WebComponent = {
    val ret = CommandButton(
      classButtonDefaultBlock,
      onclick := { () => doAction(AskDeleteBranchAction) }
    )
      .withDynamicHoverTooltip(_.DELETE_BRANCH_TOOLTIP)

    isMobile.fold(ret.withDynamicTextContent(_.DELETE), ret.withTextContent("", "trash"))
  }

  private[this] lazy val forksButtons = div("").render

  /** Utility functions */
  private[this] def branchNoToString(branchNo: BranchNo): String = (branchNo == 0).fold(Messages.get.TRUNK, Messages.get.BRANCH_NO(branchNo))

  private[this] def updateBranchList(numBranches: Int, displayBranch: BranchNo): Unit = {
    WebComponent.replaceChildElements(changeBranchButton, (0 to numBranches).map(s => option(branchNoToString(s)).render))
    changeBranchButton.selectedIndex = displayBranch
  }

  private[this] def createForkButton(move: Move, branchNo: BranchNo, recordLang: Language, tooltipPlacement: TooltipPlacement): WebComponent = CommandButton(
    classButtonDefaultBlock,
    onclick := { () =>
      dom.window.setTimeout(
        () => PlaygroundSAM.doAction(UpdateGameControlAction(_.changeDisplayBranch(branchNo).withNextDisplayPosition)),
        0
      )
    },
    dismissModal
  )
    .withTextContent(move.player.toSymbolString() + (recordLang match {
      case English => move.toWesternNotationString
      case Japanese => move.toJapaneseNotationString
    }))
    .withDynamicHoverTooltip(_.BRANCH_NO(branchNo), tooltipPlacement)

  //
  // layout
  //
  private[this] lazy val playModeMenu: Div = div(
    display := display.none.v,
    br(),
    WebComponent.dynamicLabel(_.NEW_BRANCH_MODE).element,
    div(cls := "row",
      div(cls := "col-xs-7 col-sm-9", WebComponent(p(paddingTop := "6px")).withDynamicTextContent(_.NEW_BRANCH_HELP).element),
      div(cls := "col-xs-5 col-sm-3", newBranchButton.element)
    ),
    br(),
    div(cls := "row",
      div(cls := "col-xs-7 col-sm-9", WebComponent.dynamicLabel(_.DELETE_BRANCH, paddingTop := "6px").element),
      div(cls := "col-xs-5 col-sm-3", deleteBranchButton.element)
    )
  ).render

  override lazy val element: Div = isMobile.fold(outputOnMenu, outputCompact).render

  private[this] def outputOnMenu: TypedTag[Div] = div(
    div(cls := "row",
      div(cls := "col-xs-6 col-sm-8", WebComponent.dynamicLabel(_.CHANGE_BRANCH, paddingTop := "6px").element),
      div(cls := "col-xs-6 col-sm-4", changeBranchButton)
    ),
    WebComponent.dynamicLabel(_.FORKS).element,
    br(),
    forksButtons,
    playModeMenu
  )

  private[this] def outputCompact: TypedTag[Div] = div(
    marginTop := 20.px,
    div(cls := "row",
      marginRight := 12.px,
      marginBottom := 10.px,
      div(cls := "col-xs-6", WebComponent.dynamicLabel(_.BRANCH).element),
      div(cls := "col-xs-6", marginTop := (-6).px, newBranchButton.element)
    ),
    div(
      marginLeft := 14.px,
      marginBottom := 20.px,
      div(cls := "btn-group", role := "group",
        div(cls := "btn-group", width := 130.px, marginBottom := 10.px, changeBranchButton),
        div(cls := "btn-group", deleteBranchButton.element)
      ),
      forksButtons
    )
  )

  //
  // actions
  //
  def showEditMenu(): Unit = {
    if (isMobile) {
      WebComponent.showElement(playModeMenu)
    } else {
      newBranchButton.show()
      deleteBranchButton.show()
    }
  }

  def hideEditMenu(): Unit = {
    if (isMobile) {
      WebComponent.hideElement(playModeMenu)
    } else {
      newBranchButton.hide()
      deleteBranchButton.hide()
    }
  }

  private[this] def updateButtons(game: Game, gamePosition: GamePosition, recordLang: Language): Unit = {

    deleteBranchButton.setDisabled(gamePosition.isTrunk)

    val forks = game.getForks(gamePosition)

    if (forks.isEmpty) {
      forksButtons.innerHTML = isMobile.fold(Messages.get.NO_FORKS, "")
    } else {
      val nextMove = game.getMove(gamePosition).map(_ -> gamePosition.branch)

      val buttons = (nextMove.toSeq ++ forks).map { case (m, b) => createForkButton(m, b, recordLang, isMobile.fold(TooltipPlacement.Bottom, TooltipPlacement.Right)) }

      val elem = (if (isMobile) {
        div(
          cls := "row",
          buttons.map(b => div(cls := "col-sm-4 col-xs-6", b.element))
        )
      } else {
        div(
          cls := "row",
          marginLeft := 0.px,
          buttons.map(b => div(cls := "col-xs-8", paddingLeft := 0.px, b.element))
        )
      }).render

      forksButtons.innerHTML = ""
      forksButtons.appendChild(elem)
    }
  }

  //
  // Observer
  //
  override val samObserveMask: Int = {
    import ObserveFlag._
    MODE_TYPE | GAME_BRANCH | GAME_POSITION | CONF_NEW_BRANCH | CONF_RCD_LANG | CONF_MSG_LANG | MENU_DIALOG
  }

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    import ObserveFlag._

    /** Do not update if the menu dialog is hidden. */
    if (!model.config.deviceType.isMobile || model.menuDialogOpen) {
      model.mode.getGameControl match {
        case Some(gc) =>
          show()

          if (model.menuDialogOpen || isFlagUpdated(flag, MODE_TYPE | GAME_BRANCH | GAME_POSITION | CONF_RCD_LANG | CONF_MSG_LANG)) {
            updateBranchList(gc.game.branches.length, gc.gamePosition.branch)
          }
          if (model.menuDialogOpen || isFlagUpdated(flag, MODE_TYPE | GAME_BRANCH | GAME_POSITION | CONF_RCD_LANG)) {
            updateButtons(gc.game, gc.gamePosition, model.config.recordLang)
          }
          if (model.menuDialogOpen || isFlagUpdated(flag, CONF_NEW_BRANCH)) {
            newBranchButton.select(model.config.newBranchMode)
          }
          if (model.menuDialogOpen || isFlagUpdated(flag, MODE_TYPE)) {
            if (model.mode.modeType == PlayModeType) showEditMenu() else hideEditMenu()
          }

        case None =>
          hide()
      }
    }
  }

}
