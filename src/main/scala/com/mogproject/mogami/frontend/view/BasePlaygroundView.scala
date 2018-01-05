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
    cp.on("success", (e: Event) => Tooltip.display(e.trigger, "Copied!"))
    cp.on("error", (e: Event) => Tooltip.display(e.trigger, "Failed!"))

  }

  def renderAnalyzeResult(result: AnalyzeResult, messageLang: Language, recordLang: Language): Unit = result match {
    case CheckmateAnalyzeResult(r) => renderCheckmateAnalyzeResult(r, messageLang, recordLang)
    case CountAnalyzeResult(p, k, n) => renderCountAnalyzeResult(p, k, n)
  }

  def renderCheckmateAnalyzeResult(result: Option[Seq[Move]], messageLang: Language, recordLang: Language): Unit = {
    website.analyzeMenu.checkmateButton.displayResult(result, messageLang, recordLang)
  }

  def renderCountAnalyzeResult(point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int): Unit = {
    website.analyzeMenu.pointCountButton.displayResult(point, isKingInPromotionZone, numPiecesInPromotionZone)
  }

  //
  // Dialogs
  //
  def askPromote(messageLang: Language, pieceFace: PieceFace, rawMove: Move, rotate: Boolean): Unit = {
    PromotionDialog(messageLang, pieceFace, rawMove, rotate).show()
  }

  def showGameInfoDialog(messageLang: Language, gameInfo: GameInfo, isHandicapped: Boolean): Unit = {
    GameInfoDialog(messageLang, gameInfo, isHandicapped).show()
  }

  def showEditWarningDialog(messageLang: Language): Unit = {
    val s = messageLang match {
      case Japanese => p("棋譜およびコメントの情報が失われますが、よろしいですか?")
      case English => p("The record and comments will be discarded. Are you sure?")
    }
    val action = ChangeModeAction(EditModeType, confirmed = true)
    YesNoDialog(messageLang, s, () => PlaygroundSAM.doAction(action)).show()
  }

  def askDeleteBranch(messageLang: Language, branchNo: BranchNo): Unit = {
    val s = messageLang match {
      case Japanese => p(s"現在の変化 (Branch#${branchNo}) が削除されます。コメントも失われますが、よろしいですか?")
      case English => p(s"Branch#${branchNo} will be deleted. Comments on this branch will also be removed. Are you sure?")
    }
    val action = UpdateGameControlAction(_.deleteBranch(branchNo))
    YesNoDialog(messageLang, s, () => PlaygroundSAM.doAction(action)).show()
  }

  def showEditAlertDialog(msg: String, messageLang: Language): Unit = {
    val s = messageLang match {
      case Japanese => p("不正な局面です。", br, s"(${msg})")
      case English => p("Invalid state.", br, s"(${msg})")
    }
    AlertDialog(messageLang, s).show()
  }

  def showCommentDialog(messageLang: Language, text: String): Unit = {
    CommentDialog(messageLang, text).show()
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
  def drawAsImage(config: BasePlaygroundConfiguration, gameControl: GameControl): Unit = {
    dom.window.document.body.style.backgroundColor = "black"

    val t = "Snapshot - Shogi Playground"
    CanvasBoard(config, gameControl).processPNGData { data =>
      val elem = a(attr("download") := "snapshot.png", title := t, href := data, img(alt := t, src := data))
      dom.window.document.body.innerHTML = elem.toString
    }
  }

}
