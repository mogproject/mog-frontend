package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.control.{ControlBar, ControlBarType}
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
class SideBarLeft extends SideBarLike {

  override val EXPANDED_WIDTH: Int = SideBarLeft.EXPANDED_WIDTH

  override protected val outputClass: String = "sidebar-left"

  lazy val controlBar = ControlBar(ControlBarType.LongList)

//  override def childComponents: Seq[WebComponent] = Seq(controlBar)

  override lazy val content: Div = div(
    marginLeft := SideBarLeft.EXPANDED_MARGIN,
    cls := "sidebar-left-content",
    div(
      cls := "long-select",
      controlBar.element
    )
  ).render

  override lazy val titleExpanded: Heading = h4(
    cls := "sidebar-heading",
    onclick := { () => collapseSideBar() },
    span(cls := "pull-right glyphicon glyphicon-minus"),
    marginLeft := 14.px, "Moves"
  ).render

  override lazy val titleCollapsed: Heading = h4(
    cls := "sidebar-heading",
    display := display.none.v,
    onclick := { () => expandSideBar() },
    span(cls := "pull-right glyphicon glyphicon-plus"),
    raw("&nbsp;")
  ).render

  override def collapseSideBar(): Unit = if (!isCollapsed) {
    super.collapseSideBar()
    content.style.marginLeft = (-EXPANDED_WIDTH).px
  }

  override def expandSideBar(): Unit = if (isCollapsed) {
    super.expandSideBar()
    content.style.marginLeft = SideBarLeft.EXPANDED_MARGIN
  }
}

object SideBarLeft {
  val EXPANDED_WIDTH: Int = 240

  val EXPANDED_MARGIN: String = "calc(50% - 98px)"
}