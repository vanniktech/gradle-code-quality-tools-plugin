package com.vanniktech.code.quality.tools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.tasks.JavaExec

class CodeQualityToolsPlugin implements Plugin<Project> {
  private static final String GROUP_VERIFICATION = 'verification'
  private static final String GROUP_FORMATTING = 'formatting'

  @Override void apply(final Project rootProject) {
    rootProject.extensions.create('codeQualityTools', CodeQualityToolsPluginExtension)
    rootProject.codeQualityTools.extensions.create('findbugs', CodeQualityToolsPluginExtension.Findbugs)
    rootProject.codeQualityTools.extensions.create('checkstyle', CodeQualityToolsPluginExtension.Checkstyle)
    rootProject.codeQualityTools.extensions.create('pmd', CodeQualityToolsPluginExtension.Pmd)
    rootProject.codeQualityTools.extensions.create('lint', CodeQualityToolsPluginExtension.Lint)
    rootProject.codeQualityTools.extensions.create('ktlint', CodeQualityToolsPluginExtension.Ktlint)
    rootProject.codeQualityTools.extensions.create('detekt', DetektExtension)
    rootProject.codeQualityTools.extensions.create('cpd', CodeQualityToolsPluginExtension.Cpd)
    rootProject.codeQualityTools.extensions.create('errorProne', CodeQualityToolsPluginExtension.ErrorProne)

    def extension = rootProject.codeQualityTools
    def hasSubProjects = rootProject.subprojects.size() > 0

    if (hasSubProjects) {
      rootProject.subprojects { subProject ->
        afterEvaluate {
          addCodeQualityTools(subProject, rootProject, extension)
        }
      }
    } else {
      rootProject.afterEvaluate {
        addCodeQualityTools(rootProject, rootProject, extension)
      }
    }
  }

  private static void addCodeQualityTools(final Project project, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    addPmd(project, rootProject, extension)
    addCheckstyle(project, rootProject, extension)
    addKtlint(project, rootProject, extension)
    addCpd(project, extension)
    addDetekt(project, rootProject, extension)
    addErrorProne(project, extension)

    // Those static code tools take the longest hence we'll add them at the end.
    addLint(project, extension)
    addFindbugs(project, rootProject, extension)
  }

  protected static boolean addPmd(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.pmd.enabled
    def isPmdSupported = isJavaProject(subProject) || isAndroidProject(subProject)

    if (isNotIgnored && isEnabled && isPmdSupported) {
      subProject.plugins.apply('pmd')

      subProject.pmd {
        toolVersion = extension.pmd.toolVersion
        ignoreFailures = extension.pmd.ignoreFailures != null ? extension.pmd.ignoreFailures : !extension.failEarly
        ruleSetFiles = subProject.files(rootProject.file(extension.pmd.ruleSetFile))
      }

      subProject.task('pmd', type: Pmd) {
        description = 'Runs pmd.'
        group = GROUP_VERIFICATION

        ruleSets = []

        source = subProject.fileTree(extension.pmd.source)
        include extension.pmd.include
        exclude extension.pmd.exclude

        reports {
          html.enabled = extension.htmlReports
          xml.enabled = extension.xmlReports
        }
      }

      subProject.check.dependsOn 'pmd'

      return true
    }

    return false
  }

  protected static boolean addCheckstyle(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.checkstyle.enabled
    def isCheckstyleSupported = isJavaProject(subProject) || isAndroidProject(subProject)

    if (isNotIgnored && isEnabled && isCheckstyleSupported) {
      subProject.plugins.apply('checkstyle')

      subProject.checkstyle {
        toolVersion = extension.checkstyle.toolVersion
        configFile rootProject.file(extension.checkstyle.configFile)
        ignoreFailures = extension.checkstyle.ignoreFailures != null ? extension.checkstyle.ignoreFailures : !extension.failEarly
        showViolations extension.checkstyle.showViolations != null ? extension.checkstyle.showViolations : extension.failEarly
      }

      subProject.task('checkstyle', type: Checkstyle) {
        description = 'Runs checkstyle.'
        group = GROUP_VERIFICATION

        source = subProject.fileTree(extension.checkstyle.source)
        include extension.checkstyle.include
        exclude extension.checkstyle.exclude

        classpath = subProject.files()

        reports {
          html.enabled = extension.htmlReports
          xml.enabled = extension.xmlReports
        }
      }

      subProject.check.dependsOn 'checkstyle'

      return true
    }

    return false
  }

