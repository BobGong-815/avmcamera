package com.autochips.avm.info;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.autochips.avm.BR;


/**
 *  主页面相关按钮事件变化定义得值
 */
public class CameraInfo extends BaseObservable {
    private  boolean sHow2dView = false;// 是否显示3d页面上的按钮
    private  boolean sHow3dView = false;// 是否显示3d页面的按钮
    private  boolean showCaliDemo = false;
    private  boolean showCaliView = false; // 显示自动标定
    private  String showCaliBtnText ="自动标定中,请勿关闭...."; // 显示标定完成后显示的文字
    private  String versionName ; //

    private  boolean isShowRadarBtn = false ; // 是否显示雷达按钮

    private String radarDistance =""; // 雷达距离显示



//,default=标定中
    public boolean issHow2dView() {
        return sHow2dView;
    }

    public void setsHow2dView(boolean sHow2dView) {
        this.sHow2dView = sHow2dView;
    }

    public boolean issHow3dView() {
        return sHow3dView;
    }

    public void setsHow3dView(boolean sHow3dView) {
        this.sHow3dView = sHow3dView;
    }

    @Bindable
    public boolean isShowCaliDemo() {
        return showCaliDemo;
    }

    public void setShowCaliDemo(boolean showCaliDemo) {
        this.showCaliDemo = showCaliDemo;
        notifyPropertyChanged(BR.showCaliDemo);


    }

    @Bindable
    public boolean isShowCaliView() {
        return showCaliView;
    }

    public void setShowCaliView(boolean showCaliView) {
        this.showCaliView = showCaliView;
       notifyPropertyChanged(BR.showCaliView);
    }

    @Bindable
    public String getShowCaliBtnText() {
        return showCaliBtnText;
    }

    public void setShowCaliBtnText(String showCaliBtnText) {
        this.showCaliBtnText = showCaliBtnText;
        notifyPropertyChanged(BR.showCaliBtnText);
    }

    @Bindable
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
        notifyPropertyChanged(BR.versionName);
    }

    @Bindable
    public boolean isShowRadarBtn() {
        return isShowRadarBtn;
    }

    public void setShowRadarBtn(boolean showRadarBtn) {
        isShowRadarBtn = showRadarBtn;
        notifyPropertyChanged(BR.showRadarBtn);
    }

    @Bindable
    public String getRadarDistance() {
        return radarDistance;
    }

    public void setRadarDistance(String radarDistance) {
        this.radarDistance = radarDistance;
        notifyPropertyChanged(BR.radarDistance);
    }
}
