package com.mogproject.mogami.frontend.coordinate

/**
  * 2-D coordinates
  */
case class Coord(x: Int, y: Int) {
  def unary_- : Coord = Coord(-x, -y)
}