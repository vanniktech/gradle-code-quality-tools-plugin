@file:Suppress("Detekt.TooManyFunctions")

package com.vanniktech.code.quality.tools

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LintPlugin
import com.android.build.gradle.internal.dsl.LintOptions
import com.android.repository.Revision
import de.aaschmid.gradle.plugins.cpd.Cpd
import de.aaschmid.gradle.plugins.cpd.CpdExtension
import de.aaschmid.gradle.plugins.cpd.CpdPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.FindBugsExtension
import org.gradle.api.plugins.quality.FindBugsPlugin
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.plugins.quality.PmdPlugin
import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import java.io.File

const val GROUP_VERIFICATION = "verification"

class CodeQualityToolsPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val extension = target.extensions.create("codeQualityTools", CodeQualityToolsPluginExtension::class.java, target.objects)

    val hasSubProjects = target.subprojects.size > 0

    if (hasSubProjects) {
      target.subprojects { subProject ->
        subProject.afterEvaluate {
          addCodeQualityTools(it, target, extension)
        }
      }
    } else {
      target.afterEvaluate {
        addCodeQualityTools(it, target, extension)
      }
    }
  }

  private fun addCodeQualityTools(project: Project, rootProject: Project, extension: CodeQualityToolsPluginExtension) {
    project.addPmd(rootProject, extension)
    project.addCheckstyle(rootProject, extension)
    project.addKtlint(rootProject, extension)
    project.addCpd(extension)
    project.addDetekt(rootProject, extension)
    project.addErrorProne(extension)

    // Those static code tools take the longest hence we'll add them at the end.
    project.addLint(extension)
    project.addFindbugs(rootProject, extension)
  }
}

@Suppress("Detekt.TooGenericExceptionCaught") // Will be fixed by latest version of Detekt.
fun androidGradlePluginVersion(): Revision {
  val o = Object()

  try {
    return Revision.parseRevision(Class.forName("com.android.builder.Version").getDeclaredField("ANDROID_GRADLE_PLUGIN_VERSION").get(o).toString(), Revision.Precision.PREVIEW)
  } catch (ignored: Exception) {}

  try {
    return Revision.parseRevision(Class.forName("com.android.builder.model.Version").getDeclaredField("ANDROID_GRADLE_PLUGIN_VERSION").get(o).toString(), Revision.Precision.PREVIEW)
  } catch (ignored: Exception) {}

  throw IllegalArgumentException("Can't get Android Gradle Plugin version")
}

fun hasLintPlugin(): Boolean {
  return try {
    Class.forName("com.android.build.gradle.LintPlugin")
    true
  } catch (ignored: ClassNotFoundException) {
    false
  }
}

fun Project.kotlinFiles() = fileTree(mapOf("dir" to ".", "exclude" to "**/build/**", "includes" to listOf("**/*.kt", "**/*.kts")))

fun Project.editorconfigFiles() = fileTree(mapOf("dir" to ".", "include" to "**/.editorconfig"))

fun Project.addPmd(rootProject: Project, extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.pmd.enabled
  val isPmdSupported = isJavaProject() || isAndroidProject()

  if (isNotIgnored && isEnabled && isPmdSupported) {
    plugins.apply(PmdPlugin::class.java)

    extensions.configure(PmdExtension::class.java) {
      it.toolVersion = extension.pmd.toolVersion
      it.isIgnoreFailures = extension.pmd.ignoreFailures ?: !extension.failEarly
      it.ruleSetFiles = files(rootProject.file(extension.pmd.ruleSetFile))
    }

    tasks.register("pmd", Pmd::class.java) {
      it.description = "Runs pmd."
      it.group = GROUP_VERIFICATION

      it.ruleSets = emptyList()

      it.source = fileTree(extension.pmd.source)
      it.include(extension.pmd.include)
      it.exclude(extension.pmd.exclude)

      it.reports.html.isEnabled = extension.htmlReports
      it.reports.xml.isEnabled = extension.xmlReports
    }

    tasks.named(CHECK_TASK_NAME).configure { it.dependsOn("pmd") }
    return true
  }

  return false
}

fun Project.addCheckstyle(rootProject: Project, extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.checkstyle.enabled
  val isCheckstyleSupported = isJavaProject() || isAndroidProject()

  if (isNotIgnored && isEnabled && isCheckstyleSupported) {
    plugins.apply(CheckstylePlugin::class.java)

    extensions.configure(CheckstyleExtension::class.java) {
      it.toolVersion = extension.checkstyle.toolVersion
      it.configFile = rootProject.file(extension.checkstyle.configFile)
      it.isIgnoreFailures = extension.checkstyle.ignoreFailures ?: !extension.failEarly
      it.isShowViolations = extension.checkstyle.showViolations ?: extension.failEarly
    }

    tasks.register("checkstyle", Checkstyle::class.java) {
      it.description = "Runs checkstyle."
      it.group = GROUP_VERIFICATION

      it.source = fileTree(extension.checkstyle.source)
      it.include(extension.checkstyle.include)
      it.exclude(extension.checkstyle.exclude)

      it.classpath = files()

      it.reports.html.isEnabled = extension.htmlReports
      it.reports.xml.isEnabled = extension.xmlReports
    }

    tasks.named(CHECK_TASK_NAME).configure { it.dependsOn("checkstyle") }
    return true
  }

  return false
}

