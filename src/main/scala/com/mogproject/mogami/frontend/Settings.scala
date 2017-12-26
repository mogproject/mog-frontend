package com.mogproject.mogami.frontend

/**
  * Project level settings
  */
trait Settings {

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
        val apiKey: String = "" // needs to be overridden
      }

    }

  }

}


object Settings {
  private[this] var settingsImpl: Settings = new Settings {}

  def setSettings(settings: Settings): Unit = settingsImpl = settings

  def url = settingsImpl.url

  def api = settingsImpl.api
}