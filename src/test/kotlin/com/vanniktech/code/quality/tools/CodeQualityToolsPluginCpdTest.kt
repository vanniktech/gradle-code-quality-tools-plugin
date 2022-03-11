package com.vanniktech.code.quality.tools

import de.aaschmid.gradle.plugins.cpd.CpdPlugin
import org.gradle.api.Project
import org.junit.Assert.assertEquals
import org.junit.Test

class CodeQualityToolsPluginCpdTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertEquals(false, project.addCpd(defaultExtensions()))
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertEquals(true, project.addCpd(defaultExtensions()))

      assertCpd(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      // Ideally we don"t want to be running this in kotlin projects but since it uses the java library under the hood we can"t do much.
      assertEquals(true, project.addCpd(defaultExtensions()))

      assertCpd(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertEquals(true, project.addCpd(defaultExtensions()))

      assertCpd(project)
    }
  }

  private fun assertCpd(project: Project) {
    @Suppress("UnstableApiUsage")
    assertEquals(true, project.plugins.hasPlugin(CpdPlugin::class.java))

    assertEquals("6.0.0", project.cpd.toolVersion)
    assertEquals("java", project.cpd.language)

    project.cpdTask.apply {
      assertEquals("Runs cpd.", description)
      assertEquals("verification", group)
      assertEquals("UTF-8", encoding)

      assertEquals(50, minimumTokenCount)
      assertEquals(false, ignoreFailures)

      assertEquals(true, reports.xml.required.get())
      assertEquals(false, reports.text.required.get())
    }

    assertEquals(true, taskDependsOn(project.check, "cpdCheck"))
  }

  @Test fun ignoreFailuresFalse() {
    val extension = defaultExtensions()
    extension.cpd.ignoreFailures = false

    assertEquals(true, androidAppProject.addCpd(extension))
    assertEquals(false, androidAppProject.cpd.isIgnoreFailures)

    assertEquals(true, androidLibraryProject.addCpd(extension))
    assertEquals(false, androidLibraryProject.cpd.isIgnoreFailures)

    assertEquals(true, javaProject.addCpd(extension))
    assertEquals(false, javaProject.cpd.isIgnoreFailures)
  }

  @Test fun ignoreFailuresTrue() {
    val extension = defaultExtensions()
    extension.cpd.ignoreFailures = true

    assertEquals(true, androidAppProject.addCpd(extension))
    assertEquals(true, androidAppProject.cpd.isIgnoreFailures)

    assertEquals(true, androidLibraryProject.addCpd(extension))
    assertEquals(true, androidLibraryProject.cpd.isIgnoreFailures)

    assertEquals(true, javaProject.addCpd(extension))
    assertEquals(true, javaProject.cpd.isIgnoreFailures)
  }

  @Test fun failEarlyFalse() {
    val extension = defaultExtensions()
    extension.failEarly = false

    assertEquals(true, javaProject.addCpd(extension))
    assertEquals(true, javaProject.cpd.isIgnoreFailures)
  }

  @Test fun toolsVersion() {
    val extension = defaultExtensions()
    extension.pmd.toolVersion = "5.4.0" // We take the PMD version for CPD since they"re using the same tool.

    assertEquals(true, javaProject.addCpd(extension))
    assertEquals("5.4.0", javaProject.cpd.toolVersion)
  }

  @Test fun language() {
    val extension = defaultExtensions()
    extension.cpd.language = "swift"

    assertEquals(true, javaProject.addCpd(extension))
    assertEquals("swift", javaProject.cpd.language)
  }

  @Test fun minimumTokenCount() {
    val extension = defaultExtensions()
    extension.cpd.minimumTokenCount = 50

    assertEquals(true, javaProject.addCpd(extension))
    assertEquals(50, javaProject.cpd.minimumTokenCount)
  }

  @Test fun reports() {
    val extension = defaultExtensions()
    extension.textReports = true
    extension.xmlReports = false

    assertEquals(true, javaProject.addCpd(extension))

    javaProject.cpdTask.apply {
      assertEquals(extension.xmlReports, reports.xml.required.get())
      assertEquals(extension.textReports, reports.text.required.get())
    }
  }

  @Test fun ignoreProject() {
    val extension = defaultExtensions()
    extension.ignoreProjects = listOf(javaProject.name)
    assertEquals(false, javaProject.addCpd(extension))
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.cpd.enabled = false

    for (project in projects) {
      assertEquals(false, project.addCpd(extension))
    }
  }
}
