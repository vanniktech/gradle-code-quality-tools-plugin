package com.vanniktech.code.quality.tools

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinApiPlugin
import org.junit.Before

abstract class CommonCodeQualityToolsTest {
  lateinit var rootProject: Project

  lateinit var emptyProjects: Array<Project>
  lateinit var emptyProject: Project

  lateinit var javaProjects: Array<Project>
  lateinit var javaProject: Project
  lateinit var javaLibraryProject: Project
  // Project javaGradlePluginProject    We can't use this one yet since testing takes forever then.

  lateinit var kotlinProjects: Array<Project>

  // Project kotlinProject              groovy.lang.MissingPropertyException: No such property: KotlinPlugin for class
  // Project kotlinAndroidProject       groovy.lang.MissingPropertyException: No such property: KotlinPlugin for class
  lateinit var kotlinProject: Project

  lateinit var androidProjects: Array<Project>
  lateinit var androidAppProject: Project
  lateinit var androidLibraryProject: Project
  lateinit var androidTestProject: Project
  // Project androidInstantAppProject   groovy.lang.MissingPropertyException: No such property: InstantAppPlugin for class

  lateinit var projects: Array<Project>

  @Before fun setUp() {
    rootProject = ProjectBuilder.builder().withName("root").build()

    emptyProject = ProjectBuilder.builder().withName("empty").withParent(rootProject).build()

    javaProject = ProjectBuilder.builder().withName("java").withParent(rootProject).build()
    javaProject.plugins.apply(JavaPlugin::class.java)

    javaLibraryProject = ProjectBuilder.builder().withName("java-library").withParent(rootProject).build()
    javaLibraryProject.plugins.apply(JavaLibraryPlugin::class.java)

    kotlinProject = ProjectBuilder.builder().withName("kotlin").withParent(rootProject).build()
    kotlinProject.plugins.apply(KotlinApiPlugin::class.java)

    androidAppProject = ProjectBuilder.builder().withName("android app").build()
    androidAppProject.plugins.apply(AppPlugin::class.java)

    androidLibraryProject = ProjectBuilder.builder().withName("android library").build()
    androidLibraryProject.plugins.apply(LibraryPlugin::class.java)

    androidTestProject = ProjectBuilder.builder().withName("android test").build()
    androidTestProject.plugins.apply(TestPlugin::class.java)

    emptyProjects = arrayOf(
      emptyProject,
    )

    javaProjects = arrayOf(
      javaProject,
      javaLibraryProject,
    )

    kotlinProjects = arrayOf(
      kotlinProject,
    )

    androidProjects = arrayOf(
      androidAppProject,
      androidLibraryProject,
      androidTestProject,
    )

    projects = emptyProjects + javaProjects + kotlinProjects + androidProjects
  }

  fun taskDependsOn(task: Task, taskName: String): Boolean {
    val it = task.dependsOn.iterator()

    while (it.hasNext()) {
      val item = it.next()

      if (item.toString() == taskName) {
        return true
      }
    }

    return false
  }
}
