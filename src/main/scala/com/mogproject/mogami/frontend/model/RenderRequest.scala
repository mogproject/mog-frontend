package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend.model.board.cursor.Cursor
import com.mogproject.mogami.frontend.model.io.RecordFormat

/**
  *
  */
sealed trait RenderRequest

case class PromotionDialogRequest(rawMove: Move, rotate: Boolean) extends RenderRequest

case class CursorFlashRequest(cursor: Cursor) extends RenderRequest

case object GameInfoDialogRequest extends RenderRequest

case object EditWarningDialogRequest extends RenderRequest

case class EditAlertDialogRequest(message: String) extends RenderRequest

case object CommentDialogRequest extends RenderRequest

case object AskDeleteBranchRequest extends RenderRequest

case class CopyRecordRequest(format: RecordFormat) extends RenderRequest
