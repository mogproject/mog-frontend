package com.mogproject.mogami.frontend.view.common.datagrid

import com.mogproject.mogami.frontend.Messages

/**
  * Manages column settings.
  */
case class DataColumn[A <: Data](header: Messages => String,
                                 dataConverter: A => String,
                                 headerClass: String = "",
                                 dataClass: A => String = { _: A => "" },
                                 isEditable: A => Boolean = { _: A => false },
                                 editableIfSelected: Boolean = true,
                                 onEdit: String => Unit = { _: String => }
                                ) {

}
