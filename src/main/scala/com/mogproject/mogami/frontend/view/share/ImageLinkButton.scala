package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.{CopyButtonLike, DropdownMenu}
import org.scalajs.dom.html.Div

import scalatags.JsDom.all._


class ImageLinkButton extends CopyButtonLike with ViewButtonLike {

  /**
    * definitions of image sizes
    */
  sealed abstract class ImageSize(val w: Int)

  case object Small extends ImageSize(30)

  case object Medium extends ImageSize(40)

  case object Large extends ImageSize(50)

  private[this] val allSizes = Vector(Small, Medium, Large)

  /**
    * Image link buttons
    */
  override protected val ident = "image-link-copy"

  override protected val labelString = "Snapshot Image"

  private[this] val sizeButton = DropdownMenu(
    allSizes,
    DropdownMenu.buildLabels(allSizes),
    dropdownClass = "input-group-btn",
    clickAction = { (_: ImageSize) => updateValueWithSize() },
    dropdownHeader = Some("Image Size")
  )

  override lazy val element: Div = div(
    label(labelString),
    div(cls := "input-group",
      inputElem,
      div(cls := "input-group-btn", sizeButton.element),
      div(
        cls := "input-group-btn",
        viewButton,
        copyButton
      )
    )
  ).render

  override def updateValue(value: String): Unit = updateValueWithSize(Some(value))

  private[this] def updateValueWithSize(baseUrl: Option[String] = None): Unit = {
    val sizeParams = s"&sz=${sizeButton.getValue.w}"
    val base = baseUrl.getOrElse(getValue)
    val url = base.replaceAll("[&]size=\\d+", "") + sizeParams

    super.updateValue(url)
    updateViewUrl(url)
  }

}
