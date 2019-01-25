package com.vanniktech.code.quality.tools

import com.android.build.gradle.LintPlugin
import com.android.build.gradle.internal.dsl.LintOptions
import com.android.repository.Revision
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.Pmd

class CodeQualityToolsPlugin implements Plugin<Project> {
  private static final String GROUP_VERIFICATION = 'verification'

  @Override void apply(final Project rootProject) {
    def extension = rootProject.extensions.create('codeQualityTools', CodeQualityToolsPluginExtension)
    extension.extensions.create('findbugs', FindbugsExtension)
    extension.extensions.create('checkstyle', CheckstyleExtension)
    extension.extensions.create('pmd', PmdExtension)
    extension.extensions.create('lint', LintExtension)
    extension.extensions.create('ktlint', KtlintExtension)
    extension.extensions.create('detekt', DetektExtension)
    extension.extensions.create('cpd', CpdExtension)
    extension.extensions.create('errorProne', ErrorProneExtension)

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

      subProject.tasks.named("check").configure { it.dependsOn("pmd") }
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

      subProject.tasks.named("check").configure { it.dependsOn("checkstyle") }
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
        def androidGradlePluginVersion = androidGradlePluginVersion()

        if (androidGradlePluginVersion >= Revision.parseRevision("3.2.0", Revision.Precision.PREVIEW)) {
          buildDirIncludes.add("intermediates/javac/debug/**")
        } else {
          buildDirIncludes.add("intermediates/classes/debug/**")
        }
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

      subProject.tasks.named("check").configure { it.dependsOn("findbugs") }
      return true
    }

    return false
  }

  private static Revision androidGradlePluginVersion() {
    try {
      return Revision.parseRevision(Class.forName("com.android.builder.Version").getDeclaredField("ANDROID_GRADLE_PLUGIN_VERSION").get(this).toString(), Revision.Precision.PREVIEW)
    } catch (Exception ignored) {}

    try {
      return Revision.parseRevision(Class.forName("com.android.builder.model.Version").getDeclaredField("ANDROID_GRADLE_PLUGIN_VERSION").get(this).toString(), Revision.Precision.PREVIEW)
    } catch (Exception ignored) {}

    throw new IllegalArgumentException("Can't get Android Gradle Plugin version")
  }

  protected static boolean addLint(final Project subProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.lint.enabled
    def isAndroidProject = isAndroidProject(subProject)
    def isJavaProject = isJavaProject(subProject)

    if (isNotIgnored && isEnabled) {
      def lintOptions

      if (isAndroidProject) {
        lintOptions = subProject.android.lintOptions
      } else if (isJavaProject && hasLintPlugin()) {
        subProject.plugins.apply(LintPlugin)
        lintOptions = subProject.extensions.getByType(LintOptions)
      } else {
        lintOptions = null
      }

      if (lintOptions != null) {
        lintOptions.setWarningsAsErrors(extension.lint.warningsAsErrors != null ? extension.lint.warningsAsErrors : extension.failEarly)
        lintOptions.setAbortOnError(extension.lint.abortOnError != null ? extension.lint.abortOnError : extension.failEarly)

        if (extension.lint.checkAllWarnings != null) {
          lintOptions.setCheckAllWarnings(extension.lint.checkAllWarnings)
        }

        if (extension.lint.absolutePaths != null) {
          lintOptions.setAbsolutePaths(extension.lint.absolutePaths)
        }

        if (extension.lint.baselineFileName != null) {
          lintOptions.setBaselineFile(subProject.file(extension.lint.baselineFileName))
        }

        if (extension.lint.lintConfig != null) {
          lintOptions.setLintConfig(extension.lint.lintConfig)
        }

        if (extension.lint.checkReleaseBuilds != null) {
          lintOptions.setCheckReleaseBuilds(extension.lint.checkReleaseBuilds)
        }

        if (extension.lint.checkTestSources != null) {
          lintOptions.setCheckTestSources(extension.lint.checkTestSources)
        }

        if (extension.lint.checkDependencies != null) {
          lintOptions.setCheckDependencies(extension.lint.checkDependencies)
        }

        if (extension.lint.textReport != null) {
          lintOptions.setTextReport(extension.lint.textReport)
          lintOptions.textOutput(extension.lint.textOutput)
        }

        subProject.tasks.named("check").configure { it.dependsOn("lint") }
        return true
      }
    }

    return false
  }

  protected static boolean addKtlint(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.ktlint.enabled
    def isKtlintSupported = isKotlinProject(subProject)

    if (isNotIgnored && isEnabled && isKtlintSupported) {
      subProject.configurations.create("ktlint").defaultDependencies {
        it.add(subProject.dependencies.create("com.github.shyiko:ktlint:${extension.ktlint.toolVersion}"))
      }

      subProject.tasks.register("ktlint", KtLintTask) { task ->
        task.version = extension.ktlint.toolVersion
        task.outputDirectory = new File(subProject.buildDir, "reports/ktlint/")
        task.inputs.files(kotlinFiles(subProject), editorconfigFiles(rootProject))
      }

      subProject.tasks.register("ktlintFormat", KtLintFormatTask) { task ->
        task.version = extension.ktlint.toolVersion
        task.outputDirectory = new File(subProject.buildDir, "reports/ktlint/")
        task.inputs.files(kotlinFiles(subProject), editorconfigFiles(rootProject))
      }

      subProject.tasks.named("check").configure { it.dependsOn("ktlint") }
      return true
    }

    return false
  }

  static ConfigurableFileTree kotlinFiles(final Project project) {
    project.fileTree(dir: ".", exclude: "**/build/**", includes: ["**/*.kt", "**/*.kts"])
  }

  static ConfigurableFileTree editorconfigFiles(final Project project) {
    project.fileTree(dir: ".", include: "**/.editorconfig")
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

      subProject.tasks.named("check").configure { it.dependsOn("cpdCheck") }
      return true
    }

    return false
  }

  protected static boolean addDetekt(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
    def isNotIgnored = !shouldIgnore(subProject, extension)
    def isEnabled = extension.detekt.enabled
    def isDetektSupported = isKotlinProject(subProject)

    if (isNotIgnored && isEnabled && isDetektSupported) {
      subProject.configurations.create("detekt").defaultDependencies {
        it.add(subProject.dependencies.create("io.gitlab.arturbosch.detekt:detekt-cli:${extension.detekt.toolVersion}"))
      }

      subProject.tasks.register("detektCheck", DetektCheckTask) { task ->
        task.version = extension.detekt.toolVersion
        task.outputDirectory = new File(subProject.buildDir, "reports/detekt/")
        task.configFile = rootProject.file(extension.detekt.config)
        task.inputs.files(kotlinFiles(subProject))

        task.inputs.property("baseline-file-exists", false)

        if (extension.detekt.baselineFileName != null) {
          def file = subProject.file(extension.detekt.baselineFileName)
          task.baselineFilePath = file.toString()
          task.inputs.property("baseline-file-exists", file.exists())
        }
      }

      subProject.tasks.named("check").configure { it.dependsOn("detektCheck") }
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

  private static boolean hasLintPlugin() {
    try {
      Class.forName("com.android.build.gradle.LintPlugin")
      return true
    } catch (ClassNotFoundException ignored) {
      return false
    }
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
