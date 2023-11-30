package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutionException
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

@CacheableTask abstract class DetektCheckTask : DefaultTask() {
  @InputDirectory @PathSensitive(RELATIVE) lateinit var inputFile: File
  @Input var failFast: Boolean = true
  @Input var buildUponDefaultConfig: Boolean = false
  @Input var parallel: Boolean = false
  @Input lateinit var version: String
  @get:Classpath abstract val classpath: ConfigurableFileCollection

  // Ideally this would be an optional input file - https://github.com/gradle/gradle/issues/2016
  @Input @Optional var baselineFilePath: String? = null
  @InputFile @PathSensitive(RELATIVE) lateinit var configFile: File
  @OutputDirectory lateinit var outputDirectory: File

  init {
    group = "verification"
    description = "Runs detekt."
  }

  @get:Inject internal abstract val workerExecutor: WorkerExecutor

  @TaskAction fun run() {
    val queue = workerExecutor.noIsolation()

    queue.submit(DetektWorker::class.java) {
      it.baselineFilePath.set(baselineFilePath)
      it.buildUponDefaultConfig.set(buildUponDefaultConfig)
      it.classpath.from(classpath)
      it.configFile.set(configFile)
      it.failFast.set(failFast)
      it.inputFile.set(inputFile)
      it.outputDirectory.set(outputDirectory)
      it.parallel.set(parallel)
    }
  }
}

internal data class ReportingMetaInformation(
  val reportId: String,
  val fileEnding: String,
  val fileNameSuffix: String,
)

internal interface DetektParameters : WorkParameters {
  val baselineFilePath: Property<String?>
  val buildUponDefaultConfig: Property<Boolean>
  val classpath: ConfigurableFileCollection
  val configFile: RegularFileProperty
  val failFast: Property<Boolean>
  val inputFile: RegularFileProperty
  val outputDirectory: RegularFileProperty
  val parallel: Property<Boolean>
}

internal abstract class DetektWorker @Inject internal constructor(
  private val execOperations: ExecOperations,
) : WorkAction<DetektParameters> {
  override fun execute() {
    parameters.baselineFilePath.orNull?.let { file ->
      if (!File(file).exists()) {
        executeDetekt(shouldCreateBaseLine = true)
        throw WorkerExecutionException("Aborting build since new baseline file was created")
      }
    }

    executeDetekt()
  }

  private fun executeDetekt(shouldCreateBaseLine: Boolean = false) {
    val reportKey = "--report"
    val reportValue = listOf(
      ReportingMetaInformation("plain", "txt", "plain"),
      ReportingMetaInformation("xml", "xml", "checkstyle"),
      ReportingMetaInformation("html", "html", "report"),
    ).joinToString(separator = ",") {
      it.fileEnding + ":" + File(parameters.outputDirectory.asFile.get(), "detekt-${it.fileNameSuffix}.${it.fileEnding}").absolutePath
    }

    execOperations.javaexec { task ->
      task.mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
      task.classpath = parameters.classpath
      task.args(
        "--input",
        parameters.inputFile.get().asFile,
        reportKey,
        reportValue,
      )

      val configFile = parameters.configFile.asFile.get()
      if (configFile.exists()) {
        task.args("--config", configFile)
      }

      task.args("--excludes", "**/build/**")

      if (parameters.failFast.get()) {
        task.args("--fail-fast")
      }

      if (parameters.buildUponDefaultConfig.get()) {
        task.args("--build-upon-default-config")
      }

      if (parameters.parallel.get()) {
        task.args("--parallel")
      }

      if (shouldCreateBaseLine) {
        task.args("--create-baseline")
      }

      parameters.baselineFilePath.orNull?.let {
        task.args("--baseline", it)
      }
    }
  }
}
