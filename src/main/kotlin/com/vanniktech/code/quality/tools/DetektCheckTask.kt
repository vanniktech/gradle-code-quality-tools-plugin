package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.util.VersionNumber
import java.io.File

@CacheableTask open class DetektCheckTask : DefaultTask() {
  @Input lateinit var version: String

  // Ideally this would be an optional input file - https://github.com/gradle/gradle/issues/2016
  @Input @Optional var baselineFilePath: String? = null
  @InputFile lateinit var configFile: File

  @OutputDirectory lateinit var outputDirectory: File

  init {
    group = "verification"
    description = "Runs detekt."
  }

  @TaskAction fun run() {
    val configuration = project.configurations.maybeCreate("detekt")

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
    val fixedVersion = VersionNumber.parse(version.replace(".RC", "-RC")) // GradleVersion does not understand . as a - in this case. Let's fix it and hope it does not break.
    val possibleRcVersion = Regex("RC[\\d]+").find(version)?.value?.replace("RC", "")?.toIntOrNull()
    val isAtLeastRc10 = possibleRcVersion != null && possibleRcVersion >= RC_BREAKING_POINT // GradleVersion thinks RC10 is smaller than RC9.
    val shouldUseReport = fixedVersion >= VERSION_REPORT_CHANGE || isAtLeastRc10
    val canUseFileEnding = fixedVersion >= VERSION_REPORT_EXTENSION_CHANGE && isAtLeastRc10

    val reportKey = if (shouldUseReport) "--report" else "--output"
    val reportValue = if (shouldUseReport) {
      listOf(
          ReportingMetaInformation("plain", "txt", "plain"),
          ReportingMetaInformation("xml", "xml", "checkstyle"),
          ReportingMetaInformation("html", "html", "report")
      ).joinToString(separator = ",") {
        val reportId = if (canUseFileEnding) it.fileEnding else it.reportId
        reportId + ":" + File(outputDirectory, "detekt-${it.fileNameSuffix}.${it.fileEnding}").absolutePath
      }
    } else {
      outputDirectory.absolutePath
    }

    project.javaexec { task ->
      task.main = "io.gitlab.arturbosch.detekt.cli.Main"
      task.classpath = configuration
      task.args(
          "--config", configFile,
          "--input", project.file("."),
          "--filters", ".*build/.*",
          reportKey, reportValue
      )

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

  internal companion object {
    internal const val RC_BREAKING_POINT = 10
    internal val VERSION_REPORT_CHANGE = VersionNumber.parse("1.0.0-RC9")
    internal val VERSION_REPORT_EXTENSION_CHANGE = VersionNumber.parse("1.0.0-RC10")
  }
}
