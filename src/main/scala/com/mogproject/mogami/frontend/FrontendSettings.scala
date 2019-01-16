package com.mogproject.mogami.frontend

import scala.concurrent.duration._

/**
  * Project level settings
  */
object FrontendSettings {

  val currentYear = 2019

  val imageVersion = 4

  var imageDownloadEnabled = true

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

    object playground {
      val apiVersion = 1
      var apiUrl = "https://kifu.co"
    }

  }

  object timeout {
    val externalRecordDownload: Duration = 30.seconds
  }

  object color {
    val defaultBackground = "#ffffff"
    val defaultCursor = "#e1b265"
    val defaultLastMove = "#e0e0e0"

    val naturalBackground = "#ffd890"
    val naturalCursor = "#f08000"
    val naturalLastMove = "#ffedcc"

    val defaultTheme = (defaultBackground, defaultCursor, defaultLastMove)
    val naturalTheme = (naturalBackground, naturalCursor, naturalLastMove)
  }

  object notes {
    val numColumns = 2
    val mobileCutOffCharacters = 80
  }
}