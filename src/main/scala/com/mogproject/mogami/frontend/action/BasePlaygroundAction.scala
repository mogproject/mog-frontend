package com.mogproject.mogami.frontend.action

import com.mogproject.mogami.frontend.model.BasePlaygroundModel
import com.mogproject.mogami.frontend.sam.SAMAction

/**
  *
  */
trait BasePlaygroundAction[+Model <: BasePlaygroundModel] extends SAMAction[Model] {

}
