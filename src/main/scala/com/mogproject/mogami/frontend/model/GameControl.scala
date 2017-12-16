package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.core.move.IllegalMove
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.{GamePosition, _}

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

  val lastStatusPosition: Int = (displayBranchNo == 0).fold(game.trunk.moves.length, displayBranch.offset - game.trunk.offset + displayBranch.moves.length)

  val lastDisplayPosition: Int = lastStatusPosition + (displayBranch.status match {
    case GameStatus.IllegallyMoved => 2
    case GameStatus.Playing => 0
    case _ => 1
  })

  val statusPosition: Int = math.min(displayPosition, lastStatusPosition)

  def gamePosition: GamePosition = GamePosition(displayBranchNo, statusPosition + game.trunk.offset)

  //
  // predicates
  //
  def isAdditionalPosition: Boolean = displayPosition > lastStatusPosition

  def isFirstDisplayPosition: Boolean = displayPosition == 0

  def isFirstStatusPosition: Boolean = statusPosition == 0

  def isLastDisplayPosition: Boolean = displayPosition == lastDisplayPosition

  //
  // getters
  //
  def getDisplayingState: State = game.getState(gamePosition).get

  def getDisplayingGameStatus: GameStatus = isAdditionalPosition.fold(displayBranch.status, GameStatus.Playing)

  def getDisplayingIllegalMove: Option[IllegalMove] = (isAdditionalPosition, displayBranch.finalAction) match {
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

  def getPlayerName(player: Player): String = game.gameInfo.tags.getOrElse(player.isBlack.fold('blackName, 'whiteName), "")

  //
  // setters
  //
  def withFirstDisplayPosition: GameControl = copy(displayPosition = 0)

  def withPreviousDisplayPosition: GameControl = copy(displayPosition = math.max(0, displayPosition - 1))

  def withNextDisplayPosition: GameControl = copy(displayPosition = math.min(lastDisplayPosition, displayPosition + 1))

  def withLastDisplayPosition: GameControl = copy(displayPosition = lastDisplayPosition)
}
