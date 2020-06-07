package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.ChangeModeAction
import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.button.RadioButton
import com.mogproject.mogami.frontend.view.nav.NavBarLike

/**
  *
  */
case class NavBar(isMobile: Boolean, embeddedMode: Boolean) extends NavBarLike with PlaygroundSAMObserver {

  private[this] def availableModes: Seq[ModeType] = Seq(PlayModeType, ViewModeType) ++ (!embeddedMode).option(EditModeType)

  lazy val modeButton: RadioButton[ModeType] = RadioButton(
    availableModes,
    (_: Messages) => Map[ModeType, String](PlayModeType -> "Play", ViewModeType -> "View", EditModeType -> "Edit").view.filterKeys(availableModes.contains).toMap,
    (mt: ModeType) => doAction(ChangeModeAction(mt, confirmed = false)),
    Seq(classButtonThin, "mode-select"),
    Seq.empty
  )

  override lazy val buttons: Seq[WebComponent] = modeButton +: super.buttons

  //
  // Observer
  //
  override val samObserveMask: Long = ObserveFlag.MODE_TYPE

  override def refresh(model: PlaygroundModel, flag: Long): Unit = {
    val modeType = model.mode.modeType
    modeButton.select(modeType)
    replaceClass(navElem, "nav-bg-", s"nav-bg-${modeType.toString.take(4).toLowerCase()}")
  }
}