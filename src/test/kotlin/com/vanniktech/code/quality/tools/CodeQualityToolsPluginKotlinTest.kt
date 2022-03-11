package com.vanniktech.code.quality.tools

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CodeQualityToolsPluginKotlinTest {
  @get:Rule val testProjectDir = TemporaryFolder()

  @Test fun success() {
    Roboter(testProjectDir)
      .withKotlinFile("src/main/kotlin/com/vanniktech/test/Foo.kt", "fun foo(param: Int) = param * param\n")
      .succeeds()
  }

  @Test fun fails() {
    Roboter(testProjectDir)
      .withKotlinFile(
        "src/main/kotlin/com/vanniktech/test/Foo.kt",
        """
            @Deprecated("Don't use this") fun bar() = { }
            fun foo() = bar()
        """.trimIndent()
      )
      .fails(containsMessage = "bar(): () -> Unit' is deprecated. Don't use this")
  }

  class Roboter(
    private val directory: TemporaryFolder
  ) {
    init {
      directory.newFile("build.gradle").writeText(
        """
          |plugins {
          |  id "kotlin"
          |  id "com.vanniktech.code.quality.tools"
          |}
          |
          |codeQualityTools {
          |  ktlint.enabled = false
          |  detekt.enabled = false
          |  checkstyle.enabled = false
          |  pmd.enabled = false
          |  cpd.enabled = false
          |}
          |
          |repositories {
          |  mavenCentral()
          |}
          |
          |dependencies {
          |  implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.3.41'
          |}
          |
        """.trimMargin()
      )
    }

    fun withKotlinFile(path: String, content: String) = write(path, content)

    private fun write(path: String, content: String) = apply {
      val paths = path.split("/").dropLast(1).toTypedArray()
      if (paths.isNotEmpty()) directory.newFolder(*paths)
      directory.newFile(path).writeText(content)
    }

    fun succeeds(taskToRun: String = "compileKotlin") = apply {
      assertEquals(TaskOutcome.SUCCESS, run(taskToRun).build().task(":$taskToRun")?.outcome)
    }

    fun fails(taskToRun: String = "compileKotlin", taskToCheck: String = taskToRun, containsMessage: String) = apply {
      val buildResult = run(taskToRun).buildAndFail()
      assertEquals(TaskOutcome.FAILED, buildResult.task(":$taskToCheck")?.outcome)
      assertEquals(true, buildResult.output.contains(containsMessage))
    }

    private fun run(taskToRun: String) = GradleRunner.create().withPluginClasspath().withProjectDir(directory.root).withArguments(taskToRun)
  }
}
