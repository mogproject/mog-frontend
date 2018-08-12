package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.view.footer.FooterLike
import com.mogproject.mogami.frontend.view.menu._
import com.mogproject.mogami.frontend.view.modal.MenuDialog
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.nav.NavBarLike
import org.scalajs.dom.Element

import scalatags.JsDom.all._

/**
  *
  */
trait PlaygroundSiteLike extends WebComponent {
  def isMobile: Boolean

  def freeMode: Boolean

  def embeddedMode: Boolean

  def navBar: NavBarLike

  def mainPane: MainPaneLike

  lazy val shareMenu: ShareMenu = new ShareMenu(isMobile)
  lazy val branchMenu: BranchMenu = new BranchMenu
  lazy val manageMenu: ManageMenu = new ManageMenu(isMobile, freeMode)
  lazy val actionMenu: ActionMenu = new ActionMenu
  lazy val analyzeMenu: AnalyzeMenu = new AnalyzeMenu(isMobile)
  lazy val resetMenu: ResetMenu = new ResetMenu
  lazy val settingMenu: SettingMenu = new SettingMenu
  lazy val gameHelpMenu: GameHelpMenu = new GameHelpMenu
  lazy val aboutMenu: AccordionMenu = new AboutMenu // can be overridden by another About class

  lazy val menuPane: MenuPane = if (isMobile) {
    MenuPane(Seq(shareMenu, manageMenu, actionMenu, branchMenu, analyzeMenu, resetMenu, settingMenu, gameHelpMenu, aboutMenu))
  } else {
    MenuPane(Seq(shareMenu, manageMenu, analyzeMenu, resetMenu, settingMenu, gameHelpMenu, aboutMenu))
  }

  lazy val menuDialog = MenuDialog(menuPane)

  def footer: FooterLike

  override def element: Element = {
    val elem = (if (embeddedMode) {
      div(
        div(cls := "navbar", tag("nav")(cls := navBar.classNames, navBar.element)),
        div(cls := "container-fluid",
          width := "100%", padding := 0,
          mainPane.element
        )
      )
    } else {
      div(
        div(cls := "navbar", tag("nav")(cls := navBar.classNames, navBar.element)),
        div(cls := "container-fluid",
          isMobile.fold(Seq(width := "100%", padding := 0), ""),
          mainPane.element,
          footer.element
        )
      )
    }).render

    if (!embeddedMode && isMobile) menuDialog // activate here
    elem
  }
}
