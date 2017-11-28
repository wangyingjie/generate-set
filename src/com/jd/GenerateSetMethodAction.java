package com.jd;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import com.jd.hadler.GenerateCodeHandler;
import com.jd.registry.GenerateCodeHandlerRegistry;
import com.jd.utils.MethodExtractUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author: wangyingjie1
 * @version: 1.0
 * @createdate: 2017-11-23 14:48
 */
public class GenerateSetMethodAction extends PsiElementBaseIntentionAction {

    public static final String GENERATE_SET = "generate-set";

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class, PsiMethod.class);
        if (psiParent == null) {
            return;
        }

        String clazzName = psiParent.getClass().getName();

        Map<String, GenerateCodeHandler> handlerMap = GenerateCodeHandlerRegistry.getHandlerMap();
        GenerateCodeHandler generateCodeHandler = handlerMap.get(clazzName);

        if (generateCodeHandler == null) {
            return;
        }

        // generate code handler
        generateCodeHandler.generateCode(project, editor, element);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {

        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class, PsiMethod.class);
        if (psiParent == null) {
            return false;
        }

        PsiClass psiClass = null;
        if (psiParent instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
            if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
                return false;
            }
            // 取参数
            psiClass = PsiTypesUtil.getPsiClass(psiLocal.getType());
        }

        if (psiParent instanceof PsiMethod) {
            PsiMethodImpl psiMethod = (PsiMethodImpl) psiParent;

            PsiParameterList parameterList = psiMethod.getParameterList();

            // only support single parameter method
            if (parameterList == null || parameterList.getParametersCount() != 1) {
                return false;
            }
            PsiType returnType = psiMethod.getReturnType();
            // 取方法返回值类型
            psiClass = PsiTypesUtil.getPsiClass(returnType);
        }

        return MethodExtractUtils.existSetFieldMethod(psiClass);
    }


    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return GENERATE_SET;
    }

    @NotNull
    @Override
    public String getText() {
        return GENERATE_SET;
    }
}