fun Project.addFindbugs(rootProject: Project, extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.findbugs.enabled
  val isFindbugsSupported = isJavaProject() || isAndroidProject() || isKotlinProject()

  if (isNotIgnored && isEnabled && isFindbugsSupported) {
    val buildDirIncludes = mutableListOf<String>()

    if (isAndroidProject()) {
      val androidGradlePluginVersion = androidGradlePluginVersion()

      if (androidGradlePluginVersion >= Revision.parseRevision("3.2.0", Revision.Precision.PREVIEW)) {
        buildDirIncludes.add("intermediates/javac/debug/**")
      } else {
        buildDirIncludes.add("intermediates/classes/debug/**")
      }
    } else {
      if (isKotlinProject()) {
        buildDirIncludes.add("classes/kotlin/main/**")
      }

      if (isJavaProject()) {
        buildDirIncludes.add("classes/java/main/**")
      }
    }

    plugins.apply(FindBugsPlugin::class.java)

    extensions.configure(FindBugsExtension::class.java) {
      it.sourceSets = emptyList()
      it.isIgnoreFailures = extension.findbugs.ignoreFailures ?: !extension.failEarly
      it.toolVersion = extension.findbugs.toolVersion
      it.effort = extension.findbugs.effort
      it.reportLevel = extension.findbugs.reportLevel
      it.excludeFilter = rootProject.file(extension.findbugs.excludeFilter)
    }

    tasks.register("findbugs", FindBugs::class.java) {
      it.description = "Runs findbugs."
      it.group = GROUP_VERIFICATION

      it.classes = fileTree(buildDir).include(buildDirIncludes) as FileTree
      it.source = fileTree(extension.findbugs.source)
      it.classpath = files()

      it.reports.html.isEnabled = extension.htmlReports
      it.reports.xml.isEnabled = extension.xmlReports
      it.dependsOn(ASSEMBLE_TASK_NAME)
    }

    tasks.named(CHECK_TASK_NAME).configure { it.dependsOn("findbugs") }
    return true
  }

  return false
}

@Suppress("Detekt.ComplexMethod") fun Project.addLint(extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.lint.enabled
  val isAndroidProject = isAndroidProject()
  val isJavaProject = isJavaProject()

  if (isNotIgnored && isEnabled) {
    val lintOptions = if (isAndroidProject) {
      extensions.getByType(BaseExtension::class.java).lintOptions
    } else if (isJavaProject && hasLintPlugin()) {
      plugins.apply(LintPlugin::class.java)
      extensions.getByType(LintOptions::class.java)
    } else {
      null
    }

    if (lintOptions != null) {
      lintOptions.isWarningsAsErrors = extension.lint.warningsAsErrors ?: extension.failEarly
      lintOptions.isAbortOnError = extension.lint.abortOnError ?: extension.failEarly

      extension.lint.checkAllWarnings?.let {
        lintOptions.isCheckAllWarnings = it
      }

      extension.lint.absolutePaths?.let {
        lintOptions.isAbsolutePaths = it
      }

      extension.lint.baselineFileName?.let {
        lintOptions.baselineFile = file(it)
      }

      extension.lint.lintConfig?.let {
        lintOptions.lintConfig = it
      }

      extension.lint.checkReleaseBuilds?.let {
        lintOptions.isCheckReleaseBuilds = it
      }

      extension.lint.checkTestSources?.let {
        lintOptions.isCheckTestSources = it
      }

      extension.lint.checkDependencies?.let {
        lintOptions.isCheckDependencies = it
      }

      extension.lint.textReport?.let {
        lintOptions.setTextReport(it)
        lintOptions.textOutput(extension.lint.textOutput)
      }

      tasks.named(CHECK_TASK_NAME).configure { it.dependsOn("lint") }
      return true
    }
  }

  return false
}

fun Project.addKtlint(rootProject: Project, extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.ktlint.enabled
  val isKtlintSupported = isKotlinProject()

  if (isNotIgnored && isEnabled && isKtlintSupported) {
    val ktlint = "ktlint"

    configurations.create(ktlint).defaultDependencies {
      it.add(dependencies.create("com.github.shyiko:ktlint:${extension.ktlint.toolVersion}"))
    }

    tasks.register(ktlint, KtLintTask::class.java) { task ->
      task.version = extension.ktlint.toolVersion
      task.outputDirectory = File(buildDir, "reports/ktlint/")
      task.inputs.files(kotlinFiles(), rootProject.editorconfigFiles())
    }

    tasks.register("ktlintFormat", KtLintFormatTask::class.java) { task ->
      task.version = extension.ktlint.toolVersion
      task.outputDirectory = File(buildDir, "reports/ktlint/")
      task.inputs.files(kotlinFiles(), rootProject.editorconfigFiles())
    }

    tasks.named(CHECK_TASK_NAME).configure { it.dependsOn(ktlint) }
    return true
  }

  return false
}

