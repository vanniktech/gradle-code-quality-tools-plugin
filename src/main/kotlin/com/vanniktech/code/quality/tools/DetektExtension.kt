package com.vanniktech.code.quality.tools

open class DetektExtension {
  /**
   * Ability to enable or disable only detekt for every subproject that is not ignored.
   * @since 0.6.0
   */
  var enabled: Boolean = true

  /** @since 0.6.0 */
  var toolVersion: String = "1.23.6"

  /** @since 0.6.0 */
  var config: String = "code_quality_tools/detekt.yml"

  /**
   * Optional baseline file.
   * If one is present, it will be used in the detektCheck task.
   * If this name is specified, however, the file is not present it will be generated.
   * This mirrors the baseline mechanism from Android Lint.
   *
   * @since 0.11.0
   */
  var baselineFileName: String? = null

  /**
   * Whether to failFast or not. This will be forwarded to the CLI starting with Detekt 1.0.0 RC13
   * @since 0.18.0
   */
  var failFast: Boolean = true

  /**
   * Whether to use preconfigured defaults. Allows provided configurations to override them.
   * @since 0.19.0
   */
  var buildUponDefaultConfig: Boolean = false

  /**
   * Enables parallel compilation of source files.
   * Should only be used if the analyzing project has more than ~200 Kotlin files.
   * @since 0.19.0
   */
  var parallel: Boolean = false

  /**
   * Directories to look for source files.
   * Defaults to current directory.
   *
   * @since 0.21.0
   */
  var input: String = "."
}
