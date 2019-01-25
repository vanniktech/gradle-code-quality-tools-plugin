package com.vanniktech.code.quality.tools

open class ErrorProneExtension {
  /**
   * Ability to enable or disable only errorprone for every subproject that is not ignored.
   * @since 0.6.0
   */
  var enabled: Boolean = true

  /** @since 0.6.0 */
  var toolVersion: String = "2.1.3"
}
