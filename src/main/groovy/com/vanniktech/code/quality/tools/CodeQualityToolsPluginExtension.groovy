package com.vanniktech.code.quality.tools

/**
 * Extension for code quality
 * @since 0.2.0
 */
class CodeQualityToolsPluginExtension {
  /**
   * when set to true all enabled code quality tools will be configured in a way that even a single warning / error will fail the build process
   * @since 0.2.0
   */
  boolean failEarly = true

  /**
   * global configuration which will be applied on all enabled code quality tools that support xml reports
   * @since 0.2.0
   */
  boolean xmlReports = true

  /**
   * global configuration which will be applied on all enabled code quality tools that support html reports
   * @since 0.2.0
   */
  boolean htmlReports = false

  /**
   * global configuration which will be applied on all enabled code quality tools that support text reports
   * @since 0.6.0
   */
  boolean textReports = false

  /**
   * subprojects that should be ignored
   * @since 0.2.0
   */
  String[] ignoreProjects = []

  static class Findbugs {
    /**
     * ability to enable or disable only findbugs for every subproject that is not ignored
     * @since 0.2.0
     */
    boolean enabled = true

    /** @since 0.2.0 */
    String toolVersion = '3.0.1'

    /** @since 0.2.0 */
    String excludeFilter = 'code_quality_tools/findbugs-filter.xml'

    /**
     * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
     * @since 0.3.0
     */
    Boolean ignoreFailures = null

    /** @since 0.4.0 */
    String source = 'src'

    /** @since 0.4.0 */
    String effort = 'max'

    /** @since 0.4.0 */
    String reportLevel = 'low'
  }

  static class Checkstyle {
    /**
     * ability to enable or disable only checkstyle for every subproject that is not ignored
     * @since 0.2.0
     */
    boolean enabled = true

    /** @since 0.2.0 */
    String toolVersion = '8.6'

    /** @since 0.2.0 */
    String configFile = 'code_quality_tools/checkstyle.xml'

    /**
     * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
     * @since 0.3.0
     */
    Boolean ignoreFailures = null

    /**
     * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
     * @since 0.3.0
     */
    Boolean showViolations = null

    /** @since 0.4.0 */
    String source = 'src'

    /** @since 0.4.0 */
    List<String> include = ['**/*.java']

    /** @since 0.4.0 */
    List<String> exclude = ['**/gen/**']
  }

  static class Pmd {
    /**
     * ability to enable or disable only pmd for every subproject that is not ignored
     * @since 0.2.0
     */
    boolean enabled = true

    /** @since 0.2.0 */
    String toolVersion = '6.0.0'

    /** @since 0.2.0 */
    String ruleSetFile = 'code_quality_tools/pmd.xml'

    /**
     * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
     * @since 0.3.0
     */
    Boolean ignoreFailures = null

    /** @since 0.4.0 */
    String source = 'src'

    /** @since 0.4.0 */
    List<String> include = ['**/*.java']

    /** @since 0.4.0 */
    List<String> exclude = ['**/gen/**']
  }

  static class Cpd {
    /**
     * ability to enable or disable only cpd for every subproject that is not ignored
     * @since 0.6.0
     */
    boolean enabled = true

    /** @since 0.6.0 */
    String source = 'src'

    /** @since 0.6.0 */
    String language = 'java'

    /**
     * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
     * @since 0.6.0
     */
    Boolean ignoreFailures = null

    /**
     * A positive integer indicating the minimum token count to trigger a CPD match
     * @since 0.6.0
     */
    int minimumTokenCount = 50
  }

  static class ErrorProne {
    /**
     * ability to enable or disable only errorprone for every subproject that is not ignored
     * @since 0.6.0
     */
    boolean enabled = true

    /** @since 0.6.0 */
    String toolVersion = '2.1.3'
  }
}
