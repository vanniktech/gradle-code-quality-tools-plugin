package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class CodeQualityToolsPluginLintTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertEquals(false, project.addLint(defaultExtensions()))
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertEquals(true, project.addLint(defaultExtensions()))
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      assertEquals(false, project.addLint(defaultExtensions()))
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertEquals(true, project.addLint(defaultExtensions()))

      assertLint(project)
    }
  }

  private fun assertLint(project: Project) {
    assertEquals(true, project.lintOptions.isWarningsAsErrors)
    assertEquals(true, project.lintOptions.isAbortOnError)
    assertEquals(true, project.lintOptions.textReport)
    assertEquals(File("stdout"), project.lintOptions.textOutput)
    assertEquals(false, project.lintOptions.isCheckAllWarnings)
    assertEquals(null, project.lintOptions.baselineFile)

    assertEquals(true, taskDependsOn(project.check, "lint"))
  }

  @Test @Suppress("Detekt.LongMethod") fun configurations() {
    val extension = defaultExtensions()
    extension.lint.textReport = null
    extension.lint.textOutput = "stdout"

    extension.lint.abortOnError = false
    extension.lint.warningsAsErrors = false
    extension.lint.checkAllWarnings = true

    extension.lint.baselineFileName = "baseline.xml"

    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(false, androidAppProject.lintOptions.isWarningsAsErrors)
    assertEquals(false, androidAppProject.lintOptions.isAbortOnError)
    assertEquals(true, androidAppProject.lintOptions.isCheckAllWarnings)
    assertEquals(false, androidAppProject.lintOptions.textReport)
    assertEquals(true, androidAppProject.lintOptions.isAbsolutePaths)
    assertEquals(null, androidAppProject.lintOptions.lintConfig)
    assertEquals(false, androidAppProject.lintOptions.isCheckReleaseBuilds)
    assertEquals(true, androidAppProject.lintOptions.isCheckTestSources)
    assertEquals(false, androidAppProject.lintOptions.isCheckDependencies)
    assertEquals(androidAppProject.file("baseline.xml"), androidAppProject.lintOptions.baselineFile)
    assertEquals(null, androidAppProject.lintOptions.textOutput)

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(false, androidLibraryProject.lintOptions.isWarningsAsErrors)
    assertEquals(false, androidLibraryProject.lintOptions.isAbortOnError)
    assertEquals(true, androidLibraryProject.lintOptions.isCheckAllWarnings)
    assertEquals(false, androidLibraryProject.lintOptions.textReport)
    assertEquals(true, androidLibraryProject.lintOptions.isAbsolutePaths)
    assertEquals(null, androidLibraryProject.lintOptions.lintConfig)
    assertEquals(false, androidLibraryProject.lintOptions.isCheckReleaseBuilds)
    assertEquals(true, androidLibraryProject.lintOptions.isCheckTestSources)
    assertEquals(false, androidLibraryProject.lintOptions.isCheckDependencies)
    assertEquals(androidLibraryProject.file("baseline.xml"), androidLibraryProject.lintOptions.baselineFile)
    assertEquals(null, androidLibraryProject.lintOptions.textOutput)
  }

  @Test fun configurationsWhenNotFailEarly() {
    val extension = defaultExtensions()
    extension.failEarly = false

    extension.lint.abortOnError = true
    extension.lint.warningsAsErrors = true

    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(true, androidAppProject.lintOptions.isAbortOnError)
    assertEquals(true, androidAppProject.lintOptions.isWarningsAsErrors)

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(true, androidLibraryProject.lintOptions.isAbortOnError)
    assertEquals(true, androidLibraryProject.lintOptions.isWarningsAsErrors)
  }

  @Test fun checkAllWarnings() {
    val extension = defaultExtensions()
    extension.failEarly = false

    extension.lint.checkAllWarnings = true

    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(true, androidAppProject.lintOptions.isCheckAllWarnings)

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(true, androidLibraryProject.lintOptions.isCheckAllWarnings)
  }

  @Test fun absolutePaths() {
    val extension = defaultExtensions()
    extension.lint.absolutePaths = false

    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(false, androidAppProject.lintOptions.isAbsolutePaths)

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(false, androidLibraryProject.lintOptions.isAbsolutePaths)
  }

  @Test fun checkReleaseBuilds() {
    val extension = defaultExtensions()
    extension.lint.checkReleaseBuilds = true

    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(true, androidAppProject.lintOptions.isCheckReleaseBuilds)

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(true, androidLibraryProject.lintOptions.isCheckReleaseBuilds)
  }

  @Test fun checkTestSources() {
    val extension = defaultExtensions()
    extension.lint.checkTestSources = false

    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(false, androidAppProject.lintOptions.isCheckTestSources)

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(false, androidLibraryProject.lintOptions.isCheckTestSources)
  }

  @Test fun checkDependencies() {
    val extension = defaultExtensions()
    extension.lint.checkDependencies = true

    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(true, androidAppProject.lintOptions.isCheckDependencies)

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(true, androidLibraryProject.lintOptions.isCheckDependencies)
  }

  @Test fun lintConfigFile() {
    val extension = defaultExtensions()

    extension.lint.lintConfig = androidAppProject.file("lint.xml")
    assertEquals(true, androidAppProject.addLint(extension))
    assertEquals(extension.lint.lintConfig, androidAppProject.lintOptions.lintConfig)

    extension.lint.lintConfig = androidLibraryProject.file("lint.xml")
    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(extension.lint.lintConfig, androidLibraryProject.lintOptions.lintConfig)
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertEquals(true, androidLibraryProject.addLint(extension))
    assertEquals(false, androidLibraryProject.lintOptions.isWarningsAsErrors)
    assertEquals(false, androidLibraryProject.lintOptions.isAbortOnError)
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.lint.enabled = false

    for (project in projects) {
      assertEquals(false, project.addLint(extension))
    }
  }
}
