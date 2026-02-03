// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.editor.impl.stickyLines.actions

import com.intellij.idea.ActionsBundle
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import java.awt.datatransfer.StringSelection

/**
 * Action to copy the name of the element (class, method, function) at the sticky line.
 */
internal class StickyLinesCopyNameAction : DumbAwareAction() {

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.BGT
  }

  override fun update(e: AnActionEvent) {
    val name = findElementName(e)
    e.presentation.isEnabledAndVisible = name != null
    if (name != null) {
      e.presentation.text = ActionsBundle.message("action.EditorStickyLinesCopyName.dynamic.text", name)
    }
  }

  override fun actionPerformed(e: AnActionEvent) {
    val name = findElementName(e)
    if (name != null) {
      CopyPasteManager.getInstance().setContents(StringSelection(name))
    }
  }

  private fun findElementName(e: AnActionEvent): String? {
    val project = e.project ?: return null
    val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
    val offset = e.getData(StickyLinesDataKeys.STICKY_LINE_OFFSET) ?: return null

    val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return null
    val elementAtOffset = psiFile.findElementAt(offset) ?: return null

    // Find the nearest named element (class, method, function, etc.)
    // strict=false allows returning the element itself if it's already a PsiNamedElement
    val namedElement = PsiTreeUtil.getParentOfType(elementAtOffset, PsiNamedElement::class.java, false)
    return namedElement?.name
  }
}
