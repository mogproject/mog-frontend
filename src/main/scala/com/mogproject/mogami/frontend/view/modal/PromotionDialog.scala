package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.action.board.MakeMoveAction
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.button.PieceFaceButton
import com.mogproject.mogami.frontend.view.event.EventManageable
import org.scalajs.jquery.JQuery

import scalatags.JsDom.all._

/**
  * Promotion dialog
  */
case class PromotionDialog(messageLang: Language,
                           pieceFace: PieceFace,
                           pieceSize: Coord,
                           rawMove: Move,
                           rotate: Boolean
                          ) extends EventManageable with ModalLike {
  //
  // promotion specific
  //
  private[this] val BUTTON_HEIGHT: Int = 80
  private[this] val buttonUnpromote = PieceFaceButton(pieceFace, pieceSize, rawMove.oldPtype, rotate, height := BUTTON_HEIGHT.px, data("dismiss") := "modal", onclick := {() => PlaygroundSAM.doAction(MakeMoveAction(rawMove))})
  private[this] val buttonPromote = PieceFaceButton(pieceFace, pieceSize, rawMove.oldPtype.promoted, rotate, height := BUTTON_HEIGHT.px, data("dismiss") := "modal", onclick := {() => PlaygroundSAM.doAction(MakeMoveAction(rawMove.copy(newPtype = rawMove.newPtype.promoted, promote = true)))})

  //
  // modal traits
  //
  override val title: String = messageLang match {
    case Japanese => "成りますか?"
    case English => "Do you want to promote?"
  }

  override val modalBody: ElemType = div()

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-5 col-xs-offset-1 col-md-3 col-md-offset-3", buttonUnpromote.element),
      div(cls := "col-xs-5 col-md-3", buttonPromote.element)
    )
  )

//  override def initialize(dialog: JQuery): Unit = {
//    setModalClickEvent(buttonUnpromote.element, dialog, () => PlaygroundSAM.doAction(MakeMoveAction(rawMove)))
//    setModalClickEvent(buttonPromote.element, dialog, () => PlaygroundSAM.doAction(MakeMoveAction(rawMove.copy(newPtype = rawMove.newPtype.promoted, promote = true))))
//  }
}
