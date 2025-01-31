package com.vanniktech.code.quality.tools

private fun String?.wrap(wrap: String) = if (this == null) null else "$wrap$this$wrap"
