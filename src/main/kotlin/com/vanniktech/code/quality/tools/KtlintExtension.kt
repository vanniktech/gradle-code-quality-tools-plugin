package com.vanniktech.code.quality.tools

open class KtlintExtension {
  /**
   * Ability to enable or disable only ktlint for every subproject that is not ignored.
   * @since 0.5.0
   */
  var enabled: Boolean = true

  /** @since 0.5.0 */
  var toolVersion: String = "0.32.0"

  /**
   * Ability to enable or disable the experimental rules for ktlint. Requires ktlint 0.31.0 or later.
   * @since 0.18.0
   */
  var experimental: Boolean = false
}
