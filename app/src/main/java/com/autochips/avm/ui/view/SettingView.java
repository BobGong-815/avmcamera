package com.autochips.avm.ui.view;


import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_ACTIVATED_PANORAMA;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_ACTIVATES_PANORAMA;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_PATH_LINE;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_P_EXIT;
import static com.avm.framwork.manager.ViewSwitchManager.ACTION_INFO_TRANSPARENT_CHASSIS;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.databinding.ViewSettingBinding;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.listener.OnTabSelectListener;
import com.autochips.avm.util.SystemProperties;
import com.autochips.avm.viewmode.SettingViewModel;

import me.goldze.mvvmhabit.utils.KLog;

public class SettingView extends LinearLayout implements LifecycleOwner {
    private Context context;
    private LifecycleRegistry registry;
    private SettingViewModel viewModel;

    private ViewSettingBinding settingBinding;

    private OnVisibilityListener listener;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RelativeLayout mInfoView;
    private SegmentTabLayout mSegmentTabLayout;
    private View infoBg;

    private int[] getDescValueArray() {
        return new int[]{R.string.setting_at_once, R.string.setting_30_seconds};
    }

    private int[] getTransparentChassisDescValueArray() {
        return new int[]{R.string.setting_close, R.string.setting_low, R.string.setting_centre, R.string.setting_high};
    }

    public SettingView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        registry = new LifecycleRegistry(this);
        if (settingBinding == null) {
            viewModel = new SettingViewModel(context);
            settingBinding = DataBindingUtil.inflate(LayoutInflater.from(this.getContext()),
                    R.layout.view_setting, this, false);
            settingBinding.setViewModel(viewModel);
            addView(settingBinding.getRoot());
            viewModel.getLiveDataCloseUI().observe(this, this::closeUI);
            viewModel.getLiveDataPUI().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String aBoolean) {
                    if(infoBg.getVisibility()==GONE){
                        infoBookView(aBoolean);
                    }

                }
            });
            initTab();
        }
        checkButton();

        settingBinding.settingView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KLog.d("onTouch : " + event.toString());
                viewModel.startTimer();
                return true;
            }
        });
    }
    public void checkButton(){
        int settingPathLine = SystemProperties.getInt("settingPathLine", -1);//引导线开关
        int signalActivates = SystemProperties.getInt("signalActivates", -1);//转向灯激活 倒车影像
        int activatedPanorama = SystemProperties.getInt("activatedPanorama", 2);
        settingBinding.swSettingPathLine.setChecked(settingPathLine == 1);
        settingBinding.switchSignalActivates.setChecked(signalActivates == 1);
        //settingBinding.swCtivatedPanorama.setChecked(activatedPanorama == 1);
        if (settingPathLine == 1) {
//            bvavmJNI.bwSetTrajLineStatus((byte) 1);
            if (AvmApp.getInstance().getCameraView() != null) AvmApp.getInstance().getCameraView().getViewModel().setTrajLineEnable((byte) 1);
        }
      CameraViewModelHelper.getInstance().setTransparentIndexTab();

    }

    private void initTab() {
        String position = SystemProperties.get("pExit");
        KLog.d("position: " + position);
        LayoutParams layoutParams = new LayoutParams(settingBinding.segmentTab.getLayoutParams());
        //layoutParams.width = (304*getDescValueArray().length);
        layoutParams.leftMargin = 36;
        settingBinding.segmentTab.setTabWidth(186);
        settingBinding.segmentTab.setBackground(context.getResources().getDrawable(R.drawable.tab_selector_thumb));
        settingBinding.segmentTab.setLayoutParams(layoutParams);
        settingBinding.segmentTab.setTabData(getDescValueArray());
        settingBinding.segmentTab.setSelectTab(1);
        if (position.equals("1")) {
            settingBinding.segmentTab.setSelectTab(1);
        } else {
            settingBinding.segmentTab.setSelectTab(0);
        }
        settingBinding.segmentTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position, boolean fromUser) {
                    viewModel.startTimer();
                    viewModel.setRunning(true);
                    SystemProperties.set("pExit", position + "");
            }

            @Override
            public void onTabSameSelect(int position, boolean fromUser) {

            }
        });

        LayoutParams transparentChassisParams = new LayoutParams(settingBinding.transparentChassisTab.getLayoutParams());
        //layoutParams.width = (304*getDescValueArray().length);
        transparentChassisParams.leftMargin = 36;
        settingBinding.transparentChassisTab.setTabWidth(126);
        settingBinding.transparentChassisTab.setBackground(context.getResources().getDrawable(R.drawable.tab_selector_thumb));
        settingBinding.transparentChassisTab.setLayoutParams(transparentChassisParams);
        settingBinding.transparentChassisTab.setTabData(getTransparentChassisDescValueArray());

      int settingTabPosition = SystemProperties.getInt("settingRadarActivatedPanorama", 0);

      CameraViewModelHelper.getInstance().setTransparentIndexTab();
        settingBinding.transparentChassisTab.setSelectTab(settingTabPosition);
        settingBinding.transparentChassisTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position, boolean fromUser) {
                viewModel.startTimer();
                viewModel.setRunning(true);
                KLog.e("设置透明底盘: " + position);
                SystemProperties.set("settingRadarActivatedPanorama",  String.valueOf(position));
              CameraViewModelHelper.getInstance().setTransparentIndexTab();
            }

            @Override
            public void onTabSameSelect(int position, boolean fromUser) {

            }
        });

    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registry.setCurrentState(Lifecycle.State.STARTED);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        registry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    private boolean isFirstShow = true;
    private boolean isVisableLineShow = false;
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            viewModel.startTimer();
            settingBinding.segmentTab.setEnable(true);
            settingBinding.transparentChassisTab.setEnable(true);
            settingBinding.swSettingPathLine.setEnabled(true);
            settingBinding.switchSignalActivates.setEnabled(true);
            isVisableLineShow = settingBinding.swSettingPathLine.isChecked();
        }else{
            mSegmentTabLayout.setEnable(true);
            if(isFirstShow || isVisableLineShow != settingBinding.swSettingPathLine.isChecked()){
                if(isFirstShow) {
                    isFirstShow = false;
                }
                //AvmApp.getInstance().getCameraView().getViewModel().setBwSetRVCStatus(2);
            }
        }

    }
