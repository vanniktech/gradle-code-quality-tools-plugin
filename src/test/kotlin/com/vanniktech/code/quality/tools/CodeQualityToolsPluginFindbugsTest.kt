package com.vanniktech.code.quality.tools

import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.plugins.quality.FindBugsPlugin
import org.junit.Test

class CodeQualityToolsPluginFindbugsTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertThat(project.addFindbugs(rootProject, defaultExtensions())).isFalse()
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertThat(project.addFindbugs(rootProject, defaultExtensions())).isTrue()

      assertFindbugs(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      assertThat(project.addFindbugs(rootProject, defaultExtensions())).isTrue()

      assertFindbugs(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertThat(project.addFindbugs(rootProject, defaultExtensions())).isTrue()

      assertFindbugs(project)
    }
  }

  private fun assertFindbugs(project: Project) {
    assertThat(project.plugins.hasPlugin(FindBugsPlugin::class.java)).isTrue()

    assertThat(project.findbugs.isIgnoreFailures).isFalse()
    assertThat(project.findbugs.toolVersion).isEqualTo("3.0.1")
    assertThat(project.findbugs.effort).isEqualTo("max")
    assertThat(project.findbugs.reportLevel).isEqualTo("low")
    assertThat(project.findbugs.excludeFilter).isEqualTo(rootProject.file("code_quality_tools/findbugs-filter.xml"))

    project.findbugsTask.apply {
      assertThat(description).isEqualTo("Runs findbugs.")
      assertThat(group).isEqualTo("verification")

      assertThat(excludeFilter).isEqualTo(rootProject.file("code_quality_tools/findbugs-filter.xml"))

      assertThat(reports.xml.isEnabled).isTrue()
      assertThat(reports.html.isEnabled).isFalse()

      assertThat(taskDependsOn(this, "assemble")).isTrue()
    }

    assertThat(taskDependsOn(project.check, "findbugs")).isTrue()
  }

  @Test fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.findbugs.ignoreFailures = false

    assertThat(androidAppProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(androidAppProject.findbugs.isIgnoreFailures).isFalse()

    assertThat(androidLibraryProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.findbugs.isIgnoreFailures).isFalse()

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.findbugs.isIgnoreFailures).isFalse()
  }

  @Test fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.findbugs.ignoreFailures = true

    assertThat(androidAppProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(androidAppProject.findbugs.isIgnoreFailures).isTrue()

    assertThat(androidLibraryProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.findbugs.isIgnoreFailures).isTrue()

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.findbugs.isIgnoreFailures).isTrue()
  }

  @Test fun effort() {
    val extension = defaultExtensions()
    extension.findbugs.effort = "medium"

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.findbugs.effort).isEqualTo("medium")
  }

  @Test fun reportLevel() {
    val extension = defaultExtensions()
    extension.findbugs.reportLevel = "medium"

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.findbugs.reportLevel).isEqualTo("medium")
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.findbugs.isIgnoreFailures).isTrue()
  }

  @Test fun toolsVersion() {
    val extension = defaultExtensions()
    extension.findbugs.toolVersion = "3.0.0"

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.findbugs.toolVersion).isEqualTo(extension.findbugs.toolVersion)
  }

  @Test fun configFiles() {
    val extension = defaultExtensions()
    extension.findbugs.excludeFilter = "findbugs.xml"

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.findbugs.excludeFilter).isEqualTo(rootProject.file(extension.findbugs.excludeFilter))
  }

  @Test fun reports() {
    val extension = defaultExtensions()
    extension.htmlReports = true
    extension.xmlReports = false

    assertThat(javaProject.addFindbugs(rootProject, extension)).isTrue()

    javaProject.findbugsTask.apply {
      assertThat(reports.xml.isEnabled).isEqualTo(extension.xmlReports)
      assertThat(reports.html.isEnabled).isEqualTo(extension.htmlReports)
    }
  }

  @Test fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertThat(javaProject.addFindbugs(rootProject, extension)).isFalse()
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.findbugs.enabled = false

    for (project in projects) {
      assertThat(project.addFindbugs(rootProject, extension)).isFalse()
    }
  }
}
