package com.mogproject.mogami.frontend.api.playground

import com.mogproject.mogami.frontend.FrontendSettings

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
object RecordDownloader {
  def downloadRecord(url: String, timeoutMillis: Int): Future[Array[Char]] = {
    PlaygroundAPI(FrontendSettings.api.playground.apiUrl, FrontendSettings.api.playground.apiVersion, timeoutMillis).downloadRecord(url).map {
      case Some(response) if response.error.isEmpty => response.toDecodedCharArray // success
      case Some(response) => throw new IllegalArgumentException(response.error)
      case None => throw new IllegalArgumentException("Empty response")
    }
  }
}
