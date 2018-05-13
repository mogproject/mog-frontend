package com.mogproject.mogami.frontend.api.playground

import com.mogproject.mogami.frontend.FrontendSettings

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


/**
  *
  */
object URLShortener {

  def makeShortenedURL(longUrl: String, callback: String => Unit, failure: String => Unit): Unit = {
    PlaygroundAPI(FrontendSettings.api.playground.apiUrl, FrontendSettings.api.playground.apiVersion).shortenUrl(longUrl).onComplete {
      case Success(Some(response)) => callback(response.id)
      case Success(None) => failure("Failed: Cannot create a shortened URL")
      case Failure(e) =>
        println(e)
        failure("Failed: Cannot create a shortened URL")
    }
  }

}
