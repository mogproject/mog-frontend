package com.mogproject.mogami.frontend.view

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLImageElement

import scala.collection.mutable
import scala.scalajs.js.Date
import scalatags.JsDom.all._

/**
  * todo: Use BLOB cache
  */
class ImageCache {
  private[this] val completedUrls: mutable.Set[String] = mutable.Set.empty

  private[this] val processingUrls: mutable.Set[String] = mutable.Set.empty

  private[this] final val CHECK_INTERVAL = 100

  /**
    * Blocking download method
    *
    * @param urls      list of urls
    * @param onSuccess callback
    * @param onFailure urls failed to download will be passed
    * @param timeout   in milliseconds
    */
  def download(urls: Seq[String], onSuccess: () => Unit, onFailure: Seq[String] => Unit, timeout: Int = 10000): Unit = {
    processingUrls.clear()

    // create SVG Image objects
    val requests = urls.filter(!completedUrls.contains(_)).map { url => url -> createImageElement(url) }.toMap

    if (requests.isEmpty) {
      onSuccess()
    } else {
      // call recursive function
      downloadRec(requests, onSuccess, onFailure, new Date().getTime() + timeout)
    }
  }

  /**
    * @note SVGImageElement does NOT fire `onload` events on Safari. Use HTMLImageElement instead.
    *
    * @param url
    * @return
    */
  private[this] def createImageElement(url: String): HTMLImageElement = {
    processingUrls.add(url)

    img(
      src := url,
      width := 1,
      height := 1,
      onload := { (_: dom.Event) =>
        processingUrls.remove(url)
        completedUrls.add(url)
      }
    ).render
  }

  private[this] def downloadRec(requests: Map[String, HTMLImageElement], onSuccess: () => Unit, onFailure: Seq[String] => Unit, timeLimit: Double): Unit = {
    val processing: Set[String] = processingUrls.toSet

    // remove temporary elements
    (requests.keySet -- processing).foreach { url =>
      requests.get(url).foreach(WebComponent.removeElement)
    }

    if (processing.isEmpty) {
      // success
      onSuccess()
    } else if (new Date().getTime() > timeLimit) {
      // timeout
      onFailure(requests.keys.toSeq.sorted)
    } else {
      // continue
      dom.window.setTimeout(() => downloadRec(requests.filterKeys(processing.contains), onSuccess, onFailure, timeLimit), CHECK_INTERVAL)
    }
  }

}
