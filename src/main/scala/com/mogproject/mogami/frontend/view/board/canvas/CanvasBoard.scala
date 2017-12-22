package com.mogproject.mogami.frontend.view.board.canvas

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, GameControl}
import com.mogproject.mogami.frontend.util.PlayerUtil
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.Canvas

import scalatags.JsDom.all._

/**
  * For PNG image creation
  */
case class CanvasBoard(config: BasePlaygroundConfiguration, gameControl: GameControl) extends CanvasRenderer {

  private[this] val canvasWidth = config.layout.viewBoxBottomRight.x
  private[this] val canvasHeight = config.layout.viewBoxBottomRight.y

  protected val cv: Canvas = canvas(widthA := canvasWidth, heightA := canvasHeight).render
  protected val ctx: CanvasRenderingContext2D = cv.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  private[this] val pieceWidth = config.pieceWidth.getOrElse(40)

  private[this] val displayState = gameControl.getDisplayingState
  private[this] val lastMove = gameControl.getDisplayingLastMove
  private[this] val flipped = config.flipType == FlipEnabled
  private[this] val boardPieceReversed = displayState.board.filter(_._2.owner.isBlack ^ flipped)
  private[this] val boardPieceUnturned = displayState.board.filter(_._2.owner.isWhite ^ flipped)
  private[this] val handPieceReversed = displayState.hand.filter { case (h, n) => (h.owner.isBlack ^ flipped) && n > 0 }
  private[this] val handPieceUnturned = displayState.hand.filter { case (h, n) => (h.owner.isWhite ^ flipped) && n > 0 }
  private[this] val playerNames = Player.constructor.map(p => p -> PlayerUtil.getPlayerName(gameControl.game.gameInfo, p, config.messageLang, gameControl.isHandicapped)).toMap
  private[this] val indicator = BoardIndicator.fromGameStatus(displayState.turn, gameControl.getDisplayingGameStatus)

  object color {
    final val BACKGROUND = "#ffffff"

    /** @note should match with svg.css */
    final val BORDER = "#000000"
    final val TEXT = "#000000"
    final val LAST_MOVE = "#e0e0e0"
    final val PLAYER = "#eeeeee"

    final val INDICATOR: Map[BoardIndicator, String] = Map(
      IndicatorTurn -> "#8db6dd",
      IndicatorWin -> "#6cbf6c",
      IndicatorLose -> "#dc6460",
      IndicatorDraw -> "#99877a"
    )
  }

  object stroke {
    final val BORDER = 10
    final val LINE = 5
  }

  object font {
    final val DEFAULT = """"Helvetica Neue",Helvetica,Arial,sans-serif"""
    final val HAND_NUMBER = "Tahoma,Geneva,sans-serif"
  }

  /** set background as white */
  private[this] def drawBackground(): Unit = {
    renderRect(Rect(Coord(0, 0), canvasWidth, canvasHeight), Some(color.BACKGROUND))

    // indexes
  }

  private[this] def drawLastMoveReverse(): Unit = {
    val rs = lastMove.toSeq.flatMap { mv =>
      mv.moveFrom match {
        case Left(sq) => Seq(sq, mv.to).map(s => config.layout.board.getRect(s, flipped))
        case Right(h) => Seq(config.layout.hand.getRect(h.toPiece, flipped), config.layout.board.getRect(mv.to, flipped))
      }
    }
    rs.foreach(renderRect(_, Some(color.LAST_MOVE)))
  }

  private[this] def drawBoardReverse(): Unit = {
    val lo = config.layout.board

    renderRect(lo.boardBorderRect, None, Some(color.BORDER), stroke.BORDER)
    lo.boardLineRects.foreach(renderLine(_, color.BORDER, stroke.LINE)) // drawn after last move
    lo.boardCircleCoords.foreach(renderCircle(_, lo.CIRCLE_SIZE, color.BORDER))
    boardPieceReversed.foreach { case (sq, pc) => renderPiece(lo.getPieceRect(sq, flipped), pc.ptype) }
  }

  private[this] def drawBoardUnturned(): Unit = {
    val lo = config.layout.board

    boardPieceUnturned.foreach { case (sq, pc) => renderPiece(lo.getPieceRect(sq, !flipped), pc.ptype) }
  }

  private[this] def drawHandReverse(): Unit = {
    val lo = config.layout.hand

    Seq(lo.blackRect, lo.whiteRect).foreach(renderRect(_, None, Some(color.BORDER), stroke.BORDER))
    handPieceReversed.foreach { case (h, n) => renderPiece(lo.getPieceRect(h.toPiece, flipped), h.ptype) }
    // num pieces
  }

  private[this] def drawHandUnturned(): Unit = {
    val lo = config.layout.hand

    handPieceUnturned.foreach { case (h, n) => renderPiece(lo.getPieceRect(h.toPiece, !flipped), h.ptype) }
  }

  private[this] def drawPlayer(player: Player): Unit = {
    val lo = config.layout.player

    renderRect(lo.blackNameRect, Some(color.PLAYER), Some(color.BORDER), stroke.BORDER)
    lo.getIndicatorBackground(BLACK).foreach(renderRect(_, indicator.get(player).flatMap(color.INDICATOR.get)))
    renderText(lo.blackNameArea, playerNames(player), lo.playerNameFontSize, font.DEFAULT, color.TEXT)
    renderImage(lo.blackSymbolArea, lo.getSymbolImagePath(player))
    // indicator text
  }

  /**
    * For better image quality, do not scale if the ratio is less than 0.5
    */
  private[this] def resizeGradually(original: Canvas, targetWidth: Int): Canvas = {
    val (w, h) = if (original.width > targetWidth * 2) (original.width / 2, original.height / 2) else (targetWidth, original.height * targetWidth / original.width)

    val cv = canvas(widthA := w, heightA := h).render
    val ctx = cv.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    ctx.drawImage(original, 0, 0, w, h)

    if (w <= targetWidth) cv else resizeGradually(cv, targetWidth)
  }

  private[this] def draw(callback: () => Unit): Unit = {
    val pl1 = flipped.fold(WHITE, BLACK)
    drawBackground()
    drawLastMoveReverse()
    drawBoardReverse()
    drawHandReverse()
    drawPlayer(pl1)

    waitDraw { () =>
      val pl2 = flipped.fold(BLACK, WHITE)
      rotateCanvas()
      drawBoardUnturned()
      drawHandUnturned()
      drawPlayer(pl2)

      waitDraw(callback)
    }
  }

  private[this] def waitDraw(callback: () => Unit): Unit = {
    if (isImageReady) {
      callback()
    } else {
      dom.window.setTimeout(() => waitDraw(callback), 100)
    }
  }

  def processPNGData(callback: String => Unit): Unit = {
    draw(() => {
      // scale image
      val cv2 = resizeGradually(cv, config.layout.areaWidth(pieceWidth))

      // export image
      val data = cv2.toDataURL("image/png")

      // callback
      callback(data)
    })
  }
}
