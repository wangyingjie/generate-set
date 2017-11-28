package com.jd.registry;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangyingjie1
 * @version: 1.0
 * @createdate: 2017-11-23 15:59
 */
public class ClassTypeRegistry {

    private static final Map<String, String> CLASS_TYPE = new HashMap<>();

    static {
        registerClazzType("boolean", "false");
        registerClazzType("java.lang.Boolean", "false");
        registerClazzType("int", "0");
        registerClazzType("byte", "(byte)0");
        registerClazzType("java.lang.Byte", "(byte)0");
        registerClazzType("java.lang.Integer", "0");
        registerClazzType("java.lang.String", "\"\"");
        registerClazzType("java.math.BigDecimal", "new BigDecimal(\"0\")");

        registerClazzType("java.lang.Long", "0L");
        registerClazzType("long", "0L");
        registerClazzType("short", "0");
        registerClazzType("java.lang.Short", "0");
        registerClazzType("java.util.Date", "new Date()");
        registerClazzType("float", "0.0F");
        registerClazzType("java.lang.Float", "0.0F");
        registerClazzType("double", "0.0D");

        registerClazzType("java.lang.Double", "0.0D");
        registerClazzType("java.lang.Character", "\'\'");
        registerClazzType("char", "\'\'");
    }

    private static void registerClazzType(String clazzType, String defaultValue) {
        CLASS_TYPE.put(clazzType, defaultValue);
    }

    public static String getDefaultValue(String classType) {

        String defaultValue = CLASS_TYPE.get(classType);

        return StringUtils.isBlank(defaultValue) ? null : defaultValue;
    }

}