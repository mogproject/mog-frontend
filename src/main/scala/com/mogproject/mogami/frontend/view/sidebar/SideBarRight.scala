package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.frontend.view.menu.{AccordionMenu, GameHelpMenu, AboutMenu, MenuPane}
import com.mogproject.mogami.frontend.view.{Observer, WebComponent}
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
class SideBarRight extends SideBarLike with Observer[AccordionMenu] {

  override val EXPANDED_WIDTH: Int = SideBarRight.EXPANDED_WIDTH

  lazy val menuPane = MenuPane(Seq(GameHelpMenu, AboutMenu))

  override protected val outputClass: String = "sidebar-right"

  override lazy val titleExpanded: Heading = h4(
    cls := "sidebar-heading",
    onclick := { () => collapseSideBar() },
    span(cls := "glyphicon glyphicon-minus"),
    span(paddingLeft := 14.px, "Menu")
  ).render

  override lazy val titleCollapsed: Heading = h4(
    cls := "sidebar-heading",
    display := display.none.v,
    onclick := { () => expandSideBar() },
    span(cls := "glyphicon glyphicon-plus")
  ).render

  override def content: Div = div(
    titleExpanded,
    titleCollapsed,
    menuPane.element
  ).render

  override def collapseSideBar(): Unit = if (!isCollapsed) {
    super.collapseSideBar()
    menuPane.collapseMenu()
  }

  override def expandSideBar(): Unit = if (isCollapsed) {
    super.expandSideBar()
    menuPane.expandMenu()
  }

  def initialize(): Unit = {
    menuPane.accordions.foreach(_.addObserver(this))
  }

  override def handleUpdate(subject: AccordionMenu): Unit = {
    expandSideBar()
  }

  initialize()
}

object SideBarRight {
  val EXPANDED_WIDTH: Int = 460
}
