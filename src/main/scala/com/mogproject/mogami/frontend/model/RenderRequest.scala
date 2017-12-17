package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend.model.board.cursor.Cursor

/**
  *
  */
sealed trait RenderRequest

case class PromotionDialogRequest(rawMove: Move) extends RenderRequest

case class CursorFlashRequest(cursor: Cursor) extends RenderRequest

case object GameInfoDialogRequest extends RenderRequest
