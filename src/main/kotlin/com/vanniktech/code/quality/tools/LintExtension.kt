package com.vanniktech.code.quality.tools

import java.io.File

open class LintExtension {
  /**
   * Ability to enable or disable only lint for every subproject that is not ignored.
   * @since 0.2.0
   */
  var enabled: Boolean = true

  /**
   * Enable or disable textReport.
   * @since 0.2.0
   */
  var textReport: Boolean? = null

  /**
   * Specify the textOutput for lint. It will only be used when [textReport] is set to true.
   * @since 0.2.0
   */
  var textOutput: String = "stdout"

  /**
   * If set to false or true it overrides [CodeQualityToolsPluginExtension#failEarly].
   * @since 0.2.0
   */
  var abortOnError: Boolean? = null

  /**
   * If set to false or true it overrides [CodeQualityToolsPluginExtension#failEarly].
   * @since 0.2.0
   */
  var warningsAsErrors: Boolean? = null

  /**
   * Returns whether lint should check all warnings, including those off by default.
   * @since 0.5.0
   */
  var checkAllWarnings: Boolean? = null

  /**
   * The baseline file name (e.g. baseline.xml) which will be saved under each project.
   * @since 0.5.0
   */
  var baselineFileName: String? = null

  /**
   * Returns whether lint should use absolute paths or not.
   * @since 0.9.0
   */
  var absolutePaths: Boolean? = null

  /**
   * The lint config file (e.g. lint.xml).
   * @since 0.9.0
   */
  var lintConfig: File? = null

  /**
   * Returns whether lint should check release builds or not.
   * Since this plugin hooks lint into the check task we'll assume that you're always running
   * the full lint suite and hence checking release builds is not necessary.
   * @since 0.10.0
   */
  var checkReleaseBuilds: Boolean? = false

  /**
   * Returns whether lint should check test sources or not.
   * @since 0.13.0
   */
  var checkTestSources: Boolean? = null

  /**
   * Returns whether lint should check dependencies or not.
   * @since 0.13.0
   */
  var checkDependencies: Boolean? = null
}
