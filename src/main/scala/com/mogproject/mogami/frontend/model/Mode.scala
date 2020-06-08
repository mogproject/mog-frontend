package com.mogproject.mogami.frontend.model

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.model.board.BoardIndicator
import com.mogproject.mogami.frontend.model.board.cursor._
import com.mogproject.mogami.frontend.util.PlayerUtil
import com.mogproject.mogami.util.MapUtil
import com.mogproject.mogami.util.Implicits._

/**
  *
  */
sealed abstract class Mode(val modeType: ModeType,
                           val playerSelectable: Boolean,
                           val boxAvailable: Boolean,
                           isHandicappedHint: Option[Boolean]) {
  def playable: Set[Player]

  def forwardAvailable: Boolean

  def boardCursorAvailable: Boolean = playable.nonEmpty && (!isLiveMode || isLivePlaying)

  //
  // Getters
  //
  def isViewMode: Boolean = modeType == ViewModeType

  def isEditMode: Boolean = modeType == EditModeType

  def isLiveMode: Boolean = modeType == LiveModeType

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

  /**
    * Checks if the player can make move or resign.
    *
    * @return true is one can make move
    */
  def canMakeMove: Boolean = {
    getGameControl.exists(_.getDisplayingGameStatus == GameStatus.Playing) && playable.contains(getTurn)
  }

  def isLivePlaying: Boolean = this match {
    case LiveMode(Some(_), _, gc, _) => gc.game.trunk.status == GameStatus.Playing
    case _ => false
  }

  def isPrevious(mode: Mode): Boolean = (this, mode) match {
    case (ViewMode(a, _), ViewMode(b, _)) => a.displayPosition == b.displayPosition - 1
    case (LiveMode(_, _, a, _), LiveMode(_, _, b, _)) => a.displayPosition == b.displayPosition - 1
    case _ => false
  }

  def isNext(mode: Mode): Boolean = (this, mode) match {
    case (ViewMode(a, _), ViewMode(b, _)) => a.displayPosition == b.displayPosition + 1
    case (LiveMode(_, _, a, _), LiveMode(_, _, b, _)) => a.displayPosition == b.displayPosition + 1
    case _ => false
  }

  def isJustMoved(mode: Mode): Boolean = this match {
    case EditMode(_, _, b, h, _) => b != mode.getBoardPieces || h != mode.getHandPieces
    case _ =>
      (getGameControl, mode.getGameControl) match {
        case (Some(a), Some(b)) =>
          a.statusPosition == b.statusPosition + 1 || (a.isIllegalMove
            && a.displayPosition == a.lastDisplayPosition - 1
            && a.displayPosition == b.displayPosition + 1)
        case _ => false
      }
  }

  def isOnline(player: Player): Boolean = this match {
    case LiveMode(_, onl, _, _) => onl.getOrElse(player, true)
    case _ => true
  }

  def getPlayerNames: Map[Player, String] = {
    val g = this match {
      case PlayMode(gc, _) => gc.game.gameInfo
      case ViewMode(gc, _) => gc.game.gameInfo
      case LiveMode(_, _, gc, _) => gc.game.gameInfo
      case EditMode(gi, _, _, _, _) => gi
    }
    PlayerUtil.getPlayerNames(g)
  }

  def getIndicators: Map[Player, BoardIndicator] = {
    val status = getGameControl.map(_.getDisplayingGameStatus).getOrElse(GameStatus.Playing)
    BoardIndicator.fromGameStatus(getTurn, status)
  }

  def getTurn: Player = this match {
    case PlayMode(gc, _) => gc.getDisplayingTurn
    case ViewMode(gc, _) => gc.getDisplayingTurn
    case LiveMode(_, _, gc, _) => gc.getDisplayingTurn
    case EditMode(_, t, _, _, _) => t
  }

  def getBoardPieces: BoardType = this match {
    case PlayMode(gc, _) => gc.getDisplayingBoard
    case ViewMode(gc, _) => gc.getDisplayingBoard
    case LiveMode(_, _, gc, _) => gc.getDisplayingBoard
    case EditMode(_, _, b, _, _) => b
  }

  def getHandPieces: HandType = this match {
    case PlayMode(gc, _) => gc.getDisplayingHand
    case ViewMode(gc, _) => gc.getDisplayingHand
    case LiveMode(_, _, gc, _) => gc.getDisplayingHand
    case EditMode(_, _, _, h, _) => h
  }

  lazy val getBoxPieces: Map[Ptype, Int] = {
    val b = getBoardPieces
    val h = getHandPieces

    val a = b.values.map(_.ptype.demoted).foldLeft(Map.empty[Ptype, Int]) { case (m, pt) => MapUtil.incrementMap(m, pt) }
    val used = h.foldLeft(a) { case (m, (h, n)) => m.updated(h.ptype, m.getOrElse(h.ptype, 0) + n) }
    MapUtil.mergeMaps(State.capacity, used)(_ - _, 0).view.mapValues(math.max(0, _)).toMap
  }

  def getGameControl: Option[GameControl] = this match {
    case PlayMode(gc, _) => Some(gc)
    case ViewMode(gc, _) => Some(gc)
    case LiveMode(_, _, gc, _) => Some(gc)
    case EditMode(_, _, _, _, _) => None
  }

  def getGameInfo: GameInfo = this match {
    case PlayMode(gc, _) => gc.game.gameInfo
    case ViewMode(gc, _) => gc.game.gameInfo
    case LiveMode(_, _, gc, _) => gc.game.gameInfo
    case EditMode(gi, _, _, _, _) => gi
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

  lazy val isHandicapped: Boolean = isHandicappedHint.getOrElse {
    val bp = getBoxPieces.filter(_._2 > 0)
    bp.nonEmpty && !bp.contains(KING)
  }

  //
  // Setters
  //
  def setGameControl(gameControl: GameControl): Mode = this match {
    case x@PlayMode(_, _) => x.copy(gameControl = gameControl, isHandicappedHint = Some(isHandicapped))
    case x@ViewMode(_, _) => x.copy(gameControl = gameControl, isHandicappedHint = Some(isHandicapped))
    case x@LiveMode(_, _, _, _) => x.copy(gameControl = gameControl, isHandicappedHint = Some(isHandicapped))
    case EditMode(_, _, _, _, _) => this
  }

  def updateGameControl(f: GameControl => GameControl): Option[Mode] = getGameControl.map(gc => setGameControl(f(gc)))
}

case class PlayMode(gameControl: GameControl, isHandicappedHint: Option[Boolean])
  extends Mode(PlayModeType, true, false, isHandicappedHint) {
  override lazy val playable: Set[Player] = Player.constructor.toSet
  override lazy val forwardAvailable = false
}

case class ViewMode(gameControl: GameControl, isHandicappedHint: Option[Boolean])
  extends Mode(ViewModeType, true, false, isHandicappedHint) {
  override lazy val playable: Set[Player] = Set.empty
  override lazy val forwardAvailable = true
}

case class EditMode(gameInfo: GameInfo, turn: Player, board: BoardType, hand: HandType, isHandicappedHint: Option[Boolean])
  extends Mode(EditModeType, true, true, isHandicappedHint) {
  override lazy val playable: Set[Player] = Player.constructor.toSet
  override lazy val forwardAvailable = false
}

case class LiveMode(player: Option[Player], online: Map[Player, Boolean], gameControl: GameControl, isHandicappedHint: Option[Boolean])
  extends Mode(LiveModeType, false, false, isHandicappedHint) {
  override def forwardAvailable = !isLivePlaying

  override def playable: Set[Player] = isLivePlaying.fold(player.toSet, Set.empty)
}
