package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.action.ChangeModeAction
import com.mogproject.mogami.frontend.model.{EditModeType, ModeType, PlayModeType, ViewModeType}
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.button.RadioButton
import com.mogproject.mogami.frontend.view.nav.{FlipButton, NavBarLike}
import com.mogproject.mogami.frontend.view.observer.ModeTypeObserver

/**
  *
  */
case class NavBar(isMobile: Boolean = false) extends NavBarLike with ModeTypeObserver {

  lazy val modeButton: RadioButton[ModeType] = RadioButton(
    Seq(PlayModeType, ViewModeType, EditModeType),
    Map(English -> Seq("Play", "View", "Edit")),
    (mt: ModeType) => PlaygroundSAM.doAction(ChangeModeAction(mt, confirmed = false)),
    Seq("thin-btn", "mode-select"),
    Seq.empty
  )

  lazy val flipButton: FlipButton = new FlipButton

  lazy val buttons = Seq(modeButton, flipButton)

  override def handleUpdate(modeType: ModeType): Unit = {
    modeButton.updateValue(modeType)
    replaceClass(navElem, "nav-bg-", s"nav-bg-${modeType.toString.take(4).toLowerCase()}")
  }

}