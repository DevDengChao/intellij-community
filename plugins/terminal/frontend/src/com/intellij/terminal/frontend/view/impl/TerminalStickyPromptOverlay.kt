// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.terminal.frontend.view.impl

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import org.jetbrains.plugins.terminal.block.ui.TerminalUi
import org.jetbrains.plugins.terminal.view.TerminalOutputModel
import org.jetbrains.plugins.terminal.view.shellIntegration.TerminalCommandBlock
import org.jetbrains.plugins.terminal.view.shellIntegration.TerminalShellIntegration
import org.jetbrains.plugins.terminal.view.shellIntegration.getTypedCommandText
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * A sticky overlay component that displays the current input line (prompt + command) 
 * at the bottom of the terminal viewport when the user scrolls up.
 * 
 * This provides behavior similar to CSS position:sticky, keeping the input line visible
 * while browsing terminal history.
 */
class TerminalStickyPromptOverlay(
  private val editor: EditorEx,
  private val outputModel: TerminalOutputModel,
  private val shellIntegration: TerminalShellIntegration
) : JPanel(BorderLayout()) {
  
  private val promptLabel = JLabel()
  
  init {
    isOpaque = true
    background = TerminalUi.defaultBackgroundLazy()
    border = BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.border()),
      JBUI.Borders.empty(4, 8)
    )
    
    promptLabel.foreground = (editor as? EditorImpl)?.colorsScheme?.defaultForeground ?: JBColor.foreground()
    promptLabel.font = editor.colorsScheme.getFont(editor.settings)
    add(promptLabel, BorderLayout.CENTER)
    
    isVisible = false
  }
  
  /**
   * Updates the overlay content with the current command line text.
   */
  fun updateContent() {
    val activeBlock = shellIntegration.blocksModel.activeBlock as? TerminalCommandBlock
    val commandText = activeBlock?.getTypedCommandText(outputModel)
    
    if (commandText != null) {
      // Extract prompt text (from start to commandStartOffset)
      val promptText = if (activeBlock.commandStartOffset != null && activeBlock.commandStartOffset!! >= outputModel.startOffset) {
        outputModel.getText(activeBlock.startOffset, activeBlock.commandStartOffset!!).toString()
      } else {
        ""
      }
      
      promptLabel.text = "$promptText$commandText"
    } else {
      // Fallback: show text from cursor line
      val cursorOffset = outputModel.cursorOffset.toRelative(outputModel)
      if (cursorOffset >= 0 && cursorOffset <= outputModel.document.textLength) {
        val lineNumber = editor.document.getLineNumber(cursorOffset)
        if (lineNumber >= 0 && lineNumber < editor.document.lineCount) {
          val lineStart = editor.document.getLineStartOffset(lineNumber)
          val lineEnd = editor.document.getLineEndOffset(lineNumber)
          promptLabel.text = outputModel.document.getText(lineStart, lineEnd)
        }
      }
    }
  }
  
  /**
   * Updates the visibility of the overlay based on scroll position.
   * Shows the overlay only when scrolled away from the bottom.
   */
  fun updateVisibility() {
    val scrollY = editor.scrollingModel.verticalScrollOffset
    val contentHeight = editor.contentComponent.height
    val visibleHeight = editor.scrollingModel.visibleArea.height
    
    // Show overlay when not at the very bottom
    val isAtBottom = scrollY + visibleHeight >= contentHeight - JBUI.scale(10)
    isVisible = !isAtBottom
  }
  
  override fun getPreferredSize(): Dimension {
    val superSize = super.getPreferredSize()
    // Ensure minimum height for the overlay
    return Dimension(superSize.width, maxOf(superSize.height, JBUI.scale(30)))
  }
  
  override fun paintComponent(g: Graphics) {
    super.paintComponent(g)
    // Custom painting if needed
  }
}
