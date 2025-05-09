package com.autochips.avm.viewmode;

import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_ACTIVATED_PANORAMA;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_ACTIVATES_PANORAMA;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_PATH_LINE;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_P_EXIT;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_TRANSPARENT_CHASSIS;

import android.content.Context;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.widget.CompoundButton;

import androidx.lifecycle.MutableLiveData;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.util.GlobalSetting;
import com.autochips.avm.util.SystemProperties;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.utils.KLog;

public class SettingViewModel extends BaseCameraViewModel {
    private Context mContext;
    private MutableLiveData<Boolean> liveDataCloseUI;
    private MutableLiveData<String> liveDataInfoUI;


    public SettingViewModel(Context context) {
        this.mContext = context;
        liveDataCloseUI = new MutableLiveData<>();
        liveDataInfoUI = new MutableLiveData<>();
    }

    private CloseTimer   closeTimer = new CloseTimer(5 * 1000, 2000);

    public MutableLiveData<Boolean> getLiveDataCloseUI() {
        return liveDataCloseUI;
    }
    public MutableLiveData<String> getLiveDataPUI() {
        return liveDataInfoUI;
    }

    public void startTimer() {
        closeTimer.cancel();
        closeTimer.start();

    }

    public void cancelTimer() {
        closeTimer.cancel();
    }

    /**
     * 设置列表关闭倒计时
     */
    private class CloseTimer extends CountDownTimer {

        public CloseTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            KLog.d("自定关闭设置页面");
            liveDataCloseUI.postValue(true);
        }
    }

    //重置消失时间
    public BindingCommand pSettingClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("重置消失时间");
            startTimer();
        }
    });

    //跳转P档infobook
    public BindingCommand pInfoClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("P档说明");
            setRunning(true);
            liveDataInfoUI.postValue(ACTION_INFO_P_EXIT);
            startTimer();
        }
    });
    //跳转透明底盘infobook
    public BindingCommand transparentChassisInfoClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("透明底盘档说明");
            setRunning(true);
            liveDataInfoUI.postValue(ACTION_INFO_TRANSPARENT_CHASSIS);
            startTimer();
        }
    });
    //跳转雷达激活全景infobook
    public BindingCommand activatedPanoramaInfoClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("雷达激活全景");
            setRunning(true);
            liveDataInfoUI.postValue(ACTION_INFO_ACTIVATED_PANORAMA);
            startTimer();
        }
    });
    //跳转轨迹线infobook
    public BindingCommand pathLineInfoClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("轨迹线");
            setRunning(true);
            liveDataInfoUI.postValue(ACTION_INFO_PATH_LINE);
            startTimer();
        }
    });
    //跳转转向灯激活全景infobook
    public BindingCommand activatesPanoramaInfoClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("转向灯激活全景");
            setRunning(true);
            liveDataInfoUI.postValue(ACTION_INFO_ACTIVATES_PANORAMA);
            startTimer();
        }
    });
    //轨迹线开关
    public void switchPanorama(CompoundButton button, boolean isChecked) {
        KLog.d("轨迹线开关: = "+isChecked);
        setRunning(true);
        startTimer();
        if(isChecked){
            Settings.Global.putInt(mContext.getContentResolver(), GlobalSetting.AVM_SETTING_TRAJECTORY, 1); // SystemProperties.set("settingPathLine","1");
            bvavmJNI.bwSetTrajLineStatus((byte)1);
        }else{
            Settings.Global.putInt(mContext.getContentResolver(), GlobalSetting.AVM_SETTING_TRAJECTORY, 0); // SystemProperties.set("settingPathLine","0");
            bvavmJNI.bwSetTrajLineStatus((byte)0);
        }

    }
    //雷达激活全景开关
    public void switchActivatedPanorama(CompoundButton button, boolean isChecked) {
        KLog.d("雷达激活全景: = "+isChecked);
        setRunning(true);
        startTimer();
        if(isChecked){

            SystemProperties.set("activatedPanorama","1");
        }else{
            SystemProperties.set("activatedPanorama","2");
        }

    }
    //转向灯激活全景开关
    public void switchSignalActivates(CompoundButton button, boolean isChecked) {
        KLog.d("转向灯激活全景开关: = "+isChecked);
        setRunning(true);
        startTimer();
        if(isChecked){
            SystemProperties.set("signalActivates","1");
        }else{
            SystemProperties.set("signalActivates","0");
        }

    }
    /**
     * 是否在操作
     *
     * @param running
     */
    public void setRunning(boolean running) {
        CameraViewModelHelper.getInstance().setRunning(running);
    }

}
