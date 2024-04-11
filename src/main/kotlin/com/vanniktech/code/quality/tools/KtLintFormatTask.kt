package com.vanniktech.code.quality.tools

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
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
  @Input lateinit var version: String
  @get:Classpath abstract val classpath: ConfigurableFileCollection
  @OutputDirectory lateinit var outputDirectory: Provider<Directory>

  init {
    group = "formatting"
    description = "Runs ktlint and autoformats your code."
  }

  @get:Inject internal abstract val workerExecutor: WorkerExecutor

  @TaskAction fun run() {
    val queue = workerExecutor.noIsolation()

    queue.submit(KtLintFormatWorker::class.java) {
      it.classpath.from(classpath)
      it.outputDirectory.set(outputDirectory.get())
    }
  }
}

internal interface KtLintFormatParameters : WorkParameters {
  val classpath: ConfigurableFileCollection
  val outputDirectory: DirectoryProperty
}

internal abstract class KtLintFormatWorker @Inject internal constructor(
  private val execOperations: ExecOperations,
) : WorkAction<KtLintFormatParameters> {
  override fun execute() {
    execOperations.javaexec { task ->
      task.mainClass.set("com.pinterest.ktlint.Main")
      task.classpath = parameters.classpath

      // Required for JDK 16 or later.
      // https://github.com/pinterest/ktlint/issues/1195#issuecomment-1009027802
      task.jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")

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
