package com.keeplive.hln.network;

import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.net.Network;

import com.keeplive.hln.network.annotation.NetworkAnnotation;
import com.keeplive.hln.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
    private static final String TAG = "NetworkCallbackImpl";
    private Map<Object, List<MethodManager>> networkMapList = new HashMap<>();
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.e(Constants.TAG,"NetworkCallbackImpl:网络连接了");

    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        post(NetType.NONE);
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);

    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)){
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                post(NetType.WIFI);
            }else {
                post(NetType.MOBILE);
            }
        }
    }

    /**
     * 通知所有注册的方法，网络发生了改变
     * @param netType
     * 父类.class.isAssignableFrom(子类.class)
    子类实例 instanceof 父类类型
     */
    private void post(NetType netType) {
        Set<Object> sets = networkMapList.keySet();
        for (Object observer : sets) {
            List<MethodManager> methodList = networkMapList.get(observer);
            for (MethodManager method : methodList) {
                if (method.getType().isAssignableFrom(netType.getClass())) {
                    if (method.getNetType() == netType ||
                            netType == NetType.NONE ||
                            method.getNetType() == NetType.AUTO) {
                        invoke(method, observer, netType);
                    }
                }
            }
        }
    }

    private void invoke(MethodManager methodManager, Object observer, NetType netType) {
        try {
            Method execute = methodManager.getMethod();
            execute.invoke(observer, netType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册监听
     * @param observer
     */
    public void registerObserver(Object observer) {
        List<MethodManager> methodList = networkMapList.get(observer);
        if (methodList == null) {
            methodList = getAnnotationMethod(observer);
            networkMapList.put(observer, methodList);
        }
    }

    /**
     * 遍历注册类中的所有方法，收集被注解方法的信息
     * @param observer
     * @return
     */
    private List<MethodManager> getAnnotationMethod(Object observer) {
        List<MethodManager> methodList = new ArrayList<>();
        Method[] methods = observer.getClass().getMethods();
        for (Method method : methods) {
            NetworkAnnotation network = method.getAnnotation(NetworkAnnotation.class);
            if (network == null) {
                continue;
            }
            Log.e(Constants.TAG,"NetworkCallbackImpl:NETWORK.....");
            //校验返回值
            Type returnType = method.getGenericReturnType();
            if (!"void".equals(returnType.toString())) {
                throw new RuntimeException(method.getName() + "return type should be null");
            }
            //校验参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new RuntimeException(method.getName() + "arguments should be one");
            }

            MethodManager methodManager = new MethodManager(parameterTypes[0],
                    network.netType(), method);
            methodList.add(methodManager);
        }
        return methodList;
    }

    public void unRegisterObserver(Object observer) {
        if (!networkMapList.isEmpty()) {
            networkMapList.remove(observer);
        }
    }

    public void unRegisterAllObserver() {
        if (!networkMapList.isEmpty()) {
            networkMapList.clear();
        }
        NetworkManager.getInstance().getConnectivityManager().unregisterNetworkCallback(this);
        networkMapList = null;
    }
}
