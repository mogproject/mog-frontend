package com.mogproject.mogami.frontend.view.analyze

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.analyze.CountPointAction
import com.mogproject.mogami.frontend.model.English
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.button.{HoverCommandButton, MultiLingualLabel}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
class PointCountButton(isMobile: Boolean) extends WebComponent with SAMObserver[BasePlaygroundModel] {

  private[this] val countButton = HoverCommandButton(
    MultiLingualLabel("Count", "計算"),
    () => doAction(CountPointAction),
    Map(English -> "Count points for this position", Japanese -> "局面の点数を計算 (持将棋判定用)")
  )

  private[this] lazy val countMessage: Div = div(
    cls := "col-xs-8 col-sm-9 text-muted",
    marginTop := 6
  ).render

  override lazy val element: Div = div(
    div(cls := "row",
      div(cls := "col-xs-5 col-sm-3",
        countButton.element
      ),
      countMessage
    )
  ).render

  //
  // messaging
  //
  def displayResult(point: Int, isKingInPromotionZone: Boolean, numPiecesInPromotionZone: Int): Unit = {
    val plural = (1 < numPiecesInPromotionZone).fold("s", "")

    val msg = Seq(
      s"Points: ${point}",
      "In the promotion zone: " + isKingInPromotionZone.fold("King + ", "") + s"${numPiecesInPromotionZone} piece${plural}"
    ).mkString("\n")
    displayMessage(msg)
  }

  private[this] def displayMessage(message: String): Unit = {
    countMessage.innerHTML = message.replace("\n", br().toString())
  }

  def clearMessage(): Unit = {
    countMessage.innerHTML = ""
  }

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.GAME_BRANCH | ObserveFlag.GAME_POSITION

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    clearMessage()
  }
}
