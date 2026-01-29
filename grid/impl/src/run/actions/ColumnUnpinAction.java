// Copyright 2000-2026 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.database.run.actions;

import com.intellij.database.DataGridBundle;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.database.datagrid.GridColumn;
import com.intellij.database.datagrid.HierarchicalColumnsDataGridModel.HierarchicalGridColumn;
import com.intellij.database.datagrid.ModelIndex;
import com.intellij.database.datagrid.ModelIndexSet;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Unpins selected columns.
 */
public class ColumnUnpinAction extends ColumnHeaderActionBase {

  public ColumnUnpinAction() {
    super(true);
    getTemplatePresentation().setIcon(AllIcons.General.Pin_tab);
  }

  @Override
  protected void update(AnActionEvent e, @NotNull DataGrid grid, @NotNull ModelIndexSet<GridColumn> columnIdxs) {
    super.update(e, grid, columnIdxs);
    e.getPresentation().setEnabledAndVisible(!columnIdxs.isEmpty() && !isHierarchical(columnIdxs, grid));
    if (columnIdxs.size() == 1) {
      e.getPresentation().setText(DataGridBundle.message("action.Console.TableResult.UnpinColumn.text"));
    }
    else {
      e.getPresentation().setText(DataGridBundle.message("action.Console.TableResult.UnpinColumns.text"));
    }
  }

  @Override
  protected void actionPerformed(AnActionEvent e, @NotNull DataGrid grid, @NotNull ModelIndexSet<GridColumn> columnIdxs) {
    for (ModelIndex<GridColumn> columnIdx : columnIdxs.asIterable()) {
      grid.getAppearance().pinColumn(columnIdx, false);
    }
  }

  private static boolean isHierarchical(@NotNull ModelIndexSet<GridColumn> indices, @NotNull DataGrid grid) {
    for (ModelIndex<GridColumn> idx : indices.asIterable()) {
      GridColumn column = grid.getColumn(idx);
      if (column instanceof HierarchicalGridColumn) return true;
    }
    return false;
  }
}

