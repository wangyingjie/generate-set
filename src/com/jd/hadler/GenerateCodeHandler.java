package com.jd.hadler;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author: wangyingjie1
 * @version: 1.0
 * @createdate: 2017-11-28 13:14
 */
public interface GenerateCodeHandler {

    /**
     * a generate code
     *
     * @param project
     * @param editor
     * @param element
     */
    void generateCode(@NotNull Project project, Editor editor, @NotNull PsiElement element);


}
