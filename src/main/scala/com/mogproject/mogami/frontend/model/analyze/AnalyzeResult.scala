package com.mogproject.mogami.frontend.model.analyze

import com.mogproject.mogami.Move

case class AnalyzeResult(status: AnalyzeStatus, timeoutSec: Int, result: Option[Seq[Move]])