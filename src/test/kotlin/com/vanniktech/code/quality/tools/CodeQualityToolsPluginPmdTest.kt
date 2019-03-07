package com.vanniktech.code.quality.tools

import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.plugins.quality.PmdPlugin
import org.junit.Test

class CodeQualityToolsPluginPmdTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertThat(project.addPmd(rootProject, defaultExtensions())).isFalse()
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertThat(project.addPmd(rootProject, defaultExtensions())).isTrue()

      assertPmd(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      // Ideally we don't want to be running this in kotlin projects but since it uses the java library under the hood we can't do much.
      assertThat(project.addPmd(rootProject, defaultExtensions())).isTrue()

      assertPmd(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertThat(project.addPmd(rootProject, defaultExtensions())).isTrue()

      assertPmd(project)
    }
  }

  private fun assertPmd(project: Project) {
    assertThat(project.plugins.hasPlugin(PmdPlugin::class.java)).isTrue()

    assertThat(project.pmd.isIgnoreFailures).isFalse()
    assertThat(project.pmd.toolVersion).isEqualTo("6.0.0")
    assertThat(project.pmd.ruleSetFiles.singleFile).isEqualTo(rootProject.file("code_quality_tools/pmd.xml"))

    project.pmdTask.apply {
      assertThat(description).isEqualTo("Runs pmd.")
      assertThat(group).isEqualTo("verification")

      assertThat(ruleSetFiles.singleFile).isEqualTo(rootProject.file("code_quality_tools/pmd.xml"))
      assertThat(ruleSets).isEmpty()

      assertThat(includes.size).isEqualTo(1)
      assertThat(includes.contains("**/*.java")).isTrue()

      assertThat(excludes.size).isEqualTo(1)
      assertThat(excludes.contains("**/gen/**")).isTrue()

      assertThat(reports.xml.isEnabled).isTrue()
      assertThat(reports.html.isEnabled).isFalse()
    }

    assertThat(taskDependsOn(project.check, "pmd")).isTrue()
  }

  @Test fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.pmd.ignoreFailures = false

    assertThat(androidAppProject.addPmd(rootProject, extension)).isTrue()
    assertThat(androidAppProject.pmd.isIgnoreFailures).isFalse()

    assertThat(androidLibraryProject.addPmd(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.pmd.isIgnoreFailures).isFalse()

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()
    assertThat(javaProject.pmd.isIgnoreFailures).isFalse()
  }

  @Test fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.pmd.ignoreFailures = true

    assertThat(androidAppProject.addPmd(rootProject, extension)).isTrue()
    assertThat(androidAppProject.pmd.isIgnoreFailures).isTrue()

    assertThat(androidLibraryProject.addPmd(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.pmd.isIgnoreFailures).isTrue()

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()
    assertThat(javaProject.pmd.isIgnoreFailures).isTrue()
  }

  @Test fun include() {
    val extension = defaultExtensions()
    extension.pmd.include = listOf("*.java")

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()

    javaProject.pmdTask.apply {
      assertThat(includes.size).isEqualTo(1)
      assertThat(includes.contains("*.java")).isTrue()
    }
  }

  @Test fun exclude() {
    val extension = defaultExtensions()
    extension.pmd.exclude = listOf("**/gen")

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()

    javaProject.pmdTask.apply {
      assertThat(excludes.size).isEqualTo(1)
      assertThat(excludes.contains("**/gen")).isTrue()
    }
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()
    assertThat(javaProject.pmd.isIgnoreFailures).isTrue()
  }

  @Test fun toolsVersion() {
    val extension = defaultExtensions()
    extension.pmd.toolVersion = "5.4.0"

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()
    assertThat(javaProject.pmd.toolVersion).isEqualTo(extension.pmd.toolVersion)
  }

  @Test fun configFiles() {
    val extension = defaultExtensions()
    extension.pmd.ruleSetFile = "pmd.xml"

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()
    assertThat(javaProject.pmd.ruleSetFiles.singleFile).isEqualTo(rootProject.file(extension.pmd.ruleSetFile))
  }

  @Test fun reports() {
    val extension = defaultExtensions()
    extension.htmlReports = true
    extension.xmlReports = false

    assertThat(javaProject.addPmd(rootProject, extension)).isTrue()

    javaProject.pmdTask.apply {
      assertThat(reports.xml.isEnabled).isEqualTo(extension.xmlReports)
      assertThat(reports.html.isEnabled).isEqualTo(extension.htmlReports)
    }
  }

  @Test fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertThat(javaProject.addPmd(rootProject, extension)).isFalse()
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.pmd.enabled = false

    for (project in projects) {
      assertThat(project.addPmd(rootProject, extension)).isFalse()
    }
  }
}
