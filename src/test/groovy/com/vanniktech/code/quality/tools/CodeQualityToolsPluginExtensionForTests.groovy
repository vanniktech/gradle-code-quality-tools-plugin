package com.vanniktech.code.quality.tools

/** Simple testing helper which will emulate real environment regarding the extension model. */
@SuppressWarnings("UnnecessaryQualifiedReference") class CodeQualityToolsPluginExtensionForTests extends CodeQualityToolsPluginExtension {
  FindbugsExtension findbugs = new FindbugsExtension()
  CheckstyleExtension checkstyle = new CheckstyleExtension()
  PmdExtension pmd = new PmdExtension()
  LintExtension lint = new LintExtension()
  KtlintExtension ktlint = new KtlintExtension()
  DetektExtension detekt = new DetektExtension()
  CpdExtension cpd = new CpdExtension()
  ErrorProneExtension errorProne = new ErrorProneExtension()
}
