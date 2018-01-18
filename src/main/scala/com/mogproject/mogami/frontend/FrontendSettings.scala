package com.mogproject.mogami.frontend

/**
  * Project level settings
  */
object FrontendSettings {

  val currentYear = 2018

  val imageVersion = 1

  object url {
    val baseUrl = "https://play.mogproject.com/"
    val authorSiteUrl = "https://mogproject.com"
    val authorContactUrl = "https://twitter.com/mogproject"
    val donationUrl = "https://www.paypal.me/mogproject"
    val shogiBotUrl = "https://www.facebook.com/shogibot/"
    val playgroundLiveUrl = "https://live.mogproject.com"

    object credit {
      val shineleckomaUrl = "http://shineleckoma.web.fc2.com/"
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

}