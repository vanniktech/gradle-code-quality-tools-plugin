package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdPlugin
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addPmd

class CodeQualityToolsPluginPmdTest extends CommonCodeQualityToolsTest {
  @Test void java() {
    javaProjects.each { project ->
      assert addPmd(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertPmd(project)
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
    // Ideally we don't want to be running this in kotlin projects but since it uses the java library under the hood we can't do much.
      assert addPmd(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertPmd(project)
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert addPmd(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertPmd(project)
    }
  }

  private void assertPmd(Project project) {
    assert project.plugins.hasPlugin(PmdPlugin)

    assert !project.pmd.ignoreFailures
    assert project.pmd.toolVersion == '5.8.1'
    assert project.pmd.ruleSetFiles.singleFile == rootProject.file("code_quality_tools/pmd.xml")

    def task = project.tasks.findByName('pmd')

    assert task instanceof Pmd

    task.with {
      assert description == 'Runs pmd.'
      assert group == 'verification'

      assert ruleSetFiles.singleFile == rootProject.file("code_quality_tools/pmd.xml")
      assert ruleSets.empty

      assert includes.size() == 1
      assert includes.contains('**/*.java')

      assert excludes.size() == 1
      assert excludes.contains('**/gen/**')

      assert reports.xml.enabled
      assert !reports.html.enabled
    }

    assert taskDependsOn(project.check, 'pmd')
  }

  @Test void ignoreFailuresFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.ignoreFailures = false

    assert addPmd(androidAppProject, rootProject, extension)
    assert androidAppProject.pmd.ignoreFailures == extension.pmd.ignoreFailures

    assert addPmd(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.pmd.ignoreFailures == extension.pmd.ignoreFailures

    assert addPmd(javaProject, rootProject, extension)
    assert javaProject.pmd.ignoreFailures == extension.pmd.ignoreFailures
  }

  @Test void ignoreFailuresTrue() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.ignoreFailures = true

    assert addPmd(androidAppProject, rootProject, extension)
    assert androidAppProject.pmd.ignoreFailures == extension.pmd.ignoreFailures

    assert addPmd(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.pmd.ignoreFailures == extension.pmd.ignoreFailures

    assert addPmd(javaProject, rootProject, extension)
    assert javaProject.pmd.ignoreFailures == extension.pmd.ignoreFailures
  }

  @Test void include() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.include = ['*.java']

    for (def project : projects) {
      assert addPmd(project, rootProject, extension)
      def task = project.tasks.findByName('pmd')

      task.with {
        assert includes.size() == 1
        assert includes.contains('*.java')
      }
    }
  }

  @Test void exclude() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.exclude = ['**/gen']

    for (def project : projects) {
      assert addPmd(project, rootProject, extension)
      def task = project.tasks.findByName('pmd')

      task.with {
        assert excludes.size() == 1
        assert excludes.contains('**/gen')
      }
    }
  }

  @Test void failEarlyFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.failEarly = false

    assert addPmd(javaProject, rootProject, extension)
    assert javaProject.pmd.ignoreFailures
  }

  @Test void toolsVersion() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.toolVersion = '5.4.0'

    assert addPmd(javaProject, rootProject, extension)
    assert javaProject.pmd.toolVersion == extension.pmd.toolVersion
  }

  @Test void configFiles() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.ruleSetFile = 'pmd.xml'

    assert addPmd(javaProject, rootProject, extension)
    assert javaProject.pmd.ruleSetFiles.singleFile == rootProject.file(extension.pmd.ruleSetFile)
  }

  @Test void reports() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.htmlReports = true
    extension.xmlReports = false

    assert addPmd(javaProject, rootProject, extension)

    javaProject.tasks.findByName('pmd').with {
      assert reports.xml.enabled == extension.xmlReports
      assert reports.html.enabled == extension.htmlReports
    }
  }

  @Test void ignoreProject() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.ignoreProjects = [javaProject.name]
    assert !addPmd(javaProject, rootProject, extension)
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.enabled = false

    for (def project : projects) {
      assert !addPmd(project, rootProject, extension)
    }
  }
}
