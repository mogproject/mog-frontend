package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.Ptype
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.board.EditAttributeAction
import com.mogproject.mogami.frontend.model.EditMode
import com.mogproject.mogami.frontend.model.board.cursor.BoardCursor
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.button.PieceFaceButton
import com.mogproject.mogami.frontend.view.system.SVGImageCache
import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

/**
  *
  */
class EditHelpArea(implicit imageCache: SVGImageCache) extends WebComponent with PlaygroundSAMObserver {

  final val ATTRIBUTE_BUTTON_HEIGHT: Int = 64

  lazy val attributeArea: Div = div().render

  override lazy val element: Div = {
    clearAttributes()

    div(
      WebComponent.dynamicLabel(_.ATTRIBUTES).element,
      attributeArea,
      br(),
      WebComponent.dynamicLabel(_.HELP).element,
      WebComponent(ul()).withDynamicInnerElements(_.EDIT_HELP).element
    ).render
  }

  def clearAttributes(): Unit = {
    WebComponent.replaceChildElement(attributeArea, WebComponent.dynamicSpan(_.SELECT_PIECE_ON_BOARD).element)
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

    WebComponent.replaceChildElements(attributeArea, Seq(g(true), br(), g(false)).map(_.render))
  }

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.CURSOR_SELECT | ObserveFlag.MODE_EDIT

  override def refresh(model: PlaygroundModel, flag: Long): Unit = {
    model.mode match {
      case EditMode(_, _, b, _, _) =>
        model.selectedCursor match {
          case Some((_, BoardCursor(sq))) => b.get(sq).foreach(p => renderAttributes(p.ptype, model.config.pieceFace))
          case _ => clearAttributes()
        }
      case _ => // do nothing
    }
  }
}
