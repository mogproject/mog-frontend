package com.mogproject.mogami.frontend.view.board.canvas

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.model.board._
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, GameControl, Japanese}
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
  private[this] val playerNames = PlayerUtil.getCompletePlayerNames(gameControl.game.gameInfo, config.messageLang, gameControl.isHandicapped)
  private[this] val indicator = BoardIndicator.fromGameStatus(displayState.turn, gameControl.getDisplayingGameStatus)

  object color {
    final val BACKGROUND = "#ffffff"

    /** @note should match with svg.css */
    final val BORDER = "#000000"
    final val TEXT = "#000000"
    final val LAST_MOVE = "#e0e0e0"
    final val PLAYER = "#eeeeee"
    final val HAND_NUMBER_FILL = "#f3f372"
    final val HAND_NUMBER_STROKE = "#333333"

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
    final val HAND_NUMBER = 7
  }

  object font {
    final val DEFAULT = """"Helvetica Neue",Helvetica,Arial,sans-serif"""
    final val HAND_NUMBER = "Tahoma,Geneva,sans-serif"

    object size {
      final val INDICATOR = 60
      final val HAND_NUMBER = 110
    }
  }

  /** set background as white */
  private[this] def drawBackground(): Unit = {
    renderRect(Rect(Coord(0, 0), canvasWidth, canvasHeight), Some(color.BACKGROUND))
  }

  private[this] def drawLastMoveReverse(): Unit = {
    val rs = lastMove.toSeq.flatMap { mv =>
      mv.moveFrom match {
        case Left(sq) => Seq(sq, mv.to).map(s => config.layout.board.getRect(s, !flipped))
        case Right(h) => Seq(config.layout.hand.getRect(h.toPiece, !flipped), config.layout.board.getRect(mv.to, !flipped))
      }
    }
    rs.foreach(renderRect(_, Some(color.LAST_MOVE)))
  }

  private[this] def drawForeground(): Unit = {
    val lo = config.layout.board

    renderRect(lo.boardBorderRect, None, Some(color.BORDER), stroke.BORDER)
    lo.boardLineRects.foreach(renderLine(_, color.BORDER, stroke.LINE)) // drawn after last move
    lo.boardCircleCoords.foreach(renderCircle(_, lo.CIRCLE_SIZE, color.BORDER))
  }

  private[this] def drawBoard(player: Player): Unit = {
    val lo = config.layout.board
    displayState.board.filter(_._2.owner == player).foreach {
      case (sq, pc) => renderPiece(lo.getPieceRect(sq, player.isWhite), pc.ptype)
    }
  }

  private[this] def drawHand(player: Player): Unit = {
    val lo = config.layout.hand

    renderRect(lo.blackRect, None, Some(color.BORDER), stroke.BORDER)
    displayState.hand.foreach {
      case (h, n) if (h.owner == player) && n > 0 =>
        renderPiece(lo.getPieceRect(h.toPiece, player.isWhite), h.ptype)
      case _ =>
    }
  }

  private[this] def drawHandNumbers(player: Player): Unit = {
    val lo = config.layout.hand

    displayState.hand.foreach {
      case (h, n) if (h.owner == player) && n > 1 =>
        renderText(
          lo.getNumberRect(h.toPiece, player.isWhite),
          n.toString,
          font.size.HAND_NUMBER,
          font.HAND_NUMBER,
          color.HAND_NUMBER_FILL,
          isBold = true,
          alignCenter = true,
          Some(color.HAND_NUMBER_STROKE),
          stroke.HAND_NUMBER,
          trim = false
        )
      case _ =>
    }
  }

  private[this] def drawPlayer(player: Player): Unit = {
    val lo = config.layout.player

    renderRect(lo.blackNameRect, Some(color.PLAYER), Some(color.BORDER), stroke.BORDER)
    lo.getIndicatorBackground(BLACK).foreach(renderRect(_, indicator.get(player).flatMap(color.INDICATOR.get)))
    renderText(lo.blackNameArea, playerNames(player), lo.playerNameFontSize, font.DEFAULT, color.TEXT)
    renderImage(lo.blackSymbolArea, lo.getSymbolImagePath(player))
    indicator.get(player).foreach { ind => renderText(lo.blackIndicatorArea, ind.text, lo.indicatorFontSize, font.DEFAULT, color.PLAYER, isBold = true, alignCenter = true) }
  }

  //
  // Indexes
  //
  private[this] def drawIndexes(): Unit = {
    for (i <- 1 to 9) {
      drawFileIndex(i)
      (config.recordLang == Japanese).fold(drawJapaneseRankIndex(i), drawWesternRankIndex(i))
    }
  }

  private[this] def drawFileIndex(index: Int): Unit = {
    val r = config.layout.board.getFileIndexRect(index, flipped)
    renderText(r, index.toString, font.size.INDICATOR, font.DEFAULT, color.TEXT, alignCenter = true)
  }

  private[this] def drawJapaneseRankIndex(index: Int): Unit = {
    val r = config.layout.board.getRankIndexRect(index, flipped)
    renderImage(r, config.layout.board.getJapaneseRankIndexImagePath(index))
  }

  private[this] def drawWesternRankIndex(index: Int): Unit = {
    val r = config.layout.board.getRankIndexRect(index, flipped)
    renderText(r, ('a' + (index - 1)).toChar.toString, font.size.INDICATOR, font.DEFAULT, color.TEXT, alignCenter = true)
  }

  private[this] def draw(callback: () => Unit): Unit = {
    ctx.save()
    rotateCanvas()

    /** @note drawing order matters */
    drawBackground()
    drawLastMoveReverse()
    drawForeground()

    val pl = flipped.fold(BLACK, WHITE)
    drawBoard(pl)
    drawPlayer(pl)
    drawHand(pl)

    waitDraw { () =>
      drawHandNumbers(pl)

      waitDraw { () =>
        ctx.restore()

        drawBoard(!pl)
        drawPlayer(!pl)
        drawHand(!pl)

        drawIndexes()

        waitDraw { () =>
          drawHandNumbers(!pl)
          callback()
        }
      }
    }
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
