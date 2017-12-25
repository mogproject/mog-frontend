package com.mogproject.mogami.frontend.view

import org.scalajs.dom
import org.scalajs.dom.raw.SVGImageElement

import scala.collection.mutable
import scala.scalajs.js.Date
import scalatags.JsDom.all._
import scalatags.JsDom.{svgAttrs, svgTags}

/**
  * todo: Use BLOB cache
  */
class ImageCache {
  private[this] val completedUrls: mutable.Set[String] = mutable.Set.empty

  private[this] val processingUrls: mutable.Set[String] = mutable.Set.empty

  /**
    * Blocking download method
    *
    * @param urls      list of urls
    * @param onSuccess callback
    * @param onFailure urls failed to download will be passed
    * @param timeout   in milliseconds
    */
  def download(urls: Seq[String], onSuccess: () => Unit, onFailure: Seq[String] => Unit, timeout: Int = 10000): Unit = {
    // create SVG Image objects
    val requests = urls.filter(!completedUrls.contains(_)).map { url => url -> createImageElement(url) }.toMap

    if (requests.isEmpty) {
      onSuccess()
    } else {
      // add urls to the queue
      processingUrls.clear()
      processingUrls ++= requests.keySet

      // call recursive function
      downloadRec(requests, onSuccess, onFailure, new Date().getTime() + timeout)
    }
  }

  private[this] def createImageElement(url: String): SVGImageElement = {
    val elem: SVGImageElement = svgTags.image(svgAttrs.width := 1, svgAttrs.height := 1).render
    elem.onload = { _: dom.Event =>
      processingUrls.remove(url)
      completedUrls.add(url)
    }
    // must be after setting onload event for mobile Safari
    elem.href.baseVal = url
    elem
  }

  private[this] def downloadRec(requests: Map[String, SVGImageElement], onSuccess: () => Unit, onFailure: Seq[String] => Unit, timeLimit: Double): Unit = {
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
      dom.window.setTimeout(() => downloadRec(requests.filterKeys(processing.contains), onSuccess, onFailure, timeLimit), 100)
    }
  }

}
