package com.vanniktech.code.quality.tools

import de.aaschmid.gradle.plugins.cpd.Cpd
import de.aaschmid.gradle.plugins.cpd.CpdPlugin
import org.gradle.api.Project
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addCpd

class CodeQualityToolsPluginCpdTest extends CommonCodeQualityToolsTest {
  @Test void empty() {
    emptyProjects.each { project ->
      assert !addCpd(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void java() {
    javaProjects.each { project ->
      assert addCpd(project, new CodeQualityToolsPluginExtensionForTests())

      assertCpd(project)
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
      // Ideally we don't want to be running this in kotlin projects but since it uses the java library under the hood we can't do much.
      assert addCpd(project, new CodeQualityToolsPluginExtensionForTests())

      assertCpd(project)
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert addCpd(project, new CodeQualityToolsPluginExtensionForTests())

      assertCpd(project)
    }
  }

  private void assertCpd(Project project) {
    assert project.plugins.hasPlugin(CpdPlugin)

    assert project.cpd.toolVersion == '6.0.0'
    assert project.cpd.language == 'java'

    def task = project.tasks.findByName('cpdCheck')

    assert task instanceof Cpd

    task.with {
      assert description == 'Runs cpd.'
      assert group == 'verification'
      assert encoding == 'UTF-8'

      assert minimumTokenCount == 50
      assert !ignoreFailures

      assert reports.xml.enabled
      assert !reports.text.enabled
    }

    assert taskDependsOn(project.check, 'cpdCheck')
  }

  @Test void ignoreFailuresFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.cpd.ignoreFailures = false

    assert addCpd(androidAppProject, extension)
    assert androidAppProject.tasks.findByName('cpdCheck').ignoreFailures == extension.cpd.ignoreFailures

    assert addCpd(androidLibraryProject, extension)
    assert androidLibraryProject.tasks.findByName('cpdCheck').ignoreFailures == extension.cpd.ignoreFailures

    assert addCpd(javaProject, extension)
    assert javaProject.tasks.findByName('cpdCheck').ignoreFailures == extension.cpd.ignoreFailures
  }

  @Test void ignoreFailuresTrue() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.cpd.ignoreFailures = true

    assert addCpd(androidAppProject, extension)
    assert androidAppProject.tasks.findByName('cpdCheck').ignoreFailures == extension.cpd.ignoreFailures

    assert addCpd(androidLibraryProject, extension)
    assert androidLibraryProject.tasks.findByName('cpdCheck').ignoreFailures == extension.cpd.ignoreFailures

    assert addCpd(javaProject, extension)
    assert javaProject.tasks.findByName('cpdCheck').ignoreFailures == extension.cpd.ignoreFailures
  }

  @Test void failEarlyFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.failEarly = false

    assert addCpd(javaProject, extension)
    assert javaProject.tasks.findByName('cpdCheck').ignoreFailures
  }

  @Test void toolsVersion() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.pmd.toolVersion = '5.4.0' // We take the PMD version for CPD since they're using the same tool.

    assert addCpd(javaProject, extension)
    assert javaProject.cpd.toolVersion == "5.4.0"
  }

  @Test void language() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.cpd.language = 'swift'

    assert addCpd(javaProject, extension)
    assert javaProject.cpd.language == "swift"
  }

  @Test void minimumTokenCount() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.cpd.minimumTokenCount = 50

    assert addCpd(javaProject, extension)
    javaProject.tasks.findByName('cpdCheck').with {
      assert minimumTokenCount == 50
    }
  }

  @Test void reports() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.textReports = true
    extension.xmlReports = false

    assert addCpd(javaProject, extension)

    javaProject.tasks.findByName('cpdCheck').with {
      assert reports.xml.enabled == extension.xmlReports
      assert reports.text.enabled == extension.textReports
    }
  }

  @Test void ignoreProject() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.ignoreProjects = [javaProject.name]
    assert !addCpd(javaProject, extension)
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.cpd.enabled = false

    for (def project : projects) {
      assert !addCpd(project, extension)
    }
  }
}
