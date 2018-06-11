package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model._


/**
  *
  */
object CopyAllMovesAction extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    val text = model.mode.getGameControl.map(gc => gc.getAllMoveRepresentation(model.config.recordLang).map {
      case (i, s, _, _) => i.map(_ + ": ").getOrElse("") + s
    }.mkString("", "\n", "\n")).getOrElse("")
    Some(model.copy(messageBox = Some(CopyAllMovesMessage(text))))
  }

}
