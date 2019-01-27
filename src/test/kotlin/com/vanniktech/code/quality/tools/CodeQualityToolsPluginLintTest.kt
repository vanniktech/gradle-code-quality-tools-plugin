package com.vanniktech.code.quality.tools

import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.api.Project
import org.junit.Test

class CodeQualityToolsPluginLintTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertThat(project.addLint(defaultExtensions())).isFalse()
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertThat(project.addLint(defaultExtensions())).isTrue()
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      assertThat(project.addLint(defaultExtensions())).isTrue()
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertThat(project.addLint(defaultExtensions())).isTrue()

      assertLint(project)
    }
  }

  private fun assertLint(project: Project) {
    assertThat(project.lintOptions.isWarningsAsErrors).isTrue()
    assertThat(project.lintOptions.isAbortOnError).isTrue()
    assertThat(project.lintOptions.textReport).isFalse()
    assertThat(project.lintOptions.textOutput).isEqualTo(null)
    assertThat(project.lintOptions.isCheckAllWarnings).isFalse()
    assertThat(project.lintOptions.baselineFile).isEqualTo(null)

    assertThat(taskDependsOn(project.check, "lint")).isTrue()
  }

  @Test @Suppress("Detekt.LongMethod") fun configurations() {
    val extension = defaultExtensions()
    extension.lint.textReport = true
    extension.lint.textOutput = "stdout"

    extension.lint.abortOnError = false
    extension.lint.warningsAsErrors = false
    extension.lint.checkAllWarnings = true

    extension.lint.baselineFileName = "baseline.xml"

    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.isWarningsAsErrors).isFalse()
    assertThat(androidAppProject.lintOptions.isAbortOnError).isFalse()
    assertThat(androidAppProject.lintOptions.isCheckAllWarnings).isTrue()
    assertThat(androidAppProject.lintOptions.textReport).isTrue()
    assertThat(androidAppProject.lintOptions.isAbsolutePaths).isTrue()
    assertThat(androidAppProject.lintOptions.lintConfig).isEqualTo(null)
    assertThat(androidAppProject.lintOptions.isCheckReleaseBuilds).isFalse()
    assertThat(androidAppProject.lintOptions.baselineFile).isEqualTo(androidAppProject.file("baseline.xml"))
    assertThat(androidAppProject.lintOptions.textOutput.toString()).isEqualTo(extension.lint.textOutput)

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isWarningsAsErrors).isFalse()
    assertThat(androidLibraryProject.lintOptions.isAbortOnError).isFalse()
    assertThat(androidLibraryProject.lintOptions.isCheckAllWarnings).isTrue()
    assertThat(androidLibraryProject.lintOptions.textReport).isTrue()
    assertThat(androidLibraryProject.lintOptions.isAbsolutePaths).isTrue()
    assertThat(androidLibraryProject.lintOptions.lintConfig).isEqualTo(null)
    assertThat(androidLibraryProject.lintOptions.isCheckReleaseBuilds).isFalse()
    assertThat(androidLibraryProject.lintOptions.baselineFile).isEqualTo(androidLibraryProject.file("baseline.xml"))
    assertThat(androidLibraryProject.lintOptions.textOutput.toString()).isEqualTo(extension.lint.textOutput)
  }

  @Test fun configurationsWhenNotFailEarly() {
    val extension = defaultExtensions()
    extension.failEarly = false

    extension.lint.abortOnError = true
    extension.lint.warningsAsErrors = true

    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.isAbortOnError).isTrue()
    assertThat(androidAppProject.lintOptions.isWarningsAsErrors).isTrue()

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isAbortOnError).isTrue()
    assertThat(androidLibraryProject.lintOptions.isWarningsAsErrors).isTrue()
  }

  @Test fun checkAllWarnings() {
    val extension = defaultExtensions()
    extension.failEarly = false

    extension.lint.checkAllWarnings = true

    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.isCheckAllWarnings).isTrue()

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isCheckAllWarnings).isTrue()
  }

  @Test fun absolutePaths() {
    val extension = defaultExtensions()
    extension.lint.absolutePaths = false

    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.isAbsolutePaths).isFalse()

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isAbsolutePaths).isFalse()
  }

  @Test fun checkReleaseBuilds() {
    val extension = defaultExtensions()
    extension.lint.checkReleaseBuilds = true

    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.isCheckReleaseBuilds).isTrue()

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isCheckReleaseBuilds).isTrue()
  }

  @Test fun checkTestSources() {
    val extension = defaultExtensions()
    extension.lint.checkTestSources = true

    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.isCheckTestSources).isTrue()

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isCheckTestSources).isTrue()
  }

  @Test fun checkDependencies() {
    val extension = defaultExtensions()
    extension.lint.checkDependencies = true

    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.isCheckDependencies).isTrue()

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isCheckDependencies).isTrue()
  }

  @Test fun lintConfigFile() {
    val extension = defaultExtensions()

    extension.lint.lintConfig = androidAppProject.file("lint.xml")
    assertThat(androidAppProject.addLint(extension)).isTrue()
    assertThat(androidAppProject.lintOptions.lintConfig).isEqualTo(extension.lint.lintConfig)

    extension.lint.lintConfig = androidLibraryProject.file("lint.xml")
    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.lintConfig).isEqualTo(extension.lint.lintConfig)
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertThat(androidLibraryProject.addLint(extension)).isTrue()
    assertThat(androidLibraryProject.lintOptions.isWarningsAsErrors).isFalse()
    assertThat(androidLibraryProject.lintOptions.isAbortOnError).isFalse()
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.lint.enabled = false

    for (project in projects) {
      assertThat(project.addLint(extension)).isFalse()
    }
  }
}
