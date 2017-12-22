package com.mogproject.mogami.frontend.view.board.canvas

import com.mogproject.mogami.frontend.model.board.FlipEnabled
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, GameControl}
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

  // pieceWidth: Int,
  //  layout: SVGAreaLayout,
  //  messageLang: Language, // for default player names
  //  recordLang: Language, // for indexes
  //  pieceFace: PieceFace,
  //  isFlipped: Boolean,
  //  playerNames: Map[Player, String],
  //  state: State,
  //  lastMove: Option[Move]

  object color {
    final val BACKGROUND = "#ffffff"

    /** @note should match with svg.css */
    final val BORDER = "#000000"
    final val LAST_MOVE = "#e0e0e0"
  }

  /** set background as white */
  private[this] def drawBackground(): Unit = {
    renderRect(Rect(Coord(0, 0), canvasWidth, canvasHeight), Some(color.BACKGROUND))
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

    renderRect(config.layout.board.boardBorderRect, None, Some(color.BORDER), 10)
    lo.boardLineRects.foreach(renderLine(_, color.BORDER, 5))
    lo.boardCircleCoords.foreach(renderCircle(_, lo.CIRCLE_SIZE, color.BORDER))

    displayState.board.filter(_._2.owner.isBlack ^ flipped).foreach { case (sq, pc) =>
      renderImage(lo.getPieceRect(sq, flipped), config.pieceFace.getImagePath(pc.ptype))
    }

  }

  private[this] def drawBoardUnturned(): Unit = {
    val lo = config.layout.board

    displayState.board.filter(_._2.owner.isWhite ^ flipped) foreach { case (sq, pc) =>
      renderImage(lo.getPieceRect(!sq, flipped), config.pieceFace.getImagePath(pc.ptype))
    }
  }

  private[this] def resizeGradually(original: Canvas, targetWidth: Int): Canvas = {
    val (w, h) = if (original.width > targetWidth * 2) (original.width / 2, original.height / 2) else (targetWidth, original.height * targetWidth / original.width)

    val cv = canvas(widthA := w, heightA := h).render
    val ctx = cv.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    ctx.drawImage(original, 0, 0, w, h)

    if (w <= targetWidth) cv else resizeGradually(cv, targetWidth)
  }

  private[this] def draw(callback: () => Unit): Unit = {
    drawBackground()
    drawLastMoveReverse()
    drawBoardReverse()

    waitDraw { () =>
      rotateCanvas()
      drawBoardUnturned()
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
