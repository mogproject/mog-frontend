package com.mogproject.mogami.frontend

import scala.concurrent.duration._

/**
  * Project level settings
  */
object FrontendSettings {

  val currentYear = 2018

  val imageVersion = 4

  object url {
    val baseUrl = "https://play.mogproject.com/"
    val authorSiteUrl = "https://mogproject.com"
    val authorContactUrl = "https://twitter.com/mogproject"
    val donationUrl = "https://www.paypal.me/mogproject/5"
    val shogiBotUrl = "https://www.facebook.com/shogibot/"
    val playgroundLiveUrl = "https://live.mogproject.com"
    val queryParamDocUrl = "https://github.com/mogproject/mog-playground/wiki/Query-Parameters"

    object credit {
      val shineleckomaUrl = "http://shineleckoma.web.fc2.com/"
      val hidetchiUrl = "http://81dojo.com/"
      val shogiCzUrl = "http://www.shogi.cz/en/"
      val loraFontsUrl = "https://github.com/cyrealtype/Lora-Cyrillic"
    }

  }

  object api {

    object google {

      object URLShortener {
        var apiKey: String = "" // needs to be overridden
      }

    }

  }

  object timeout {
    val externalRecordDownload: Duration = 30.seconds
  }

}