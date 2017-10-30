package com.mogproject.mogami.frontend.model.board

import com.mogproject.mogami.{HandType, Piece, Player}
import com.mogproject.mogami.core.Square
import com.mogproject.mogami.core.state.State.BoardType
import com.mogproject.mogami.frontend.model.board.cursor.CursorEvent
import com.mogproject.mogami.frontend.sam.SAMModel
import com.mogproject.mogami.frontend.view.board.Cursor

/**
  * Board model
  */
case class BoardModel(config: BoardConfiguration = BoardConfiguration(),
                      playerNames: Map[Player, Option[String]] = Player.constructor.map(_ -> None).toMap,
                      onlineStatus: Map[Player, Option[Boolean]] = Player.constructor.map(_ -> None).toMap,
                      indicators: Map[Player, Option[BoardIndicator]] = Player.constructor.map(_ -> None).toMap,
                      isViewMode: Boolean = false,
                      isEditMode: Boolean = false,
                      cursorEnabled: Boolean = true,
                      activeCursor: Option[Cursor] = None,
                      selectedCursor: Option[Cursor] = None,
                      isFlipped: Boolean = false,
                      activeBoard: BoardType = Map.empty,
                      activeHand: HandType = Map.empty,
                      cursorEvent: Option[CursorEvent] = None
                     ) extends SAMModel {

}
