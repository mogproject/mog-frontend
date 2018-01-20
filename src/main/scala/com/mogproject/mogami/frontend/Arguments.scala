package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.core.game.Game.{HistoryHash, Position}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGCompactLayout, SVGStandardLayout, SVGWideLayout}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.scalajs.js.URIUtils.{decodeURIComponent, encodeURIComponent}
import scala.util.{Failure, Success, Try}

/**
  * stores parameters
  */
case class Arguments(sfen: Option[String] = None,
                     usen: Option[String] = None,
                     gameInfo: GameInfo = GameInfo(),
                     gamePosition: GamePosition = GamePosition(0, 0),
                     comments: Map[BranchNo, Map[Position, String]] = Map.empty,
                     action: Action = PlayAction,
                     config: BasePlaygroundConfiguration = BasePlaygroundConfiguration()) {

  def loadLocalStorage(): Arguments = this.copy(config = config.loadLocalStorage())

  def parseQueryString(query: String): Arguments = {

    @tailrec
    def f(sofar: Arguments, ls: List[List[String]]): Arguments = ls match {
      case (x :: s :: Nil) :: xs =>
        (if (x.startsWith("c")) {
          // Comments
          parseGamePosition(x.drop(1)) map { pos =>
            val c = sofar.comments.updated(pos.branch, sofar.comments.getOrElse(pos.branch, Map.empty).updated(pos.position, s))
            sofar.copy(comments = c)
          }
        } else {
          x match {
            case "sfen" => Some(sofar.copy(sfen = Some(s)))
            case "u" => Some(sofar.copy(usen = Some(s)))
            case "mlang" => Language.parseString(s).map(lang => sofar.updateConfig(_.copy(messageLang = lang)))
            case "rlang" => Language.parseString(s).map(lang => sofar.updateConfig(_.copy(recordLang = lang)))
            case "p" => PieceFace.parseString(s).map(pf => sofar.updateConfig(_.copy(pieceFace = pf)))
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
            case "bn" => Some(sofar.copy(gameInfo = sofar.gameInfo.updated('blackName, s)))
            case "wn" => Some(sofar.copy(gameInfo = sofar.gameInfo.updated('whiteName, s)))
            case "free" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(freeMode = b)))
            case "embed" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(embeddedMode = b)))
            case "ve" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(visualEffectEnabled = b)))
            case "se" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(soundEffectEnabled = b)))
            case "dev" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(isDev = b)))
            case "debug" => parseBoolean(s).map(b => sofar.updateConfig(_.copy(isDebug = b)))
            case _ => None
          }
        }) match {
          case Some(a: Arguments) =>
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

  private[this] def parseGamePosition(s: String): Option[GamePosition] = {
    val pattern = raw"(?:([\d])+[.])?([\d]+)".r

    s match {
      case pattern(null, y) => for {yy <- Try(y.toInt).toOption} yield GamePosition(0, yy)
      case pattern(x, y) => for {xx <- Try(x.toInt).toOption; yy <- Try(y.toInt).toOption} yield GamePosition(xx, yy)
      case _ => None
    }
  }

  private[this] def parseBoolean(s: String): Option[Boolean] = {
    s.toLowerCase match {
      case "true" => Some(true)
      case "false" => Some(false)
      case _ => None
    }
  }

  def updateConfig(f: BasePlaygroundConfiguration => BasePlaygroundConfiguration): Arguments = copy(config = f(config))
}

trait ArgumentsBuilderLike {

  def gameControl: GameControl

  def config: BasePlaygroundConfiguration

  private[this] lazy val displayingState: State = gameControl.getDisplayingState

  private[this] def toGamePosition(branchNo: BranchNo, pos: Position) = (branchNo == 0).fold("", branchNo + ".") + pos

  private[this] lazy val gameParams: Seq[(String, String)] = Seq("u" -> gameControl.game.toUsenString) ++ config.freeMode.option("free" -> "true")

  private[this] lazy val instantGameParams: Seq[(String, String)] = Seq("u" -> Game(Branch(displayingState)).toUsenString)

  private[this] lazy val fullRecordUrl: String = createUrl(recordParams(createFullUrl = true))

  /**
    * true if comments are too long and omitted
    */
  lazy val commentOmitted: Boolean = fullRecordUrl.length > 2000

