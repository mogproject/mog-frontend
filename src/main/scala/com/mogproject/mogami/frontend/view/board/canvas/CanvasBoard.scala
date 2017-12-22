package com.mogproject.mogami.frontend.view.board.canvas

import com.mogproject.mogami.frontend.model.board.FlipEnabled
import com.mogproject.mogami.frontend.model.{BasePlaygroundConfiguration, GameControl}
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.Canvas

import scalatags.JsDom.all._

/**
  * For PNG image creation
  */
case class CanvasBoard(config: BasePlaygroundConfiguration, gameControl: GameControl) {

  private[this] val pieceWidth = config.pieceWidth.getOrElse(40)

  private[this] val canvasWidth = config.layout.viewBoxBottomRight.x
  private[this] val canvasHeight = config.layout.viewBoxBottomRight.y

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

  /** set background as white */
  private[this] def drawBackground(ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = "#ffffff"
    ctx.fillRect(0, 0, canvasWidth, canvasHeight)
  }

  private[this] def drawBoard(ctx: CanvasRenderingContext2D): Unit = {
    val lo = config.layout.board

    ctx.strokeStyle = "#000000"
    ctx.fillStyle = "#000000"

    ctx.lineWidth = 10
    ctx.beginPath()
    ctx.rect(lo.boardBorderRect.left, lo.boardBorderRect.top, lo.boardBorderRect.width, lo.boardBorderRect.height)
    ctx.stroke()

    ctx.lineWidth = 5
    lo.boardLineRects.foreach { r =>
      ctx.beginPath()
      ctx.moveTo(r.left, r.top)
      ctx.lineTo(r.right, r.bottom)
      ctx.stroke()
    }

    lo.boardCircleCoords.foreach { c =>
      ctx.beginPath()
      ctx.arc(c.x, c.y, lo.CIRCLE_SIZE, 0, math.Pi * 2, anticlockwise = true)
      ctx.fill()
    }

    displayState.board.foreach { case (sq, pc) =>
      val r = lo.getPieceRect(sq, flipped)
      ctx.drawImage(img(src := config.pieceFace.getImagePath(pc.ptype)).render, r.left, r.top, r.width, r.height)
    }
  }

  private[this] def resizeGradually(original: Canvas, targetWidth: Int): Canvas = {
    val (w, h) = if (original.width > targetWidth * 2) (original.width / 2, original.height / 2) else (targetWidth, original.height * targetWidth / original.width)

    val cv = canvas(widthA := w, heightA := h).render
    val ctx = cv.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    ctx.drawImage(original, 0, 0, w, h)

    if (w <= targetWidth) cv else resizeGradually(cv, targetWidth)
  }

  def toBase64: String = {
    // create a canvas
    val cv = canvas(widthA := canvasWidth, heightA := canvasHeight).render
    val ctx = cv.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    drawBackground(ctx)
    drawBoard(ctx)

    // scale image
    val cv2 = resizeGradually(cv, config.layout.areaWidth(pieceWidth))

    // export image
    cv2.toDataURL("image/png")
  }
}
