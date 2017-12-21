package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.footer.FooterLike
import org.scalajs.dom.Element

/**
  *
  */
class TestMainPane(val isMobile: Boolean, getSiteFunction: => PlaygroundSite) extends MainPaneLike {
  override def getSite: PlaygroundSite = getSiteFunction

}


case class Footer(isDevMode: Boolean = true) extends FooterLike {

}

case class TestSite(isMobile: Boolean) extends PlaygroundSite {
  override lazy val mainPane: MainPaneLike = new TestMainPane(isMobile, this)

  override lazy val navBar: NavBar = NavBar(isMobile)

  override lazy val footer: FooterLike = Footer()
}

case class TestView(isMobile: Boolean, rootElem: Element) extends BasePlaygroundView {
  override lazy val website: PlaygroundSite = TestSite(isMobile)
}
