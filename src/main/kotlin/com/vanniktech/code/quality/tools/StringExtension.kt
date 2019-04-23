package com.vanniktech.code.quality.tools

import org.gradle.util.VersionNumber

internal fun String.asVersion() = VersionNumber.parse(this)
