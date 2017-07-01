# gradle-code-quality-tools-plugin

[![Build Status](https://travis-ci.org/vanniktech/gradle-code-quality-tools-plugin.svg?branch=master)](https://travis-ci.org/vanniktech/gradle-code-quality-tools-plugin?branch=master)
[![Codecov](https://codecov.io/github/vanniktech/gradle-code-quality-tools-plugin/coverage.svg?branch=master)](https://codecov.io/github/vanniktech/gradle-code-quality-tools-plugin?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
![Java 7 required](https://img.shields.io/badge/java-7-brightgreen.svg)

Gradle plugin that generates Findbugs-, Checkstyle- and PMD Tasks for every subproject. In Android projects Lint will also be configured. All of the taks will also automatically hook into the build lifecycle.

Works with the latest Gradle Android Tools version 2.1.3. This plugin is compiled using Java 7 hence you also need Java 7 in order to use it.

# Set up

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.vanniktech:gradle-code-quality-tools-plugin:0.5.0'
    }
}

apply plugin: 'com.vanniktech.code.quality.tools'
```

Information: [This plugin is also available on Gradle plugins](https://plugins.gradle.org/plugin/com.vanniktech.code.quality.tools)

### Snapshots

Can be found [here](https://oss.sonatype.org/#nexus-search;quick~gradle-code-quality-tools-plugin). Current one is:

```groovy
classpath 'com.vanniktech:gradle-code-quality-tools-plugin:0.5.0-SNAPSHOT'
```

## Configuration

Those are all available configurations - shown with default values and their types. More information can be found in the [Java Documentation of the Extension](src/main/groovy/com/vanniktech/code/quality/tools/CodeQualityToolsPluginExtension.groovy).

```groovy
codeQualityTools {
    failEarly = true // type boolean
    xmlReports = true // type boolean
    htmlReports = false // type boolean
    ignoreProjects = [] // type String array

    findbugs {
        enabled = true // type boolean
        toolVersion = '3.0.1' // type String
        excludeFilter = 'code_quality_tools/findbugs-filter.xml' // type String
        ignoreFailures // type Boolean
        source = 'src' // type String
        effort = 'max' // type String
        reportLevel = 'low' // type String
    }

    checkstyle {
        enabled = true // type boolean
        toolVersion = '7.8.2' // type String
        configFile = 'code_quality_tools/checkstyle.xml' // type String
        ignoreFailures // type Boolean
        showViolations // type Boolean
        source = 'src' // type String
        include = '**/*.java' // type String
        exclude = '**/gen/**' // type String
    }

    pmd {
        enabled = true // type boolean
        toolVersion = '5.8.1' // type String
        ruleSetFile = 'code_quality_tools/pmd.xml' // type String
        ignoreFailures // type Boolean
        source = 'src' // type String
        include = '**/*.java' // type String
        exclude = '**/gen/**' // type String
    }

    lint {
        enabled = true // type boolean
        textReport = null // type Boolean
        textOutput = 'stdout' // type String
        abortOnError = null // type Boolean
        warningsAsErrors = null // type Boolean
        baselineFileName = null // type String
    }

    ktlint {
        enabled = true // type boolean
        toolVersion = '0.8.3' // type String
    }
}

```

# License

Copyright (C) 2016 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0