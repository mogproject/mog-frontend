package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.{Player, Ptype}
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.core.state.State.{BoardType, HandType}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.sam.SAMView
import com.mogproject.mogami.frontend.view.board.{Cursor, SVGAreaLayout, SVGStandardLayout}
import com.mogproject.mogami.frontend.view.piece.PieceFace
import org.scalajs.dom.Element

/**
  *
  */
case class TestView(rootElem: Element) extends SAMView {

  val mainPane = TestMainPane()

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

  def renderPlayerNames(playerNames: Map[Player, String]): Unit = {
    mainPane.updateSVGArea(_.player.drawNames(playerNames.get(BLACK), playerNames.get(WHITE)))
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

  def renderSelectedCursor(selectedCursor: Option[Cursor], effectEnabled: Boolean): Unit = {
    // clear current selected cursor
    mainPane.updateSVGArea(_.unselect())

    // draw new cursor
    selectedCursor.foreach { c => mainPane.updateSVGArea(_.select(c, effectEnabled))}
  }

}
