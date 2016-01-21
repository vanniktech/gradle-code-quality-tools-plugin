package com.vanniktech.code.quality.tools

class CodeQualityToolsPluginExtension {
    boolean failEarly = true
    boolean xmlReports = true
    boolean htmlReports = false
    String[] ignoreProjects = []

    Findbugs findbugs = new Findbugs()
    Checkstyle checkstyle = new Checkstyle()
    Pmd pmd = new Pmd()

    static class Findbugs {
        String toolVersion = '3.0.1'
        String excludeFilter = 'code_quality_tools/findbugs-filter.xml'
    }

    static class Checkstyle {
        String toolVersion = '6.14.1'
        String configFile = 'code_quality_tools/checkstyle.xml'
    }

    static class Pmd {
        String toolVersion = '5.4.1'
        String ruleSetFile = 'code_quality_tools/pmd.xml'
    }
}
