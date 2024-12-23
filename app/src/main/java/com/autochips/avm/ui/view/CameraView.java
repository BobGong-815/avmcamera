package com.autochips.avm.ui.view;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.ASSIST_DRIVE_PAS_BUTTON_PRESS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_UINM_TURN_LIGHT_SW_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_CHIME_PAS_WARNTONE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RLDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RLMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RRDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RRMidDistance;
import static com.avm.framwork.constant.CameraContracts.ROW_1_LEFT;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_2_D;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_2_D_BOTTOM;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_2_D_LIFT;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_2_D_RIGHT;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_2_D_TOP;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_LEFT_FRONT;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_LEFT_REAR;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_RIGHT_FRONT;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_RIGHT_REAR;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.databinding.ViewBottomBinding;
import com.autochips.avm.databinding.ViewCameraBinding;
import com.autochips.avm.databinding.ViewCameraRightBinding;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.helper.LongPressGestureListener;
import com.autochips.avm.listener.CallBackHelper;
import com.autochips.avm.listener.CallBackInterface;
import com.autochips.avm.listener.ICameraViewListener;
import com.autochips.avm.listener.OnTabSelectListener;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.ui.BottomDialog;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.NotCloseToast;
import com.autochips.avm.util.RearviewToast;
import com.autochips.avm.util.SystemProperties;
import com.autochips.avm.viewmode.CameraViewModel;
import com.avm.framwork.constant.CameraContracts;
import com.avm.framwork.manager.CanManager;
import com.avm.framwork.manager.ViewSwitchManager;
import com.gxa.lib.car.HalPropertyIds;
import com.gxa.service.camera.AvmManager;

import java.util.List;
import java.util.Locale;

import me.goldze.mvvmhabit.utils.KLog;

@SuppressLint("WrongConstant")
public class CameraView extends View implements LifecycleOwner {
    private BottomDialog bottomDialog;
    private boolean SHOW_OVERLAY_LAYER = true;
    private static final String TAG = CameraView.class.getName();
    private LifecycleRegistry registry = new LifecycleRegistry(this);

    public static SurfaceControl windowSurfaceControl;

    private CallBackInterface callBackInterface = new CallBackInterface() {
        @Override
        public void setup(int msg, int param1, int param2) {
            KLog.d("CallBackInterface  msg " + msg + "   param1  " + param1 + "   param2: " + param2);
            if ( mViewCameraBinding == null && mViewCameraRightBinding == null) {
                return;
            }
            mMainHandler.post(() -> {
                setAVMBreakdown(msg, param1, param2);
            });
        }
    };

    private int[] getDescValueArray() {
        return new int[]{R.string.camera_2d, R.string.camera_3d, R.string.camera_wide_angle};
    }

    private int[] getWideAngleValueArray() {
        return new int[]{R.string.front_wide_angle, R.string.back_wide_angle, R.string.before, R.string.rear_wheel};
    }


    /**
     * 层级
     */
    public static int WINDOW_TYPE = 2501;
    //层级2501
    private static final int CALIBRATION_SHOW_WINDOW = 1001;//显示手动标记界面
    /**
     * 窗口相关
     */
    private WindowManager mWindowManager;
    protected WindowManager.LayoutParams mWindowLps;//window的属性
    //    protected WindowManager.LayoutParams mWindowLpsBottom;//window的属性
//    protected WindowManager.LayoutParams mFullWindowLps;//全面的窗口参数
    protected ViewCameraBinding mViewCameraBinding;//总windowManager界面
    protected ViewCameraRightBinding mViewCameraRightBinding;//总windowManager界面
    private ViewBottomBinding cameraBinding;
    protected CameraViewModel viewModel;
    private SettingView settingView;
    private RearviewMirrorView rearviewMirrorView;
    private int viewPosition = 0;// 是2d或者3d页面切换
    private int hisPosition = -1;// 临时记忆视角模式

    private Context mContext;
    private boolean btnSettingSelect = false;// 设置
    private boolean btnRearSelect = false;// 后视镜
    public static volatile boolean isShowing = false; //界面是否正在显示

    private int camera3DDirection;//当前3D视角

    private LongPressGestureListener longPressGestureListener;
    private boolean isRightView = false;

    //view
    protected ConstraintLayout calibration;
    protected RadarStatusView rearRadarViewId;
    protected RadarStatusFrontView rearRadarFrontViewId;
    protected ConstraintLayout layout3dTouchId;
    protected FrameLayout viewFrame;
    protected ConstraintLayout layout2d;
    protected ConstraintLayout layout3d;
    protected View liftBg;
    protected LinearLayout layoutSettingId;
    protected RelativeLayout infobook;
    protected View infoBg;
    protected SegmentTabLayout segmentWideAngle;
    protected View rootView,camera2dBg,camera3dBg;
    protected SegmentTabLayout segmentTab;
    protected Group viewShow2dGroupId;
    protected Group viewShow3dGroupId,smartGroupId;
    protected ImageView radarSoundIv,radarfErrImgId1,radarfErrImgId2,radarfErrImgId3,radarfErrImgId4,
            radarErrImgId1,radarErrImgId2,radarErrImgId3,radarErrImgId4,cameraIv
            ,cameraLeftFront,cameraRightFront,cameraLeftRear,cameraRightRear,cameraRight
            ,cameraTop,cameraBottom,cameraLift,ivBreakdown,ivSetting,ivBackMirror,cameraIvLift;
    protected LinearLayout llBackMirror,llSetting,cameraBreakdown,toastBg,toastNcBg,layoutShowFull2d,layoutCalibrateId;
    protected ConstraintLayout layoutWideAngle,cameraImageLayout,radarSoundLayout
            ,parkingAssistLayout,mainAvmViewRootId,cameraImageLayoutLift;
    protected TextView tvBreakdown,manualCalibration,automaticCalibration,infoTitle,infoContent,rearRadarImgId,rearRadarFrontId;
    protected AppCompatButton infoOk;
    protected RelativeLayout rlClose;
    protected View red2dTop,red2dLift,red2dRight,red2dBottom,red3dleftFront,red3dleftFront1,red3drightFront,red3drightFront1,
            red3dleftRear,red3dleftRear1,red3drightRear,red3drightRear1;
    protected ConstraintLayout clCon,conRadarError,conRadar;
    private int cameraShowType = -1;//记录当前显示视角，判断是否要显示故障,0前，1,后，2左，3右，4左右
    private int camera3DShowType = -1;//记录当前显示视角，判断是否要显示故障,-1、无选中、1,左前，2右前，3左后，4右后，


    public CameraView(Context context) {
        super(context);
        KLog.d("BaseCameraView");
        this.mContext = context;
        cameraBinding = ViewBottomBinding.inflate(LayoutInflater.from(mContext), null, false);
        initWindow();
        initView();
        initData();
    }

    public static boolean isIsShowing() {
        return isShowing;
    }

    /**
     * 更新窗口
     */
    public void updateWind(float alpha, int wh) {
        showSmartWin();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        bottomDialog = new BottomDialog(getContext());
        registry.setCurrentState(Lifecycle.State.STARTED);

        viewModel.setCameraViewListener(new ICameraViewListener() {
            @Override
            public void closeAvm() {
                dismissView("1");
            }

            @Override
            public void setBtnSettingSelect(boolean btnSettingSelect) {
                setBtnSettingSelectView(btnSettingSelect);
            }

            @Override
            public void setBtnRearMirrorSelect(boolean btnRearSelect) {
                setBtnRearSelectView(btnRearSelect);
            }

            @Override
            public void setCalibrationSelect(boolean btnCalibrationSelect) {
                Log.d("Cal", "setCalibrationSelect : " + btnCalibrationSelect);
                if (btnCalibrationSelect) {
                    calibration.setVisibility(VISIBLE);
                } else {
                    calibration.setVisibility(GONE);
                }
            }
        });
        viewModel.getLiveDataCamera2DTopUI().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String type) {
                KLog.i("onChanged .... " + type);
                hidViewButtonTimer.start(0);
                KLog.d(" layout2d getLiveDataCamera2DTopUI " + segmentTab.getCurrentTab());
                if (segmentTab.getCurrentTab() == 0) {
                    layout2d.setVisibility(VISIBLE);
                }
                //showFullWin();
                chick2DView(type);
            }
        });

        camera2dBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                KLog.i(" onClick: "+1);
                if (viewShow2dGroupId.getVisibility() == View.VISIBLE) {
                    viewShow2dGroupId.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewShow2dGroupId.setVisibility(GONE);
                        }
                    }, 500);
                } else {
                    KLog.d(" layout2d camera2dBg ");
                    viewShow2dGroupId.setVisibility(VISIBLE);
                    hidViewButtonTimer.start(0);
                }
            }
        });

        camera3dBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                KLog.i(" onClick: "+2);
                if (viewShow3dGroupId.getVisibility() == View.VISIBLE) {
                    viewShow3dGroupId.setVisibility(GONE);
                } else {
                    viewShow3dGroupId.setVisibility(VISIBLE);
                    hidViewButtonTimer.start(1);
                }
            }
        });

        viewModel.getLiveDataRadarSound().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                int status = CanManager.getInstance().getIntStatus(ASSIST_DRIVE_PAS_BUTTON_PRESS, 0);
                KLog.d("雷达 点击status " + status);
                if (status == 0) {
                    radarSoundIv.setImageDrawable(mContext.getDrawable(R.drawable.ic_radar_sound_close));
                    KLog.d("雷达 关闭提示音 ");
                    radarSoundLayout.setBackground(mContext.getDrawable(R.drawable.shape_bg_blue_12));
                } else {
                    radarSoundIv.setImageDrawable(mContext.getDrawable(R.drawable.ic_radar_sound_open));
                    KLog.d("雷达 打开提示音 ");
                    radarSoundLayout.setBackground(mContext.getDrawable(R.drawable.shape_bg_nor_12));
                }
                CameraViewModelHelper.getInstance().radarSoundStatus((Integer) 1, status);
            }
        });

        viewModel.getLiveDataCamera3DTopUI().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String type) {
                layout3d.setVisibility(VISIBLE);
                hidViewButtonTimer.start(1);
                //showFullWin();
                chick3DView(type);
            }
        });
        layoutShowFull2d.setOnTouchListener(this::showFull2DByOnTouch);
        camera3dBg.setOnTouchListener(new OnTouchListener() {// 长按 60s 显示标定图标
            private long resTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KLog.i(" onClick: "+9);
                if (clickCount < 10) {
                    return false;
                }
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mMainHandler.removeCallbacksAndMessages("count");
                        resTime = SystemClock.elapsedRealtime();
                        KLog.d(" AVM 打开标定操作页面 initWindow 按下 ");
                        break;
                    case MotionEvent.ACTION_UP:
                        mMainHandler.removeCallbacksAndMessages("count");
                        clickCount = 0;
                        long movTime = SystemClock.elapsedRealtime();
                        long endTime = movTime - resTime;
                        KLog.d(" AVM 打开标定操作页面 initWindow " + endTime);
                        if (endTime > 10 * 1000) {
                            mMainHandler.sendEmptyMessageDelayed(CALIBRATION_SHOW_WINDOW, 0);
                        }
                        break;
                }
                return false;
            }
        });

    }

    /**
     * 显示自动标定操作弹框
     * 2d 模式下连续点击10下，3s内切到3d，长按左卡片车顶上10s时间，放开手
     */
    private int clickCount = 0;
    private long lastClickTime;

    public void onCameraIv(View view) {
        if (viewPosition == 0) {
            long now = System.currentTimeMillis();
            if ((now - lastClickTime) > 500) {
                clickCount = 0;
            } else {
                clickCount++;
            }
//            Log.d("Cal", "(now-lastClickTime) = " + (now-lastClickTime) + " , clickCount = " + clickCount);
            lastClickTime = now;
            if (clickCount >= 10) {
                mMainHandler.sendEmptyMessage(CALIBRATION_SHOW_WINDOW);
                clickCount = 0;
            }
        }
    }

    public boolean showFull2DByOnTouch(View v, MotionEvent event) {
        KLog.d("showFull2DByOnTouch");
        KLog.i(" onClick: "+5);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            viewModel.setRunning(true);
//            showFullWin();
            AvmRuntime.self().clickLeftCard();
            return true;
        }
        return false;

    }


    @SuppressLint("WrongConstant")
    private void initWindow() {
        mWindowLps = new WindowManager.LayoutParams();
        mWindowLps.setTitle("AvmCameraView");
        //临时注释让全屏显示，否则摄像头画面显示不全
        mWindowLps.alpha = 0.0f;//1.0f不透明
        if (Build.BOARD.equals("rk30sdk")) WINDOW_TYPE = 2038;
        mWindowLps.type = WINDOW_TYPE;//WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
        mWindowLps.x = 0;
        mWindowLps.y = 0;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        KLog.d(" AVM 主界面被启动 initWindow " + mWindowLps);
//        mWindowLpsBottom = mWindowLps;
        mWindowLps.alpha = 0.0f;
        mWindowLps.width = 2;
        mWindowLps.height = 2;
        mWindowLps.gravity = Gravity.LEFT | Gravity.TOP;


        BvAvmJNIHelper.getInstance().setCallback((v -> {
            /**
             * cameraBinding 为了解决车模透明时，穿透到桌面而增加了window底部蒙版，只有视频返回第一帧的时候，才显示
             */
            showBottomView();
        }));
    }

    @SuppressLint("WrongConstant")
    private void showBottomView() {

        KLog.d(cameraBinding.frameLayoutId + "窗口层级 mWindowLpsBottom：" + mWindowLps);
        if (SHOW_OVERLAY_LAYER) cameraBinding.frameLayoutId.setVisibility(GONE);
        if (isShowing) {
            mWindowLps.alpha = 1.0f;
            if (isSmartWin) mWindowLps.format = PixelFormat.TRANSLUCENT;
            else mWindowLps.format = PixelFormat.UNKNOWN;
            KLog.d(isSmartWin + " isSmartWin bottom_view-isFullWin=" + isFullWin + " mWindowLpsBottom=" + mWindowLps);
            mWindowManager.updateViewLayout(rootView, mWindowLps);
            if (SHOW_OVERLAY_LAYER)
                mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);
            if (SHOW_OVERLAY_LAYER) cameraBinding.frameLayoutId.setVisibility(VISIBLE);
            setCameraViewLayer();
        } else {
            mWindowLps.alpha = 0.0f;
            mWindowLps.format = PixelFormat.TRANSLUCENT;
            mWindowLps.width = 0;
            mWindowLps.height = 0;
            mWindowManager.updateViewLayout(rootView, mWindowLps);
            if (SHOW_OVERLAY_LAYER)
                mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);

