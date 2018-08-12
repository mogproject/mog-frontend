package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board.DoubleBoard
import com.mogproject.mogami.frontend.model.{PlaygroundConfiguration, PlaygroundModel}
import com.mogproject.mogami.frontend.view.system.PlaygroundLocalStorage

/**
  *
  */
case class UpdateConfigurationAction(f: PlaygroundConfiguration => PlaygroundConfiguration) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    val mc = model.config
    val c = f(mc)

    // save settings to Local Storage (only updates)
    PlaygroundLocalStorage(
      (mc.pieceWidth != c.pieceWidth).option(c.pieceWidth),
      (mc.layout != c.layout).option(c.layout),
      (mc.pieceFace != c.pieceFace).option(c.pieceFace),
      (mc.colorBackground != c.colorBackground).option(c.colorBackground.drop(1)), // Remove leading '#'
      (mc.colorCursor != c.colorCursor).option(c.colorCursor.drop(1)),
      (mc.colorLastMove != c.colorLastMove).option(c.colorLastMove.drop(1)),
      (mc.boardIndexType != c.boardIndexType).option(c.boardIndexType),
      (mc.flipType.numAreas != c.flipType.numAreas).option(c.flipType == DoubleBoard),
      (mc.visualEffectEnabled != c.visualEffectEnabled).option(c.visualEffectEnabled),
      (mc.soundEffectEnabled != c.soundEffectEnabled).option(c.soundEffectEnabled),
      (mc.messageLang != c.messageLang).option(c.messageLang),
      (mc.recordLang != c.recordLang).option(c.recordLang)
    ).save()

    Some(model.copy(config = c, selectedCursor = None))
  }
}
