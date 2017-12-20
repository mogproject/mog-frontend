package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.{EditModeType, ModeType}
import com.mogproject.mogami.frontend.view.branch.BranchArea
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

  lazy val branchArea = BranchArea(isMobile = false)

  lazy val editHelpArea = new EditHelpArea

  private[this] val title = span(cls := "sidebar-left-title").render

  override lazy val content: Div = div(
    marginLeft := SideBarLeft.EXPANDED_MARGIN,
    cls := "sidebar-left-content",
    div(
      cls := "long-select",
      controlBar.element
    ),
    branchArea.element,
    editHelpArea.element
  ).render

  override lazy val titleExpanded: Heading = h4(
    cls := "sidebar-heading",
    onclick := { () => collapseSideBar() },
    span(cls := "pull-right glyphicon glyphicon-minus"),

    title
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

  def refresh(modeType: ModeType): Unit = {
    title.innerHTML = (modeType == EditModeType).fold("Edit", "Moves")
    if (modeType == EditModeType) editHelpArea.show() else editHelpArea.hide()
  }
}

object SideBarLeft {
  val EXPANDED_WIDTH: Int = 240

  val EXPANDED_MARGIN: String = "calc(50% - 98px)"
}