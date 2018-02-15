package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addDetekt

class CodeQualityToolsPluginDetektTest extends CommonCodeQualityToolsTest {
  @Test void empty() {
    emptyProjects.each { project ->
      assert !addDetekt(project, rootProject, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void java() {
    javaProjects.each { project ->
      assert !addDetekt(project, rootProject, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
      assert addDetekt(project, rootProject, new CodeQualityToolsPluginExtensionForTests())

      assertDetekt(project, rootProject)
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert !addDetekt(project, rootProject, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  private static void assertDetekt(Project project, Project rootProject) {
    def dependencies = project.configurations.getByName('detektCheck').dependencies
    assert dependencies.size() == 1

    def detektCheck = dependencies[0]
    assert detektCheck.group == 'io.gitlab.arturbosch.detekt'
    assert detektCheck.name == 'detekt-cli'
    assert detektCheck.version == '1.0.0.RC6'
    assert taskDependsOn(project.check, 'detektCheck')

    assert project.getTasksByName('detektCheck', false).size() == 1
    def detektCheckTask = project.getTasksByName('detektCheck', false)[0]
    assert detektCheckTask.group == 'verification'
    assert detektCheckTask.description == 'Runs detekt.'
    assert detektCheckTask.main == 'io.gitlab.arturbosch.detekt.cli.Main'
    assert detektCheckTask.args.size() == 6
    assert detektCheckTask.args[0] == '--config'
    assert detektCheckTask.args[1] == rootProject.file('code_quality_tools/detekt.yml').toString()
    assert detektCheckTask.args[2] == '--input'
    assert detektCheckTask.args[3] == project.file('.').toString()
    assert detektCheckTask.args[4] == '--output'
    assert detektCheckTask.args[5] == new File(project.buildDir, "reports/detekt").toString()
  }

  @Test void configurations() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.detekt.toolVersion = '1.0.0-RC5'

    assert addDetekt(kotlinPlatformCommonProject, rootProject, extension)

    def detektCheck = kotlinPlatformCommonProject.configurations.getByName('detektCheck').dependencies[0]
    assert detektCheck.group == 'io.gitlab.arturbosch.detekt'
    assert detektCheck.name == 'detekt-cli'
    assert detektCheck.version == '1.0.0-RC5'
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.detekt.enabled = false

    for (def project : projects) {
      assert !addDetekt(project, rootProject, extension)
    }
  }
}
