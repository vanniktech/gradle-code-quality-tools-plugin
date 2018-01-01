package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addLint

class CodeQualityToolsPluginLintTest extends CommonCodeQualityToolsTest {
  @Test void empty() {
    emptyProjects.each { project ->
      assert !addLint(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void java() {
    javaProjects.each { project ->
      assert !addLint(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
      assert !addLint(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert addLint(project, new CodeQualityToolsPluginExtensionForTests())

      assertLint(project)
    }
  }

  private static void assertLint(Project project) {
    assert project.android.lintOptions.warningsAsErrors
    assert project.android.lintOptions.abortOnError
    assert !project.android.lintOptions.textReport
    assert project.android.lintOptions.textOutput == null
    assert !project.android.lintOptions.checkAllWarnings
    assert project.android.lintOptions.baselineFile == null

    assert taskDependsOn(project.check, 'lint')
  }

  @Test void configurations() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.lint.textReport = true
    extension.lint.textOutput = 'stdout'

    extension.lint.abortOnError = false
    extension.lint.warningsAsErrors = false
    extension.lint.checkAllWarnings = true

    extension.lint.baselineFileName = "baseline.xml"

    assert addLint(androidAppProject, extension)
    assert androidAppProject.android.lintOptions.warningsAsErrors == extension.lint.warningsAsErrors
    assert androidAppProject.android.lintOptions.abortOnError == extension.lint.abortOnError
    assert androidAppProject.android.lintOptions.checkAllWarnings == extension.lint.checkAllWarnings
    assert androidAppProject.android.lintOptions.textReport == extension.lint.textReport
    assert androidAppProject.android.lintOptions.absolutePaths
    assert androidAppProject.android.lintOptions.lintConfig == null
    assert androidAppProject.android.lintOptions.baselineFile == androidAppProject.file("baseline.xml")
    assert androidAppProject.android.lintOptions.textOutput.toString() == extension.lint.textOutput

    assert addLint(androidLibraryProject, extension)
    assert androidLibraryProject.android.lintOptions.warningsAsErrors == extension.lint.warningsAsErrors
    assert androidLibraryProject.android.lintOptions.abortOnError == extension.lint.abortOnError
    assert androidLibraryProject.android.lintOptions.checkAllWarnings == extension.lint.checkAllWarnings
    assert androidLibraryProject.android.lintOptions.textReport == extension.lint.textReport
    assert androidLibraryProject.android.lintOptions.absolutePaths
    assert androidLibraryProject.android.lintOptions.lintConfig == null
    assert androidLibraryProject.android.lintOptions.baselineFile == androidLibraryProject.file("baseline.xml")
    assert androidLibraryProject.android.lintOptions.textOutput.toString() == extension.lint.textOutput
  }

  @Test void configurationsWhenNotFailEarly() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.failEarly = false

    extension.lint.abortOnError = true
    extension.lint.warningsAsErrors = true

    assert addLint(androidAppProject, extension)
    assert androidAppProject.android.lintOptions.abortOnError == extension.lint.abortOnError
    assert androidAppProject.android.lintOptions.warningsAsErrors == extension.lint.warningsAsErrors

    assert addLint(androidLibraryProject, extension)
    assert androidLibraryProject.android.lintOptions.abortOnError == extension.lint.abortOnError
    assert androidLibraryProject.android.lintOptions.warningsAsErrors == extension.lint.warningsAsErrors
  }

  @Test void checkAllWarnings() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.failEarly = false

    extension.lint.checkAllWarnings = true

    assert addLint(androidAppProject, extension)
    assert androidAppProject.android.lintOptions.checkAllWarnings == extension.lint.checkAllWarnings

    assert addLint(androidLibraryProject, extension)
    assert androidLibraryProject.android.lintOptions.checkAllWarnings == extension.lint.checkAllWarnings
  }

  @Test void absolutePaths() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.lint.absolutePaths = false

    assert addLint(androidAppProject, extension)
    assert androidAppProject.android.lintOptions.absolutePaths == extension.lint.absolutePaths

    assert addLint(androidLibraryProject, extension)
    assert androidLibraryProject.android.lintOptions.absolutePaths == extension.lint.absolutePaths
  }

  @Test void lintConfigFile() {
    def extension = new CodeQualityToolsPluginExtensionForTests()

    extension.lint.lintConfig = androidAppProject.file("lint.xml")
    assert addLint(androidAppProject, extension)
    assert androidAppProject.android.lintOptions.lintConfig == extension.lint.lintConfig

    extension.lint.lintConfig = androidLibraryProject.file("lint.xml")
    assert addLint(androidLibraryProject, extension)
    assert androidLibraryProject.android.lintOptions.lintConfig == extension.lint.lintConfig
  }

  @Test void failEarlyFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.failEarly = false

    assert addLint(androidLibraryProject, extension)
    assert !androidLibraryProject.android.lintOptions.warningsAsErrors
    assert !androidLibraryProject.android.lintOptions.abortOnError
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.lint.enabled = false

    for (def project : projects) {
      assert !addLint(project, extension)
    }
  }
}
