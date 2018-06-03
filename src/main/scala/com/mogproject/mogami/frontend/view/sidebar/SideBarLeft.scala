package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.action.CopyAllMovesAction
import com.mogproject.mogami.frontend.view.SVGImageCache
import com.mogproject.mogami.frontend.view.branch.BranchArea
import com.mogproject.mogami.frontend.view.button.CommandButton
import com.mogproject.mogami.frontend.view.control.{ControlBar, ControlBarType}
import com.mogproject.mogami.frontend.view.i18n.Messages
import org.scalajs.dom.html.{Div, Heading}
import scalatags.JsDom.all._

/**
  *
  */
class SideBarLeft(implicit imageCache: SVGImageCache) extends SideBarLike with SAMObserver[BasePlaygroundModel] {

  override val EXPANDED_WIDTH: Int = SideBarLeft.EXPANDED_WIDTH

  override protected val outputClass: String = "sidebar-left hidden-xs"

  lazy val controlBar = ControlBar(ControlBarType.LongList)

  private[this] lazy val copyMovesButton: WebComponent = CommandButton(
    classButtonDefault + " " + classButtonThin,
    onclick := { () => doAction(CopyAllMovesAction) }
  ).withDynamicTextContent(_.COPY_ALL_MOVES)

  lazy val branchArea = BranchArea(isMobile = false)

  lazy val editHelpArea = new EditHelpArea

  private[this] val title = span(cls := "sidebar-left-title").render

  override lazy val content: Div = div(
    marginLeft := SideBarLeft.EXPANDED_MARGIN,
    cls := "sidebar-left-content",
    div(
      cls := "long-select",
      controlBar.element,
      copyMovesButton.element
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

  //
  // Action
  //
  def copyAllMoves(txt: String): Unit = {
    copyMovesButton.element.setAttribute("data-clipboard-text", txt)
  }

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.MODE_EDIT | ObserveFlag.CONF_MSG_LANG

  override def refresh(model: BasePlaygroundModel, flag: Long): Unit = {
    if (model.mode.isEditMode) {
      title.innerHTML = Messages.get.EDIT
      editHelpArea.show()
    } else {
      title.innerHTML = Messages.get.MOVES
      editHelpArea.hide()
    }
  }
}

object SideBarLeft {
  val EXPANDED_WIDTH: Int = 240

  val EXPANDED_MARGIN: String = "calc(50% - 98px)"
}