//        if (cameraBinding.getRoot().getParent() != null){
//          mWindowManager.removeView(cameraBinding.getRoot());
//          mWindowManager.removeView(mViewCameraBinding.getRoot());
//        }
        }

    }

    //初始化view
    protected void initView() {
        CallBackHelper.getInstance().setCallBackInterface(callBackInterface);
        registry.setCurrentState(Lifecycle.State.CREATED);
        if(AvmApp.getInstance().isRight){
            mViewCameraRightBinding = ViewCameraRightBinding.inflate(LayoutInflater.from(mContext), null, false);
        }else {
            mViewCameraBinding = ViewCameraBinding.inflate(LayoutInflater.from(mContext), null, false);
        }
        //mViewCameraBinding.cameraTextureView.setSurfaceTextureListener(new SurfaceTextureHelper
        // ());
        viewModel = new CameraViewModel();
        if(AvmApp.getInstance().isRight){
            mViewCameraRightBinding.setViewModel(viewModel);
        }else {
            mViewCameraBinding.setViewModel(viewModel);
        }
        findViewById();
        rearRadarViewId.setViewModel(viewModel);
        rearRadarFrontViewId.setViewModel(viewModel);
        settingView.setInfoBookView(infobook, infoBg, segmentWideAngle);
        layout3dTouchId.setOnTouchListener(this::onTouch);
        viewFrame.setOnTouchListener(this::onTouchView);
        rearviewMirrorView.setOnClickListener((v) -> {
        });

        Locale current = AvmApp.getInstance().getResources().getConfiguration().locale;
        String language = current.getLanguage();
        KLog.d(" 当前language："+language);
        if(language.equals("vi") || language.equals("ms") || language.equals("en")
                || language.equals("th") || language.equals("ru") || language.equals("de") || language.equals("nb")
                || language.equals("fr") || language.equals("it") || language.equals("es") || language.equals("esrUs") || language.equals("nl")
                || language.equals("sv") || language.equals("pt") || language.equals("ptrBR")){
            //越南
            ViewGroup.LayoutParams layoutParams = infobook.getLayoutParams();
            layoutParams.height = 270;
            infobook.setLayoutParams(layoutParams);
        }

//        settingView.setOnClickListener((v) -> {
//        });
        layout2d.setOnClickListener((view) -> {
            KLog.d(" layout2d setOnClickListener ");
            viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(0);
        });
        layout3d.setOnClickListener((view) -> {
            KLog.d(" layout3d setOnClickListener ");
            viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(1);
        });
        liftBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                KLog.i(" onClick: "+3);
                setViewDialog();
            }
        });
        layoutSettingId.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                KLog.i(" onClick: "+4);
                setViewDialog();
            }
        });
        viewRedChick();
        tabView();
        tabViewInit();

    }

    //初始化控件id
    private void findViewById() {
        if(AvmApp.getInstance().isRight){
            rearRadarViewId = mViewCameraRightBinding.rearRadarViewId;
            rearRadarFrontViewId = mViewCameraRightBinding.rearRadarFrontViewId;
            calibration = mViewCameraRightBinding.calibration;
            settingView = mViewCameraRightBinding.settingView;
            infobook = mViewCameraRightBinding.infobook;
            infoBg = mViewCameraRightBinding.infoBg;
            segmentWideAngle = mViewCameraRightBinding.segmentWideAngle;
            rearviewMirrorView = mViewCameraRightBinding.rearviewMirrorView;
            layout3dTouchId = mViewCameraRightBinding.layout3dTouchId;
            viewFrame = mViewCameraRightBinding.viewFrame;
            layout2d = mViewCameraRightBinding.layout2d;
            layout3d = mViewCameraRightBinding.layout3d;
            liftBg = mViewCameraRightBinding.liftBg;
            layoutSettingId = mViewCameraRightBinding.layoutSettingId;
            rootView = mViewCameraRightBinding.getRoot();
            segmentTab = mViewCameraRightBinding.segmentTab;
            viewShow2dGroupId = mViewCameraRightBinding.viewShow2dGroupId;
            viewShow3dGroupId = mViewCameraRightBinding.viewShow3dGroupId;
            radarSoundIv = mViewCameraRightBinding.radarSoundIv;
            radarfErrImgId1 = mViewCameraRightBinding.radarfErrImgId1;
            radarfErrImgId2 = mViewCameraRightBinding.radarfErrImgId2;
            radarfErrImgId3 = mViewCameraRightBinding.radarfErrImgId3;
            radarfErrImgId4 = mViewCameraRightBinding.radarfErrImgId4;
            radarErrImgId1 = mViewCameraRightBinding.radarErrImgId1;
            radarErrImgId2 = mViewCameraRightBinding.radarErrImgId2;
            radarErrImgId3 = mViewCameraRightBinding.radarErrImgId3;
            radarErrImgId4 = mViewCameraRightBinding.radarErrImgId4;
            cameraIv = mViewCameraRightBinding.cameraIv;
            llBackMirror = mViewCameraRightBinding.llBackMirror;
            llSetting = mViewCameraRightBinding.llSetting;
            layoutWideAngle = mViewCameraRightBinding.layoutWideAngle;
            camera2dBg = mViewCameraRightBinding.camera2dBg;
            camera3dBg = mViewCameraRightBinding.camera3dBg;
            cameraImageLayout = mViewCameraRightBinding.cameraImageLayout;
            cameraBreakdown = mViewCameraRightBinding.cameraBreakdown;
            toastNcBg = mViewCameraRightBinding.toastNcBg;
            toastBg = mViewCameraRightBinding.toastBg;
            cameraLeftFront = mViewCameraRightBinding.cameraLeftFront;
            cameraRightFront = mViewCameraRightBinding.cameraRightFront;
            cameraLeftRear = mViewCameraRightBinding.cameraLeftRear;
            cameraRightRear = mViewCameraRightBinding.cameraRightRear;
            smartGroupId = mViewCameraRightBinding.smartGroupId;
            layoutShowFull2d = mViewCameraRightBinding.layoutShowFull2d;
            cameraRight = mViewCameraRightBinding.cameraRight;
            radarSoundLayout = mViewCameraRightBinding.radarSoundLayout;
            parkingAssistLayout = mViewCameraRightBinding.parkingAssistLayout;
            cameraTop = mViewCameraRightBinding.cameraTop;
            cameraBottom = mViewCameraRightBinding.cameraBottom;
            cameraLift = mViewCameraRightBinding.cameraLift;
            layoutCalibrateId = mViewCameraRightBinding.layoutCalibrateId;
            mainAvmViewRootId = mViewCameraRightBinding.mainAvmViewRootId;
            ivBreakdown = mViewCameraRightBinding.ivBreakdown;
            tvBreakdown = mViewCameraRightBinding.tvBreakdown;
            ivSetting = mViewCameraRightBinding.ivSetting;
            ivBackMirror = mViewCameraRightBinding.ivBackMirror;
            manualCalibration = mViewCameraRightBinding.manualCalibration;
            automaticCalibration = mViewCameraRightBinding.automaticCalibration;
            infoTitle = mViewCameraRightBinding.infoTitle;
            infoContent = mViewCameraRightBinding.infoContent;
            infoOk = mViewCameraRightBinding.infoOk;
            rearRadarImgId = mViewCameraRightBinding.rearRadarImgId;
            rearRadarFrontId = mViewCameraRightBinding.rearRadarFrontId;
            //热区
            red2dTop = mViewCameraRightBinding.red2dTop;
            red2dLift = mViewCameraRightBinding.red2dLift;
            red2dRight = mViewCameraRightBinding.red2dRight;
            red2dBottom = mViewCameraRightBinding.red2dBottom;
            red3dleftFront = mViewCameraRightBinding.red3dleftFront;
            red3dleftFront1 = mViewCameraRightBinding.red3dleftFront1;
            red3drightFront = mViewCameraRightBinding.red3drightFront;
            red3drightFront1 = mViewCameraRightBinding.red3drightFront1;
            red3dleftRear = mViewCameraRightBinding.red3dleftRear;
            red3dleftRear1 = mViewCameraRightBinding.red3dleftRear1;
            red3drightRear = mViewCameraRightBinding.red3drightRear;
            red3drightRear1 = mViewCameraRightBinding.red3drightRear1;
            cameraImageLayoutLift = mViewCameraRightBinding.cameraImageLayoutLift;
            cameraIvLift = mViewCameraRightBinding.cameraIvLift;
            rlClose = mViewCameraRightBinding.rlClose;
            clCon = mViewCameraRightBinding.clCon;
            conRadarError = mViewCameraRightBinding.conRadarError;
            conRadar = mViewCameraRightBinding.conRadar;
        }else {
            rearRadarViewId = mViewCameraBinding.rearRadarViewId;
            rearRadarFrontViewId = mViewCameraBinding.rearRadarFrontViewId;
            calibration = mViewCameraBinding.calibration;
            settingView = mViewCameraBinding.settingView;
            infobook = mViewCameraBinding.infobook;
            infoBg = mViewCameraBinding.infoBg;
            segmentWideAngle = mViewCameraBinding.segmentWideAngle;
            rearviewMirrorView = mViewCameraBinding.rearviewMirrorView;
            layout3dTouchId = mViewCameraBinding.layout3dTouchId;
            viewFrame = mViewCameraBinding.viewFrame;
            layout2d = mViewCameraBinding.layout2d;
            layout3d = mViewCameraBinding.layout3d;
            liftBg = mViewCameraBinding.liftBg;
            layoutSettingId = mViewCameraBinding.layoutSettingId;
            rootView = mViewCameraBinding.getRoot();
            segmentTab = mViewCameraBinding.segmentTab;
            viewShow2dGroupId = mViewCameraBinding.viewShow2dGroupId;
            viewShow3dGroupId = mViewCameraBinding.viewShow3dGroupId;
            radarSoundIv = mViewCameraBinding.radarSoundIv;
            radarfErrImgId1 = mViewCameraBinding.radarfErrImgId1;
            radarfErrImgId2 = mViewCameraBinding.radarfErrImgId2;
            radarfErrImgId3 = mViewCameraBinding.radarfErrImgId3;
            radarfErrImgId4 = mViewCameraBinding.radarfErrImgId4;
            radarErrImgId1 = mViewCameraBinding.radarErrImgId1;
            radarErrImgId2 = mViewCameraBinding.radarErrImgId2;
            radarErrImgId3 = mViewCameraBinding.radarErrImgId3;
            radarErrImgId4 = mViewCameraBinding.radarErrImgId4;
            cameraIv = mViewCameraBinding.cameraIv;
            llBackMirror = mViewCameraBinding.llBackMirror;
            llSetting = mViewCameraBinding.llSetting;
            layoutWideAngle = mViewCameraBinding.layoutWideAngle;
            camera2dBg = mViewCameraBinding.camera2dBg;
            camera3dBg = mViewCameraBinding.camera3dBg;
            cameraImageLayout = mViewCameraBinding.cameraImageLayout;
            cameraBreakdown = mViewCameraBinding.cameraBreakdown;
            toastNcBg = mViewCameraBinding.toastNcBg;
            toastBg = mViewCameraBinding.toastBg;
            cameraLeftFront = mViewCameraBinding.cameraLeftFront;
            cameraRightFront = mViewCameraBinding.cameraRightFront;
            cameraLeftRear = mViewCameraBinding.cameraLeftRear;
            cameraRightRear = mViewCameraBinding.cameraRightRear;
            smartGroupId = mViewCameraBinding.smartGroupId;
            layoutShowFull2d = mViewCameraBinding.layoutShowFull2d;
            cameraRight = mViewCameraBinding.cameraRight;
            radarSoundLayout = mViewCameraBinding.radarSoundLayout;
            parkingAssistLayout = mViewCameraBinding.parkingAssistLayout;
            cameraTop = mViewCameraBinding.cameraTop;
            cameraBottom = mViewCameraBinding.cameraBottom;
            cameraLift = mViewCameraBinding.cameraLift;
            layoutCalibrateId = mViewCameraBinding.layoutCalibrateId;
            mainAvmViewRootId = mViewCameraBinding.mainAvmViewRootId;
            ivBreakdown = mViewCameraBinding.ivBreakdown;
            tvBreakdown = mViewCameraBinding.tvBreakdown;
            ivSetting = mViewCameraBinding.ivSetting;
            ivBackMirror = mViewCameraBinding.ivBackMirror;
            manualCalibration = mViewCameraBinding.manualCalibration;
            automaticCalibration = mViewCameraBinding.automaticCalibration;
            infoTitle = mViewCameraBinding.infoTitle;
            infoContent = mViewCameraBinding.infoContent;
            infoOk = mViewCameraBinding.infoOk;
            rearRadarImgId = mViewCameraBinding.rearRadarImgId;
            rearRadarFrontId = mViewCameraBinding.rearRadarFrontId;
            //热区
            red2dTop = mViewCameraBinding.red2dTop;
            red2dLift = mViewCameraBinding.red2dLift;
            red2dRight = mViewCameraBinding.red2dRight;
            red2dBottom = mViewCameraBinding.red2dBottom;
            red3dleftFront = mViewCameraBinding.red3dleftFront;
            red3dleftFront1 = mViewCameraBinding.red3dleftFront1;
            red3drightFront = mViewCameraBinding.red3drightFront;
            red3drightFront1 = mViewCameraBinding.red3drightFront1;
            red3dleftRear = mViewCameraBinding.red3dleftRear;
            red3dleftRear1 = mViewCameraBinding.red3dleftRear1;
            red3drightRear = mViewCameraBinding.red3drightRear;
            red3drightRear1 = mViewCameraBinding.red3drightRear1;
            cameraImageLayoutLift = mViewCameraBinding.cameraImageLayoutLift;
            cameraIvLift = mViewCameraBinding.cameraIvLift;
            rlClose = mViewCameraBinding.rlClose;
            clCon = mViewCameraBinding.clCon;
            conRadarError = mViewCameraBinding.conRadarError;
            conRadar = mViewCameraBinding.conRadar;
        }
    }

    public void hidenMirrowView(){
        rearviewMirrorView.hidenMirrowView();
    }

    public void viewRearStatus(int status) {
        rearviewMirrorView.reverseLight(status);
    }

    public CameraViewModel getViewModel() {
        return viewModel;
    }

    public void setCurrentGear(int gear) {
        if (rearviewMirrorView.getVisibility() == View.VISIBLE){
            rearviewMirrorView.gearInfo(AvmRuntime.self().isRearGearSts() ? 1 : 0);
        }
        rlClose.setVisibility(AvmRuntime.self().isRearGearSts() ? GONE : VISIBLE);
    }

    /**
     * 激活类型
     *
     * @param model
     */
