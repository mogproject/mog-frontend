package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.frontend.view.board.player.SVGPlayerLayout
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}

class SVGAreaLayoutSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks {

  "SVGStandardLayout#board" must "have intended coordinates" in {
    SVGStandardLayout.board.center mustBe Coord(1024, 1341)
  }

  "SVGStandardLayout#player" must "have intended coordinates" in {

    SVGStandardLayout.player mustBe SVGPlayerLayout(
      Coord(1024,1341),
      Rect(Coord(79,2455),630,197),
      Rect(Coord(79,2425),150,150),
      Rect(Coord(229,2460),480,105),
      Rect(Coord(84,2560),620,87),
      List(Rect(Coord(74,2420),1930,30), Rect(Coord(1974,2449),30,208)),80,80,false)

    SVGStandardLayout.player.getIndicatorTextArea(BLACK) mustBe Rect(Coord(394, 2560), 0, 87)
    SVGStandardLayout.player.getIndicatorTextArea(WHITE) mustBe Rect(Coord(1654, 35), 0, 87)
  }


}
