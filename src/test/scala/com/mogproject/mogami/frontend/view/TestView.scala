package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.view.footer.FooterLike
import com.mogproject.mogami.frontend.view.nav.NavBar
import org.scalajs.dom.Element

/**
  *
  */
case class TestMainPane(isMobile: Boolean = false, isLandscape: Boolean = false) extends MainPaneLike {

}


case class Footer(isDevMode: Boolean = true) extends FooterLike {

}

case class TestSite(isMobile: Boolean, isLandscape: Boolean) extends PlaygroundSite {
  override lazy val mainPane: MainPaneLike = TestMainPane(isMobile, isLandscape)

  override lazy val navBar: NavBar = NavBar(isMobile)

  override lazy val footer: FooterLike = Footer()
}

case class TestView(rootElem: Element) extends BasePlaygroundView {
  override lazy val website = TestSite(false, false)
}
