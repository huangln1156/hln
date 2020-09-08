package com.fix.hln_hotfix;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.fix.hln_hotfix.constant.Constants;
import com.fix.hln_hotfix.utils.FileDexUtils;
import com.fix.hln_hotfix.utils.FileUtils;
import com.fix.hln_hotfix.utils.ParamSort;

import java.io.File;
import java.io.IOException;

public class SecondActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        String s = getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath() +
                File.separator + Constants.DEX_NAME;
        String s1 = Environment.getExternalStorageDirectory() + Constants.DEX_NAME;

        Log.d("SecondActivity", s);
        Log.d("SecondActivity", s1);
    }

    public void fix(View view) {
        File sourceFile = new File(Environment.getExternalStorageDirectory(), Constants.DEX_NAME);
        File targetFile = new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath() +
                File.separator + Constants.DEX_NAME);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        try {
            FileUtils.copyFile(sourceFile, targetFile);
            FileDexUtils.loadFixedDex(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bug(View view) {
        ParamSort paramSort = new ParamSort();
        paramSort.math(this);
    }
}
