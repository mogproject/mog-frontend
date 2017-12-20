package com.mogproject.mogami.frontend.model.analyze

import com.mogproject.mogami.Move

sealed trait AnalyzeResult

case class CheckmateAnalyzeResult(result: Option[Seq[Move]]) extends AnalyzeResult

case class CountAnalyzeResult(point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int) extends AnalyzeResult