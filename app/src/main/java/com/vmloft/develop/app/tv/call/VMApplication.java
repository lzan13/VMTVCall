package com.vmloft.develop.app.tv.call;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import com.vmloft.develop.library.tools.tv.utils.VMLog;
import java.util.Iterator;
import java.util.List;

/**
 * 项目入口
 * Created by lzan13 on 2017/3/14.
 */
public class VMApplication extends Application {

    private final String TAG = this.getClass().getSimpleName();

    private static Context context;

    private EMConnectionListener connectionListener;

    private VMCallReceiver callReceiver;

    private boolean isInit = false;

    @Override public void onCreate() {
        super.onCreate();
        context = this;
        // 初始化环信sdk
        initHyphenate();
    }

    /**
     * 初始化环信sdk，并做一些注册监听的操作
     */
    private void initHyphenate() {
        // 获取当前进程 id 并取得进程名
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            // 则此application的onCreate 是被service 调用的，直接返回
            return;
        }
        if (isInit) {
            return;
        }
        // 初始化sdk的一些配置
        EMOptions options = new EMOptions();
        options.setAutoLogin(true);
        options.setSortMessageByServerTime(false);
        // 初始化环信SDK,一定要先调用init()
        EMClient.getInstance().init(context, options);

        // 开启 debug 模式
        EMClient.getInstance().setDebugMode(true);

        // 通话管理类的初始化
        VMCallManager.getInstance().init(context);

        // 初始化通话广播监听
        initCallListener();
        // 初始化链接监听
        initConnectListener();
    }

    /**
     * 初始化通话监听
     */
    private void initCallListener() {
        // 设置通话广播监听器
        IntentFilter callFilter = new IntentFilter(
                EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new VMCallReceiver();
        }
        //注册通话广播接收者
        context.registerReceiver(callReceiver, callFilter);
    }

    private void initConnectListener() {
        /**
         * Created by lzan13 on 2016/10/26.
         * 链接监听详细处理类
         */
        connectionListener = new EMConnectionListener() {

            /**
             * 链接聊天服务器成功
             */
            @Override public void onConnected() {
                VMLog.d("onConnected");
            }

            /**
             * 链接聊天服务器失败
             *
             * @param errorCode 连接失败错误码
             */
            @Override public void onDisconnected(final int errorCode) {
                VMLog.d("onDisconnected - " + errorCode);
                if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    VMLog.d("user login another device - " + errorCode);
                } else if (errorCode == EMError.USER_REMOVED) {
                    VMLog.d("user be removed - " + errorCode);
                } else {
                    VMLog.d("con't servers - " + errorCode);
                }
            }
        };
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 根据Pid获取当前进程的名字，一般就是当前app的包名
     *
     * @param pid 进程的id
     * @return 返回进程的名字
     */
    private String getAppName(int pid) {
        String processName = null;
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info =
                    (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    // 根据进程的信息获取当前进程的名字
                    processName = info.processName;
                    // 返回当前进程名
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有匹配的项，返回为null
        return null;
    }
}
