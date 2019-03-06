package com.vanniktech.code.quality.tools

import com.github.spotbugs.SpotBugsPlugin
import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.api.Project
import org.junit.Test

class CodeQualityToolsPluginSpotbugsTest : CommonCodeQualityToolsTest() {
  @Test
  fun empty() {
    emptyProjects.forEach { project ->
      assertThat(project.addSpotbugs(rootProject, defaultExtensions())).isFalse()
    }
  }

  @Test
  fun java() {
    javaProjects.forEach { project ->
      assertThat(project.addSpotbugs(rootProject, defaultExtensions())).isTrue()

      assertSpotbugs(project)
    }
  }

  @Test
  fun kotlin() {
    kotlinProjects.forEach { project ->
      assertThat(project.addSpotbugs(rootProject, defaultExtensions())).isTrue()

      assertSpotbugs(project)
    }
  }

  @Test
  fun android() {
    androidProjects.forEach { project ->
      assertThat(project.addSpotbugs(rootProject, defaultExtensions())).isTrue()

      assertSpotbugs(project)
    }
  }

  private fun assertSpotbugs(project: Project) {
    assertThat(project.plugins.hasPlugin(SpotBugsPlugin::class.java)).isTrue()

    assertThat(project.spotbugs.isIgnoreFailures).isFalse()
    assertThat(project.spotbugs.toolVersion).isEqualTo("3.1.12")
    assertThat(project.spotbugs.effort).isEqualTo("max")
    assertThat(project.spotbugs.reportLevel).isEqualTo("low")
    assertThat(project.spotbugs.excludeFilter).isEqualTo(rootProject.file("code_quality_tools/findbugs-filter.xml"))

    project.spotbugsTask.apply {
      assertThat(description).isEqualTo("Runs spotbugs.")
      assertThat(group).isEqualTo("verification")

      assertThat(excludeFilter).isEqualTo(rootProject.file("code_quality_tools/findbugs-filter.xml"))

      assertThat(reports.xml.isEnabled).isTrue()
      assertThat(reports.html.isEnabled).isFalse()

      assertThat(taskDependsOn(this, "assemble")).isTrue()
    }

    assertThat(taskDependsOn(project.check, "spotbugs")).isTrue()
  }

  @Test
  fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.spotbugs.ignoreFailures = false

    assertThat(androidAppProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(androidAppProject.spotbugs.isIgnoreFailures).isFalse()

    assertThat(androidLibraryProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.spotbugs.isIgnoreFailures).isFalse()

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.spotbugs.isIgnoreFailures).isFalse()
  }

  @Test
  fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.spotbugs.ignoreFailures = true

    assertThat(androidAppProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(androidAppProject.spotbugs.isIgnoreFailures).isTrue()

    assertThat(androidLibraryProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(androidLibraryProject.spotbugs.isIgnoreFailures).isTrue()

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.spotbugs.isIgnoreFailures).isTrue()
  }

  @Test
  fun effort() {
    val extension = defaultExtensions()
    extension.spotbugs.effort = "medium"

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.spotbugs.effort).isEqualTo("medium")
  }

  @Test
  fun reportLevel() {
    val extension = defaultExtensions()
    extension.spotbugs.reportLevel = "medium"

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.spotbugs.reportLevel).isEqualTo("medium")
  }

  @Test
  fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.spotbugs.isIgnoreFailures).isTrue()
  }

  @Test
  fun toolsVersion() {
    val extension = defaultExtensions()
    extension.spotbugs.toolVersion = "3.0.0"

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.spotbugs.toolVersion).isEqualTo(extension.spotbugs.toolVersion)
  }

  @Test
  fun configFiles() {
    val extension = defaultExtensions()
    extension.spotbugs.excludeFilter = "spotbugs.xml"

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()
    assertThat(javaProject.spotbugs.excludeFilter).isEqualTo(rootProject.file(extension.spotbugs.excludeFilter))
  }

  @Test
  fun reports() {
    val extension = defaultExtensions()
    extension.htmlReports = true
    extension.xmlReports = false

    assertThat(javaProject.addSpotbugs(rootProject, extension)).isTrue()

    javaProject.spotbugsTask.apply {
      assertThat(reports.xml.isEnabled).isEqualTo(extension.xmlReports)
      assertThat(reports.html.isEnabled).isEqualTo(extension.htmlReports)
    }
  }

  @Test
  fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertThat(javaProject.addSpotbugs(rootProject, extension)).isFalse()
  }

  @Test
  fun enabled() {
    val extension = defaultExtensions()
    extension.spotbugs.enabled = false

    for (project in projects) {
      assertThat(project.addSpotbugs(rootProject, extension)).isFalse()
    }
  }
}
