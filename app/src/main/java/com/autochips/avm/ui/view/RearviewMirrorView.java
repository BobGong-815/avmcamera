package com.autochips.avm.ui.view;

import static android.hardware.automotive.vehicle.V2_0.VehicleProperty.SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.REARVIEW_MIRROR_ADJUSTMENT;
import android.app.UiModeManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.databinding.ViewRearviewMirrorBinding;
import com.autochips.avm.util.RearviewToast;
import com.autochips.avm.viewmode.RearviewMirrorModel;
import com.avm.framwork.helper.ThreadPoolUtil;
import com.avm.framwork.manager.CanManager;

import java.util.Arrays;

import me.goldze.mvvmhabit.utils.KLog;

public class RearviewMirrorView extends LinearLayout implements LifecycleOwner, View.OnClickListener {

    private LifecycleRegistry registry;
    private Context context;
    private ViewRearviewMirrorBinding rearviewMirrorBinding;
    private RearviewMirrorModel rearviewMirrorModel;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private OnVisibilityListener listener;

    public RearviewMirrorView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RearviewMirrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {

        registry = new LifecycleRegistry(this);
        if (rearviewMirrorBinding == null) {
            rearviewMirrorModel = new RearviewMirrorModel(context);
            rearviewMirrorBinding = DataBindingUtil.inflate(LayoutInflater.from(this.getContext()),
                    R.layout.view_rearview_mirror, this, false);
            rearviewMirrorBinding.setViewModel(rearviewMirrorModel);
            addView(rearviewMirrorBinding.getRoot());
            rearviewMirrorModel.getLiveDataCloseUI().observe(this, this::closeUI);

            rearviewMirrorBinding.llRearviewMirror.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    rearviewMirrorModel.startTimer();
                    return true;
                }
            });
        }
    }

    /**
     * 档位信息
     */
    public void gearInfo(int val) {
        setRearviewMirrorDownViewStatus(val);

    }

    /**
     * 后视镜下翻状态
     */
    public void rearMirrorFlipDown(int val) {
        KLog.d("reverseLight: "+val);
        setRearMirrorFlipDownStatus(val);
    }

    /**
     * 后视镜折叠状态
     */
    public void rearMirrorFold(int value) {
        if (value == 1) {
            rearviewMirrorBinding.swFold.setChecked(true);
        } else if (value == 2) {
            rearviewMirrorBinding.swFold.setChecked(false);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        KLog.d("BaseWorkStateManager onFinishInflate");
        post(this::initData);

    }

    private void initData() {
        // 后视镜下翻
        int reverseLightSts = CanManager.getInstance().getIntStatus(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 16777216);
        setRearviewMirrorDownViewStatus(reverseLightSts);
        KLog.d("initData reverseLightSts: "+reverseLightSts);
        rearMirrorFlipDown(reverseLightSts);

        // 设置点击事件
//        rearviewMirrorBinding.llSettingRearviewMirrorDown.setOnClickListener(this);
//        rearviewMirrorBinding.llFold.setOnClickListener(this);
        rearviewMirrorBinding.swRearviewMirrorDown.setOnClickListener(this);
        rearviewMirrorBinding.swFold.setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        KLog.d("RearviewMirrorView onAttachedToWindow");
        registry.setCurrentState(Lifecycle.State.STARTED);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KLog.d("RearviewMirrorView onDetachedFromWindow");
        registry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    private void closeUI(boolean b) {
        KLog.d("closeUI 设置页面");
        mHandler.post(() -> {
            this.setVisibility(GONE);
            if (listener != null) {
                listener.Visibility(true);
            }
        });
    }

    public void setListener(OnVisibilityListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View view) {
      KLog.d("后视镜11----");
        if (view.getId() == R.id.sw_rearview_mirror_down) {
//            if(CameraViewModelHelper.valGear != 3) {
//                KLog.d("is not R gear");
//                return;
//            }
            rearviewMirrorModel.startTimer();
            rearviewMirrorModel.setRunning(true);
            int reverseAutoMaticStatus = CanManager.getInstance().getIntStatus(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 16777216);
            // 外后视镜倒车下翻
            ThreadPoolUtil.getInstance().execute(() -> {
                KLog.d("后视镜11----当前reverseAutoMaticStatus：" + reverseAutoMaticStatus);
                CanManager.getInstance().setIntProperty(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 16777216, reverseAutoMaticStatus == 4 ? 1 : 4);
            });
            if(reverseAutoMaticStatus == 4){
                KLog.d("后视镜11----设置false 1");
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(false);
                rearviewMirrorBinding.swRearviewMirrorDown.setChecked(false);
                RearviewToast.getInstance().showToast("后视镜下翻已关闭");
                DataManager.writeFault(DataConstant.Code.REARM_CLOSE);
            }else{
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(true);
                rearviewMirrorBinding.swRearviewMirrorDown.setChecked(true);
                RearviewToast.getInstance().showToast("后视镜下翻已开启");
                DataManager.writeFault(DataConstant.Code.REARM_OPEN);
            }
        } else if (view.getId() == R.id.sw_fold) {//折叠
            rearviewMirrorModel.startTimer();
            rearviewMirrorModel.setRunning(true);
            if(view.getId() == R.id.sw_fold) {
                if (rearviewMirrorBinding.swFold.isChecked()) {
                    Integer[] arrFold = {1, 9};
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrFold);
                    RearviewToast.getInstance().showToast("后视镜已折叠");
                } else {
                    Integer[] arrUnfold = {1, 10};
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrUnfold);
                    RearviewToast.getInstance().showToast("后视镜已展开");
                }
            }else {
                if (rearviewMirrorBinding.swFold.isChecked()) {
                    rearviewMirrorBinding.swFold.setChecked(false);
                    Integer[] arrUnfold = {1, 10};
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrUnfold);
                    RearviewToast.getInstance().showToast("后视镜已展开");
                } else {
                    rearviewMirrorBinding.swFold.setChecked(true);
                    Integer[] arrFold = {1, 9};
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrFold);
                    RearviewToast.getInstance().showToast("后视镜已折叠");
                }
            }
        }

    }

    public void changeShow(float speedValue) {
        boolean isEnable = speedValue <= 15;//可以点击
        if(isEnable != rearviewMirrorBinding.llFold.isEnabled()){
            rearviewMirrorBinding.llFold.setAlpha(isEnable ? 1.0f : 0.3f);
            rearviewMirrorBinding.llFold.setEnabled(isEnable);
            rearviewMirrorBinding.swFold.setEnabled(isEnable);
        }
        if(isEnable != rearviewMirrorBinding.llSettingRearviewMirrorDown.isEnabled()){
            rearviewMirrorBinding.llSettingRearviewMirrorDown.setAlpha(isEnable ? 1.0f : 0.3f);
            rearviewMirrorBinding.llSettingRearviewMirrorDown.setEnabled(isEnable);
            rearviewMirrorBinding.swRearviewMirrorDown.setEnabled(isEnable);
        }
    }

    public interface OnVisibilityListener {
        void Visibility(boolean isVisibility);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            rearviewMirrorModel.startTimer();
            int[] mirrorArr = CanManager.getInstance().getIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0);
            KLog.v("后视镜折叠展开状态：mirrorArr.length:"+ mirrorArr.length +" string:"+ Arrays.toString(mirrorArr));
            if(mirrorArr.length > 1) {
                rearviewMirrorBinding.swFold.setChecked(mirrorArr[1] == 9);
            }
        }
        rearviewMirrorBinding.llSettingRearviewMirrorDown.setVisibility(AvmApp.OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH==1?VISIBLE:GONE);
        rearviewMirrorBinding.llFold.setVisibility(AvmApp.OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH==1?VISIBLE:GONE);
    }

    private void setRearMirrorFlipDownStatus(int reverseLightSts) {
//        if(CameraViewModelHelper.valGear != 3){
//            setRearviewMirrorDownViewStatus(0);
//            KLog.d("is not R gear");
//            return;
//        }
        if(reverseLightSts==4){
            rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(true);
            rearviewMirrorBinding.swRearviewMirrorDown.setChecked(true);
        }else if(reverseLightSts==1){
            KLog.d("后视镜11----设置false 2");
            rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(false);
            rearviewMirrorBinding.swRearviewMirrorDown.setChecked(false);
        }
    }

    private void setRearviewMirrorDownViewStatus(int reverseLightSts) {
        //倒车档可以操作
        int status = CanManager.getInstance().getIntStatus(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 16777216);
        KLog.e("reverseLightSts: " + reverseLightSts + " read SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE is " + status);
        setRearMirrorFlipDownStatus(status);
//        rearviewMirrorBinding.llSettingRearviewMirrorDown.setAlpha(reverseLightSts == 1 ? 1.0f : 0.3f);
//        rearviewMirrorBinding.llSettingRearviewMirrorDown.setEnabled(reverseLightSts == 1);
//        rearviewMirrorBinding.swRearviewMirrorDown.setEnabled(reverseLightSts == 1);
//        rearviewMirrorBinding.llSettingRearviewMirrorDown.setClickable(reverseLightSts == 1);
//        rearviewMirrorBinding.swRearviewMirrorDown.setClickable(reverseLightSts == 1);
    }

    public void skinView(int uiMode) {
        KLog.e("换肤模式-切换-后视镜11---:"+uiMode);
        RearviewToast.getInstance().uiMode(uiMode);
        switch (uiMode) {
            case UiModeManager.MODE_NIGHT_YES:
                KLog.e("黑夜模式");
                rearviewMirrorBinding.llRearviewMirror.setBackgroundResource(R.drawable.shape_bg_fae1e3e6_24);
                rearviewMirrorBinding.tvRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                rearviewMirrorBinding.tvFlod.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                rearviewMirrorBinding.viewLine.setBackgroundResource(R.color.line_color);
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                rearviewMirrorBinding.llRearviewMirror.setBackgroundResource(R.drawable.shape_bg_fae1e3e6_24_day);
                rearviewMirrorBinding.tvRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                rearviewMirrorBinding.tvFlod.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                rearviewMirrorBinding.viewLine.setBackgroundResource(R.color.line_color_day);
                break;
        }
    }
}
