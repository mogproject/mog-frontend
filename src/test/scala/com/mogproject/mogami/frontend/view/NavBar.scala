package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.ChangeModeAction
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button.RadioButton
import com.mogproject.mogami.frontend.view.nav.{FlipButton, NavBarLike}

/**
  *
  */
case class NavBar(isMobile: Boolean) extends NavBarLike {

  lazy val modeButton: RadioButton[ModeType] = RadioButton(
    Seq(PlayModeType, ViewModeType, EditModeType),
    Map(English -> Seq("Play", "View", "Edit")),
    (mt: ModeType) => doAction(ChangeModeAction(mt, confirmed = false)),
    Seq("thin-btn", "mode-select"),
    Seq.empty
  )

  override lazy val buttons: Seq[WebComponent] = modeButton +: super.buttons

//  override def handleUpdate(modeType: ModeType): Unit = {
//    modeButton.updateValue(modeType)
//    replaceClass(navElem, "nav-bg-", s"nav-bg-${modeType.toString.take(4).toLowerCase()}")
//  }

}