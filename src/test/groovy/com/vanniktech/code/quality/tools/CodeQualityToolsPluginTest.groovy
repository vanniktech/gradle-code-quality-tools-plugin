package com.vanniktech.code.quality.tools

import org.gradle.api.Project
import org.gradle.api.plugins.quality.*
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

public class CodeQualityToolsPluginTest {
    private Project rootProject
    private Project javaProject
    private Project androidAppProject
    private Project androidLibraryProject

    private Project[] projects

    @Before
    public void setUp() {
        rootProject = ProjectBuilder.builder().withName('root').build()

        javaProject = ProjectBuilder.builder().withName('java').withParent(rootProject).build()
        javaProject.plugins.apply('java')

        androidAppProject = ProjectBuilder.builder().withName('android app').build()
        androidAppProject.plugins.apply('com.android.application')

        androidLibraryProject = ProjectBuilder.builder().withName('android library').build()
        androidLibraryProject.plugins.apply('com.android.library')

        projects = [javaProject, androidAppProject, androidLibraryProject]
    }

    @Test
    public void testAddFindbugsJavaDefault() {
        CodeQualityToolsPlugin.addFindbugs(javaProject, rootProject, new CodeQualityToolsPluginExtension())

        assertFindbugs(javaProject, "/build/classes/main")
    }

    @Test
    public void testAddFindbugsAndroidAppDefault() {
        CodeQualityToolsPlugin.addFindbugs(androidAppProject, rootProject, new CodeQualityToolsPluginExtension())

        assertFindbugs(androidAppProject, "/build/intermediates/classes/debug")
    }

    @Test
    public void testAddFindbugsAndroidLibraryDefault() {
        CodeQualityToolsPlugin.addFindbugs(androidLibraryProject, rootProject, new CodeQualityToolsPluginExtension())

        assertFindbugs(androidLibraryProject, "/build/intermediates/classes/debug")
    }

    private void assertFindbugs(Project project, String classesPath) {
        assert project.plugins.hasPlugin(FindBugsPlugin)

        assert !project.findbugs.ignoreFailures
        assert project.findbugs.toolVersion == '3.0.1'
        assert project.findbugs.effort == 'max'
        assert project.findbugs.reportLevel == 'low'
        assert project.findbugs.excludeFilter == rootProject.file('code_quality_tools/findbugs-filter.xml')

        def task = project.tasks.findByName('findbugs')

        assert task instanceof FindBugs

        task.with {
            assert description == 'Run findbugs'
            assert group == 'verification'

            assert classesPath == classes.dir.absolutePath.replace(project.projectDir.absolutePath, '')

            assert excludeFilter == rootProject.file('code_quality_tools/findbugs-filter.xml')

            assert reports.xml.enabled
            assert !reports.html.enabled
        }
    }

    @Test
    public void testAddCheckstyleJavaDefault() {
        CodeQualityToolsPlugin.addCheckstyle(javaProject, rootProject, new CodeQualityToolsPluginExtension())

        assertCheckstyle(javaProject)
    }

    @Test
    public void testAddCheckstyleAndroidAppDefault() {
        CodeQualityToolsPlugin.addCheckstyle(androidAppProject, rootProject, new CodeQualityToolsPluginExtension())

        assertCheckstyle(androidAppProject)
    }

    @Test
    public void testAddCheckstyleAndroidLibraryDefault() {
        CodeQualityToolsPlugin.addCheckstyle(androidLibraryProject, rootProject, new CodeQualityToolsPluginExtension())

        assertCheckstyle(androidLibraryProject)
    }

    private void assertCheckstyle(Project project) {
        assert project.plugins.hasPlugin(CheckstylePlugin)

        assert !project.checkstyle.ignoreFailures
        assert project.checkstyle.showViolations
        assert project.checkstyle.toolVersion == '6.14.1'
        assert project.checkstyle.configFile == rootProject.file("code_quality_tools/checkstyle.xml")

        def task = project.tasks.findByName('checkstyle')

        assert task instanceof Checkstyle

        task.with {
            assert description == 'Run checkstyle'
            assert group == 'verification'

            assert configFile == rootProject.file("code_quality_tools/checkstyle.xml")

            assert includes.size() == 1
            assert includes.contains('**/*.java')

            assert excludes.size() == 1
            assert excludes.contains('**/gen/**')

            assert reports.xml.enabled
            assert !reports.html.enabled
        }
    }

    @Test
    public void testAddPmdJavaDefault() {
        CodeQualityToolsPlugin.addPmd(javaProject, rootProject, new CodeQualityToolsPluginExtension())

        assertPmd(javaProject)
    }

    @Test
    public void testAddPmdAndroidAppDefault() {
        CodeQualityToolsPlugin.addPmd(androidAppProject, rootProject, new CodeQualityToolsPluginExtension())

        assertPmd(androidAppProject)
    }

