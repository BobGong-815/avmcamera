package com.autochips.avm.viewmode;

import android.content.Context;
import android.os.CountDownTimer;

import androidx.lifecycle.MutableLiveData;

import com.autochips.avm.helper.CameraViewModelHelper;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.utils.KLog;

public class RearviewMirrorModel extends BaseCameraViewModel {
    private Context mContext;
    private MutableLiveData<Boolean> liveDataCloseUI;

    public RearviewMirrorModel(Context context) {
        this.mContext = context;
        liveDataCloseUI = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getLiveDataCloseUI() {
        return liveDataCloseUI;
    }

    private CloseTimer closeTimer = new CloseTimer(5 * 1000, 2000);

    public void startTimer() {
        closeTimer.cancel();
        closeTimer.start();

    }
    //重置消失时间
    public BindingCommand pRearviewClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d("重置消失时间");
            startTimer();
        }
    });

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

    /**
     * 是否在操作
     *
     * @param running
     */
    public void setRunning(boolean running) {
        CameraViewModelHelper.getInstance().setRunning(running);
    }
}
