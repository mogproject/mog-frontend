package com.mogproject.mogami.frontend.bench

case class BenchResult(result: Seq[Double]) {
  def average: Double = if (result.isEmpty) 0.0 else result.sum / result.length / 1000

  def maxValue: Double = result.max / 1000

  def minValue: Double = result.min / 1000

  def print(): Unit = {
    println(f"\n- avg: ${average}%.3fs, min: ${minValue}s, max: ${maxValue}s ${result.map(x => f"${x / 1000}%.3f").mkString("[", ", ", "]")}\n")
  }
}