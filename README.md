# gradle-code-quality-tools-plugin

[![Build Status](https://travis-ci.org/vanniktech/gradle-code-quality-tools-plugin.svg?branch=master)](https://travis-ci.org/vanniktech/gradle-code-quality-tools-plugin?branch=master)
[![Codecov](https://codecov.io/github/vanniktech/gradle-code-quality-tools-plugin/coverage.svg?branch=master)](https://codecov.io/github/vanniktech/gradle-code-quality-tools-plugin?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Gradle plugin that generates configures [ErrorProne](http://errorprone.info/), [Findbugs](http://findbugs.sourceforge.net/), [Checkstyle](http://checkstyle.sourceforge.net/), [PMD](https://pmd.github.io/), [CPD](https://pmd.github.io/pmd-5.8.0/usage/cpd-usage.html), [Lint](https://developer.android.com/studio/write/lint.html), [Detekt](https://github.com/arturbosch/detekt) & [Ktlint](https://github.com/shyiko/ktlint). All of these tools are also automatically hooked into the `check` gradle task.

Works with the latest Gradle Android Tools version 3.0.1.

# Set up

**root/build.gradle**

```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.vanniktech:gradle-code-quality-tools-plugin:0.8.0'
  }
}

apply plugin: 'com.vanniktech.code.quality.tools'
```

Information: [This plugin is also available on Gradle plugins](https://plugins.gradle.org/plugin/com.vanniktech.code.quality.tools)

### Snapshots

Can be found [here](https://oss.sonatype.org/#nexus-search;quick~gradle-code-quality-tools-plugin). Current one is:

```groovy
classpath 'com.vanniktech:gradle-code-quality-tools-plugin:0.9.0-SNAPSHOT'
```

## Configuration

Those are all available configurations - shown with default values and their types. More information can be found in the [Java Documentation of the Extension](src/main/groovy/com/vanniktech/code/quality/tools/CodeQualityToolsPluginExtension.groovy).

```groovy
codeQualityTools {
  failEarly = true // type boolean
  xmlReports = true // type boolean
  htmlReports = false // type boolean
  textReports = false // type boolean
  ignoreProjects = [] // type String array

  findbugs {
    enabled = true // type boolean
    toolVersion = '3.0.1' // type String
    excludeFilter = 'code_quality_tools/findbugs-filter.xml' // type String
    ignoreFailures = null // type Boolean
    source = 'src' // type String
    effort = 'max' // type String
    reportLevel = 'low' // type String
  }

  checkstyle {
    enabled = true // type boolean
    toolVersion = '7.8.2' // type String
    configFile = 'code_quality_tools/checkstyle.xml' // type String
    ignoreFailures = null // type Boolean
    showViolations // type Boolean
    source = 'src' // type String
    include = ['**/*.java'] // type List<String>
    exclude = ['**/gen/**'] // type List<String>
  }

  pmd {
    enabled = true // type boolean
    toolVersion = '5.8.1' // type String
    ruleSetFile = 'code_quality_tools/pmd.xml' // type String
    ignoreFailures = null // type Boolean
    source = 'src' // type String
    include = ['**/*.java'] // type List<String>
    exclude = ['**/gen/**'] // type List<String>
  }

  lint {
    enabled = true // type boolean
    textReport = null // type Boolean
    textOutput = 'stdout' // type String
    abortOnError = null // type Boolean
    warningsAsErrors = null // type Boolean
    checkAllWarnings = null // type Boolean
    baselineFileName = null // type String
  }

  ktlint {
    enabled = true // type boolean
    toolVersion = '0.8.3' // type String
  }

  detekt {
    enabled = true // type boolean
    toolVersion = '1.0.0.M12.3' // type String
    config = 'code_quality_tools/detekt.yml' // type String
  }

  cpd {
    enabled = true // type boolean
    toolVersion = '5.4.2' // type String
    source = 'src' // type String
    language = 'java' // type String
    ignoreFailures = null // type Boolean
    minimumTokenCount = 50 // type int
  }

  errorProne {
    enabled = true // type boolean
    toolVersion = '2.0.20' // type String
  }
}

```

In order to specify Gradle Versions for Plugins (Detekt, ErrorProne or CPD), you'll need to do that using `gradle.properties`:

```groovy
# We need to define those here since adding Gradle Plugins is only allowed before afterEvaluate.
# Defining values with custom extensions are not visible at this prior step though.
codeQualityTools.detekt.gradlePluginVersion = 1.0.0.M13.2
codeQualityTools.errorProne.gradlePluginVersion = 0.0.10
codeQualityTools.cpd.gradlePluginVersion = 1.0
```

# License

Copyright (C) 2016 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0