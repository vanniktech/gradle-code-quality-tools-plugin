package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.FindBugsPlugin
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addFindbugs

class CodeQualityToolsPluginFindbugsTest extends CommonCodeQualityToolsTest {
  @Test void empty() {
    emptyProjects.each { project ->
      assert !addFindbugs(project, rootProject, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void java() {
    javaProjects.each { project ->
      assert addFindbugs(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertFindbugs(project)
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
      assert addFindbugs(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertFindbugs(project)
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert addFindbugs(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertFindbugs(project)
    }
  }

  private void assertFindbugs(Project project) {
    assert project.plugins.hasPlugin(FindBugsPlugin)

    assert !project.findbugs.ignoreFailures
    assert project.findbugs.toolVersion == '3.0.1'
    assert project.findbugs.effort == 'max'
    assert project.findbugs.reportLevel == 'low'
    assert project.findbugs.excludeFilter == rootProject.file('code_quality_tools/findbugs-filter.xml')

    def task = project.tasks.findByName('findbugs')

    assert task instanceof FindBugs

    task.with {
      assert description == 'Runs findbugs.'
      assert group == 'verification'

      assert excludeFilter == rootProject.file('code_quality_tools/findbugs-filter.xml')

      assert reports.xml.enabled
      assert !reports.html.enabled

      assert taskDependsOn(task, 'assemble')
    }

    assert taskDependsOn(project.check, 'findbugs')
  }

  @Test void ignoreFailuresFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.findbugs.ignoreFailures = false

    assert addFindbugs(androidAppProject, rootProject, extension)
    assert androidAppProject.findbugs.ignoreFailures == extension.findbugs.ignoreFailures

    assert addFindbugs(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.findbugs.ignoreFailures == extension.findbugs.ignoreFailures

    assert addFindbugs(javaProject, rootProject, extension)
    assert javaProject.findbugs.ignoreFailures == extension.findbugs.ignoreFailures
  }

  @Test void ignoreFailuresTrue() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.findbugs.ignoreFailures = true

    assert addFindbugs(androidAppProject, rootProject, extension)
    assert androidAppProject.findbugs.ignoreFailures == extension.findbugs.ignoreFailures

    assert addFindbugs(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.findbugs.ignoreFailures == extension.findbugs.ignoreFailures

    assert addFindbugs(javaProject, rootProject, extension)
    assert javaProject.findbugs.ignoreFailures == extension.findbugs.ignoreFailures
  }

  @Test void effort() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.findbugs.effort = 'medium'

    assert addFindbugs(javaProject, rootProject, extension)
    assert javaProject.findbugs.effort == 'medium'
  }

  @Test void reportLevel() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.findbugs.reportLevel = 'medium'

    assert addFindbugs(javaProject, rootProject, extension)
    assert javaProject.findbugs.reportLevel == 'medium'
  }

  @Test void failEarlyFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.failEarly = false

    assert addFindbugs(javaProject, rootProject, extension)
    assert javaProject.findbugs.ignoreFailures
  }

  @Test void toolsVersion() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.findbugs.toolVersion = '3.0.0'

    assert addFindbugs(javaProject, rootProject, extension)
    assert javaProject.findbugs.toolVersion == extension.findbugs.toolVersion
  }

  @Test void configFiles() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.findbugs.excludeFilter = 'findbugs.xml'

    assert addFindbugs(javaProject, rootProject, extension)
    assert javaProject.findbugs.excludeFilter == rootProject.file(extension.findbugs.excludeFilter)
  }

  @Test void reports() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.htmlReports = true
    extension.xmlReports = false

    assert addFindbugs(javaProject, rootProject, extension)

    javaProject.tasks.findByName('findbugs').with {
      assert reports.xml.enabled == extension.xmlReports
      assert reports.html.enabled == extension.htmlReports
    }
  }

  @Test void ignoreProject() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.ignoreProjects = [javaProject.name]
    assert !addFindbugs(javaProject, rootProject, extension)
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.findbugs.enabled = false

    for (def project : projects) {
      assert !addFindbugs(project, rootProject, extension)
    }
  }
}
