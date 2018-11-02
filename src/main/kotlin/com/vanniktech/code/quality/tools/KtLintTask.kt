package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask open class KtLintTask : DefaultTask() {
  @Input lateinit var version: String
  @OutputFile lateinit var checkStyleOutputFile: File

  init {
    group = "verification"
    description = "Runs ktlint."
  }

  @TaskAction fun run() {
    val configuration = project.configurations.maybeCreate("ktlint")

    project.dependencies.add("ktlint", "com.github.shyiko:ktlint:$version")

    project.javaexec { task ->
      task.main = "com.github.shyiko.ktlint.Main"
      task.classpath = configuration
      task.args("--reporter=plain", "--reporter=checkstyle,output=$checkStyleOutputFile", "**/*.kt", "**/*.kts", "!build/")
    }
  }
}
