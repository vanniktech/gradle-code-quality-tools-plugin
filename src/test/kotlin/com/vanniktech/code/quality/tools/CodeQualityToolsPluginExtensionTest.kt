package com.vanniktech.code.quality.tools

import org.junit.Assert.assertEquals
import org.junit.Test

class CodeQualityToolsPluginExtensionTest {
  @Test fun defaults() {
    val extension = defaultExtensions()

    assertEquals(true, extension.failEarly)
    assertEquals(true, extension.xmlReports)
    assertEquals(false, extension.htmlReports)
    assertEquals(false, extension.textReports)

    assertEquals("1.0.1", extension.ktlint.toolVersion)
    assertEquals("8.6", extension.checkstyle.toolVersion)
    assertEquals("6.0.0", extension.pmd.toolVersion)

    assertEquals("src", extension.checkstyle.source)
    assertEquals("src", extension.pmd.source)
    assertEquals("src", extension.cpd.source)

    assertEquals(listOf("**/*.java"), extension.checkstyle.include)
    assertEquals(listOf("**/*.java"), extension.pmd.include)

    assertEquals(listOf("**/gen/**"), extension.checkstyle.exclude)
    assertEquals(listOf("**/gen/**"), extension.pmd.exclude)

    assertEquals("java", extension.cpd.language)

    assertEquals(null, extension.checkstyle.ignoreFailures)
    assertEquals(null, extension.checkstyle.showViolations)
    assertEquals(null, extension.pmd.ignoreFailures)
    assertEquals(null, extension.cpd.ignoreFailures)

    assertEquals("code_quality_tools/checkstyle.xml", extension.checkstyle.configFile)
    assertEquals("code_quality_tools/pmd.xml", extension.pmd.ruleSetFile)

    assertEquals(true, extension.ignoreProjects.isEmpty())

    assertEquals(true, extension.cpd.enabled)
    assertEquals(true, extension.ktlint.enabled)
    assertEquals(true, extension.lint.enabled)
    assertEquals(true, extension.checkstyle.enabled)
    assertEquals(true, extension.pmd.enabled)

    assertEquals(true, extension.lint.textReport)
    assertEquals("stdout", extension.lint.textOutput)
    assertEquals(null, extension.lint.abortOnError)
    assertEquals(null, extension.lint.warningsAsErrors)
    assertEquals(null, extension.lint.checkAllWarnings)
    assertEquals(null, extension.lint.baselineFileName)
    assertEquals(null, extension.lint.absolutePaths)
    assertEquals(null, extension.lint.lintConfig)
    assertEquals(false, extension.lint.checkReleaseBuilds)
    assertEquals(true, extension.lint.checkTestSources)
    assertEquals(null, extension.lint.checkDependencies)

    assertEquals(true, extension.kotlin.allWarningsAsErrors)
  }
}
