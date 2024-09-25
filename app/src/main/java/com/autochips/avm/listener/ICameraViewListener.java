package com.autochips.avm.listener;

/**
 * 摄像头界面接收的回调
 */
public interface ICameraViewListener {
    //关闭AVM首页
    void closeAvm();

    //控制设置显隐
    void setBtnSettingSelect(boolean btnSettingSelect);

    //后视镜弹窗显隐
    void setBtnRearMirrorSelect(boolean btnRearSelect);

    //标定view显隐
    void setCalibrationSelect(boolean btnCalibrationSelect);
}

