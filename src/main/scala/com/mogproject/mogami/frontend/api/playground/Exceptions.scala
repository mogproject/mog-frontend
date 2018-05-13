package com.mogproject.mogami.frontend.api.playground

case class APIRequestError(message: String) extends RuntimeException(s"RequestError: ${message}")

case class APIResponseError(message: String) extends RuntimeException(s"ResponseError: ${message}")

case class APIConnectionError(message: String) extends RuntimeException(s"ConnectionError: ${message}")
