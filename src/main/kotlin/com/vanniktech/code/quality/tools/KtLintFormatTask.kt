package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

@CacheableTask abstract class KtLintFormatTask : DefaultTask() {
  @Input var experimental: Boolean = false
  @Input lateinit var version: String
  @get:Classpath abstract val classpath: ConfigurableFileCollection
  @OutputDirectory lateinit var outputDirectory: File

  init {
    group = "formatting"
    description = "Runs ktlint and autoformats your code."
  }

  @get:Inject internal abstract val workerExecutor: WorkerExecutor

  @TaskAction fun run() {
    val queue = workerExecutor.noIsolation()

    queue.submit(KtLintFormatWorker::class.java) {
      it.classpath.from(classpath)
      it.experimental.set(experimental)
      it.outputDirectory.set(outputDirectory)
    }
  }
}

internal interface KtLintFormatParameters : WorkParameters {
  val classpath: ConfigurableFileCollection
  val experimental: Property<Boolean>
  val outputDirectory: RegularFileProperty
}

internal abstract class KtLintFormatWorker @Inject internal constructor(
  private val execOperations: ExecOperations,
) : WorkAction<KtLintFormatParameters> {
  override fun execute() {
    execOperations.javaexec { task ->
      task.mainClass.set("com.pinterest.ktlint.Main")
      task.classpath = parameters.classpath

      if (parameters.experimental.get()) {
        task.args("--experimental")
      }

      task.args(
        "--reporter=plain",
        "--reporter=checkstyle,output=${File(parameters.outputDirectory.asFile.get(), "ktlint-checkstyle-report.xml")}",
        "-F",
        "**/*.kt",
        "**/*.kts",
        "!build/",
        "!build/**",
      )
    }
  }
}
