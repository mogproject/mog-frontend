package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.core.move.IllegalMove
import com.mogproject.mogami.core.state.State.PromotionFlag
import com.mogproject.mogami.frontend.model.io.{CSA, KI2, KIF, RecordFormat}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{GamePosition, _}
import org.scalajs.dom

/**
  * Manages a game and its display position.
  *
  * @param game            game
  * @param displayBranchNo branch number to display
  * @param displayPosition display position
  *                        0 - initial position (= trunk's offset)
  *                        1~n - position after n-th move (when the branch has n moves)
  *                        n+1 - additional position(1): game end status (e.g. Mated, Resigned, Drawn) or an illegal move
  *                        n+2 - additional position(2): illegal move status
  */
case class GameControl(game: Game, displayBranchNo: BranchNo = 0, displayPosition: Int = 0) {
  val displayBranch: Branch = game.getBranch(displayBranchNo).getOrElse(
    throw new RuntimeException(s"failed to select branch: ${displayBranchNo}")
  )

  /**
    * This may include the moves of both the trunk and the displaying branch
    */
  lazy val displayMoves: Vector[Move] = game.getAllMoves(displayBranchNo)

  private[this] def getLastStatusPosition(branchNo: BranchNo): Int = {
    val br = game.getBranch(branchNo).getOrElse(game.trunk)
    br.moves.length + (displayBranchNo == 0).fold(0, br.offset - game.trunk.offset)
  }

  private[this] def getLastDisplayPosition(branchNo: BranchNo): Int = {
    val br = game.getBranch(branchNo).getOrElse(game.trunk)
    getLastStatusPosition(branchNo) + (br.status match {
      case GameStatus.IllegallyMoved => 2
      case GameStatus.Playing => 0
      case _ => 1
    })
  }

  val lastStatusPosition: Int = getLastStatusPosition(displayBranchNo)

  val lastDisplayPosition: Int = getLastDisplayPosition(displayBranchNo)

  val statusPosition: Int = math.min(displayPosition, lastStatusPosition)

  def gamePosition: GamePosition = GamePosition(displayBranchNo, statusPosition + game.trunk.offset)

  /** If the position is before the offset of a branch, actually you are seeing the trunk. */
  def effectiveBranchNo: BranchNo = (statusPosition < displayBranch.offset).fold(0, displayBranchNo)

  //
  // predicates
  //
  def isAdditionalPosition: Boolean = displayPosition > lastStatusPosition

  def isLastStatusPosition: Boolean = displayPosition >= lastStatusPosition

  def isFirstDisplayPosition: Boolean = displayPosition == 0

  def isFirstStatusPosition: Boolean = statusPosition == 0

  def isLastDisplayPosition: Boolean = displayPosition == lastDisplayPosition

  def isInitialState: Boolean = game.trunk.moves.isEmpty && game.branches.isEmpty && game.comments.isEmpty

  //
  // getters
  //
  def getDisplayingState: State = game.getState(gamePosition).getOrElse(throw new RuntimeException(s"Failed to get state: ${gamePosition}"))

  def isIllegalMove: Boolean = getDisplayingIllegalMove.isDefined && isAdditionalPosition

  def getDisplayingTurn: Player = isIllegalMove.when[Player](!_)(getDisplayingState.turn)

  def getDisplayingBoard: BoardType = getDisplayingIllegalMove.map(mv => getDisplayingState.makeNextPosition(mv.move)._1).getOrElse(getDisplayingState.board)

  def getDisplayingHand: HandType = getDisplayingIllegalMove.map(mv => getDisplayingState.makeNextPosition(mv.move)._2).getOrElse(getDisplayingState.hand)

  /** last status position => cannot move */
  private[this] val finalizedGameStatus = Seq(GameStatus.Mated, GameStatus.Uchifuzume, GameStatus.PerpetualCheck, GameStatus.Drawn)

  /**
    * Get the game status of a displaying position.
    *
    * @return
    */
  def getDisplayingGameStatus: GameStatus = {
    if (isAdditionalPosition || (isLastStatusPosition && finalizedGameStatus.contains(displayBranch.status))) {
      displayBranch.status
    } else {
      GameStatus.Playing
    }
  }

  lazy val getDisplayingIllegalMove: Option[IllegalMove] = (isAdditionalPosition, displayBranch.finalAction) match {
    case (true, Some(x@IllegalMove(_))) => Some(x)
    case _ => None
  }

  def getDisplayingLastMove: Option[Move] = {
    (getDisplayingIllegalMove, isFirstStatusPosition) match {
      case (Some(IllegalMove(mv)), _) => Some(mv)
      case (_, false) => Some(displayMoves(statusPosition - 1))
      case (_, true) => None
    }
  }

  def getDisplayingLastMoveTo: Option[Square] = getDisplayingLastMove.map(_.to)

  def getComment: Option[String] = game.getComment(gamePosition)

  //
  // setters
  //
  def withFirstDisplayPosition: GameControl = copy(displayPosition = 0)

  def withPreviousDisplayPosition: GameControl = copy(displayPosition = math.max(0, displayPosition - 1))

  def withNextDisplayPosition: GameControl = copy(displayPosition = math.min(lastDisplayPosition, displayPosition + 1))

  def withLastDisplayPosition: GameControl = copy(displayPosition = lastDisplayPosition)

  def changeDisplayBranch(branchNo: BranchNo): GameControl = {
    copy(displayBranchNo = branchNo, displayPosition = math.min(getLastDisplayPosition(branchNo), displayPosition))
  }

  def deleteBranch(branchNo: BranchNo): GameControl = {
    // if a displaying branch is deleted, change to the trunk
    val gc = if (branchNo == displayBranchNo) changeDisplayBranch(0) else this
    game.deleteBranch(branchNo).map(g => gc.copy(game = g)).getOrElse(this)
  }

  //
  // move operations
  //
  def getMoveCandidates(moveFrom: MoveFrom, moveTo: Square): Seq[Move] = {
    val st = getDisplayingState
    val promotes = if (st.canAttack(moveFrom, moveTo)) {
      st.getPromotionFlag(moveFrom, moveTo) match {
        case Some(PromotionFlag.CannotPromote) => Seq(false)
        case Some(PromotionFlag.CanPromote) => Seq(false, true)
        case Some(PromotionFlag.MustPromote) => Seq(true)
        case None => Seq.empty
      }
    } else {
      Seq.empty
    }
    promotes.flatMap(p => MoveBuilderSfen(moveFrom, moveTo, promote = p).toMove(st, getDisplayingLastMoveTo).toSeq)
  }

  def makeMove(move: Move, newBranchMode: Boolean, moveForward: Boolean): Option[GameControl] = {
    val offset = moveForward.fold(1, 0)
    if (newBranchMode) {
      // New Branch Mode
      /** @note compare moves regardless of elapsed time */
      game.getMove(gamePosition).map(_.dropElapsedTime) match {
        case Some(m) if m == move => Some(this.copy(displayPosition = displayPosition + offset)) // move next
        case Some(_) => game.getForks(gamePosition).find(_._1 == move) match {
          case Some((_, br)) => moveToBranch(br, offset)
          case None => createNewBranch(move, offset)
        }
        case None => makeMoveOnCurrentBranch(move, offset)
      }
    } else {
      makeMoveOnCurrentBranch(move, offset) // Normal Mode
    }
  }

  private[this] def moveToBranch(branchNo: BranchNo, offset: Int): Option[GameControl] = {
    Some(this.copy(displayBranchNo = branchNo, displayPosition = displayPosition + offset))
  }

  private[this] def createNewBranch(move: Move, offset: Int): Option[GameControl] = {
    game.createBranch(gamePosition, move).map(g => this.copy(game = g, displayBranchNo = game.branches.length + 1, displayPosition = displayPosition + offset))
  }

  private[this] def makeMoveOnCurrentBranch(move: Move, offset: Int): Option[GameControl] = {
    game.truncated(gamePosition).updateBranch(displayBranchNo)(_.makeMove(move)).map { g =>
      this.copy(game = g, displayPosition = displayPosition + offset)
    }
  }

  def makeSpecialMove(specialMove: SpecialMove, moveForward: Boolean): Option[GameControl] = {
    game
      .truncated(GamePosition(effectiveBranchNo, statusPosition))
      .updateBranch(effectiveBranchNo)(b => Some(b.updateFinalAction(Some(specialMove))))
      .map { g => this.copy(game = g, displayBranchNo = effectiveBranchNo, displayPosition = statusPosition + moveForward.fold(1, 0)) }
  }

  def getRecord(format: RecordFormat): String = format match {
    case CSA => game.toCsaString
    case KIF => game.toKifString
    case KI2 => game.toKi2String
  }
}
