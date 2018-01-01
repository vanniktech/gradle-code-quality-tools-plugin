package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.junit.Test

import static com.vanniktech.code.quality.tools.CodeQualityToolsPlugin.addKtlint

class CodeQualityToolsPluginKtlintTest extends CommonCodeQualityToolsTest {
  @Test void empty() {
    emptyProjects.each { project ->
      assert !addKtlint(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void java() {
    javaProjects.each { project ->
      assert !addKtlint(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  @Test void kotlin() {
    kotlinProjects.each { project ->
      assert addKtlint(project, new CodeQualityToolsPluginExtensionForTests())

      assertKtlint(project)
    }
  }

  @Test void android() {
    androidProjects.each { project ->
      assert !addKtlint(project, new CodeQualityToolsPluginExtensionForTests())
    }
  }

  private static void assertKtlint(Project project) {
    def dependencies = project.configurations.getByName('ktlint').dependencies
    assert dependencies.size() == 1

    def ktlint = dependencies[0]
    assert ktlint.group == 'com.github.shyiko'
    assert ktlint.name == 'ktlint'
    assert ktlint.version == '0.14.0'
    assert taskDependsOn(project.check, 'ktlint')

    assert project.getTasksByName('ktlint', false).size() == 1
    def ktlintTask = project.getTasksByName('ktlint', false)[0]
    assert ktlintTask.group == 'verification'
    assert ktlintTask.description == 'Runs ktlint.'
    assert ktlintTask.main == 'com.github.shyiko.ktlint.Main'
    assert ktlintTask.args.size() == 3
    assert ktlintTask.args[0] == '--reporter=plain'
    assert ktlintTask.args[1] == "--reporter=checkstyle,output=${project.buildDir}/reports/ktlint/ktlint-checkstyle-report.xml"
    assert ktlintTask.args[2] == 'src/**/*.kt'

    assert project.getTasksByName('ktlintFormat', false).size() == 1
    def ktlintFormatTask = project.getTasksByName('ktlintFormat', false)[0]
    assert ktlintFormatTask.group == 'formatting'
    assert ktlintFormatTask.description == 'Runs ktlint and autoformats your code.'
    assert ktlintFormatTask.main == 'com.github.shyiko.ktlint.Main'
    assert ktlintFormatTask.args.size() == 2
    assert ktlintFormatTask.args[0] == '-F'
    assert ktlintFormatTask.args[1] == 'src/**/*.kt'
  }

  @Test void configurations() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.ktlint.toolVersion = '0.8.2'

    assert addKtlint(kotlinPlatformCommonProject, extension)

    def ktlint = kotlinPlatformCommonProject.configurations.getByName('ktlint').dependencies[0]
    assert ktlint.group == 'com.github.shyiko'
    assert ktlint.name == 'ktlint'
    assert ktlint.version == '0.8.2'
  }

  @Test void enabled() {
    def extension = new CodeQualityToolsPluginExtensionForTests()
    extension.ktlint.enabled = false

    for (def project : projects) {
      assert !addKtlint(project, extension)
    }
  }
}
