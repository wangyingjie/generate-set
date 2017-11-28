package com.jd.registry;

import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiLocalVariableImpl;
import com.jd.hadler.impl.GenerateCodeConvertHandler;
import com.jd.hadler.GenerateCodeHandler;
import com.jd.hadler.impl.GenerateCodeSetMethodHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyingjie1
 * @version: 1.0
 * @createdate: 2017-11-28 15:10
 */
public class GenerateCodeHandlerRegistry {

    private static final Map<String, GenerateCodeHandler> HANDLER_MAP = new HashMap<>();


    //com.intellij.psi.impl.source.tree.java.PsiLocalVariableImpl
    //com.intellij.psi.impl.source.PsiMethodImpl
    static {
        HANDLER_MAP.put(PsiLocalVariableImpl.class.getName(), new GenerateCodeSetMethodHandler());
        HANDLER_MAP.put(PsiMethodImpl.class.getName(), new GenerateCodeConvertHandler());
    }

    public static Map<String, GenerateCodeHandler> getHandlerMap() {
        return HANDLER_MAP;
    }

}