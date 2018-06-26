package com.vanniktech.code.quality.tools

import org.assertj.core.api.Java6Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CodeQualityToolsPluginDetektTest {
  @get:Rule val testProjectDir = TemporaryFolder()

  @Test fun success() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit\n")
        .succeeds()
  }

  @Test fun differentConfigFile() {
    Roboter(testProjectDir, config = "code_quality_tools/config-detekt.yml")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit\n")
        .succeeds()
  }

  @Test fun fails() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit")
        .fails()
  }

  @Test fun disabled() {
    Roboter(testProjectDir, enabled = false)
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit")
        .doesNothing()
  }

  @Test fun creatingInitialBaselineFails() {
    Roboter(testProjectDir, baselineFileName = "detekt-baseline.xml")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit")
        .fails()
        .baseLineContains("<ID>NewLineAtEndOfFile:Foo.kt\$.Foo.kt</ID>")
  }

  @Test fun runningWithBaseLineSucceeds() {
    Roboter(testProjectDir, baselineFileName = "detekt-baseline.xml")
        .withConfiguration("failFast: true")
        .withBaseLineFile("""
            <?xml version="1.0"?>
            <SmellBaseline>
              <Blacklist timestamp="1529425729991"></Blacklist>
              <Whitelist timestamp="1529425729991">
                <ID>NewLineAtEndOfFile:Foo.kt$.Foo.kt</ID>
              </Whitelist>
            </SmellBaseline>""".trimIndent())
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit")
        .succeeds()
  }

  @Test fun mayBeConstSucceedsWithReleaseCandidate7() {
    Roboter(testProjectDir, version = "1.0.0.RC7")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", """
            |const val TEST = "test"
            |val TEST2 = "test" + TEST
            |""".trimMargin())
        .succeeds()
  }

  @Test fun mayBeConstFailsWithReleaseCandidate72() {
    // RC7-2 fixed this - https://github.com/arturbosch/detekt/pull/930
    Roboter(testProjectDir, version = "1.0.0.RC7-2")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", """
            |const val TEST = "test"
            |val TEST2 = "test" + TEST
            |""".trimMargin())
        .fails()
  }

  @Test fun checkTaskRunsDetekt() {
    Roboter(testProjectDir, taskToRun = "check")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit")
        .fails()
  }

  class Roboter(
    private val directory: TemporaryFolder,
    private val config: String = "code_quality_tools/detekt.yml",
    enabled: Boolean = true,
    version: String = "1.0.0.RC6",
    private val baselineFileName: String? = null,
    private val taskToRun: String = "detektCheck"
  ) {
    init {
      directory.newFile("build.gradle").writeText("""
          |plugins {
          |  id "kotlin"
          |  id "com.vanniktech.code.quality.tools"
          |}
          |
          |codeQualityTools {
          |  detekt {
          |    config = "$config"
          |    enabled = $enabled
          |    toolVersion = "$version"
          |    baselineFileName = ${baselineFileName.wrap("\"")}
          |  }
          |}
          |
          |repositories {
          |  jcenter()
          |}
          |""".trimMargin())
    }

    fun withBaseLineFile(content: String) = write(requireNotNull(baselineFileName), content)

    fun withConfiguration(configuration: String) = write(config, configuration)

    fun withKotlinFile(path: String, content: String) = write(path, content)

    private fun write(path: String, content: String) = apply {
      directory.newFolder(*path.split("/").dropLast(1).toTypedArray())
      directory.newFile(path).writeText(content)
    }

    fun succeeds() = apply {
      assertThat(run().build().task(":detektCheck")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    fun fails() = apply {
      assertThat(run().buildAndFail().task(":detektCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
    }

    fun doesNothing() = apply {
      assertThat(run().buildAndFail().task(":detektCheck")).isNull()
    }

    fun baseLineContains(content: String) {
      assertThat(File(directory.root, baselineFileName).readText()).contains(content)
    }

    private fun run() = GradleRunner.create().withPluginClasspath().withProjectDir(directory.root).withArguments(taskToRun)
  }
}

private fun String?.wrap(wrap: String) = if (this == null) null else "$wrap$this$wrap"
