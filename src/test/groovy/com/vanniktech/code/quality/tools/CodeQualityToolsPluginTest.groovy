package com.vanniktech.code.quality.tools

import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

public class CodeQualityToolsPluginTest {
    private def rootProject;
    private def javaProject;

    @Before
    public void setUp() {
        rootProject = ProjectBuilder.builder().withName('root').build();

        javaProject = ProjectBuilder.builder().withName('java').withParent(rootProject).build();
        javaProject.plugins.apply('java')
    }

    @Test
    public void testShouldApplyPMD() {
        rootProject.plugins.apply(CodeQualityToolsPlugin)

        assert javaProject.plugins.hasPlugin(PmdPlugin)

        assert !javaProject.pmd.ignoreFailures
        assert javaProject.pmd.toolVersion == '5.4.1'

        def task = javaProject.tasks.findByName('pmd')

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
            assert reports.html.enabled
        }
    }

    @Test
    public void testShouldApplyCheckstyle() {
        rootProject.plugins.apply(CodeQualityToolsPlugin)

        assert javaProject.plugins.hasPlugin(CheckstylePlugin)

        assert !javaProject.checkstyle.ignoreFailures
        assert javaProject.checkstyle.showViolations
        assert javaProject.checkstyle.toolVersion == '6.14.1'
        assert javaProject.checkstyle.configFile == rootProject.file("code_quality_tools/checkstyle.xml")

        def task = javaProject.tasks.findByName('checkstyle')

        assert task instanceof Checkstyle

        task.with {
            assert description == 'Run checkstyle'
            assert group == 'verification'

            assert includes.size() == 1
            assert includes.contains('**/*.java')

            assert excludes.size() == 1
            assert excludes.contains('**/gen/**')

            assert reports.xml.enabled
        }
    }
}
