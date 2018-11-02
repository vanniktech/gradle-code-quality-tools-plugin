package com.vanniktech.code.quality.tools

import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CodeQualityToolsPluginKtlintTest {
  @get:Rule val testProjectDir = TemporaryFolder()

  @Test fun success() {
    Roboter(testProjectDir)
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
        .succeeds()
  }

  @Test fun configuration() {
    Roboter(testProjectDir)
        .withConfiguration("""
            |[*.{kt,kts}]
            |insert_final_newline=true
            """.trimMargin())
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param")
        .fails()
  }

  @Test fun autoFormat() {
    Roboter(testProjectDir)
        .withConfiguration("""
            |[*.{kt,kts}]
            |insert_final_newline=true
            """.trimMargin())
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param")
        .succeeds(taskToRun = "ktlintFormat")
        .hasKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
  }

  @Test fun noSrcFolder() {
    Roboter(testProjectDir)
        .succeeds()
  }

  @Test fun fails() {
    Roboter(testProjectDir)
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo( ) = Unit")
        .fails()
  }

  @Test fun ignoresFileInBuildDirectory() {
    Roboter(testProjectDir)
        .withKotlinFile("build/Foo.kt", "fun foo( ) = Unit")
        .succeeds()
  }

  @Test fun failsOnKotlinScript() {
    Roboter(testProjectDir)
        .withKotlinFile("build.gradle.kts", "fun foo( ) = Unit")
        .fails()
  }

  @Test fun autoCorrectKotlinScript() {
    Roboter(testProjectDir)
        .withKotlinFile("build.gradle.kts", "fun foo( ) = Unit")
        .succeeds(taskToRun = "ktlintFormat")
        .hasKotlinFile("build.gradle.kts", "fun foo() = Unit")
  }

  @Test fun disabled() {
    Roboter(testProjectDir, enabled = false)
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo( ) = Unit")
        .doesNothing()
  }

  @Test fun checkTaskRunsKtlint() {
    Roboter(testProjectDir)
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo( ) = Unit")
        .fails(taskToRun = "check", taskToCheck = "ktlint")
  }

  class Roboter(
    private val directory: TemporaryFolder,
    enabled: Boolean = true,
    version: String = "0.29.0"
  ) {
    init {
      directory.newFile("build.gradle").writeText("""
          |plugins {
          |  id "kotlin"
          |  id "com.vanniktech.code.quality.tools"
          |}
          |
          |codeQualityTools {
          |  ktlint {
          |    enabled = $enabled
          |    toolVersion = "$version"
          |  }
          |  detekt.enabled = false
          |  checkstyle.enabled = false
          |  pmd.enabled = false
          |  findbugs.enabled = false
          |  cpd.enabled = false
          |  errorProne.enabled = false
          |}
          |
          |repositories {
          |  jcenter()
          |}
          |""".trimMargin())
    }

    fun withConfiguration(configuration: String) = write(".editorconfig", configuration)

    fun withKotlinFile(path: String, content: String) = write(path, content)

    fun hasKotlinFile(path: String, content: String) {
      assertThat(File(directory.root, path)).hasContent(content)
    }

    private fun write(path: String, content: String) = apply {
      directory.newFolder(*path.split("/").dropLast(1).toTypedArray())
      directory.newFile(path).writeText(content)
    }

    fun succeeds(taskToRun: String = "ktlint") = apply {
      assertThat(run(taskToRun).build().task(":$taskToRun")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
      assertReportsExist()
    }

    fun fails(taskToRun: String = "ktlint", taskToCheck: String = taskToRun) = apply {
      assertThat(run(taskToRun).buildAndFail().task(":$taskToCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
      assertReportsExist()
    }

    private fun assertReportsExist() {
      assertThat(File(directory.root, "build/reports/ktlint/ktlint-checkstyle-report.xml")).exists()
    }

    fun doesNothing(taskToRun: String = "ktlint") = apply {
      assertThat(run(taskToRun).buildAndFail().task(":$taskToRun")).isNull()
    }

    private fun run(taskToRun: String) = GradleRunner.create().withPluginClasspath().withProjectDir(directory.root).withArguments(taskToRun)
  }
}
