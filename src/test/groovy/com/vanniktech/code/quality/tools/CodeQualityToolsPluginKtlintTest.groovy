package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.junit.Ignore
import org.junit.Test

public class CodeQualityToolsPluginKtlintTest extends CommonCodeQualityToolsTest {
  @Test public void javaDefault() {
    assert CodeQualityToolsPlugin.addKtlint(javaProject, new CodeQualityToolsPluginExceptionForTests())

    assertKtlint(javaProject)
  }

  @Test @Ignore("buildToolsVersion is not specified.") public void androidAppDefault() {
    assert CodeQualityToolsPlugin.addKtlint(androidAppProject, new CodeQualityToolsPluginExceptionForTests())

    assertKtlint(androidAppProject)
  }

  @Test @Ignore("buildToolsVersion is not specified.") public void androidLibraryDefault() {
    assert CodeQualityToolsPlugin.addKtlint(androidLibraryProject, new CodeQualityToolsPluginExceptionForTests())

    assertKtlint(androidLibraryProject)
  }

  private static void assertKtlint(Project project) {
    def ktlint = project.configurations.getByName('ktlint').dependencies[0]
    assert ktlint.group == 'com.github.shyiko'
    assert ktlint.name == 'ktlint'
    assert ktlint.version == '0.13.0'
    assert taskDependsOn(project.check, 'ktlint')

    assert project.getTasksByName('ktlint', false).size() == 1
    def ktlintTask = project.getTasksByName('ktlint', false)[0]
    assert ktlintTask.group == 'verification'
    assert ktlintTask.description == 'Check Kotlin code style.'
    assert ktlintTask.main == 'com.github.shyiko.ktlint.Main'
    assert ktlintTask.args.size() == 3
    assert ktlintTask.args[0] == '--reporter=plain'
    assert ktlintTask.args[1] == "--reporter=checkstyle,output=${project.buildDir}/reports/ktlint/ktlint-checkstyle-report.xml"
    assert ktlintTask.args[2] == 'src/**/*.kt'

    assert project.getTasksByName('ktlintFormat', false).size() == 1
    def ktlintFormatTask = project.getTasksByName('ktlintFormat', false)[0]
    assert ktlintFormatTask.group == 'formatting'
    assert ktlintFormatTask.description == 'Fix Kotlin code style deviations.'
    assert ktlintFormatTask.main == 'com.github.shyiko.ktlint.Main'
    assert ktlintFormatTask.args.size() == 2
    assert ktlintFormatTask.args[0] == '-F'
    assert ktlintFormatTask.args[1] == 'src/**/*.kt'
  }

  @Test public void configurations() {
    def extension = new CodeQualityToolsPluginExceptionForTests()
    extension.ktlint.toolVersion = '0.8.2'

    assert CodeQualityToolsPlugin.addKtlint(androidLibraryProject, extension)

    def ktlint = androidLibraryProject.configurations.getByName('ktlint').dependencies[0]
    assert ktlint.group == 'com.github.shyiko'
    assert ktlint.name == 'ktlint'
    assert ktlint.version == '0.8.2'
  }

  @Test public void enabled() {
    def extension = new CodeQualityToolsPluginExceptionForTests()
    extension.ktlint.enabled = false

    for (def project : projects) {
      assert !CodeQualityToolsPlugin.addKtlint(project, extension)
    }
  }
}
