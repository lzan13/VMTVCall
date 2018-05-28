package com.vmloft.develop.app.tv.call;

import android.hardware.Camera;
import com.hyphenate.chat.EMCallManager;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2016/8/9.
 * 处理视频通话过程中摄像头回调数据
 */
public class VMCameraDataProcessor implements EMCallManager.EMCameraDataProcessor {

    byte yDelta = 0;

    synchronized void setYDelta(byte yDelta) {
        VMLog.d("brigntness uDelta:" + yDelta);
        this.yDelta = yDelta;
    }

    /**
     * data size is width*height*2
     * the first width*height is Y, second part is UV
     * the storage layout detailed please refer 2.x demo CameraHelper.onPreviewFrame
     *
     * @param bytes 回调的数据
     * @param camera 相机对象
     * @param width 采集画面的宽
     * @param height 采集画面的高
     * @param rotateAngle 画面旋转角度
     */
    @Override public void onProcessData(byte[] bytes, Camera camera, int width, int height,
            int rotateAngle) {
        //MLLog.i("width: %d, height: %d, rotateAngle: %d", width, height, rotateAngle);
        
        int wh = width * height;
        for (int i = 0; i < wh; i++) {
            int d = (bytes[i] & 0xFF) + yDelta;
            d = d < 16 ? 16 : d;
            d = d > 235 ? 235 : d;
            bytes[i] = (byte) d;
        }
    }
}
