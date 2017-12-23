package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.{MoveFrom, Square}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.model.board.cursor.{MouseHoldEvent, _}

/**
  *
  */
case class BoardCursorEventAction(cursorEvent: CursorEvent) extends PlaygroundAction with CursorAdjustable with PieceEditable {

  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
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
  private[this] def executeMouseMove(model: BasePlaygroundModel, areaId: Int, cursor: Option[Cursor]): Option[BasePlaygroundModel] = {
    cursor.map(areaId -> _) match {
      case ac if ac == model.activeCursor => None
      case ac@Some((_, cc)) if model.mode.canActivate(cc) => Some(model.copy(newActiveCursor = ac))
      case _ => Some(model.copy(newActiveCursor = None))
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
  private[this] def executeMouseDown(model: BasePlaygroundModel, areaId: Int, cursor: Option[Cursor]): Option[BasePlaygroundModel] = {
    val flashedCursor = cursor.flatMap(c => model.mode.canActivate(c).option(c))
    val newModel = model.copy(newFlashedCursor = flashedCursor)

    (cursor, newModel.mode) match {
      //
      // Player (Editing)
      //
      case (Some(PlayerCursor(pl)), EditMode(gi, t, b, h)) if pl != t =>
        Some(newModel.copy(newMode = EditMode(gi, pl, b, h)))
      //
      // Player (Playing/Viewing)
      //
      case (Some(PlayerCursor(_)), _) if model.mode.playerSelectable && !model.mode.isEditMode =>
        Some(newModel.copy(newMessageBox = Some(HandleDialogMessage(GameInfoDialog)), newActiveCursor = None))
      //
      // Forward/Backward
      //
      case (Some(BoardCursor(sq)), _) if newModel.mode.forwardAvailable && sq.file != 5 =>
        invokeForward(newModel, areaId, sq).map(_.copy(newSelectedCursor = cursor.map(areaId -> _)))
      //
      // Select
      //
      case (Some(c), _) if newModel.selectedCursor.isEmpty && newModel.mode.canSelect(c) =>
        Some(newModel.copy(newSelectedCursor = cursor.map(areaId -> _)))
      //
      // Invalid Selection
      //
      case (Some(_), _) if newModel.selectedCursor.isEmpty =>
        Some(newModel)
      //
      // Invoke (Editing)
      //
      case (Some(c), em: EditMode) if newModel.selectedCursor.isDefined =>
        Some(newModel.copy(newMode = editPiece(em, newModel.selectedCursor.get._2, c), newSelectedCursor = None))
      //
      // Invoke (Playing)
      //
      case (Some(BoardCursor(sq)), PlayMode(gc)) if newModel.selectedCursor.isDefined =>
        makeMove(gc, newModel.copy(newSelectedCursor = None), areaId, newModel.selectedCursor.get._2.moveFrom, sq, newModel.config.newBranchMode)
      //
      // Invoke (Live)
      //
      case (Some(BoardCursor(sq)), LiveMode(pl, gc)) if newModel.selectedCursor.isDefined =>
        makeMove(gc, newModel.copy(newSelectedCursor = None), areaId, newModel.selectedCursor.get._2.moveFrom, sq, newBranchMode = false)
      //
      // Deselect
      //
      case _ if newModel.selectedCursor.isDefined =>
        Some(newModel.copy(newSelectedCursor = None))
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
  private[this] def executeMouseUp(model: BasePlaygroundModel, cursor: Option[Cursor]): Option[BasePlaygroundModel] = {
    (model.selectedCursor, cursor) match {
      case (Some((_, selected)), Some(released)) if selected != released =>
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
        executeMouseDown(model, 0, cursor) // areaId does not matter
      case _ => None
    }
  }

  /**
    * Mouse Hold
    *
    * @param model
    * @return
    */
  private[this] def executeMouseHold(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    (model.mode.forwardAvailable, model.selectedCursor) match {
      case (true, Some((areaId, BoardCursor(sq)))) => invokeForward(model, areaId, sq)
      case _ => None
    }
  }

  private[this] def makeMove(gc: GameControl, newModel: BasePlaygroundModel, areaId: Int, moveFrom: MoveFrom, moveTo: Square, newBranchMode: Boolean): Option[BasePlaygroundModel] = {
    val candidates = gc.getMoveCandidates(moveFrom, moveTo)
    candidates.length match {
      case 0 => Some(newModel)
      case 1 => Some(newModel.copy(newModel.mode.setGameControl(gc.makeMove(candidates.head, newBranchMode, moveForward = true).get)))
      case 2 =>
        val isFlipped = newModel.config.isAreaFlipped(areaId) ^ candidates.head.player.isWhite
        Some(newModel.copy(newMessageBox = Some(HandleDialogMessage(PromotionDialog(candidates.head, isFlipped)))))
      case _ => None // never happens
    }
  }

  private[this] def invokeForward(model: BasePlaygroundModel, areaId: Int, square: Square): Option[BasePlaygroundModel] = {
    model.mode match {
      case m@ViewMode(gc) =>
        val isForward = model.config.isAreaFlipped(areaId) ^ square.file < 5
        val nextGC = isForward.fold(gc.withNextDisplayPosition, gc.withPreviousDisplayPosition)
        Some(model.copy(newMode = m.setGameControl(nextGC)))
      case _ => None
    }
  }
}
