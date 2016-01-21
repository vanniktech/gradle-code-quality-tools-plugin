package com.vanniktech.code.quality.tools

import org.junit.Test

class CodeQualityToolsPluginExtensionTest {
    @Test
    public void testDefaults() {
        def extension = new CodeQualityToolsPluginExtension()

        assert extension.failEarly
        assert extension.xmlReports
        assert !extension.htmlReports

        assert extension.findbugs.toolVersion == '3.0.1'
        assert extension.checkstyle.toolVersion == '6.14.1'
        assert extension.pmd.toolVersion == '5.4.1'

        assert extension.findbugs.excludeFilter == 'code_quality_tools/findbugs-filter.xml'
        assert extension.checkstyle.configFile == 'code_quality_tools/checkstyle.xml'
        assert extension.pmd.ruleSetFile == 'code_quality_tools/pmd.xml'

        assert extension.ignoreProjects.size() == 0
    }
}