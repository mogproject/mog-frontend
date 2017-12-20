package com.mogproject.mogami.frontend.action.analyze

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.model.analyze.{AnalyzeCompleted, AnalyzeResult, AnalyzeStarted}
import com.mogproject.mogami.mate.MateSolver

/**
  *
  */
case class AnalyzeCheckmateAction(timeoutSec: Int, started: Boolean = false) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    if (!started) {
      Some(model.copy(newAnalyzeResult = Some(AnalyzeResult(AnalyzeStarted, timeoutSec, None))))
    } else {
      model.mode.getGameControl.map { gc =>
        val result = MateSolver.solve(gc.getDisplayingState, gc.getDisplayingLastMoveTo, timeLimitMillis = 1000L * timeoutSec)
        model.copy(newAnalyzeResult = Some(AnalyzeResult(AnalyzeCompleted, timeoutSec, result)))
      }
    }
  }
}
