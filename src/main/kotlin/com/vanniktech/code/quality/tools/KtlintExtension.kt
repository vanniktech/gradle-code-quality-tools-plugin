package com.vanniktech.code.quality.tools

open class KtlintExtension {
  /**
   * Ability to enable or disable only ktlint for every subproject that is not ignored.
   * @since 0.5.0
   */
  var enabled: Boolean = true

  /** @since 0.5.0 */
  var toolVersion: String = "1.0.1"
}
