package com.vanniktech.code.quality.tools

import com.android.build.gradle.BaseExtension
import de.aaschmid.gradle.plugins.cpd.Cpd
import de.aaschmid.gradle.plugins.cpd.CpdExtension
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.testfixtures.ProjectBuilder

fun defaultExtensions(): CodeQualityToolsPluginExtension {
  val project = ProjectBuilder.builder().build()
  return CodeQualityToolsPluginExtension(project.objects)
}

val Project.cpd get() = extensions.getByType(CpdExtension::class.java)
val Project.cpdTask get() = tasks.getByName("cpdCheck") as Cpd

val Project.checkstyle get() = extensions.getByType(org.gradle.api.plugins.quality.CheckstyleExtension::class.java)
val Project.checkstyleTask get() = tasks.getByName("checkstyle") as Checkstyle

val Project.pmd get() = extensions.getByType(PmdExtension::class.java)
val Project.pmdTask get() = tasks.getByName("pmd") as Pmd

val Project.lintOptions get() = extensions.getByType(BaseExtension::class.java).lintOptions

val Project.check get() = tasks.getByName("check")
