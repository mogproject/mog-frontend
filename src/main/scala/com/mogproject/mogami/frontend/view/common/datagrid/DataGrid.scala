package com.mogproject.mogami.frontend.view.common.datagrid

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.Messages
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._
import org.scalajs.dom.KeyboardEvent

import scala.collection.mutable

/**
  *
  */
class DataGrid[A <: DataRow](header: Seq[Messages => String],
                             headerClasses: Seq[String],
                             maxDisplayRows: Int,
                             onChange: Option[Int] => Unit) extends WebComponent {
  private[this] var keyDownCounter: Int = 0

  private[this] var selectedRowValue: Option[Int] = None

  // Note: requires O(1) for apply()
  private[this] val rows: mutable.ArrayBuffer[HTMLElement] = mutable.ArrayBuffer.empty

  def selectRow(row: Int): Unit = {
    selectedRowValue.foreach { index => if (index < rows.length) WebComponent.clearClass(rows(index)) }
    selectedRowValue = Some(row)
    if (rows.nonEmpty) {
      val elem = rows(row)
      WebComponent.setClass(elem, DataGrid.SELECTED_CLASS)
      scrollToRow(row)
    }
  }

  def selectedRow: Option[Int] = selectedRowValue

  private[this] def navigate(offset: Int): Unit = {
    onClick(Math.min(Math.max(selectedRowValue.getOrElse(0) + offset, 0), rows.length - 1))
  }

  private[this] def scrollToRow(row: Int): Unit = {
    val r = rows(row)

    if (tableBody.scrollTop < r.offsetTop + r.clientHeight - tableBody.clientHeight) {
      // scroll downwards
      tableBody.scrollTop = r.offsetTop + r.clientHeight - tableBody.clientHeight
    } else if (r.offsetTop < tableBody.scrollTop) {
      // scroll upwards
      tableBody.scrollTop = r.offsetTop
    }
  }

  def updateData(data: Seq[A]): Unit = {
    rows.clear()
    data.zipWithIndex.foreach { case (row, i) =>
      rows += tr(
        onclick := { () => onClick(i) },
        row.toTokens.map { case (s, c) =>
          td(c.nonEmpty.option(cls := c), s)
        }
      ).render
    }

    WebComponent.removeAllChildElements(tableBody)
    rows.foreach(tableBody.appendChild)
  }

  private[this] def onClick(index: Int): Unit = if (!selectedRowValue.contains(index)) {
    onChange(Some(index))
  }

  private[this] lazy val headerElems = header.zip(headerClasses).map { case (f, c) =>
    WebComponent.dynamicTableHeaderCellElement(f, attr("scope") := "col", c.nonEmpty.option(cls := c))
  }

  private[this] lazy val tableElem = table(
    cls := "table table-striped table-hover",
    thead(headerElems.map(_.element)),
    tableBody
  ).render

  private[this] lazy val tableBody = tbody().render

  override lazy val element: HTMLElement = div(
    cls := "move-list",
    tabindex := 0,
    onkeydown := { evt: KeyboardEvent =>
      if (evt.keyCode == 13 || 32 <= evt.keyCode && evt.keyCode <= 40) {
        evt.preventDefault()
        keyDownCounter += 1

        // TODO: get modifier state (e.g. Shift+Space)

        if (keyDownCounter == 1 || keyDownCounter >= DataGrid.KEY_DOWN_REPEAT_WAIT) {
          evt.keyCode match {
            case 39 | 40 | 13 | 32 => navigate(1) // right, down, enter, space
            case 37 | 38 => navigate(-1) // left, up
            case 34 => navigate(maxDisplayRows - 1) // page down
            case 33 => navigate(-maxDisplayRows + 1) // page up
            case 35 => navigate(rows.length) // end
            case 36 => navigate(-rows.length) //home
          }
        }
      }
    },
    onkeyup := { _: KeyboardEvent =>
      keyDownCounter = 0
    },
    tableElem
  ).render
}

object DataGrid {
  protected val SELECTED_CLASS = "info"
  protected val KEY_DOWN_REPEAT_WAIT = 8
}