package com.vanniktech.code.quality.tools

import com.vanniktech.code.quality.tools.KtlintExtension.Companion.PINTEREST_VERSION_CHANGE
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask open class KtLintFormatTask : DefaultTask() {
  @Input var experimental: Boolean = false
  @Input lateinit var version: String
  @OutputDirectory lateinit var outputDirectory: File

  init {
    group = "formatting"
    description = "Runs ktlint and autoformats your code."
  }

  @TaskAction fun run() {
    project.javaexec { task ->
      task.main = if (version.asVersion() >= PINTEREST_VERSION_CHANGE) "com.pinterest.ktlint.Main" else "com.github.shyiko.ktlint.Main"
      task.classpath = project.configurations.getByName("ktlint")
      if (experimental) {
        task.args("--experimental")
      }
      task.args("--reporter=plain", "--reporter=checkstyle,output=${File(outputDirectory, "ktlint-checkstyle-report.xml")}", "-F", "**/*.kt", "**/*.kts", "!build/")
    }
  }
}
