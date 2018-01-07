package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.i18n.{DynamicHoverTooltipLike, Messages}
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement
import com.mogproject.mogami.frontend.view.tooltip.TooltipPlacement.TooltipPlacement
import org.scalajs.dom.Element
import org.scalajs.dom.html.{Anchor, Div}

import scalatags.JsDom.all._
import com.mogproject.mogami.util.Implicits._

/**
  *
  */
case class RadioButton[A](items: Seq[A],
                          labelFunc: Messages => Map[A, String],
                          clickAction: A => Unit,
                          buttonClasses: Seq[String] = Seq("btn-sm"),
                          buttonGroupClasses: Seq[String] = Seq("btn-group-justified")
                         ) extends WebComponent {

  private[this] var value: A = items.head

  protected def createInnerElements: Div = {
    div(cls := ("btn-group" :: buttonGroupClasses.toList).mkString(" "),
      items.map { item =>
        a(
          cls := ("btn" :: "btn-primary" :: (value == item).fold("active", "notActive") :: buttonClasses.toList).mkString(" "),
          onclick := { () => clickAction(item); select(item) },
          StringFrag(labelFunc(Messages.get).getOrElse(item, ""))
        )
      }
    ).render
  }

  override lazy val element: Element = div(
    cls := "input-group"
  ).render

  //
  // Operations
  //
  def select(item: A): Unit = {
    value = item
    WebComponent.replaceChildElement(element, createInnerElements)
  }

  def getValue: A = value

  override def withDynamicHoverTooltip(f: Messages => String, tooltipPlacement: TooltipPlacement = TooltipPlacement.Bottom): RadioButton[A] = {
    val thisElem = element

    new RadioButton[A](items, labelFunc, clickAction, buttonClasses, buttonGroupClasses) with DynamicHoverTooltipLike {
      override lazy val element: Element = thisElem

      override lazy val placement: TooltipPlacement = tooltipPlacement

      override def getTooltipMessage(messages: Messages): String = f(messages)
    }
  }

}
