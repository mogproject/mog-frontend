package com.mogproject.mogami.frontend.model.io

/**
  * Record Format
  */
sealed trait RecordFormat

case object CSA extends RecordFormat

case object KIF extends RecordFormat

case object KI2 extends RecordFormat

object RecordFormat {
  lazy val all = Vector(CSA, KIF, KI2)

  def detect(s: String): RecordFormat = {
    val ls = s.split("\n")
    if (ls.exists(x => x.startsWith("▲") || x.startsWith("△")))
      KI2
    else if (ls.exists(x => x.startsWith("先手：") || x.startsWith("上手：")))
      KIF
    else
      CSA
  }
}
