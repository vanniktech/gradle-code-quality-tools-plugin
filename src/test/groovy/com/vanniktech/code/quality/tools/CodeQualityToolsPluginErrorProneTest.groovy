package com.vanniktech.code.quality.tools

import net.ltgt.gradle.errorprone.ErrorPronePlugin
import org.gradle.api.Project
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addErrorProne

class CodeQualityToolsPluginErrorProneTest extends CommonCodeQualityToolsTest {
  @Test void empty() {
    emptyProjects.each { project ->
      assert !addErrorProne(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void java() {
    javaProjects.each { project ->
      assert addErrorProne(project, new CodeQualityToolsPluginExtensionForTests())

      assertErrorProne(project)
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
      // Ideally we don't want to be running this in kotlin projects but since it uses the java library under the hood we can't do much.
      assert addErrorProne(project, new CodeQualityToolsPluginExtensionForTests())

      assertErrorProne(project)
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert addErrorProne(project, new CodeQualityToolsPluginExtensionForTests())

      assertErrorProne(project)
    }
  }

  private static void assertErrorProne(Project project) {
    assert project.plugins.hasPlugin(ErrorPronePlugin)

    def forcedModules = project.configurations.getByName('errorprone').resolutionStrategy.forcedModules

    assert forcedModules.size() == 1
    assert forcedModules[0].toString() == "com.google.errorprone:error_prone_core:2.1.3"
  }

  @Test void version() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.errorProne.toolVersion = '2.2.0'

    addErrorProne(javaProject, extension)

    def forcedModules = javaProject.configurations.getByName('errorprone').resolutionStrategy.forcedModules

    assert forcedModules.size() == 1
    assert forcedModules[0].toString() == "com.google.errorprone:error_prone_core:2.2.0"
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.errorProne.enabled = false

    for (def project : projects) {
      assert !addErrorProne(project, extension)
    }
  }
}
