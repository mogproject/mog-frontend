package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.footer.FooterLike
import org.scalajs.dom.Element

/**
  *
  */
case class MainPane(isMobile: Boolean, override val getSite: () => PlaygroundSite) extends MainPaneLike

case class Footer(isDevMode: Boolean = true) extends FooterLike

case class TestSite(isMobile: Boolean) extends PlaygroundSite {
  override lazy val mainPane: MainPaneLike = MainPane(isMobile, () => this)

  override lazy val navBar: NavBar = NavBar(isMobile)

  override lazy val footer: FooterLike = Footer()
}

case class TestView(isMobile: Boolean, rootElem: Element) extends BasePlaygroundView {
  override lazy val website: PlaygroundSite = TestSite(isMobile)
}
