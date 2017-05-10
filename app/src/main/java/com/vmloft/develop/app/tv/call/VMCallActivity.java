package com.vmloft.develop.app.tv.call;

import android.os.Bundle;
import android.view.WindowManager;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.library.tools.tv.VMBaseTVActivity;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lzan13 on 2016/8/8.
 *
 * 通话界面的父类，做一些音视频通话的通用操作
 */
public class VMCallActivity extends VMBaseTVActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置通话界面属性，保持屏幕常亮，关闭输入法，以及解锁
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /**
     * 初始化界面方法，做一些界面的初始化操作
     */
    protected void initView() {
        activity = this;

        initCallPushProvider();

        if (VMCallManager.getInstance().getCallState() == VMCallManager.CallState.DISCONNECTED) {
            // 收到呼叫或者呼叫对方时初始化通话状态监听
            VMCallManager.getInstance().setCallState(VMCallManager.CallState.CONNECTING);
            VMCallManager.getInstance().registerCallStateListener();
            VMCallManager.getInstance().attemptPlayCallSound();

            // 如果不是对方打来的，就主动呼叫
            if (!VMCallManager.getInstance().isInComingCall()) {
                VMCallManager.getInstance().makeCall();
            }
        }
    }

    /**
     * 初始化通话推送提供者
     */
    private void initCallPushProvider() {
        VMCallPushProvider pushProvider = new VMCallPushProvider();
        EMClient.getInstance().callManager().setPushProvider(pushProvider);
    }


    /**
     * 挂断通话
     */
    protected void endCall() {
        VMCallManager.getInstance().endCall();
        onFinish();
    }

    /**
     * 拒绝通话
     */
    protected void rejectCall() {
        VMCallManager.getInstance().rejectCall();
        onFinish();
    }

    /**
     * 接听通话
     */
    protected void answerCall() {
        VMCallManager.getInstance().answerCall();
    }

    /**
     * 销毁界面时做一些自己的操作
     */
    @Override protected void onFinish() {
        super.onFinish();
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 拦截返回按键
     */
    @Override public void onBackPressed() {
        // super.onBackPressed();

    }

    @Override protected void onResume() {
        super.onResume();
        // 判断当前通话状态，如果已经挂断，则关闭通话界面
        if (VMCallManager.getInstance().getCallState() == VMCallManager.CallState.DISCONNECTED) {
            onFinish();
            return;
        }
    }
}
