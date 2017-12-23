package com.mogproject.mogami.frontend.state

import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, EditMode}

/**
  *
  */
trait ObserveFlagLike {

  //
  // Configuration
  //
  final val CONF = 0x00000001

  final val CONF_MSG_LANG = 0x00000002
  final val CONF_RCD_LANG = 0x00000004

  final val CONF_DEVICE = 0x00000010
  final val CONF_LAYOUT = 0x00000020
  final val CONF_NUM_AREAS = 0x00000040
  final val CONF_FLIP_TYPE = 0x00000080
  final val CONF_PIECE_WIDTH = 0x00000100
  final val CONF_PIECE_FACE = 0x00000200
  final val CONF_NEW_BRANCH = 0x00000400

  final val CONF_DEV = 0x00000800
  final val CONF_DEBUG = 0x00001000

  //
  // Mode
  //
  final val MODE_TYPE = 0x00010000
  final val MODE_FROM_EDIT = 0x00020000
  final val MODE_TO_EDIT = 0x00040000

  final val GAME_BRANCH = 0x00100000 // Includes Trunk. Check {turn, board, hand} in Edit Mode
  final val GAME_INFO = 0x00200000
  final val GAME_COMMENT = 0x00400000
  final val GAME_POSITION = 0x00800000

  //
  // Cursor
  //
  final val CURSOR_ACTIVE = 0x10000000
  final val CURSOR_SELECT = 0x20000000
  final val CURSOR_FLASH = 0x40000000

}

object ObserveFlag extends ObserveFlagLike {
  def getObserveFlag(oldModel: BasePlaygroundModel, newModel: BasePlaygroundModel): Int = {
    var ret: Int = 0

    //
    // Config updates
    //
    if (oldModel.config != newModel.config) {
      ret |= CONF

      val a = oldModel.config
      val b = newModel.config

      if (a.messageLang != b.messageLang) ret |= CONF_MSG_LANG
      if (a.recordLang != b.recordLang) ret |= CONF_RCD_LANG
      if (a.deviceType != b.deviceType) ret |= CONF_DEVICE
      if (a.layout != b.layout) ret |= CONF_LAYOUT
      if (a.flipType != b.flipType) {
        ret |= CONF_FLIP_TYPE

        if (a.flipType.numAreas != b.flipType.numAreas) ret |= CONF_NUM_AREAS
      }
      if (a.pieceWidth != b.pieceWidth) ret |= CONF_PIECE_WIDTH
      if (a.pieceFace != b.pieceFace) ret |= CONF_PIECE_FACE
      if (a.newBranchMode != b.newBranchMode) ret |= CONF_NEW_BRANCH
    }

    //
    // Mode updates
    //
    if (oldModel.mode != newModel.mode) {
      val a = oldModel.mode
      val b = newModel.mode

      if (a.modeType != b.modeType) {
        ret |= MODE_TYPE

        if (a.isEditMode) ret |= MODE_FROM_EDIT
        if (b.isEditMode) ret |= MODE_TO_EDIT
      }

      if (a.getGameInfo != b.getGameInfo) ret |= GAME_INFO

      (a.getGameControl, b.getGameControl) match {
        case (Some(g), Some(h)) =>
          if (g.game.trunk != h.game.trunk || g.game.branches != h.game.branches) ret |= GAME_BRANCH
          if (g.gamePosition != h.gamePosition) ret |= GAME_POSITION
          if (g.game.comments != h.game.comments) ret |= GAME_COMMENT
        case _ =>
      }

      (a, b) match {
        case (EditMode(_, t1, b1, h1), EditMode(_, t2, b2, h2)) if t1 != t2 || b1 != b2 || h1 != h2 => ret |= GAME_BRANCH
        case _ =>
      }
    }

    //
    // Cursor updates
    //
    if (oldModel.activeCursor != newModel.activeCursor) ret |= CURSOR_ACTIVE
    if (oldModel.selectedCursor != newModel.selectedCursor) ret |= CURSOR_SELECT
    if (oldModel.flashedCursor != newModel.flashedCursor) ret |= CURSOR_FLASH

    ret
  }
}