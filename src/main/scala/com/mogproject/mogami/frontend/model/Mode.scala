package com.mogproject.mogami.frontend.model

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.board.BoardIndicator
import com.mogproject.mogami.util.MapUtil

import scala.util.Try

/**
  *
  */
sealed abstract class Mode(val playable: Set[Player],
                           val playerSelectable: Boolean,
                           val forwardAvailable: Boolean,
                           val boxAvailable: Boolean) {
  def boardCursorAvailable: Boolean = playable.nonEmpty

  //
  // Getters
  //
  def getPlayerNames: Map[Player, String] = {
    val tags = (this match {
      case PlayMode(gc) => gc.game.gameInfo
      case ViewMode(gc) => gc.game.gameInfo
      case LiveMode(_, gc) => gc.game.gameInfo
      case EditMode(gi, _, _, _) => gi
    }).tags
    (tags.get('blackName).map(BLACK -> _) ++ tags.get('whiteName).map(WHITE -> _)).toMap
  }

  def getIndicators: Map[Player, BoardIndicator] = {
    val (turn, gs) = this match {
      case PlayMode(gc) => (gc.getDisplayingState.turn, gc.getDisplayingGameStatus)
      case ViewMode(gc) => (gc.getDisplayingState.turn, gc.getDisplayingGameStatus)
      case LiveMode(_, gc) => (gc.getDisplayingState.turn, gc.getDisplayingGameStatus)
      case EditMode(_, t, _, _) => (t, GameStatus.Playing)
    }
    BoardIndicator.fromGameStatus(turn, gs)
  }

  def getBoardPieces: BoardType = this match {
    case PlayMode(gc) => gc.getDisplayingState.board
    case ViewMode(gc) => gc.getDisplayingState.board
    case LiveMode(_, gc) => gc.getDisplayingState.board
    case EditMode(_, _, b, _) => b
  }

  def getHandPieces: HandType = this match {
    case PlayMode(gc) => gc.getDisplayingState.hand
    case ViewMode(gc) => gc.getDisplayingState.hand
    case LiveMode(_, gc) => gc.getDisplayingState.hand
    case EditMode(_, _, _, h) => h
  }

  def getBoxPieces: Map[Ptype, Int] = this match {
    case EditMode(_, _, b, h) =>
      val bb = b.values.groupBy(_.demoted.ptype).mapValues(_.size)
      val hh = h.filter(_._2 > 0).map { case (hd, n) => hd.ptype -> n }
      val used = MapUtil.mergeMaps(bb, hh)(_ + _, 0)
      MapUtil.mergeMaps(State.capacity, used)(_ - _, 0).mapValues(math.max(0, _))
    case _ => Map.empty
  }

}

case class PlayMode(gameControl: GameControl) extends Mode(Player.constructor.toSet, true, false, false) {

}

case class ViewMode(gameControl: GameControl) extends Mode(Set.empty, true, true, false) {

}

case class EditMode(gameInfo: GameInfo, turn: Player, board: BoardType, hand: HandType) extends Mode(Player.constructor.toSet, true, false, true) {

}

case class LiveMode(player: Option[Player], gameControl: GameControl) extends Mode(player.toSet, false, false, false) {

}
