package com.jd;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import com.jd.register.ClassTypeRegistry;
import com.jd.utils.MethodExtractUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        if (psiParent instanceof PsiLocalVariable) {

            PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;

            if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
                return;
            }

            handleWithLocalVariable(psiLocal, project, psiLocal);

        } else if (psiParent instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiParent;
            if (method.getReturnType() == null) {
                return;
            }

            //  handleWithMethod(method, project, method);
        }

        // System.out.println("Hello world!");
    }


    private void handleWithLocalVariable(PsiLocalVariable localVariable, Project project, PsiElement element) {
        PsiElement parent1 = localVariable.getParent();
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
        String splitText = "";
        int cur = statementOffset;
        String text = document.getText(new TextRange(cur - 1, cur));
        while (text.equals(" ") || text.equals("\t")) {
            splitText = text + splitText;
            cur--;
            if (cur < 1) {
                break;
            }
            text = document.getText(new TextRange(cur - 1, cur));
        }
        splitText = "\n" + splitText;


        StringBuilder builder = new StringBuilder("");

        for (PsiMethod method : methodList) {
            PsiParameter[] parameters = method.getParameterList().getParameters();
            for (PsiParameter parameter : parameters) {
                String classType = parameter.getType().getCanonicalText();
                builder.append(splitText)
                        .append(generateName).append(".").append(method.getName()).append("(")
                        .append(ClassTypeRegistry.getDefaultValue(classType))
                        .append(");");
            }
        }

        document.insertString(statementOffset + parent1.getText().length(), builder.toString());

        return;
    }


    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {

        return true;
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
