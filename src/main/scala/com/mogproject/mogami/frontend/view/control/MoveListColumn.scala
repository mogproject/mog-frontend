package com.mogproject.mogami.frontend.view.control

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.control.UpdateElapsedTimeAction
import com.mogproject.mogami.frontend.view.common.datagrid.DataColumn

import scala.util.Try

/**
  * Defines the columns for the move list.
  */
object MoveListColumn {
  private[this] val indexColumn = DataColumn[MoveListData](
    _ => "*",
    _.index.map(_.toString).getOrElse(""),
    "move-list-index",
    _ => "move-list-index"
  )

  private[this] val hasCommentColumn: DataColumn[MoveListData] = DataColumn[MoveListData](
    _ => "",
    _.hasComment.fold("*", ""),
    "move-list-flag",
    _ => "move-list-flag"
  )

  private[this] val hasForkColumn: DataColumn[MoveListData] = DataColumn[MoveListData](
    _ => "",
    _.hasFork.fold("+", ""),
    "move-list-flag",
    _ => "move-list-flag"
  )

  private[this] val moveStringColumn = DataColumn[MoveListData](
    _.MOVE,
    _.moveString,
    "move-list-move",
    data => "mv move-list-move" + (data.elapsedTime.nonEmpty && isLongJapaneseNotation(data.moveString)).fold(s" mv-${data.moveString.length}char", "")
  )

  private[this] val elapsedTimeColumn = DataColumn[MoveListData](
    _.TIME,
    _.elapsedTime.map(formatTime).getOrElse(""),
    "move-list-time",
    _ => "move-list-time",
    isEditable = _.index.isDefined,
    onEdit = editTime
  )

  val columns = Seq(indexColumn, hasCommentColumn, hasForkColumn, moveStringColumn, elapsedTimeColumn)

  /**
    *
    * @param mv move representation
    * @return true if the string is in Japanese and longer than the default width in the move list
    */
  private[this] def isLongJapaneseNotation(mv: String): Boolean = {
    (mv(1).toInt >= 128) && mv.length >= 7
  }

  /**
    * @return "min:sec"
    */
  private[this] def formatTime(elapsedTime: Int): String = f"${elapsedTime / 60}:${elapsedTime % 60}%02d"

  private[this] val elapsedTimePattern = """(?:(\d*):)?(\d+)""".r

  private[this] val MAX_ELAPSED_TIME = 999 * 60 + 59

  private[this] def editTime(updatedText: String): Unit = {
    // validate text
    val elapsedTime = validateElapsedTime(updatedText.trim)
    // do action
    PlaygroundSAM.doAction(UpdateElapsedTimeAction(elapsedTime))
  }

  protected[control] def validateElapsedTime(text: String): Option[Int] = {
    text match {
      case "" =>
        None
      case elapsedTimePattern(minutes, seconds) if minutes == null || minutes.isEmpty =>
        Some(Try(seconds.toInt).toOption.getOrElse(MAX_ELAPSED_TIME))
      case elapsedTimePattern(minutes, seconds) =>
        Some((for {
          mm <- Try(minutes.toInt).toOption if mm <= MAX_ELAPSED_TIME / 60
          ss <- Try(seconds.toInt).toOption
        } yield {
          math.min(MAX_ELAPSED_TIME, mm * 60 + ss)
        }).getOrElse(MAX_ELAPSED_TIME))
      case _ => None
    }
  }

}
