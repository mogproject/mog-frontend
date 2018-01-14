package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.core.game.Game.{HistoryHash, Position}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.view.board.{SVGCompactLayout, SVGStandardLayout, SVGWideLayout}

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
      case ("sfen" :: s :: Nil) :: xs => f(sofar.copy(sfen = Some(s)), xs)
      case ("u" :: s :: Nil) :: xs => f(sofar.copy(usen = Some(s)), xs)
      case (x :: s :: Nil) :: xs if x.startsWith("c") => // comments
        parseGamePosition(x.drop(1)) match {
          case Some(pos) =>
            val c = sofar.comments.updated(pos.branch, sofar.comments.getOrElse(pos.branch, Map.empty).updated(pos.position, s))
            f(sofar.copy(comments = c), xs)
          case _ =>
            println(s"Invalid parameter: ${x}=${s}")
            f(sofar, xs)
        }
      case ("mlang" :: s :: Nil) :: xs => Language.parseString(s) match {
        case Some(lang) => f(sofar.copy(config = sofar.config.copy(messageLang = lang)), xs)
        case None =>
          println(s"Invalid parameter: mlang=${s}")
          f(sofar, xs)
      }
      case ("rlang" :: s :: Nil) :: xs => Language.parseString(s) match {
        case Some(lang) => f(sofar.copy(config = sofar.config.copy(recordLang = lang)), xs)
        case None =>
          println(s"Invalid parameter: rlang=${s}")
          f(sofar, xs)
      }
      case ("p" :: s :: Nil) :: xs => PieceFace.parseString(s) match {
        case Some(pf) => f(sofar.copy(config = sofar.config.copy(pieceFace = pf)), xs)
        case None =>
          println(s"Invalid parameter: p=${s}")
          f(sofar, xs)
      }
      case ("move" :: s :: Nil) :: xs => parseGamePosition(s) match {
        case Some(gp) => f(sofar.copy(gamePosition = gp), xs)
        case _ =>
          println(s"Invalid parameter: move=${s}")
          f(sofar, xs)
      }
      case ("flip" :: s :: Nil) :: xs => s.toLowerCase match {
        case "true" => f(sofar.copy(config = sofar.config.copy(flipType = FlipEnabled)), xs)
        case "false" => f(sofar, xs)
        case "double" => f(sofar.copy(config = sofar.config.copy(flipType = DoubleBoard)), xs)
        case _ =>
          println(s"Invalid parameter: flip=${s}")
          f(sofar, xs)
      }
      case ("action" :: s :: Nil) :: xs => s match {
        case "image" => f(sofar.copy(action = ImageAction), xs)
        case "notes" => f(sofar.copy(action = NotesAction), xs)
        case _ =>
          println(s"Invalid parameter: action=${s}")
          f(sofar, xs)
      }
      case ("sz" :: s :: Nil) :: xs => Try(s.toInt) match {
        case Success(n) if n > 0 => f(sofar.copy(config = sofar.config.copy(pieceWidth = Some(n))), xs)
        case _ =>
          println(s"Invalid parameter: sz=${s}")
          f(sofar, xs)
      }
      case ("layout" :: s :: Nil) :: xs => s.toLowerCase match {
        case "s" => f(sofar.copy(config = sofar.config.copy(layout = SVGStandardLayout)), xs)
        case "c" => f(sofar.copy(config = sofar.config.copy(layout = SVGCompactLayout)), xs)
        case "w" => f(sofar.copy(config = sofar.config.copy(layout = SVGWideLayout)), xs)
        case _ =>
          println(s"Invalid parameter: layout=${s}")
          f(sofar, xs)
      }
      case ("device" :: s :: Nil) :: xs => s.toLowerCase match {
        case "1" => f(sofar.copy(config = sofar.config.copy(deviceType = DeviceType.MobilePortrait)), xs)
        case "2" => f(sofar.copy(config = sofar.config.copy(deviceType = DeviceType.MobileLandscape)), xs)
        case _ =>
          println(s"Invalid parameter: device=${s}")
          f(sofar, xs)
      }
      case ("bn" :: s :: Nil) :: xs => f(sofar.copy(gameInfo = sofar.gameInfo.updated('blackName, s)), xs)
      case ("wn" :: s :: Nil) :: xs => f(sofar.copy(gameInfo = sofar.gameInfo.updated('whiteName, s)), xs)

      case ("free" :: s :: Nil) :: xs => s.toLowerCase match {
        case "true" => f(sofar.copy(config = sofar.config.copy(freeMode = true)), xs)
        case "false" => f(sofar.copy(config = sofar.config.copy(freeMode = false)), xs)
        case _ =>
          println(s"Invalid parameter: free=${s}")
          f(sofar, xs)
      }
      case ("dev" :: s :: Nil) :: xs => s.toLowerCase match {
        case "true" => f(sofar.copy(config = sofar.config.copy(isDev = true)), xs)
        case "false" => f(sofar.copy(config = sofar.config.copy(isDev = false)), xs)
        case _ =>
          println(s"Invalid parameter: dev=${s}")
          f(sofar, xs)
      }
      case ("debug" :: s :: Nil) :: xs => s.toLowerCase match {
        case "true" => f(sofar.copy(config = sofar.config.copy(isDebug = true)), xs)
        case "false" => f(sofar.copy(config = sofar.config.copy(isDebug = false)), xs)
        case _ =>
          println(s"Invalid parameter: debug=${s}")
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

}

case class ArgumentsBuilder(gameControl: GameControl,
                            config: BasePlaygroundConfiguration = BasePlaygroundConfiguration()) {

  private[this] lazy val displayingState: State = gameControl.getDisplayingState

  private[this] def toGamePosition(branchNo: BranchNo, pos: Position) = (branchNo == 0).fold("", branchNo + ".") + pos

  private[this] lazy val gameParams: Seq[(String, String)] = Seq("u" -> gameControl.game.toUsenString) ++ config.freeMode.option("free" -> "true")

  private[this] lazy val instantGameParams: Seq[(String, String)] = Seq("u" -> Game(Branch(displayingState)).toUsenString)

  lazy val fullRecordUrl: String = createUrl(recordParams(createFullUrl = true))

  /**
    * true if comments are too long and omitted
    */
  lazy val commentOmitted: Boolean = fullRecordUrl.length > 2000

  def toRecordUrl: String = {
    if (commentOmitted) createUrl(gameParams ++ gameInfoParams ++ positionParams) else fullRecordUrl
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

  def toNotesViewUrl: String = createUrl(notesViewActionParams ++ recordParams())

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

  private[this] def recordParams(createFullUrl: Boolean = false): Seq[(String, String)] =
    gameParams ++ (createFullUrl || !commentOmitted).fold(commentParams, Seq.empty) ++ gameInfoParams ++ positionParams

  private[this] def imageActionParams: Seq[(String, String)] = Seq(("action", "image"))

  private[this] def notesViewActionParams: Seq[(String, String)] = Seq(("action", "notes"))

  private[this] def createUrl(params: Seq[(String, String)]) = {
    config.baseUrl + "?" + (params.map { case (k, v) => k + "=" + encodeURIComponent(v) } ++ config.toQueryParameters).mkString("&")
  }

}