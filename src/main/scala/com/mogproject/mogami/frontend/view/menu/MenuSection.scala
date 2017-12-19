package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
trait MenuSection extends WebComponent {
//  override def childComponents: Seq[WebComponent] = accordions

  override lazy val element: Div = div().render

  def accordions: Seq[AccordionMenu]

  def outputs: Seq[Div] = accordions.map(_.element)

  def show(): Unit = outputs.foreach(_.style.display = display.block.v)

  def hide(): Unit = outputs.foreach(_.style.display = display.none.v)

  def collapseTitle(): Unit = accordions.foreach(_.collapseTitle())

  def expandTitle(): Unit = accordions.foreach(_.expandTitle())
}
