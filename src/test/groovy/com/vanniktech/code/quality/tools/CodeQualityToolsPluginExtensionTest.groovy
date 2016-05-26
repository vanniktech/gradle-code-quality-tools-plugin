package com.vanniktech.code.quality.tools

import org.junit.Test

class CodeQualityToolsPluginExtensionTest {
    @Test
    public void testDefaults() {
        def extension = new CodeQualityToolsPluginExceptionForTests()

        assert extension.failEarly
        assert extension.xmlReports
        assert !extension.htmlReports

        assert extension.findbugs.toolVersion == '3.0.1'
        assert extension.checkstyle.toolVersion == '6.18'
        assert extension.pmd.toolVersion == '5.4.1'

        assert extension.findbugs.ignoreFailures == null
        assert extension.checkstyle.ignoreFailures == null
        assert extension.checkstyle.showViolations == null
        assert extension.pmd.ignoreFailures == null

        assert extension.findbugs.excludeFilter == 'code_quality_tools/findbugs-filter.xml'
        assert extension.checkstyle.configFile == 'code_quality_tools/checkstyle.xml'
        assert extension.pmd.ruleSetFile == 'code_quality_tools/pmd.xml'

        assert extension.ignoreProjects.size() == 0

        assert extension.lint.enabled
        assert extension.findbugs.enabled
        assert extension.checkstyle.enabled
        assert extension.pmd.enabled

        assert extension.lint.textReport == null
        assert extension.lint.textOutput == 'stdout'
        assert extension.lint.abortOnError == null
        assert extension.lint.warningsAsErrors == null
    }
}
