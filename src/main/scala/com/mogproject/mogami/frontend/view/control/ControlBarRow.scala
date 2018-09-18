package com.mogproject.mogami.frontend.view.control

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.common.datagrid.DataRow

/**
  *
  */
case class ControlBarRow(index: Option[Int],
                         hasComment: Boolean,
                         hasFork: Boolean,
                         move: String,
                         elapsedTime: Option[Int]
                        ) extends DataRow {
  override lazy val toTokens: Seq[(String, String)] = Seq(
    index.map(_.toString).getOrElse("") -> "move-list-index",
    hasComment.fold("*", "") -> "move-list-flag",
    hasFork.fold("+", "") -> "move-list-flag",
    move -> ("mv move-list-move" + (elapsedTime.nonEmpty && isLongJapaneseNotation(move)).fold(s" mv-${move.length}char", "")),
    elapsedTime.map(x => f"${x / 60}:${x % 60}%02d").getOrElse("") -> "move-list-time"
  )

  /**
    *
    * @param mv move representation
    * @return true if the string is in Japanese and longer than the default width in the move list
    */
  private[this] def isLongJapaneseNotation(mv: String): Boolean = {
    (mv(1).toInt >= 128) && mv.length >= 7
  }
}
