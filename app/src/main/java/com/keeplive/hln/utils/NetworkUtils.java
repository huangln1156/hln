package com.keeplive.hln.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.keeplive.hln.network.NetType;

import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;


public class NetworkUtils {
    /**
     * 判断当前网络状态是否可用
     * @param context
     * @return
     */
    public static boolean isWifiProxy(Context context) {
        String proxyAddress;
        int proxyPort;
        proxyAddress = System.getProperty("http.proxyHost");
        String portStr = System.getProperty("http.proxyPort");
        proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr == null){
            return false;
        }
        NetworkInfo[] info = connMgr.getAllNetworkInfo();
        for(NetworkInfo info0 : info){
            if(info0.getState() == NetworkInfo.State.CONNECTED){
                return true;
            }
        }
        return false;
    }

    public static NetType getNetType(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr == null){
            return NetType.NONE;
        }
        //获取当前激活的网络连接状态
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null){
            return NetType.NONE;
        }

        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_MOBILE){
            return NetType.MOBILE;
        }else if(nType == ConnectivityManager.TYPE_WIFI){
            return NetType.WIFI;
        }
        return NetType.NONE;
    }
    public static boolean isSMI5GConnected(ServiceState serviceState) {
        if (serviceState == null) {
            return false;
        }
        Log.e(Constants.TAG,"5Gtype1");
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Log.e(Constants.TAG,"5Gtype2");
                Method hwOwnMethod = ServiceState.class.getMethod("getHwNetworkType");
                hwOwnMethod.setAccessible(true);
                int result = (int) hwOwnMethod.invoke(serviceState);

                if (result == 20) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    public static boolean is5GConnected(Context context) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            ServiceState serviceState = telephonyManager.getServiceState();
            try {
                Method hwOwnMethod = ServiceState.class.getMethod("getHwNetworkType");
                hwOwnMethod.setAccessible(true);
                int result = (int) hwOwnMethod.invoke(serviceState);

                if (result == 20) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    /**
     * 获取当前网络连接的类型
     *
     * @param context context
     * @return int
     */
    public static NetworkUtil.NetType getNetworkState(Context context) {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
        if (null == connManager) { // 为空则认为无网络
            return NetworkUtil.NetType.NETWORK_NONE;
        }
        // 获取网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NetworkUtil.NetType.NETWORK_NONE;
        }
        // 判断是否为WIFI
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NetworkUtil.NetType.NETWORK_WIFI;
                }
            }
        }
        // 若不是WIFI，则去判断是2G、3G、4G网
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        int networkType = telephonyManager.getNetworkType();
        boolean gConnected = is5GConnected(context);
        if (gConnected) {
            return NetworkUtil.NetType.NETWORK_5G;
        }
        boolean common5GConnected = isCommon5GConnected(context);
        if (common5GConnected) {
            return NetworkUtil.NetType.NETWORK_5G;
        }

        //boolean nrConnected = isNRConnected(telephonyManager);
        //if (nrConnected) {
        //  return NetType.NETWORK_5G;
        //}
        switch (networkType) {
            /*
             GPRS : 2G(2.5) General Packet Radia Service 114kbps
             EDGE : 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
             UMTS : 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
             CDMA : 2G 电信 Code Division Multiple Access 码分多址
             EVDO_0 : 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
             EVDO_A : 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
             1xRTT : 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
             HSDPA : 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
             HSUPA : 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
             HSPA : 3G (分HSDPA,HSUPA) High Speed Packet Access
             IDEN : 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
             EVDO_B : 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
             LTE : 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
             EHRPD : 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
             HSPAP : 3G HSPAP 比 HSDPA 快些
             */
            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetworkUtil.NetType.NETWORK_2G;
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetworkUtil.NetType.NETWORK_3G;
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetworkUtil.NetType.NETWORK_4G;
            case TelephonyManager.NETWORK_TYPE_NR:
                return NetworkUtil.NetType.NETWORK_5G;
            default:
                return NetworkUtil.NetType.NETWORK_UNKNOWN;
        }
    }
    private static boolean isCommon5GConnected(Context context) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            ServiceState serviceState = telephonyManager.getServiceState();
            try {
                Method getNrStateMethod = ServiceState.class.getMethod("getNrState");
                getNrStateMethod.setAccessible(true);
                int result = (int) getNrStateMethod.invoke(serviceState);

                if (result == 3 || result == 2) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    /**
     * 打开网络设置页面
     */
    public static void openSetting(Activity context, int requestCode){
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.View");
        context.startActivityForResult(intent,requestCode);
    }
}
