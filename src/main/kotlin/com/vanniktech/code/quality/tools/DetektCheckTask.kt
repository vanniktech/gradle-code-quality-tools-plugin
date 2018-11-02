package com.vanniktech.code.quality.tools

import com.android.ide.common.repository.GradleVersion
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
    val fixedVersion = version.replace(".RC", "-RC") // GradleVersion does not understand . as a - in this case. Let's fix it and hope it does not break.
    val shouldUseReport = GradleVersion.parse(fixedVersion) >= VERSION_REPORT_CHANGE
    val reportKey = if (shouldUseReport) "--report" else "--output"
    val reportValue = if (shouldUseReport) {
      listOf(ReportingMetaInformation("plain", "txt", "plain"), ReportingMetaInformation("xml", "xml", "checkstyle"), ReportingMetaInformation("html", "html", "report"))
          .joinToString(separator = ",") { it.reportId + ":" + File(outputDirectory, "detekt-${it.fileNameSuffix}.${it.fileEnding}").absolutePath }
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

  // Can merge reportId and fileEnding after https://github.com/arturbosch/detekt/issues/1111
  internal data class ReportingMetaInformation(
    val reportId: String,
    val fileEnding: String,
    val fileNameSuffix: String
  )

  internal companion object {
    internal val VERSION_REPORT_CHANGE = GradleVersion.parse("1.0.0-RC9")
  }
}
