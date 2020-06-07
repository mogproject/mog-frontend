package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.core.game.Game.Position
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.view.board.{SVGCompactLayout, SVGStandardLayout, SVGWideLayout}

import scala.annotation.tailrec
import scala.scalajs.js.URIUtils.decodeURIComponent
import scala.util.Try

/**
  * stores parameters
  */
case class PlaygroundArguments(sfen: Option[String] = None,
                               usen: Option[String] = None,
                               gameInfo: GameInfo = GameInfo(),
                               externalRecordUrl: Option[String] = None,
                               gamePosition: GamePosition = GamePosition(0, 0),
                               comments: Map[BranchNo, Map[Position, String]] = Map.empty,
                               action: Action = PlayAction,
                               config: PlaygroundConfiguration = PlaygroundConfiguration())
  extends ArgumentsLike[PlaygroundConfiguration, PlaygroundArguments] {

  override def updateConfig(f: PlaygroundConfiguration => PlaygroundConfiguration): PlaygroundArguments = copy(config = f(config))

  override def parseQueryString(query: String): PlaygroundArguments = {

    @tailrec
    def f(sofar: PlaygroundArguments, ls: List[List[String]]): PlaygroundArguments = ls match {
      case (x :: s :: Nil) :: xs =>
        (if (x.startsWith("c") && !x.startsWith("co")) {
          // Comments
          parseGamePosition(x.drop(1)) map { pos =>
            val c = sofar.comments.updated(pos.branch, sofar.comments.getOrElse(pos.branch, Map.empty).updated(pos.position, s))
            sofar.copy(comments = c)
          }
        } else {
          x match {
            case "sfen" => Some(sofar.copy(sfen = Some(s)))
            case "u" => Some(sofar.copy(usen = Some(s)))
            case "r" => Some(sofar.copy(externalRecordUrl = Some(s)))
            case "mlang" => Language.parseString(s).map(lang => sofar.updateConfig(_.copy(messageLang = lang)))
            case "rlang" => Language.parseString(s).map(lang => sofar.updateConfig(_.copy(recordLang = lang)))
            case "p" => PieceFace.parseString(s).map(pf => sofar.updateConfig(_.copy(pieceFace = pf)))
            case "colorbg" => parseColor(s).map(c => sofar.updateConfig(_.copy(colorBackground = c)))
            case "colorcs" => parseColor(s).map(c => sofar.updateConfig(_.copy(colorCursor = c)))
            case "colorlm" => parseColor(s).map(c => sofar.updateConfig(_.copy(colorLastMove = c)))
            case "bi" => BoardIndexType.parseString(s).map(it => sofar.updateConfig(_.copy(boardIndexType = it)))
            case "move" => parseGamePosition(s).map(gp => sofar.copy(gamePosition = gp))
            case "flip" => s.toLowerCase match {
              case "true" => Some(sofar.updateConfig(_.copy(flipType = FlipEnabled)))
              case "false" => Some(sofar)
              case "double" => Some(sofar.updateConfig(_.copy(flipType = DoubleBoard)))
              case _ => None
            }
            case "action" => s match {
              case "image" => Some(sofar.copy(action = ImageAction))
              case "notes" => Some(sofar.copy(action = NotesAction))
              case _ => None
            }
            case "sz" => Try(s.toInt).toOption.filter(_ > 0).map(n => sofar.updateConfig(_.copy(pieceWidth = Some(n))))
            case "layout" => s.toLowerCase match {
              case "s" => Some(sofar.updateConfig(_.copy(layout = SVGStandardLayout)))
              case "c" => Some(sofar.updateConfig(_.copy(layout = SVGCompactLayout)))
              case "w" => Some(sofar.updateConfig(_.copy(layout = SVGWideLayout)))
              case _ => None
            }
            case "device" => s match {
              case "1" => Some(sofar.updateConfig(_.copy(deviceType = DeviceType.MobilePortrait)))
              case "2" => Some(sofar.updateConfig(_.copy(deviceType = DeviceType.MobileLandscape)))
              case _ => None
            }
            case "bn" => Some(sofar.copy(gameInfo = sofar.gameInfo.updated(Symbol("blackName"), s)))
            case "wn" => Some(sofar.copy(gameInfo = sofar.gameInfo.updated(Symbol("whiteName"), s)))
            case "free" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(freeMode = b)))
            case "embed" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(embeddedMode = b)))
            case "ve" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(visualEffectEnabled = b)))
            case "se" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(soundEffectEnabled = b)))
            case "dev" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(isDev = b)))
            case "debug" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(isDebug = b)))
            case _ => None
          }
        }) match {
          case Some(a: PlaygroundArguments) =>
            f(a, xs)
          case None =>
            println(s"Invalid parameter: ${x}=${s}")
            f(sofar, xs)
        }
      case x :: xs =>
        if (x.exists(_.nonEmpty)) println(s"Unknown parameter: ${x}")
        f(sofar, xs)
      case Nil => sofar
    }

    f(this, query.stripPrefix("?").split("&").map(s => decodeURIComponent(s).split("=", 2).toList).toList)
  }

}
