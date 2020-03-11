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

  private val testCode = "package com.vanniktech.test;\nfun foo(param: Int) = param * param\n"
  private val testPath = "src/main/kotlin/com/vanniktech/test/Foo.kt"

  @Test fun success() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile(testPath, testCode)
        .succeeds()
  }

  @Test fun works() {
    Roboter(testProjectDir, version = "1.0.0")
      .withConfiguration("") // Fail Fast is configured via the CLI parameter.
      .withKotlinFile(testPath, testCode)
      .succeeds()
  }

  @Test fun parallel() {
    Roboter(testProjectDir, parallel = true)
      .withConfiguration("failFast: true")
      .withKotlinFile(testPath, testCode)
      .succeeds()
  }

  @Test fun noSrcFolder() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .succeeds()
  }

  @Test fun defaultConfigFile() {
    Roboter(testProjectDir, buildUponDefaultConfig = true)
      .withConfiguration("failFast: true")
      .withKotlinFile(testPath, testCode)
      .succeeds()
  }

  @Test fun differentConfigFile() {
    Roboter(testProjectDir, config = "code_quality_tools/config-detekt.yml")
        .withConfiguration("failFast: true")
        .withKotlinFile(testPath, testCode)
        .succeeds()
  }

  @Test fun fails() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile(testPath, "fun foo() = Unit")
        .fails(containsMessage = "NewLineAtEndOfFile - [Foo.kt]")
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
        .fails(containsMessage = "NewLineAtEndOfFile - [build.gradle.kts]")
  }

  @Test fun disabled() {
    Roboter(testProjectDir, enabled = false)
        .withConfiguration("failFast: true")
        .withKotlinFile(testPath, "fun foo() = Unit")
        .doesNothing()
  }

  @Test fun creatingInitialBaselineFails() {
    Roboter(testProjectDir, baselineFileName = "detekt-baseline.xml")
        .withConfiguration("failFast: true")
        .withKotlinFile(testPath, "fun foo() = Unit")
        .fails(containsMessage = "NewLineAtEndOfFile - [Foo.kt]")
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
                <ID>InvalidPackageDeclaration:Foo.kt$.Foo.kt</ID>
              </Whitelist>
            </SmellBaseline>""".trimIndent())
        .withKotlinFile(testPath, "fun foo(i: Int) = i * 3\n")
        .succeeds()
  }

  @Test fun checkTaskRunsDetekt() {
    Roboter(testProjectDir)
        .withConfiguration("failFast: true")
        .withKotlinFile(testPath, "fun foo() = Unit")
        .fails(taskToRun = "check", taskToCheck = "detektCheck", containsMessage = "NewLineAtEndOfFile - [Foo.kt] at")
  }

  class Roboter(
    private val directory: TemporaryFolder,
    private val config: String = "code_quality_tools/detekt.yml",
    enabled: Boolean = true,
    version: String = "1.0.0",
    private val baselineFileName: String? = null,
    buildUponDefaultConfig: Boolean = false,
    parallel: Boolean = false
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
          |    enabled = $enabled
          |    parallel = $parallel
          |    buildUponDefaultConfig = $buildUponDefaultConfig
          |    config = "$config"
          |    toolVersion = "$version"
          |    baselineFileName = ${baselineFileName.wrap("\"")}
          |  }
          |  ktlint.enabled = false
          |  checkstyle.enabled = false
          |  pmd.enabled = false
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
      val paths = path.split("/").dropLast(1).toTypedArray()
      if (paths.isNotEmpty()) directory.newFolder(*paths)
      directory.newFile(path).writeText(content)
    }

    fun succeeds(taskToRun: String = "detektCheck") = apply {
      assertThat(run(taskToRun).build().task(":$taskToRun")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
      assertReportsExist()
    }

    fun fails(taskToRun: String = "detektCheck", taskToCheck: String = taskToRun, containsMessage: String) = apply {
      val buildResult = run(taskToRun).buildAndFail()
      assertThat(buildResult.task(":$taskToCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
      assertThat(buildResult.output).contains(containsMessage)
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
