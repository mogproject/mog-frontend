package com.mogproject.mogami.frontend.api.google

import com.mogproject.mogami.frontend.Settings
import gapi.client.urlshortener.Response
import org.scalajs.dom

import scala.scalajs.js

/**
  *
  */
case class URLShortener(baseUrl: String, apiKey: String) {
  private[this] var initialized: Option[Thenable] = None

  private[this] def getClient: Option[Thenable] = {
    initialized match {
      case Some(_) => // do nothing
      case None => initialized = initialize()
    }
    initialized
  }

  private[this] def initialize(): Option[Thenable] = {
    // check if Google API client is ready
    if (apiKey.isEmpty) {
      dom.console.warn("Google API Key is not set.")
      None
    } else if (js.isUndefined(gapi) || js.isUndefined(gapi.client)) {
      // not yet loaded
      None
    } else {
      gapi.client.setApiKey(apiKey)
      Some(gapi.client.load("urlshortener"))
    }
  }

  def legalizeUrl(url: String): String = {
    if (url.startsWith(baseUrl))
      url
    else
      baseUrl + url.dropWhile(_ != '?')
  }

  def makeShortenedURL(longUrl: String, callback: String => Unit, failure: String => Unit): Unit = {
    getClient match {
      case None =>
        failure("Failed: Google API Client is not ready")
      case Some(th) => th.`then` { () =>
        val request = gapi.client.urlshortener.url.insert(RequestParams(legalizeUrl(longUrl)))

        request.execute {
          case (response: Response, _) if response.id != null => callback(response.id.toString)
          case _ => failure("Failed: Cannot create a shortened URL")
        }
      }
    }

  }
}

object URLShortener {
  private[this] lazy val impl = URLShortener(Settings.url.baseUrl, Settings.api.google.URLShortener.apiKey)

  def makeShortenedURL(longUrl: String, callback: String => Unit, failure: String => Unit): Unit = impl.makeShortenedURL(longUrl, callback, failure)
}