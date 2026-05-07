// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.java.codeInsight;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.stickyLines.actions.StickyLinesDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.awt.datatransfer.DataFlavor;

/**
 * Tests for {@link com.intellij.openapi.editor.impl.stickyLines.actions.StickyLinesCopyNameAction}.
 * Verifies that element names (classes, methods, inner classes, fields) can be copied from sticky lines in Java files.
 */
public class StickyLinesCopyNameActionTest extends LightJavaCodeInsightFixtureTestCase {

  public void testCopyClassName() {
    myFixture.configureByText("MyClass.java", """
      public class MyClass {
        public void method() {}
      }
      """);
    // Offset points to the class name "MyClass" (starts at offset 13 after "public class ")
    int classNameOffset = myFixture.getEditor().getDocument().getText().indexOf("MyClass");
    performCopyNameAction(classNameOffset);
    assertEquals("MyClass", getClipboardContents());
  }

  public void testCopyMethodName() {
    myFixture.configureByText("MyClass.java", """
      public class MyClass {
        public void myMethod() {}
      }
      """);
    // Offset points to the method name "myMethod"
    int methodNameOffset = myFixture.getEditor().getDocument().getText().indexOf("myMethod");
    performCopyNameAction(methodNameOffset);
    assertEquals("myMethod", getClipboardContents());
  }

  public void testCopyInnerClassName() {
    myFixture.configureByText("Outer.java", """
      public class Outer {
        public static class Inner {
          public void innerMethod() {}
        }
      }
      """);
    // Offset points to the inner class name "Inner"
    int innerClassOffset = myFixture.getEditor().getDocument().getText().indexOf("Inner");
    performCopyNameAction(innerClassOffset);
    assertEquals("Inner", getClipboardContents());
  }

  public void testCopyPrivateMethodName() {
    myFixture.configureByText("MyClass.java", """
      public class MyClass {
        private void privateMethod() {
          System.out.println("private");
        }
      }
      """);
    int methodOffset = myFixture.getEditor().getDocument().getText().indexOf("privateMethod");
    performCopyNameAction(methodOffset);
    assertEquals("privateMethod", getClipboardContents());
  }

  public void testCopyFieldName() {
    myFixture.configureByText("MyClass.java", """
      public class MyClass {
        private String myField;
      }
      """);
    int fieldOffset = myFixture.getEditor().getDocument().getText().indexOf("myField");
    performCopyNameAction(fieldOffset);
    assertEquals("myField", getClipboardContents());
  }

  public void testActionDisabledForNoNamedElement() {
    myFixture.configureByText("MyClass.java", """
      public class MyClass {
        // This is a comment
      }
      """);
    // Offset points to the comment content where there's no named element
    int commentOffset = myFixture.getEditor().getDocument().getText().indexOf("This is a comment");
    
    AnAction action = ActionManager.getInstance().getAction("EditorStickyLinesCopyName");
    AnActionEvent event = createActionEvent(commentOffset);
    action.update(event);
    
    // The action should be disabled when there's no named element at the offset
    assertFalse("Action should be disabled for comments", event.getPresentation().isEnabledAndVisible());
  }

  private void performCopyNameAction(int offset) {
    AnAction action = ActionManager.getInstance().getAction("EditorStickyLinesCopyName");
    assertNotNull("EditorStickyLinesCopyName action should be registered", action);
    
    AnActionEvent event = createActionEvent(offset);
    action.actionPerformed(event);
  }

  private AnActionEvent createActionEvent(int offset) {
    Editor editor = myFixture.getEditor();
    PsiFile psiFile = myFixture.getFile();
    
    DataContext dataContext = SimpleDataContext.builder()
      .add(CommonDataKeys.PROJECT, getProject())
      .add(CommonDataKeys.EDITOR, editor)
      .add(CommonDataKeys.PSI_FILE, psiFile)
      .add(StickyLinesDataKeys.STICKY_LINE_OFFSET, offset)
      .build();
    
    return AnActionEvent.createFromDataContext(ActionPlaces.UNKNOWN, null, dataContext);
  }

  private String getClipboardContents() {
    try {
      var contents = CopyPasteManager.getInstance().getContents();
      if (contents == null) {
        fail("Clipboard contents is null");
        return null;
      }
      return (String) contents.getTransferData(DataFlavor.stringFlavor);
    }
    catch (Exception e) {
      fail("Failed to get clipboard contents: " + e.getMessage());
      return null;
    }
  }
}
