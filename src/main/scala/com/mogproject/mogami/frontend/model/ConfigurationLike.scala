package com.mogproject.mogami.frontend.model

/**
  * Base trait for Configuration
  */
trait ConfigurationLike[T <: ConfigurationLike[T]] {
  self: T =>

  def loadLocalStorage(): T

}
