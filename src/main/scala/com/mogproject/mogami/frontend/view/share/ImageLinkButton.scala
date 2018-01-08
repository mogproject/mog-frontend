package com.mogproject.mogami.frontend.view.share

import com.mogproject.mogami.frontend.view.button.{CopyButtonLike, DropdownMenu}
import org.scalajs.dom.Element


/**
  * definitions of image sizes
  */
sealed abstract class ImageSize(val w: Int)

object ImageSize {

  case object Small extends ImageSize(30)

  case object Medium extends ImageSize(40)

  case object Large extends ImageSize(50)

  val all = Vector(Small, Medium, Large)

}

class ImageLinkButton extends CopyButtonLike {

  /**
    * Image link buttons
    */
  override protected val ident = "image-link-copy"

  private[this] val sizeButton = {
    val d = DropdownMenu(
      ImageSize.all,
      _.IMAGE_SIZE_OPTIONS,
      dropdownClass = "input-group-btn",
      clickAction = { (_: ImageSize) => updateValueWithSize() },
      dropdownHeader = Some(_.IMAGE_SIZE)
    )
    d.select(ImageSize.Medium)
    d
  }

  override protected def rightButton: Option[Element] = Some(sizeButton.element)

  override protected def viewButtonEnabled: Boolean = true

  override def updateValue(value: String): Unit = updateValueWithSize(Some(value))

  private[this] def updateValueWithSize(baseUrl: Option[String] = None): Unit = {
    val sizeParams = s"&sz=${sizeButton.getValue.w}"
    val base = baseUrl.getOrElse(getValue)
    val url = base.replaceAll("[&]sz=\\d+", "") + sizeParams

    super.updateValue(url)
  }

}
