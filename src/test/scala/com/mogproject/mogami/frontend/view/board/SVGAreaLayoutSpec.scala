package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.core.Player.{BLACK, WHITE}
import com.mogproject.mogami.frontend.view.board.player.SVGPlayerLayout
import com.mogproject.mogami.frontend.view.coordinate.{Coord, Rect}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}

class SVGAreaLayoutSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks {

  "SVGStandardLayout#player" must "have intended coordinates" in {
    SVGStandardLayout.player mustBe SVGPlayerLayout(
      Rect(Coord(1339, 30), 630, 197),
      Rect(Coord(1819, 107), 150, 150),
      Rect(Coord(1339, 147), 480, 84),
      Rect(Coord(1339, 30), 630, 92),
      List(Rect(Coord(44, 232), 1930, 30), Rect(Coord(44, 25), 30, 208)),
      Rect(Coord(79, 2455), 630, 197),
      Rect(Coord(79, 2425), 150, 150),
      Rect(Coord(229, 2455), 480, 84),
      Rect(Coord(79, 2560), 630, 92),
      List(Rect(Coord(74, 2420), 1930, 30), Rect(Coord(1974, 2449), 30, 208))
    )
    SVGStandardLayout.player.getIndicatorArea(BLACK) mustBe Rect(Coord(394, 2540), 630, 92)
    SVGStandardLayout.player.getIndicatorArea(WHITE) mustBe Rect(Coord(1024, 50), 630, 92)
  }


}
