// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.terminal.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.TerminalBundle;
import org.jetbrains.plugins.terminal.ui.TerminalContainer;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import java.awt.Component;

public final class TerminalScrollToEndAction extends DumbAwareAction {

  public TerminalScrollToEndAction() {
    super(TerminalBundle.messagePointer("action.Terminal.ScrollToEnd.text"),
          TerminalBundle.messagePointer("action.Terminal.ScrollToEnd.description"),
          AllIcons.RunConfigurations.Scroll_down);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    TerminalWidget terminalWidget = e.getData(TerminalContainer.TERMINAL_WIDGET_DATA_KEY);
    if (terminalWidget != null) {
      scrollToEnd(terminalWidget);
    }
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    TerminalWidget terminalWidget = e.getData(TerminalContainer.TERMINAL_WIDGET_DATA_KEY);
    e.getPresentation().setEnabledAndVisible(terminalWidget != null);
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }

  private static void scrollToEnd(@NotNull TerminalWidget terminalWidget) {
    JComponent component = terminalWidget.getComponent();
    JScrollBar scrollBar = findScrollBar(component);
    if (scrollBar != null) {
      scrollBar.setValue(scrollBar.getMaximum());
    }
  }

  private static JScrollBar findScrollBar(Component component) {
    if (component instanceof JScrollBar) {
      return (JScrollBar)component;
    }
    if (component instanceof java.awt.Container container) {
      for (Component child : container.getComponents()) {
        JScrollBar scrollBar = findScrollBar(child);
        if (scrollBar != null) {
          return scrollBar;
        }
      }
    }
    return null;
  }
}

