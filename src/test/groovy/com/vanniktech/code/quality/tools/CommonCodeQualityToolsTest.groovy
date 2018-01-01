package com.vanniktech.code.quality.tools

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeaturePlugin
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

abstract class CommonCodeQualityToolsTest {
  Project rootProject

  Project[] emptyProjects
  Project emptyProject

  Project[] javaProjects
  Project javaProject
  Project javaLibraryProject
  // Project javaGradlePluginProject    We can't use this one yet since testing takes forever then.

  Project[] kotlinProjects
  // Project kotlinProject              groovy.lang.MissingPropertyException: No such property: KotlinPlugin for class
  // Project kotlinAndroidProject       groovy.lang.MissingPropertyException: No such property: KotlinPlugin for class
  Project kotlinPlatformCommonProject
  Project kotlinPlatformJvmProject
  Project kotlinPlatformJsProject

  Project[] androidProjects
  Project androidAppProject
  Project androidLibraryProject
  Project androidTestProject
  Project androidFeatureProject
  // Project androidInstantAppProject   groovy.lang.MissingPropertyException: No such property: InstantAppPlugin for class

  Project[] projects

  @Before void setUp() {
    rootProject = ProjectBuilder.builder().withName('root').build()

    emptyProject = ProjectBuilder.builder().withName('empty').withParent(rootProject).build()

    javaProject = ProjectBuilder.builder().withName('java').withParent(rootProject).build()
    javaProject.plugins.apply(JavaPlugin)

    javaLibraryProject = ProjectBuilder.builder().withName('java-library').withParent(rootProject).build()
    javaLibraryProject.plugins.apply(JavaLibraryPlugin)

    kotlinPlatformCommonProject = ProjectBuilder.builder().withName('kotlin-platform-common').withParent(rootProject).build()
    kotlinPlatformCommonProject.plugins.apply(KotlinPlatformCommonPlugin)

    kotlinPlatformJvmProject = ProjectBuilder.builder().withName('kotlin-platform-jvm').withParent(rootProject).build()
    kotlinPlatformJvmProject.plugins.apply(KotlinPlatformJvmPlugin)

    kotlinPlatformJsProject = ProjectBuilder.builder().withName('kotlin-platform-js').withParent(rootProject).build()
    kotlinPlatformJsProject.plugins.apply(KotlinPlatformJsPlugin)

    androidAppProject = ProjectBuilder.builder().withName('android app').build()
    androidAppProject.plugins.apply(AppPlugin)

    androidLibraryProject = ProjectBuilder.builder().withName('android library').build()
    androidLibraryProject.plugins.apply(LibraryPlugin)

    androidTestProject = ProjectBuilder.builder().withName('android test').build()
    androidTestProject.plugins.apply(TestPlugin)

    androidFeatureProject = ProjectBuilder.builder().withName('android feature').build()
    androidFeatureProject.plugins.apply(FeaturePlugin)

    emptyProjects = [
        emptyProject
    ]

    javaProjects = [
        javaProject,
        javaLibraryProject
    ]

    kotlinProjects = [
        kotlinPlatformCommonProject,
        kotlinPlatformJvmProject,
        kotlinPlatformJsProject
    ]

    androidProjects = [
        androidAppProject,
        androidLibraryProject,
        androidTestProject,
        androidFeatureProject
    ]

    projects = emptyProjects + javaProjects + kotlinProjects + androidProjects
  }

  static boolean taskDependsOn(final Task task, final String taskName) {
    def it = task.dependsOn.iterator()

    while (it.hasNext()) {
      def item = it.next()

      if (item.toString().equals(taskName)) {
        return true
      }
    }

    return false
  }
}
