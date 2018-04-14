package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.frontend.io.TextReader
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled}
import com.mogproject.mogami.frontend.model.io.{CSA, KI2, KIF, RecordFormat}
import com.mogproject.mogami.frontend.state.BasePlaygroundState
import com.mogproject.mogami.frontend.view.{BasePlaygroundView, BrowserInfo}
import com.mogproject.mogami.frontend.view.board.{SVGCompactLayout, SVGStandardLayout}
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.JSApp


/**
  *
  */
trait PlaygroundAppLike[M <: BasePlaygroundModel, V <: BasePlaygroundView, S <: BasePlaygroundState[M, V]] extends JSApp {

  def createModel(mode: Mode, config: BasePlaygroundConfiguration): M

  def createView(config: BasePlaygroundConfiguration, rootElem: Element): V

  def createState(model: M, view: V): S

  def samAdapter: (M, BasePlaygroundModel) => M

  override def main(): Unit = {

    // get args
    val args = Arguments()
      .loadLocalStorage()
      .parseQueryString(dom.window.location.search)
    if (args.config.isDebug) {
      println("Debug Mode enabled.")
      println(s"Sound supported: ${BrowserInfo.isSoundSupported}")
    }
    if (args.config.isDev) println("Dev Mode enabled.")

    // load game
    createGameFromArgs(args).recover {
      case e: Throwable =>
        println(s"Failed to create a game: ${e}")
        Game(args.config.freeMode)
    }.map { game =>
      // update mode
      val isSnapshot = game.trunk.moves.isEmpty && game.trunk.finalAction.isEmpty && game.branches.isEmpty

      val mode = isSnapshot.fold(
        PlayMode(GameControl(game, 0, 0)),
        ViewMode(GameControl(game, args.gamePosition.branch, math.max(0, args.gamePosition.position - game.trunk.offset)))
      )

      // verify config
      val verifiedConfig = args.config.copy(
        soundEffectEnabled = args.config.soundEffectEnabled && BrowserInfo.isSoundSupported,
        flipType = (args.config.embeddedMode && args.config.flipType == DoubleBoard).fold(FlipDisabled, args.config.flipType)
      )

      // create model
      val model = createModel(mode, verifiedConfig)

      // create view
      val rootElem = dom.document.getElementById("app").asInstanceOf[Div]
      val view = createView(verifiedConfig, rootElem)

      // handle special actions
      args.action match {
        case NotesAction =>
          view.drawNotes(game, verifiedConfig.recordLang)
        case ImageAction =>
          // todo: support compact layout
          val conf = if (verifiedConfig.layout == SVGCompactLayout) verifiedConfig.copy(layout = SVGStandardLayout) else verifiedConfig
          view.drawAsImage(conf, mode)
        case PlayAction =>
          // initialize state
          if (verifiedConfig.isDebug) println("Initializing...")

          PlaygroundSAM.initialize(samAdapter)
          SAM.initialize(createState(model, view))

          // hide loading message and show the main contents
          if (verifiedConfig.isDebug) println("Finished initialization.")
          rootElem.style.display = scalatags.JsDom.all.display.block.v
          clearMessageWindow()
      }
    }
  }

  private[this] def createGameFromArgs(args: Arguments): Future[Game] = {
    ((args.externalRecordUrl, args.usen, args.sfen) match {
      case (Some(r), _, _) => loadExternalRecord(r, args.config.freeMode, args.config.isDebug)
      case (None, Some(u), _) => Future(Game.parseUsenString(u, args.config.freeMode)) // parse USEN string
      case (None, None, Some(s)) => Future(Game.parseSfenString(s, args.config.freeMode)) // parse SFEN string
      case _ => Future(Game(args.config.freeMode))
    }).map { game =>

      // update comments
      val comments = for {
        (b, m) <- args.comments
        (pos, c) <- m
        h <- game.getHistoryHash(GamePosition(b, pos))
      } yield h -> c

      game.copy(newGameInfo = args.gameInfo, newComments = game.comments ++ comments)
    }
  }

  private[this] def loadExternalRecord(url: String, freeMode: Boolean, isDebug: Boolean): Future[Game] = {
    // download external file
    println(s"Downloading: ${url}")

    TextReader.readURL(url, FrontendSettings.timeout.externalRecordDownload.toMillis.toInt).map { content =>
      RecordFormat.detect(content) match {
        case CSA => Game.parseCsaString(content, freeMode)
        case KIF => Game.parseKifString(content, freeMode)
        case KI2 => Game.parseKi2String(content, freeMode)
      }
    }
  }

  private[this] def addMessage(msg: String) = {
    import scalatags.JsDom.all._
    dom.document.getElementById("messageWindow").appendChild(p(msg).render)
  }

  private[this] def clearMessageWindow() = {
    dom.document.getElementById("messageWindow").textContent = ""
  }
}
