package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.junit.Assert.assertEquals
import org.junit.Test

class CodeQualityToolsPluginCheckstyleTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertEquals(false, project.addCheckstyle(rootProject, defaultExtensions()))
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertEquals(true, project.addCheckstyle(rootProject, defaultExtensions()))

      assertCheckstyle(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      // Ideally we don"t want to be running this in kotlin projects but since it uses the java library under the hood we can"t do much.
      assertEquals(true, project.addCheckstyle(rootProject, defaultExtensions()))

      assertCheckstyle(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertEquals(true, project.addCheckstyle(rootProject, defaultExtensions()))

      assertCheckstyle(project)
    }
  }

  private fun assertCheckstyle(project: Project) {
    assertEquals(true, project.plugins.hasPlugin(CheckstylePlugin::class.java))

    assertEquals(false, project.checkstyle.isIgnoreFailures)
    assertEquals(true, project.checkstyle.isShowViolations)
    assertEquals("8.6", project.checkstyle.toolVersion)
    assertEquals(rootProject.file("code_quality_tools/checkstyle.xml"), project.checkstyle.configFile)

    project.checkstyleTask.apply {
      assertEquals("Runs checkstyle.", description)
      assertEquals("verification", group)

      assertEquals(rootProject.file("code_quality_tools/checkstyle.xml"), configFile)

      assertEquals(1, includes.size)
      assertEquals(true, includes.contains("**/*.java"))

      assertEquals(1, excludes.size)
      assertEquals(true, excludes.contains("**/gen/**"))

      assertEquals(true, reports.xml.required.get())
      assertEquals(false, reports.html.required.get())
    }

    assertEquals(true, taskDependsOn(project.check, "checkstyle"))
  }

  @Test fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.checkstyle.ignoreFailures = false

    assertEquals(true, androidAppProject.addCheckstyle(rootProject, extension))
    assertEquals(false, androidAppProject.checkstyle.isIgnoreFailures)

    assertEquals(true, androidLibraryProject.addCheckstyle(rootProject, extension))
    assertEquals(false, androidLibraryProject.checkstyle.isIgnoreFailures)

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))
    assertEquals(false, javaProject.checkstyle.isIgnoreFailures)
  }

  @Test fun include() {
    val extension = defaultExtensions()
    extension.checkstyle.include = listOf("*.java")

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))

    javaProject.checkstyleTask.apply {
      assertEquals(1, includes.size)
      assertEquals(true, includes.contains("*.java"))
    }
  }

  @Test fun exclude() {
    val extension = defaultExtensions()
    extension.checkstyle.exclude = listOf("**/gen")

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))

    javaProject.checkstyleTask.apply {
      assertEquals(1, excludes.size)
      assertEquals(true, excludes.contains("**/gen"))
    }
  }

  @Test fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.checkstyle.ignoreFailures = true

    assertEquals(true, androidAppProject.addCheckstyle(rootProject, extension))
    assertEquals(true, androidAppProject.checkstyle.isIgnoreFailures)

    assertEquals(true, androidLibraryProject.addCheckstyle(rootProject, extension))
    assertEquals(true, androidLibraryProject.checkstyle.isIgnoreFailures)

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))
    assertEquals(true, javaProject.checkstyle.isIgnoreFailures)
  }

  @Test fun showViolationsFalse() {
    val extension = defaultExtensions()
    extension.checkstyle.showViolations = false

    assertEquals(true, androidAppProject.addCheckstyle(rootProject, extension))
    assertEquals(false, androidAppProject.checkstyle.isShowViolations)

    assertEquals(true, androidLibraryProject.addCheckstyle(rootProject, extension))
    assertEquals(false, androidLibraryProject.checkstyle.isShowViolations)

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))
    assertEquals(false, javaProject.checkstyle.isShowViolations)
  }

  @Test fun showViolationsTrue() {
    val extension = defaultExtensions()
    extension.checkstyle.showViolations = true

    assertEquals(true, androidAppProject.addCheckstyle(rootProject, extension))
    assertEquals(true, androidAppProject.checkstyle.isShowViolations)

    assertEquals(true, androidLibraryProject.addCheckstyle(rootProject, extension))
    assertEquals(true, androidLibraryProject.checkstyle.isShowViolations)

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))
    assertEquals(true, javaProject.checkstyle.isShowViolations)
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))
    assertEquals(true, javaProject.checkstyle.isIgnoreFailures)
    assertEquals(false, javaProject.checkstyle.isShowViolations)
  }

  @Test fun toolsVersion() {
    val extension = defaultExtensions()
    extension.checkstyle.toolVersion = "6.14.0"

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))
    assertEquals(extension.checkstyle.toolVersion, javaProject.checkstyle.toolVersion)
  }

  @Test fun configFiles() {
    val extension = defaultExtensions()
    extension.checkstyle.configFile = "checkstyle.xml"

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))
    assertEquals(rootProject.file(extension.checkstyle.configFile), javaProject.checkstyle.configFile)
  }

  @Test fun reports() {
    val extension = defaultExtensions()
    extension.htmlReports = true
    extension.xmlReports = false

    assertEquals(true, javaProject.addCheckstyle(rootProject, extension))

    javaProject.checkstyleTask.apply {
      assertEquals(extension.xmlReports, reports.xml.required.get())
      assertEquals(extension.htmlReports, reports.html.required.get())
    }
  }

  @Test fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertEquals(false, javaProject.addCheckstyle(rootProject, extension))
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.checkstyle.enabled = false

    for (project in projects) {
      assertEquals(false, project.addCheckstyle(rootProject, extension))
    }
  }
}
