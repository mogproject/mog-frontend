package com.mogproject.mogami.frontend.api

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("MobileWindow")
sealed class MobileWindow extends js.Object {
  def orientation: UndefOr[Int] = js.native
}

@js.native
@JSGlobal("MobileScreen")
sealed class MobileScreen extends js.Object {
  def orientation: UndefOr[Orientation] = js.native
}

@js.native
@JSGlobal("Orientation")
sealed class Orientation extends js.Object {
  def angle: UndefOr[Int] = js.native
}
