package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.Move

/**
  * Dialogs in Model
  */
sealed trait Dialog

case class PromotionDialog(rawMove: Move, rotate: Boolean) extends Dialog

case object GameInfoDialog extends Dialog

case object EditWarningDialog extends Dialog

case class EditAlertDialog(message: String) extends Dialog

case object CommentDialog extends Dialog

case object AskDeleteBranchDialog extends Dialog

case object EmbedDialog extends Dialog
