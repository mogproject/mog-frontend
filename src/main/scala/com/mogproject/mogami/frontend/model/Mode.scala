package com.mogproject.mogami.frontend.model

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.board.BoardIndicator
import com.mogproject.mogami.frontend.view.board._
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
  def isEditMode: Boolean = boxAvailable

  def canActivate(cursor: Cursor): Boolean = cursor match {
    case BoardCursor(_) => boardCursorAvailable
    case HandCursor(_) => boardCursorAvailable
    case PlayerCursor(_) => playerSelectable
    case BoxCursor(_) => boxAvailable
    case _ => false
  }

  def canSelect(cursor: Cursor): Boolean = cursor match {
    case BoardCursor(sq) => getBoardPieces.get(sq).exists(p => (isEditMode || p.owner == getTurn) && playable(p.owner))
    case HandCursor(h) => (isEditMode || h.owner == getTurn) && playable(h.owner) && getHandPieces.get(h).exists(_ > 0)
    case BoxCursor(pt) => boxAvailable && getBoxPieces.get(pt).exists(_ > 0)
    case _ => false
  }

  def getPlayerNames: Map[Player, String] = {
    val tags = (this match {
      case PlayMode(gc, _) => gc.game.gameInfo
      case ViewMode(gc) => gc.game.gameInfo
      case LiveMode(_, gc) => gc.game.gameInfo
      case EditMode(gi, _, _, _) => gi
    }).tags
    (tags.get('blackName).map(BLACK -> _) ++ tags.get('whiteName).map(WHITE -> _)).toMap
  }

  def getIndicators: Map[Player, BoardIndicator] = {
    val (turn, gs) = this match {
      case PlayMode(gc, _) => (gc.getDisplayingState.turn, gc.getDisplayingGameStatus)
      case ViewMode(gc) => (gc.getDisplayingState.turn, gc.getDisplayingGameStatus)
      case LiveMode(_, gc) => (gc.getDisplayingState.turn, gc.getDisplayingGameStatus)
      case EditMode(_, t, _, _) => (t, GameStatus.Playing)
    }
    BoardIndicator.fromGameStatus(turn, gs)
  }

  def getTurn: Player = this match {
    case PlayMode(gc, _) => gc.getDisplayingState.turn
    case ViewMode(gc) => gc.getDisplayingState.turn
    case LiveMode(_, gc) => gc.getDisplayingState.turn
    case EditMode(_, t, _, _) => t
  }

  def getBoardPieces: BoardType = this match {
    case PlayMode(gc, _) => gc.getDisplayingState.board
    case ViewMode(gc) => gc.getDisplayingState.board
    case LiveMode(_, gc) => gc.getDisplayingState.board
    case EditMode(_, _, b, _) => b
  }

  def getHandPieces: HandType = this match {
    case PlayMode(gc, _) => gc.getDisplayingState.hand
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

  def getGameControl: Option[GameControl] = this match {
    case PlayMode(gc, _) => Some(gc)
    case ViewMode(gc) => Some(gc)
    case LiveMode(_, gc) => Some(gc)
    case EditMode(_, _, _, _) => None
  }

  def getAttackSquares(moveFrom: MoveFrom): Set[Square] = {
    val bb = for {
      gc <- getGameControl
      st = gc.getDisplayingState
    } yield {
      moveFrom match {
        case Left(sq) => st.attackBBOnBoard(st.turn).get(sq).map(_ & ~st.occupancy(st.turn)).getOrElse(BitBoard.empty)
        case Right(h) => st.attackBBInHand(h)
      }
    }
    bb.map(_.toSet) getOrElse (Set.empty)
  }

  //
  // Setters
  //
  def setGameControl(gameControl: GameControl): Mode = this match {
    case x@PlayMode(_, _) => x.copy(gameControl = gameControl)
    case x@ViewMode(_) => x.copy(gameControl = gameControl)
    case x@LiveMode(_, _) => x.copy(gameControl = gameControl)
    case EditMode(_, _, _, _) => this
  }
}

case class PlayMode(gameControl: GameControl, newBranchMode: Boolean = false) extends Mode(Player.constructor.toSet, true, false, false) {

}

case class ViewMode(gameControl: GameControl) extends Mode(Set.empty, true, true, false) {

}

case class EditMode(gameInfo: GameInfo, turn: Player, board: BoardType, hand: HandType) extends Mode(Player.constructor.toSet, true, false, true) {

}

case class LiveMode(player: Option[Player], gameControl: GameControl) extends Mode(player.toSet, false, false, false) {

}
