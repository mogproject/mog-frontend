package com.mogproject.mogami.frontend.view.common.datagrid

/**
  * Represents one data row.
  */
trait DataRow {
  /**
    * Converts one row into a representation format.
    *
    * @return Sequence of (text content, class names)
    */
  def toTokens: Seq[(String, String)]
}
