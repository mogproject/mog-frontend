package com.mogproject.mogami.frontend.view.coordinate

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}

class RectSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks {

  "Rect#getSVGTextStartPoint" must "return the text start point" in {
    val r = Rect(Coord(12, 34), 567, 890)

    r.getSVGTextStartPoint(false, false, None) mustBe Coord(12, 924)
    r.getSVGTextStartPoint(false, true, None) mustBe Coord(295, 924)
    r.getSVGTextStartPoint(true, false, None) mustBe Coord(-580, -35)
    r.getSVGTextStartPoint(true, true, None) mustBe Coord(-296, -35)

    r.getSVGTextStartPoint(false, false, Some((100, 60, false))) mustBe Coord(12, 519)
    r.getSVGTextStartPoint(false, true, Some((100, 60, false))) mustBe Coord(295, 519)
    r.getSVGTextStartPoint(true, false, Some((100, 60, false))) mustBe Coord(-580, -440)
    r.getSVGTextStartPoint(true, true, Some((100, 60, false))) mustBe Coord(-296, -440)

    r.getSVGTextStartPoint(false, false, Some((100, 60, true))) mustBe Coord(295, 34)
    r.getSVGTextStartPoint(false, true, Some((100, 60, true))) mustBe Coord(295, 34)
    r.getSVGTextStartPoint(true, false, Some((100, 60, true))) mustBe Coord(-296, -925)
    r.getSVGTextStartPoint(true, true, Some((100, 60, true))) mustBe Coord(-296, -925)
  }
}
