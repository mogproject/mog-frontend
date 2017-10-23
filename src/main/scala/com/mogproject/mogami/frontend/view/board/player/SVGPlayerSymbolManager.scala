package com.mogproject.mogami.frontend.view.board.player

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.raw.SVGElement

/**
  *
  */
trait SVGPlayerSymbolManager {
  self: SVGPlayer =>

  // local variables
  private[this] var currentSymbolElements: Seq[SVGElement] = Seq.empty

  //
  // Utility
  //
  private[this] def getImagePath(player: Player): String = s"assets/img/p/common/${player.toString.take(2)}.svg"


  //
  // Operation
  //
  def drawSymbols(): Unit = {
    clearSymbols()
    val elems = Seq(
      layout.whiteSymbolArea.toSVGImage(getImagePath(isFlipped.fold(BLACK, WHITE)), rotated = true),
      layout.blackSymbolArea.toSVGImage(getImagePath(isFlipped.fold(WHITE, BLACK)), rotated = false)
    )
    currentSymbolElements = materializeForeground(elems.map(_.render))
  }

  def clearSymbols(): Unit = {
    WebComponent.removeElements(currentSymbolElements)
    currentSymbolElements = Seq.empty
  }
}
