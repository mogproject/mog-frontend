package com.mogproject.mogami.frontend.model.analyze

/**
  *
  */
sealed trait AnalyzeStatus

case object AnalyzeStarted extends AnalyzeStatus

case object AnalyzeCompleted extends AnalyzeStatus