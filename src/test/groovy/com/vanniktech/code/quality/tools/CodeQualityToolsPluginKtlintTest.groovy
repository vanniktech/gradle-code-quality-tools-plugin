package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import org.junit.Ignore

public class CodeQualityToolsPluginKtlintTest {
  private Project rootProject
  private Project javaProject
  private Project androidAppProject
  private Project androidLibraryProject

  @Before public void setUp() {
    rootProject = ProjectBuilder.builder().withName('root').build()

    javaProject = ProjectBuilder.builder().withName('java').withParent(rootProject).build()
    javaProject.plugins.apply('java')

    androidAppProject = ProjectBuilder.builder().withName('android app').build()
    androidAppProject.plugins.apply('com.android.application')

    androidLibraryProject = ProjectBuilder.builder().withName('android library').build()
    androidLibraryProject.plugins.apply('com.android.library')
  }

  @Test public void testAddKtlintJavaDefault() {
    assert CodeQualityToolsPlugin.addKtlint(javaProject, new CodeQualityToolsPluginExceptionForTests())

    assertKtlint(javaProject)
  }

  @Test @Ignore("buildToolsVersion is not specified.") public void testAddKtlintAndroidAppDefault() {
    assert CodeQualityToolsPlugin.addKtlint(androidAppProject, new CodeQualityToolsPluginExceptionForTests())

    assertKtlint(androidAppProject)
  }

  @Test @Ignore("buildToolsVersion is not specified.") public void testAddKtlintAndroidLibraryDefault() {
    assert CodeQualityToolsPlugin.addKtlint(androidLibraryProject, new CodeQualityToolsPluginExceptionForTests())

    assertKtlint(androidLibraryProject)
  }

  private static void assertKtlint(Project project) {
    def ktlint = project.configurations.getByName('ktlint').dependencies[0]
    assert ktlint.group == 'com.github.shyiko'
    assert ktlint.name == 'ktlint'
    assert ktlint.version == '0.8.3'
    assert CodeQualityToolsPluginTest.taskDependsOn(project.check, 'ktlint')
    assert project.getTasksByName('ktlintFormat', false).size() == 1
  }

  @Test public void testKtlintConfigurations() {
    def extension = new CodeQualityToolsPluginExceptionForTests()
    extension.ktlint.toolVersion = '0.8.2'

    assert CodeQualityToolsPlugin.addKtlint(androidLibraryProject, extension)

    def ktlint = androidLibraryProject.configurations.getByName('ktlint').dependencies[0]
    assert ktlint.group == 'com.github.shyiko'
    assert ktlint.name == 'ktlint'
    assert ktlint.version == '0.8.2'
  }

  @Test public void testKtlintEnabled() {
    def extension = new CodeQualityToolsPluginExceptionForTests()
    extension.ktlint.enabled = false

    for (def project : projects) {
      assert !CodeQualityToolsPlugin.addKtlint(project, extension)
    }
  }
}
