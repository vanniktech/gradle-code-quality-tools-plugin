package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KtLintFormatTask : DefaultTask() {
  @Input lateinit var version: String
  @OutputFile lateinit var checkStyleOutputFile: File

  init {
    group = "formatting"
    description = "Runs ktlint and autoformats your code."
  }

  @TaskAction fun run() {
    val configuration = project.configurations.maybeCreate("ktlint")

    project.dependencies.add("ktlint", "com.github.shyiko:ktlint:$version")

    project.javaexec { task ->
      task.main = "com.github.shyiko.ktlint.Main"
      task.classpath = configuration
      task.args("--reporter=plain", "--reporter=checkstyle,output=$checkStyleOutputFile", "-F", "**/*.kt", "**/*.kts", "!build/")
    }
  }
}