//
//    private void setIndexTab(int position){
//        KLog.e(position+" setIndexTab 设置透明底盘: " + position);
//        if (position == 0) {
//            bvavmJNI.bwSetCarBottomStatus((byte) 0);
//            bvavmJNI.bwSetCarTransparency( 1f);
//        }else if(position == 1){
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.3f);
//        } else if(position == 2){
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency( 0.15f);
//        }else {
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.05f);
//        }
//    }

    public void closeUI(boolean b) {
        KLog.d("closeUI 设置页面");
        mHandler.postDelayed(() -> {
            this.setVisibility(GONE);
            if (mInfoView.getVisibility() == View.VISIBLE) {
                mInfoView.setVisibility(View.GONE);
                infoBg.setVisibility(GONE);
            }
            if (listener != null) {
                listener.Visibility(true);
            }
            mSegmentTabLayout.setEnable(true);
        }, 100);

    }

    private void infoBookView(String type) {
        KLog.d("infoBookView 页面");
        mInfoView.setVisibility(View.VISIBLE);
        infoBg.setVisibility(VISIBLE);
        viewModel.cancelTimer();
//        mSegmentTabLayout.setEnable(false);
//        settingBinding.segmentTab.setEnable(false);
//        settingBinding.transparentChassisTab.setEnable(false);
//        settingBinding.swSettingPathLine.setEnabled(false);
//        settingBinding.switchSignalActivates.setEnabled(false);
        TextView title = mInfoView.findViewById(R.id.info_title);
        TextView content = mInfoView.findViewById(R.id.info_content);
        TextView ok = mInfoView.findViewById(R.id.info_ok);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfoView.setVisibility(View.GONE);
                infoBg.setVisibility(GONE);
                viewModel.startTimer();
                mSegmentTabLayout.setEnable(true);
                settingBinding.segmentTab.setEnable(true);
                settingBinding.transparentChassisTab.setEnable(true);
                settingBinding.swSettingPathLine.setEnabled(true);
                settingBinding.switchSignalActivates.setEnabled(true);
            }
        });
        switch (type) {
            case ACTION_INFO_P_EXIT:
                title.setText(R.string.setting_p_exit);
                content.setText(R.string.setting_p_exit_content);
                break;
            case ACTION_INFO_TRANSPARENT_CHASSIS:
                title.setText(R.string.setting_transparent_chassis_infobook_title);
                content.setText(R.string.setting_transparent_chassis_infobook_content);
                break;
            case ACTION_INFO_ACTIVATED_PANORAMA:
                title.setText(R.string.setting_radar_activated_panorama);
                content.setText(R.string.setting_radar_activated_panorama_content);
                break;
            case ACTION_INFO_PATH_LINE:
                title.setText(R.string.setting_path_line);
                content.setText(R.string.setting_path_line_content);
                break;
            case ACTION_INFO_ACTIVATES_PANORAMA:
                title.setText(R.string.setting_signal_activates_panorama);
                content.setText(R.string.setting_signal_activates_panorama_content);
                break;
        }
    }


    public void setInfoBookView(RelativeLayout view,View viewBg,SegmentTabLayout segmentTabLayout) {
        this.mInfoView = view;
        this.infoBg = viewBg;
        this.mSegmentTabLayout = segmentTabLayout;
        infoBg.setOnClickListener((view1)->{});
    }

    public void setListener(OnVisibilityListener listener) {
        this.listener = listener;
    }

    public interface OnVisibilityListener {
        void Visibility(boolean isVisibility);

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void skinView( int uiMode){
      KLog.e("换肤模式 "+uiMode);
        switch (uiMode) {
            case UiModeManager.MODE_NIGHT_YES:

                settingBinding.settingView.setBackgroundResource(R.drawable.shape_bg_nor);
                settingBinding.tvSettingPExit.setTextColor(context.getResources().getColor(R.color.setting_view_title_color));
                settingBinding.segmentTab.setThumbDrawable(R.drawable.tab_selector_thumb);
                settingBinding.segmentTab.setThumbDrawable3(R.drawable.tab_selector_thumb);
                settingBinding.segmentTab.setThumbDrawable2(R.drawable.tab_selector_thumb_old);
                settingBinding.segmentTab.setBackground(context.getResources().getDrawable(R.drawable.tab_selector_thumb));
                settingBinding.segmentTab.setTextSelectColor(R.color.setting_view_select_title_color,0);
                settingBinding.segmentTab.setTextUnselectColor(R.color.setting_view_content_color);
                settingBinding.transparentChassis.setTextColor(context.getResources().getColor(R.color.setting_view_title_color));
                settingBinding.activatedPanorama.setTextColor(context.getResources().getColor(R.color.setting_view_title_color));
                settingBinding.pathLine.setTextColor(context.getResources().getColor(R.color.setting_view_title_color));
                settingBinding.tvActivatesPanorama.setTextColor(context.getResources().getColor(R.color.setting_view_title_color));


                settingBinding.transparentChassisTab.setThumbDrawable(R.drawable.tab_selector_thumb);
                settingBinding.transparentChassisTab.setThumbDrawable3(R.drawable.tab_selector_thumb);
                settingBinding.transparentChassisTab.setThumbDrawable2(R.drawable.tab_selector_thumb_old);
                settingBinding.transparentChassisTab.setBackground(context.getResources().getDrawable(R.drawable.tab_selector_thumb));
                settingBinding.transparentChassisTab.setTextSelectColor(R.color.setting_view_select_title_color,0);
                settingBinding.transparentChassisTab.setTextUnselectColor(R.color.setting_view_content_color);

                if (AvmApp.ISAY5T) {
                    //settingBinding.swCtivatedPanorama.setBackgroundResource(R.drawable.selector_switch_t_track);
                    settingBinding.swSettingPathLine.setBackgroundResource(R.drawable.selector_switch_t_track);
                    settingBinding.switchSignalActivates.setBackgroundResource(R.drawable.selector_switch_t_track);
                }else {
                    //swCtivatedPanorama.setBackgroundResource(R.drawable.selector_switch_track);
                    settingBinding.swSettingPathLine.setBackgroundResource(R.drawable.selector_switch_track);
                    settingBinding.switchSignalActivates.setBackgroundResource(R.drawable.selector_switch_track);
                }



                settingBinding.pExitInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip));
                settingBinding.transparentChassisInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip));
                settingBinding.activatedPanoramaInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip));
                settingBinding.pathLineInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip));
                settingBinding.activatesPanoramaInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip));
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                settingBinding.settingView.setBackgroundResource(R.drawable.shape_bg_nor_day);
                settingBinding.tvSettingPExit.setTextColor(context.getResources().getColor(R.color.setting_view_select_title_color));
                settingBinding.segmentTab.setThumbDrawable(R.drawable.tab_setting_selector_thumb_day);
                settingBinding.segmentTab.setThumbDrawable3(R.drawable.tab_setting_selector_thumb_day);
                settingBinding.segmentTab.setThumbDrawable2(R.drawable.tab_selector_thumb_old_day);
                settingBinding.segmentTab.setBackground(context.getResources().getDrawable(R.drawable.tab_setting_selector_thumb_day));
                settingBinding.segmentTab.setTextSelectColor(R.color.setting_tab_color_day,0);
                settingBinding.segmentTab.setTextUnselectColor(R.color.setting_view_content_color_day);
                settingBinding.transparentChassis.setTextColor(context.getResources().getColor(R.color.setting_view_select_title_color));
                settingBinding.activatedPanorama.setTextColor(context.getResources().getColor(R.color.setting_view_select_title_color));
                settingBinding.pathLine.setTextColor(context.getResources().getColor(R.color.setting_view_select_title_color));
                settingBinding.tvActivatesPanorama.setTextColor(context.getResources().getColor(R.color.setting_view_select_title_color));

                settingBinding.transparentChassisTab.setThumbDrawable(R.drawable.tab_setting_selector_thumb_day);
                settingBinding.transparentChassisTab.setThumbDrawable3(R.drawable.tab_setting_selector_thumb_day);
                settingBinding.transparentChassisTab.setThumbDrawable2(R.drawable.tab_selector_thumb_old_day);
                settingBinding.transparentChassisTab.setBackground(context.getResources().getDrawable(R.drawable.tab_setting_selector_thumb_day));
                settingBinding.transparentChassisTab.setTextSelectColor(R.color.setting_tab_color_day,0);
                settingBinding.transparentChassisTab.setTextUnselectColor(R.color.setting_view_content_color_day);

                if (AvmApp.ISAY5T) {
                    //settingBinding.swCtivatedPanorama.setBackgroundResource(R.drawable.selector_switch_track_t_day);
                    settingBinding.swSettingPathLine.setBackgroundResource(R.drawable.selector_switch_track_t_day);
                    settingBinding.switchSignalActivates.setBackgroundResource(R.drawable.selector_switch_track_t_day);
                }else {
                    //settingBinding.swCtivatedPanorama.setBackgroundResource(R.drawable.selector_switch_track_day);
                    settingBinding.swSettingPathLine.setBackgroundResource(R.drawable.selector_switch_track_day);
                    settingBinding.switchSignalActivates.setBackgroundResource(R.drawable.selector_switch_track_day);
                }


                settingBinding.pExitInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip_day));
                settingBinding.transparentChassisInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip_day));
                settingBinding.activatedPanoramaInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip_day));
                settingBinding.pathLineInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip_day));
                settingBinding.activatesPanoramaInfo.setImageDrawable(context.getDrawable(R.drawable.ic_tip_day));
                break;
        }
    }

    public void reloadLanauge(){
        settingBinding.tvSettingPExit.setText(R.string.setting_p_exit);
        settingBinding.tvActivatesPanorama.setText(R.string.setting_signal_activates_panorama);
        settingBinding.activatedPanorama.setText(R.string.setting_radar_activated_panorama);
        settingBinding.segmentTab.setTabData(getDescValueArray());
        settingBinding.pathLine.setText(R.string.setting_path_line);
        settingBinding.transparentChassis.setText(R.string.setting_transparent_chassis);
        settingBinding.transparentChassisTab.setTabData(getTransparentChassisDescValueArray());
    }

    public void setViewRefresh(int style){
        KLog.v("setViewRefresh 当前language："+style);
        settingBinding.settingView.setLayoutDirection(style);
    }

}