fun Project.addCpd(extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.cpd.enabled
  val isCpdSupported = isJavaProject() || isAndroidProject()

  if (isNotIgnored && isEnabled && isCpdSupported) {
    plugins.apply(CpdPlugin::class.java)

    extensions.configure(CpdExtension::class.java) {
      it.language = extension.cpd.language
      it.toolVersion = extension.pmd.toolVersion
      it.ignoreFailures = extension.cpd.ignoreFailures ?: !extension.failEarly
      it.minimumTokenCount = extension.cpd.minimumTokenCount
    }

    // CPD Plugin already creates the task so we'll just reconfigure it.
    tasks.named("cpdCheck", Cpd::class.java) {
      it.description = "Runs cpd."
      it.group = GROUP_VERIFICATION

      it.reports.text.isEnabled = extension.textReports
      it.reports.xml.isEnabled = extension.xmlReports

      it.encoding = "UTF-8"
      it.source = fileTree(extension.cpd.source).filter { it.name.endsWith(".${extension.cpd.language}") }.asFileTree
    }

    tasks.named(CHECK_TASK_NAME).configure { it.dependsOn("cpdCheck") }
    return true
  }

  return false
}

fun Project.addDetekt(rootProject: Project, extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.detekt.enabled
  val isDetektSupported = isKotlinProject()

  if (isNotIgnored && isEnabled && isDetektSupported) {
    configurations.create("detekt").defaultDependencies {
      it.add(dependencies.create("io.gitlab.arturbosch.detekt:detekt-cli:${extension.detekt.toolVersion}"))
    }

    tasks.register("detektCheck", DetektCheckTask::class.java) { task ->
      task.version = extension.detekt.toolVersion
      task.outputDirectory = File(buildDir, "reports/detekt/")
      task.configFile = rootProject.file(extension.detekt.config)
      task.inputs.files(kotlinFiles())

      task.inputs.property("baseline-file-exists", false)

      extension.detekt.baselineFileName?.let {
        val file = file(it)
        task.baselineFilePath = file.toString()
        task.inputs.property("baseline-file-exists", file.exists())
      }
    }

    tasks.named(CHECK_TASK_NAME).configure { it.dependsOn("detektCheck") }
    return true
  }

  return false
}

private fun Project.shouldIgnore(extension: CodeQualityToolsPluginExtension) = extension.ignoreProjects.contains(name)

fun Project.addErrorProne(extension: CodeQualityToolsPluginExtension): Boolean {
  val isNotIgnored = !shouldIgnore(extension)
  val isEnabled = extension.errorProne.enabled
  val isErrorProneSupported = isJavaProject() || isAndroidProject()

  if (isNotIgnored && isEnabled && isErrorProneSupported) {
    plugins.apply("net.ltgt.errorprone")
    configurations.getByName("errorprone").resolutionStrategy.force("com.google.errorprone:error_prone_core:${extension.errorProne.toolVersion}")

    return true
  }

  return false
}

private fun Project.isJavaProject(): Boolean {
  val isJava = plugins.hasPlugin("java")
  val isJavaLibrary = plugins.hasPlugin("java-library")
  val isJavaGradlePlugin = plugins.hasPlugin("java-gradle-plugin")
  return isJava || isJavaLibrary || isJavaGradlePlugin
}

private fun Project.isAndroidProject(): Boolean {
  val isAndroidLibrary = plugins.hasPlugin("com.android.library")
  val isAndroidApp = plugins.hasPlugin("com.android.application")
  val isAndroidTest = plugins.hasPlugin("com.android.test")
  val isAndroidFeature = plugins.hasPlugin("com.android.feature")
  val isAndroidInstantApp = plugins.hasPlugin("com.android.instantapp")
  return isAndroidLibrary || isAndroidApp || isAndroidTest || isAndroidFeature || isAndroidInstantApp
}

private fun Project.isKotlinProject(): Boolean {
  val isKotlin = plugins.hasPlugin("kotlin")
  val isKotlinAndroid = plugins.hasPlugin("kotlin-android")
  val isKotlinPlatformCommon = plugins.hasPlugin("kotlin-platform-common")
  val isKotlinPlatformJvm = plugins.hasPlugin("kotlin-platform-jvm")
  val isKotlinPlatformJs = plugins.hasPlugin("kotlin-platform-js")
  return isKotlin || isKotlinAndroid || isKotlinPlatformCommon || isKotlinPlatformJvm || isKotlinPlatformJs
}
