package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.footer.FooterLike
import com.mogproject.mogami.frontend.view.menu._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.nav.NavBarLike
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
trait PlaygroundSite extends WebComponent {
  def isMobile: Boolean

  def navBar: NavBarLike

  def mainPane: MainPaneLike

  val shareMenu = new ShareMenu
  //lazy val branchMenu = new BranchMenu

  val manageMenu = new ManageMenu

  lazy val actionMenu = new ActionMenu

  val analyzeMenu = new AnalyzeMenu

  val resetMenu = new ResetMenu

  val settingMenu = new SettingMenu

  val gameHelpMenu = new GameHelpMenu

  val aboutMenu = new AboutMenu

  lazy val menuPane: MenuPane = if (isMobile) {
    MenuPane(Seq(shareMenu, manageMenu, actionMenu, analyzeMenu, resetMenu, settingMenu, gameHelpMenu, aboutMenu))
  } else {
    MenuPane(Seq(shareMenu, manageMenu, analyzeMenu, resetMenu, settingMenu, gameHelpMenu, aboutMenu))
  }

  def footer: FooterLike

  override def element: Element = div(
    div(cls := "navbar", tag("nav")(cls := navBar.classNames, navBar.element)),
    div(cls := "container-fluid",
      isMobile.fold(Seq(position := position.fixed.v, width := "100%", padding := 0), ""),
      mainPane.element,
      footer.element
    )
  ).render
}
