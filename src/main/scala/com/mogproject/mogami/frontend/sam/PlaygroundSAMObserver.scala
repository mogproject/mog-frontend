package com.mogproject.mogami.frontend.sam

import com.mogproject.mogami.frontend.model.PlaygroundModel

/**
  *
  */
trait PlaygroundSAMObserver extends SAMObserver[PlaygroundModel] {

  PlaygroundSAM.addObserver(this)

}
