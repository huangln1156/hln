package com.fix.hln_hotfix.utils;

import java.lang.reflect.Array;

public class ArrayUtils {
    /**
     * 合并数组
     * @param arratLhs  前数组（插队数组）
     * @param arrayPhs  后数组（已有数组）
     * @return 处理后的新数组
     */
    public static Object combineArray(Object arratLhs, Object arrayPhs) {
        //获得一个数组的Class对象，通过Array.newInstance()可以反射生成数组对象
        Class<?> loacalClass = arratLhs.getClass().getComponentType();
        //插队的数组长度
        int i = Array.getLength(arratLhs);
        //新数组的总长度=前数组长度+后数组长度
        int j = i + Array.getLength(arrayPhs);
        //生成数组对象
        Object result = Array.newInstance(loacalClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                //从零开始遍历，如果前数组有值，添加到新数组的第一个位置
                Array.set(result, k, Array.get(arratLhs, k));
            } else {
                //添加完前数组，再添加后数组，合并完成
                Array.set(result, k, Array.get(arratLhs, k - i));
            }

        }
        return result;
    }
}
