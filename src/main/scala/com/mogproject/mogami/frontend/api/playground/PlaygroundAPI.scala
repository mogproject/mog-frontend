package com.mogproject.mogami.frontend.api.playground

import com.mogproject.mogami.frontend.FrontendSettings
import org.scalajs.dom.ext.{Ajax, AjaxException}
import play.api.libs.json.{Json, Reads, Writes}

import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * General request
  */
trait PlaygroundAPIRequest

/**
  * General response
  */
trait PlaygroundAPIResponse

/**
  * Request
  *
  * @param longUrl Original URL
  */
case class UrlShortenRequest(longUrl: String) extends PlaygroundAPIRequest

/**
  * Response
  *
  * @param id       Shortened URL
  * @param longUrl  Original URL
  * @param imageUrl Image URL
  */
case class UrlShortenResponse(id: String, longUrl: String, imageUrl: String) extends PlaygroundAPIResponse

/**
  * API handler
  *
  * @param apiUrl        URL to the API server
  * @param apiVersion    API version to use
  * @param timeoutMillis connection timeout in milliseconds
  */
case class PlaygroundAPI(apiUrl: String, apiVersion: Int, timeoutMillis: Int = 5000) {
  implicit val urlShortenRequestWrites: Writes[UrlShortenRequest] = Json.writes[UrlShortenRequest]
  implicit val urlShortenResponseReads: Reads[UrlShortenResponse] = Json.reads[UrlShortenResponse]

  private[this] def process[Request <: PlaygroundAPIRequest, Response <: PlaygroundAPIResponse]
  (path: String, request: Request)
  (implicit writes: Writes[Request], reads: Reads[Response]): Future[Option[Response]] = {
    Ajax.post(
      s"${apiUrl}/v${apiVersion}/${path}",
      data = Json.toJson(request).toString,
      timeout = timeoutMillis,
      headers = Map(
        "Content-Type" -> "application/json"
      )
    ).recover {
      case AjaxException(xhr) => xhr
    } map { xhr =>
      xhr.status match {
        case 304 => None // NotModified
        case 400 => throw APIRequestError(xhr.responseText)
        case 500 => throw APIResponseError(xhr.responseText)
        case 200 if xhr.responseText.isEmpty => None // OK
        case 200 =>
          val ret = Try(Json.parse(xhr.responseText).as[Response])
          ret.map(Some.apply).recover { case e: Throwable =>
            throw APIResponseError(s"class=${e.getClass}, error=${e.getMessage}, response=${xhr.responseText}")
          }.get
        case 0 => throw APIConnectionError(xhr.responseText)
        case _ => throw APIResponseError(s"status: ${xhr.status}, text: ${xhr.responseText}")
      }
    }
  }

  private[this] def legalizeUrl(url: String): String = {
    if (url.startsWith(FrontendSettings.url.baseUrl)) url else FrontendSettings.url.baseUrl + url.dropWhile(_ != '?')
  }

  def shortenUrl(longUrl: String): Future[Option[UrlShortenResponse]] = {
    process[UrlShortenRequest, UrlShortenResponse]("shorten", UrlShortenRequest(legalizeUrl(longUrl)))
  }

}