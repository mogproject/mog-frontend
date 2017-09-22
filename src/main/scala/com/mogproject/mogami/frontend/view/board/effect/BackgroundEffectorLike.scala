package com.mogproject.mogami.frontend.view.board.effect

import org.scalajs.dom.raw.SVGElement

trait BackgroundEffectorLike[A, T <: EffectorTarget] extends EffectorLike[A, T] {

  override def materialize(elems: Seq[SVGElement]): Unit = target.materializeBackground(elems)

}