  protected static boolean addFindbugs(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.findbugs.enabled
    def isFindbugsSupported = isJavaProject(subProject) || isAndroidProject(subProject) || isKotlinProject(subProject)

    if (isNotIgnored && isEnabled && isFindbugsSupported) {
      def buildDirIncludes = new ArrayList()

      if (isAndroidProject(subProject)) {
        buildDirIncludes.add("intermediates/classes/debug/**")
      } else {
        if (isKotlinProject(subProject)) {
          buildDirIncludes.add("classes/kotlin/main/**")
        }

        if (isJavaProject(subProject)) {
          buildDirIncludes.add("classes/java/main/**")
        }
      }

      subProject.plugins.apply('findbugs')

      subProject.findbugs {
        sourceSets = []
        ignoreFailures = extension.findbugs.ignoreFailures != null ? extension.findbugs.ignoreFailures : !extension.failEarly
        toolVersion = extension.findbugs.toolVersion
        effort = extension.findbugs.effort
        reportLevel = extension.findbugs.reportLevel
        excludeFilter = rootProject.file(extension.findbugs.excludeFilter)
      }

      subProject.task('findbugs', type: FindBugs, dependsOn: 'assemble') {
        description = 'Runs findbugs.'
        group = GROUP_VERIFICATION

        classes = subProject.fileTree(subProject.buildDir).include(buildDirIncludes)
        source = subProject.fileTree(extension.findbugs.source)
        classpath = subProject.files()

        reports {
          html.enabled = extension.htmlReports
          xml.enabled = extension.xmlReports
        }
      }

      subProject.check.dependsOn 'findbugs'

      return true
    }

    return false
  }

  protected static boolean addLint(final Project subProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.lint.enabled
    def isLintSupported = isAndroidProject(subProject)

    if (isNotIgnored && isEnabled && isLintSupported) {
      subProject.android.lintOptions {
        warningsAsErrors extension.lint.warningsAsErrors != null ? extension.lint.warningsAsErrors : extension.failEarly
        abortOnError extension.lint.abortOnError != null ? extension.lint.abortOnError : extension.failEarly
      }

      if (extension.lint.checkAllWarnings != null) {
        subProject.android.lintOptions {
          checkAllWarnings = extension.lint.checkAllWarnings
        }
      }

      if (extension.lint.absolutePaths != null) {
        subProject.android.lintOptions {
          absolutePaths = extension.lint.absolutePaths
        }
      }

      if (extension.lint.baselineFileName != null) {
        subProject.android.lintOptions {
          baseline subProject.file(extension.lint.baselineFileName)
        }
      }

      if (extension.lint.lintConfig != null) {
        subProject.android.lintOptions {
          lintConfig extension.lint.lintConfig
        }
      }

      if (extension.lint.checkReleaseBuilds != null) {
        subProject.android.lintOptions {
          checkReleaseBuilds extension.lint.checkReleaseBuilds
        }
      }

      if (extension.lint.textReport != null) {
        subProject.android.lintOptions {
          textReport extension.lint.textReport
          textOutput extension.lint.textOutput
        }
      }

      subProject.check.dependsOn 'lint'

      return true
    }

    return false
  }

  protected static boolean addKtlint(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.ktlint.enabled
    def isKtlintSupported = isKotlinProject(subProject)

    if (isNotIgnored && isEnabled && isKtlintSupported) {
      subProject.configurations {
        ktlint
      }

      subProject.dependencies {
        ktlint "com.github.shyiko:ktlint:${extension.ktlint.toolVersion}"
      }

      def outputDir = "${subProject.buildDir}/reports/ktlint/"
      def configurationFiles = rootProject.fileTree(dir: ".", include: "**/.editorconfig")
      def inputFiles = subProject.fileTree(dir: "src", include: "**/*.kt")

      subProject.task('ktlint', type: JavaExec) {
        inputs.files(inputFiles, configurationFiles)
        outputs.dir(outputDir)
        group = GROUP_VERIFICATION
        description = 'Runs ktlint.'
        main = 'com.github.shyiko.ktlint.Main'
        classpath = subProject.configurations.ktlint
        def outputFile = "${outputDir}ktlint-checkstyle-report.xml"
        args '--reporter=plain', "--reporter=checkstyle,output=${outputFile}", 'src/**/*.kt'
      }

      subProject.task('ktlintFormat', type: JavaExec) {
        inputs.files(inputFiles, configurationFiles)
        outputs.upToDateWhen { true } // We only need the input as it'll change when we reformat.
        group = GROUP_FORMATTING
        description = "Runs ktlint and autoformats your code."
        main = "com.github.shyiko.ktlint.Main"
        classpath = subProject.configurations.ktlint
        args "-F", "src/**/*.kt"
      }

      subProject.check.dependsOn 'ktlint'

      return true
    }

    return false
  }

