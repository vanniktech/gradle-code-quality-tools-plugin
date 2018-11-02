package com.vanniktech.code.quality.tools
/** Simple testing helper which will emulate real environment regarding the extension model. */
@SuppressWarnings("UnnecessaryQualifiedReference") class CodeQualityToolsPluginExtensionForTests extends CodeQualityToolsPluginExtension {
  CodeQualityToolsPluginExtension.Findbugs findbugs = new CodeQualityToolsPluginExtension.Findbugs()
  CodeQualityToolsPluginExtension.Checkstyle checkstyle = new CodeQualityToolsPluginExtension.Checkstyle()
  CodeQualityToolsPluginExtension.Pmd pmd = new CodeQualityToolsPluginExtension.Pmd()
  LintExtension lint = new LintExtension()
  KtlintExtension ktlint = new KtlintExtension()
  DetektExtension detekt = new DetektExtension()
  CodeQualityToolsPluginExtension.Cpd cpd = new CodeQualityToolsPluginExtension.Cpd()
  CodeQualityToolsPluginExtension.ErrorProne errorProne = new CodeQualityToolsPluginExtension.ErrorProne()
}
