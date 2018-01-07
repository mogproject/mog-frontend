package com.mogproject.mogami.frontend.view.analyze

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.analyze.CountPointAction
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.i18n.DynamicHoverTooltip
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
class PointCountButton(isMobile: Boolean) extends WebComponent with SAMObserver[BasePlaygroundModel] {

  private[this] val countButton = DynamicHoverTooltip(
    CommandButton(DynamicComponent(_.COUNT_POINT).element, () => doAction(CountPointAction)),
    _.COUNT_POINT_TOOLTIP
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
    displayMessage(Messages.get.COUNT_POINT_RESULT(point, isKingInPromotionZone, numPiecesInPromotionZone))
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
