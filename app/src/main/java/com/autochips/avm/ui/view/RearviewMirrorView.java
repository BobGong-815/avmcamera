package com.autochips.avm.ui.view;

import static android.hardware.automotive.vehicle.V2_0.VehicleProperty.SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.REARVIEW_MIRROR_ADJUSTMENT;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.ACU_FOLD_UNFOLD_CTL_REQ;

import android.app.UiModeManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.databinding.ViewRearviewMirrorBinding;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.util.CustomToast;
import com.autochips.avm.util.RearviewToast;
import com.autochips.avm.viewmode.RearviewMirrorModel;
import com.avm.framwork.helper.ThreadPoolUtil;
import com.avm.framwork.manager.CanManager;

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
        }
    }

    /**
     * 档位信息
     */
    public void gearInfo(int val) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setRearviewMirrorDownViewStatus(val);
            }
        });
    }

    /**
     * 后视镜下翻状态
     */
    public void reverseLight(int val) {
        KLog.d("reverseLight: " + val);
        setRearviewMirrorStatus(val);
    }

    public void hidenMirrowView(){
        rearviewMirrorBinding.llSettingRearviewMirrorDown.setVisibility(View.GONE);
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
        int reverseLightSts = CanManager.getInstance().getIntStatus(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 0);
        KLog.i("reverseAutoMaticStatus ......  " + reverseLightSts);
        setRearviewMirrorDownViewStatus(reverseLightSts);
        KLog.d("initData reverseLightSts: " + reverseLightSts);
        reverseLight(reverseLightSts);

        // 设置点击事件
        rearviewMirrorBinding.llSettingRearviewMirrorDown.setOnClickListener(this);
        rearviewMirrorBinding.llSettingExpand.setOnClickListener(this);
        rearviewMirrorBinding.llSettingFold.setOnClickListener(this);
        rearviewMirrorBinding.llSettingExpand.setOnTouchListener(new ItOnTouchListener(rearviewMirrorBinding.tvSettingExpand));
        rearviewMirrorBinding.llSettingFold.setOnTouchListener(new ItOnTouchListener(rearviewMirrorBinding.tvSettingFold));
        rearviewMirrorBinding.llSettingRearviewMirrorDown.setOnTouchListener(new ItOnTouchListener(rearviewMirrorBinding.tvSettingRearview));
    }

    private boolean isOnTouchFlod = false;//是否触摸了折叠
    private boolean isOnTouchExpand = false;//是否触摸了展开
    class ItOnTouchListener implements OnTouchListener {

        private TextView textView;

        public ItOnTouchListener(TextView textView) {
            this.textView = textView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean isCheck = v.isPressed();
            if(v.getId() != R.id.ll_setting_rearview_mirror_down) {
                UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
                int uiMode = uiModeManager.getNightMode();
                if (uiMode == UiModeManager.MODE_NIGHT_YES) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        textView.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!isCheck) {
                            textView.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        textView.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        textView.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (!isCheck) {
                            textView.setTextColor(getResources().getColor(R.color.setting_view_content_color_day));
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        textView.setTextColor(getResources().getColor(R.color.setting_view_content_color_day));
                    }
                }
            }
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v.getId() == R.id.ll_setting_expand) {//展开
                    isOnTouchExpand = true;
                    rearviewMirrorModel.cancleTimer();
                    Integer[] arrUnfold = {1, 10};
                    //区分3.0平台
                    if (AvmApp.EEA == 2) {
                        CanManager.getInstance().setIntProperty(ACU_FOLD_UNFOLD_CTL_REQ, 0, 2);
                    } else {
                        CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrUnfold);
                    }
                } else if (v.getId() == R.id.ll_setting_fold) {//折叠
                    isOnTouchFlod = true;
                    rearviewMirrorModel.cancleTimer();
                    Integer[] arrFold = {1, 9};
                    if (AvmApp.EEA == 2) {
                        CanManager.getInstance().setIntProperty(ACU_FOLD_UNFOLD_CTL_REQ, 0, 1);
                    } else {
                        CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrFold);
                    }
                }
            }
            return false;
        }
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
        mHandler.postDelayed(() -> {
            this.setVisibility(GONE);
            if (listener != null) {
                listener.Visibility(true);
            }
        }, 100);

    }

    public void setListener(OnVisibilityListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View view) {
        KLog.d("后视镜11---- EEA:"+AvmApp.EEA);
        if (view.getId() == R.id.ll_setting_rearview_mirror_down) {
            // 外后视镜倒车下翻
            ThreadPoolUtil.getInstance().execute(() -> {
                int reverseAutoMaticStatus = CanManager.getInstance().getIntStatus(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 0);
                CanManager.getInstance().setIntProperty(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 0, reverseAutoMaticStatus == 4 ? 1 : 4);
                rearviewMirrorModel.startTimer();
                rearviewMirrorModel.setRunning(true);
            });

            if (rearviewMirrorBinding.llSettingRearviewMirrorDown.isSelected() == true) {
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(false);
                RearviewToast.getInstance().showToast(getResources().getString(R.string.setting_rearview_mirror_down_close));
                DataManager.writeFault(DataConstant.Code.REARM_CLOSE);
            } else {
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(true);
                RearviewToast.getInstance().showToast(getResources().getString(R.string.setting_rearview_mirror_down_open));
                DataManager.writeFault(DataConstant.Code.REARM_OPEN);
            }
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            int uiMode = uiModeManager.getNightMode();
            if (uiMode == UiModeManager.MODE_NIGHT_YES) {
                if(rearviewMirrorBinding.llSettingRearviewMirrorDown.isSelected()) {
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                }else {
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(getResources().getColor(R.color.setting_view_content_color));
                }
            } else {
                if(rearviewMirrorBinding.llSettingRearviewMirrorDown.isSelected()) {
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                }else {
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(getResources().getColor(R.color.setting_view_content_color_day));
                }
            }
        } else if (view.getId() == R.id.ll_setting_expand) {//展开
            if(!isOnTouchExpand){
                rearviewMirrorModel.cancleTimer();
                Integer[] arrUnfold = {1, 10};
                //区分3.0平台
                if (AvmApp.EEA == 2) {
                    CanManager.getInstance().setIntProperty(ACU_FOLD_UNFOLD_CTL_REQ, 0, 2);
                } else {
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrUnfold);
                }
            }
            mHandler.postDelayed(()->{
                isOnTouchExpand = false;
                rearviewMirrorModel.startTimer();//ACU_ORVMOperationReq
                rearviewMirrorModel.setRunning(true);
                Integer[] arrUnfoldP = {0, 0};
                //区分3.0平台
                if(AvmApp.EEA == 2){
                    CanManager.getInstance().setIntProperty(ACU_FOLD_UNFOLD_CTL_REQ, 0, 0);
                }else {
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrUnfoldP);
                }
                if(AvmRuntime.self().getCurrentSped() <= 15) {
                    RearviewToast.getInstance().showToast(getResources().getString(R.string.desc_rearview_mirror_expand));
                }
            },isOnTouchExpand ? 0 : 500);

        } else if (view.getId() == R.id.ll_setting_fold) {//折叠
            if(!isOnTouchFlod){
                rearviewMirrorModel.cancleTimer();
                Integer[] arrFold = {1, 9};
                if (AvmApp.EEA == 2) {
                    CanManager.getInstance().setIntProperty(ACU_FOLD_UNFOLD_CTL_REQ, 0, 1);
                } else {
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrFold);
                }
            }
            mHandler.postDelayed(()->{
                isOnTouchFlod = false;
                rearviewMirrorModel.startTimer();
                rearviewMirrorModel.setRunning(true);
                Integer[] arrFoldP = {0, 0};
                if(AvmApp.EEA == 2){
                    CanManager.getInstance().setIntProperty(ACU_FOLD_UNFOLD_CTL_REQ, 0, 0);
                }else {
                    CanManager.getInstance().setIntArray(REARVIEW_MIRROR_ADJUSTMENT, 0, arrFoldP);
                }
                if(AvmRuntime.self().getCurrentSped() <= 15) {
                    RearviewToast.getInstance().showToast(getResources().getString(R.string.desc_rearview_mirror_fold));
                }
            },isOnTouchFlod ? 0 : 500);
        }

    }

    public interface OnVisibilityListener {
        void Visibility(boolean isVisibility);

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE)
            rearviewMirrorModel.startTimer();
        rearviewMirrorBinding.llSettingRearviewMirrorDown.setVisibility(AvmApp.OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH==1?VISIBLE:GONE);
        rearviewMirrorBinding.llFold.setVisibility(AvmApp.OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH==1?VISIBLE:GONE);
    }

    private void setRearviewMirrorStatus(int reverseLightSts) {
        if(!AvmRuntime.self().isRearGearSts()){
            setRearviewMirrorDownViewStatus(0);
            KLog.d("is not R gear");
            return;
        }
        if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_ID) {
            if (reverseLightSts == 3) {
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(true);
            } else if (reverseLightSts == 0) {
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(false);
            }
        } else if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_T_ID
                || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_G_ID
                || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_GR_ID) {
            if (reverseLightSts == 4) {
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(true);
                UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
                int uiMode = uiModeManager.getNightMode();
                if (uiMode == UiModeManager.MODE_NIGHT_YES){
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                }else {
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                }
            } else if (reverseLightSts == 1) {
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(false);
                UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
                int uiMode = uiModeManager.getNightMode();
                if (uiMode == UiModeManager.MODE_NIGHT_YES){
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(getResources().getColor(R.color.setting_view_content_color));
                }else {
                    rearviewMirrorBinding.tvSettingRearview.setTextColor(getResources().getColor(R.color.setting_view_content_color_day));
                }
            }
        }

    }

    public void setRearviewMirrorDownViewStatus(int reverseLightSts) {
        KLog.e("reverseLightSts: " + reverseLightSts+"AvmRuntime.self().isRearGearSts():"+AvmRuntime.self().isRearGearSts());
        //倒车档可以操作
        if (reverseLightSts == 1) {
            int status = CanManager.getInstance().getIntStatus(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 0);
            KLog.i("status ............  " + status);
            setRearviewMirrorStatus(status);
        } else {
            rearviewMirrorBinding.llSettingRearviewMirrorDown.setSelected(false);
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            int uiMode = uiModeManager.getNightMode();
            if (uiMode == UiModeManager.MODE_NIGHT_YES){
                rearviewMirrorBinding.tvSettingRearview.setTextColor(getResources().getColor(R.color.setting_view_content_color));
            }else {
                rearviewMirrorBinding.tvSettingRearview.setTextColor(getResources().getColor(R.color.setting_view_content_color_day));
            }
        }
        rearviewMirrorBinding.llSettingRearviewMirrorDown.setAlpha(reverseLightSts == 1 ? 1.0f : 0.3f);
        rearviewMirrorBinding.llSettingRearviewMirrorDown.setEnabled(reverseLightSts == 1);
    }

    public void changeShow(float speedValue){
        boolean isEnable = speedValue <= 15;//可以点击
        if(isEnable != rearviewMirrorBinding.llSettingExpand.isEnabled()){
            rearviewMirrorBinding.llSettingExpand.setAlpha(isEnable ? 1.0f : 0.3f);
            rearviewMirrorBinding.llSettingExpand.setEnabled(isEnable);

            rearviewMirrorBinding.llSettingFold.setAlpha(isEnable ? 1.0f : 0.3f);
            rearviewMirrorBinding.llSettingFold.setEnabled(isEnable);
        }
    }

    public void skinView(int uiMode) {
//        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
//        int uiMode = uiModeManager.getNightMode();
        KLog.e("换肤模式-切换-后视镜11---:" + uiMode);
        if (rearviewMirrorBinding == null) return;

        RearviewToast.getInstance().uiMode(uiMode);
        switch (uiMode) {
            case UiModeManager.MODE_NIGHT_YES:
                KLog.e("黑夜模式");
                rearviewMirrorBinding.llRearviewMirror.setBackgroundResource((AvmApp.OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH==1 && AvmApp.OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH == 1)?
                        R.mipmap.rearview_setting_bg : R.mipmap.rearview_setting_short_bg);
                rearviewMirrorBinding.llSettingFold.setBackgroundResource(R.drawable.button_select);
                rearviewMirrorBinding.llSettingExpand.setBackgroundResource(R.drawable.button_select);
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setBackgroundResource(R.drawable.button_select_down);
                rearviewMirrorBinding.tvRearview.setTextColor(context.getResources().getColor(R.color.setting_view_title_color));
                rearviewMirrorBinding.tvSettingFold.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(
                        (rearviewMirrorBinding.llSettingRearviewMirrorDown.isSelected() && rearviewMirrorBinding.llSettingRearviewMirrorDown.isEnabled()) ?
                                R.color.setting_view_content_color_day : R.color.setting_view_content_color));
                rearviewMirrorBinding.tvSettingExpand.setTextColor(context.getResources().getColor(R.color.setting_view_content_color));
                rearviewMirrorBinding.ivSettingFold.setImageDrawable(context.getDrawable(R.drawable.button_select_setting_fold));
                rearviewMirrorBinding.ivSettingView.setImageDrawable(context.getDrawable(R.drawable.button_select_iv_mirror));
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                rearviewMirrorBinding.llRearviewMirror.setBackgroundResource((AvmApp.OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH==1 && AvmApp.OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH ==1)?
                        R.mipmap.rearview_setting_bg_day : R.mipmap.rearview_setting_short_bg_day);
                rearviewMirrorBinding.llSettingFold.setBackgroundResource(R.drawable.button_rearview_select_day);
                rearviewMirrorBinding.llSettingExpand.setBackgroundResource(R.drawable.button_rearview_select_day);
                rearviewMirrorBinding.llSettingRearviewMirrorDown.setBackgroundResource(R.drawable.button_select_down_day);
                rearviewMirrorBinding.tvRearview.setTextColor(context.getResources().getColor(R.color.setting_view_title_color_day));
                rearviewMirrorBinding.tvSettingFold.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                rearviewMirrorBinding.tvSettingRearview.setTextColor(context.getResources().getColor(
                        (rearviewMirrorBinding.llSettingRearviewMirrorDown.isSelected() && rearviewMirrorBinding.llSettingRearviewMirrorDown.isEnabled()) ?
                                R.color.setting_view_content_color :  R.color.setting_view_content_color_day));
                rearviewMirrorBinding.tvSettingExpand.setTextColor(context.getResources().getColor(R.color.setting_view_content_color_day));
                rearviewMirrorBinding.ivSettingFold.setImageDrawable(context.getDrawable(R.drawable.button_select_setting_fold_day));
                rearviewMirrorBinding.ivSettingView.setImageDrawable(context.getDrawable(R.drawable.button_select_iv_mirror_day));
                break;
        }
    }

    public void  reloadLanauge(){
        rearviewMirrorBinding.tvSettingRearview.setText(R.string.setting_rearview_mirror_down);
        rearviewMirrorBinding.tvSettingFold.setText(R.string.setting_fold);
        rearviewMirrorBinding.tvSettingExpand.setText(R.string.setting_expand);
        rearviewMirrorBinding.tvRearview.setText(R.string.setting_rearview_mirror);
    }
}