//    private ViewType hisModel = ViewType.gear_D;
//    private ViewType hisModelTurn = ViewType.gear_D;// 转向退出的时候的模式
//    private  boolean isChangeGear = false; // 用来判断是否换挡，如果，换挡测不取消按钮高亮
    public void viewShowStatus() {
        int turnExitReverseIn = -1;

//       if (model == ViewType.gear_turn_exit && hisModel == ViewType.ReverseIn){
//         turnExitReverseIn = 1;
//         hisModelTurn = model;
//       }
//        isChangeGear = true;
        KLog.d("viewShowStatus()");
        isDismissView = false;
        //radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_nor));
        rearviewMirrorView.gearInfo(AvmRuntime.self().isRearGearSts() ? 1 : 0);
//       KLog.d(hisModel + "  valGear viewShowStatus 模式：" + model + " 记忆模式: " + viewPosition);
        int settingPathLine = SystemProperties.getInt("settingPathLine", -1);
        if (settingPathLine == 1) {
//            bvavmJNI.bwSetTrajLineStatus((byte) 1);
            if (AvmApp.getInstance().getCameraView() != null) AvmApp.getInstance().getCameraView().getViewModel().setTrajLineEnable((byte) 1);
        }
//        hisModel = model;
//        viewModel.setmHisModel(hisModel);
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        if (AvmRuntime.self().isRearGearSts()) {
            if (AvmService.JNI_IN_THREAD_FLAG) {
                AvmApp.getInstance().getCameraView().getViewModel().updateTrajLineStatus(3);
            } else {
                BvAvmJNIHelper.getInstance().updateTrajLineStatus(3);
            }
        } else {
            if (AvmService.JNI_IN_THREAD_FLAG) {
                AvmApp.getInstance().getCameraView().getViewModel().updateTrajLineStatus(0);
            } else {
                BvAvmJNIHelper.getInstance().updateTrajLineStatus(0);
            }
        }
        if (!isShowing) {
            KLog.d(isShowing + "  isShowing viewShowStatus viewPosition ：" + viewPosition);
            return;
        }

        int outsideTabIndex = getOutsideTabIndex();
        if (outsideTabIndex != -1) segmentTab.setSelectTab(outsideTabIndex);
//        if (hisPosition > 0) {
//            segmentTab.setSelectTab(hisPosition);
//            hisPosition = -1;
//        }
        if (isSmartWin) {
            KLog.d("  isShowing isSmartWin  ：" + isSmartWin);
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }

        List<Integer> events = AvmRuntime.self().getEvents();
        if (events.contains(DataDefine.EVT_SHIFT_D) || events.contains(DataDefine.EVT_SHIFT_N) || events.contains(DataDefine.EVT_SHIFT_R) || events.contains(DataDefine.EVT_SHIFT_P)) {//档位有变
            if (AvmRuntime.self().isRearGearSts()) {
                rearviewMirrorView.gearInfo(1); // 后视镜下翻按钮可操作
                viewModelReverseIn();
                int status = CanManager.getInstance().getIntStatus(CLUSTER_CHIME_PAS_WARNTONE, 0);
                KLog.d("雷达报警图标状态:" + status);
//                if (0 < status && status < 6) {
//                    radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_sel));
//                } else {
//                    radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_nor));
//                }
            } else {
                viewModelByActive("gear_P_D_N");
            }
        }
        if (events.contains(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE) || events.contains(DataDefine.EVT_TURN_LAMP_ACTIVE) || events.contains(DataDefine.EVT_TURN_LAMP_RESET)) {//转向有变
            if (AvmRuntime.self().getTurnDirect() == DataDefine.SENSOR_NONE) {
                if (AvmRuntime.self().isRearGearSts()) {
                    viewModelByturnToRever();
                } else {
                    viewModelByActive("gear_turn_exit");
                }
            } else if (AvmRuntime.self().getTurnDirect() == DataDefine.SENSOR_TURN_LAMP_LEFT) {
                KLog.d("转向3d前FRONT_3D 1");
                leftModel();
            } else if (AvmRuntime.self().getTurnDirect() == DataDefine.SENSOR_TURN_LAMP_RIGHT) {
                rightModel();
            }
        }
        setWindowType();
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
    }

    private void setWindowType() {
        KLog.d("setWindowType()");
        boolean bl = rootView.isAttachedToWindow();
        int newHeight = mContext.getResources().getDimensionPixelSize(R.dimen.screen_height);
        if (AvmRuntime.self().isRearGearSts()) {
            newHeight = 1080;
        }
        if (mWindowLps.height == newHeight) return;
        mWindowLps.height = newHeight;

        KLog.d("刷新--setWindowType-bl ：" + bl);
//        CameraGLSurfaceView.glStatus ;
        if (bl && CameraGLSurfaceView.glStatus == 1 && isShowing) {
            mWindowManager.updateViewLayout(rootView, mWindowLps);
            mWindowLps.format = PixelFormat.UNKNOWN;
            if (SHOW_OVERLAY_LAYER)
                mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);
            if (SHOW_OVERLAY_LAYER) cameraBinding.frameLayoutId.setVisibility(VISIBLE);
            setCameraViewLayer();
        }

    }

    private void viewModelByturnToRever() {
    }

    private void turnExitGear() {

    }

    public void setRadarFailStatus(int flag, int value) {
        if (mViewCameraBinding == null && mViewCameraRightBinding == null) return;
        switch (flag) {
            case 1:
                radarErrImgId1.setVisibility(value == 0 ? GONE : VISIBLE);
                break;
            case 2:
                radarErrImgId2.setVisibility(value == 0 ? GONE : VISIBLE);
                break;
            case  3:
                radarErrImgId3.setVisibility(value == 0 ? GONE :VISIBLE);
                break;
            case 4:
                radarErrImgId4.setVisibility(value == 0 ? GONE : VISIBLE);
                break;
        }
//        if (value > 0) {
//            RearviewToast.getInstance().showToast(AvmApp.getInstance().getString(R.string.camera_radar_error));
//        }
        if(radarErrImgId1.getVisibility() == GONE
                && radarErrImgId2.getVisibility() == GONE
                && radarErrImgId3.getVisibility() == GONE
                && radarErrImgId4.getVisibility() == GONE
                && radarfErrImgId1.getVisibility() == GONE
                && radarfErrImgId2.getVisibility() == GONE
                && radarfErrImgId3.getVisibility() == GONE
                && radarfErrImgId4.getVisibility() == GONE) {
            NotCloseToast.getInstance().cancelToast();
        }else {
            NotCloseToast.getInstance().showToast(AvmApp.getInstance().getString(R.string.camera_radar_error));
        }
    }

    public void setFrontRadarFailStatus(int flag, int value) {
        if (mViewCameraBinding == null && mViewCameraRightBinding == null) return;
        switch (flag) {
            case 1:
                radarfErrImgId1.setVisibility(value == 0 ? GONE : VISIBLE);
                break;
            case 2:
                radarfErrImgId2.setVisibility(value == 0 ? GONE : VISIBLE);
                break;
            case  3:
                radarfErrImgId3.setVisibility(value == 0 ? GONE :VISIBLE);
                break;
            case 4:
                radarfErrImgId4.setVisibility(value == 0 ? GONE : VISIBLE);
                break;
        }
//        if (value > 0) {
//            RearviewToast.getInstance().showToast(AvmApp.getInstance().getString(R.string.camera_radar_error));
//        }
        if(radarErrImgId1.getVisibility() == GONE
                && radarErrImgId2.getVisibility() == GONE
                && radarErrImgId3.getVisibility() == GONE
                &  radarErrImgId4.getVisibility() == GONE
                && radarfErrImgId1.getVisibility() == GONE
                && radarfErrImgId2.getVisibility() == GONE
                && radarfErrImgId3.getVisibility() == GONE
                && radarfErrImgId4.getVisibility() == GONE) {
            NotCloseToast.getInstance().cancelToast();
        }else {
            NotCloseToast.getInstance().showToast(AvmApp.getInstance().getString(R.string.camera_radar_error));
        }
    }

    /**
     * 左转向
     */
    private void leftModel() {
        if (viewPosition == 2 || AvmRuntime.self().isRearGearSts()) {
            return;
        }
        int status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
        bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        if (viewPosition == 0) {
            status = bvavmJNI.BW_LEFT_RIGHT_FRONT;
            chick2DView(CAMERA_2_D_LIFT_RIGHT);
            viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 1) {
            status = bvavmJNI.BW_LEFT_REAR_3D;
            chick3DView(CAMERA_3_D_LEFT_REAR);
            viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        }
        CameraGLSurfaceView.setAngleOfView(status);
    }

    /**
     * 右转向
     */
    private void rightModel() {
        if (viewPosition == 2 || AvmRuntime.self().isRearGearSts()) {
            return;
        }
        int status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
        if (viewPosition == 0) {
            status = bvavmJNI.BW_LEFT_RIGHT_FRONT;
            chick2DView(CAMERA_2_D_LIFT_RIGHT);
            viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 1) {
            bvavmJNI.bwSet3DfreeFlag(0);//复位3D
            status = bvavmJNI.BW_RIGHT_REAR_3D;
            chick3DView(CAMERA_3_D_RIGHT_REAR);
            viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        }
        CameraGLSurfaceView.setAngleOfView(status);


    }

    private void clickShowModel() {
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        if (AvmRuntime.self().isRearGearSts()) {
            viewModelReverseInClick();
            return;
        }
        viewModelByActive("clickShowModel");
    }

    /**
     * R档的时候视角切换
     */
    private void viewModelReverseInClick() {
        KLog.d(isSmartWin + " AvmRuntime valGear R档视角切换viewModelReverseInClick:" + viewPosition);
        // 广角
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }

        if (tabSelectFromUser) {
            int status = 0;
            if (viewPosition == 0) {
                status = bvavmJNI.BW_2D_REAR_UNDISTORT;
                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                chick2DView(CAMERA_2_D_BOTTOM);
            } else if (viewPosition == 1) {
                status = bvavmJNI.BW_FRONT_3D;
                chick3DView(CAMERA_3_D);
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_back
                        : R.mipmap.ic_camera_card_rback));
            } else if (viewPosition == 2) {
//                status = bvavmJNI.BW_2D_REAR_120;
                if (AvmRuntime.self().isRearGearSts()) {
                    setAngleStatus(bvavmJNI.BW_2D_REAR_120);
                } else {
                    setAngleStatus(bvavmJNI.BW_2D_FRONT_120);
                }
            }
            if (status != 0) CameraGLSurfaceView.setAngleOfView(status);
        } else {
            updateTabViewIndex();
        }
//        CameraGLSurfaceView.setAngleOfView(status);

    }

    /**
     * 设置按钮方向
     */
    private void setButtonStatus() {

    }

    private void viewModelReverseIn() {
        KLog.d(hisPosition + " hisPosition R档视角切换:" + viewPosition);
        chick2DView(CAMERA_2_D_BOTTOM);
        chick3DView(CAMERA_3_D);
        //        int status = 0;
        int outsideTabIndex = getOutsideTabIndex();
        Log.d("AvmRuntime", "viewModelReverseIn() outsideTabIndex = " + outsideTabIndex);
        if (outsideTabIndex == 0) {
            //            status = bvavmJNI.BW_2D_REAR_UNDISTORT;
            //            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            chick2DView(CAMERA_2_D_BOTTOM);
        } else if (outsideTabIndex == 1) {
//            status = bvavmJNI.BW_2D_REAR_UNDISTORT;
            chick3DView(CAMERA_3_D);
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_back
                    : R.mipmap.ic_camera_card_rback));
        } else if (outsideTabIndex == 2) {
            //            status = bvavmJNI.BW_2D_REAR_UNDISTORT;
            //            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            setAngleStatus(bvavmJNI.BW_2D_REAR);
        }
        //        CameraGLSurfaceView.setAngleOfView(status);
        //这里注释是为了改这个BUG，在记忆3D/广角模式下，R档激活，手动切换改变记忆3D/广角，没有被记忆
        hisPosition = viewPosition;

        //        updateTabViewIndex();
    }


    private void viewModelByActive(String position) {
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        KLog.d(viewPosition + "  viewPosition viewModelByActive 显示位置:" + position);
        //        KLog.d(viewPosition + "  当前转向:" + CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT));
        //        KLog.d(viewPosition + "  当前档位:" + hisModel);
        //        int status = 0;
        if (viewPosition == 0) {
            //如果进来转向灯还在，不在R档的情况下，显示左右视图
            if (CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT) == 1 || CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT) == 2 && !AvmRuntime.self().isRearGearSts()) {
//                status = bvavmJNI.BW_LEFT_RIGHT_FRONT;
//                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                chick2DView(CAMERA_2_D_LIFT_RIGHT);
            } else {
//                status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
//                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                chick2DView(CAMERA_2_D_TOP);
            }

            viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 1) {
            //如果进来转向灯还在，不在R档的情况下，显示左右轉向試圖
            if (CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT) == 1 && AvmRuntime.self().isRearGearSts()) {
//                status = bvavmJNI.BW_LEFT_REAR_3D;
                chick3DView(CAMERA_3_D_LEFT_REAR);
            } else if (CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT) == 2 && !AvmRuntime.self().isRearGearSts()) {
//                status = bvavmJNI.BW_RIGHT_REAR_3D;
                chick3DView(CAMERA_3_D_RIGHT_REAR);
//                AvmRuntime.self().clickLeftCard();
            } else {
//                status = bvavmJNI.BW_REAR_3D;
                chick3DView(CAMERA_3_D);
            }
            viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 2) {
            if (position.equals("gear_turn_exit")) {
                return;
            }
//            status = bvavmJNI.BW_2D_FRONT_120;
            setAngleStatus(bvavmJNI.BW_2D_FRONT_120);
        } else {
//            status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        }
//        CameraGLSurfaceView.setAngleOfView(status);
    }

    private boolean tabSelectFromUser = false;
    //    private int tabClickCnt = 0;
