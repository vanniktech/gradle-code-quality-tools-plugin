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
    assert ktlint.version == '0.8.3'
    assert taskDependsOn(project.check, 'ktlint')
    assert project.getTasksByName('ktlintFormat', false).size() == 1
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
