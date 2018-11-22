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
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
        .succeeds()
  }

  @Test fun successBreakingReportChangeRC9() {
    Roboter(testProjectDir, version = "1.0.0.RC9")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
        .succeeds()
  }

  @Test fun successBreakingReportChangeRC92() {
    Roboter(testProjectDir, version = "1.0.0.RC9.2")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
        .succeeds()
  }

  @Test fun successBreakingReportChangeRC10() {
    Roboter(testProjectDir, version = "1.0.0-RC10")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
        .succeeds()
  }

  @Test fun worksWithRC11() {
    Roboter(testProjectDir, version = "1.0.0-RC11")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
        .succeeds()
  }

  @Test fun noSrcFolder() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .succeeds()
  }

  @Test fun differentConfigFile() {
    Roboter(testProjectDir, config = "code_quality_tools/config-detekt.yml")
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
        .succeeds()
  }

  @Test fun fails() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit")
        .fails()
  }

  @Test fun ignoresFileInBuildDirectory() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile("build/Foo.kt", "fun foo() = Unit")
        .succeeds()
  }

  @Test fun failsOnKotlinScript() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile("build.gradle.kts", "fun foo() = Unit")
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
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile("src/main/com/vanniktech/test/Foo.kt", "fun foo() = Unit")
        .fails(taskToRun = "check", taskToCheck = "detektCheck")
  }

  class Roboter(
    private val directory: TemporaryFolder,
    private val config: String = "code_quality_tools/detekt.yml",
    enabled: Boolean = true,
    version: String = "1.0.0.RC6",
    private val baselineFileName: String? = null
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
          |  ktlint.enabled = false
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

    fun withBaseLineFile(content: String) = write(requireNotNull(baselineFileName), content)

    fun withConfiguration(configuration: String) = write(config, configuration)

    fun withKotlinFile(path: String, content: String) = write(path, content)

    private fun write(path: String, content: String) = apply {
      directory.newFolder(*path.split("/").dropLast(1).toTypedArray())
      directory.newFile(path).writeText(content)
    }

    fun succeeds(taskToRun: String = "detektCheck") = apply {
      assertThat(run(taskToRun).build().task(":$taskToRun")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
      assertReportsExist()
    }

    fun fails(taskToRun: String = "detektCheck", taskToCheck: String = taskToRun) = apply {
      assertThat(run(taskToRun).buildAndFail().task(":$taskToCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
      assertReportsExist()
    }

    private fun assertReportsExist() {
      assertThat(File(directory.root, "build/reports/detekt/detekt-report.html")).exists()
      assertThat(File(directory.root, "build/reports/detekt/detekt-checkstyle.xml")).exists()
      assertThat(File(directory.root, "build/reports/detekt/detekt-plain.txt")).exists()
    }

    fun doesNothing(taskToRun: String = "detektCheck") = apply {
      assertThat(run(taskToRun).buildAndFail().task(":$taskToRun")).isNull()
    }

    fun baseLineContains(content: String) {
      assertThat(File(directory.root, baselineFileName).readText()).contains(content)
    }

    private fun run(taskToRun: String) = GradleRunner.create().withPluginClasspath().withProjectDir(directory.root).withArguments(taskToRun)
  }
}

private fun String?.wrap(wrap: String) = if (this == null) null else "$wrap$this$wrap"
