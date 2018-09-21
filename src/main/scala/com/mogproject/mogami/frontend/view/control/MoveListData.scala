package com.mogproject.mogami.frontend.view.control

import com.mogproject.mogami.frontend.view.common.datagrid.Data

/**
  * Data entity for the move list.
  */
case class MoveListData(index: Option[Int],
                        hasComment: Boolean,
                        hasFork: Boolean,
                        moveString: String,
                        elapsedTime: Option[Int]
                       ) extends Data {

}
