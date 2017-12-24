package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.menu._
import com.mogproject.mogami.frontend.view.Observer
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
trait SideBarRight extends SideBarLike with Observer[AccordionMenu] with SAMObserver[BasePlaygroundModel] {

  def getMenuPane: MenuPane

  override val EXPANDED_WIDTH: Int = SideBarRight.EXPANDED_WIDTH

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
    getMenuPane.element
  ).render

  override def collapseSideBar(): Unit = if (!isCollapsed) {
    super.collapseSideBar()
    getMenuPane.collapseMenu()
  }

  override def expandSideBar(): Unit = if (isCollapsed) {
    super.expandSideBar()
    getMenuPane.expandMenu()
  }

  def initialize(): Unit = {
    getMenuPane.accordions.foreach(_.addObserver(this))
  }

  override def handleUpdate(subject: AccordionMenu): Unit = {
    expandSideBar()
  }

  initialize()

  //
  // Observer
  //
  override val samObserveMask: Int = ObserveFlag.CONF_DEVICE

  /**
    * Collapse this sidebar is the screen is too small
    *
    * @param model model
    * @param flag -1: all bits on => refresh all
    */
  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    if (model.config.collapseByDefault) collapseSideBar()
  }
}

object SideBarRight {
  val EXPANDED_WIDTH: Int = 460
}
