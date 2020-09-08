package com.fix.hln_hotfix.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static void copyFile(File sourceFile,File targetFile) throws IOException {
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len=inBuff.read(b))!=-1){
            outBuff.write(b,0,len);
        }
        outBuff.flush();
        input.close();
        inBuff.close();
        output.close();
        outBuff.close();
    }
}
