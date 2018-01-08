package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.WebComponent
import com.mogproject.mogami.frontend.view.button.{CopyButtonLike, ShortenButtonLike}
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/**
  *
  */
abstract class ShareUnit(id: String, view: Boolean = false) extends WebComponent {

  val copyBar: CopyButtonLike = new CopyButtonLike {
    override protected def ident: String = id + "-copy"

    override protected def viewButtonEnabled: Boolean = view
  }

  val shortenBar: ShortenButtonLike = new ShortenButtonLike {
    override def target: String = copyBar.getValue

    override protected def ident: String = id + "-short"
  }

  override def element: HTMLElement = div(
    copyBar.element,
    shortenBar.element
  ).render

  def updateValue(value: String): Unit = {
    copyBar.updateValue(value)
    shortenBar.clear()
  }
}
