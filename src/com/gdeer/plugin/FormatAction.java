package com.gdeer.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;

import java.util.Arrays;

public class FormatAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取编辑器model
        final Editor mEditor = e.getData(PlatformDataKeys.EDITOR);

        final Project project = e.getProject();
        if (null == mEditor || project == null) {
            return;
        }

        // 获取代码内容
        final Document document = mEditor.getDocument();
        // 获取光标model
        final CaretModel caretModel = mEditor.getCaretModel();
        // 获取选中区域model
        final SelectionModel selectionModel = mEditor.getSelectionModel();

        String s = selectionModel.getSelectedText();
        if (s == null || s.length() == 0) {
            System.out.println("no selection");
            return;
        }

        // 找到最远的等号的 index
        String[] lines = s.split("\n");
        int maxIndexOfEqual = -1;
        for (String line : lines) {
            int indexOfEqual = line.indexOf("=");
            if (indexOfEqual > maxIndexOfEqual) {
                maxIndexOfEqual = indexOfEqual;
            }
        }

        if (maxIndexOfEqual < 0) {
            System.out.println("no equal sign");
            return;
        }

        // 给每一行的等号前添加空格
        StringBuilder newDocumentBuilder = new StringBuilder();
        for (String line : lines) {
            String newLine = line;
            int indexOfEqual = line.indexOf("=");
            if (indexOfEqual > 0 && indexOfEqual < maxIndexOfEqual) {
                String lineHead = line.substring(0, indexOfEqual);
                String lineEnd = line.substring(indexOfEqual, line.length());

                int blankCount = maxIndexOfEqual - indexOfEqual;
                StringBuilder lineMidBuilder = new StringBuilder();
                for (int i = 0; i < blankCount; i++) {
                    lineMidBuilder.append(" ");
                }
                String lineMid = lineMidBuilder.toString();

                newLine = lineHead + lineMid + lineEnd;
            }
            newDocumentBuilder.append(newLine).append('\n');
        }

        // 替换旧代码
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(start, end, newDocumentBuilder.toString());
        });
    }
}