    @Test
    public void testAddPmdAndroidLibraryDefault() {
        CodeQualityToolsPlugin.addPmd(androidLibraryProject, rootProject, new CodeQualityToolsPluginExtension())

        assertPmd(androidLibraryProject)
    }

    private void assertPmd(Project project) {
        assert project.plugins.hasPlugin(PmdPlugin)

        assert !project.pmd.ignoreFailures
        assert project.pmd.toolVersion == '5.4.1'
        assert project.pmd.ruleSetFiles.singleFile == rootProject.file("code_quality_tools/pmd.xml")

        def task = project.tasks.findByName('pmd')

        assert task instanceof Pmd

        task.with {
            assert description == 'Run pmd'
            assert group == 'verification'

            assert ruleSetFiles.singleFile == rootProject.file("code_quality_tools/pmd.xml")
            assert ruleSets.empty

            assert includes.size() == 1
            assert includes.contains('**/*.java')

            assert excludes.size() == 1
            assert excludes.contains('**/gen/**')

            assert reports.xml.enabled
            assert !reports.html.enabled
        }
    }

    @Test
    public void testFailEarly() {
        def extension = new CodeQualityToolsPluginExtension()
        extension.failEarly = false

        for (def project : projects) {
            CodeQualityToolsPlugin.addFindbugs(project, rootProject, extension)
            CodeQualityToolsPlugin.addCheckstyle(project, rootProject, extension)
            CodeQualityToolsPlugin.addPmd(project, rootProject, extension)

            assert project.findbugs.ignoreFailures
            assert project.checkstyle.ignoreFailures
            assert !project.checkstyle.showViolations
            assert project.pmd.ignoreFailures
        }
    }

    @Test
    public void testReports() {
        def extension = new CodeQualityToolsPluginExtension()
        extension.htmlReports = true
        extension.xmlReports = false

        for (def project : projects) {
            CodeQualityToolsPlugin.addFindbugs(project, rootProject, extension)
            CodeQualityToolsPlugin.addCheckstyle(project, rootProject, extension)
            CodeQualityToolsPlugin.addPmd(project, rootProject, extension)

            def findbugsTask = project.tasks.findByName('findbugs')

            findbugsTask.with {
                assert reports.xml.enabled == extension.xmlReports
                assert reports.html.enabled == extension.htmlReports
            }

            def checkstyleTask = project.tasks.findByName('checkstyle')

            checkstyleTask.with {
                assert reports.xml.enabled == extension.xmlReports
                assert reports.html.enabled == extension.htmlReports
            }

            def pmdTask = project.tasks.findByName('pmd')

            pmdTask.with {
                assert reports.xml.enabled == extension.xmlReports
                assert reports.html.enabled == extension.htmlReports
            }
        }
    }

    @Test
    public void testToolVersions() {
        def extension = new CodeQualityToolsPluginExtension()
        extension.findbugs.toolVersion = '3.0.0'
        extension.checkstyle.toolVersion = '6.14.0'
        extension.pmd.toolVersion = '5.4.0'

        for (def project : projects) {
            CodeQualityToolsPlugin.addFindbugs(project, rootProject, extension)
            CodeQualityToolsPlugin.addCheckstyle(project, rootProject, extension)
            CodeQualityToolsPlugin.addPmd(project, rootProject, extension)

            assert project.findbugs.toolVersion == extension.findbugs.toolVersion
            assert project.checkstyle.toolVersion == extension.checkstyle.toolVersion
            assert project.pmd.toolVersion == extension.pmd.toolVersion
        }
    }

    @Test
    public void testConfigFiles() {
        def extension = new CodeQualityToolsPluginExtension()
        extension.findbugs.excludeFilter = 'findbugs.xml'
        extension.checkstyle.configFile = 'checkstyle.xml'
        extension.pmd.ruleSetFile = 'pmd.xml'

        for (def project : projects) {
            CodeQualityToolsPlugin.addFindbugs(project, rootProject, extension)
            CodeQualityToolsPlugin.addCheckstyle(project, rootProject, extension)
            CodeQualityToolsPlugin.addPmd(project, rootProject, extension)

            assert project.findbugs.excludeFilter == rootProject.file(extension.findbugs.excludeFilter)
            assert project.checkstyle.configFile == rootProject.file(extension.checkstyle.configFile)
            assert project.pmd.ruleSetFiles.singleFile == rootProject.file(extension.pmd.ruleSetFile)
        }
    }

    @Test
    public void testIgnoreProjects() {
        def extension = new CodeQualityToolsPluginExtension()

        for (def project : projects) {
            extension.ignoreProjects = [project.name]

            CodeQualityToolsPlugin.addFindbugs(project, rootProject, extension)
            CodeQualityToolsPlugin.addCheckstyle(project, rootProject, extension)
            CodeQualityToolsPlugin.addPmd(project, rootProject, extension)

            assert !project.plugins.hasPlugin(FindBugsPlugin)
            assert !project.plugins.hasPlugin(CheckstylePlugin)
            assert !project.plugins.hasPlugin(PmdPlugin)
        }
    }
}
