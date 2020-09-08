package com.fix.hln_hotfix.utils;

import java.lang.reflect.Field;

public class ReflectUtils {
    /**
     * * 通过反射获取对象，并设置私有可访问
     *
     * @param obj   该属性所属类对象
     * @param clazz 该属性所属类
     * @param field 属性名
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     */
    public static Object getFiled(Object obj, Class<?> clazz, String field)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field loadlField = clazz.getDeclaredField(field);
        loadlField.setAccessible(true);
        return loadlField.get(obj);
    }

    public static void setFiled(Object obj, Class<?> clazz, Object vaule)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field loadlField = clazz.getDeclaredField("dexElements");
        loadlField.setAccessible(true);
        loadlField.set(obj, vaule);
    }

    public static Object getpathList(Object baseDexClassLoader)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, ClassNotFoundException {
        return getFiled(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     *
     * @param paramObject PathList对象
     * @return DexElements 对象
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    public static Object getDexElements(Object paramObject)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, ClassNotFoundException {
        return getFiled(paramObject, paramObject.getClass(), "dexElements");
    }
}
