package com.vanniktech.code.quality.tools

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.junit.Before

@Suppress("Detekt.UnnecessaryAbstractClass")
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
  lateinit var kotlinPlatformCommonProject: Project
  lateinit var kotlinPlatformJvmProject: Project
  lateinit var kotlinPlatformJsProject: Project

  lateinit var androidProjects: Array<Project>
  lateinit var androidAppProject: Project
  lateinit var androidLibraryProject: Project
  lateinit var androidTestProject: Project
  // Project androidInstantAppProject   groovy.lang.MissingPropertyException: No such property: InstantAppPlugin for class

  lateinit var projects: Array<Project>

  @Before @Suppress("Detekt.LongMethod") fun setUp() {
    rootProject = ProjectBuilder.builder().withName("root").build()

    emptyProject = ProjectBuilder.builder().withName("empty").withParent(rootProject).build()

    javaProject = ProjectBuilder.builder().withName("java").withParent(rootProject).build()
    javaProject.plugins.apply(JavaPlugin::class.java)

    javaLibraryProject = ProjectBuilder.builder().withName("java-library").withParent(rootProject).build()
    javaLibraryProject.plugins.apply(JavaLibraryPlugin::class.java)

    kotlinPlatformCommonProject = ProjectBuilder.builder().withName("kotlin-platform-common").withParent(rootProject).build()
    kotlinPlatformCommonProject.plugins.apply(KotlinPlatformCommonPlugin::class.java)

    kotlinPlatformJvmProject = ProjectBuilder.builder().withName("kotlin-platform-jvm").withParent(rootProject).build()
    kotlinPlatformJvmProject.plugins.apply(KotlinPlatformJvmPlugin::class.java)

    kotlinPlatformJsProject = ProjectBuilder.builder().withName("kotlin-platform-js").withParent(rootProject).build()
    kotlinPlatformJsProject.plugins.apply(KotlinPlatformJsPlugin::class.java)

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
      kotlinPlatformCommonProject,
      kotlinPlatformJvmProject,
      kotlinPlatformJsProject,
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
