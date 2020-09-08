package com.fix.hln_hotfix;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.fix.hln_hotfix.constant.Constants;
import com.fix.hln_hotfix.utils.FileDexUtils;
import com.fix.hln_hotfix.utils.FileUtils;
import com.fix.hln_hotfix.utils.ParamSort;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }

        }
    }

    /**
     * 7.0安装
     * @param apkPath
     */
    private void installApk(String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return;
        }

        Intent install = new Intent(Intent.ACTION_VIEW);
        // 调用系统自带安装环境
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.chually.newpos.fileprovider", apkFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        getApplicationContext().startActivity(install);
    }

    public void jum(View view) {
        startActivity(new Intent(MainActivity.this, SecondActivity.class));
    }
}
