package com.vanniktech.code.quality.tools

open class CodeQualityToolsPluginExtension {
  /**
   * When set to true all enabled code quality tools will be configured in a way that even a single warning / error will fail the build process.
   * @since 0.2.0
   */
  var failEarly: Boolean = true

  /**
   * Global configuration which will be applied on all enabled code quality tools that support xml reports.
   * @since 0.2.0
   */
  var xmlReports: Boolean = true

  /**
   * Global configuration which will be applied on all enabled code quality tools that support html reports.
   * @since 0.2.0
   */
  var htmlReports: Boolean = false

  /**
   * Global configuration which will be applied on all enabled code quality tools that support text reports.
   * @since 0.6.0
   */
  var textReports: Boolean = false

  /**
   * List of subprojects identified by their name that should be ignored.
   * @since 0.2.0
   */
  var ignoreProjects: Array<String> = emptyArray()
}
