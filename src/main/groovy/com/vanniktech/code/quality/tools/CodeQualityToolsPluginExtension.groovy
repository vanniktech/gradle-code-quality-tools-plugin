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
     * global configuration which will be applied on all enabled code quality tools
     * @since 0.2.0
     */
    boolean xmlReports = true

    /**
     * global configuration which will be applied on all enabled code quality tools
     * @since 0.2.0
     */
    boolean htmlReports = false

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
        Boolean ignoreFailures

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
        String toolVersion = '7.0'

        /** @since 0.2.0 */
        String configFile = 'code_quality_tools/checkstyle.xml'

        /**
         * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
         * @since 0.3.0
         */
        Boolean ignoreFailures

        /**
         * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
         * @since 0.3.0
         */
        Boolean showViolations

        /** @since 0.4.0 */
        String source = 'src'

        /** @since 0.4.0 */
        String include = '**/*.java'

        /** @since 0.4.0 */
        String exclude = '**/gen/**'
    }

    static class Pmd {
        /**
         * ability to enable or disable only pmd for every subproject that is not ignored
         * @since 0.2.0
         */
        boolean enabled = true

        /** @since 0.2.0 */
        String toolVersion = '5.5.1'

        /** @since 0.2.0 */
        String ruleSetFile = 'code_quality_tools/pmd.xml'

        /**
         * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
         * @since 0.3.0
         */
        Boolean ignoreFailures

        /** @since 0.4.0 */
        String source = 'src'

        /** @since 0.4.0 */
        String include = '**/*.java'

        /** @since 0.4.0 */
        String exclude = '**/gen/**'
    }

    static class Lint {
        /**
         * ability to enable or disable only lint for every subproject that is not ignored
         * @since 0.2.0
         */
        boolean enabled = true

        /**
         * Enable or disable textReport
         * @since 0.2.0
         */
        Boolean textReport = null

        /**
         * Specify the textOutput for lint. It will only be used when {@link #textReport} is set to true
         * @since 0.2.0
         */
        String textOutput = 'stdout'

        /**
         * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
         * @since 0.2.0
         */
        Boolean abortOnError

        /**
         * if set to false or true it overrides {@link CodeQualityToolsPluginExtension#failEarly}
         * @since 0.2.0
         */
        Boolean warningsAsErrors
    }
}
