package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.action.ChangeModeAction
import com.mogproject.mogami.frontend.model.{EditModeType, ModeType, PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.button.RadioButton
import com.mogproject.mogami.frontend.view.footer.FooterLike
import com.mogproject.mogami.frontend.view.nav.NavBarLike
import com.mogproject.mogami.frontend.view.observer.ModeTypeObserver
import org.scalajs.dom.Element

/**
  *
  */
case class TestMainPane(isMobile: Boolean = false, isLandscape: Boolean = false) extends MainPaneLike {

}

case class NavBar(isMobile: Boolean = false) extends NavBarLike with ModeTypeObserver {

  lazy val modeButton: RadioButton[ModeType] = RadioButton(
    Seq(PlayModeType, ViewModeType, EditModeType),
    Map(English -> Seq("Play", "View", "Edit")),
    (mt: ModeType) => PlaygroundSAM.doAction(ChangeModeAction(mt, confirmed = false)),
    Seq("thin-btn", "mode-select"),
    Seq.empty
  )

  lazy val buttons = Seq(modeButton)

  override def handleUpdate(modeType: ModeType): Unit = {
    modeButton.updateValue(modeType)
    println("Handle Update! " + modeType)
  }
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
