package com.vanniktech.code.quality.tools

import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.junit.Test

class CodeQualityToolsPluginCheckstyleTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertThat(project.addCheckstyle(rootProject, defaultExtensions())).isFalse()
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertThat(project.addCheckstyle(rootProject, defaultExtensions())).isTrue()

      assertCheckstyle(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      // Ideally we don"t want to be running this in kotlin projects but since it uses the java library under the hood we can"t do much.
      assertThat(project.addCheckstyle(rootProject, defaultExtensions())).isTrue()

      assertCheckstyle(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertThat(project.addCheckstyle(rootProject, defaultExtensions())).isTrue()

      assertCheckstyle(project)
    }
  }

  private fun assertCheckstyle(project: Project) {
    assertThat(project.plugins.hasPlugin(CheckstylePlugin::class.java)).isTrue()

    assertThat(project.checkstyle.isIgnoreFailures).isFalse()
    assertThat(project.checkstyle.isShowViolations).isTrue()
    assertThat(project.checkstyle.toolVersion).isEqualTo("8.6")
    assertThat(project.checkstyle.configFile).isEqualTo(rootProject.file("code_quality_tools/checkstyle.xml"))

    project.checkstyleTask.apply {
      assertThat(description).isEqualTo("Runs checkstyle.")
      assertThat(group).isEqualTo("verification")

      assertThat(configFile).isEqualTo(rootProject.file("code_quality_tools/checkstyle.xml"))

      assertThat(includes.size).isEqualTo(1)
      assertThat(includes.contains("**/*.java")).isTrue()

      assertThat(excludes.size).isEqualTo(1)
      assertThat(excludes.contains("**/gen/**")).isTrue()

      assertThat(reports.xml.isEnabled).isTrue()
      assertThat(reports.html.isEnabled).isFalse()
    }

    assertThat(taskDependsOn(project.check, "checkstyle")).isTrue()
  }

  @Test fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.checkstyle.ignoreFailures = false

    assertThat(androidAppProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidAppProject.checkstyle.isIgnoreFailures).isFalse()

    assertThat(androidLibraryProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.checkstyle.isIgnoreFailures).isFalse()

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(javaProject.checkstyle.isIgnoreFailures).isFalse()
  }

  @Test fun include() {
    val extension = defaultExtensions()
    extension.checkstyle.include = listOf("*.java")

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()

    javaProject.checkstyleTask.apply {
      assertThat(includes.size).isEqualTo(1)
      assertThat(includes.contains("*.java")).isTrue()
    }
  }

  @Test fun exclude() {
    val extension = defaultExtensions()
    extension.checkstyle.exclude = listOf("**/gen")

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()

    javaProject.checkstyleTask.apply {
      assertThat(excludes.size).isEqualTo(1)
      assertThat(excludes.contains("**/gen")).isTrue()
    }
  }

  @Test fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.checkstyle.ignoreFailures = true

    assertThat(androidAppProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidAppProject.checkstyle.isIgnoreFailures).isTrue()

    assertThat(androidLibraryProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.checkstyle.isIgnoreFailures).isTrue()

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(javaProject.checkstyle.isIgnoreFailures).isTrue()
  }

  @Test fun showViolationsFalse() {
    val extension = defaultExtensions()
    extension.checkstyle.showViolations = false

    assertThat(androidAppProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidAppProject.checkstyle.isShowViolations).isFalse()

    assertThat(androidLibraryProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.checkstyle.isShowViolations).isFalse()

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(javaProject.checkstyle.isShowViolations).isFalse()
  }

  @Test fun showViolationsTrue() {
    val extension = defaultExtensions()
    extension.checkstyle.showViolations = true

    assertThat(androidAppProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidAppProject.checkstyle.isShowViolations).isTrue()

    assertThat(androidLibraryProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.checkstyle.isShowViolations).isTrue()

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(javaProject.checkstyle.isShowViolations).isTrue()
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(javaProject.checkstyle.isIgnoreFailures).isTrue()
    assertThat(javaProject.checkstyle.isShowViolations).isFalse()
  }

  @Test fun toolsVersion() {
    val extension = defaultExtensions()
    extension.checkstyle.toolVersion = "6.14.0"

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(javaProject.checkstyle.toolVersion).isEqualTo(extension.checkstyle.toolVersion)
  }

  @Test fun configFiles() {
    val extension = defaultExtensions()
    extension.checkstyle.configFile = "checkstyle.xml"

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()
    assertThat(javaProject.checkstyle.configFile).isEqualTo(rootProject.file(extension.checkstyle.configFile))
  }

  @Test fun reports() {
    val extension = defaultExtensions()
    extension.htmlReports = true
    extension.xmlReports = false

    assertThat(javaProject.addCheckstyle(rootProject, extension)).isTrue()

    javaProject.checkstyleTask.apply {
      assertThat(reports.xml.isEnabled).isEqualTo(extension.xmlReports)
      assertThat(reports.html.isEnabled).isEqualTo(extension.htmlReports)
    }
  }

  @Test fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertThat(javaProject.addCheckstyle(rootProject, extension)).isFalse()
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.checkstyle.enabled = false

    for (project in projects) {
      assertThat(project.addCheckstyle(rootProject, extension)).isFalse()
    }
  }
}
