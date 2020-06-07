package com.mogproject.mogami.frontend.view.control

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class MoveListColumnSpec extends AnyFlatSpec with Matchers {
  "MoveListColumn#validateElapsedTime" must "validate time expression" in {
    MoveListColumn.validateElapsedTime("") mustBe None
    MoveListColumn.validateElapsedTime("0") mustBe Some(0)
    MoveListColumn.validateElapsedTime("0000000000") mustBe Some(0)
    MoveListColumn.validateElapsedTime(":0") mustBe Some(0)
    MoveListColumn.validateElapsedTime(":00") mustBe Some(0)
    MoveListColumn.validateElapsedTime(":000") mustBe Some(0)
    MoveListColumn.validateElapsedTime("0:0") mustBe Some(0)
    MoveListColumn.validateElapsedTime("00:0") mustBe Some(0)
    MoveListColumn.validateElapsedTime("000:0") mustBe Some(0)
    MoveListColumn.validateElapsedTime("1") mustBe Some(1)
    MoveListColumn.validateElapsedTime("01") mustBe Some(1)
    MoveListColumn.validateElapsedTime("00000000001") mustBe Some(1)
    MoveListColumn.validateElapsedTime(":01") mustBe Some(1)
    MoveListColumn.validateElapsedTime("0:01") mustBe Some(1)
    MoveListColumn.validateElapsedTime("00:01") mustBe Some(1)
    MoveListColumn.validateElapsedTime("000:01") mustBe Some(1)
    MoveListColumn.validateElapsedTime("999:59") mustBe Some(59999)
    MoveListColumn.validateElapsedTime("0:99999") mustBe Some(59999)
    MoveListColumn.validateElapsedTime("999:99") mustBe Some(59999)
    MoveListColumn.validateElapsedTime("9999999999") mustBe Some(59999)
    MoveListColumn.validateElapsedTime("9999999999:") mustBe None
    MoveListColumn.validateElapsedTime("9999999999:0") mustBe Some(59999)
  }
}
