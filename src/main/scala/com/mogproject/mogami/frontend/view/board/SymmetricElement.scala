package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.Player
import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import org.scalajs.dom.raw.SVGElement
import com.mogproject.mogami.util.Implicits._

/**
  *
  */
case class SymmetricElement[T <: SVGElement](blackElements: Seq[T], whiteElements: Seq[T])
                                            (implicit flippable: Flippable) {
  def get(player: Player): Seq[T] = flippable.getFlippedPlayer(player).isBlack.fold(blackElements, whiteElements)

  def getFirst(player: Player): T = get(player).head

  def values: Seq[T] = blackElements ++ whiteElements

  def keyValues: Seq[(Player, Seq[T])] = Seq(BLACK -> blackElements, WHITE -> whiteElements)

  def foreach(f: (Player, T) => Unit): Unit = keyValues.foreach { case (pl, xs) => xs.foreach(f(pl, _)) }
}

object SymmetricElement {
  def apply[T <: SVGElement](blackElement: T, whiteElement: T)(implicit flippable: Flippable): SymmetricElement[T] =
    new SymmetricElement(Seq(blackElement), Seq(whiteElement))(flippable)

  def apply[T <: SVGElement](generator: Player => T)(implicit flippable: Flippable): SymmetricElement[T] =
    new SymmetricElement(Seq(generator(BLACK)), Seq(generator(WHITE)))(flippable)
}
