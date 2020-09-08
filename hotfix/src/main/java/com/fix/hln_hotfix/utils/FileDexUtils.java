package com.fix.hln_hotfix.utils;

import android.content.Context;

import com.fix.hln_hotfix.constant.Constants;

import java.io.File;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FileDexUtils {
    private static HashSet<File> loadedDex=new HashSet<>();
    static {
        loadedDex.clear();
    }
    public static void loadFixedDex(Context context){
        if (context==null)return;
        //dex文件目录
        File fileDir = context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE);
        //遍历私有目录中的所有文件
        File[] listFiles = fileDir.listFiles();
        for (File file : listFiles) {
            if (file.getName().endsWith(Constants.DEX_SUFFIX)
                    &&!"classes.dex".equals(file.getName())){
                loadedDex.add(file);
            }

        }
        createDexClassLoader(context,fileDir);
    }

    private static void createDexClassLoader(Context context, File fileDir) {
        //临时解压目录
        String optimizedDir = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        //创建该目录
        File fopt = new File(optimizedDir);
        if (!fopt.exists()){
            fopt.mkdirs();
        }
        for (File dex : loadedDex) {
            //初始化DexClassLoader(自有的)
            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath(), optimizedDir, null, context.getClassLoader());
            hotfix(classLoader,context);
        }
    }

    private static void hotfix(DexClassLoader classLoader, Context context) {
        //获取系统PathClassLoader
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        //获取自有的dexElementes数组
        try {
            //获取自有的dexElements数组
            Object selfElements = ReflectUtils.getDexElements(ReflectUtils.getpathList(classLoader));
            //获取系统的dexElements数组
            Object systemElements = ReflectUtils.getDexElements(ReflectUtils.getpathList(pathClassLoader));
            //合并成新的dexElements对象
            Object dexElements = ArrayUtils.combineArray(selfElements, systemElements);
            //获取系统的pathList对象
            Object systemPathList = ReflectUtils.getpathList(pathClassLoader);
            //重新赋值系统的pathList属性值--修改的dexElements数组（新合成的）
            ReflectUtils.setFiled(systemPathList,systemPathList.getClass(),dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
