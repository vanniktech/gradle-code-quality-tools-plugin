package com.vanniktech.code.quality.tools

import org.gradle.api.Project

class CodeQualityToolsPluginExtension {
    boolean failEarly = true
    boolean xmlReports = true
    boolean htmlReports = false
    String[] ignoreProjects = []

    static class Findbugs {
        boolean enabled = true
        String toolVersion = '3.0.1'
        String excludeFilter = 'code_quality_tools/findbugs-filter.xml'
    }

    static class Checkstyle {
        boolean enabled = true
        String toolVersion = '6.14.1'
        String configFile = 'code_quality_tools/checkstyle.xml'
    }

    static class Pmd {
        boolean enabled = true
        String toolVersion = '5.4.1'
        String ruleSetFile = 'code_quality_tools/pmd.xml'
    }

    static class Lint {
        boolean enabled = true
        Boolean textReport = null
        String textOutput = 'stdout'
    }
}
