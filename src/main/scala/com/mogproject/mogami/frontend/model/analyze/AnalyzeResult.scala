package com.mogproject.mogami.frontend.model.analyze

import com.mogproject.mogami.Move

sealed abstract class AnalyzeResult(val status: AnalyzeStatus)

case class CheckmateAnalyzeResult(override val status: AnalyzeStatus, timeoutSec: Int, result: Option[Seq[Move]]) extends AnalyzeResult(status)

case class CountAnalyzeResult(override val status: AnalyzeStatus, point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int) extends AnalyzeResult(status)