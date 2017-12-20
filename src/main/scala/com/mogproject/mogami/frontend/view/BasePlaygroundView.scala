package com.mogproject.mogami.frontend.view

import com.mogproject.mogami._
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.core.state.State.{BoardType, HandType}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.action.{ChangeModeAction, RefreshScreenAction, UpdateGameControlAction}
import com.mogproject.mogami.frontend.api.Clipboard
import com.mogproject.mogami.frontend.api.Clipboard.Event
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.model.analyze.AnalyzeResult
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.model.board.cursor.Cursor
import com.mogproject.mogami.frontend.sam.{PlaygroundSAM, SAMView}
import com.mogproject.mogami.frontend.view.board.SVGAreaLayout
import com.mogproject.mogami.frontend.view.bootstrap.Tooltip
import com.mogproject.mogami.frontend.view.menu.MenuPane
import com.mogproject.mogami.frontend.view.modal._
import org.scalajs.dom
import org.scalajs.dom.{Element, UIEvent}

import scalatags.JsDom.all._

/**
  *
  */
trait BasePlaygroundView extends SAMView {

  def rootElem: Element

  def website: PlaygroundSite

  def mainPane: MainPaneLike = website.mainPane

  def menuPane: MenuPane = website.menuPane

  override def initialize(): Unit = {
    // create elements
    rootElem.appendChild(website.element)


    // add rotation detection
    dom.window.addEventListener("orientationchange", (_: UIEvent) => PlaygroundSAM.doAction(RefreshScreenAction))

    // initialize clipboard.js
    val cp = new Clipboard(".btn")
    cp.on("success", (e: Event) => Tooltip.display(e.trigger, "Copied!"))
    cp.on("error", (e: Event) => Tooltip.display(e.trigger, "Failed!"))

  }

  def renderLayout(numAreas: Int, pieceWidth: Option[Int], layout: SVGAreaLayout): Unit = {
    mainPane.renderSVGAreas(numAreas, pieceWidth, layout)
  }

  def renderSize(pieceWidth: Option[Int], layout: SVGAreaLayout): Unit = {
    mainPane.resizeSVGAreas(pieceWidth, layout)
  }

  def renderIndex(useJapanese: Boolean = false): Unit = {
    mainPane.updateSVGArea(_.board.drawIndexes(useJapanese))
  }

  def renderFlip(flipType: FlipType): Unit = {
    flipType match {
      case FlipDisabled => mainPane.updateSVGArea(_.setFlip(false))
      case FlipEnabled => mainPane.updateSVGArea(_.setFlip(true))
      case DoubleBoard => Seq(0, 1).foreach { n => mainPane.updateSVGArea(n, _.setFlip(n == 1)) }
    }
  }

  def renderPlayerNames(playerNames: Map[Player, String], messageLang: Language): Unit = {
    mainPane.updateSVGArea(_.player.drawNames(
      playerNames.getOrElse(BLACK, getDefaultPlayerName(BLACK, messageLang)),
      playerNames.getOrElse(WHITE, getDefaultPlayerName(WHITE, messageLang))
    ))
  }

  private[this] def getDefaultPlayerName(player: Player, messageLang: Language): String = {
    (player, messageLang) match {
      case (BLACK, Japanese) => "先手"
      case (WHITE, Japanese) => "後手"
      case (BLACK, English) => "Black"
      case (WHITE, English) => "White"
    }
  }

  def renderIndicators(indicators: Map[Player, BoardIndicator]): Unit = {
    mainPane.updateSVGArea(_.player.drawIndicators(indicators.get(BLACK), indicators.get(WHITE)))
  }

  def renderBox(enabled: Boolean): Unit = {
    mainPane.updateSVGArea(enabled.fold(_.showBox(), _.hideBox()))
  }

  def renderBoardPieces(board: BoardType, pieceFace: PieceFace): Unit = {
    mainPane.updateSVGArea(_.board.drawPieces(board, pieceFace, keepLastMove = false))
  }

  def renderHandPieces(hand: HandType, pieceFace: PieceFace): Unit = {
    mainPane.updateSVGArea(_.hand.drawPieces(hand, pieceFace, keepLastMove = false))
  }

  def renderLastMove(lastMove: Option[Move]): Unit = {
    mainPane.updateSVGArea(_.drawLastMove(lastMove))
  }

  def renderBoxPieces(pieces: Map[Ptype, Int], pieceFace: PieceFace): Unit = {
    mainPane.updateSVGArea(_.box.drawPieces(pieces, pieceFace, keepLastMove = false))
  }

  def renderActiveCursor(activeCursor: Option[(Int, Cursor)]): Unit = {
    // clear current active cursor
    mainPane.updateSVGArea(_.clearActiveCursor())

    // draw new cursor
    activeCursor match {
      case Some((n, c)) => mainPane.updateSVGArea(n, _.drawCursor(c))
      case None => // do nothing
    }
  }

  def renderSelectedCursor(selectedCursor: Option[Cursor], effectEnabled: Boolean, legalMoves: Set[Square]): Unit = {
    // clear current selected cursor
    mainPane.updateSVGArea(_.unselect())

    // draw new cursor
    selectedCursor.foreach { c => mainPane.updateSVGArea(_.select(c, effectEnabled, legalMoves)) }
  }

  def renderMoveEffect(move: Move, pieceFace: PieceFace, visualEffectEnabled: Boolean, soundEffectEnabled: Boolean): Unit = {
    if (soundEffectEnabled) mainPane.playClickSound()
    if (visualEffectEnabled) {
      mainPane.updateSVGArea(a => a.board.effect.moveEffector.start(a.board.getRect(move.to)))
      if (move.promote) mainPane.updateSVGArea(_.board.startPromotionEffect(move.to, move.oldPiece, pieceFace))
    }
  }

  def renderForward(isForward: Boolean): Unit = {
    mainPane.updateSVGArea(_.board.effect.forwardEffector.start(isForward))
  }

  //
  // Controls
  //
  def renderControlBars(gameControl: Option[GameControl], recordLang: Language): Unit = {
    mainPane.updateControlBars(_.refresh(gameControl, recordLang))
  }

  def renderComment(modeType: ModeType, comment: String): Unit = {
    mainPane.updateComment(modeType, comment)
  }

  def renderBranchArea(gameControl: Option[GameControl], recordLang: Language, modeType: ModeType, newBranchMode: Boolean): Unit = {
    mainPane.updateBranchArea(gameControl, recordLang, modeType, newBranchMode)
  }

  //
  // Menu
  //
  def updateModeType(modeType: ModeType): Unit = {
    menuPane.accordions.foreach(_.refresh(modeType))
    mainPane.updateModeType(modeType)
  }

  def updateConfigMenu(config: BasePlaygroundConfiguration): Unit = {
    website.settingMenu.refresh(config)
  }

  def renderCheckmateAnalyzeResult(result: AnalyzeResult): Unit = {
    // todo: notify to the menu dialog


  }

  //
  // Dialogs
  //
  def askPromote(messageLang: Language, pieceFace: PieceFace, pieceSize: Coord, rawMove: Move, rotate: Boolean): Unit = {
    PromotionDialog(messageLang, pieceFace, pieceSize, rawMove, rotate).show()
  }

  def showGameInfoDialog(messageLang: Language, gameInfo: GameInfo): Unit = {
    GameInfoDialog(messageLang, gameInfo).show()
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

}
