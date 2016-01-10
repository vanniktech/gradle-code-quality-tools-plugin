package com.vanniktech.code.quality.tools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.Pmd

class CodeQualityToolsPlugin implements Plugin<Project> {
    @Override
    void apply(final Project rootProject) {
        rootProject.subprojects { subProject ->
            subProject.plugins.apply('pmd')
            subProject.pmd {
                toolVersion = '5.4.1'
                ignoreFailures = false
            }

            subProject.task("pmd", type: Pmd) {
                description = 'Run pmd'
                group = 'verification'

                ruleSetFiles = project.files(rootProject.file("code_quality_tools/pmd.xml"))
                ruleSets = []

                source = fileTree('src/main/java')
                include '**/*.java'
                exclude '**/gen/**'
            }

            subProject.plugins.apply('checkstyle')
            subProject.checkstyle {
                toolVersion = '6.14.1'
                configFile rootProject.file('code_quality_tools/checkstyle.xml')
                ignoreFailures false
                showViolations true
            }

            subProject.task("checkstyle", type: Checkstyle) {
                description = 'Run checkstyle'
                group = 'verification'

                source 'src'
                include '**/*.java'
                exclude '**/gen/**'

                classpath = files()
            }

            afterEvaluate {
                final boolean isAndroidLibrary = subProject.plugins.hasPlugin('com.android.library')
                final boolean isAndroidApp = subProject.plugins.hasPlugin('com.android.application')
                final boolean isAndroidPlugin = isAndroidLibrary || isAndroidApp

                final String findbugsClassesPath = isAndroidPlugin ? 'build/intermediates/classes/debug/' : 'build/classes/main/'

                subProject.plugins.apply('findbugs')

                subProject.findbugs {
                    sourceSets = []
                    ignoreFailures = false
                    toolVersion = '3.0.1'
                    effort = 'max'
                    reportLevel = 'low'
                    excludeFilter = rootProject.file("code_quality_tools/findbugs-filter.xml")
                }

                subProject.task("findbugs", type: FindBugs) {
                    description = 'Run findbugs'
                    group = 'verification'

                    classes = subProject.fileTree(findbugsClassesPath)
                    source = subProject.fileTree('src')
                    classpath = subProject.files()
                }

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
