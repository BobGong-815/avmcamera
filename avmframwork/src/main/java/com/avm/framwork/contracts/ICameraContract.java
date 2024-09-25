package com.avm.framwork.contracts;


import android.content.Context;
import android.view.Surface;

public interface ICameraContract {
    interface ICameraPresenter {

        void setViewListener(ICameraView iCameraView);

        void removeViewListener(ICameraView iCameraView);

        //初始化
        void init(Context context);

        //切换视角
        void switchViewAngle();

        //设置相关数据
        void setDeviceValue(String key, int value);

        //设置相关数据
        void setDeviceValue(int value, String key);

        //获取相关数据
        int getDeviceValue(String key);

        //手动点击进入360、或应用内切换到摄像机界面
        void entryCameraPage();

        //传给EVS的surface
        boolean setSurface(Surface surface, int w, int h);

        //更新开关状态，包括转向开关、BSD开关、FCW开关
        void updateSwitchState();

        //用于BSD二级报警 str播报文字 传给语音
        void sendWarningSound(Context context, String str);

    }

    interface ICameraView {
        void onPageTypeChange();//工作模式

        void onCallbackBoor();//车门
        void onCallbackMessage(String key, int value);//操作错误码或其他

        void onEvsInfoCallback(String key, int value);//evs回调

        void onEventChange(String key, int value);//设置的开关状态改变
    }
}
