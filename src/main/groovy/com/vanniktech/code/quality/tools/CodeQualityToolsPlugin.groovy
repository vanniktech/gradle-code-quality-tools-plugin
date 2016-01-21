package com.vanniktech.code.quality.tools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.Pmd

class CodeQualityToolsPlugin implements Plugin<Project> {
    @Override
    void apply(final Project rootProject) {
        rootProject.extensions.create("codeQualityTools", CodeQualityToolsPluginExtension)

        rootProject.subprojects { subProject ->
            afterEvaluate {
                def extension = rootProject.codeQualityTools

                if (!shouldIgnore(subProject, extension)) { // Reason for checking again in each add method: Unit Tests (they can't handle afterEvaluate properly)
                    addPmd(subProject, rootProject, extension)
                    addCheckstyle(subProject, rootProject, extension)
                    addFindbugs(subProject, rootProject, extension)

                    tasks.findByName('pmd').dependsOn('assemble')
                    tasks.findByName('findbugs').dependsOn('assemble')

                    def checkTask = tasks.findByName('check')

                    checkTask.dependsOn('pmd')
                    checkTask.dependsOn('findbugs')
                    checkTask.dependsOn('checkstyle')
                }
            }
        }
    }

    protected static void addPmd(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
        if (!shouldIgnore(subProject, extension)) {
            subProject.plugins.apply('pmd')

            subProject.pmd {
                toolVersion = extension.pmd.toolVersion
                ignoreFailures = !extension.failEarly
                ruleSetFiles = subProject.files(rootProject.file(extension.pmd.ruleSetFile))
            }

            subProject.task("pmd", type: Pmd) {
                description = 'Run pmd'
                group = 'verification'

                ruleSets = []

                source = subProject.fileTree('src/main/java')
                include '**/*.java'
                exclude '**/gen/**'

                reports {
                    html.enabled = extension.htmlReports
                    xml.enabled = extension.xmlReports
                }
            }
        }
    }

    protected static void addCheckstyle(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
        if (!shouldIgnore(subProject, extension)) {
            subProject.plugins.apply('checkstyle')

            subProject.checkstyle {
                toolVersion = extension.checkstyle.toolVersion
                configFile rootProject.file(extension.checkstyle.configFile)
                ignoreFailures = !extension.failEarly
                showViolations extension.failEarly
            }

            subProject.task("checkstyle", type: Checkstyle) {
                description = 'Run checkstyle'
                group = 'verification'

                source 'src'
                include '**/*.java'
                exclude '**/gen/**'

                classpath = subProject.files()

                reports {
                    html.enabled = extension.htmlReports
                    xml.enabled = extension.xmlReports
                }
            }
        }
    }

    protected static void addFindbugs(final Project subProject, final Project rootProject, final CodeQualityToolsPluginExtension extension) {
        if (!shouldIgnore(subProject, extension)) {
            final String findbugsClassesPath = isAndroidProject(subProject) ? 'build/intermediates/classes/debug/' : 'build/classes/main/'

            subProject.plugins.apply('findbugs')

            subProject.findbugs {
                sourceSets = []
                ignoreFailures = !extension.failEarly
                toolVersion = extension.findbugs.toolVersion
                effort = 'max'
                reportLevel = 'low'
                excludeFilter = rootProject.file(extension.findbugs.excludeFilter)
            }

            subProject.task("findbugs", type: FindBugs) {
                description = 'Run findbugs'
                group = 'verification'

                classes = subProject.fileTree(findbugsClassesPath)
                source = subProject.fileTree('src')
                classpath = subProject.files()

                reports {
                    html.enabled = extension.htmlReports
                    xml.enabled = extension.xmlReports
                }
            }
        }
    }

    private static boolean shouldIgnore(final Project project, final CodeQualityToolsPluginExtension extension) {
        extension.ignoreProjects != null && extension.ignoreProjects.contains(project.name)
    }

    private static boolean isAndroidProject(final Project project) {
        final boolean isAndroidLibrary = project.plugins.hasPlugin('com.android.library')
        final boolean isAndroidApp = project.plugins.hasPlugin('com.android.application')
        isAndroidLibrary || isAndroidApp
    }
}
