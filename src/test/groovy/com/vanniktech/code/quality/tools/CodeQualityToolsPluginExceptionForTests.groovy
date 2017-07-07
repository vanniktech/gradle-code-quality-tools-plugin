package com.vanniktech.code.quality.tools

/**
 * Simple testing helper which will emulate real environment regarding the extension model
 */
@SuppressWarnings("UnnecessaryQualifiedReference")
class CodeQualityToolsPluginExceptionForTests extends CodeQualityToolsPluginExtension {
    CodeQualityToolsPluginExtension.Findbugs findbugs = new CodeQualityToolsPluginExtension.Findbugs()
    CodeQualityToolsPluginExtension.Checkstyle checkstyle = new CodeQualityToolsPluginExtension.Checkstyle()
    CodeQualityToolsPluginExtension.Pmd pmd = new CodeQualityToolsPluginExtension.Pmd()
    CodeQualityToolsPluginExtension.Lint lint = new CodeQualityToolsPluginExtension.Lint()
    CodeQualityToolsPluginExtension.Ktlint ktlint = new CodeQualityToolsPluginExtension.Ktlint()
    CodeQualityToolsPluginExtension.Detekt detekt = new CodeQualityToolsPluginExtension.Detekt()
    CodeQualityToolsPluginExtension.Cpd cpd = new CodeQualityToolsPluginExtension.Cpd()
}