//    private long tabClickTime = 0;
    private OnTabSelectListener tabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position, boolean fromUser) {
//            long time = System.currentTimeMillis();
//            if ((time - tabClickTime) > 800) {
//                tabClickCnt = 0;
//            } else {
//                tabClickCnt++;
//            }
//            tabClickTime = time;
//            if (tabClickCnt >= 10) {
//                getViewModel().setManualCalibration1();
//                tabClickCnt = 0;
//            }
//            Log.d("Cal", "tabClickCnt = " + tabClickCnt + " , tabClickTime = " + tabClickTime + " , " + (time-tabClickTime));

            tabSelectFromUser = fromUser;
            Log.d("AvmRuntime", "onTabSelect : " + position + " , " + viewPosition + " , fromUser is " + fromUser);
            if (fromUser) {
                if (position == 0) {
                    AvmRuntime.self().setMemoryMode(DataDefine.MEM_MODE_2D);
                } else if (position == 1) {
                    AvmRuntime.self().setMemoryMode(DataDefine.MEM_MODE_3D);
                } else if (position == 2) {
                    AvmRuntime.self().setMemoryMode(DataDefine.MEM_MODE_WIDE_ANGLE);
                }
            }
            boolean isChangeGear = false;
            List<Integer> events = AvmRuntime.self().getEvents();
            if (events.contains(DataDefine.EVT_SHIFT_D) || events.contains(DataDefine.EVT_SHIFT_N) || events.contains(DataDefine.EVT_SHIFT_R) || events.contains(DataDefine.EVT_SHIFT_P)) {
                isChangeGear = true;
            }
            KLog.d(viewPosition + "tabSelectListener onTabSelect = " + position + " isChangeGear：" + isChangeGear);
            viewModel.setRunning(true);
//            if (!isChangeGear) {// 换挡的时候，不给取消高亮
//                llBackMirror.setSelected(false);
//                llSetting.setSelected(false);
//            }

            if (viewPosition == position) {
//                if (isChangeGear && viewPosition == 2) {
//                    setAngleStatus(bvavmJNI.BW_2D_FRONT_120);
//                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
//                }
                return;
            }
            viewPosition = position;// 是否点击了2d按钮
            KLog.d("AvmRuntime viewPosition set to " + position);
            hidViewButtonTimer.start(position);
//            bvavmJNI.bwSet3DfreeFlag(0);//复位3D
            if (position == 0) {
                DataManager.writeFault(DataConstant.Code.ST_2D);
                layout2d.setEnabled(true);
                layout3d.setEnabled(false);
                KLog.d(" layout2d tab ");
                layout2d.setVisibility(View.VISIBLE);
                layout3d.setVisibility(View.GONE);
                layoutWideAngle.setVisibility(GONE);
                camera2dBg.setVisibility(View.VISIBLE);
                camera3dBg.setVisibility(View.GONE);
                if (!isSmartWin) {
                    cameraImageLayout.setVisibility(VISIBLE);
                } else {
                    cameraImageLayout.setVisibility(GONE);
                }
//                camera3DDirection = bvavmJNI.BW_2D_FRONT;
                chick2DView(ViewSwitchManager.CAMERA_2_D_TOP);
            } else if (position == 1) {
                DataManager.writeFault(DataConstant.Code.ST_3D);
                layout3d.setEnabled(true);
                layout2d.setEnabled(false);
                layout2d.setVisibility(View.GONE);
                layout3d.setVisibility(View.VISIBLE);
                layoutWideAngle.setVisibility(GONE);
                camera2dBg.setVisibility(View.GONE);
                camera3dBg.setVisibility(View.VISIBLE);
                if (!isSmartWin) {
                    cameraImageLayout.setVisibility(VISIBLE);
                } else {
                    cameraImageLayout.setVisibility(GONE);
                }
//                camera3DDirection = bvavmJNI.BW_LEFT_FRONT_3D;
                KLog.d("转向3d前FRONT_3D 3");
                bvavmJNI.bwSet3DfreeFlag(0);//复位3D
                cameraBreakdown.setVisibility(View.GONE);
                chick3DView(ViewSwitchManager.CAMERA_3_D);
            } else {
                DataManager.writeFault(DataConstant.Code.ST_ANGLE);
                cameraShowType = 0;
                segmentWideAngle.setTabSelect();
                //setAngleStatus();
                KLog.d("tabSelectListener isSmartWin = " + isSmartWin);
                if (!isSmartWin) {//三分之一屏不显示
                    layoutWideAngle.setVisibility(VISIBLE);
                } else {
                    layoutWideAngle.setVisibility(GONE);
                }
                cameraImageLayout.setVisibility(View.GONE);
                //layoutWideAngle.setVisibility(VISIBLE);
                cameraImageLayoutLift.setVisibility(GONE);
                camera2dBg.setVisibility(View.GONE);
                camera3dBg.setVisibility(View.GONE);
                cameraBreakdown.setVisibility(View.GONE);
            }
            SystemProperties.set("tabSelect", String.valueOf(position));

//            viewShowStatus(hisModel);
            clickShowModel();
            if (!isChangeGear && AvmRuntime.self().isRearGearSts()) {
                hisPosition = viewPosition;
            }
            KLog.d("tabSelectListener settingView = " + isChangeGear);
            if (settingView.getVisibility() == View.VISIBLE) {
                llSetting.setSelected(false);
                settingView.setVisibility(GONE);
                liftBg.setVisibility(GONE);
            }
            if (infobook.getVisibility() == VISIBLE) {
                infobook.setVisibility(GONE);
                infoBg.setVisibility(GONE);
                liftBg.setVisibility(GONE);
            }
            if (rearviewMirrorView.getVisibility() == VISIBLE) {
                llBackMirror.setSelected(false);
                rearviewMirrorView.setVisibility(View.GONE);
                liftBg.setVisibility(GONE);
            }
        }

        @Override
        public void onTabSameSelect(int position, boolean fromUser) {
            KLog.d("onTabSameSelect = " + position);
        }
    };

    /**
     * 广角被点
     */
    @SuppressLint("NewApi")
    private void setAngleStatus(int type) {
        int tabIndex = getWideAngleTabIndex();
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        segmentWideAngle.setVisibility(VISIBLE);
        KLog.d("setTabSelect 广角切换 = " + type);
        if (type == bvavmJNI.BW_2D_FRONT_120) {
            cameraShowType = 1;
            if (tabIndex != -1) segmentWideAngle.setSelectTab(tabIndex);
        } else if (type == bvavmJNI.BW_2D_REAR_120 || type == bvavmJNI.BW_2D_REAR_UNDISTORT) {
            cameraShowType = 3;
            if (tabIndex != -1) segmentWideAngle.setSelectTab(tabIndex);
        } else if (type == bvavmJNI.BW_LEFT_RIGHT_FRONT) {
            if (tabIndex != -1) segmentWideAngle.setSelectTab(tabIndex);
        } else {
            if (tabIndex != -1) segmentWideAngle.setSelectTab(tabIndex);
        }

        viewShow2dGroupId.setVisibility(GONE);
        viewShow3dGroupId.setVisibility(GONE);
        layout2d.setEnabled(false);
        layout3d.setEnabled(false);
    }

    private OnTabSelectListener onTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position, boolean fromUser) {
            KLog.d("onTabSelectListener onTabSelect = " + position);
            viewModel.setRunning(true);
            if (position == 0) {
                if (segmentTab.getCurrentTab() == 2) {
                    cameraShowType = 0;
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
                    SystemProperties.set("tabSelectWideAngle", "0");
                    //                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.front_wide_angle));
                }
            } else if (position == 1) {
                if (segmentTab.getCurrentTab() == 2) {
                    cameraShowType = 1;
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_120);
                    SystemProperties.set("tabSelectWideAngle", "1");
                    //                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.back_wide_angle));
                }
            } else if (position == 2) {
                if (segmentTab.getCurrentTab() == 2) {
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_RIGHT_FRONT);
                    SystemProperties.set("tabSelectWideAngle", "2");
//                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.before));
                }
            } else if (position == 3) {
                if (segmentTab.getCurrentTab() == 2) {
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_RIGHT_BACK);
                    SystemProperties.set("tabSelectWideAngle", "3");
//                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.rear_wheel));
                }
            }
            SystemProperties.set("tabSelectWideAngle", String.valueOf(position));
        }

        @Override
        public void onTabSameSelect(int position, boolean fromUser) {

        }
    };

    private void tabViewInit() {
        RearviewToast.getInstance().init(toastBg);
        NotCloseToast.getInstance().init(toastNcBg);
        segmentTab.setOnTabSelectListener(tabSelectListener);
        segmentWideAngle.setOnTabSelectListener(onTabSelectListener);
        cameraIv.setOnClickListener(this::onCameraIv);
    }


    @SuppressLint("NewApi")
    private void tabView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(segmentTab.getLayoutParams());
        //layoutParams.width = (304*getDescValueArray().length);
        layoutParams.leftMargin = 18;
        segmentTab.setTabWidth(134.5f);
        segmentTab.setBackground(mContext.getResources().getDrawable(R.drawable.tab_selector_thumb));
        segmentTab.setLayoutParams(layoutParams);
        segmentTab.setTabData(getDescValueArray());

        int outsideTabIndex = getOutsideTabIndex();
        Log.d("AvmRuntime", "tabView() outsideTabIndex = " + outsideTabIndex);
//        int tabSelect = SystemProperties.getInt("tabSelect", 0);
        KLog.d("2D+++++ tabSelect= " + outsideTabIndex);

        if (AvmService.JNI_IN_THREAD_FLAG) {
            if (AvmApp.getInstance().getCameraView() != null) AvmApp.getInstance().getCameraView().getViewModel().reset3D();
        } else {
            bvavmJNI.bwSet3DfreeFlag(0);//复位3D
        }

        if (outsideTabIndex == 0) {
            camera3DDirection = bvavmJNI.BW_2D_FRONT_UNDISTORT;
            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            //判断轨迹线有没有打开，打开就显示2D轨迹线
            int settingPathLine = SystemProperties.getInt("settingPathLine", -1);
            KLog.d("设置车辅线:" + settingPathLine);
            if (settingPathLine == 1) {
                if (AvmService.JNI_IN_THREAD_FLAG) {
                    if (AvmApp.getInstance().getCameraView() != null) AvmApp.getInstance().getCameraView().getViewModel().updateTrajLineStatus(1);
                } else {
//                    bvavmJNI.bwSetTrajLineStatus((byte) 1);
                    if (AvmApp.getInstance().getCameraView() != null) AvmApp.getInstance().getCameraView().getViewModel().setTrajLineEnable((byte) 1);
                }
                //bvavmJNI.bwSetCarIsDgear((byte) 1);//2D前视
                //bvavmJNI.bwSetCarIsBack((byte) 0);//2D后视
            }
            if (!isSmartWin) {
                //需要记忆模式
                //2D
//                CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_UNDISTORT); //默认设置视角  2024  08 28
//                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                if (AvmRuntime.self().isRearGearSts()) {
                    chick2DView(CAMERA_2_D_BOTTOM);
                    KLog.d(" layout2d tabview .......bottom ");

                } else {
                    chick2DView(CAMERA_2_D_TOP);
                    KLog.d(" layout2d tabview .......top  ");
                }
                layout2d.setVisibility(View.VISIBLE);
                layout3d.setVisibility(View.GONE);
                layoutWideAngle.setVisibility(GONE);
                cameraImageLayout.setVisibility(VISIBLE);
            } else {
                cameraImageLayout.setVisibility(GONE);
            }
            camera2dBg.setVisibility(View.VISIBLE);
            camera3dBg.setVisibility(View.GONE);

        } else if (outsideTabIndex == 1) {
            KLog.d("转向3d前FRONT_3D 2："+CameraGLSurfaceView.getCameraDirection());

            if (!isSmartWin) {
                //                camera3DDirection = bvavmJNI.BW_LEFT_FRONT_3D;
                //                CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_FRONT_3D);
                layout2d.setVisibility(View.GONE);
                layout3d.setVisibility(View.VISIBLE);
                layoutWideAngle.setVisibility(GONE);
                if(CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_REAR_3D
                        || CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_FRONT_3D){
                    chick3DView(CAMERA_3_D);
                }else {
                    chick3DView(ViewSwitchManager.CAMERA_3_D_LEFT_FRONT);
                }
                cameraImageLayout.setVisibility(VISIBLE);
            } else {
                cameraImageLayout.setVisibility(GONE);
            }
            KLog.d("3D+++++ = ");
            camera2dBg.setVisibility(View.GONE);
            camera3dBg.setVisibility(View.VISIBLE);
        } else {
            if (!isSmartWin) {//三分之一屏不显示
                //                camera3DDirection = bvavmJNI.BW_2D_FRONT_120;
                //                CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
                layout2d.setVisibility(View.GONE);
                layout3d.setVisibility(View.GONE);
                cameraImageLayout.setVisibility(View.GONE);
                layoutWideAngle.setVisibility(VISIBLE);
            } else {
                layoutWideAngle.setVisibility(GONE);
            }
            camera2dBg.setVisibility(View.GONE);
            camera3dBg.setVisibility(View.GONE);
            cameraBreakdown.setVisibility(View.GONE);
            KLog.d("广角++++ = ");
        }
        // LinearLayout.LayoutParams layoutParamsAg =
        // new LinearLayout.LayoutParams(segmentWideAngle.getLayoutParams());
        segmentWideAngle.setTabWidth(207);
        segmentWideAngle.setBackground(mContext.getResources().getDrawable(R.mipmap.gj_bg));
        //layoutParamsAg.width = 720;
        //layoutParamsAg.height = 84;
        //segmentWideAngle.setLayoutParams(layoutParamsAg);
        segmentWideAngle.setTabData(getWideAngleValueArray());
        segmentWideAngle.setTextSelectColor(R.color.setting_view_title_color, 5);
        //        segmentWideAngle.setSelectTab(0);

        if (isFullWin) updateTabViewIndex();

