package com.mogproject.mogami.frontend.model.board

import com.mogproject.mogami._
import com.mogproject.mogami.core.state.State.BoardType
import com.mogproject.mogami.frontend.model.{Mode, PlayMode}
import com.mogproject.mogami.frontend.model.board.cursor.CursorEvent
import com.mogproject.mogami.frontend.sam.SAMModel
import com.mogproject.mogami.frontend.view.board.Cursor
import com.mogproject.mogami.util.MapUtil

/**
  * Board model
  */
case class BoardModel(config: BoardConfiguration = BoardConfiguration(),
                      playerNames: Map[Player, Option[String]] = Player.constructor.map(_ -> None).toMap,
                      onlineStatus: Map[Player, Option[Boolean]] = Player.constructor.map(_ -> None).toMap,
                      indicators: Map[Player, Option[BoardIndicator]] = Player.constructor.map(_ -> None).toMap,
                      mode: Mode = PlayMode,
                      activeCursor: Option[Cursor] = None,
                      selectedCursor: Option[Cursor] = None,
                      isFlipped: Boolean = false,
                      activeBoard: BoardType = Map.empty,
                      activeHand: HandType = Map.empty,
                      cursorEvent: Option[CursorEvent] = None
                     ) extends SAMModel {

  def boxPieces: Map[Ptype, Int] = {
    val b = activeBoard.values.groupBy(_.demoted.ptype).mapValues(_.size)
    val h = activeHand.filter(_._2 > 0).map { case (hd, n) => hd.ptype -> n }
    val used = MapUtil.mergeMaps(b, h)(_ + _, 0)
    MapUtil.mergeMaps(State.capacity, used)(_ - _, 0).mapValues(math.max(0, _))
  }
}
