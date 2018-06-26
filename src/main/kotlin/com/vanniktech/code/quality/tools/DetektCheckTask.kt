package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.File

open class DetektCheckTask : DefaultTask() {
  @Input lateinit var version: String

  // Ideally this would be an optional input file - https://github.com/gradle/gradle/issues/2016
  @Input @Optional var baselineFilePath: String? = null
  @InputFile lateinit var configFile: File

  @OutputDirectory lateinit var outputDirectory: File

  @TaskAction fun run() {
    val configuration = project.configurations.create("detekt")

    project.dependencies.add("detekt", "io.gitlab.arturbosch.detekt:detekt-cli:$version")

    baselineFilePath?.let { file ->
      if (!File(file).exists()) {
        executeDetekt(configuration, shouldCreateBaseLine = true)
        throw TaskExecutionException(this, GradleException("Aborting build since new baseline file was created"))
      }
    }

    executeDetekt(configuration)
  }

  private fun executeDetekt(configuration: FileCollection, shouldCreateBaseLine: Boolean = false) {
    project.javaexec { task ->
      task.main = "io.gitlab.arturbosch.detekt.cli.Main"
      task.classpath = configuration
      task.args(
          "--config", configFile,
          "--input", project.file("."),
          "--output", outputDirectory
      )

      if (shouldCreateBaseLine) {
        task.args("--create-baseline")
      }

      baselineFilePath?.let {
        task.args("--baseline", it)
      }
    }
  }
}
