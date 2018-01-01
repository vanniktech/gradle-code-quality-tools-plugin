package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addCheckstyle

class CodeQualityToolsPluginCheckstyleTest extends CommonCodeQualityToolsTest {
  @Test void empty() {
    emptyProjects.each { project ->
      assert !addCheckstyle(project, rootProject, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void java() {
    javaProjects.each { project ->
      assert addCheckstyle(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertCheckstyle(project)
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
      // Ideally we don't want to be running this in kotlin projects but since it uses the java library under the hood we can't do much.
      assert addCheckstyle(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertCheckstyle(project)
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert addCheckstyle(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertCheckstyle(project)
    }
  }

  private void assertCheckstyle(Project project) {
    assert project.plugins.hasPlugin(CheckstylePlugin)

    assert !project.checkstyle.ignoreFailures
    assert project.checkstyle.showViolations
    assert project.checkstyle.toolVersion == '8.6'
    assert project.checkstyle.configFile == rootProject.file("code_quality_tools/checkstyle.xml")

    def task = project.tasks.findByName('checkstyle')

    assert task instanceof Checkstyle

    task.with {
      assert description == 'Runs checkstyle.'
      assert group == 'verification'

      assert configFile == rootProject.file("code_quality_tools/checkstyle.xml")

      assert includes.size() == 1
      assert includes.contains('**/*.java')

      assert excludes.size() == 1
      assert excludes.contains('**/gen/**')

      assert reports.xml.enabled
      assert !reports.html.enabled
    }

    assert taskDependsOn(project.check, 'checkstyle')
  }

  @Test void ignoreFailuresFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.ignoreFailures = false

    assert addCheckstyle(androidAppProject, rootProject, extension)
    assert androidAppProject.checkstyle.ignoreFailures == extension.checkstyle.ignoreFailures

    assert addCheckstyle(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.checkstyle.ignoreFailures == extension.checkstyle.ignoreFailures

    assert addCheckstyle(javaProject, rootProject, extension)
    assert javaProject.checkstyle.ignoreFailures == extension.checkstyle.ignoreFailures
  }

  @Test void include() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.include = ['*.java']

    assert addCheckstyle(javaProject, rootProject, extension)
    def task = javaProject.tasks.findByName('checkstyle')

    task.with {
      assert includes.size() == 1
      assert includes.contains('*.java')
    }
  }

  @Test void exclude() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.exclude = ['**/gen']

    assert addCheckstyle(javaProject, rootProject, extension)
    def task = javaProject.tasks.findByName('checkstyle')

    task.with {
      assert excludes.size() == 1
      assert excludes.contains('**/gen')
    }
  }

  @Test void ignoreFailuresTrue() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.ignoreFailures = true

    assert addCheckstyle(androidAppProject, rootProject, extension)
    assert androidAppProject.checkstyle.ignoreFailures == extension.checkstyle.ignoreFailures

    assert addCheckstyle(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.checkstyle.ignoreFailures == extension.checkstyle.ignoreFailures

    assert addCheckstyle(javaProject, rootProject, extension)
    assert javaProject.checkstyle.ignoreFailures == extension.checkstyle.ignoreFailures
  }

  @Test void showViolationsFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.showViolations = false

    assert addCheckstyle(androidAppProject, rootProject, extension)
    assert androidAppProject.checkstyle.showViolations == extension.checkstyle.showViolations

    assert addCheckstyle(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.checkstyle.showViolations == extension.checkstyle.showViolations

    assert addCheckstyle(javaProject, rootProject, extension)
    assert javaProject.checkstyle.showViolations == extension.checkstyle.showViolations
  }

  @Test void showViolationsTrue() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.showViolations = true

    assert addCheckstyle(androidAppProject, rootProject, extension)
    assert androidAppProject.checkstyle.showViolations == extension.checkstyle.showViolations

    assert addCheckstyle(androidLibraryProject, rootProject, extension)
    assert androidLibraryProject.checkstyle.showViolations == extension.checkstyle.showViolations

    assert addCheckstyle(javaProject, rootProject, extension)
    assert javaProject.checkstyle.showViolations == extension.checkstyle.showViolations
  }

  @Test void failEarlyFalse() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.failEarly = false

    assert addCheckstyle(javaProject, rootProject, extension)
    assert javaProject.checkstyle.ignoreFailures
    assert !javaProject.checkstyle.showViolations
  }

  @Test void toolsVersion() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.toolVersion = '6.14.0'

    assert addCheckstyle(javaProject, rootProject, extension)
    assert javaProject.checkstyle.toolVersion == extension.checkstyle.toolVersion
  }

  @Test void configFiles() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.configFile = 'checkstyle.xml'

    assert addCheckstyle(javaProject, rootProject, extension)
    assert javaProject.checkstyle.configFile == rootProject.file(extension.checkstyle.configFile)
  }

  @Test void reports() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.htmlReports = true
    extension.xmlReports = false

    assert addCheckstyle(javaProject, rootProject, extension)

    javaProject.tasks.findByName('checkstyle').with {
      assert reports.xml.enabled == extension.xmlReports
      assert reports.html.enabled == extension.htmlReports
    }
  }

  @Test void ignoreProject() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.ignoreProjects = [javaProject.name]
    assert !addCheckstyle(javaProject, rootProject, extension)
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.checkstyle.enabled = false

    for (def project : projects) {
      assert !addCheckstyle(project, rootProject, extension)
    }
  }
}
