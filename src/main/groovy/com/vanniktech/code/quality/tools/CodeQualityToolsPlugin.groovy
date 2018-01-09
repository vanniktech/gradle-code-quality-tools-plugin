package com.vanniktech.code.quality.tools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.tasks.JavaExec

class CodeQualityToolsPlugin implements Plugin<Project> {
  @Override void apply(final Project rootProject) {
    rootProject.extensions.create('codeQualityTools', CodeQualityToolsPluginExtension)
    rootProject.codeQualityTools.extensions.create('findbugs', CodeQualityToolsPluginExtension.Findbugs)
    rootProject.codeQualityTools.extensions.create('checkstyle', CodeQualityToolsPluginExtension.Checkstyle)
    rootProject.codeQualityTools.extensions.create('pmd', CodeQualityToolsPluginExtension.Pmd)
    rootProject.codeQualityTools.extensions.create('lint', CodeQualityToolsPluginExtension.Lint)
    rootProject.codeQualityTools.extensions.create('ktlint', CodeQualityToolsPluginExtension.Ktlint)
    rootProject.codeQualityTools.extensions.create('detekt', CodeQualityToolsPluginExtension.Detekt)
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
    addKtlint(project, extension)
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
        group = 'verification'

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
        group = 'verification'

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
      final String findbugsClassesPath = isAndroidProject(subProject) ? 'build/intermediates/classes/debug/' : 'build/classes/java/main/'

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
        group = 'verification'

        classes = subProject.fileTree(findbugsClassesPath)
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

  protected static boolean addKtlint(final Project subProject, final CodeQualityToolsPluginExtension extension) {
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

      subProject.task('ktlint', type: JavaExec) {
        group = 'verification'
        description = 'Runs ktlint.'
        main = 'com.github.shyiko.ktlint.Main'
        classpath = subProject.configurations.ktlint
        def outputFile = "${subProject.buildDir}/reports/ktlint/ktlint-checkstyle-report.xml"
        args '--reporter=plain', "--reporter=checkstyle,output=${outputFile}", 'src/**/*.kt'
      }

      subProject.task('ktlintFormat', type: JavaExec) {
        group = 'formatting'
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
        group = 'verification'

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
      project.configurations {
        detektCheck
      }

      project.dependencies {
        detektCheck "io.gitlab.arturbosch.detekt:detekt-cli:${extension.detekt.toolVersion}"
      }

      project.task('detektCheck', type: JavaExec) {
        group = 'verification'
        description = 'Runs detekt.'
        main = 'io.gitlab.arturbosch.detekt.cli.Main'
        classpath = project.configurations.detektCheck
        args "--config", rootProject.file(extension.detekt.config), "--input", project.file('.')
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
