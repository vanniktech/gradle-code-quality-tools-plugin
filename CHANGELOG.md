# Change Log

Version 0.13.0 *(In development)*
---------------------------------

Version 0.12.0 *(2018-06-24)*
-----------------------------

- Reupload 0.11.0 binary with a new version since mavenCentral upload was flaky.

Version 0.11.0 *(2018-06-24)*
-----------------------------

- Detekt: Add baseline feature and rewrite the internals. [\#138](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/138) ([vanniktech](https://github.com/vanniktech))
- Unify setup, improve a few things and bump versions. [\#136](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/136) ([vanniktech](https://github.com/vanniktech))
- Use Gradle Maven Publish Plugin for publishing. [\#135](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/135) ([vanniktech](https://github.com/vanniktech))
- Ktlint: Consider .editorconfig files as task inputs. [\#132](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/132) ([vanniktech](https://github.com/vanniktech))
- Detekt: Consider configuration file as task input. [\#131](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/131) ([vanniktech](https://github.com/vanniktech))
- Fix classes for Findbugs in Kotlin only project. [\#126](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/126) ([vanniktech](https://github.com/vanniktech))

Version 0.10.0 *(2018-03-13)*
-----------------------------

- Add option for Lint.checkReleaseBuilds and default to false. [\#125](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/125) ([vanniktech](https://github.com/vanniktech))
- Update README to include google\(\) repository in buildscript repositories. [\#123](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/123) ([vanniktech](https://github.com/vanniktech))
- Extract groups as constants. [\#121](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/121) ([vanniktech](https://github.com/vanniktech))
- Detekt: Make detektCheck task incremental. [\#119](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/119) ([vanniktech](https://github.com/vanniktech))
- ktlint: Make ktlint & ktlintFormat incremental. [\#118](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/118) ([vanniktech](https://github.com/vanniktech))
- Detekt: Generate reports from execution. [\#117](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/117) ([vanniktech](https://github.com/vanniktech))
- Add tests for ErrorProne. [\#114](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/114) ([vanniktech](https://github.com/vanniktech))
- Add tests for CPD. [\#113](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/113) ([vanniktech](https://github.com/vanniktech))
- Fix casing of errorprone. [\#111](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/111) ([vanniktech](https://github.com/vanniktech))
- CodeQualityToolsPluginExtension: Explicitly set Booleans to null. [\#110](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/110) ([vanniktech](https://github.com/vanniktech))

Version 0.9.0 *(2018-01-06)*
----------------------------

- Also test against an empty project where no plugin is applied. [\#109](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/109) ([vanniktech](https://github.com/vanniktech))
- Remove cpd toolVersion and default to pmd. [\#108](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/108) ([vanniktech](https://github.com/vanniktech))
- Update Checkstyle to 8.6 [\#107](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/107) ([vanniktech](https://github.com/vanniktech))
- Update Detekt to 1.0.0.RC6 [\#106](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/106) ([vanniktech](https://github.com/vanniktech))
- Update ktlint to 0.14.0 [\#105](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/105) ([vanniktech](https://github.com/vanniktech))
- Update Error Prone version to 2.1.3 & Plugin to 0.0.13 [\#103](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/103) ([vanniktech](https://github.com/vanniktech))
- Update default PMD version to 6.0.0 & CPD to 1.1 [\#101](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/101) ([vanniktech](https://github.com/vanniktech))
- Slight README modifications. [\#100](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/100) ([vanniktech](https://github.com/vanniktech))
- Convert build.gradle from 4 spaces to 2. [\#99](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/99) ([vanniktech](https://github.com/vanniktech))
- Update Gradle to 4.4.1 [\#98](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/98) ([vanniktech](https://github.com/vanniktech))
- Add lintConfig configuration property. [\#97](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/97) ([vanniktech](https://github.com/vanniktech))
- Lint: Add absolutePaths configuration property. [\#96](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/96) ([vanniktech](https://github.com/vanniktech))
- Also enable CPD & ErrorProne in Single project. [\#95](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/95) ([vanniktech](https://github.com/vanniktech))
- Update Jacoco Gradle Plugin to 0.11.0 [\#94](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/94) ([vanniktech](https://github.com/vanniktech))
- Add support for single project. Only tools missing are CPD & ErrorProne. [\#93](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/93) ([vanniktech](https://github.com/vanniktech))
- Get rid of Detekt Gradle Plugin and do it ourself. [\#92](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/92) ([vanniktech](https://github.com/vanniktech))
- Refactor the Plugin a bit and encapsulate each tool into it's own method. [\#91](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/91) ([vanniktech](https://github.com/vanniktech))
- Better ktlint integration & update to 0.13.0 [\#90](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/90) ([vanniktech](https://github.com/vanniktech))
- Update libraries to latest. [\#89](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/89) ([vanniktech](https://github.com/vanniktech))
- Update plugin-publish-plugin [\#88](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/88) ([vanniktech](https://github.com/vanniktech))
- Fix paths in ktlint output directory 2.0 [\#87](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/87) ([AndrewPolovko](https://github.com/AndrewPolovko))
- Update JUnit Jacoco Gradle Plugin to 0.9.0 [\#86](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/86) ([vanniktech](https://github.com/vanniktech))
- Fix paths in ktlint output directory. [\#85](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/85) ([vanniktech](https://github.com/vanniktech))
- Fix gradle build and clean issue on Windows. [\#84](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/84) ([AndrewPolovko](https://github.com/AndrewPolovko))
- Don't clean build again when deploying SNAPSHOTS. [\#83](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/83) ([vanniktech](https://github.com/vanniktech))
- Update Jacoco Gradle Plugin to 0.8.0 [\#82](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/82) ([vanniktech](https://github.com/vanniktech))

Version 0.8.0 *(2017-08-12)*
----------------------------

- Fix findbugs tasks when running on Java Module with Gradle 4.\* [\#81](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/81) ([vanniktech](https://github.com/vanniktech))

Version 0.7.0 *(2017-08-02)*
---------------------------

- Change task execution order to have Lint & Findbugs at the end. [\#76](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/76) ([vanniktech](https://github.com/vanniktech))
- ktlint task: Generate checkstyle report. [\#74](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/74) ([vanniktech](https://github.com/vanniktech))
- Update ktlint to 0.9.1 [\#73](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/73) ([vanniktech](https://github.com/vanniktech))
- Specify multiple inclusion/exclusion patterns [\#72](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/72) ([budnyjj](https://github.com/budnyjj))
- Fix configuration for gradlePluginVersion. [\#71](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/71) ([vanniktech](https://github.com/vanniktech))
- Update Detekt to 1.0.0.M13.2 [\#70](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/70) ([vanniktech](https://github.com/vanniktech))

Version 0.6.0 *(2017-07-08)*
----------------------------

- Add ErrorProne. [\#64](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/64) ([vanniktech](https://github.com/vanniktech))
- Add CPD. [\#63](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/63) ([vanniktech](https://github.com/vanniktech))
- Add detekt. [\#62](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/62) ([vanniktech](https://github.com/vanniktech))

Version 0.5.0 *(2017-07-01)*
----------------------------

- Add KtLint. [\#60](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/60) ([vanniktech](https://github.com/vanniktech))
- Update to Gradle 4.0 [\#58](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/58) ([vanniktech](https://github.com/vanniktech))
- Add baseline extension point for Lint. [\#55](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/55) ([vanniktech](https://github.com/vanniktech))
- Lint: Add checkAllWarnings. [\#53](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/53) ([vanniktech](https://github.com/vanniktech))
- Update Checkstyle to 7.6 and PMD to 5.5.4 [\#51](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/51) ([vanniktech](https://github.com/vanniktech))

Version 0.4.0 *(2016-07-16)*
----------------------------

- Update Default PMD Version to 5.5.0 [\#38](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/38) ([vanniktech](https://github.com/vanniktech))
- Update Default Checkstyle Version to 7.0 [\#37](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/37) ([vanniktech](https://github.com/vanniktech))
- Findbugs extension: Add effort + reportLevel [\#36](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/36) ([vanniktech](https://github.com/vanniktech))
- Checkstyle + PMD: Include + exclude extension [\#35](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/35) ([vanniktech](https://github.com/vanniktech))
- Add source attribute to Findbugs, PMD & Checkstyle extension [\#34](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/34) ([vanniktech](https://github.com/vanniktech))
- PMD: Remove unnecessary dependency to assemble task [\#33](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/33) ([vanniktech](https://github.com/vanniktech))

Version 0.3.0 *(2016-05-16)*
----------------------------

- Update default Checkstyle version to 6.18 [\#30](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/30) ([vanniktech](https://github.com/vanniktech))
- Add ignoreFailures & showViolations to checkstyle extension [\#29](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/29) ([vanniktech](https://github.com/vanniktech))
- Add ignoreFailures to pmd extension [\#28](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/28) ([vanniktech](https://github.com/vanniktech))
- Add ignoreFailures to findbugs extension [\#27](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/27) ([vanniktech](https://github.com/vanniktech))
- Update Android Gradle Build Tools to 2.1.0 [\#22](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/22) ([vanniktech](https://github.com/vanniktech))

Version 0.2.0 *(2016-02-13)*
----------------------------

- Add abortOnError & warningsAsErrors on lint extension [\#11](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/11) ([vanniktech](https://github.com/vanniktech))
- Add documentation on CodeQualityToolsPluginExtension [\#9](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/9) ([vanniktech](https://github.com/vanniktech))
- Fix configuration. Add lint to tools & configuration has enabled flag for all tools [\#5](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/5) ([vanniktech](https://github.com/vanniktech))
- Add CodeQualityToolsExtension and some configuration options [\#3](https://github.com/vanniktech/gradle-code-quality-tools-plugin/pull/3) ([vanniktech](https://github.com/vanniktech))

Version 0.1.0 *(2016-01-10)*
----------------------------

- Initial release