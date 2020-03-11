package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.NONE
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.File

@CacheableTask open class DetektCheckTask : DefaultTask() {
  @Input var failFast: Boolean = true
  @Input var buildUponDefaultConfig: Boolean = false
  @Input var parallel: Boolean = false
  @Input lateinit var version: String

  // Ideally this would be an optional input file - https://github.com/gradle/gradle/issues/2016
  @Input @Optional var baselineFilePath: String? = null
  @InputFile @PathSensitive(RELATIVE) lateinit var configFile: File
  @OutputDirectory @PathSensitive(NONE) lateinit var outputDirectory: File

  init {
    group = "verification"
    description = "Runs detekt."
  }

  @TaskAction fun run() {
    val configuration = project.configurations.getByName("detekt")

    baselineFilePath?.let { file ->
      if (!File(file).exists()) {
        executeDetekt(configuration, shouldCreateBaseLine = true)
        throw TaskExecutionException(this, GradleException("Aborting build since new baseline file was created"))
      }
    }

    executeDetekt(configuration)
  }

  private fun executeDetekt(configuration: FileCollection, shouldCreateBaseLine: Boolean = false) {
    val reportKey = "--report"
    val reportValue = listOf(
          ReportingMetaInformation("plain", "txt", "plain"),
          ReportingMetaInformation("xml", "xml", "checkstyle"),
          ReportingMetaInformation("html", "html", "report")
      ).joinToString(separator = ",") {
        it.fileEnding + ":" + File(outputDirectory, "detekt-${it.fileNameSuffix}.${it.fileEnding}").absolutePath
      }

    project.javaexec { task ->
      task.main = "io.gitlab.arturbosch.detekt.cli.Main"
      task.classpath = configuration
      task.args(
          "--input", project.file("."),
          reportKey, reportValue
      )

      if (configFile.exists()) {
          task.args("--config", configFile)
      }

      task.args("--excludes", "**/build/**")

      if (failFast) {
        task.args("--fail-fast")
      }

      if (buildUponDefaultConfig) {
        task.args("--build-upon-default-config")
      }

      if (parallel) {
        task.args("--parallel")
      }

      if (shouldCreateBaseLine) {
        task.args("--create-baseline")
      }

      baselineFilePath?.let {
        task.args("--baseline", it)
      }
    }
  }

  internal data class ReportingMetaInformation(
    val reportId: String,
    val fileEnding: String,
    val fileNameSuffix: String
  )
}
