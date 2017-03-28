/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vmloft.develop.app.tv.call;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.library.tools.tv.VMBaseTVActivity;
import com.vmloft.develop.library.tools.tv.utils.VMLog;

/**
 * 主界面，直接显示呼叫拨号盘，以及历史呼叫人员
 */
public class VMMainActivity extends VMBaseTVActivity {

    private String TAG = this.getClass().getSimpleName();

    private Activity activity;

    private ProgressDialog dialog;

    private String localAccount;
    private String remoteAccount;

    @BindView(R.id.text_call_local) TextView localView;
    @BindView(R.id.text_call_remote) EditText remoteView;
    @BindView(R.id.btn_0) Button btn0;
    @BindView(R.id.btn_1) Button btn1;
    @BindView(R.id.btn_2) Button btn2;
    @BindView(R.id.btn_3) Button btn3;
    @BindView(R.id.btn_4) Button btn4;
    @BindView(R.id.btn_5) Button btn5;
    @BindView(R.id.btn_6) Button btn6;
    @BindView(R.id.btn_7) Button btn7;
    @BindView(R.id.btn_8) Button btn8;
    @BindView(R.id.btn_9) Button btn9;
    @BindView(R.id.btn_delete) ImageButton btnDelete;
    @BindView(R.id.btn_backspace) ImageButton btnBackspace;
    @BindView(R.id.btn_call) Button btnCall;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        activity = this;
        ButterKnife.bind(activity);

        // 默认焦点在 5 上
        btn5.setFocusable(true);

        // 判断是否已经成功登录过，否则调用 SDK 登录
        if (!EMClient.getInstance().isLoggedInBefore()) {
            signUp();
        } else {
            VMLog.i("已经登录，可以直接通话了");
            localAccount = EMClient.getInstance().getCurrentUser();
            localView.setText(String.format(getString(R.string.local_account), localAccount));
        }
    }

    @OnClick({
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6,
            R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_delete, R.id.btn_backspace, R.id.btn_call
    }) void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_0:
                inputNumber("0");
                break;
            case R.id.btn_1:
                inputNumber("1");
                break;
            case R.id.btn_2:
                inputNumber("2");
                break;
            case R.id.btn_3:
                inputNumber("3");
                break;
            case R.id.btn_4:
                inputNumber("4");
                break;
            case R.id.btn_5:
                inputNumber("5");
                break;
            case R.id.btn_6:
                inputNumber("6");
                break;
            case R.id.btn_7:
                inputNumber("7");
                break;
            case R.id.btn_8:
                inputNumber("8");
                break;
            case R.id.btn_9:
                inputNumber("9");
                break;
            case R.id.btn_delete:
                remoteView.setText("");
                break;
            case R.id.btn_backspace:
                backspaceNumber();
                break;
            case R.id.btn_call:
                makeCallVideo();
                break;
        }
    }

    /**
     * 插入字符
     */
    private void inputNumber(String string) {
        int index = remoteView.getSelectionStart();
        remoteView.getText().insert(index, string);
    }

    /**
     * 删除字符
     */
    private void backspaceNumber() {
        int index = remoteView.getSelectionStart();
        remoteView.getText().delete(index - 1, index);
    }

    /**
     * 视频呼叫
     */
    private void makeCallVideo() {
        remoteAccount = remoteView.getText().toString().trim();
        if (TextUtils.isEmpty(remoteAccount)) {
            Toast.makeText(activity, "请输入对方账户", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(VMMainActivity.this, VMVideoCallActivity.class);
        VMCallManager.getInstance().setChatId(remoteAccount);
        VMCallManager.getInstance().setInComingCall(false);
        VMCallManager.getInstance().setCallType(VMCallManager.CallType.VIDEO);
        startActivity(intent);
    }

    /**
     * 注册账户，这里因为是 TV 项目，默认第一自动注册，注册逻辑这里采用随机账户，
     */
    private void signUp() {

        dialog = new ProgressDialog(activity);
        dialog.setMessage("正在初始化，请稍后...");
        dialog.show();

        new Thread(new Runnable() {
            @Override public void run() {
                int random = (int) (1000 + Math.random() * 1000);
                localAccount = String.valueOf(random);
                try {
                    EMClient.getInstance().createAccount(localAccount, "1");
                    // 注册成功去登录
                    signIn();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    switch (e.getErrorCode()) {
                        case EMError.USER_ALREADY_EXIST:
                            // 如果账户已存在，重新注册，确保注册成功
                            signUp();
                            break;
                    }
                }
            }
        }).start();
    }

    /**
     * 登录账户，这里采用自动注册登录方式，不需要后边再次登录
     */
    private void signIn() {
        EMClient.getInstance().login(localAccount, "1", new EMCallBack() {
            @Override public void onSuccess() {
                VMLog.d("onSuccess 可以通话了 ~");
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        dialog.dismiss();
                        localAccount = EMClient.getInstance().getCurrentUser();
                        localView.setText(String.format(getString(R.string.local_account), localAccount));
                    }
                });
            }

            @Override public void onError(int i, String s) {
                VMLog.d("onError %d, %s", i, s);
            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }
}
