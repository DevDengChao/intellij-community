package com.intellij.database.datagrid;

import com.intellij.database.settings.DataGridAppearanceSettings;
import org.jetbrains.annotations.NotNull;

import com.intellij.database.datagrid.GridColumn;
import com.intellij.database.datagrid.ModelIndex;

public interface DataGridAppearance {
  void setResultViewVisibleRowCount(int v);

  void setResultViewShowRowNumbers(boolean v);

  void setTransparentColumnHeaderBackground(boolean v);

  void setTransparentRowHeaderBackground(boolean v);

  void setResultViewAdditionalRowsCount(int v);

  void setResultViewSetShowHorizontalLines(boolean v);

  void setResultViewStriped(boolean v);

  void setBooleanMode(@NotNull DataGridAppearanceSettings.BooleanMode v);

  @NotNull DataGridAppearanceSettings.BooleanMode getBooleanMode();

  void setResultViewAllowMultilineColumnLabels(boolean v);

  void setAnonymousColumnName(@NotNull String name);

  void addSpaceForHorizontalScrollbar(boolean v);

  void expandMultilineRows(boolean v);

  default void setHoveredRowBgHighlightingEnabled(boolean v) {}

  /**
   * Pin or unpin a column. When pinned, the column should stay on the left while scrolling.
   */
  default void pinColumn(@NotNull ModelIndex<GridColumn> columnIdx, boolean pinned) {}
}
