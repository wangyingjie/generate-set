package com.jd.hadler.impl;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.jd.hadler.GenerateCodeHandler;
import com.jd.registry.ClassTypeRegistry;
import com.jd.utils.MethodExtractUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author: wangyingjie1
 * @version: 1.0
 * @createdate: 2017-11-28 14:51
 */
public class GenerateCodeSetMethodHandler implements GenerateCodeHandler {

    @Override
    public void generateCode(@NotNull Project project, Editor editor, @NotNull PsiElement element) {

        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        if (psiParent == null) {
            return;
        }
        PsiLocalVariable localVariable = (PsiLocalVariable) psiParent;

        PsiElement parent1 = psiParent.getParent();
        if (!(parent1 instanceof PsiDeclarationStatement)) {
            return;
        }
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        String generateName = localVariable.getName();

        List<PsiMethod> methodList = MethodExtractUtils.extractSetMethods(psiClass);

        if (methodList.size() == 0) {
            return;
        }

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile containingFile = element.getContainingFile();
        Document document = psiDocumentManager.getDocument(containingFile);

        int statementOffset = parent1.getTextOffset();
        String blankText = "";
        int cur = statementOffset;
        String text = document.getText(new TextRange(cur - 1, cur));
        while (text.equals(" ") || text.equals("\t")) {
            blankText = text + blankText;
            cur--;
            if (cur < 1) {
                break;
            }
            text = document.getText(new TextRange(cur - 1, cur));
        }
        blankText = "\n" + blankText;

        StringBuilder builder = new StringBuilder("");

        for (PsiMethod method : methodList) {
            PsiParameter[] parameters = method.getParameterList().getParameters();
            for (PsiParameter parameter : parameters) {
                String classType = parameter.getType().getCanonicalText();
                builder.append(blankText)
                        .append(generateName).append(".").append(method.getName()).append("(")
                        .append(ClassTypeRegistry.getDefaultValue(classType))
                        .append(");");
            }
        }

        document.insertString(statementOffset + parent1.getText().length(), builder.toString());
    }
}