package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.{MoveFrom, Square}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.model.board.cursor.{MouseHoldEvent, _}
import com.mogproject.mogami.frontend.view.system.BrowserInfo

/**
  *
  */
case class BoardCursorEventAction(cursorEvent: CursorEvent) extends PlaygroundAction with CursorAdjustable with PieceEditable {

  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    cursorEvent match {
      case MouseMoveEvent(areaId, c) => executeMouseMove(model, areaId, c)
      case MouseDownEvent(areaId, c) => executeMouseDown(model, areaId, c)
      case MouseUpEvent(c) => executeMouseUp(model, c)
      case MouseHoldEvent => executeMouseHold(model)
    }
  }

  /**
    * Mouse Move
    *
    * @param model
    * @param areaId
    * @param cursor
    * @return
    */
  private[this] def executeMouseMove(model: PlaygroundModel, areaId: Int, cursor: Option[Cursor]): Option[PlaygroundModel] = {
    cursor.map(areaId -> _) match {
      case ac if ac == model.activeCursor => None
      case ac@Some((_, cc)) if model.mode.canActivate(cc) => Some(model.copy(activeCursor = ac))
      case _ => Some(model.copy(activeCursor = None))
    }
  }

  /**
    * Mouse Down
    *
    * @param model
    * @param areaId
    * @param cursor
    * @return
    */
  private[this] def executeMouseDown(model: PlaygroundModel, areaId: Int, cursor: Option[Cursor]): Option[PlaygroundModel] = {
    val flashedCursor = cursor.flatMap(c => model.mode.canActivate(c).option(c))
    val newModel = model.copy(flashedCursor = flashedCursor)

    (cursor, newModel.mode) match {
      //
      // Player (Editing)
      //
      case (Some(PlayerCursor(pl)), x@EditMode(_, t, _, _, _)) if pl != t =>
        Some(newModel.copy(mode = x.copy(turn = pl)))
      //
      // Player (Playing/Viewing)
      //
      case (Some(PlayerCursor(_)), _) if model.mode.playerSelectable && !model.mode.isEditMode =>
        Some(newModel.copy(messageBox = Some(HandleDialogMessage(GameInfoDialog)), activeCursor = None))
      //
      // Forward/Backward
      //
      case (Some(BoardCursor(sq)), _) if newModel.mode.forwardAvailable && sq.file != 5 =>
        invokeForward(newModel, areaId, sq).map(_.copy(selectedCursor = cursor.map(areaId -> _)))
      //
      // Select
      //
      case (Some(c), _) if newModel.selectedCursor.isEmpty && newModel.mode.canSelect(c) =>
        Some(newModel.copy(selectedCursor = cursor.map(areaId -> _)))
      //
      // Invalid Selection
      //
      case (Some(_), _) if newModel.selectedCursor.isEmpty =>
        Some(newModel)
      //
      // Invoke (Editing)
      //
      case (Some(c), em: EditMode) if newModel.selectedCursor.isDefined =>
        Some(newModel.copy(mode = editPiece(em, newModel.selectedCursor.get._2, c), selectedCursor = None))
      //
      // Invoke (Playing)
      //
      case (Some(BoardCursor(sq)), PlayMode(gc, _)) if newModel.selectedCursor.isDefined =>
        makeMove(gc, newModel.copy(selectedCursor = None), areaId, newModel.selectedCursor.get._2.moveFrom, sq, newModel.config.newBranchMode)
      //
      // Invoke (Live)
      //
      case (Some(BoardCursor(sq)), LiveMode(pl, _, gc, _)) if newModel.selectedCursor.isDefined =>
        makeMove(gc, newModel.copy(selectedCursor = None), areaId, newModel.selectedCursor.get._2.moveFrom, sq, newBranchMode = false)
      //
      // Deselect
      //
      case _ if newModel.selectedCursor.isDefined =>
        Some(newModel.copy(selectedCursor = None))
      //
      // Nothing
      //
      case _ =>
        None
    }
  }

  /**
    * Mouse Up
    *
    * @param model
    * @param cursor
    * @return
    */
  private[this] def executeMouseUp(model: PlaygroundModel, cursor: Option[Cursor]): Option[PlaygroundModel] = {
    (model.selectedCursor, cursor) match {
      case _ if model.mode.isViewMode => None // ignore mouse up in View Mode
      case (Some((areaId, selected)), Some(released)) if selected != released =>
        val cursor = if (selected.isHand || model.mode.isEditMode) {
          Some(released) // no adjustment for hand pieces or in Edit Mode
        } else {
          for {
            from <- selected.board
            to <- released.board
            p <- model.mode.getBoardPieces.get(from)
            sq <- adjustMovement(p, from, to)
          } yield BoardCursor(sq)
        }
        executeMouseDown(model, areaId, cursor)
      case _ if BrowserInfo.hasTouchEvent => Some(model.copy(activeCursor = None))
      case _ => None
    }
  }

  /**
    * Mouse Hold
    *
    * @param model
    * @return
    */
  private[this] def executeMouseHold(model: PlaygroundModel): Option[PlaygroundModel] = {
    (model.mode.forwardAvailable, model.selectedCursor) match {
      case (true, Some((areaId, BoardCursor(sq)))) => invokeForward(model, areaId, sq)
      case _ => None
    }
  }

  private[this] def makeMove(gc: GameControl, newModel: PlaygroundModel, areaId: Int, moveFrom: MoveFrom, moveTo: Square, newBranchMode: Boolean): Option[PlaygroundModel] = {
    val candidates = gc.getMoveCandidates(moveFrom, moveTo)
    candidates.length match {
      case 0 => Some(newModel)
      case 1 =>
        Some(newModel.copy(newModel.mode.setGameControl(gc.makeMove(candidates.head, newBranchMode, moveForward = true).get)))
      case 2 =>
        val isFlipped = newModel.config.isAreaFlipped(areaId) ^ candidates.head.player.isWhite
        Some(newModel.copy(messageBox = Some(HandleDialogMessage(PromotionDialog(candidates.head, isFlipped)))))
      case _ => None // never happens
    }
  }

  private[this] def invokeForward(model: PlaygroundModel, areaId: Int, square: Square): Option[PlaygroundModel] = {
    if (model.mode.isViewMode || model.mode.isLiveMode) {
      model.mode.getGameControl.map { gc =>
        val isForward = model.config.isAreaFlipped(areaId) ^ square.file < 5
        val nextGC = isForward.fold(gc.withNextDisplayPosition, gc.withPreviousDisplayPosition)
        model.copy(mode = model.mode.setGameControl(nextGC))
      }
    } else {
      None
    }
  }
}
