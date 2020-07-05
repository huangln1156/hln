package com.keeplive.hln.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.keeplive.hln.App;

import cn.lezhi.speedtest.R;
import cn.lezhi.speedtest.app.MyApplication;
import cn.lezhi.speedtest.util.log.LogUtil;
import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * author: liushuai.
 * date:   18-9-6 上午11:49
 * mail: 1462828919@qq.com
 */
public class NetworkUtil {

  public enum NetType {
    NETWORK_NONE(-1, "无网络", 0, R.drawable.ic_nonet_white),// 没有网络连接
    NETWORK_UNKNOWN(0, "未知", 0, R.drawable.ic_nonet_white),
    NETWORK_ETHERNET(1, "宽带", 0, R.drawable.ic_net_ethernet),
    NETWORK_WIFI(2, "Wi-Fi", 0, R.drawable.ic_wifi_white),// wifi连接
    NETWORK_2G(3, "2G", 0.3f, R.drawable.ic_net_2g),// 2G
    NETWORK_3G(4, "3G", 20, R.drawable.ic_net_3g),// 3G
    NETWORK_4G(5, "4G", 150, R.drawable.ic_net_4g),// 4G
    NETWORK_5G(6, "5G", 200, R.drawable.ic_net_diagnosis_5g);

    public int code;
    public String name;

    public float useData; //使用流量数据，单位MB
    public int iconRes;

    NetType(int code, String name, float useData, int iconRes) {
      this.code = code;
      this.name = name;
      this.useData = useData;
      this.iconRes = iconRes;
    }

    /**
     * 根据code生成Type
     */
    public static NetType fromCode(int code) {
      for (NetType netType : values()) {
        if (netType.code == code) {
          return netType;
        }
      }
      return NetType.NETWORK_UNKNOWN;
    }
  }

  /**
   * 检查是否有可用网络
   */
  public static boolean isNetworkConnected() {
    ConnectivityManager connectivityManager = (ConnectivityManager) App.applicationContext
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    return connectivityManager.getActiveNetworkInfo() != null;
  }

  /**
   * 获取运营商名字
   *
   * @param context context
   * @return int
   */
  public static String getOperatorName(Context context) {
    /*
     * getSimOperatorName()就可以直接获取到运营商的名字
     * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
     * IMSI相关链接：http://baike.baidu.com/item/imsi
     */
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
    // getSimOperatorName就可以直接获取到运营商的名字
    return telephonyManager != null ? telephonyManager.getSimOperatorName() : null;
  }

  /**
   * 获取当前网络连接的类型
   *
   * @param context context
   * @return int
   */
  public static NetType getNetworkState(Context context) {
    ConnectivityManager connManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
    if (null == connManager) { // 为空则认为无网络
      return NetType.NETWORK_NONE;
    }
    // 获取网络类型，如果为空，返回无网络
    NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
    if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
      return NetType.NETWORK_NONE;
    }
    // 判断是否为WIFI
    NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (null != wifiInfo) {
      NetworkInfo.State state = wifiInfo.getState();
      if (null != state) {
        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
          return NetType.NETWORK_WIFI;
        }
      }
    }
    // 若不是WIFI，则去判断是2G、3G、4G网
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
    int networkType = telephonyManager.getNetworkType();
    boolean gConnected = is5GConnected(context);
    if (gConnected) {
      return NetType.NETWORK_5G;
    }
    boolean common5GConnected = isCommon5GConnected(context);
    if (common5GConnected) {
      return NetType.NETWORK_5G;
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
        return NetType.NETWORK_2G;
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
        return NetType.NETWORK_3G;
      // 4G网络
      case TelephonyManager.NETWORK_TYPE_LTE:
        return NetType.NETWORK_4G;
      case TelephonyManager.NETWORK_TYPE_NR:
        return NetType.NETWORK_5G;
      default:
        return NetType.NETWORK_UNKNOWN;
    }
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

  public static boolean isSMI5GConnected(ServiceState serviceState) {
    if (serviceState == null) {
      return false;
    }
    LogUtil.d("5Gtype1");
    if (Build.VERSION.SDK_INT >= 29) {
      try {
        LogUtil.d("5Gtype2");
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
   * 判断是否wifi连接
   *
   * @param context context
   * @return true/false
   */
  public static boolean isWifiConnected(Context context) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if (networkInfo != null) {
        int networkInfoType = networkInfo.getType();
        if (networkInfoType == ConnectivityManager.TYPE_WIFI
            || networkInfoType == ConnectivityManager.TYPE_ETHERNET) {
          return networkInfo.isConnected();
        }
      }
    }
    return false;
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

  public static boolean isNRConnected(TelephonyManager telephonyManager) {
    try {
      Object obj = Class.forName(telephonyManager.getClass().getName())
          .getDeclaredMethod("getServiceState", new Class[0])
          .invoke(telephonyManager, new Object[0]);

      Method[] methods = Class.forName(obj.getClass().getName()).getDeclaredMethods();

      for (Method method : methods) {
        if (method.getName().equals("getNrStatus") || method.getName().equals("getNrState")) {
          method.setAccessible(true);
          return ((Integer) method.invoke(obj, new Object[0])).intValue() == 3;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