  def toRecordUrl: String = {
    if (commentOmitted) createUrl(recordParams(createFullUrl = false)) else fullRecordUrl
  }

  def toSnapshotUrl: String = {
    createUrl(instantGameParams ++ gameControl.getComment.map("c0" -> _) ++ gameInfoParams)
  }

  def toImageLinkUrl: String = {
    createUrl(imageActionParams ++ gameParams ++ gameInfoParams ++ positionParams)
  }

  private[this] lazy val commentParams: Seq[(String, String)] = {
    val comments = mutable.HashMap[HistoryHash, String]()
    comments ++= gameControl.game.comments

    for {
      (br, n) <- (gameControl.game.trunk, -1) +: gameControl.game.branches.zipWithIndex
      (h, i) <- br.historyHash.zipWithIndex
      c <- comments.get(h)
    } yield {
      comments -= h
      ("c" + toGamePosition(n + 1, i + br.offset), c)
    }
  }

  def toNotesViewUrl: String = createUrl(notesViewActionParams ++ recordParams(createFullUrl = false))

  private[this] def gameInfoParams: Seq[(String, String)] = {
    val params = List(("bn", 'blackName), ("wn", 'whiteName))
    params.flatMap { case (q, k) => gameControl.game.gameInfo.tags.get(k).map((q, _)) }
  }

  private[this] def positionParams: Seq[(String, String)] = {
    val br = gameControl.gamePosition.branch
    val pos = gameControl.displayPosition

    val prefix = (br == 0).fold("", s"${br}.")
    (br != 0 || pos != gameControl.game.trunk.offset).option(
      ("move", s"${prefix}${pos}")
    ).toSeq
  }

  /**
    *
    * @param createFullUrl if true, the url length can exceed the maximum length
    * @return
    */
  protected def recordParams(createFullUrl: Boolean): Seq[(String, String)] =
    gameParams ++ (createFullUrl || !commentOmitted).fold(commentParams, Seq.empty) ++ gameInfoParams ++ positionParams

  protected def snapshotParams: Seq[(String, String)] = instantGameParams ++ gameControl.getComment.map("c0" -> _) ++ gameInfoParams

  private[this] def imageActionParams: Seq[(String, String)] = Seq(("action", "image"))

  private[this] def notesViewActionParams: Seq[(String, String)] = Seq(("action", "notes"))

  protected def createUrl(params: Seq[(String, String)]): String = {
    config.baseUrl + "?" + (params.map { case (k, v) => k + "=" + encodeURIComponent(v) } ++ config.toQueryParameters).mkString("&")
  }

}


case class ArgumentsBuilder(gameControl: GameControl,
                            config: BasePlaygroundConfiguration = BasePlaygroundConfiguration()) extends ArgumentsBuilderLike {
}

case class ArgumentsBuilderEmbed(gameControl: GameControl) extends ArgumentsBuilderLike {
  override val config: BasePlaygroundConfiguration = BasePlaygroundConfiguration()


  def toEmbedCode(isSnapshot: Boolean,
                  pieceWidth: Int,
                  layout: SVGAreaLayout,
                  pieceFace: PieceFace,
                  isFlipped: Boolean,
                  visualEffectEnabled: Boolean,
                  soundEffectEnabled: Boolean,
                  messageLang: Option[Language],
                  recordLang: Option[Language]
                 ): String = {
    val params = isSnapshot.fold(snapshotParams, recordParams(createFullUrl = false)) ++ Seq(
      "embed" -> "true",
      "sz" -> pieceWidth.toString,
      "layout" -> (layout match {
        case SVGStandardLayout => "s"
        case SVGCompactLayout => "c"
        case SVGWideLayout => "w"
      }),
      "p" -> pieceFace.faceId,
      "ve" -> visualEffectEnabled.toString,
      "se" -> soundEffectEnabled.toString
    ) ++ messageLang.map("mlang" -> _.toString) ++ recordLang.map("rlang" -> _.toString) ++ isFlipped.option("flip" -> "true")

    val url = createUrl(params)
    val w = layout.areaWidth(pieceWidth) + 38
    val h = layout.areaHeight(pieceWidth) + 170 + (layout == SVGStandardLayout).fold(10, 0)

    s"""<iframe src="${url}" width="${w}" height="${h}"></iframe>"""
  }
}
