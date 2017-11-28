package com.jd.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangyingjie1
 * @version: 1.0
 * @createdate: 2017-11-23 16:13
 */
public class MethodExtractUtils {

    public static final String SET_METHOD_NAME_PREFIX = "set";

    public static final String GET_METHOD_NAME_PREFIX = "get";

    @NotNull
    public static List<PsiMethod> extractSetMethods(PsiClass clazz) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isJDKClazz(clazz)) {
            addSetMethodToList(clazz, methodList);
            // get super class recursion(递归)
            clazz = clazz.getSuperClass();
        }
        return methodList;
    }


    public static void addSetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidMethod(method, SET_METHOD_NAME_PREFIX)) {
                methodList.add(method);
            }
        }
    }

    @NotNull
    public static List<PsiMethod> extractGetMethods(PsiClass clazz) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isJDKClazz(clazz)) {
            addGetMethodToList(clazz, methodList);
            // get super class recursion(递归)
            clazz = clazz.getSuperClass();
        }
        return methodList;
    }

    public static void addGetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidMethod(method, GET_METHOD_NAME_PREFIX)) {
                methodList.add(method);
            }
        }
    }

    public static boolean isValidMethod(PsiMethod m, String methodNamePrefix) {
        return m.hasModifierProperty("public") && !m.hasModifierProperty("static") && m.getName().startsWith(methodNamePrefix);
    }

    public static boolean isJDKClazz(PsiClass clazz) {
        if (clazz == null) {
            return false;
        }
        String qualifiedName = clazz.getQualifiedName();
        // exclude java.util.*  and so on
        if (qualifiedName == null || qualifiedName.startsWith("java.")) {
            return false;
        }
        return true;
    }

    public static boolean existSetFieldMethod(PsiClass clazz) {
        while (isJDKClazz(clazz)) {
            for (PsiMethod m : clazz.getMethods()) {
                if (isValidMethod(m, SET_METHOD_NAME_PREFIX)) {
                    return true;
                }
            }
            clazz = clazz.getSuperClass();
        }
        return false;
    }

}