package com.mogproject.mogami.frontend.state

import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, EditMode, HandleDialogMessage, PromotionDialog}

/**
  *
  */
trait ObserveFlagLike {

  //
  // Configuration
  //
  final val CONF = 1L << 0

  final val CONF_MSG_LANG = 1L << 1
  final val CONF_RCD_LANG = 1L << 2
  final val CONF_IDX_TYPE = 1L << 3

  final val CONF_DEVICE = 1L << 4
  final val CONF_LAYOUT = 1L << 5
  final val CONF_NUM_AREAS = 1L << 6
  final val CONF_FLIP_TYPE = 1L << 7
  final val CONF_PIECE_WIDTH = 1L << 8
  final val CONF_PIECE_FACE = 1L << 9
  final val CONF_COLOR_BACKGROUND = 1L << 30
  final val CONF_COLOR_CURSOR = 1L << 31
  final val CONF_COLOR_LAST_MOVE = 1L << 32
  final val CONF_NEW_BRANCH = 1L << 10
  final val CONF_SOUND = 1L << 11

  final val CONF_DEV = 1L << 12
  final val CONF_DEBUG = 1L << 13

  // Note: These flags will invoke MainPanelLike#refresh()
  final val CONF_ALL = CONF_DEVICE | CONF_LAYOUT | CONF_NUM_AREAS | CONF_FLIP_TYPE | CONF_PIECE_WIDTH | CONF_PIECE_FACE |
    CONF_MSG_LANG | CONF_RCD_LANG | CONF_SOUND | CONF_IDX_TYPE | CONF_COLOR_BACKGROUND | CONF_COLOR_CURSOR | CONF_COLOR_LAST_MOVE

  //
  // Mode
  //
  final val MODE_TYPE = 1L << 16
  final val MODE_EDIT = 1L << 17

  final val GAME_BRANCH = 1L << 18 // Includes Trunk. Check {turn, board, hand} in Edit Mode
  final val GAME_INFO = 1L << 19
  final val GAME_COMMENT = 1L << 20
  final val GAME_POSITION = 1L << 21
  final val GAME_INDICATOR = 1L << 22
  final val GAME_HANDICAP = 1L << 23
  final val GAME_JUST_MOVED = 1L << 24 // Transition to the next move
  final val GAME_NEXT_POS = 1L << 25
  final val GAME_PREV_POS = 1L << 26
  final val GAME_BRANCH_CHANGED = 1L << 27

  // Note: These flags will invoke MainPanelLike#refresh()
  final val MODE_ALL = MODE_TYPE | GAME_BRANCH | GAME_INFO | GAME_POSITION | GAME_HANDICAP | GAME_INDICATOR | GAME_JUST_MOVED | GAME_NEXT_POS | GAME_PREV_POS

  //
  // Menu Dialog (Open/Closed)
  //
  final val MENU_DIALOG = 1L << 28
  final val PROMOTION_DIALOG = 1L << 29

  //
  // Cursor
  //
  final val CURSOR_ACTIVE = 1L << 60
  final val CURSOR_SELECT = 1L << 61
  final val CURSOR_FLASH = 1L << 62

  // Note: These flags will invoke MainPanelLike#refresh()
  final val CURSOR_ALL = CURSOR_ACTIVE | CURSOR_SELECT | CURSOR_FLASH
}

object ObserveFlag extends ObserveFlagLike {
  def getObserveFlag(oldModel: BasePlaygroundModel, newModel: BasePlaygroundModel): Long = {
    var ret: Long = 0

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
      if (a.colorBackground != b.colorBackground) ret |= CONF_COLOR_BACKGROUND
      if (a.colorCursor != b.colorCursor) ret |= CONF_COLOR_CURSOR
      if (a.colorLastMove != b.colorLastMove) ret |= CONF_COLOR_LAST_MOVE
      if (a.boardIndexType != b.boardIndexType) ret |= CONF_IDX_TYPE
      if (a.newBranchMode != b.newBranchMode) ret |= CONF_NEW_BRANCH
      if (a.soundEffectEnabled != b.soundEffectEnabled) ret |= CONF_SOUND
    }

    //
    // Mode updates
    //
    if (oldModel.mode != newModel.mode) {
      val a = oldModel.mode
      val b = newModel.mode

      if (a.modeType != b.modeType) {
        ret |= MODE_TYPE

        if (a.isEditMode || b.isEditMode) ret |= MODE_EDIT
      }

      if (a.getGameInfo != b.getGameInfo) ret |= GAME_INFO

      (a.getGameControl, b.getGameControl) match {
        case (Some(g), Some(h)) =>
          if (g.game.trunk != h.game.trunk || g.game.branches != h.game.branches) ret |= GAME_BRANCH
          if (g.displayBranchNo != h.displayBranchNo) {
            ret |= GAME_POSITION
            ret |= GAME_BRANCH_CHANGED
          }
          if (g.displayPosition != h.displayPosition) ret |= GAME_POSITION
          if (g.game.comments != h.game.comments) ret |= GAME_COMMENT
        case _ =>
      }

      (a, b) match {
        case (EditMode(_, t1, b1, h1), EditMode(_, t2, b2, h2)) if t1 != t2 || b1 != b2 || h1 != h2 => ret |= GAME_BRANCH
        case _ =>
      }

      if (a.getIndicators != b.getIndicators) ret |= GAME_INDICATOR
      if (a.isHandicapped != b.isHandicapped) ret |= GAME_HANDICAP

      if (b.isJustMoved(a)) ret |= GAME_JUST_MOVED
      if (b.isNext(a)) ret |= GAME_NEXT_POS
      if (b.isPrevious(a)) ret |= GAME_PREV_POS
    }

    //
    // Cursor updates
    //
    if (oldModel.activeCursor != newModel.activeCursor) ret |= CURSOR_ACTIVE
    if (oldModel.selectedCursor != newModel.selectedCursor) ret |= CURSOR_SELECT
    if (newModel.flashedCursor.isDefined) ret |= CURSOR_FLASH

    //
    // Menu dialog updates
    //
    if (oldModel.menuDialogOpen != newModel.menuDialogOpen) ret |= MENU_DIALOG

    //
    // Promotion dialog updates
    //
    newModel.messageBox match {
      case Some(HandleDialogMessage(_: PromotionDialog, _)) => ret |= PROMOTION_DIALOG
      case _ =>
    }

    ret
  }
}