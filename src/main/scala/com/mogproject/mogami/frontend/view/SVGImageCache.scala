package com.mogproject.mogami.frontend.view

import com.mogproject.mogami.frontend.{FrontendSettings, LocalStorage}
import org.scalajs.dom.ext.{Ajax, AjaxException}
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, URL}

import scala.collection.mutable
import scala.scalajs.js
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * SVG Image Cache
  */
class SVGImageCache {
  private[this] val cacheData: mutable.Map[String, String] = mutable.Map.empty

  private[this] val processingUrls: mutable.Set[String] = mutable.Set.empty

  def getURL(url: String): String = {
    cacheData.getOrElse(url, url)
  }

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

    // filter urls
    val requestUrls = urls.filter(!cacheData.contains(_))

    // request downloading
    val requests = requestUrls.map(u => downloadImage(u, timeout))

    Future.sequence(requests).onComplete {
      case Success(_) => onSuccess()
      case Failure(_) => onFailure(processingUrls.toSeq.sorted)
    }
  }

  /**
    * @note SVGImageElement does NOT fire `onload` events on Safari. Use HTMLImageElement instead.
    * @param url
    * @return
    */
  private[this] def downloadImage(url: String, timeout: Int): Future[String] = {
    if (FrontendSettings.imageDownloadEnabled) {
      processingUrls += url

      // check Local Storage
      LocalStorage.loadImage(url, FrontendSettings.imageVersion) match {
        case Some(data) => Future(createObjectURL(url, data))
        case None =>
          Ajax.get(url, timeout = timeout).recover {
            case AjaxException(xhr) => xhr
          } map { xhr =>
            xhr.status match {
              case 200 => xhr.responseText
              case _ =>
                val msg = s"Failed to download image: url=${url} status=${xhr.status}"
                println(msg)
                throw new RuntimeException(msg)
            }
          } map { data =>
            // save to Local Storage
            LocalStorage.saveImage(url, FrontendSettings.imageVersion, data)
            createObjectURL(url, data)
          }
      }
    } else {
      // for testing / benchmark
      Future("")
    }
  }

  private[this] def createObjectURL(url: String, data: String): String = {
    // Do not add ";charset=utf-8". This will break data on Safari.
    val objUrl = URL.createObjectURL(new Blob(js.Array(data.asInstanceOf[js.Any]), BlobPropertyBag("image/svg+xml")))
    if (!cacheData.contains(url)) {
      cacheData += url -> objUrl
      processingUrls.remove(url)
    }
    objUrl
  }

}
