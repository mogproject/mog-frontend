package com.mogproject.mogami.frontend.view.modal

import com.mogproject.mogami._
import com.mogproject.mogami.frontend.action.board.MakeMoveAction
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.SVGImageCache
import com.mogproject.mogami.frontend.view.button.PieceFaceButton

import scalatags.JsDom.all._

/**
  * Promotion dialog
  */
case class PromotionDialog(pieceFace: PieceFace,
                           rawMove: Move,
                           rotate: Boolean
                          )(implicit imageCache: SVGImageCache) extends ModalLike {
  //
  // promotion specific
  //
  private[this] val BUTTON_HEIGHT: Int = 80
  private[this] val buttonUnpromote = PieceFaceButton(pieceFace, rawMove.oldPtype, rotate, height := BUTTON_HEIGHT.px, dismiss, onclick := { () => PlaygroundSAM.doAction(MakeMoveAction(rawMove)) })
  private[this] val buttonPromote = PieceFaceButton(pieceFace, rawMove.oldPtype.promoted, rotate, height := BUTTON_HEIGHT.px, dismiss, onclick := { () => PlaygroundSAM.doAction(MakeMoveAction(rawMove.copy(newPtype = rawMove.newPtype.promoted, promote = true))) })

  //
  // modal traits
  //
  override def getTitle(messages: Messages): Frag = StringFrag(messages.ASK_PROMOTE)

  override val modalBody: ElemType = div()

  override val modalFooter: ElemType = div(footerDefinition,
    div(cls := "row",
      div(cls := "col-xs-5 col-xs-offset-1 col-md-3 col-md-offset-3", buttonUnpromote.element),
      div(cls := "col-xs-5 col-md-3", buttonPromote.element)
    )
  )
}