//        if (hisPosition > 0) {
//            mViewCameraBinding.segmentTab.setSelectTab(hisPosition);
//            hisPosition = -1;
//        } else {
//            mViewCameraBinding.segmentTab.setSelectTab(tabSelect);
//        }
    }

    public boolean isSmartWin = false;
    public boolean isFullWin = false;

    private void setSafView() {
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mViewCameraBinding.cameraSurfaceView.getLayoutParams();
//        layoutParams.setMargins(42,80,0,0);


    }

    /**
     * 显示1/3 屏
     */
    public void showSmartWin() {
        Log.i(TAG, "valGear showSmartWin: 显示1/3屏幕");
        if (isSmartWin || isFullWin) {
            Log.i(TAG, "showSmartWin: 已经显示1/3屏幕");
            return;
        }

        if (mWindowLps == null) return;

        mWindowLps.y = 84;
        mWindowLps.x = AvmApp.getInstance().isRight ? 1392 : 42;
        isSmartWin = true;
        mWindowLps.width = 486;
        mWindowLps.height = 870;
        ConstraintLayout.LayoutParams layoutParamsF = (ConstraintLayout.LayoutParams)viewFrame.getLayoutParams();
        layoutParamsF.height = 870;
        layoutParamsF.width = 486;
        layoutParamsF.topMargin = 0;
        viewFrame.setLayoutParams(layoutParamsF);
        if(mViewCameraRightBinding != null){
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)rlClose.getLayoutParams();
            layoutParams.topMargin = 9;
            layoutParams.setMarginEnd(9);
            rlClose.setLayoutParams(layoutParams);
            ConstraintLayout.LayoutParams layoutParamsRl = (ConstraintLayout.LayoutParams)radarSoundLayout.getLayoutParams();
            layoutParamsRl.topMargin = 9;
            layoutParamsRl.setMarginEnd(411);
            radarSoundLayout.setLayoutParams(layoutParamsRl);
            ConstraintLayout.LayoutParams layoutParamsClCon = (ConstraintLayout.LayoutParams)clCon.getLayoutParams();
            layoutParamsClCon.topMargin = 0;
            clCon.setLayoutParams(layoutParamsClCon);
            ConstraintLayout.LayoutParams layoutParamsCl = (ConstraintLayout.LayoutParams)conRadar.getLayoutParams();
            layoutParamsCl.setMarginEnd(0);
            conRadar.setLayoutParams(layoutParamsCl);
            ConstraintLayout.LayoutParams layoutParamsCr = (ConstraintLayout.LayoutParams)conRadarError.getLayoutParams();
            layoutParamsCr.setMarginEnd(0);
            layoutParamsCr.topMargin = 0;
            conRadarError.setLayoutParams(layoutParamsCr);
        }else {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)rlClose.getLayoutParams();
            layoutParams.topMargin = 9;
            layoutParams.setMarginStart(9);
            rlClose.setLayoutParams(layoutParams);
            ConstraintLayout.LayoutParams layoutParamsRl = (ConstraintLayout.LayoutParams)radarSoundLayout.getLayoutParams();
            layoutParamsRl.topMargin = 9;
            layoutParamsRl.setMarginStart(411);
            radarSoundLayout.setLayoutParams(layoutParamsRl);
            ConstraintLayout.LayoutParams layoutParamsClCon = (ConstraintLayout.LayoutParams)clCon.getLayoutParams();
            layoutParamsClCon.topMargin = 0;
            clCon.setLayoutParams(layoutParamsClCon);
            ConstraintLayout.LayoutParams layoutParamsCl = (ConstraintLayout.LayoutParams)conRadar.getLayoutParams();
            layoutParamsCl.setMarginStart(0);
            conRadar.setLayoutParams(layoutParamsCl);
            ConstraintLayout.LayoutParams layoutParamsCr = (ConstraintLayout.LayoutParams)conRadarError.getLayoutParams();
            layoutParamsCr.setMarginStart(0);
            layoutParamsCr.topMargin = 0;
            conRadarError.setLayoutParams(layoutParamsCr);
        }
        mWindowLps.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;

        mWindowLps.format = PixelFormat.TRANSLUCENT; // 设置透明背景
        clearFocus();

        showView();
        rootView.setVisibility(View.VISIBLE);

        smartGroupId.setVisibility(GONE);
        layoutWideAngle.setVisibility(GONE);
        layout2d.setVisibility(GONE);
        viewShow2dGroupId.setVisibility(GONE);
        cameraImageLayout.setVisibility(GONE);
        cameraImageLayoutLift.setVisibility(GONE);
        layoutShowFull2d.setVisibility(VISIBLE);
        cameraRight.setVisibility(GONE);
        cameraLeftFront.setVisibility(GONE);
        viewShow3dGroupId.setVisibility(GONE);
        cameraBreakdown.setVisibility(GONE);
        calibration.setVisibility(GONE);
        KLog.d("设置：1");
        //viewModel.startTestTimer();
        CameraGLSurfaceView.glStatus = 0;
//        AvmApp.getInstance().getViewBottom().showSmartWin();
    }

    private boolean isFristShowApp = true;
    /**
     * 显示全屏 页面
     * 如果，全屏显示，则不显示小屏
     */
    public void showFullWin() {
        if (mWindowLps == null) return;
        mWindowLps.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | //设置底部可以点击 周边点击添加 2024 08 29
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        Log.i(TAG, isSmartWin + " valGear showFullWin: 全屏显示  t底部透明 " + isFullWin + " 第一帧CameraGLSurfaceView：" + CameraGLSurfaceView.glStatus);
        if (isFullWin) {
            updateTabViewIndex();
            setWindowType();
            return;
        }
        cameraBinding.frameLayoutId.setVisibility(GONE);
        mWindowLps.width = mContext.getResources().getDimensionPixelSize(R.dimen.screen_width);
        if(mViewCameraRightBinding != null){
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)rlClose.getLayoutParams();
            layoutParams.topMargin = 93;
            layoutParams.setMarginEnd(51);
            rlClose.setLayoutParams(layoutParams);
            ConstraintLayout.LayoutParams layoutParamsRl = (ConstraintLayout.LayoutParams)radarSoundLayout.getLayoutParams();
            layoutParamsRl.topMargin = 93;
            layoutParamsRl.setMarginEnd(453);
            radarSoundLayout.setLayoutParams(layoutParamsRl);
            ConstraintLayout.LayoutParams layoutParamsClCon = (ConstraintLayout.LayoutParams)clCon.getLayoutParams();
            layoutParamsClCon.topMargin = 84;
            clCon.setLayoutParams(layoutParamsClCon);
            ConstraintLayout.LayoutParams layoutParamsCl = (ConstraintLayout.LayoutParams)conRadar.getLayoutParams();
            layoutParamsCl.setMarginEnd(42);
            conRadar.setLayoutParams(layoutParamsCl);
            ConstraintLayout.LayoutParams layoutParamsCr = (ConstraintLayout.LayoutParams)conRadarError.getLayoutParams();
            layoutParamsCr.setMarginEnd(42);
            layoutParamsCr.topMargin = 84;
            conRadarError.setLayoutParams(layoutParamsCr);
        }else{
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)rlClose.getLayoutParams();
            layoutParams.topMargin = 93;
            layoutParams.setMarginStart(51);
            rlClose.setLayoutParams(layoutParams);
            ConstraintLayout.LayoutParams layoutParamsRl = (ConstraintLayout.LayoutParams)radarSoundLayout.getLayoutParams();
            layoutParamsRl.topMargin = 93;
            layoutParamsRl.setMarginStart(453);
            radarSoundLayout.setLayoutParams(layoutParamsRl);
            ConstraintLayout.LayoutParams layoutParamsClCon = (ConstraintLayout.LayoutParams)clCon.getLayoutParams();
            layoutParamsClCon.topMargin = 84;
            clCon.setLayoutParams(layoutParamsClCon);
            ConstraintLayout.LayoutParams layoutParamsCl = (ConstraintLayout.LayoutParams)conRadar.getLayoutParams();
            layoutParamsCl.setMarginStart(42);
            conRadar.setLayoutParams(layoutParamsCl);
            ConstraintLayout.LayoutParams layoutParamsCr = (ConstraintLayout.LayoutParams)conRadarError.getLayoutParams();
            layoutParamsCr.setMarginStart(42);
            layoutParamsCr.topMargin = 84;
            conRadarError.setLayoutParams(layoutParamsCr);
        }
        if (AvmRuntime.self().isRearGearSts()) {
            mWindowLps.height = 1080;
        } else {
            mWindowLps.height = mContext.getResources().getDimensionPixelSize(R.dimen.screen_height);
        }

        smartGroupId.setVisibility(VISIBLE);
        mWindowLps.format = PixelFormat.UNKNOWN;

        isSmartWin = false;
        isFullWin = true;
        mWindowLps.x = 0;
        mWindowLps.y = 0;
        ConstraintLayout.LayoutParams layoutParamsF = (ConstraintLayout.LayoutParams)viewFrame.getLayoutParams();
        layoutParamsF.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParamsF.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParamsF.topMargin = 84;
        viewFrame.setLayoutParams(layoutParamsF);
        rootView.setVisibility(View.VISIBLE); // 设置了mWindow。flags之后 修复隐藏状态栏
        if(isFristShowApp && BvAvmJNIHelper.isAvmInit) {
            isFristShowApp = false;
            viewModel.setBwSetRVCStatus(4);
        }
        showComm();
        mMainHandler.postDelayed(() -> {
            // 延时隐藏，防止事件冲突
            layoutShowFull2d.setVisibility(GONE);
            llBackMirror.setVisibility(AvmApp.OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH==0 && AvmApp.OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH ==0 ? GONE :VISIBLE);
            showBottomView();
        }, 200);
    }

    public void showView() {
        if (isShowing) {
            return;
        }
        showComm();
    }

    private boolean isSmartWinToFull = false;

    public void showComm() {

        Log.i(TAG, " 开始 显示AVM showComm t底部透明： " + mWindowLps);
//        isSmartWin = false;
        updateWind();
        DataManager.writeFault(DataConstant.Code.COMMING_APP);
        DataManager.writeFault(DataConstant.Code.APK_OPEN);
        DataManager.writeFault(DataConstant.Code.BP_SHOW);
        isShowing = true;
        hidViewButtonTimer.start(viewPosition);
        smartGroupId.setVisibility(VISIBLE);

        tabView();
        skinView();
        //初始化进来也要显示上一次设置的透明度的车模
        showRadarSoundView();
//        setTransparentIndexTab();
//        SystemProperties.setGlobal("avm_state", 1);
//        AvmManager.getInstance(AvmApp.getInstance()).sendAvmState(1);
        int calibrateBtn = Settings.System.getInt(getContext().getContentResolver(), "avm.calibrate", 0);
        if (calibrateBtn > 0) {
            layoutCalibrateId.setVisibility(VISIBLE);
        }
        inputViewModel();
        isDismissView = false;
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
        }
        if (isFullWin) {// R档关闭avm，手动进来不需要记忆视角，需要回到2d后视角
            if (hisPosition > 0) {
                segmentTab.setSelectTab(hisPosition);
                hisPosition = -1;// R挡的时候需要记忆，广角或3d模式
            }
            settingView.checkButton();
        }

        rearviewMirrorView.setListener(onVisibilityListener);
        settingView.setListener(onVisibilityListenerSettingView);
        Log.i(TAG, isFullWin + "  isFullWin 显示AVM 结束 showComm isSmartWin： " + isSmartWin);
        llSetting.setSelected(false);
        llBackMirror.setSelected(false);
        CameraViewModelHelper.getInstance().setIsRadarActiveTow();
    }

    private void inputViewModel() {
//      KLog.d(isDismissView+" isDismissView hisModel= " + hisModel +"hisPosition= "+hisPosition + "viewPosition="+ viewPosition);
        KLog.d("inputViewModel()");
        if (isDismissView && hisPosition == 2) {

            if (AvmRuntime.self().isRearGearSts()) {
                CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_120);
                setAngleStatus(bvavmJNI.BW_2D_REAR_120);
                hisPosition = -1;// R挡的时候需要记忆，广角或3d模式

                if (AvmService.JNI_IN_THREAD_FLAG) {
                    AvmApp.getInstance().getCameraView().getViewModel().reset3D();
                } else {
                    BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
                }

                if (AvmService.JNI_IN_THREAD_FLAG) {
                    AvmApp.getInstance().getCameraView().getViewModel().updateTrajLineStatus(3);
                } else {
                    BvAvmJNIHelper.getInstance().updateTrajLineStatus(3);
                }

                isDismissView = false;
                return;
            }
        }
        viewShowStatus();
    }

    RearviewMirrorView.OnVisibilityListener onVisibilityListener = new RearviewMirrorView.OnVisibilityListener() {
        @Override
        public void Visibility(boolean isVisibility) {// 取消按钮高亮
            if (settingView.getVisibility() == GONE) {
                llSetting.setSelected(false);
            }
            if (rearviewMirrorView.getVisibility() == GONE) {
                llBackMirror.setSelected(false);
            }
        }
    };
    SettingView.OnVisibilityListener onVisibilityListenerSettingView = new SettingView.OnVisibilityListener() {
        @Override
        public void Visibility(boolean isVisibility) {// 取消按钮高亮
            if (settingView.getVisibility() == GONE) {
                llSetting.setSelected(false);
            }
            if (rearviewMirrorView.getVisibility() == GONE) {
                llBackMirror.setSelected(false);
            }
        }
    };

    private void setCameraViewLayer() {
        int left = mainAvmViewRootId.getPaddingLeft();
        int right = mainAvmViewRootId.getPaddingRight();
        int bottom = 0;
        int top = mainAvmViewRootId.getPaddingTop();
        KLog.i("mWindowLps.height....... " + mWindowLps.height + "mWindowLps.wight...  " + mWindowLps.width);
        KLog.i("mWindowLps.height.......isSmartWin " + isSmartWin);
        if (mWindowLps.height == 1080) {
            bottom = 90;
        }
        KLog.i("mWindowLps.height ....1.... left top right bottom : " + left + ", " + top + ", " + right + ", " + bottom);
        mainAvmViewRootId.setPadding(left, top, right, bottom);
    }


    /**
     * 更新窗口
     */
    @SuppressLint("WrongConstant")
    private void updateWind() {
        boolean attachedToWindow = rootView.isAttachedToWindow();
        mWindowLps.alpha = 0.0f;
        //      KLog.d("窗口层级 mWindowLpsBottom："+mWindowLpsBottom);

        cameraBinding.frameLayoutId.setVisibility(GONE);
        if (rootView.getParent() == null && !attachedToWindow) {
            if (SHOW_OVERLAY_LAYER) mWindowManager.addView(cameraBinding.getRoot(), mWindowLps);
            mWindowManager.addView(rootView, mWindowLps);
            return;
        }
        mWindowManager.updateViewLayout(rootView, mWindowLps);
        if (SHOW_OVERLAY_LAYER)
            mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);
    }

