package com.vanniktech.code.quality.tools

open class PmdExtension {
  /**
   * Ability to enable or disable only pmd for every subproject that is not ignored.
   * @since 0.2.0
   */
  var enabled: Boolean = true

  /** @since 0.2.0 */
  var toolVersion: String = "6.0.0"

  /** @since 0.2.0 */
  var ruleSetFile: String = "code_quality_tools/pmd.xml"

  /**
   * If set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}.
   * @since 0.3.0
   */
  var ignoreFailures: Boolean? = null

  /** @since 0.4.0 */
  var source: String = "src"

  /** @since 0.4.0 */
  var include: List<String> = listOf("**/*.java")

  /** @since 0.4.0 */
  var exclude: List<String> = listOf("**/gen/**")
}
