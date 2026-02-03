// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.editor.impl.stickyLines.actions

import com.intellij.openapi.actionSystem.DataKey

/**
 * DataKeys for passing data to sticky line actions.
 */
internal object StickyLinesDataKeys {
  /**
   * The navigation offset of the sticky line. This is the offset in the document
   * where the caret should be placed when clicking on the sticky line.
   */
  @JvmField
  val STICKY_LINE_OFFSET: DataKey<Int> = DataKey.create("sticky.line.offset")
}
