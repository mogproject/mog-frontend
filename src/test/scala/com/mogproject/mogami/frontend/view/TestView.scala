package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.footer.FooterLike
import org.scalajs.dom.Element

/**
  *
  */
case class MainPane(isMobile: Boolean, override val getSite: () => PlaygroundSiteLike) extends MainPaneLike

case class Footer(isDev: Boolean, isDebug: Boolean) extends FooterLike

case class TestSite(isMobile: Boolean, isDev: Boolean, isDebug: Boolean) extends PlaygroundSiteLike {
  override lazy val mainPane: MainPaneLike = MainPane(isMobile, () => this)

  override lazy val navBar: NavBar = NavBar(isMobile)

  override lazy val footer: FooterLike = Footer(isDev, isDebug)
}

case class TestView(isMobile: Boolean, isDev: Boolean, isDebug: Boolean, rootElem: Element) extends BasePlaygroundView {
  override lazy val website: PlaygroundSiteLike = TestSite(isMobile, isDev, isDebug)
}
