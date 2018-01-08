package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.WebComponent
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._

/**
  *
  */
case class MenuPane(accordions: Seq[AccordionMenu]) extends WebComponent {

  override lazy val element: Div = div(
    cls := "panel-group", id := "accordion", role := "tablist", aria.multiselectable := true,
    accordions.map(_.element)
  ).render

  def collapseMenu(): Unit = {
    accordions.foreach(_.collapseTitle())
  }

  def expandMenu(): Unit = {
    accordions.foreach(_.expandTitle())
  }
}
