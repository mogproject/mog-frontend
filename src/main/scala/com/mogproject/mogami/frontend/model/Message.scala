package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.model.analyze.AnalyzeResult
import com.mogproject.mogami.frontend.model.io.RecordFormat

/**
  * Messages in Model
  */
sealed trait Message

case class AnalyzeResultMessage(analyzeResult: AnalyzeResult) extends Message

case class CopyResultMessage(format: RecordFormat) extends Message

case class HandleDialogMessage(dialog: Dialog, open: Boolean = true) extends Message