  protected static boolean addCpd(final Project subProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.cpd.enabled
    def isCpdSupported = isJavaProject(subProject) || isAndroidProject(subProject)

    if (isNotIgnored && isEnabled && isCpdSupported) {
      subProject.plugins.apply('cpd')

      subProject.cpd {
        language = extension.cpd.language
        toolVersion = extension.pmd.toolVersion
      }

      subProject.cpdCheck {
        description = 'Runs cpd.'
        group = GROUP_VERIFICATION

        reports {
          xml.enabled = extension.xmlReports
          text.enabled = extension.textReports
        }
        encoding = 'UTF-8'
        source = subProject.fileTree(extension.cpd.source).filter { it.name.endsWith(".${extension.cpd.language}") }
        minimumTokenCount = extension.cpd.minimumTokenCount
        ignoreFailures = extension.cpd.ignoreFailures != null ? extension.cpd.ignoreFailures : !extension.failEarly
      }

      subProject.check.dependsOn 'cpdCheck'

      return true
    }

    return false
  }

  protected static boolean addDetekt(final Project project, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(project, extension)
    def isEnabled = extension.detekt.enabled
    def isDetektSupported = isKotlinProject(project)

    if (isNotIgnored && isEnabled && isDetektSupported) {
      def task = project.tasks.create("detektCheck", DetektCheckTask)
      task.version = extension.detekt.toolVersion
      task.group = GROUP_VERIFICATION
      task.description = "Runs detekt."
      task.outputDirectory = new File(project.buildDir, "reports/detekt/")
      task.configFile = rootProject.file(extension.detekt.config)
      task.inputs.files(project.fileTree(dir: ".", include: "**/*.kt"))

      task.inputs.property("baseline-file-exists", false)

      if (extension.detekt.baselineFileName != null) {
        def file = project.file(extension.detekt.baselineFileName)
        task.baselineFilePath = file.toString()
        task.inputs.property("baseline-file-exists", file.exists())
      }

      project.check.dependsOn 'detektCheck'

      return true
    }

    return false
  }

  protected static boolean addErrorProne(final Project project, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(project, extension)
    def isEnabled = extension.errorProne.enabled
    def isErrorProneSupported = isJavaProject(project) || isAndroidProject(project)

    if (isNotIgnored && isEnabled && isErrorProneSupported) {
      project.plugins.apply('net.ltgt.errorprone')
      project.configurations.errorprone {
        resolutionStrategy.force "com.google.errorprone:error_prone_core:${extension.errorProne.toolVersion}"
      }

      return true
    }

    return false
  }

  private static boolean isKotlinProject(final Project project) {
    final boolean isKotlin = project.plugins.hasPlugin('kotlin')
    final boolean isKotlinAndroid = project.plugins.hasPlugin('kotlin-android')
    final boolean isKotlinPlatformCommon = project.plugins.hasPlugin('kotlin-platform-common')
    final boolean isKotlinPlatformJvm = project.plugins.hasPlugin('kotlin-platform-jvm')
    final boolean isKotlinPlatformJs = project.plugins.hasPlugin('kotlin-platform-js')
    return isKotlin || isKotlinAndroid || isKotlinPlatformCommon || isKotlinPlatformJvm || isKotlinPlatformJs
  }

  private static boolean isJavaProject(final Project project) {
    final boolean isJava = project.plugins.hasPlugin('java')
    final boolean isJavaLibrary = project.plugins.hasPlugin('java-library')
    final boolean isJavaGradlePlugin = project.plugins.hasPlugin('java-gradle-plugin')
    return isJava || isJavaLibrary || isJavaGradlePlugin
  }

  private static boolean isAndroidProject(final Project project) {
    final boolean isAndroidLibrary = project.plugins.hasPlugin('com.android.library')
    final boolean isAndroidApp = project.plugins.hasPlugin('com.android.application')
    final boolean isAndroidTest = project.plugins.hasPlugin('com.android.test')
    final boolean isAndroidFeature = project.plugins.hasPlugin('com.android.feature')
    final boolean isAndroidInstantApp = project.plugins.hasPlugin('com.android.instantapp')
    return isAndroidLibrary || isAndroidApp || isAndroidTest || isAndroidFeature || isAndroidInstantApp
  }

  private static boolean shouldIgnore(final Project project, final CodeQualityToolsPluginExtension extension) {
    return extension.ignoreProjects?.contains(project.name)
  }
}
