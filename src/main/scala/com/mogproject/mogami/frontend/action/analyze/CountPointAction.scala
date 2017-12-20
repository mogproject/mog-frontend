package com.mogproject.mogami.frontend.action.analyze

import com.mogproject.mogami.{BISHOP, KING, Ptype, ROOK}
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.model.analyze.{AnalyzeCompleted, CountAnalyzeResult}

/**
  *
  */
object CountPointAction extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    model.mode.getGameControl.map { gc =>
      val st = gc.getDisplayingState
      val t = st.turn
      val bp = st.board.filter(_._2.owner == t).map(x => getPtypePoint(x._2.ptype.demoted)).sum
      val hp = st.hand.filter(_._1.owner == t).map { case (k, v) => getPtypePoint(k.ptype) * v }.sum
      val isKingInPromotionZone = st.turnsKing.exists(_.isPromotionZone(t))
      val numPiecesInPromotionZone = st.board.count { case (k, v) => k.isPromotionZone(t) && v.owner == t && v.ptype != KING }
      model.copy(newAnalyzeResult = Some(CountAnalyzeResult(AnalyzeCompleted, bp + hp, isKingInPromotionZone, numPiecesInPromotionZone)))
    }
  }

  private[this] def getPtypePoint(ptype: Ptype): Int = ptype match {
    case BISHOP | ROOK => 5
    case KING => 0
    case _ => 1
  }
}
