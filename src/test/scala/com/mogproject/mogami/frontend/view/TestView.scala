package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.footer.FooterLike
import com.mogproject.mogami.frontend.view.menu.MenuPane
import org.scalajs.dom.Element

/**
  *
  */
case class MainPane(isMobile: Boolean, embeddedMode: Boolean, override val getSite: () => PlaygroundSiteLike) extends MainPaneLike

case class Footer(isDev: Boolean, isDebug: Boolean) extends FooterLike

case class TestSite(isMobile: Boolean, freeMode: Boolean, embeddedMode: Boolean, isDev: Boolean, isDebug: Boolean) extends PlaygroundSiteLike {
  override lazy val mainPane: MainPaneLike = MainPane(isMobile, embeddedMode, () => this)

  // can modify menus
  //  override lazy val menuPane: MenuPane = if (isMobile) {
  //    MenuPane(Seq(actionMenu, settingMenu))
  //  } else {
  //    MenuPane(Seq(settingMenu))
  //  }

  override lazy val navBar: NavBar = NavBar(isMobile, embeddedMode)

  override lazy val footer: FooterLike = Footer(isDev, isDebug)
}

case class TestView(isMobile: Boolean, freeMode: Boolean, embeddedMode: Boolean, isDev: Boolean, isDebug: Boolean, rootElem: Element) extends BasePlaygroundView {
  override lazy val website: PlaygroundSiteLike = TestSite(isMobile, freeMode, embeddedMode, isDev, isDebug)
}