//    public View getRootView() {
//        if (mViewCameraBinding == null && mViewCameraRightBinding == null) return null;
//        return rootView;
//    }

    //R档时如果是2D状态，默认显示倒车视角及显示2D切换图标
    public void show2DView() {
        if (segmentTab.getCurrentTab() == 0) {
            KLog.d(" layout2d show2DView ");
            layout2d.setVisibility(View.VISIBLE);
            layout3d.setVisibility(View.GONE);
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_UNDISTORT);
            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            viewModel.setLiveDataCamera2DTopUI(ViewSwitchManager.CAMERA_2_D_BOTTOM);
        }
    }

    //雷达提示音显隐
    public void showRadarSoundView() {
        if (mViewCameraBinding == null && mViewCameraRightBinding == null) return;
        //获取当前雷达音开关状态
        int status = CanManager.getInstance().getIntStatus(ASSIST_DRIVE_PAS_BUTTON_PRESS, 0);
        KLog.d("雷达提示音 showRadarSoundView open status:"+status);
        radarSoundIv.setImageDrawable(mContext.getDrawable(status == 0 ? R.drawable.ic_radar_sound_open
                : R.drawable.ic_radar_sound_close));
        radarSoundLayout.setBackground(mContext.getDrawable(status == 0 ? R.drawable.shape_bg_nor_12
                : R.drawable.shape_bg_blue_12));
    }

    //雷达故障提示显隐
    public void showParkingAssistView(int isVisible) {
        if (isVisible == 0) {
            parkingAssistLayout.setVisibility(GONE);
        } else {
            parkingAssistLayout.setVisibility(VISIBLE);
        }
    }


    // 是否关闭，用来处理R档推出的时候，记忆广角、3d
    private boolean isDismissView = false;

    public void dismissView(String position) {
        KLog.d(position + " position dismissView isShowing = " + isShowing);
        tabSelectFromUser = false;
        if (!isShowing) {
            return;
        }
//        bottomDialog.dismiss();
        if (SHOW_OVERLAY_LAYER) cameraBinding.frameLayoutId.setVisibility(GONE);
        DataManager.writeFault(DataConstant.Code.BP_HIDE);
        isFullWin = false;
        isSmartWin = false;
        isShowing = false;
        camera3DShowType = -1;
        isDismissView = true;
        boolean attachedToWindow = rootView.isAttachedToWindow();
        boolean attachedToWindowcameraBinding = cameraBinding.getRoot().isAttachedToWindow();
        KLog.d(attachedToWindowcameraBinding + " attachedToWindowcameraBinding dismissView attached = " + attachedToWindow);
        mWindowLps.alpha = 0.0f;
        mWindowLps.width = 1;
        mWindowLps.height = 1;

        if (cameraBinding.getRoot().getParent() != null) {
//          mWindowManager.removeView(cameraBinding.getRoot());
//          mWindowManager.removeView(mViewCameraBinding.getRoot());
            if (SHOW_OVERLAY_LAYER)
                mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);
        }
        if(rootView.getParent() != null){
            mWindowManager.updateViewLayout(rootView, mWindowLps);
            rootView.setVisibility(View.GONE);
        }

        //释放摄像头画面数据
