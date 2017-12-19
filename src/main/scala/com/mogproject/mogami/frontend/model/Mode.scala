package com.mogproject.mogami.frontend.model

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.board.BoardIndicator
import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.util.MapUtil

/**
  *
  */
sealed abstract class Mode(val modeType: ModeType,
                           val playable: Set[Player],
                           val playerSelectable: Boolean,
                           val forwardAvailable: Boolean,
                           val boxAvailable: Boolean) {
  def boardCursorAvailable: Boolean = playable.nonEmpty

  //
  // Getters
  //
  def isViewMode: Boolean = modeType == ViewModeType

  def isEditMode: Boolean = modeType == EditModeType

  def canActivate(cursor: Cursor): Boolean = cursor match {
    case BoardCursor(_) => boardCursorAvailable
    case HandCursor(_) => boardCursorAvailable
    case PlayerCursor(_) => playerSelectable
    case BoxCursor(_) => boxAvailable
    case _ => false
  }

  def canSelect(cursor: Cursor): Boolean = {
    if (getGameControl.exists(_.getDisplayingGameStatus != GameStatus.Playing)) {
      false
    } else {
      cursor match {
        case BoardCursor(sq) => getBoardPieces.get(sq).exists(p => (isEditMode || p.owner == getTurn) && playable(p.owner))
        case HandCursor(h) => (isEditMode || h.owner == getTurn) && playable(h.owner) && getHandPieces.get(h).exists(_ > 0)
        case BoxCursor(pt) => boxAvailable && getBoxPieces.get(pt).exists(_ > 0)
        case _ => false
      }
    }
  }

  def isPrevious(mode: Mode): Boolean = (this, mode) match {
    case (ViewMode(a), ViewMode(b)) => a.displayPosition == b.displayPosition - 1
    case _ => false
  }

  def isNext(mode: Mode): Boolean = (this, mode) match {
    case (ViewMode(a), ViewMode(b)) => a.displayPosition == b.displayPosition + 1
    case _ => false
  }

  def isJustMoved(mode: Mode): Boolean = this match {
    case EditMode(_, _, b, h) => b != mode.getBoardPieces || h != mode.getHandPieces
    case _ =>
      (getGameControl, mode.getGameControl) match {
        case (Some(a), Some(b)) => a.statusPosition == b.statusPosition + 1
        case _ => false
      }
  }

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

  def getTurn: Player = this match {
    case PlayMode(gc) => gc.getDisplayingState.turn
    case ViewMode(gc) => gc.getDisplayingState.turn
    case LiveMode(_, gc) => gc.getDisplayingState.turn
    case EditMode(_, t, _, _) => t
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
      val a = b.values.map(_.ptype.demoted).foldLeft(Map.empty[Ptype, Int]) { case (m, pt) => MapUtil.incrementMap(m, pt) }
      val used = h.foldLeft(a) { case (m, (h, n)) => m.updated(h.ptype, m.getOrElse(h.ptype, 0) + n) }
      MapUtil.mergeMaps(State.capacity, used)(_ - _, 0).mapValues(math.max(0, _))
    case _ => Map.empty
  }

  def getGameControl: Option[GameControl] = this match {
    case PlayMode(gc) => Some(gc)
    case ViewMode(gc) => Some(gc)
    case LiveMode(_, gc) => Some(gc)
    case EditMode(_, _, _, _) => None
  }

  def getLegalMoves(moveFrom: MoveFrom): Set[Square] = {
    (for {
      gc <- getGameControl
      st = gc.getDisplayingState
      bb <- st.legalMovesBB.get(moveFrom)
    } yield {
      bb.toSet
    }).getOrElse(Set.empty)
  }

  def getLastMove: Option[Move] = getGameControl.flatMap(_.getDisplayingLastMove)


  //
  // Setters
  //
  def setGameControl(gameControl: GameControl): Mode = this match {
    case x@PlayMode(_) => x.copy(gameControl = gameControl)
    case x@ViewMode(_) => x.copy(gameControl = gameControl)
    case x@LiveMode(_, _) => x.copy(gameControl = gameControl)
    case EditMode(_, _, _, _) => this
  }

  def updateGameControl(f: GameControl => GameControl): Option[Mode] = getGameControl.map(gc => setGameControl(f(gc)))
}

case class PlayMode(gameControl: GameControl) extends Mode(PlayModeType, Player.constructor.toSet, true, false, false) {

}

case class ViewMode(gameControl: GameControl) extends Mode(ViewModeType, Set.empty, true, true, false) {

}

case class EditMode(gameInfo: GameInfo, turn: Player, board: BoardType, hand: HandType) extends Mode(EditModeType, Player.constructor.toSet, true, false, true) {

}

case class LiveMode(player: Option[Player], gameControl: GameControl) extends Mode(LiveModeType, player.toSet, false, false, false) {

}
