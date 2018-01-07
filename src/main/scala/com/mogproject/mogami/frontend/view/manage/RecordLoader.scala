package com.mogproject.mogami.frontend.view.manage

import com.mogproject.mogami.Game
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.dialog.MenuDialogAction
import com.mogproject.mogami.frontend.action.game.LoadGameAction
import com.mogproject.mogami.frontend.model.io.{CSA, KI2, KIF, RecordFormat}
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.frontend.view.i18n.Messages

import scala.util.{Failure, Success, Try}

/**
  *
  */
trait RecordLoader {
  self: SaveLoadButton =>

  protected def loadRecord(fileName: String, content: String): Unit = {

    val fileType = fileName.split('.').lastOption.mkString
    val result = fileType.toUpperCase match {
      case "CSA" => Try(Game.parseCsaString(content))
      case "KIF" => Try(Game.parseKifString(content))
      case "KI2" => Try(Game.parseKi2String(content))
      case _ => Failure(new RuntimeException(s"${Messages.get.UNKNOWN_TYPE}: ${fileType}"))
    }

    result match {
      case Success(g) =>
        displayFileLoadMessage(s"${Messages.get.LOAD_SUCCESS}: ${fileName}<br/>${getLoadInfo(g)}")
        displayFileLoadTooltip(Messages.get.LOAD_FAILURE)

        doAction(LoadGameAction(g))
        doAction(MenuDialogAction(false), 1000) // close menu modal after 1 sec (mobile)

      case Failure(e) =>
        displayFileLoadMessage(s"${Messages.get.ERROR}: ${e.getMessage}")
        displayFileLoadTooltip(Messages.get.LOAD_FAILURE)
    }
  }

  protected def loadRecordText(format: RecordFormat, content: String): Unit = {
    val result = format match {
      case CSA => Try(Game.parseCsaString(content))
      case KIF => Try(Game.parseKifString(content))
      case KI2 => Try(Game.parseKi2String(content))
    }

    result match {
      case Success(g) =>
        displayFileLoadMessage("")
        displayTextLoadMessage("")
        displayTextLoadTooltip(Messages.get.LOAD_SUCCESS)

        doAction(LoadGameAction(g))
        doAction(MenuDialogAction(false), 1000) // close menu modal after 1 sec (mobile)

      case Failure(e) =>
        displayTextLoadMessage(s"${Messages.get.ERROR}: ${e.getMessage}")
        displayTextLoadTooltip(Messages.get.LOAD_FAILURE)
    }
  }

  private[this] def getLoadInfo(g: Game): String = {
    val ss = Seq(s"${g.trunk.moves.length} ${Messages.get.LOAD_INFO_MOVES}") ++ g.branches.nonEmpty.option(s"${g.branches.length} ${Messages.get.LOAD_INFO_BRANCHES}")
    ss.mkString("(", ", ", ")")
  }

}
