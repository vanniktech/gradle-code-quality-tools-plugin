package com.vanniktech.code.quality.tools

import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class CodeQualityToolsPluginExtensionTest {
  @Test @Suppress("Detekt.LongMethod") fun defaults() {
    val extension = defaultExtensions()

    assertThat(extension.failEarly).isTrue()
    assertThat(extension.xmlReports).isTrue()
    assertThat(extension.htmlReports).isFalse()
    assertThat(extension.textReports).isFalse()

    assertThat(extension.errorProne.toolVersion).isEqualTo("2.1.3")
    assertThat(extension.detekt.toolVersion).isEqualTo("1.0.0.RC6")
    assertThat(extension.ktlint.toolVersion).isEqualTo("0.14.0")
    assertThat(extension.findbugs.toolVersion).isEqualTo("3.0.1")
    assertThat(extension.checkstyle.toolVersion).isEqualTo("8.6")
    assertThat(extension.pmd.toolVersion).isEqualTo("6.0.0")

    assertThat(extension.findbugs.source).isEqualTo("src")
    assertThat(extension.checkstyle.source).isEqualTo("src")
    assertThat(extension.pmd.source).isEqualTo("src")
    assertThat(extension.cpd.source).isEqualTo("src")

    assertThat(extension.checkstyle.include).isEqualTo(listOf("**/*.java"))
    assertThat(extension.pmd.include).isEqualTo(listOf("**/*.java"))

    assertThat(extension.checkstyle.exclude).isEqualTo(listOf("**/gen/**"))
    assertThat(extension.pmd.exclude).isEqualTo(listOf("**/gen/**"))

    assertThat(extension.cpd.language).isEqualTo("java")

    assertThat(extension.findbugs.ignoreFailures).isEqualTo(null)
    assertThat(extension.checkstyle.ignoreFailures).isEqualTo(null)
    assertThat(extension.checkstyle.showViolations).isEqualTo(null)
    assertThat(extension.pmd.ignoreFailures).isEqualTo(null)
    assertThat(extension.cpd.ignoreFailures).isEqualTo(null)

    assertThat(extension.detekt.config).isEqualTo("code_quality_tools/detekt.yml")
    assertThat(extension.findbugs.excludeFilter).isEqualTo("code_quality_tools/findbugs-filter.xml")
    assertThat(extension.checkstyle.configFile).isEqualTo("code_quality_tools/checkstyle.xml")
    assertThat(extension.pmd.ruleSetFile).isEqualTo("code_quality_tools/pmd.xml")

    assertThat(extension.ignoreProjects).isEmpty()

    assertThat(extension.errorProne.enabled).isTrue()
    assertThat(extension.cpd.enabled).isTrue()
    assertThat(extension.detekt.enabled).isTrue()
    assertThat(extension.ktlint.enabled).isTrue()
    assertThat(extension.lint.enabled).isTrue()
    assertThat(extension.findbugs.enabled).isTrue()
    assertThat(extension.checkstyle.enabled).isTrue()
    assertThat(extension.pmd.enabled).isTrue()

    assertThat(extension.lint.textReport).isEqualTo(null)
    assertThat(extension.lint.textOutput).isEqualTo("stdout")
    assertThat(extension.lint.abortOnError).isEqualTo(null)
    assertThat(extension.lint.warningsAsErrors).isEqualTo(null)
    assertThat(extension.lint.checkAllWarnings).isEqualTo(null)
    assertThat(extension.lint.baselineFileName).isEqualTo(null)
  }
}
