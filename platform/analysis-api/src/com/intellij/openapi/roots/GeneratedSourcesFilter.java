// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.roots;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class GeneratedSourcesFilter {
  public static final ExtensionPointName<GeneratedSourcesFilter> EP_NAME = ExtensionPointName.create("com.intellij.generatedSourcesFilter");

  public static boolean isGeneratedSourceByAnyFilter(@NotNull VirtualFile file, @NotNull Project project) {
    return ReadAction.compute(() -> {
      if (project.isDisposed() || !file.isValid()) return false;
      for (GeneratedSourcesFilter filter : EP_NAME.getExtensions()) {
        if (filter.isGeneratedSource(file, project)) {
          return true;
        }
      }
      return false;
    });
  }

  public abstract boolean isGeneratedSource(@NotNull VirtualFile file, @NotNull Project project);

  /**
   * Returns all elements that have been processed by a code generator to derive the given element.
   *
   * @param element the generated element
   * @return a list of original elements. An empty result indicates that the element is not considered to be generated by the filter.
   */
  public @NotNull List<? extends PsiElement> getOriginalElements(@NotNull PsiElement element) {
    return Collections.emptyList();
  }

  /**
   * The method is called only if {@link #isGeneratedSource} returns {@code true}.
   *
   * @return a text to be shown in the editor notification panel or {@code null} for the default text
   */
  public @NlsContexts.LinkLabel @Nullable String getNotificationText(@NotNull VirtualFile file, @NotNull Project project) {
    return null;
  }
}
