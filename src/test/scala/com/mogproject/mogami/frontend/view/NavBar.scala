package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.ChangeModeAction
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.sam.PlaygroundSAM
import com.mogproject.mogami.frontend.view.action.ResignButton
import com.mogproject.mogami.frontend.view.button.RadioButton
import com.mogproject.mogami.frontend.view.nav.{FlipButton, NavBarLike}
import com.mogproject.mogami.frontend.view.observer.ModeTypeObserver

/**
  *
  */
case class NavBar(isMobile: Boolean) extends NavBarLike with ModeTypeObserver {

  lazy val modeButton: RadioButton[ModeType] = RadioButton(
    Seq(PlayModeType, ViewModeType, EditModeType),
    Map(English -> Seq("Play", "View", "Edit")),
    (mt: ModeType) => PlaygroundSAM.doAction(ChangeModeAction(mt, confirmed = false)),
    Seq("thin-btn", "mode-select"),
    Seq.empty
  )

  lazy val flipButton: FlipButton = new FlipButton

  lazy val resignButton: ResignButton = ResignButton(isSmall = true, confirm = false)

  lazy val buttons: Seq[WebComponent] = Seq(modeButton, flipButton) ++ isMobile.fold(Seq.empty, Seq(resignButton))

  override def handleUpdate(modeType: ModeType): Unit = {
    modeButton.updateValue(modeType)
    replaceClass(navElem, "nav-bg-", s"nav-bg-${modeType.toString.take(4).toLowerCase()}")
  }

}