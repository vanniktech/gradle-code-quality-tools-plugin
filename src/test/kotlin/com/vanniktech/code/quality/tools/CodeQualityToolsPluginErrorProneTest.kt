package com.vanniktech.code.quality.tools

import net.ltgt.gradle.errorprone.ErrorPronePlugin
import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.api.Project
import org.junit.Test

class CodeQualityToolsPluginErrorProneTest : CommonCodeQualityToolsTest() {
  @Test fun empty() {
    emptyProjects.forEach { project ->
      assertThat(project.addErrorProne(defaultExtensions())).isFalse()
    }
  }

  @Test fun java() {
    javaProjects.forEach { project ->
      assertThat(project.addErrorProne(defaultExtensions())).isTrue()

      assertErrorProne(project)
    }
  }

  @Test fun kotlin() {
    kotlinProjects.forEach { project ->
      // Ideally we don't want to be running this in kotlin projects but since it uses the java library under the hood we can't do much.
      assertThat(project.addErrorProne(defaultExtensions())).isTrue()

      assertErrorProne(project)
    }
  }

  @Test fun android() {
    androidProjects.forEach { project ->
      assertThat(project.addErrorProne(defaultExtensions())).isTrue()

      assertErrorProne(project)
    }
  }

  private fun assertErrorProne(project: Project) {
    assertThat(project.plugins.hasPlugin(ErrorPronePlugin::class.java)).isTrue()

    val forcedModules = project.configurations.getByName("errorprone").resolutionStrategy.forcedModules

    assertThat(forcedModules.size).isEqualTo(1)
    assertThat(forcedModules.first().toString()).isEqualTo("com.google.errorprone:error_prone_core:2.1.3")
  }

  @Test fun version() {
    val extension = defaultExtensions()
    extension.errorProne.toolVersion = "2.2.0"

    javaProject.addErrorProne(extension)

    val forcedModules = javaProject.configurations.getByName("errorprone").resolutionStrategy.forcedModules

    assertThat(forcedModules.size).isEqualTo(1)
    assertThat(forcedModules.first().toString()).isEqualTo("com.google.errorprone:error_prone_core:2.2.0")
  }

  @Test fun enabled() {
    val extension = defaultExtensions()
    extension.errorProne.enabled = false

    for (project in projects) {
      assertThat(project.addErrorProne(extension)).isFalse()
    }
  }
}
