package com.vanniktech.code.quality.tools

open class CpdExtension {
  /**
   * Ability to enable or disable only cpd for every subproject that is not ignored.
   * @since 0.6.0
   */
  var enabled: Boolean = true

  /** @since 0.6.0 */
  var source: String = "src"

  /** @since 0.6.0 */
  var language: String = "java"

  /**
   * If set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}.
   * @since 0.6.0
   */
  var ignoreFailures: Boolean? = null

  /**
   * A positive integer indicating the minimum token count to trigger a CPD match.
   * @since 0.6.0
   */
  var minimumTokenCount: Int = 50
}
