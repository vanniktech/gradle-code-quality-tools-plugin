package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.gradle.api.plugins.quality.PmdPlugin
import org.junit.Assert.assertEquals
import org.junit.Test

class CodeQualityToolsPluginPmdTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertEquals(false, project.addPmd(rootProject, defaultExtensions()))
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertEquals(true, project.addPmd(rootProject, defaultExtensions()))

      assertPmd(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      // Ideally we don't want to be running this in kotlin projects but since it uses the java library under the hood we can't do much.
      assertEquals(true, project.addPmd(rootProject, defaultExtensions()))

      assertPmd(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertEquals(true, project.addPmd(rootProject, defaultExtensions()))

      assertPmd(project)
    }
  }

  private fun assertPmd(project: Project) {
    assertEquals(true, project.plugins.hasPlugin(PmdPlugin::class.java))

    assertEquals(false, project.pmd.isIgnoreFailures)
    assertEquals("6.0.0", project.pmd.toolVersion)
    assertEquals(rootProject.file("code_quality_tools/pmd.xml"), project.pmd.ruleSetFiles.singleFile)

    project.pmdTask.apply {
      assertEquals("Runs pmd.", description)
      assertEquals("verification", group)

      assertEquals(rootProject.file("code_quality_tools/pmd.xml"), ruleSetFiles.singleFile)
      assertEquals(true, ruleSets.isEmpty())

      assertEquals(1, includes.size)
      assertEquals(true, includes.contains("**/*.java"))

      assertEquals(1, excludes.size)
      assertEquals(true, excludes.contains("**/gen/**"))

      assertEquals(true, reports.xml.required.get())
      assertEquals(false, reports.html.required.get())
    }

    assertEquals(true, taskDependsOn(project.check, "pmd"))
  }

  @Test fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.pmd.ignoreFailures = false

    assertEquals(true, androidAppProject.addPmd(rootProject, extension))
    assertEquals(false, androidAppProject.pmd.isIgnoreFailures)

    assertEquals(true, androidLibraryProject.addPmd(rootProject, extension))
    assertEquals(false, androidLibraryProject.pmd.isIgnoreFailures)

    assertEquals(true, javaProject.addPmd(rootProject, extension))
    assertEquals(false, javaProject.pmd.isIgnoreFailures)
  }

  @Test fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.pmd.ignoreFailures = true

    assertEquals(true, androidAppProject.addPmd(rootProject, extension))
    assertEquals(true, androidAppProject.pmd.isIgnoreFailures)

    assertEquals(true, androidLibraryProject.addPmd(rootProject, extension))
    assertEquals(true, androidLibraryProject.pmd.isIgnoreFailures)

    assertEquals(true, javaProject.addPmd(rootProject, extension))
    assertEquals(true, javaProject.pmd.isIgnoreFailures)
  }

  @Test fun include() {
    val extension = defaultExtensions()
    extension.pmd.include = listOf("*.java")

    assertEquals(true, javaProject.addPmd(rootProject, extension))

    javaProject.pmdTask.apply {
      assertEquals(1, includes.size)
      assertEquals(true, includes.contains("*.java"))
    }
  }

  @Test fun exclude() {
    val extension = defaultExtensions()
    extension.pmd.exclude = listOf("**/gen")

    assertEquals(true, javaProject.addPmd(rootProject, extension))

    javaProject.pmdTask.apply {
      assertEquals(1, excludes.size)
      assertEquals(true, excludes.contains("**/gen"))
    }
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertEquals(true, javaProject.addPmd(rootProject, extension))
    assertEquals(true, javaProject.pmd.isIgnoreFailures)
  }

  @Test fun toolsVersion() {
    val extension = defaultExtensions()
    extension.pmd.toolVersion = "5.4.0"

    assertEquals(true, javaProject.addPmd(rootProject, extension))
    assertEquals(extension.pmd.toolVersion, javaProject.pmd.toolVersion)
  }

  @Test fun configFiles() {
    val extension = defaultExtensions()
    extension.pmd.ruleSetFile = "pmd.xml"

    assertEquals(true, javaProject.addPmd(rootProject, extension))
    assertEquals(rootProject.file(extension.pmd.ruleSetFile), javaProject.pmd.ruleSetFiles.singleFile)
  }

  @Test fun reports() {
    val extension = defaultExtensions()
    extension.htmlReports = true
    extension.xmlReports = false

    assertEquals(true, javaProject.addPmd(rootProject, extension))

    javaProject.pmdTask.apply {
      assertEquals(extension.xmlReports, reports.xml.required.get())
      assertEquals(extension.htmlReports, reports.html.required.get())
    }
  }

  @Test fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertEquals(false, javaProject.addPmd(rootProject, extension))
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.pmd.enabled = false

    for (project in projects) {
      assertEquals(false, project.addPmd(rootProject, extension))
    }
  }
}
