package com.vanniktech.code.quality.tools

import org.junit.Test

class CodeQualityToolsPluginExtensionTest {
  @Test void defaults() {
    def extension = new CodeQualityToolsPluginExtensionForTests()

    assert extension.failEarly
    assert extension.xmlReports
    assert !extension.htmlReports
    assert !extension.textReports

    assert extension.errorProne.toolVersion == '2.1.3'
    assert extension.cpd.toolVersion == '5.4.2'
    assert extension.detekt.toolVersion == '1.0.0.RC6'
    assert extension.ktlint.toolVersion == '0.14.0'
    assert extension.findbugs.toolVersion == '3.0.1'
    assert extension.checkstyle.toolVersion == '8.6'
    assert extension.pmd.toolVersion == '6.0.0'

    assert extension.findbugs.source == 'src'
    assert extension.checkstyle.source == 'src'
    assert extension.pmd.source == 'src'
    assert extension.cpd.source == 'src'

    assert extension.checkstyle.include == ['**/*.java']
    assert extension.pmd.include == ['**/*.java']

    assert extension.checkstyle.exclude == ['**/gen/**']
    assert extension.pmd.exclude == ['**/gen/**']

    assert extension.cpd.language == 'java'

    assert extension.findbugs.ignoreFailures == null
    assert extension.checkstyle.ignoreFailures == null
    assert extension.checkstyle.showViolations == null
    assert extension.pmd.ignoreFailures == null
    assert extension.cpd.ignoreFailures == null

    assert extension.detekt.config == 'code_quality_tools/detekt.yml'
    assert extension.findbugs.excludeFilter == 'code_quality_tools/findbugs-filter.xml'
    assert extension.checkstyle.configFile == 'code_quality_tools/checkstyle.xml'
    assert extension.pmd.ruleSetFile == 'code_quality_tools/pmd.xml'

    assert extension.ignoreProjects.size() == 0

    assert extension.errorProne.enabled
    assert extension.cpd.enabled
    assert extension.detekt.enabled
    assert extension.ktlint.enabled
    assert extension.lint.enabled
    assert extension.findbugs.enabled
    assert extension.checkstyle.enabled
    assert extension.pmd.enabled

    assert extension.lint.textReport == null
    assert extension.lint.textOutput == 'stdout'
    assert extension.lint.abortOnError == null
    assert extension.lint.warningsAsErrors == null
    assert extension.lint.checkAllWarnings == null
    assert extension.lint.baselineFileName == null
  }
}
