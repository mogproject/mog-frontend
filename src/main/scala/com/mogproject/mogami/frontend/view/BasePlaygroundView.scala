package com.mogproject.mogami.frontend.view

import com.mogproject.mogami._
import com.mogproject.mogami.core.state.StateCache
import com.mogproject.mogami.frontend.action.{ChangeModeAction, UpdateConfigurationAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.api.Clipboard
import com.mogproject.mogami.frontend.api.Clipboard.Event
import com.mogproject.mogami.frontend.model.analyze._
import com.mogproject.mogami.frontend.sam.{PlaygroundSAM, SAMView}
import com.mogproject.mogami.frontend.view.menu.MenuPane
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, GameControl}
import com.mogproject.mogami.frontend.view.board.canvas.CanvasBoard
import com.mogproject.mogami.frontend.view.i18n.Messages
import com.mogproject.mogami.frontend.view.modal._
import org.scalajs.dom
import org.scalajs.dom.{Element, UIEvent}

import scalatags.JsDom.all._

/**
  *
  */
trait BasePlaygroundView extends SAMView {

  def rootElem: Element

  def website: PlaygroundSiteLike

  def mainPane: MainPaneLike = website.mainPane

  def menuPane: MenuPane = website.menuPane

  override def initialize(): Unit = {
    // create elements
    rootElem.appendChild(website.element)

    // add rotation detection
    dom.window.addEventListener("orientationchange", (_: UIEvent) => PlaygroundSAM.doAction(UpdateConfigurationAction(_.updateScreenOrientation())))

    // initialize clipboard.js
    val cp = new Clipboard(".btn")
    cp.on("success", (e: Event) => Tooltip.display(e.trigger, Messages.get.COPY_SUCCESS))
    cp.on("error", (e: Event) => Tooltip.display(e.trigger, Messages.get.COPY_FAILURE))

    // expand About menu
    if (!website.isMobile) menuPane.accordions.find(_.ident == "About").foreach(a => dom.window.setTimeout(() => a.expandContent(), 100))
  }

  def renderAnalyzeResult(result: AnalyzeResult, recordLang: Language): Unit = result match {
    case CheckmateAnalyzeResult(r) => renderCheckmateAnalyzeResult(r, recordLang)
    case CountAnalyzeResult(p, k, n) => renderCountAnalyzeResult(p, k, n)
  }

  def renderCheckmateAnalyzeResult(result: Option[Seq[Move]], recordLang: Language): Unit = {
    website.analyzeMenu.checkmateButton.displayResult(result, recordLang)
  }

  def renderCountAnalyzeResult(point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int): Unit = {
    website.analyzeMenu.pointCountButton.displayResult(point, isKingInPromotionZone, numPiecesInPromotionZone)
  }

  //
  // Dialogs
  //
  def askPromote(pieceFace: PieceFace, rawMove: Move, rotate: Boolean): Unit = {
    PromotionDialog(pieceFace, rawMove, rotate, mainPane.embeddedMode).show()
  }

  def showGameInfoDialog(gameInfo: GameInfo, isHandicapped: Boolean): Unit = {
    GameInfoDialog(gameInfo, isHandicapped, mainPane.embeddedMode).show()
  }

  def showEditWarningDialog(): Unit = {
    val action = ChangeModeAction(EditModeType, confirmed = true)
    YesNoDialog(p(Messages.get.ASK_EDIT), () => PlaygroundSAM.doAction(action), mainPane.embeddedMode).show()
  }

  def askDeleteBranch(branchNo: BranchNo): Unit = {
    val action = UpdateGameControlAction(_.deleteBranch(branchNo))
    YesNoDialog(p(Messages.get.ASK_DELETE_BRANCH(branchNo)), () => PlaygroundSAM.doAction(action), mainPane.embeddedMode).show()
  }

  def showEditAlertDialog(msg: String): Unit = {
    AlertDialog(p(Messages.get.INVALID_STATE, br(), s"(${msg})"), mainPane.embeddedMode).show()
  }

  def showCommentDialog(text: String): Unit = {
    CommentDialog(text, mainPane.embeddedMode).show()
  }

  //
  // Notes action
  //
  def drawNotes(game: Game, recordLang: Language)(implicit stateCache: StateCache): Unit = {
    dom.window.document.head.appendChild(link(rel := "stylesheet", tpe := "text/css", href := "assets/css/notesview.css").render)
    dom.window.document.body.innerHTML = game.trunk.toHtmlString(recordLang == Japanese, game.comments)
  }

  //
  // Image action
  //
  def drawAsImage(config: BasePlaygroundConfiguration, mode: Mode): Unit = {
    dom.window.document.body.style.backgroundColor = "black"

    val t = "Snapshot - Shogi Playground"
    CanvasBoard(config, mode).processPNGData { data =>
      val elem = a(attr("download") := "snapshot.png", title := t, href := data, img(alt := t, src := data))
      dom.window.document.body.innerHTML = elem.toString
    }
  }

}
