package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{BasePlaygroundModel, EditMode}
import com.mogproject.mogami.frontend.model.board.cursor.BoardCursor

/**
  * Change attributes of a selected piece in Edit Mode
  */
case class EditAttributeAction(promoted: Boolean, rotated: Boolean) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    (model.mode, model.selectedCursor) match {
      case (m@EditMode(_, _, b, _, _), Some((areaId, BoardCursor(sq)))) =>
        b.get(sq).map { p =>
          val owner = (model.config.isAreaFlipped(areaId) ^ rotated).fold(WHITE, BLACK)
          val ptype = promoted.fold(p.ptype.promoted, p.ptype.demoted)
          model.copy(newMode = m.copy(board = b.updated(sq, Piece(owner, ptype))), newSelectedCursor = None)
        }
      case _ => None
    }
  }
}
