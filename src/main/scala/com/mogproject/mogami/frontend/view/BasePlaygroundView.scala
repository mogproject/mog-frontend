package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.{Move, Player, Ptype, Square}
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.core.state.State.{BoardType, HandType}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.model.board.cursor.Cursor
import com.mogproject.mogami.frontend.sam.SAMView
import com.mogproject.mogami.frontend.view.board.SVGAreaLayout
import com.mogproject.mogami.frontend.view.piece.PieceFace
import org.scalajs.dom.Element

/**
  *
  */
trait BasePlaygroundView extends SAMView {

  def rootElem: Element

  def mainPane: MainPaneLike

  override def initialize(): Unit = {
    rootElem.appendChild(mainPane.element)
  }

  def renderLayout(numAreas: Int, layout: SVGAreaLayout): Unit = {
    mainPane.renderSVGAreas(numAreas, layout)
  }

  def renderSize(pieceWidth: Int): Unit = {
    mainPane.updateSVGArea(_.resize(pieceWidth))
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
      mainPane.updateSVGArea( a => a.board.effect.moveEffector.start(a.board.getRect(move.to)))
      if (move.promote) mainPane.updateSVGArea(_.board.startPromotionEffect(move.to, move.oldPiece, pieceFace))
    }
  }
}
