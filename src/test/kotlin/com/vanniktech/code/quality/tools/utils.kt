package com.vanniktech.code.quality.tools

import com.android.build.gradle.BaseExtension
import de.aaschmid.gradle.plugins.cpd.Cpd
import de.aaschmid.gradle.plugins.cpd.CpdExtension
import org.gradle.api.Project
import org.gradle.api.internal.model.InstantiatorBackedObjectFactory
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.internal.reflect.Instantiator

fun defaultExtensions(): CodeQualityToolsPluginExtension {
  // This is not ideal but the only solution I have found that somewhat works.
  return CodeQualityToolsPluginExtension(
    InstantiatorBackedObjectFactory(object : Instantiator {
      @Suppress("DEPRECATION")
      override fun <T : Any?> newInstance(type: Class<out T>, vararg parameters: Any?): T {
        return type.newInstance()
      }
    })
  )
}

val Project.cpd get() = extensions.getByType(CpdExtension::class.java)
val Project.cpdTask get() = tasks.getByName("cpdCheck") as Cpd

val Project.checkstyle get() = extensions.getByType(org.gradle.api.plugins.quality.CheckstyleExtension::class.java)
val Project.checkstyleTask get() = tasks.getByName("checkstyle") as Checkstyle

val Project.pmd get() = extensions.getByType(PmdExtension::class.java)
val Project.pmdTask get() = tasks.getByName("pmd") as Pmd

val Project.lintOptions get() = extensions.getByType(BaseExtension::class.java).lintOptions

val Project.check get() = tasks.getByName("check")