//            BvAvmJNIHelper.getInstance().avmDeInit();
//        SystemProperties.setGlobal("avm_state", 0);
//        AvmManager.getInstance(AvmApp.getInstance()).sendAvmState(0);
        if (settingView.getVisibility() == View.VISIBLE) {
            settingView.setVisibility(GONE);
            liftBg.setVisibility(GONE);
        }
        if (infobook.getVisibility() == VISIBLE) {
            infobook.setVisibility(GONE);
            infoBg.setVisibility(GONE);
            liftBg.setVisibility(GONE);
        }
        if (rearviewMirrorView.getVisibility() == VISIBLE) {
            rearviewMirrorView.setVisibility(View.GONE);
            liftBg.setVisibility(GONE);
        }
        CameraGLSurfaceView.glStatus = 0;
    }

    public void removeView() {
        if (mViewCameraBinding == null && mViewCameraRightBinding == null) return;
        boolean attachedToWindow = rootView.isAttachedToWindow();
        if (attachedToWindow) {
            mWindowManager.removeView(rootView);
            if (SHOW_OVERLAY_LAYER) mWindowManager.removeView(cameraBinding.getRoot());
        }
    }

    public void setBtnSettingSelectView(boolean btnSettingSelect) {
        this.btnSettingSelect = btnSettingSelect;
        llBackMirror.setSelected(false);
        if (settingView.getVisibility() == View.VISIBLE) {
            settingView.closeUI(true);
            infobook.setVisibility(GONE);
            settingView.setVisibility(GONE);
            liftBg.setVisibility(GONE);
            llSetting.setSelected(false);
            infoBg.setVisibility(GONE);

        } else {
            settingView.setVisibility(View.VISIBLE);
            liftBg.setVisibility(VISIBLE);
            llSetting.setSelected(true);
        }
        rearviewMirrorView.setVisibility(GONE);
        //        startCalibration();
        KLog.d("btnSettingSelect = " + btnSettingSelect);

    }

    public void setBtnRearSelectView(boolean btnRearSelect) {
        this.btnRearSelect = btnRearSelect;
        llSetting.setSelected(false);
        settingView.setVisibility(GONE);
        if (infobook.getVisibility() == VISIBLE) {
            infobook.setVisibility(GONE);
            infoBg.setVisibility(GONE);
            liftBg.setVisibility(GONE);
        }
        if (rearviewMirrorView.getVisibility() == VISIBLE) {
            rearviewMirrorView.setVisibility(View.GONE);
            llBackMirror.setSelected(false);
            liftBg.setVisibility(GONE);
        } else {
            llBackMirror.setSelected(true);
            rearviewMirrorView.setVisibility(View.VISIBLE);
            //            rearviewMirrorView.skinView();
            liftBg.setVisibility(VISIBLE);
        }
        KLog.d("setBtnRearSelectView = " + btnRearSelect);
        rearviewMirrorView.setRearviewMirrorDownViewStatus(AvmRuntime.self().isRearGearSts() ? 1 : 0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        KLog.d("onAttachedToWindow 获取焦点:");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        registry.setCurrentState(Lifecycle.State.DESTROYED);
        viewModel.removeCameraViewListener();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    private boolean isOnTouch = false;

    private int touchIndex = 0;

    public boolean onTouch(View v, MotionEvent event) {
        viewModel.setRunning(true);
        KLog.i(" onClick: "+7);
        if (infobook.getVisibility() != VISIBLE)
            llSetting.setSelected(false);
        llBackMirror.setSelected(false);
        KLog.i("onTouch: viewPosition=" + viewPosition);
        //去掉这个判断，避免2D跟广角无法点击屏幕消失设置跟后视镜
        /*if (viewPosition != 1) {
            return false;
        }*/

        int action = event.getAction();
        int touch_x = 0;
        int touch_y = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                KLog.i("calibrationBt - ACTION_DOWN");
                if (rearviewMirrorView.getVisibility() == View.VISIBLE) {
                    rearviewMirrorView.setVisibility(GONE);
                }
                if (settingView.getVisibility() == VISIBLE && infobook.getVisibility() != VISIBLE) {
                    settingView.setVisibility(GONE);
                    liftBg.setVisibility(GONE);
                }
                // 按下时，为开始坐标
                touch_x = (int) event.getRawX();
                touch_y = (int) event.getRawY();
                if (viewPosition == 1) {
                    bvavmJNI.bwSet3DfreeFlag(1);
                }
                break;
            case MotionEvent.ACTION_UP:

                // 手指起来时的，为结束的坐标
//                touch_x = (int) event.getRawX();
//                touch_y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指在屏幕移动时的左边，应要计算，大于一定范围时，才认为是在滑动，这里需要测试，差值不能太大，太大的话，会认为滑不动
                int endX = (int) event.getRawX();
                int endY = (int) event.getRawY();
                //蒙版存在的时候不可以拖动
                // 先不做计算处理
                if (viewPosition == 1 && infoBg.getVisibility() != VISIBLE) {
                    //3D的时候拖动车模
                    KLog.i(endX + " startX开始拖动车模bwSetTouchScreenPos " + endY);
                    int finalTouch_x = endX;
                    int finalTouch_y = endY;
                    boolean canMove = AvmApp.getInstance().isRight ? (endX > 66 && endX < 1356 && endY < 950 && endY > 113)
                            : (endX < 1860 && endX > 570 && endY < 950 && endY > 113);
                    if (canMove) {
                        mMainHandler.postDelayed(() -> {
                            int touchPos = bvavmJNI.bwSetTouchScreenPos(finalTouch_x, finalTouch_y);
                            KLog.i("滑动车模角度touchPos  " + touchPos);
                            if (touchIndex != touchPos) {
                                if (touchPos == 1) {
                                    cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_leftfront
                                            : R.mipmap.ic_camera_card_rleftfront));
                                    hidViewButtonTimer.start(1);
                                    chick3DView(ViewSwitchManager.CAMERA_3_D_LEFT_FRONT);
                                } else if (touchPos == 2) {
                                    cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_leftback
                                            : R.mipmap.ic_camera_card_rleftback));
                                    hidViewButtonTimer.start(1);
                                    chick3DView(ViewSwitchManager.CAMERA_3_D_LEFT_REAR);
                                } else if (touchPos == 3) {
                                    cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_rightfront
                                            : R.mipmap.ic_camera_card_rrightfront));
                                    hidViewButtonTimer.start(1);
                                    chick3DView(ViewSwitchManager.CAMERA_3_D_RIGHT_FRONT);
                                } else if (touchPos == 4) {
                                    cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_rightback
                                            : R.mipmap.ic_camera_card_rrightback));
                                    hidViewButtonTimer.start(1);
                                    chick3DView(ViewSwitchManager.CAMERA_3_D_RIGHT_REAR);
                                }
                            }
                            touchIndex = touchPos;
                        }, 30);
                    }


                }
                break;
        }
        return true;
    }

    public boolean onTouchView(View v, MotionEvent event) {
        viewModel.setRunning(true);
        KLog.i(" onClick: "+8);
        KLog.i("onTouch: viewPosition=" + viewPosition);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                llBackMirror.setSelected(false);
                llSetting.setSelected(false);
                if (rearviewMirrorView.getVisibility() == View.VISIBLE) {
                    rearviewMirrorView.setVisibility(GONE);
                }
                if (settingView.getVisibility() == VISIBLE) {
                    settingView.setVisibility(GONE);
                    liftBg.setVisibility(GONE);
                }
                if (infobook.getVisibility() == VISIBLE) {
                    infobook.setVisibility(GONE);
                    infoBg.setVisibility(GONE);
                    liftBg.setVisibility(GONE);
                }
                break;
            case MotionEvent.ACTION_UP:

                // 手指起来时的，为结束的坐标
//                touch_x = (int) event.getRawX();
//                touch_y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;


        }


        return true;
    }


    private final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case CALIBRATION_SHOW_WINDOW:
                    KLog.w("CALIBRATION_SHOW_WINDOW");
                    calibration.setVisibility(VISIBLE);
                    viewModel.startTimer();
                    break;
            }
        }
    };

    private HidViewButtonTimer hidViewButtonTimer = new HidViewButtonTimer(5000);

    /**
     * 设置列表关闭倒计时
     */
    private class HidViewButtonTimer extends CountDownTimer {
        private int type = 0;// 是否点击了2d或3d按钮

        public void start(int type) {
            this.type = type;
            if (type == 0) {
                KLog.d(" layout2d start " + segmentTab.getCurrentTab());
                if (segmentTab.getCurrentTab() == 0) {
                    viewShow2dGroupId.setVisibility(VISIBLE);
                }

            } else {
                if (segmentTab.getCurrentTab() == 1) {
                    viewShow3dGroupId.setVisibility(VISIBLE);
                }
//                mViewCameraBinding.cameraRightRear.setVisibility(VISIBLE);
            }
            cancel();
            start();
        }

        public HidViewButtonTimer(long millisInFuture) {
            super(millisInFuture, 2100);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {

            if (type == 0) {
                viewShow2dGroupId.setVisibility(GONE);
            } else {
                viewShow3dGroupId.setVisibility(GONE);
                //                cameraRightRear.setVisibility(GONE);
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        KLog.e("onConfigurationChanged: = " + newConfig.densityDpi);
        skinView();
    }

    //摄像头故障
    public static void onBVAVMMessage(int msg, int param1, int param2) {
        KLog.e("msg: = " + msg + "  param1: = " + param1 + "    param2: = " + param2 + " , " + Thread.currentThread());

// call stack
//        at com.autochips.avm.ui.view.CameraView.onBVAVMMessage(CameraView.java:2)
//        at com.android.bvavm.bvavmJNI.avmRender2(Native Method)
//        at s7.avmRender2(BvAvmJNIHelper.java:5)
//        at com.autochips.avm.ui.view.CameraGLSurfaceView$c.onDrawFrame(CameraGLSurfaceView.java:9)
//        at android.opengl.GLSurfaceView$GLThread.guardedRun(GLSurfaceView.java:1574)
//        at android.opengl.GLSurfaceView$GLThread.run(GLSurfaceView.java:1273)
        CallBackHelper.getInstance().setup(msg, param1, param2);
    }

    //埋点
    public static void bAvmFault(int code, int param1, int param2) {
        KLog.i("bAvmFault code:"+code);
        DataManager.writeFault(code);
    }

    public void setRadar(int model, int len) {
        if(model == CLUSTER_PAS_RLDistance || model == CLUSTER_PAS_RLMidDistance
                || model == CLUSTER_PAS_RRDistance || model == CLUSTER_PAS_RRMidDistance) {
            rearRadarViewId.status(model, len);
        } else {
            rearRadarFrontViewId.status(model, len);
        }
    }


    /**
     * 2d摄像头按钮显示（正常与故障）
     *
     * @param imageView
     * @param rotation
     * @param status
     */
    private void showCameraImgStatus2d(ImageView imageView, float rotation, int status) {
        if (status == bvavmJNI.CAMERA2_ERR_OK) {
            imageView.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
        } else {
            cameraStatus = true;
            imageView.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_fault));
        }
        imageView.setRotation(rotation);
    }

    boolean cameraStatus = false;

    private void setAVMBreakdown(int msg, int param1, int param2) {

        KLog.e("msg: = " + msg + "  param1: = " + param1 + "   param2: = " + param2);
//      param2 = bvavmJNI.CAMERA2_ERR_OK；
        if (cameraBinding.getRoot().getParent() != null && cameraBinding.getRoot().isAttachedToWindow()){
            mWindowLps.alpha = 1.0f;
            mWindowLps.format = isSmartWin ? PixelFormat.TRANSLUCENT : PixelFormat.UNKNOWN;
            mWindowManager.updateViewLayout(cameraBinding.getRoot(),mWindowLps);
            KLog.e(" setAVMBreakdown mWindowLps: = " + mWindowLps);
        }
        if(isSmartWin){
            return;
        }
        if (msg == bvavmJNI.BWAVM_MSG_CAMERA2_STATUS) {
            if (segmentTab.getCurrentTab() == 0) {//2D
                boolean isCanOpreateBreakDown = (param1 == bvavmJNI.BWAVM_FRONT_CAM_ID && cameraShowType == 0) ||
                        (param1 == bvavmJNI.BWAVM_REAR_CAM_ID && cameraShowType == 1) ||
                        (param1 == bvavmJNI.BWAVM_LEFT_CAM_ID && (cameraShowType == 2 || cameraShowType == 3 || cameraShowType == 4)) ||
                        (param1 == bvavmJNI.BWAVM_RIGHT_CAM_ID && (cameraShowType == 2 || cameraShowType == 3 || cameraShowType == 4));
                switch (param1) {
                    case bvavmJNI.BWAVM_FRONT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraTop.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                    : R.mipmap.ic_camera_default));
                            cameraTop.setRotation(0);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    cameraBreakdown.setVisibility(View.GONE);
                                }, 0);
                            }
                        } else {
                            cameraStatus = true;
                            cameraTop.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown
                                    ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraTop.setRotation(0);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    cameraBreakdown.setVisibility(View.VISIBLE);
                                }, 0);
                            }
                        }
                    }
                    break;
                    case bvavmJNI.BWAVM_REAR_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraBottom.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                    : R.mipmap.ic_camera_default));
                            cameraBottom.setRotation(180);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    cameraBreakdown.setVisibility(View.GONE);
                                }, 0);
                            }
                        } else {
                            cameraStatus = true;
                            cameraBottom.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown
                                    ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraBottom.setRotation(180);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    cameraBreakdown.setVisibility(View.VISIBLE);
                                }, 0);
                            }
                        }

                    }
                    break;
                    case bvavmJNI.BWAVM_LEFT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraLift.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                    : R.mipmap.ic_camera_default));
                            cameraLift.setRotation(270);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    cameraBreakdown.setVisibility(View.GONE);
                                }, 0);
                            }
                        } else {
                            cameraStatus = true;
                            cameraLift.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown
                                    ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraLift.setRotation(270);
                            int BWAVM_RIGHT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(3);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    if(BWAVM_RIGHT_CAM_ID == -1) {
                                        cameraBreakdown.setVisibility(View.VISIBLE);
                                    }
                                }, 0);
                            }
                        }
                    }
                    break;
                    case bvavmJNI.BWAVM_RIGHT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraRight.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                    : R.mipmap.ic_camera_default));
                            cameraRight.setRotation(90);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    cameraBreakdown.setVisibility(View.GONE);
                                }, 0);
                            }
                        } else {
                            cameraStatus = true;
                            cameraRight.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown
                                    ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraRight.setRotation(90);
                            int BWAVM_LEFT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(2);
                            if(isCanOpreateBreakDown) {
                                mMainHandler.postDelayed(() -> {
                                    if(BWAVM_LEFT_CAM_ID == -1) {
                                        cameraBreakdown.setVisibility(View.VISIBLE);
                                    }
                                }, 0);
                            }
                        }
                    }
                    break;
                    default:
                        break;
                }

            } else if (segmentTab.getCurrentTab() == 1) {//3D
                cameraBreakdown.setVisibility(View.GONE);
                switch (param1) {
                    case bvavmJNI.BWAVM_FRONT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraLeftFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraLeftFront.setRotation(135);
                            cameraRightFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraRightFront.setRotation(225);
                        } else {
                            cameraLeftFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraLeftFront.setRotation(135);
                            cameraRightFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraRightFront.setRotation(225);
                        }
                    }
                    break;
                    case bvavmJNI.BWAVM_REAR_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraRightRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraRightRear.setRotation(315);
                            cameraLeftRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraLeftRear.setRotation(45);
                        } else {
                            cameraRightRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraRightRear.setRotation(315);
                            cameraLeftRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraLeftRear.setRotation(45);
                        }
                    }
                    break;
                    case bvavmJNI.BWAVM_LEFT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraLeftFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraLeftFront.setRotation(135);
                            cameraLeftRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraLeftRear.setRotation(45);
                        } else {
                            cameraLeftFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraLeftFront.setRotation(135);
                            cameraLeftRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraLeftRear.setRotation(45);
                        }
                    }
                    break;
                    case bvavmJNI.BWAVM_RIGHT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            cameraRightFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraRightFront.setRotation(225);
                            cameraRightRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            cameraRightRear.setRotation(315);
                        } else {
                            cameraRightFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraRightFront.setRotation(225);
                            cameraRightRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            cameraRightRear.setRotation(315);
                        }
                    }
                    break;
                    default:
                        break;
                }
            }else {
                cameraBreakdown.setVisibility(View.GONE);
            }
        }

    }

    public void skinView() {
        UiModeManager uiModeManager = (UiModeManager) mContext.getSystemService(Context.UI_MODE_SERVICE);
        int uiMode = uiModeManager.getNightMode();
        KLog.e("skinView: isSmartWin " + isSmartWin);
        if (mViewCameraBinding == null && mViewCameraRightBinding == null) {
            return;
        }
        if (isSmartWin) {
            mainAvmViewRootId.setBackgroundColor(Color.TRANSPARENT);
            mainAvmViewRootId.invalidate();
            return;
        }
        settingView.skinView(uiMode);
        rearviewMirrorView.skinView(uiMode);
        NotCloseToast.getInstance().uiMode(uiMode);
        switch (uiMode) {
            case UiModeManager.MODE_NIGHT_YES:
                KLog.e("黑夜模式");
                //CustomToast.showToast("黑夜模式");
                //SkinCompatManager.getInstance().restoreDefaultTheme();
                bvavmJNI.bwSetIsDay(0);
                if (SHOW_OVERLAY_LAYER)
                    cameraBinding.frameLayoutId.setBackground(mContext.getDrawable(R.color.avm_bg));
                mainAvmViewRootId.setBackground(mContext.getDrawable(R.color.avm_bg));
                cameraBreakdown.setBackgroundResource(R.drawable.selector_breakdown_bg);
                ivBreakdown.setImageDrawable(mContext.getDrawable(R.mipmap.info_cam_error_day));
                tvBreakdown.setTextColor(mContext.getResources().getColor(R.color.test_color_D9));
                llSetting.setBackgroundResource(R.drawable.button_select);
                llBackMirror.setBackgroundResource(R.drawable.button_select);
                ivSetting.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_setting));
                ivBackMirror.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_mirror));
                segmentTab.setThumbDrawable(R.drawable.tab_selector_thumb);
                segmentTab.setThumbDrawable3(R.drawable.tab_selector_thumb);
                segmentTab.setThumbDrawable2(R.drawable.tan_selector_camera_thumb);
                segmentTab.setBackground(mContext.getResources().getDrawable(R.drawable.tab_selector_thumb));
                segmentTab.setTextSelectColor(R.color.setting_view_title_color, 1);
                segmentTab.setTextUnselectColor(R.color.setting_view_content_color);
                manualCalibration.setTextColor(mContext.getResources().getColor(R.color.setting_view_bg));
                automaticCalibration.setTextColor(mContext.getResources().getColor(R.color.setting_view_bg));
                infobook.setBackgroundResource(R.drawable.shape_bg_nor);
                infoTitle.setTextColor(mContext.getResources().getColor(R.color.setting_view_title_color));
                infoContent.setTextColor(mContext.getResources().getColor(R.color.setting_view_content_color));
                infoOk.setTextColor(mContext.getResources().getColor(R.color.setting_view_title_color));
                infoOk.setBackgroundResource(R.drawable.shape_text_bg_nor);
                //rearRadarImgId.setBackgroundResource(R.mipmap.rada_distance_30);

                if (AvmApp.ISAY5T) {
                    segmentWideAngle.setThumbDrawable2(R.mipmap.wide_angle);
                    segmentWideAngle.setBackground(mContext.getResources().getDrawable(R.mipmap.gj_bg));
                    segmentWideAngle.setTextSelectColor(R.color.setting_view_title_color, 5);
                    segmentWideAngle.setTextUnselectColor(R.color.setting_view_content_color);
                }
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                // CustomToast.showToast("白天模式");
                //SkinCompatManager.getInstance().loadSkin("day",null,SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                bvavmJNI.bwSetIsDay(1);
                if (SHOW_OVERLAY_LAYER)
                    cameraBinding.frameLayoutId.setBackground(mContext.getDrawable(R.color.avm_bg));
                mainAvmViewRootId.setBackground(mContext.getDrawable(R.color.avm_bg_day));
                cameraBreakdown.setBackgroundResource(R.drawable.selector_breakdown_bg_day);
                ivBreakdown.setImageDrawable(mContext.getDrawable(R.mipmap.info_cam_error_day));
                tvBreakdown.setTextColor(mContext.getResources().getColor(R.color.test_color_0A1532));
                llSetting.setBackgroundResource(R.drawable.button_select_day);
                llBackMirror.setBackgroundResource(R.drawable.button_select_day);
                ivSetting.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_setting_day));
                ivBackMirror.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_mirror_day));
                segmentTab.setThumbDrawable(R.drawable.tab_selector_thumb_day);
                segmentTab.setThumbDrawable3(R.drawable.tab_selector_thumb_day);
                segmentTab.setThumbDrawable2(R.drawable.tan_selector_camera_thumb_day);
                segmentTab.setBackground(mContext.getResources().getDrawable(R.drawable.tab_selector_thumb_day));
                segmentTab.setTextSelectColor(R.color.setting_view_title_color_day, 1);
                segmentTab.setTextUnselectColor(R.color.setting_view_content_color_day);
                manualCalibration.setTextColor(mContext.getResources().getColor(R.color.white));
                automaticCalibration.setTextColor(mContext.getResources().getColor(R.color.white));

                infobook.setBackgroundResource(R.drawable.shape_bg_nor_day);
                infoTitle.setTextColor(mContext.getResources().getColor(R.color.setting_view_title_color_day));
                infoContent.setTextColor(mContext.getResources().getColor(R.color.setting_view_content_color_day));
                infoOk.setTextColor(mContext.getResources().getColor(R.color.setting_view_title_color_day));
                infoOk.setBackgroundResource(R.drawable.shape_text_bg_nor_day);
                //rearRadarImgId.setImageDrawable(mContext.getDrawable(R.mipmap.rada_distance_30_day));


                if (AvmApp.ISAY5T) {
                    segmentWideAngle.setThumbDrawable2(R.mipmap.wide_angle_day);
                    segmentWideAngle.setBackground(mContext.getResources().getDrawable(R.mipmap.gj_bg_day));
                    segmentWideAngle.setTextSelectColor(R.color.setting_view_title_color_day, 5);
                    segmentWideAngle.setTextUnselectColor(R.color.setting_view_content_color_day);
                }
                break;
        }


    }

    public void changeRearviewShow(float speedValue){
        rearviewMirrorView.changeShow(speedValue);
    }

    public void reloadLanauge(){
        rearviewMirrorView.reloadLanauge();
        settingView.reloadLanauge();
        Locale current = AvmApp.getInstance().getResources().getConfiguration().locale;
        String language = current.getLanguage();
        KLog.d(" 当前language："+language);
        ViewGroup.LayoutParams layoutParams = infobook.getLayoutParams();
        layoutParams.height = language.equals("vi") || language.equals("ms") || language.equals("en")
                || language.equals("th") || language.equals("ru") || language.equals("de") || language.equals("nb")
                || language.equals("fr") || language.equals("it") || language.equals("es") || language.equals("esrUs") || language.equals("nl")
                || language.equals("sv") || language.equals("pt") || language.equals("ptrBR")? 270 : 240;
        infobook.setLayoutParams(layoutParams);
        tvBreakdown.setText(R.string.camera_breakdown);
    }

    public void chick2DView(String type) {
        KLog.i("chick2DView ......... " + type);
        int BWAVM_FRONT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(0);
        int BWAVM_REAR_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(1);
        int BWAVM_LEFT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(2);
        int BWAVM_RIGHT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(3);
        boolean isAy5T = AvmApp.ISAY5T;
        switch (type) {
            case CAMERA_2_D:
                cameraShowType = 0;
                cameraTop.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                cameraTop.setRotation(0);
                cameraLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                cameraLift.setRotation(270);
                cameraBottom.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                cameraBottom.setRotation(180);
                cameraRight.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                cameraRight.setRotation(90);
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_back
                        : R.mipmap.ic_camera_card_rback));
                cameraImageLayoutLift.setVisibility(GONE);
                mMainHandler.postDelayed(() -> {
                    if (BWAVM_FRONT_CAM_ID == 0) {
                        cameraBreakdown.setVisibility(View.GONE);
                    } else {
                        cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                }, 0);
                break;
            case CAMERA_2_D_TOP:
                cameraShowType = 0;
                showCameraImgStatus(cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                //CustomToast.showToast(AvmApp.getInstance().getString(R.string.translate_front));
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_front
                        : R.mipmap.ic_camera_card_rfront));
                cameraImageLayoutLift.setVisibility(GONE);
                mMainHandler.postDelayed(() -> {
                    if (BWAVM_FRONT_CAM_ID == 0) {
                        cameraBreakdown.setVisibility(View.GONE);
                    } else {
                        cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                }, 0);
                break;
            case CAMERA_2_D_LIFT:
                cameraShowType = 2;
                showCameraImgStatus(cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                //CustomToast.showToast(AvmApp.getInstance().getString(R.string.translate_left));
                cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_right));
                if (!isSmartWin) {
                    cameraImageLayoutLift.setVisibility(VISIBLE);
                } else {
                    cameraImageLayoutLift.setVisibility(GONE);
                }

                cameraIvLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_cameraview_card_left_2));
                mMainHandler.postDelayed(() -> {
                    if(BWAVM_RIGHT_CAM_ID==0 || BWAVM_LEFT_CAM_ID ==0){
                        cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                }, 0);
                break;
            case CAMERA_2_D_BOTTOM:
                cameraShowType = 1;
                showCameraImgStatus(cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                //CustomToast.showToast(AvmApp.getInstance().getString(R.string.translate_rear));
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_back
                        : R.mipmap.ic_camera_card_rback));
                cameraImageLayoutLift.setVisibility(GONE);
                mMainHandler.postDelayed(() -> {
                    if (BWAVM_REAR_CAM_ID == 0) {
                        cameraBreakdown.setVisibility(View.GONE);
                    } else {
                        cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                }, 0);
                break;
            case CAMERA_2_D_RIGHT:
                cameraShowType = 3;
                showCameraImgStatus(cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                // CustomToast.showToast(AvmApp.getInstance().getString(R.string.translate_right));
                cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_right));
                if (!isSmartWin) {
                    cameraImageLayoutLift.setVisibility(VISIBLE);
                } else {
                    cameraImageLayoutLift.setVisibility(GONE);
                }
                cameraIvLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_cameraview_card_left_2));
                mMainHandler.postDelayed(() -> {
                    if(BWAVM_RIGHT_CAM_ID==0 || BWAVM_LEFT_CAM_ID ==0){
                        cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                }, 0);
                break;
            case CAMERA_2_D_LIFT_RIGHT:
                cameraShowType = 4;
                showCameraImgStatus(cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                //CustomToast.showToast(AvmApp.getInstance().getString(R.string.translate_left_right));

                cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_right));
                if (!isSmartWin) {
                    cameraImageLayoutLift.setVisibility(VISIBLE);
                } else {
                    cameraImageLayoutLift.setVisibility(GONE);
                }
                cameraIvLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_cameraview_card_left_2));
                mMainHandler.postDelayed(() -> {
                    if (BWAVM_RIGHT_CAM_ID == 0 || BWAVM_LEFT_CAM_ID == 0) {
                        cameraBreakdown.setVisibility(View.GONE);
                    } else {
                        cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                }, 0);
                break;
        }
        if(AvmApp.getInstance().isRight){
            boolean isVisibleLift = cameraImageLayoutLift.getVisibility() == VISIBLE;
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)cameraImageLayout.getLayoutParams();
            layoutParams.setMarginEnd(isVisibleLift ? 546 : 1806);
            cameraImageLayout.setLayoutParams(layoutParams);
        }
    }

    private void chick3DView(String type) {
        KLog.e("type :"+type);
        cameraImageLayoutLift.setVisibility(GONE);
        int BWAVM_FRONT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(0);
        int BWAVM_REAR_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(1);
        int BWAVM_LEFT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(2);
        int BWAVM_RIGHT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(3);
        int leftFrontStatus = (BWAVM_FRONT_CAM_ID == 0 && BWAVM_LEFT_CAM_ID == 0) ? 0 : -1;
        int rightFrontStatus = (BWAVM_FRONT_CAM_ID == 0 && BWAVM_RIGHT_CAM_ID == 0) ? 0 : -1;
        int leftRearStatus = (BWAVM_REAR_CAM_ID == 0 && BWAVM_LEFT_CAM_ID == 0) ? 0 : -1;
        int rightRearStatus = (BWAVM_REAR_CAM_ID == 0 && BWAVM_RIGHT_CAM_ID == 0) ? 0 : -1;
        KLog.e("chick3DView : leftFrontStatus:"+leftFrontStatus+"  rightFrontStatus:"+rightFrontStatus
                +" leftRearStatus:"+leftRearStatus +" rightRearStatus:"+rightRearStatus);
        boolean isAy5T = AvmApp.ISAY5T;
        if(AvmApp.getInstance().isRight){
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)cameraImageLayout.getLayoutParams();
            layoutParams.setMarginEnd(1806);
            cameraImageLayout.setLayoutParams(layoutParams);
        }
        switch (type) {
            case CAMERA_3_D:
                camera3DShowType = -1;
                showCameraImgStatus(cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(cameraRightRear,315,rightRearStatus,4,false);
                if(AvmRuntime.self().isRearGearSts()){
                    cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_back
                            : R.mipmap.ic_camera_card_rback));
                }else {
                    cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_front
                            : R.mipmap.ic_camera_card_rfront));
                }
                break;
            case CAMERA_3_D_LEFT_FRONT:
                camera3DShowType = 1;
                showCameraImgStatus(cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(cameraRightRear,315,rightRearStatus,4,false);
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_leftfront
                        : R.mipmap.ic_camera_card_rleftfront));
                break;
            case CAMERA_3_D_RIGHT_FRONT:
                camera3DShowType = 2;
                showCameraImgStatus(cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(cameraRightRear,315,rightRearStatus,4,false);
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_rightfront
                        : R.mipmap.ic_camera_card_rrightfront));
                break;
            case CAMERA_3_D_LEFT_REAR:
                camera3DShowType = 3;
                showCameraImgStatus(cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(cameraRightRear,315,rightRearStatus,4,false);
                showCameraImgStatus(cameraRightRear,315,rightRearStatus,4,false);
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_leftback
                        : R.mipmap.ic_camera_card_rleftback));
                break;
            case CAMERA_3_D_RIGHT_REAR:
                camera3DShowType = 4;
                showCameraImgStatus(cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(cameraRightRear,315,rightRearStatus,4,false);
                cameraIv.setImageDrawable(mContext.getDrawable(mViewCameraRightBinding == null ? R.mipmap.ic_camera_card_rightback
                        : R.mipmap.ic_camera_card_rrightback));
                break;
        }
    }

    /**
     * 摄像头按钮显示（正常与故障）
     * @param imageView
     * @param rotation 旋转角度
     * @param status 0是正常 -1是异常
     * @param direction 方向 0前，1,后，2左，3右，4左右 3D:1左前、2右前、3左右、4右后
     * @param is2d 是否是2d
     */
    private void showCameraImgStatus(ImageView imageView, float rotation, int status , int direction ,boolean is2d){
        boolean isSelect = false;
        if(is2d){
            isSelect = cameraShowType == direction;
            if(direction == 2 || direction == 3 || direction == 4) {
                isSelect = (cameraShowType == 2 || cameraShowType == 3 || cameraShowType == 4);
            }
        }else {
            //3d
            if(camera3DShowType == -1){
                isSelect = false;
            }else {
                isSelect = camera3DShowType == direction;
            }
        }
        if (status == bvavmJNI.CAMERA2_ERR_OK) {
            imageView.setImageDrawable(mContext.getDrawable(isSelect ? R.mipmap.ic_camera_click : R.mipmap.ic_camera_default));
        } else {
            imageView.setImageDrawable(mContext.getDrawable(isSelect ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
        }
        imageView.setRotation(rotation);
    }

    /**
     * 热区点击
     */
    private void viewRedChick() {
        red2dTop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dTop();
                }
            }
        });
        red2dLift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dLift();
                }
            }
        });
        red2dRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dRight();
                }
            }
        });
        red2dBottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dBottom();
                }
            }
        });
        red3dleftFront.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftFront();
                }
            }
        });
        red3dleftFront1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftFront();
                }
            }
        });
        red3drightFront.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dRightFront();
                }
            }
        });
        red3drightFront1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dRightFront();
                }
            }
        });
        red3dleftRear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftRear();
                }
            }
        });
        red3dleftRear1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftRear();
                }
            }
        });
        red3drightRear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dRightRear();
                }
            }
        });
        red3drightRear1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dRightRear();
                }
            }
        });
    }

    //开始标定
    public void startCalibration() {
        viewModel.setStartCalibration();
    }

    //反馈结果
    public void calibrationBack() {
        viewModel.calibrationBack();
    }

    //反馈错误结果
    public void calibrationError() {
        viewModel.calibrationBackError();
    }


    private void viewInAnimation() {
        TranslateAnimation showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
        showAnimation.setDuration(500);
        startAnimation(showAnimation);
        setVisibility(View.VISIBLE);
    }

    private void viewOutAnimation() {
        // 居中隐藏的动画
        TranslateAnimation hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideAnimation.setDuration(500);
        startAnimation(hideAnimation);
        setVisibility(View.GONE);
    }

    private void setViewDialog() {
        liftBg.setVisibility(GONE);
        if (rearviewMirrorView.getVisibility() == View.VISIBLE) {
            rearviewMirrorView.setVisibility(GONE);
            llBackMirror.setSelected(false);
        }
        if (settingView.getVisibility() == VISIBLE) {
            settingView.setVisibility(GONE);
            llSetting.setSelected(false);
        }
        if (infobook.getVisibility() == VISIBLE) {
            infobook.setVisibility(GONE);
            infoBg.setVisibility(GONE);
        }
    }

    private void updateTabViewIndex() {
        int outsideTabIndex = getOutsideTabIndex();
        if (outsideTabIndex != -1) segmentTab.setSelectTab(outsideTabIndex);
        KLog.d("AvmRuntime updateTabViewIndex() outsideTabIndex = " + outsideTabIndex);
        if (outsideTabIndex == 0) {
            if (CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_2D_FRONT_UNDISTORT) {
                chick2DView(CAMERA_2_D_TOP);
            } else if (CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_2D_REAR_UNDISTORT) {
                chick2DView(CAMERA_2_D_BOTTOM);
            } else if (CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_LEFT_RIGHT_FRONT
                || CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_LEFT_RIGHT_BACK) {
                chick2DView(CAMERA_2_D_LIFT_RIGHT);
            }
        }else if(outsideTabIndex == 1){
            KLog.d("AvmRuntime updateTabViewIndex() getCameraDirection = " + CameraGLSurfaceView.getCameraDirection());
            if (CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_LEFT_REAR_3D) {
                chick3DView(CAMERA_3_D_LEFT_REAR);
            } else if (CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_RIGHT_REAR_3D) {
                chick3DView(CAMERA_3_D_RIGHT_REAR);
            } else if(CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_REAR_3D ||
                    CameraGLSurfaceView.getCameraDirection() == bvavmJNI.BW_FRONT_3D){
                chick3DView(CAMERA_3_D);
            }
        } else if (outsideTabIndex == 2) {
            int wideAngleTabIndex = getWideAngleTabIndex();
            KLog.d("AvmRuntime updateTabViewIndex() wideAngleTabIndex = " + wideAngleTabIndex);
            if (wideAngleTabIndex != -1) {
                segmentWideAngle.setSelectTab(wideAngleTabIndex);
            } else {
                segmentWideAngle.setSelectTab(0);
            }
        }
    }

    private int getOutsideTabIndex() {
        int memory = AvmRuntime.self().getMemoryType();
        int viewAngle = CameraGLSurfaceView.getCameraDirection();
        KLog.d("AvmRuntime getOutsideTabIndex() memory is " + memory + " , viewAngle is " + viewAngle + " , viewPosition is " + viewPosition);

        switch (viewAngle) {
            case bvavmJNI.BW_2D_FRONT:
            case bvavmJNI.BW_2D_REAR:
            case bvavmJNI.BW_2D_RIGHT:
            case bvavmJNI.BW_DPMM_LEFT_2D:
            case bvavmJNI.BW_2D_FRONT_UNDISTORT:
            case bvavmJNI.BW_2D_REAR_UNDISTORT:
            case bvavmJNI.BW_2D_LEFT_UNDISTORT:
            case bvavmJNI.BW_2D_RIGHT_UNDISTORT:
                return 0;
            case bvavmJNI.BW_FRONT_3D:
            case bvavmJNI.BW_RIGHT_FRONT_3D:
            case bvavmJNI.BW_RIGHT_3D:
            case bvavmJNI.BW_RIGHT_REAR_3D:
            case bvavmJNI.BW_REAR_3D:
            case bvavmJNI.BW_LEFT_REAR_3D:
            case bvavmJNI.BW_LEFT_3D:
            case bvavmJNI.BW_LEFT_FRONT_3D:
            case bvavmJNI.BW_DPMM_FRONT_3D:
            case bvavmJNI.BW_DPMM_LEFT_3D:
            case bvavmJNI.BW_DPMM_RIGHT_3D:
            case bvavmJNI.BW_FREE_3D:
                return 1;
            case bvavmJNI.BW_LEFT_RIGHT_FRONT:
            case bvavmJNI.BW_LEFT_RIGHT_BACK:
                if (viewPosition == 0) {
                    return 0;
                } else if (viewPosition == 2) {
                    return 2;
                }
            case bvavmJNI.BW_2D_FRONT_120:
            case bvavmJNI.BW_2D_REAR_120:
                return 2;
        }

        return -1;
    }

    private int getWideAngleTabIndex() {
        int memory = AvmRuntime.self().getMemoryType();
        int viewAngle = CameraGLSurfaceView.getCameraDirection();
        if (memory == DataDefine.MEM_MODE_WIDE_ANGLE) {
            switch (viewAngle) {
                case bvavmJNI.BW_2D_FRONT_120:
                    return 0;
                case bvavmJNI.BW_2D_REAR_120:
                    return 1;
                case bvavmJNI.BW_LEFT_RIGHT_FRONT:
                    return 2;
                case bvavmJNI.BW_LEFT_RIGHT_BACK:
                    return 3;
            }
        }

        return -1;
    }

}
