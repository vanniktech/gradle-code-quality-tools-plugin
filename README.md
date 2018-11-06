# gradle-code-quality-tools-plugin

Gradle plugin that configures [Error Prone](http://errorprone.info/), [Findbugs](http://findbugs.sourceforge.net/), [Checkstyle](http://checkstyle.sourceforge.net/), [PMD](https://pmd.github.io/), [CPD](https://pmd.github.io/pmd-6.0.0/#cpd), [Lint](https://developer.android.com/studio/write/lint.html), [Detekt](https://github.com/arturbosch/detekt) & [Ktlint](https://github.com/shyiko/ktlint). All of these tools are also automatically hooked into the `check` gradle task. Below, I'll go more into depth how each of those plugins are configured.

# Set up

**root/build.gradle**

```gradle
buildscript {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }
  dependencies {
    classpath "com.vanniktech:gradle-code-quality-tools-plugin:0.14.0"
  }
}

apply plugin: "com.vanniktech.code.quality.tools"
```

Information: [This plugin is also available on Gradle plugins](https://plugins.gradle.org/plugin/com.vanniktech.code.quality.tools)

### Snapshot

```gradle
buildscript {
  repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
  }
  dependencies {
    classpath "com.vanniktech:gradle-code-quality-tools-plugin:0.15.0-SNAPSHOT"
  }
}

apply plugin: "com.vanniktech.code.quality.tools"
```

## Configuration

The philosophy of this plugin is to fail early. This means having zero warnings / errors reported from any tools. If you're just getting started with this in a large code base you might not be able to achieve this right away in which case you might want to set `failEarly` to `false` and then apply at a finer more granular scope how each tool should behave e.g. `checkstyle { ignoreFailures = false }`.

Those are all the available configurations - shown with default values and their types. More information can be found in the [Java Documentation of the Extension](src/main/groovy/com/vanniktech/code/quality/tools/CodeQualityToolsPluginExtension.groovy).

```groovy
codeQualityTools {
  boolean failEarly = true
  boolean xmlReports = true
  boolean htmlReports = false
  boolean textReports = false
  String[] ignoreProjects = []

  findbugs {
    boolean enabled = true
    String toolVersion = '3.0.1'
    String excludeFilter = 'code_quality_tools/findbugs-filter.xml'
    Boolean ignoreFailures = null
    String source = 'src'
    String effort = 'max'
    String reportLevel = 'low'
  }
  checkstyle {
    boolean enabled = true
    String toolVersion = '8.6'
    String configFile = 'code_quality_tools/checkstyle.xml'
    Boolean ignoreFailures = null
    Boolean showViolations = null
    String source = 'src'
    List<String> include = ['**/*.java']
    List<String> exclude = ['**/gen/**']
  }
  pmd {
    boolean enabled = true
    String toolVersion = '6.0.0'
    String ruleSetFile = 'code_quality_tools/pmd.xml'
    Boolean ignoreFailures = null
    String source = 'src'
    List<String> include = ['**/*.java']
    List<String> exclude = ['**/gen/**']
  }
  lint {
    boolean enabled = true
    Boolean textReport = null
    String textOutput = 'stdout'
    Boolean abortOnError = null
    Boolean warningsAsErrors = null
    Boolean checkAllWarnings = null
    String baselineFileName = null
    Boolean absolutePaths = null
    File lintConfig = null
  }
  ktlint {
    boolean enabled = true
    String toolVersion = '0.14.0'
  }
  detekt {
    boolean enabled = true
    String toolVersion = '1.0.0.RC6'
    String config = 'code_quality_tools/detekt.yml'
    String baselineFileName = null
  }
  cpd {
    boolean enabled = true
    String source = 'src'
    String language = 'java'
    Boolean ignoreFailures = null
    int minimumTokenCount = 50
  }
  errorProne {
    boolean enabled = true
    String toolVersion = '2.1.3'
  }
}
```

## Tools

Here I'll give a bit more information about how each of the tools will be applied. If there's a Gradle task that this plugin will generate it will also be hooked up into the `check` Gradle task. This means that when you execute `check` all of the rools will be running for you.

### Error Prone

It'll apply the [Error Prone Gradle Plugin](https://github.com/tbroyer/gradle-errorprone-plugin) which will run together with `assemble`. There's no report generated for this but you'll get compile warnings & errors.

### Findbugs

It'll apply the [Findbugs Plugin](https://docs.gradle.org/current/userguide/findbugs_plugin.html) and generate the `findbugs` task that will execute findbugs. The configuration properties of `codeQualityTools -> findbugs` mirror the [properties from the plugin](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.FindBugsExtension.html).

### Checkstyle

It'll apply the [Checkstyle Plugin](https://docs.gradle.org/current/userguide/checkstyle_plugin.html) and generate the `checkstyle` task that will execute checkstyle. The configuration properties of `codeQualityTools -> checkstyle` mirror the [properties from the plugin](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.CheckstyleExtension.html).

### PMD

It'll apply the [PMD Plugin](https://docs.gradle.org/current/userguide/pmd_plugin.html) and generate the `pmd` task that will execute pmd. The configuration properties of `codeQualityTools -> pmd` mirror the [properties from the plugin](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.PmdExtension.html).

### CPD

It'll apply the [CPD Plugin](https://github.com/aaschmid/gradle-cpd-plugin) and generate the `cpdCheck` task that will execute cpd. The configuration properties of `codeQualityTools -> cpd` mirror the [properties from the plugin](https://github.com/aaschmid/gradle-cpd-plugin#options).

### Lint

This will only work when one of the Android Plugins (`com.android.application`, `com.android.library`, etc.) are applied. The configuration properties of `codeQualityTools -> lint` mirror the [properties from the lintOptions](https://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.LintOptions.html).

### Detekt

It'll use the specified detekt version and generate the `detektCheck` task which will run detekt on your code base.

### Ktlint

It'll use the specified ktlint version and then generate two tasks. `ktlint` which will run ktlint over your code and flag issues. `ktlintFormat` will reformat your code.

**Note:** There might be some configuration properties that are not mirrored in which case feel free to open a PR. Personally, I don't have the need for all of them.

# License

Copyright (C) 2016 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0
