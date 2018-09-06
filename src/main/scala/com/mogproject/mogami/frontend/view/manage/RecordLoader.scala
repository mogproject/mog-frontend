package com.mogproject.mogami.frontend.view.manage

import com.mogproject.mogami.Game
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.action.dialog.MenuDialogAction
import com.mogproject.mogami.frontend.action.game.LoadGameAction
import com.mogproject.mogami.frontend.model.io.{CSA, KI2, KIF, RecordFormat}
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.frontend.FrontendSettings
import com.mogproject.mogami.frontend.io.TextReader
import com.mogproject.mogami.frontend.view.i18n.Messages
import org.scalajs.dom

import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.URIUtils.encodeURIComponent

/**
  *
  */
trait RecordLoader {
  self: SaveLoadButton =>

  protected def loadRecord(fileName: String, content: String, freeMode: Boolean): Unit = {
    val fileType = fileName.split('.').lastOption.mkString
    val result = fileType.toUpperCase match {
      case "CSA" => parseRecord(CSA, content, freeMode)
      case "KIF" => parseRecord(KIF, content, freeMode)
      case "KI2" => parseRecord(KI2, content, freeMode)
      case _ => Failure(new RuntimeException(s"${Messages.get.UNKNOWN_TYPE}: ${fileType}"))
    }

    result match {
      case Success(g) =>
        displayFileLoadMessage(s"${Messages.get.LOAD_SUCCESS}: ${fileName}<br/>${getLoadInfo(g)}")
        displayFileLoadTooltip(Messages.get.LOAD_SUCCESS)

        doAction(LoadGameAction(g))
        doAction(MenuDialogAction(false), 1000) // close menu modal after 1 sec (mobile)

      case Failure(e) =>
        displayFileLoadMessage(s"${Messages.get.ERROR}: ${e.getMessage}")
        displayFileLoadTooltip(Messages.get.LOAD_FAILURE)
    }
  }

  protected def loadRecordText(format: RecordFormat, content: String, freeMode: Boolean): Unit = {
    val result = parseRecord(format, content, freeMode)

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

  private[this] def parseRecord(format: RecordFormat, content: String, freeMode: Boolean): Try[Game] = Try {
    format match {
      case CSA => Game.parseCsaString(content, freeMode)
      case KIF => Game.parseKifString(content, freeMode)
      case KI2 => Game.parseKi2String(content, freeMode)
    }
  }

  private[this] def getLoadInfo(g: Game): String = {
    val ss = Seq(s"${g.trunk.moves.length} ${Messages.get.LOAD_INFO_MOVES}") ++ g.branches.nonEmpty.option(s"${g.branches.length} ${Messages.get.LOAD_INFO_BRANCHES}")
    ss.mkString("(", ", ", ")")
  }

  protected def loadRecordUrl(url: String, freeMode: Boolean): Unit = {
    TextReader.readURL(url, FrontendSettings.timeout.externalRecordDownload.toMillis.toInt).onComplete {
      case Success(content) =>
        displayUrlLoadMessage(Messages.get.LOADING + "...")

        // TODO: simplify
        dom.window.setTimeout(() => {
          Try(RecordFormat.detect(content) match {
            case CSA => Game.parseCsaString(content, freeMode)
            case KIF => Game.parseKifString(content, freeMode)
            case KI2 => Game.parseKi2String(content, freeMode)
          }) match {
            case Success(g) =>
              displayUrlLoadMessage(s"${Messages.get.LOAD_SUCCESS} ${getLoadInfo(g)}")
              externalUrlCopyButton.updateValue(getExternalSharingUrl(url))
              urlLoadInput.showTooltip(Messages.get.LOAD_SUCCESS)
              doAction(LoadGameAction(g))
              doAction(MenuDialogAction(false), 1000) // close menu modal after 1 sec (mobile)

            case Failure(e) =>
              abortUrlLoad(s"[${Messages.get.ERROR}] ${e.getMessage}")
          }
        }, 500)

      case Failure(e) =>
        abortUrlLoad(s"[${Messages.get.ERROR}] ${e.getMessage}")
    }
  }

  private[this] def getExternalSharingUrl(externalUrl: String): String = {
    FrontendSettings.url.baseUrl + "?r=" + encodeURIComponent(externalUrl)
  }
}
