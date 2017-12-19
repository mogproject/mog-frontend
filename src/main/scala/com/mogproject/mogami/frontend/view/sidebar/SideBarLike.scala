package com.mogproject.mogami.frontend.view.sidebar

import com.mogproject.mogami.frontend.view.{Observable, WebComponent}
import com.mogproject.mogami.util.Implicits._
import org.scalajs.dom.html.{Div, Heading}

import scalatags.JsDom.all._

/**
  *
  */
trait SideBarLike extends WebComponent with Observable[SideBarLike] {
  def EXPANDED_WIDTH: Int

  val COLLAPSED_WIDTH: Int = 60

  protected def outputClass: String

  private[this] var isCollapsedValue = false

  def content: Div

  def titleExpanded: Heading

  def titleCollapsed: Heading

  override lazy val element: Div = div(cls := "sidebar " + outputClass,
    width := EXPANDED_WIDTH,
    div(
      titleExpanded,
      titleCollapsed,
      content
    )
  ).render

  def collapseSideBar(): Unit = if (!isCollapsedValue) {
    element.style.width = COLLAPSED_WIDTH.px
    titleExpanded.style.display = display.none.v
    titleCollapsed.style.display = display.block.v
    isCollapsedValue = true
    notifyObservers(this)
  }

  def expandSideBar(): Unit = if (isCollapsedValue) {
    element.style.width = EXPANDED_WIDTH.px
    titleCollapsed.style.display = display.none.v
    titleExpanded.style.display = display.block.v
    isCollapsedValue = false
    notifyObservers(this)
  }

  def isCollapsed: Boolean = isCollapsedValue

  def currentWidth: Int = isCollapsed.fold(COLLAPSED_WIDTH, EXPANDED_WIDTH)
}

