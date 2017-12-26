package com.mogproject.mogami.frontend.api

import scalajs.js
import scalajs.js.annotation.JSGlobal

/**
  *
  * @see http://matt-harrison.com/perfect-web-audio-on-ios-devices-with-the-web-audio-api/
  */

@js.native
@JSGlobal("WebAudioAPISound")
class WebAudioAPISound(url: String, options: js.Object = ???) extends js.Object {

  @js.native
  def play(): Unit = js.native

  @js.native
  def stop(): Unit = js.native

  @js.native
  def setVolume(volume: Double): Unit = js.native
}
