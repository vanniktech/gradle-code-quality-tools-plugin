package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.NONE
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask open class KtLintTask : DefaultTask() {
  @Input lateinit var version: String
  @OutputDirectory @PathSensitive(NONE) lateinit var outputDirectory: File

  init {
    group = "verification"
    description = "Runs ktlint."
  }

  @TaskAction fun run() {
    project.javaexec { task ->
      task.main = "com.github.shyiko.ktlint.Main"
      task.classpath = project.configurations.getByName("ktlint")
      task.args("--reporter=plain", "--reporter=checkstyle,output=${File(outputDirectory, "ktlint-checkstyle-report.xml")}", "**/*.kt", "**/*.kts", "!build/")
    }
  }
}
