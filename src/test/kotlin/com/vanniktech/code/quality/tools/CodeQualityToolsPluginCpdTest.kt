package com.vanniktech.code.quality.tools

import de.aaschmid.gradle.plugins.cpd.CpdPlugin
import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.api.Project
import org.junit.Test

class CodeQualityToolsPluginCpdTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertThat(project.addCpd(defaultExtensions())).isFalse()
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertThat(project.addCpd(defaultExtensions())).isTrue()

      assertCpd(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      // Ideally we don"t want to be running this in kotlin projects but since it uses the java library under the hood we can"t do much.
      assertThat(project.addCpd(defaultExtensions())).isTrue()

      assertCpd(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertThat(project.addCpd(defaultExtensions())).isTrue()

      assertCpd(project)
    }
  }

  private fun assertCpd(project: Project) {
    assertThat(project.plugins.hasPlugin(CpdPlugin::class.java)).isTrue()

    assertThat(project.cpd.toolVersion).isEqualTo("6.0.0")
    assertThat(project.cpd.language).isEqualTo("java")

    project.cpdTask.apply {
      assertThat(description).isEqualTo("Runs cpd.")
      assertThat(group).isEqualTo("verification")
      assertThat(encoding).isEqualTo("UTF-8")

      assertThat(minimumTokenCount).isEqualTo(50)
      assertThat(ignoreFailures).isFalse()

      assertThat(reports.xml.isEnabled).isTrue()
      assertThat(reports.text.isEnabled).isFalse()
    }

    assertThat(taskDependsOn(project.check, "cpdCheck")).isTrue()
  }

  @Test fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.cpd.ignoreFailures = false

    assertThat(androidAppProject.addCpd(extension)).isTrue()
    assertThat(androidAppProject.cpd.isIgnoreFailures).isFalse()

    assertThat(androidLibraryProject.addCpd(extension)).isTrue()
    assertThat(androidLibraryProject.cpd.isIgnoreFailures).isFalse()

    assertThat(javaProject.addCpd(extension)).isTrue()
    assertThat(javaProject.cpd.isIgnoreFailures).isFalse()
  }

  @Test fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.cpd.ignoreFailures = true

    assertThat(androidAppProject.addCpd(extension)).isTrue()
    assertThat(androidAppProject.cpd.isIgnoreFailures).isTrue()

    assertThat(androidLibraryProject.addCpd(extension)).isTrue()
    assertThat(androidLibraryProject.cpd.isIgnoreFailures).isTrue()

    assertThat(javaProject.addCpd(extension)).isTrue()
    assertThat(javaProject.cpd.isIgnoreFailures).isTrue()
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertThat(javaProject.addCpd(extension)).isTrue()
    assertThat(javaProject.cpd.isIgnoreFailures).isTrue()
  }

  @Test fun toolsVersion() {
    val extension = defaultExtensions()
    extension.pmd.toolVersion = "5.4.0" // We take the PMD version for CPD since they"re using the same tool.

    assertThat(javaProject.addCpd(extension)).isTrue()
    assertThat(javaProject.cpd.toolVersion).isEqualTo("5.4.0")
  }

  @Test fun language() {
    val extension = defaultExtensions()
    extension.cpd.language = "swift"

    assertThat(javaProject.addCpd(extension)).isTrue()
    assertThat(javaProject.cpd.language).isEqualTo("swift")
  }

  @Test fun minimumTokenCount() {
    val extension = defaultExtensions()
    extension.cpd.minimumTokenCount = 50

    assertThat(javaProject.addCpd(extension)).isTrue()
    javaProject.cpd.apply {
      assertThat(minimumTokenCount).isEqualTo(50)
    }
  }

  @Test fun reports() {
    val extension = defaultExtensions()
    extension.textReports = true
    extension.xmlReports = false

    assertThat(javaProject.addCpd(extension)).isTrue()

    javaProject.cpdTask.apply {
      assertThat(reports.xml.isEnabled).isEqualTo(extension.xmlReports)
      assertThat(reports.text.isEnabled).isEqualTo(extension.textReports)
    }
  }

  @Test fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertThat(javaProject.addCpd(extension)).isFalse()
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.cpd.enabled = false

    for (project in projects) {
      assertThat(project.addCpd(extension)).isFalse()
    }
  }
}
