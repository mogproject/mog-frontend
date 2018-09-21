package com.mogproject.mogami.frontend.view.common.datagrid

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.view.WebComponent
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._
import org.scalajs.dom.{Event, KeyboardEvent}
import org.scalajs.jquery.{JQuery, jQuery}

import scala.collection.mutable

/**
  * Generic data grid table.
  */
class DataGrid[A <: Data](columns: Seq[DataColumn[A]],
                          maxDisplayRows: Int,
                          onSelect: Int => Unit) extends WebComponent {
  private[this] var keyDownCounter: Int = 0

  private[this] var selectedRowValue: Option[Int] = None

  // buffer for the text before editing
  private[this] var oldEditableText: String = ""

  // Note: requires O(1) for apply()
  private[this] val dataList: mutable.ArrayBuffer[A] = mutable.ArrayBuffer.empty

  private[this] val rows: mutable.ArrayBuffer[HTMLElement] = mutable.ArrayBuffer.empty

  def selectRow(row: Int): Unit = {
    // clear previously selected row
    selectedRowValue.foreach { index =>
      if (index < rows.length) {
        val prev = rows(index)
        WebComponent.clearClass(prev)
        unsetCellEditable(jQuery(prev).children("td"))
      }
    }

    // select new row
    selectedRowValue = Some(row)
    if (rows.nonEmpty) {
      val elem = rows(row)
      WebComponent.setClass(elem, DataGrid.SELECTED_CLASS)
      columns.zipWithIndex.foreach { case (col, i) =>
        if (col.isEditable(dataList(row)) && col.editableIfSelected) {
          setCellEditable(jQuery(elem).children(s"td:nth-child(${i + 1})"), col, row)
        }
      }

      scrollToRow(row)
    }
  }

  def selectedRow: Option[Int] = selectedRowValue

  private[this] def navigate(offset: Int): Unit = {
    onClick(math.min(math.max(selectedRowValue.getOrElse(0) + offset, 0), rows.length - 1))
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
    dataList.clear()
    rows.clear()

    data.zipWithIndex.foreach { case (d, i) =>
      dataList += d
      rows += tr(
        onclick := { () => onClick(i) },
        columns.map { col =>
          val c = col.dataClass(d)
          val cell = td(
            c.nonEmpty.option(cls := c),
            col.dataConverter(d)
          ).render
          if (col.isEditable(d) && !col.editableIfSelected) setCellEditable(jQuery(cell), col, i)
          cell
        }
      ).render
    }

    WebComponent.removeAllChildElements(tableBody)
    rows.foreach(tableBody.appendChild)
  }

  private[this] def setCellEditable(cell: JQuery, column: DataColumn[A], row: Int): Unit = {
    cell.attr(contenteditable.name, true)
      .on("keydown", { evt: KeyboardEvent =>
        evt.stopPropagation()

        evt.keyCode match {
          case 13 | 9 | 27 => // ENTER, TAB, ESCAPE
            evt.preventDefault()
            cell.blur()
          case _ => // do nothing
        }
      })
      .on("click", { evt: Event => evt.stopPropagation() })
      .on("focus", { _: Event => oldEditableText = cell.text() })
      .on("blur", { _: Event =>
        val text = cell.text()
        cell.text(oldEditableText) // restore original text
        column.onEdit(text) // callback
        element.focus()
      })
  }

  private[this] def unsetCellEditable(cell: JQuery): Unit = {
    cell.removeAttr(contenteditable.name).off("keydown click focus blur")
  }

  private[this] def onClick(index: Int): Unit = if (!selectedRowValue.contains(index)) {
    onSelect(index)
  }

  private[this] lazy val headerElems = columns.map { col =>
    WebComponent.dynamicTableHeaderCellElement(
      col.header, attr("scope") := "col", col.headerClass.nonEmpty.option(cls := col.headerClass)
    )
  }

  private[this] lazy val tableElem = table(
    cls := "table table-striped table-hover table-editable",
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

        if (keyDownCounter == 1 || keyDownCounter >= DataGrid.KEY_DOWN_REPEAT_WAIT) {
          evt.keyCode match {
            case 39 | 40 => navigate(1) // right, down
            case 37 | 38 => navigate(-1) // left, up
            case 13 | 32 => navigate(evt.getModifierState("Shift").fold(-1, 1)) // enter, space
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