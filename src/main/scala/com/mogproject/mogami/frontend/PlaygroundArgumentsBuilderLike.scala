package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.core.game.Game.{HistoryHash, Position}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache

import scala.collection.mutable
import scala.scalajs.js.URIUtils.encodeURIComponent


trait PlaygroundArgumentsBuilderLike {

  def gameControl: GameControl

  def config: PlaygroundConfiguration

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
