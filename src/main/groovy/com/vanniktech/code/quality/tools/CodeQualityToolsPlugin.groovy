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

    rootProject.subprojects { subProject ->
      addGradlePlugins(subProject, extension)

      afterEvaluate {
        addCodeQualityTools(subProject, rootProject, extension)
      }
    }
  }

  private static void addGradlePlugins(final Project project, final CodeQualityToolsPluginExtension extension) {
    addErrorProneGradlePlugin(project, extension)
    addDetektGradlePlugin(project, extension)
    addCpdGradlePlugin(project, extension)
  }

  private static void addCodeQualityTools(final Project project, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    if (!shouldIgnore(project, extension)) {
      // Reason for checking again in each add method: Unit Tests (they can't handle afterEvaluate properly)
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
  }

  private static void addCpdGradlePlugin(final Project subProject, final CodeQualityToolsPluginExtension extension) {
    if (extension.cpd.enabled) {
      def cpdGradlePluginVersion = subProject.findProperty('codeQualityTools.cpd.gradlePluginVersion') ?: '1.0'

      subProject.buildscript.dependencies {
        classpath "de.aaschmid:gradle-cpd-plugin:$cpdGradlePluginVersion"
      }
    }
  }

  private static void addDetektGradlePlugin(final Project subProject, final CodeQualityToolsPluginExtension extension) {
    if (extension.detekt.enabled) {
      def detektGradlePluginVersion = subProject.findProperty('codeQualityTools.detekt.gradlePluginVersion') ?: '1.0.0.M13.2'

      subProject.buildscript {
        repositories {
          maven { url "https://plugins.gradle.org/m2/" }
        }
        dependencies {
          classpath "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektGradlePluginVersion"
        }
      }
    }
  }

  private static void addErrorProneGradlePlugin(final Project project, final CodeQualityToolsPluginExtension extension) {
    if (extension.errorProne.enabled) {
      def errorProneGradlePluginVersion = project.findProperty('codeQualityTools.errorProne.gradlePluginVersion') ?: '0.0.10'

      project.buildscript {
        repositories {
          maven { url "https://plugins.gradle.org/m2/" }
        }
        dependencies {
          classpath "net.ltgt.gradle:gradle-errorprone-plugin:$errorProneGradlePluginVersion"
        }
      }
    }
  }

  protected static boolean addPmd(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    if (!shouldIgnore(subProject, extension) && extension.pmd.enabled) {
      subProject.plugins.apply('pmd')

      subProject.pmd {
        toolVersion = extension.pmd.toolVersion
        ignoreFailures = extension.pmd.ignoreFailures != null ? extension.pmd.ignoreFailures : !extension.failEarly
        ruleSetFiles = subProject.files(rootProject.file(extension.pmd.ruleSetFile))
      }

      subProject.task('pmd', type: Pmd) {
        description = 'Run pmd'
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
    if (!shouldIgnore(subProject, extension) && extension.checkstyle.enabled) {
      subProject.plugins.apply('checkstyle')

      subProject.checkstyle {
        toolVersion = extension.checkstyle.toolVersion
        configFile rootProject.file(extension.checkstyle.configFile)
        ignoreFailures = extension.checkstyle.ignoreFailures != null ? extension.checkstyle.ignoreFailures : !extension.failEarly
        showViolations extension.checkstyle.showViolations != null ? extension.checkstyle.showViolations : extension.failEarly
      }

      subProject.task('checkstyle', type: Checkstyle) {
        description = 'Run checkstyle'
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
    if (!shouldIgnore(subProject, extension) && extension.findbugs.enabled) {
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
        description = 'Run findbugs'
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
    if (!shouldIgnore(subProject, extension) && extension.lint.enabled && isAndroidProject(subProject)) {
      subProject.android.lintOptions {
        warningsAsErrors extension.lint.warningsAsErrors != null ? extension.lint.warningsAsErrors : extension.failEarly
        abortOnError extension.lint.abortOnError != null ? extension.lint.abortOnError : extension.failEarly
      }

      if (extension.lint.checkAllWarnings != null) {
        subProject.android.lintOptions {
          checkAllWarnings = extension.lint.checkAllWarnings
        }
      }

      if (extension.lint.baselineFileName != null) {
        subProject.android.lintOptions {
          baseline subProject.file(extension.lint.baselineFileName)
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
    if (!shouldIgnore(subProject, extension) && extension.ktlint.enabled) {
      subProject.configurations {
        ktlint
      }

      subProject.dependencies {
        ktlint "com.github.shyiko:ktlint:${extension.ktlint.toolVersion}"
      }

      subProject.task('ktlint', type: JavaExec) {
        group = 'verification'
        description = 'Check Kotlin code style.'
        main = 'com.github.shyiko.ktlint.Main'
        classpath = subProject.configurations.ktlint
        def outputFile = "${subProject.buildDir}/reports/ktlint/ktlint-checkstyle-report.xml"
        args '--reporter=plain', "--reporter=checkstyle,output=${outputFile}", 'src/**/*.kt'
      }

      subProject.task('ktlintFormat', type: JavaExec) {
        group = 'formatting'
        description = "Fix Kotlin code style deviations."
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
    if (!shouldIgnore(subProject, extension) && extension.cpd.enabled) {
      subProject.plugins.apply('cpd')

      subProject.cpd {
        language = extension.cpd.language
        toolVersion = extension.cpd.toolVersion
      }

      subProject.cpdCheck {
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
    if (!shouldIgnore(project, extension) && extension.detekt.enabled) {
      project.plugins.apply('io.gitlab.arturbosch.detekt')
      project.detekt {
        version = extension.detekt.toolVersion
        profile("main") {
          input = "${project.file('.')}"
          config = rootProject.file(extension.detekt.config)
        }
      }

      project.check.dependsOn 'detektCheck'

      return true
    }

    return false
  }

  protected static boolean addErrorProne(final Project project, final CodeQualityToolsPluginExtension extension) {
    if (!shouldIgnore(project, extension) && extension.errorProne.enabled) {
      project.plugins.apply('net.ltgt.errorprone')
      project.configurations.errorprone {
        resolutionStrategy.force "com.google.errorprone:error_prone_core:${extension.errorProne.toolVersion}"
      }

      return true
    }

    return false
  }

  protected static boolean isAndroidProject(final Project project) {
    final boolean isAndroidLibrary = project.plugins.hasPlugin('com.android.library')
    final boolean isAndroidApp = project.plugins.hasPlugin('com.android.application')
    return isAndroidLibrary || isAndroidApp
  }

  private static boolean shouldIgnore(final Project project, final CodeQualityToolsPluginExtension extension) {
    return extension.ignoreProjects?.contains(project.name)
  }
}
