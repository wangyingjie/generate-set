package com.jd.hadler.impl;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.jd.hadler.GenerateCodeHandler;
import com.jd.utils.MethodExtractUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wangyingjie1
 * @version: 1.0
 * @createdate: 2017-11-28 14:51
 */
public class GenerateCodeConvertHandler implements GenerateCodeHandler {

    @Override
    public void generateCode(@NotNull Project project, Editor editor, @NotNull PsiElement element) {

        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (psiParent == null) {
            return;
        }

        PsiMethodImpl psiMethod = (PsiMethodImpl) psiParent;

        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();

        String sourceVariableName = "";

        // source propertyName -> Method
        Map<String, PsiMethod> map = new HashMap<>();
        for (PsiParameter p : psiParameters) {
            sourceVariableName = p.getName();
            PsiType sourceVariableType = p.getType();
            List<PsiMethod> methodList = MethodExtractUtils.extractGetMethods(PsiTypesUtil.getPsiClass(sourceVariableType));
            for (PsiMethod method : methodList) {
                map.put(method.getName().substring(3), method);
            }
            /// System.out.println("sourceVariableName=====>" + sourceVariableName);
        }

        PsiType returnType = psiMethod.getReturnType();
        if (returnType == null) {
            return;
        }

        String targetClazzName = returnType.getDeepComponentType().getPresentableText();

        PsiClass psiClass = PsiTypesUtil.getPsiClass(returnType);

        List<PsiMethod> methodList = MethodExtractUtils.extractSetMethods(psiClass);
        if (methodList.size() == 0) {
            return;
        }

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile containingFile = element.getContainingFile();
        Document document = psiDocumentManager.getDocument(containingFile);

        String blankText = "        ";
        StringBuilder builder = new StringBuilder("\n");

        // first char to lower case
        String targetVariableName = targetClazzName.substring(0, 1).toLowerCase() + targetClazzName.substring(1);

        builder.append(blankText)
                .append(targetClazzName).append(" " + targetVariableName).append(" = new ").append(targetClazzName).append("();\n");

        for (PsiMethod method : methodList) {
            builder.append(blankText).append(targetVariableName).append(".").append(method.getName()).append("(");
            String propertyName = method.getName().substring(3);
            if (map.get(propertyName) != null) {
                builder.append(sourceVariableName).append(".")
                        .append(map.get(propertyName).getName()).append("()");
            } else {
                builder.append("null");
            }
            builder.append(");\n");
        }

        builder.append(blankText).append("return ").append(targetVariableName).append(";");

        document.insertString(psiMethod.getBody().getTextOffset() + 1, builder.toString());
    }

}