package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.Ptype
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.board.EditAttributeAction
import com.mogproject.mogami.frontend.model.EditMode
import com.mogproject.mogami.frontend.model.board.cursor.BoardCursor
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.button.PieceFaceButton
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
class EditHelpArea extends WebComponent with SAMObserver[BasePlaygroundModel] {

  final val ATTRIBUTE_BUTTON_HEIGHT: Int = 64

  lazy val attributeArea: Div = div().render

  override lazy val element: Div = {
    clearAttributes()

    div(
      label("Attributes"),
      attributeArea,
      br(),
      label("Help"),
      ul(
        li("Click on a player name to set the turn to move."),
        li("Double-click on a piece on board to change its attributes:",
          ul(
            li("Black Unpromoted ->"),
            li("Black Promoted ->"),
            li("White Unpromoted ->"),
            li("White Promoted ->"),
            li("Black Unpromoted")
          )
        )
      )
    ).render
  }

  def clearAttributes(): Unit = {
    attributeArea.innerHTML = "Select a piece on board."
  }

  def renderAttributes(ptype: Ptype, pieceFace: PieceFace): Unit = {
    def f(promoted: Boolean, rotated: Boolean) = {
      PieceFaceButton(pieceFace, promoted.fold(ptype.promoted, ptype.demoted), rotated, height := ATTRIBUTE_BUTTON_HEIGHT.px, onclick := { () => doAction(EditAttributeAction(promoted, rotated)) })
    }

    def g(rotated: Boolean) = {
      div(cls := "row",
        div(cls := "col-xs-5 col-xs-offset-1", f(promoted = false, rotated).element),
        if (ptype.demoted != ptype.promoted) div(cls := "col-xs-5", f(promoted = true, rotated).element) else ""
      )
    }

    WebComponent.removeAllChildElements(attributeArea)
    attributeArea.appendChild(div(g(true), br(), g(false)).render)
  }

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CURSOR_SELECT | ObserveFlag.MODE_EDIT

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    model.mode match {
      case EditMode(_, _, b, _) =>
        model.selectedCursor match {
          case Some((_, BoardCursor(sq))) => b.get(sq).foreach(p => renderAttributes(p.ptype, model.config.pieceFace))
          case _ => clearAttributes()
        }
      case _ => // do nothing
    }
  }
}
