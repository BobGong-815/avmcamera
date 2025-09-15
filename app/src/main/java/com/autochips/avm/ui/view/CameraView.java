package com.autochips.avm.ui.view;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.ASSIST_DRIVE_PAS_BUTTON_PRESS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_UINM_TURN_LIGHT_SW_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_CHIME_PAS_WARNTONE;
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
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_REAR;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_RIGHT_FRONT;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_RIGHT_REAR;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.autochips.avm.databinding.ViewCameraBinding;
import com.autochips.avm.em.ViewType;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.helper.LongPressGestureListener;
import com.autochips.avm.listener.CallBackHelper;
import com.autochips.avm.listener.CallBackInterface;
import com.autochips.avm.listener.OnTabSelectListener;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.ui.BottomDialog;
import com.autochips.avm.ui.activity.MainActivity;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.GlobalSetting;
import com.autochips.avm.util.NotCloseToast;
import com.autochips.avm.util.RearviewToast;
import com.autochips.avm.util.SystemProperties;
import com.autochips.avm.viewmode.CameraViewModel;
import com.avm.framwork.constant.CameraContracts;
import com.avm.framwork.manager.CanManager;
import com.avm.framwork.manager.ViewSwitchManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

import me.goldze.mvvmhabit.utils.KLog;
@SuppressLint("WrongConstant")
public class CameraView extends View implements LifecycleOwner {
  private BottomDialog bottomDialog ;
    private static final String TAG = CameraView.class.getName();
    private LifecycleRegistry registry = new LifecycleRegistry(this);
    public static SurfaceControl windowSurfaceControl;
    private CallBackInterface callBackInterface = new CallBackInterface() {
        @Override
        public void setup(int msg, int param1, int param2) {
            KLog.d("CallBackInterface  msg " + msg + "   param1  " + param1 + "   param2: " + param2);
            if (mViewCameraBinding == null) {
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
    public static final int WINDOW_TYPE = 2501;
//    public static final int WINDOW_TYPE_2038 = 2038;
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
    protected CameraViewModel viewModel;
    private SettingView settingView;
    protected SegmentTabLayout segmentTab;
    protected SegmentTabLayout segmentWideAngle;
    private RearviewMirrorView rearviewMirrorView;
    public int viewPosition = 0;// 是2d或者3d页面切换
    private int hisPosition = -1;// 临时记忆视角模式


    private Context mContext;
    private boolean btnSettingSelect = false;// 设置
    private boolean btnRearSelect = false;// 后视镜
    public static volatile boolean isShowing = false; //界面是否正在显示

    private int camera3DDirection;//当前3D视角
    private int cameraShowType = -1;//记录当前显示视角，判断是否要显示故障,0前，1,后，2左，3右，4左右
    private int camera3DShowType = -1;//记录当前显示视角，判断是否要显示故障,-1、无选中、1,左前，2右前，3左后，4右后，
    private LongPressGestureListener longPressGestureListener;
    private  boolean canChange3DRear = false;//是否允许倒挡设置3d视角
    private boolean isFirst = true;//是否第一次显示
    private boolean canShowAct = false;//是否允许显示act

    public static final int WINDOW_SHOW_RIGHT = 810;
    public static final int WINDOW_SHOW_LEFT = 48;
    public static final int WINDOW_SHOW_MIDDLE = 636;
    public static final int WINDOW_SHOW_LEFT_SCREEN = 45;
    public static final int WINDOW_SHOW_RIGHT_SCREEN = 728;

    public static boolean ENABLE_SKIP;

    public CameraView(Context context) {
        super(context);
        KLog.d("BaseCameraView");
        this.mContext = context;
        initWindow();
        initView();
        initData();
    }

    public static boolean isIsShowing() {
        return isShowing;
    }

    //打开摄像头失败
    public void openCameraError(boolean isFail){
        KLog.d("openCameraError isFail:"+isFail);
        if(mViewCameraBinding.llCameraOpenFail != null) {
            if (isFail) {
                if (mViewCameraBinding.llCameraOpenFail.getVisibility() == GONE) {
                    mViewCameraBinding.llCameraOpenFail.setVisibility(VISIBLE);
                }
            } else {
                if (mViewCameraBinding.llCameraOpenFail.getVisibility() == VISIBLE) {
                    mViewCameraBinding.llCameraOpenFail.setVisibility(GONE);
                }
            }
        }else {
            KLog.d("openCameraError llCameraOpenFail is null");
        }
    }

    /**
     * 更新窗口
     */
    public boolean isFirstOpen = false;
    public void updateWind(float alpha,int wh) {
        isFirstOpen = true;
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
                if (btnCalibrationSelect) {
                    mViewCameraBinding.calibration.setVisibility(VISIBLE);
                } else {
                    mViewCameraBinding.calibration.setVisibility(GONE);
                }
            }
        });
        viewModel.getLiveDataCamera2DTopUI().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String type) {
                hidViewButtonTimer.start(0);
                KLog.d(" layout2d getLiveDataCamera2DTopUI " + mViewCameraBinding.segmentTab.getCurrentTab());
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {
                    mViewCameraBinding.layout2d.setVisibility(VISIBLE);
                }
                //showFullWin();
                chick2DView(type);
            }
        });

        mViewCameraBinding.camera2dBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                KLog.d(" onClick 1 ");
                if (mViewCameraBinding.viewShow2dGroupId.getVisibility() == View.VISIBLE) {
                    mViewCameraBinding.viewShow2dGroupId.setVisibility(GONE);
                } else {
                    KLog.d(" layout2d camera2dBg ");
                    mViewCameraBinding.viewShow2dGroupId.setVisibility(VISIBLE);
                    hidViewButtonTimer.start(0);
                }
            }
        });

        mViewCameraBinding.camera3dBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.viewShow3dGroupId.getVisibility() == View.VISIBLE) {
                    mViewCameraBinding.viewShow3dGroupId.setVisibility(GONE);
                } else {
                    mViewCameraBinding.viewShow3dGroupId.setVisibility(VISIBLE);
                    hidViewButtonTimer.start(1);
                }
            }
        });

        viewModel.getLiveDataRadarSound().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                int status = CanManager.getInstance().getIntStatus(ASSIST_DRIVE_PAS_BUTTON_PRESS, 0);
                KLog.d("雷达 点击status " + status);
                if (status == 1) {
                    mViewCameraBinding.radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_nor));
                    CameraViewModelHelper.getInstance().radarSoundStatus((Integer) 0, status);
                    KLog.d("雷达 关闭提示音 ");
                } else {
                    mViewCameraBinding.radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_sel));
                    CameraViewModelHelper.getInstance().radarSoundStatus((Integer) 1, status);
                    KLog.d("雷达 打开提示音 ");
                }

            }
        });

        viewModel.getLiveDataCamera3DTopUI().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String type) {
                mViewCameraBinding.layout3d.setVisibility(VISIBLE);
                hidViewButtonTimer.start(1);
                //showFullWin();
                chick3DView(type);
            }
        });

        //mViewCameraBinding.layoutShowFull2d.setOnTouchListener(this::showFull2DByOnTouch);
        mViewCameraBinding.camera3dBg.setOnTouchListener(new OnTouchListener() {// 长按 60s 显示标定图标
            private  long resTime ;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (count < 10){
                  return false;
                }
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                       mMainHandler.removeCallbacksAndMessages("count");
                        resTime = SystemClock.elapsedRealtime();
                        KLog.d(" AVM 打开标定操作页面 initWindow 按下 " );
                        break;
                    case MotionEvent.ACTION_UP:
                      mMainHandler.removeCallbacksAndMessages("count");
                       count = 0;
                        long movTime = SystemClock.elapsedRealtime();
                        long endTime = movTime -resTime;
                        KLog.d(" AVM 打开标定操作页面 initWindow " + endTime);
                        if (endTime > 10*1000){
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
  private  int count = 0;
    public  void  onCameraIv(View view){
      if (viewPosition == 0){
        count ++;
        if (count>= 10){
          mMainHandler.removeCallbacksAndMessages("count");
          mMainHandler.postDelayed(()->count =0,"count",3000);
        }
      }
    }

  public boolean showFull2DByOnTouch() {
      KLog.d("showFull2DByOnTouch");
      KLog.d(" onClick 8 ");
      if (CameraViewModelHelper.getInstance().isPGearShowSmart) {
          CameraViewModelHelper.getInstance().isPGearShowSmart = false;
          CameraViewModelHelper.getInstance().isPGearShowSmartDismiss = true;
      }
      // 开始动画
      canChange3DRear = false;

      dismissView("click");
      mMainHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
              showFullWin();
              KLog.d("showFull2DByOnTouch hisModel:" + hisModel + " viewPosition:" + viewPosition + " valGear:"
                      + CameraViewModelHelper.valGear);
              if (viewPosition == 2 && CameraViewModelHelper.valGear == 3) {
                  CameraViewModelHelper.getInstance().gdNotChangeView = true;
                  CameraViewModelHelper.getInstance().setViewModel(ViewType.ReverseIn);
              } else if (viewPosition == 1 && CameraViewModelHelper.valGear == 3) {
                  canChange3DRear = true;
                  CameraViewModelHelper.getInstance().setViewModel(ViewType.ReverseIn);
              }
              //此处点击左卡片要直接进入倒计时
              if (CameraViewModelHelper.getInstance().isRGearShowSmart) {
                  //R挡下，打开转向灯，激活左卡片后点击左卡片激活右卡片，关闭转向灯，无操作30秒没有退出全景，应退出
                  CameraViewModelHelper.getInstance().isRGearShowSmart = false;
                  //CameraViewModelHelper.getInstance().setRunning();
              } else {
                  CameraViewModelHelper.getInstance().setRunning(true);
              }
          }
      }, 100);

      return true;
    }


    @SuppressLint("WrongConstant")
    private void initWindow() {

        mWindowLps = new WindowManager.LayoutParams();
        //临时注释让全屏显示，否则摄像头画面显示不全
        mWindowLps.alpha = 0.0f;//1.0f不透明
      mWindowLps.type = WINDOW_TYPE;
      mWindowLps.x = 0;
        mWindowLps.y = 0;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        KLog.d(" AVM 主界面被启动 initWindow " + mWindowLps);
//        mWindowLpsBottom = mWindowLps;
      mWindowLps.alpha = 0.0f;
      mWindowLps.width = 2;
      mWindowLps.height = 2;
      mWindowLps.gravity = Gravity.LEFT |Gravity.TOP;

        BvAvmJNIHelper.getInstance().setCallback((v -> {
            /**
             * cameraBinding 为了解决车模透明时，穿透到桌面而增加了window底部蒙版，只有视频返回第一帧的时候，才显示
             */
            showBottomView();
        }));
    }

    @SuppressLint("WrongConstant")
    private void showBottomView(){

      KLog.d(isShowing + " isShowing 窗口层级 mWindowLpsBottom："+mWindowLps);
      if (isShowing){
        mWindowLps.alpha = 1.0f;
        if (isSmartWin)
        mWindowLps.format = PixelFormat.TRANSLUCENT;
        else
          mWindowLps.format = PixelFormat.UNKNOWN;
        KLog.d(isSmartWin +" isSmartWin bottom_view-isFullWin=" +isFullWin + " mWindowLpsBottom="+mWindowLps + "isFirst:"+isFirst);
        mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(),mWindowLps);
        setCameraViewLayer();
      }else {
        mWindowLps.alpha = 0.0f;
        mWindowLps.format = PixelFormat.TRANSLUCENT;
        mWindowLps.width = 0;
        mWindowLps.height = 0;
        mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(),mWindowLps);
      }

    }

    //初始化view+
    private void initView() {
        CallBackHelper.getInstance().setCallBackInterface(callBackInterface);
        registry.setCurrentState(Lifecycle.State.CREATED);
        mViewCameraBinding = ViewCameraBinding.inflate(LayoutInflater.from(mContext), null, false);
        //mViewCameraBinding.cameraTextureView.setSurfaceTextureListener(new SurfaceTextureHelper
        // ());

        viewModel = new CameraViewModel();
        mViewCameraBinding.setViewModel(viewModel);
        mViewCameraBinding.rearRadarViewId.setViewModel(viewModel);

        settingView = mViewCameraBinding.settingView;
        segmentTab = mViewCameraBinding.segmentTab;
        segmentWideAngle = mViewCameraBinding.segmentWideAngle;
        settingView.setInfoBookView(mViewCameraBinding.infobook, mViewCameraBinding.infoBg,mViewCameraBinding.segmentWideAngle);
        rearviewMirrorView = mViewCameraBinding.rearviewMirrorView;
        mViewCameraBinding.layout3dTouchId.setOnTouchListener(this::onTouch);
        mViewCameraBinding.viewFrame.setOnTouchListener(this::onTouchView);
        mViewCameraBinding.viewFrame.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                KLog.d("key event : " + event);
                return false;
            }
        });
        rearviewMirrorView.setOnClickListener((v) -> {
        });
        settingView.setOnClickListener((v) -> {
        });
        mViewCameraBinding.layout2d.setOnClickListener((view) -> {
            KLog.d(" layout2d setOnClickListener ");
            mViewCameraBinding.viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(0);
        });
        mViewCameraBinding.layout3d.setOnClickListener((view) -> {
            KLog.d(" layout3d setOnClickListener ");
            mViewCameraBinding.viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(1);
        });
        mViewCameraBinding.liftBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewDialog();
            }
        });
        mViewCameraBinding.layoutSettingId.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewDialog();
            }
        });
        mViewCameraBinding.toastInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String string = button.getText().toString();
                if (string.equals("标定失败!")) {
                    System.exit(0);
                }
            }
        });
        viewRedChick();
        tabView();
        tabViewInit();
//        mViewCameraBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            KLog.d("mWindowLps.height：getVisibility:" + mViewCameraBinding.getRoot().getVisibility());
//
//        });
    }

    public void rearMirrowFlipDown(int status) {
        mViewCameraBinding.rearviewMirrorView.rearMirrorFlipDown(status);
    }

    public void rearMirrorFold(int status) {
        mViewCameraBinding.rearviewMirrorView.rearMirrorFold(status);
    }

    /**
     * 激活类型
     *
     * @param model
     */
    private ViewType hisModel = ViewType.gear_D;
    private ViewType hisModelTurn = ViewType.gear_D;// 转向退出的时候的模式
    private  boolean isChangeGear = false; // 用来判断是否换挡，如果，换挡测不取消按钮高亮

    public CameraViewModel getViewModel() {
        return viewModel;
    }

    public void viewShowStatus(ViewType model,int potison) {
       int turnExitReverseIn = -1;
       if (model == ViewType.gear_turn_exit && hisModel == ViewType.ReverseIn){
         turnExitReverseIn = 1;
         hisModelTurn = model;
       }
        isChangeGear = true;
        isDismissView = false;
        mViewCameraBinding.radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_nor));
        mViewCameraBinding.rearviewMirrorView.gearInfo(CameraViewModelHelper.valGear == 3 ? 1 : 0);
        KLog.d(hisModel + "  valGear viewShowStatus 模式：" + model + " 记忆模式: " + viewPosition);
        try {
            int settingPathLine = Settings.Global.getInt(mContext.getContentResolver(), GlobalSetting.AVM_SETTING_TRAJECTORY);// SystemProperties.getInt("settingPathLine", -1);
            if (settingPathLine == 1) {
                bvavmJNI.bwSetTrajLineStatus((byte) 1);
            }
        } catch (Settings.SettingNotFoundException settingNotFoundException) {
            settingNotFoundException.printStackTrace();
        }
        hisModel = model;
        viewModel.setmHisModel(hisModel);
        if( model == ViewType.ReverseIn){
            KLog.d("  R挡转向：" + model + " 记忆模式: " + viewPosition);
        }else {
            KLog.d("  转向3d复位");
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        }
        if (hisModel == ViewType.ReverseIn) {
            BvAvmJNIHelper.getInstance().bwSetTrajLineStatus(3);
        } else {
            BvAvmJNIHelper.getInstance().bwSetTrajLineStatus(0);
        }
        if (!isShowing) {
            KLog.d(isShowing + "  isShowing viewShowStatus viewPosition ：" + viewPosition);
            return;
        }

        if (hisPosition > 0) {
            mViewCameraBinding.segmentTab.setSelectTab(hisPosition);
            hisPosition = -1;
        }
        if (isSmartWin) {
            KLog.d("  isShowing isSmartWin  ：" + isSmartWin);
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        switch (model) {
            case ReverseOut:
            case gear_D:
            case gear_N:
            case gear_P:
              viewModelByActive("gear_P_D_N");
              break;
            case gear_turn_exit:
              if (turnExitReverseIn == 1){
                viewModelByturnToRever();
              }else {
                viewModelByActive("gear_turn_exit");
              }
                break;
            case ReverseIn:
                mViewCameraBinding.rearviewMirrorView.gearInfo(1); // 后视镜下翻按钮可操作
                if(viewPosition != 0 && (CameraViewModelHelper.getInstance().turnRSet || canChange3DRear
                        || CameraViewModelHelper.getInstance().gdNotChangeView)) {
                    CameraViewModelHelper.getInstance().turnRSet = false;
                    if(viewPosition == 1) {
                        KLog.d("r 挡下3d模式不响应转向灯视角 canChange3DRear:"+canChange3DRear);
                        if(canChange3DRear) {
                            canChange3DRear = false;
//                            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_FRONT_3D);
                            chick3DView(CAMERA_3_D);
//                            if (CameraViewModelHelper.getInstance().turnValue == 1) {
//                                leftModel();
//                            } else {
//                                rightModel();
//                            }
                        }
                    }
                    if(CameraViewModelHelper.getInstance().gdNotChangeView) {
                        CameraViewModelHelper.getInstance().gdNotChangeView = false;
                        //记忆广角模式，在 R 档下，主动进入全景，视角为前广角，应为后广角
                        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_120);
                        setAngleStatus(bvavmJNI.BW_2D_REAR_120);
                    }
                }else {
                    viewModelReverseIn();
                }
                int status = CanManager.getInstance().getIntStatus(CLUSTER_CHIME_PAS_WARNTONE, 0);
                KLog.d("雷达报警图标状态:" + status);
                if (0 < status && status < 6) {
                    mViewCameraBinding.radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_sel));
                } else {
                    mViewCameraBinding.radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_nor));
                }
                break;
            case gear_Left:
                KLog.d("转向3d前FRONT_3D 1");
                leftModel();
                break;
            case gear_Right:
                rightModel();
                break;
//            case gear_turn_exit:
//                turnExit();
//                break;
        }
        setWindowType(model,1);
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
    }

    private void setWindowType(ViewType viewType, int windowType){

      boolean bl = mViewCameraBinding.getRoot().isAttachedToWindow();
      mWindowLps.height = mContext.getResources().getDimensionPixelSize(R.dimen.screen_height);
//      if (viewType == ViewType.ReverseIn && !isSmartWin){
//        mWindowLps.height = 1080;
//      }
      KLog.d("刷新--setWindowType-bl ：" + bl +" height:"+mWindowLps.height + "CameraGLSurfaceView.glStatus:"+CameraGLSurfaceView.glStatus);
//        CameraGLSurfaceView.glStatus ;
      if ( bl && (CameraGLSurfaceView.glStatus == 1 || CameraGLSurfaceView.glStatus == 25) && isShowing){
          KLog.d("setCameraViewLayer");
        mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(),mWindowLps);
        mWindowLps.format = PixelFormat.UNKNOWN;
        setCameraViewLayer();
      }

    }

  private void viewModelByturnToRever() {
  }

  private void turnExitGear() {

    }

    private void turnExit()
    {
        int status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
        if (viewPosition == 0) {
            status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
        } else if (viewPosition == 1) {
            status = bvavmJNI.BW_FRONT_3D;
        } else if (viewPosition == 2) {
            status = bvavmJNI.BW_2D_FRONT_120;
        }
        CameraGLSurfaceView.setAngleOfView(status);
    }

    public void  setRadarFailStatus(int flag,int value){
        switch (flag){
            case  1:
                mViewCameraBinding.radarErrImgId1.setVisibility(value == 0 ? GONE :VISIBLE);
                break;
            case  2:
                mViewCameraBinding.radarErrImgId2.setVisibility(value == 0 ? GONE :VISIBLE);
                break;
//            case  3:
//                mViewCameraBinding.radarErrImgId3.setVisibility(value == 0 ? GONE :VISIBLE);
//                break;
            case  4:
                mViewCameraBinding.radarErrImgId4.setVisibility(value == 0 ? GONE :VISIBLE);
                break;
        }
        if (value > 0 ){
            NotCloseToast.getInstance().showToast("超声波雷达出现故障，请检查！");
        }else {
            if(mViewCameraBinding.radarErrImgId1.getVisibility() == GONE
             && mViewCameraBinding.radarErrImgId2.getVisibility() == GONE
            && mViewCameraBinding.radarErrImgId4.getVisibility() == GONE) {
                NotCloseToast.getInstance().cancelToast();
            }
        }

    }

    /**
     * 左转向
     */
    private void leftModel() {
      if (viewPosition == 2 || (hisModel == ViewType.ReverseIn && viewPosition == 0)) {
        return;
      }
        int status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
//        bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        if (viewPosition == 0) {
            status = bvavmJNI.BW_LEFT_RIGHT_FRONT;
            chick2DView(CAMERA_2_D_LIFT_RIGHT);
            mViewCameraBinding.viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 1) {
            status = bvavmJNI.BW_LEFT_REAR_3D;
            chick3DView(CAMERA_3_D_LEFT_REAR);
            mViewCameraBinding.viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);

        }
        CameraGLSurfaceView.setAngleOfView(status);
    }

    /**
     * 右转向
     */
    private void rightModel() {
      if (viewPosition == 2 || (hisModel == ViewType.ReverseIn && viewPosition == 0)) {
        return;
      }
        int status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
        if (viewPosition == 0) {
            status = bvavmJNI.BW_LEFT_RIGHT_FRONT;
            chick2DView(CAMERA_2_D_LIFT_RIGHT);
            mViewCameraBinding.viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 1) {
            bvavmJNI.bwSet3DfreeFlag(0);//复位3D
            status = bvavmJNI.BW_RIGHT_REAR_3D;
            chick3DView(CAMERA_3_D_RIGHT_REAR);
            mViewCameraBinding.viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        }
        CameraGLSurfaceView.setAngleOfView(status);


    }

    private void clickShowModel() {
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        if (hisModel == ViewType.ReverseIn) {
            viewModelReverseInClick();
            return;
        }
        viewModelByActive("clickShowModel");
    }

    /**
     * R档的时候视角切换
     */
    private void viewModelReverseInClick() {
        KLog.d(isSmartWin +" valGear R档视角切换viewModelReverseInClick:" + viewPosition);
        int status = 0;
        // 广角
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        if (tabSelectFromUser) {
            if (viewPosition == 0) {
                status = bvavmJNI.BW_2D_REAR_UNDISTORT;
//                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                chick2DView(CAMERA_2_D_BOTTOM);
            } else if (viewPosition == 1) {
                status = bvavmJNI.BW_FRONT_3D;
                chick3DView(CAMERA_3_D);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_back));
            } else if (viewPosition == 2) {
                status = bvavmJNI.BW_2D_REAR_120;
                setAngleStatus(bvavmJNI.BW_2D_REAR_120);
            }
        } else {
            updateTabViewIndex();
        }

    }


    /**
     * 设置按钮方向
     */
    private void setButtonStatus() {


    }


    private void viewModelReverseIn() {
        KLog.d(hisPosition+" hisPosition R档视角切换:" + viewPosition);
        chick2DView(CAMERA_2_D_BOTTOM);
        chick3DView(CAMERA_3_D);
        int status = 0;
        if (viewPosition == 0) {
            status = bvavmJNI.BW_2D_REAR_UNDISTORT;
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            chick2DView(CAMERA_2_D_BOTTOM);
        } else if (viewPosition == 1) {
            status = bvavmJNI.BW_2D_REAR_UNDISTORT;
            chick2DView(CAMERA_2_D_BOTTOM);
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_back));
        } else if (viewPosition == 2) {
            status = bvavmJNI.BW_2D_REAR_UNDISTORT;
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
//            setAngleStatus(bvavmJNI.BW_2D_REAR);
        }
        CameraGLSurfaceView.setAngleOfView(status);
        //这里注释是为了改这个BUG，在记忆3D/广角模式下，R档激活，手动切换改变记忆3D/广角，没有被记忆
        hisPosition = viewPosition;
        mViewCameraBinding.segmentTab.setSelectTab(0);
    }


    private void viewModelByActive(String position) {
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        KLog.d(viewPosition + "  viewPosition viewModelByActive 显示位置:" + position);
        KLog.d(viewPosition + "  当前转向:" + CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT));
        KLog.d(viewPosition + "  当前档位:" + hisModel);
        int status = 0;
        if (viewPosition == 0) {
            //如果进来转向灯还在，不在R档的情况下，显示左右视图
            if(CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT)==1||CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT)==2&&hisModel != ViewType.ReverseIn){
                status = bvavmJNI.BW_LEFT_RIGHT_FRONT;
//                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                chick2DView(CAMERA_2_D_LIFT_RIGHT);
            }else{
                status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
//                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                chick2DView(CAMERA_2_D_TOP);
            }

            mViewCameraBinding.viewShow2dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 1) {
            //如果进来转向灯还在，不在R档的情况下，显示左右轉向試圖
            if(CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT)==1&&hisModel != ViewType.ReverseIn){
                status = bvavmJNI.BW_LEFT_REAR_3D;
                chick3DView(CAMERA_3_D_LEFT_REAR);
            }else if(CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT)==2&&hisModel != ViewType.ReverseIn){
                status = bvavmJNI.BW_RIGHT_REAR_3D;
                chick3DView(CAMERA_3_D_RIGHT_REAR);
            }else{
                status = bvavmJNI.BW_REAR_3D;
                chick3DView(CAMERA_3_D);
            }
            mViewCameraBinding.viewShow3dGroupId.setVisibility(VISIBLE);
            hidViewButtonTimer.start(viewPosition);
        } else if (viewPosition == 2) {
          if (position.equals("gear_turn_exit")){
            return;
          }
            status = bvavmJNI.BW_2D_FRONT_120;
            setAngleStatus(bvavmJNI.BW_2D_FRONT_120);
        } else {
            status = bvavmJNI.BW_2D_FRONT_UNDISTORT;
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        }
        CameraGLSurfaceView.setAngleOfView(status);
    }


    private boolean tabSelectFromUser = false;
    private OnTabSelectListener tabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position, boolean fromUser) {
            tabSelectFromUser = fromUser;
            KLog.d(viewPosition + "tabSelectListener onTabSelect = " + position + " isChangeGear：" + isChangeGear);
            viewModel.setRunning(true);
            if (!isChangeGear){// 换挡的时候，不给取消高亮
                mViewCameraBinding.llBackMirror.setSelected(false);
                mViewCameraBinding.llSetting.setSelected(false);

            }

            if (viewPosition == position) {
              if (isChangeGear && viewPosition == 2){
//                setAngleStatus(bvavmJNI.BW_2D_FRONT_120);
//                CameraGLSurfaceView.glStatus = bvavmJNI.BW_2D_FRONT_120;
                isChangeGear = false;
              }
                return;
            }
            viewPosition = position;// 是否点击了2d按钮
            hidViewButtonTimer.start(position);
//            bvavmJNI.bwSet3DfreeFlag(0);//复位3D
            if (position == 0) {
                DataManager.writeFault(DataConstant.Code.ST_2D);
                mViewCameraBinding.layout2d.setEnabled(true);
                mViewCameraBinding.layout3d.setEnabled(false);
                KLog.d(" layout2d tab ");
                mViewCameraBinding.layout2d.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
                mViewCameraBinding.layout3d.setVisibility(View.GONE);
                mViewCameraBinding.layoutWideAngle.setVisibility(GONE);
                mViewCameraBinding.camera2dBg.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
                mViewCameraBinding.camera3dBg.setVisibility(View.GONE);
                if (!isSmartWin) {
                    mViewCameraBinding.cameraImageLayout.setVisibility(VISIBLE);
                } else {
                    mViewCameraBinding.cameraImageLayout.setVisibility(GONE);
                }
//                camera3DDirection = bvavmJNI.BW_2D_FRONT;
                chick2DView(ViewSwitchManager.CAMERA_2_D_TOP);
            } else if (position == 1) {
                DataManager.writeFault(DataConstant.Code.ST_3D);
                KLog.d("转向3d前FRONT_3D 3");
                bvavmJNI.bwSet3DfreeFlag(0);//复位3D
                //chick3DView(ViewSwitchManager.CAMERA_3_D_LEFT_FRONT);
                mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                mViewCameraBinding.layout3d.setEnabled(true);
                mViewCameraBinding.layout2d.setEnabled(false);
                mViewCameraBinding.layout2d.setVisibility(View.GONE);
                mViewCameraBinding.layout3d.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
                mViewCameraBinding.layoutWideAngle.setVisibility(GONE);
                mViewCameraBinding.camera2dBg.setVisibility(View.GONE);
                mViewCameraBinding.camera3dBg.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
                if (!isSmartWin) {
                    mViewCameraBinding.cameraImageLayout.setVisibility(VISIBLE);
                } else {
                    mViewCameraBinding.cameraImageLayout.setVisibility(GONE);
                }
//                camera3DDirection = bvavmJNI.BW_LEFT_FRONT_3D;
            } else {
                DataManager.writeFault(DataConstant.Code.ST_ANGLE);
                cameraShowType = 0;
//                if (BvAvmJNIHelper.getInstance().bwGetCamerastatus(0) == 0) {
//                    mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
//                } else {
//                    mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
//                }
                //setAngleStatus();
                KLog.d("tabSelectListener isSmartWin = " + isSmartWin);
                if (!isSmartWin) {//三分之一屏不显示
                    mViewCameraBinding.layoutWideAngle.setVisibility(VISIBLE);
                } else {
                    mViewCameraBinding.layoutWideAngle.setVisibility(GONE);
                }
                mViewCameraBinding.layout2d.setVisibility(View.GONE);
                mViewCameraBinding.layout3d.setVisibility(View.GONE);
                mViewCameraBinding.cameraImageLayout.setVisibility(View.GONE);
                //mViewCameraBinding.layoutWideAngle.setVisibility(VISIBLE);
                mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
                mViewCameraBinding.camera2dBg.setVisibility(View.GONE);
                mViewCameraBinding.camera3dBg.setVisibility(View.GONE);
                mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
            }

            SystemProperties.set("tabSelect", String.valueOf(position));
            if (tabSelectFromUser) {
                int memEvt = DataDefine.EVT_SWITCH_2_2D;
                if (position == 1) {
                    memEvt = DataDefine.EVT_SWITCH_2_3D;
                } else if (position == 2) {
                    memEvt = DataDefine.EVT_SWITCH_2_WIDE_ANGLE;
                }
                AvmRuntime.self().userClick(memEvt);
            }

//            viewShowStatus(hisModel);
            clickShowModel();
            if ( isChangeGear){
                isChangeGear = false;
                return;
            }else if (hisModel == ViewType.ReverseIn){
              hisPosition = viewPosition;
            }
          KLog.d("tabSelectListener settingView = " + isChangeGear);
            if (settingView.getVisibility() == View.VISIBLE) {
                settingView.setVisibility(GONE);
                mViewCameraBinding.liftBg.setVisibility(GONE);
            }
            if (mViewCameraBinding.infobook.getVisibility() == VISIBLE) {
                mViewCameraBinding.infobook.setVisibility(GONE);
                mViewCameraBinding.infoBg.setVisibility(GONE);
                mViewCameraBinding.liftBg.setVisibility(GONE);
            }
            if (rearviewMirrorView.getVisibility() == VISIBLE) {
                rearviewMirrorView.setVisibility(View.GONE);
                mViewCameraBinding.liftBg.setVisibility(GONE);
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
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
            return;
        }
        mViewCameraBinding.segmentWideAngle.setVisibility(VISIBLE);
        KLog.d("setAngleStatus 广角切换 = " + type);
        if (type == bvavmJNI.BW_2D_FRONT_120) {
            mViewCameraBinding.segmentWideAngle.setSelectTab(0);
            cameraShowType = 1;
        } else if (type == bvavmJNI.BW_2D_REAR_120 || type == bvavmJNI.BW_2D_REAR_UNDISTORT) {
            mViewCameraBinding.segmentWideAngle.setSelectTab(1);
            cameraShowType = 3;
        } else if (type == bvavmJNI.BW_LEFT_RIGHT_FRONT) {
            mViewCameraBinding.segmentWideAngle.setSelectTab(2);
        } else {
            mViewCameraBinding.segmentWideAngle.setSelectTab(3);
        }

        mViewCameraBinding.viewShow2dGroupId.setVisibility(GONE);
        mViewCameraBinding.viewShow3dGroupId.setVisibility(GONE);
        mViewCameraBinding.layout2d.setEnabled(false);
        mViewCameraBinding.layout3d.setEnabled(false);
    }

    private OnTabSelectListener onTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position, boolean fromUser) {
            KLog.d("onTabSelectListener onTabSelect = " + position);
            viewModel.setRunning(true);
            if (position == 0) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 2) {
                    cameraShowType = 0;
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
                    SystemProperties.set("tabSelectWideAngle", "0");
                    if (fromUser) {
                        AvmRuntime.self().userClick(DataDefine.EVT_WIDE_ANGLE_FRONT);
                    }
//                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.front_wide_angle));
                }
            } else if (position == 1) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 2) {
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_120);
                    SystemProperties.set("tabSelectWideAngle", "1");
                    if (fromUser) {
                        AvmRuntime.self().userClick(DataDefine.EVT_WIDE_ANGLE_REAR);
                    }
                    cameraShowType = 1;
                }
            } else if (position == 2) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 2) {
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_RIGHT_FRONT);
                    SystemProperties.set("tabSelectWideAngle", "2");
                    if (fromUser) {
                        AvmRuntime.self().userClick(DataDefine.EVT_WIDE_ANGLE_FRONT_WHEEL);
                    }
//                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.before));
                }
            } else if (position == 3) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 2) {
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_RIGHT_BACK);
                    SystemProperties.set("tabSelectWideAngle", "3");
                    if (fromUser) {
                        AvmRuntime.self().userClick(DataDefine.EVT_WIDE_ANGLE_REAR_WHEEL);
                    }
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
        RearviewToast.getInstance().init(mViewCameraBinding.toastBg);
        NotCloseToast.getInstance().init(mViewCameraBinding.toastNcBg);
        mViewCameraBinding.segmentTab.setOnTabSelectListener(tabSelectListener);
        mViewCameraBinding.segmentWideAngle.setOnTabSelectListener(onTabSelectListener);
        mViewCameraBinding.cameraIv.setOnClickListener(this::onCameraIv);
    }


    @SuppressLint("NewApi")
    private void tabView() {
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(mViewCameraBinding.segmentTab.getLayoutParams());
        //layoutParams.width = (304*getDescValueArray().length);
        layoutParams.leftMargin = 18;
        mViewCameraBinding.segmentTab.setTabWidth(150f);
        mViewCameraBinding.segmentTab.setBackground(mContext.getResources().getDrawable(R.drawable.tab_selector_thumb));
        mViewCameraBinding.segmentTab.setLayoutParams(layoutParams);
        mViewCameraBinding.segmentTab.setTabData(getDescValueArray());
        int tabSelect = SystemProperties.getInt("tabSelect", 0);
      KLog.d("2D+++++ tabSelect= "+ tabSelect);
        bvavmJNI.bwSet3DfreeFlag(0);//复位3D
        if (tabSelect == 0) {

            camera3DDirection = bvavmJNI.BW_2D_FRONT_UNDISTORT;
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            try {
                //判断轨迹线有没有打开，打开就显示2D轨迹线
                int settingPathLine = Settings.Global.getInt(mContext.getContentResolver(), GlobalSetting.AVM_SETTING_TRAJECTORY);// SystemProperties.getInt("settingPathLine", -1);
                KLog.d("设置车辅线:" + settingPathLine);
                if (settingPathLine == 1) {
                    bvavmJNI.bwSetTrajLineStatus((byte) 1);
                    bvavmJNI.bwSetCarIsDgear((byte) 1);//2D前视
                    //bvavmJNI.bwSetCarIsBack((byte) 0);//2D后视
                }
            } catch (Settings.SettingNotFoundException settingNotFoundException) {
                settingNotFoundException.printStackTrace();
            }
            if (!isSmartWin) {
                CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_UNDISTORT);
//                bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                KLog.d(" layout2d tabview ");
                mViewCameraBinding.layout2d.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
                mViewCameraBinding.layout3d.setVisibility(View.GONE);
                mViewCameraBinding.layoutWideAngle.setVisibility(GONE);
                mViewCameraBinding.cameraImageLayout.setVisibility(VISIBLE);
            } else {
                mViewCameraBinding.cameraImageLayout.setVisibility(GONE);
            }
            mViewCameraBinding.camera2dBg.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
            mViewCameraBinding.camera3dBg.setVisibility(View.GONE);

        } else if (tabSelect == 1) {
            KLog.d("转向3d前FRONT_3D 2");

            if (!isSmartWin) {
                if(CameraViewModelHelper.getInstance().isClick && CameraViewModelHelper.getInstance().isRGearShowSmart){
                    //记忆3D模式，在R档下，转向激活左卡片，退出左卡片，再主动进入全景，显示3D左前视角，应为3D后视
                    camera3DDirection = bvavmJNI.BW_FRONT_3D;
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_FRONT_3D);
                }else {
                    camera3DDirection = bvavmJNI.BW_LEFT_FRONT_3D;
                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_FRONT_3D);
                }
                mViewCameraBinding.layout2d.setVisibility(View.GONE);
                mViewCameraBinding.layout3d.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
                mViewCameraBinding.layoutWideAngle.setVisibility(GONE);
                if(CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_FRONT_3D){
                    chick3DView(CAMERA_3_D);
                }else {
                    chick3DView(ViewSwitchManager.CAMERA_3_D_LEFT_FRONT);
                }
                mViewCameraBinding.cameraImageLayout.setVisibility(VISIBLE);
            } else {
                mViewCameraBinding.cameraImageLayout.setVisibility(GONE);
            }
            KLog.d("3D+++++ = ");
            mViewCameraBinding.camera2dBg.setVisibility(View.GONE);
            mViewCameraBinding.camera3dBg.setVisibility(isSmartWin ? View.GONE : View.VISIBLE);
        } else {

            if (!isSmartWin) {//三分之一屏不显示
                camera3DDirection = bvavmJNI.BW_2D_FRONT_120;
                CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
                mViewCameraBinding.layout2d.setVisibility(View.GONE);
                mViewCameraBinding.layout3d.setVisibility(View.GONE);
                mViewCameraBinding.cameraImageLayout.setVisibility(View.GONE);
                mViewCameraBinding.layoutWideAngle.setVisibility(VISIBLE);
            } else {
                mViewCameraBinding.layout2d.setVisibility(View.GONE);
                mViewCameraBinding.layout3d.setVisibility(View.GONE);
                mViewCameraBinding.layoutWideAngle.setVisibility(GONE);
            }
            mViewCameraBinding.camera2dBg.setVisibility(View.GONE);
            mViewCameraBinding.camera3dBg.setVisibility(View.GONE);
            mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
            KLog.d("广角++++ = ");
        }
     // LinearLayout.LayoutParams layoutParamsAg =
       // new LinearLayout.LayoutParams(mViewCameraBinding.segmentWideAngle.getLayoutParams());
        mViewCameraBinding.segmentWideAngle.setTabWidth(144);
        mViewCameraBinding.segmentWideAngle.setBackground(mContext.getDrawable(R.drawable.shape_seg_angle_bg));
       //layoutParamsAg.width = 720;
       //layoutParamsAg.height = 84;
        //mViewCameraBinding.segmentWideAngle.setLayoutParams(layoutParamsAg);
        mViewCameraBinding.segmentWideAngle.setTabData(getWideAngleValueArray());
        mViewCameraBinding.segmentWideAngle.setTextSelectColor(R.color.white, 5);
        mViewCameraBinding.segmentWideAngle.setSelectTab(0);
      if (hisPosition > 0) {
        mViewCameraBinding.segmentTab.setSelectTab(hisPosition);
        hisPosition = -1;
      }else {
        mViewCameraBinding.segmentTab.setSelectTab(tabSelect);
      }

        if (isFullWin) {
            updateTabViewIndex();
        }

    }

    public boolean isSmartWin = false;
  public boolean isFullWin = false;

    private void setSafView() {
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mViewCameraBinding.cameraSurfaceView.getLayoutParams();
//        layoutParams.setMargins(42,80,0,0);


    }

    public View getRootView() {
        if (mViewCameraBinding == null) return null;
        return mViewCameraBinding.getRoot();
    }

    /**
     * 显示1/3 屏
     */
    public void showSmartWin() {
//        Log.e("AVM_DEBUG", Log.getStackTraceString(new Throwable()));
        Log.i(TAG, "valGear AvmRuntime showSmartWin: 显示1/3屏幕 mIsStartStatus:"+AvmService.mIsStartStatus +"mIsScreen:"+AvmService.mIsScreen+" isLeftScreen:"+AvmService.isLeftScreen);
        if (isSmartWin || isFullWin) {
            Log.i(TAG, "showSmartWin: 已经显示1/3屏幕");
            return;
        }

        ENABLE_SKIP = true;
        canShowAct = false;
        mWindowLps.y = 84;
        if (AvmService.mIsScreen) {
            //分屏
            mWindowLps.x = AvmService.isLeftScreen ? WINDOW_SHOW_LEFT_SCREEN : WINDOW_SHOW_RIGHT_SCREEN; // 右分屏810，左分屏50
        } else {
            mWindowLps.x = AvmService.mIsStartStatus && AvmService.mMapSpeedStatus ? WINDOW_SHOW_RIGHT : AvmService.mIsStartStatus ? WINDOW_SHOW_MIDDLE : WINDOW_SHOW_LEFT; // 正常模式
        }
        isSmartWin = true;
        mWindowLps.alpha = 1.0f;
        mWindowLps.width = 455;
        mWindowLps.height = 623;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mViewCameraBinding.viewFrame.getLayoutParams();
        layoutParams.height = 623;
        layoutParams.width = 455;
        layoutParams.topMargin = 0;
        mViewCameraBinding.viewFrame.setLayoutParams(layoutParams);

        mWindowLps.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        mWindowLps.format = PixelFormat.TRANSLUCENT; // 设置透明背景
        clearFocus();
        setCameraViewLayer();
        showView();
        mViewCameraBinding.rlClose.setVisibility(VISIBLE);
        mViewCameraBinding.abFullClose.setVisibility(GONE);
        mViewCameraBinding.smartGroupId.setVisibility(GONE);
        mViewCameraBinding.layoutWideAngle.setVisibility(GONE);
        mViewCameraBinding.layout2d.setVisibility(GONE);
        mViewCameraBinding.layout3d.setVisibility(GONE);
        mViewCameraBinding.camera2dBg.setVisibility(GONE);
        mViewCameraBinding.camera3dBg.setVisibility(GONE);
        mViewCameraBinding.viewShow2dGroupId.setVisibility(GONE);
        mViewCameraBinding.cameraImageLayout.setVisibility(GONE);
        mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
        mViewCameraBinding.layoutShowFull2d.setVisibility(VISIBLE);
        mViewCameraBinding.cameraRight.setVisibility(GONE);
        mViewCameraBinding.cameraLeftFront.setVisibility(GONE);
        mViewCameraBinding.viewShow3dGroupId.setVisibility(GONE);
        KLog.d("设置：1");
        //viewModel.startTestTimer();
      CameraGLSurfaceView.glStatus = 0;
//        AvmApp.getInstance().getViewBottom().showSmartWin();
    }

    /**
     * 显示全屏 页面
     * 如果，全屏显示，则不显示小屏
     */
    public void showFullWin() {
        Log.i(TAG, isSmartWin + " valGear AvmRuntime showFullWin: 全屏显示  t底部透明 " + isFullWin +" 第一帧CameraGLSurfaceView："+CameraGLSurfaceView.glStatus );
//        if(!isFullWin){
//            showSmartWin();
//            return;
//        }
        if (isFullWin) {
            updateTabViewIndex();
            return;
        }

        Intent intent = new Intent(AvmApp.getInstance(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AvmApp.getInstance().startActivity(intent);

        if (animator != null) animator.cancel(); // 可能由于snapToPosition触发了动画

        isSmartWin = false;
        isFullWin = true;
        mWindowLps.width = mContext.getResources().getDimensionPixelSize(R.dimen.screen_width);
        canShowAct = true;

        mViewCameraBinding.smartGroupId.setVisibility(VISIBLE);
        mWindowLps.format = PixelFormat.UNKNOWN;

        mWindowLps.x = 0;
        mWindowLps.y = 0;
        mWindowLps.height = mContext.getResources().getDimensionPixelSize(R.dimen.screen_height);
//        AvmApp.getInstance().getViewBottom().showInit();
//        mWindowLps.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
//                WindowManager.LayoutParams.FLAG_FULLSCREEN |
//                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;

//         mWindowLpsBottom = mWindowLps;

//      mWindowLpsBottom.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mViewCameraBinding.viewFrame.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.topMargin = 84;
        mViewCameraBinding.viewFrame.setLayoutParams(layoutParams);

        showComm();
        mMainHandler.postDelayed(() -> {
            // 延时隐藏，防止事件冲突
            mViewCameraBinding.layoutShowFull2d.setVisibility(GONE);
            mViewCameraBinding.llBackMirror.setVisibility(AvmApp.OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH==0 && AvmApp.OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH ==0 ? GONE :VISIBLE);
            showBottomView();
            mViewCameraBinding.rlClose.setVisibility(GONE);
            mViewCameraBinding.abFullClose.setVisibility(VISIBLE);
            isChangeGear = false;
        }, 200);

      CameraGLSurfaceView.glStatus = 0;
//        AvmApp.getInstance().getViewBottom().showFullWin("显示全屏");
//       bottomDialog.show();

//      setWindowType(hisModel,2);
        if(AvmApp.mAvmRvcState == 1){
            KLog.d("rvc isShow");
            //此时表示正在显示
            mMainHandler.postDelayed(() -> {
                AvmApp.mAvmRvcState = 0;
                int resRvc = bvavmJNI.bwNotifyRVC(0);
                KLog.d("关闭resRvc  = " + resRvc);
                isCanCloseRvc = false;
            },2000);
        }
        //CameraViewModelHelper.getInstance().openAndClose();
    }

    private boolean isCanCloseRvc = false;

//    public void notifyRvc(){
//        //此处表示可以退出rvc
//        isCanCloseRvc = true;
//        if(AvmApp.mAvmRvcState == 1 && isShowing){
//            AvmApp.mAvmRvcState = 0;
//            int resRvc = bvavmJNI.bwNotifyRVC(0);
//            KLog.d("notifyRvc 关闭resRvc  = " + resRvc);
//            isCanCloseRvc = false;
//        }
//    }

    public void showView() {
        if (isShowing) {
            return;
        }
        showComm();
    }

    private  boolean isSmartWinToFull = false;
    public void showComm() {
        showRootView();
        Log.i(TAG, isFirstOpen + " isFirstOpen AvmRuntime 开始 显示AVM showComm t底部透明： " + mWindowLps);
        DataManager.writeFault(DataConstant.Code.APK_OPEN);
        DataManager.writeFault(DataConstant.Code.BP_SHOW);
//        isSmartWin = false;
        updateWind();
        isShowing = true;
        CameraViewModelHelper.getInstance().openCamera();
        hidViewButtonTimer.start(viewPosition);
        mViewCameraBinding.smartGroupId.setVisibility(VISIBLE);
        //mViewCameraBinding.layoutWideAngle.setVisibility(VISIBLE);
        hideSystemUI(false);
        //hideDockSystemUI(false);
        tabView();
        skinView();
        //初始化进来也要显示上一次设置的透明度的车模

//        setTransparentIndexTab();
        if(!isFirstOpen) {
            if (isFullWin) {
                SystemProperties.setGlobal("avm_displaymode", 1);
            } else {
                SystemProperties.setGlobal("avm_displaymode", 2);
            }
            SystemProperties.setGlobal("avm_state", 1);
        }else {
            isFirstOpen = false;
        }
        int calibrateBtn = Settings.System.getInt(getContext().getContentResolver(), "avm.calibrate", 0);
        if (calibrateBtn > 0) {
            mViewCameraBinding.layoutCalibrateId.setVisibility(VISIBLE);
        }
        inputViewModel();
      isDismissView = false;
        if (isSmartWin) {
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
        }
        if (isFullWin){// R档关闭avm，手动进来不需要记忆视角，需要回到2d后视角
            Log.i(TAG, isFullWin + "  sCameraDirection 当前视图： " + isSmartWin);
            if (hisPosition >0){
                mViewCameraBinding.segmentTab.setSelectTab(hisPosition);
                hisPosition = -1;// R挡的时候需要记忆，广角或3d模式
            }
          mViewCameraBinding.settingView.checkButton();
        }

        rearviewMirrorView.setListener(onVisibilityListener);
        settingView.setListener(onVisibilityListenerSettingView);
        Log.i(TAG, isFullWin + "  isFullWin 显示AVM 结束 showComm isSmartWin： " + isSmartWin);
        mViewCameraBinding.llSetting.setSelected(false);
        mViewCameraBinding.llBackMirror.setSelected(false);
        bvavmJNI.bwClearCarBottomImage();
    }

    private  void  inputViewModel(){
      KLog.d(isDismissView+" isDismissView hisModel= " + hisModel +"hisPosition= "+hisPosition + "viewPosition="+ viewPosition);
      if (isDismissView && hisPosition == 2){

        if (hisModel == ViewType.ReverseIn){
          CameraGLSurfaceView.glStatus = bvavmJNI.BW_2D_REAR_120;
          setAngleStatus(bvavmJNI.BW_2D_REAR_120);
//          mViewCameraBinding.segmentTab.setSelectTab(hisPosition);
          hisPosition = -1;// R挡的时候需要记忆，广角或3d模式
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
            BvAvmJNIHelper.getInstance().bwSetTrajLineStatus(3);
          isDismissView = false;
          return;
        }
      }
      viewShowStatus(hisModel,1);
    }

    RearviewMirrorView.OnVisibilityListener onVisibilityListener = new RearviewMirrorView.OnVisibilityListener() {
        @Override
        public void Visibility(boolean isVisibility) {// 取消按钮高亮
            if(settingView.getVisibility()==GONE){
                mViewCameraBinding.llSetting.setSelected(false);
            }
            if(rearviewMirrorView.getVisibility()==GONE){
                mViewCameraBinding.llBackMirror.setSelected(false);
            }
        }
    };
    SettingView.OnVisibilityListener onVisibilityListenerSettingView = new SettingView.OnVisibilityListener() {
        @Override
        public void Visibility(boolean isVisibility) {// 取消按钮高亮
            if(settingView.getVisibility()==GONE){
                mViewCameraBinding.llSetting.setSelected(false);
            }
           if(rearviewMirrorView.getVisibility()==GONE){
               mViewCameraBinding.llBackMirror.setSelected(false);
           }
        }
    };

    private void hideSystemUI(boolean isShowing) {
        KLog.d("设置：2");
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(mViewCameraBinding.mainAvmViewRootId);
        if (isShowing) {
            if (windowInsetsController != null) {
                windowInsetsController.show(WindowInsetsCompat.Type.statusBars());
            }
        } else {
            if (windowInsetsController != null) {
                windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
            } else {
                KLog.d("windowInsetsController: 隐藏状态栏 null");
            }
        }

    }

    //隐藏导航dock兰
    private void hideDockSystemUI(boolean isShowing) {
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(mViewCameraBinding.mainAvmViewRootId);
        if (isShowing) {
            if (windowInsetsController != null) {
                windowInsetsController.show(WindowInsetsCompat.Type.navigationBars());
            }
        } else {
            if (windowInsetsController != null) {
                windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
            }
        }

    }



    private  void  setCameraViewLayer(){
     int left =  mViewCameraBinding.mainAvmViewRootId.getPaddingLeft();
     int right =  mViewCameraBinding.mainAvmViewRootId.getPaddingRight();
     int bottom =  0;
     int top =  mViewCameraBinding.mainAvmViewRootId.getPaddingTop();
        KLog.d("setCameraViewLayer");
      if (mWindowLps.height == 1080 && !isSmartWin){
        bottom = 108;

      }
        KLog.d("setCameraViewLayer bottom:"+bottom+"mWindowLps.heigh:"+ mWindowLps.height);
      mViewCameraBinding.mainAvmViewRootId.setPadding(left,top,right,bottom);
    }

    /**
     * 更新窗口
     */
    @SuppressLint("WrongConstant")
    private void updateWind() {
        boolean attachedToWindow = mViewCameraBinding.getRoot().isAttachedToWindow();
        mWindowLps.alpha = 0.0f;
//      KLog.d("窗口层级 mWindowLpsBottom："+mWindowLpsBottom);

      if (mViewCameraBinding.getRoot().getParent() == null && !attachedToWindow){
        mWindowManager.addView(mViewCameraBinding.getRoot(), mWindowLps);
        return;
      }
      mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(), mWindowLps);
    }

    //R档时如果是2D状态，默认显示倒车视角及显示2D切换图标
    public void show2DView() {
        if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {
            KLog.d(" layout2d show2DView ");
            mViewCameraBinding.layout2d.setVisibility(View.VISIBLE);
            mViewCameraBinding.layout3d.setVisibility(View.GONE);
            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_UNDISTORT);
//            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
            viewModel.setLiveDataCamera2DTopUI(ViewSwitchManager.CAMERA_2_D_BOTTOM);
        }
    }

    //雷达提示音显隐
    public void showRadarSoundView(int isVisible) {
         KLog.d("雷达提示音 showRadarSoundView isVisible " + isVisible);
        if (0 < isVisible && isVisible < 6) {
            //隐藏掉雷达提示音
            //mViewCameraBinding.radarSoundLayout.setVisibility(VISIBLE);
            mViewCameraBinding.radarSoundLayout.setVisibility(GONE);
            mViewCameraBinding.radarSoundIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_radar_sound_sel));
        } else {
            mViewCameraBinding.radarSoundLayout.setVisibility(GONE);
        }
    }

    //雷达故障提示显隐
    public void showParkingAssistView(int isVisible) {
        if (isVisible == 0) {
            mViewCameraBinding.parkingAssistLayout.setVisibility(GONE);
        } else {
            mViewCameraBinding.parkingAssistLayout.setVisibility(VISIBLE);
        }
    }


    // 是否关闭，用来处理R档推出的时候，记忆广角、3d
    private  boolean isDismissView = false;

    public void dismissView(String position) {
//        Log.e("AvmRuntime", Log.getStackTraceString(new Throwable()));
        tabSelectFromUser = false;
        KLog.i(position + " position AvmRuntime dismissView isShowing = " + isShowing+"turnValue:"+CameraViewModelHelper.getInstance().turnValue);
        if(position != null && position.contains("转向延时500ms退出") && (CameraViewModelHelper.getInstance().turnValue == 1
                || CameraViewModelHelper.getInstance().turnValue == 2) && isSmartWin){
            return;
        }
        if(AvmApp.mAvmRvcState== 1  && AvmApp.getInstance().getCameraView().isFullWin){
            KLog.d("dismissView  is rvc  show ");
            return;
        }
        if (!isShowing) {
            return;
        }
//        hideView();
        CameraViewModelHelper.getInstance().setViewChange();
        DataManager.writeFault(DataConstant.Code.BP_HIDE);
        //主动退出
//        bottomDialog.dismiss();
        isChangeGear = false;
        isFullWin = false;
        isSmartWin = false;
        isShowing = false;
        isDismissView = true;
        canShowAct = false;
        boolean attachedToWindow = mViewCameraBinding.getRoot().isAttachedToWindow();
        KLog.d(" attachedToWindowcameraBinding dismissView attached = " + attachedToWindow);
        mWindowLps.alpha = 0.0f;
        mWindowLps.width = 1;
        mWindowLps.height = 1;
        if (mViewCameraBinding.getRoot().getParent() != null){
          mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(), mWindowLps);
        }

        //释放摄像头画面数据
//            BvAvmJNIHelper.getInstance().avmDeInit();
        SystemProperties.setGlobal("avm_state", 0);
        SystemProperties.setGlobal("avm_displaymode", 0);
        if (settingView.getVisibility() == View.VISIBLE) {
            settingView.setVisibility(GONE);
            mViewCameraBinding.liftBg.setVisibility(GONE);
        }
        if (mViewCameraBinding.infobook.getVisibility() == VISIBLE) {
            mViewCameraBinding.infobook.setVisibility(GONE);
            mViewCameraBinding.infoBg.setVisibility(GONE);
            mViewCameraBinding.liftBg.setVisibility(GONE);
        }
        if (rearviewMirrorView.getVisibility() == VISIBLE) {
            rearviewMirrorView.setVisibility(View.GONE);
            mViewCameraBinding.liftBg.setVisibility(GONE);
        }
        hideView();
        CameraGLSurfaceView.glStatus = 0;
        //CameraViewModelHelper.getInstance().closeCamera()
    }

    public void removeView() {
        if (mViewCameraBinding == null) return;
        boolean attachedToWindow = mViewCameraBinding.getRoot().isAttachedToWindow();
        if (attachedToWindow) {
            mWindowManager.removeView(mViewCameraBinding.getRoot());
        }
    }

    public void setBtnSettingSelectView(boolean btnSettingSelect) {
        this.btnSettingSelect = btnSettingSelect;
        mViewCameraBinding.llBackMirror.setSelected(false);
        if (settingView.getVisibility() == View.VISIBLE) {
            settingView.closeUI(true);
            mViewCameraBinding.infobook.setVisibility(GONE);
            settingView.setVisibility(GONE);
            mViewCameraBinding.liftBg.setVisibility(GONE);
            mViewCameraBinding.llSetting.setSelected(false);
            mViewCameraBinding.infoBg.setVisibility(GONE);

        } else {
            settingView.setVisibility(View.VISIBLE);
            mViewCameraBinding.liftBg.setVisibility(VISIBLE);
            mViewCameraBinding.llSetting.setSelected(true);
        }
        rearviewMirrorView.setVisibility(GONE);
//        startCalibration();
        KLog.d("btnSettingSelect = " + btnSettingSelect);

    }

    public void setBtnRearSelectView(boolean btnRearSelect) {
        this.btnRearSelect = btnRearSelect;
        mViewCameraBinding.llSetting.setSelected(false);
        settingView.setVisibility(GONE);
        if (mViewCameraBinding.infobook.getVisibility() == VISIBLE) {
            mViewCameraBinding.infobook.setVisibility(GONE);
            mViewCameraBinding.infoBg.setVisibility(GONE);
            mViewCameraBinding.liftBg.setVisibility(GONE);
        }
        if (rearviewMirrorView.getVisibility() == VISIBLE) {
            rearviewMirrorView.setVisibility(View.GONE);
            mViewCameraBinding.llBackMirror.setSelected(false);
            mViewCameraBinding.liftBg.setVisibility(GONE);
        } else {
            mViewCameraBinding.llBackMirror.setSelected(true);
            rearviewMirrorView.setVisibility(View.VISIBLE);
//            rearviewMirrorView.skinView();
            mViewCameraBinding.liftBg.setVisibility(VISIBLE);
        }
        KLog.d("setBtnRearSelectView = " + btnRearSelect);

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

    public void changeRearviewShow(float speedValue){
        rearviewMirrorView.changeShow(speedValue);
    }

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

    private boolean isOnTouch = false;

    private int touchIndex = 0;

    public boolean onTouch(View v, MotionEvent event) {
        KLog.d(" onClick 9 ");
        viewModel.setRunning(true);

        if (mViewCameraBinding.infobook.getVisibility() != VISIBLE )
           mViewCameraBinding.llSetting.setSelected(false);
           mViewCameraBinding.llBackMirror.setSelected(false);
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
                if (settingView.getVisibility() == VISIBLE&&mViewCameraBinding.infobook.getVisibility() != VISIBLE) {
                    settingView.setVisibility(GONE);
                    mViewCameraBinding.liftBg.setVisibility(GONE);
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
                if (viewPosition == 1&&mViewCameraBinding.infoBg.getVisibility()!=VISIBLE) {
                    //3D的时候拖动车模
                    KLog.i(endX + " startX开始拖动车模bwSetTouchScreenPos " + endY);
                    int finalTouch_x = endX;
                    int finalTouch_y = endY;
                    if (endX < 1860 && endX > 682 && endY < 950 && endY > 113) {
                        mMainHandler.postDelayed(() -> {
                            int touchPos = bvavmJNI.bwSetTouchScreenPos(finalTouch_x, finalTouch_y);
                            KLog.i("滑动车模角度touchPos  " + touchPos);
                            if (touchIndex != touchPos) {
                                if (touchPos == 1) {
                                    mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_leftfront));
                                    hidViewButtonTimer.start(1);
                                    chick3DView(ViewSwitchManager.CAMERA_3_D_LEFT_FRONT);
                                } else if (touchPos == 2) {
                                    mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_leftback));
                                    hidViewButtonTimer.start(1);
                                    chick3DView(ViewSwitchManager.CAMERA_3_D_LEFT_REAR);
                                } else if (touchPos == 3) {
                                     mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_rightfront));
                                    hidViewButtonTimer.start(1);
                                    chick3DView(ViewSwitchManager.CAMERA_3_D_RIGHT_FRONT);
                                } else if (touchPos == 4) {
                                    mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_rightback));
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

    private int x;
    private int y;
    private boolean isMove;
    public boolean onTouchView(View v, MotionEvent event) {
        if (!ENABLE_SKIP) { // 不许滑动
            return true;
        }

        viewModel.setRunning(true);
        int action = event.getAction();
        KLog.i("onTouch: viewPosition=" + viewPosition +"event x"+event.getX()+"eventY:"+event.getY()+" action:"+action + " , ENABLE_SKIP = " + ENABLE_SKIP);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mViewCameraBinding.llBackMirror.setSelected(false);
                mViewCameraBinding.llSetting.setSelected(false);
                if (rearviewMirrorView.getVisibility() == View.VISIBLE) {
                    rearviewMirrorView.setVisibility(GONE);
                }
                if (settingView.getVisibility() == VISIBLE) {
                    settingView.setVisibility(GONE);
                    mViewCameraBinding.liftBg.setVisibility(GONE);
                }
                if (mViewCameraBinding.infobook.getVisibility() == VISIBLE) {
                    mViewCameraBinding.infobook.setVisibility(GONE);
                    mViewCameraBinding.infoBg.setVisibility(GONE);
                    mViewCameraBinding.liftBg.setVisibility(GONE);
                }
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                isMove = false;
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    // 点击事件
                    if(isSnapToPosition){
                        KLog.i("onTouch: isSnapToPosition");
                        if(animator != null){
                            isSnapToPosition = false;
                            KLog.i("onTouch: isSnapToPosition cancle animator");
                            animator.cancel();
                        }
                    }
                    if (isSmartWin) {
//                        showFull2DByOnTouch();
                        AvmRuntime.self().userClick(DataDefine.EVT_CLICK_LEFT_CARD);
                    }
                } else {
                    if(!isSnapToPosition && AvmRuntime.self().isHalfScreenState()) {
                        snapToPosition();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isSmartWin && !isSnapToPosition){
                    isMove = true;
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    int mX = mWindowLps.x + movedX;
                    int mY = mWindowLps.y + movedY;
//                    if (AvmService.mIsScreen) {
//                        //表示在分屏
//                        if (AvmService.isLeftScreen) {
//                            // 左分屏：X范围0-735
//                            mX = Math.max(0, Math.min(mX, 735));
//                        } else {
//                            // 右分屏：X范围760-1465
//                            mX = Math.max(760, Math.min(mX, 1465));
//                        }
//                    } else {
//                        // 正常模式：X范围0-1465
                    mX = Math.max(0, Math.min(mX, 1465));
                    //}
                    mWindowLps.x = mX;
                    mWindowLps.y = Math.max(0, Math.min(mY, 349));  // 1080 - 108 - 623 = 349
                    x = nowX;
                    y = nowY;
                    mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(), mWindowLps);
                }
                break;
        }
        return true;
    }

    private boolean isSnapToPosition = false;//正在拖动位置
    private ValueAnimator animator;
    public void snapToPosition() {
        KLog.i("snapToPosition: AvmService.isLeftScreen"+AvmService.isLeftScreen+"AvmService.mIsStartStatus:"+AvmService.mIsStartStatus);
        isSnapToPosition = true;
        //mViewCameraBinding.viewFrame.setEnabled(false);
        int screenHeight = 1080 - 108;
        // 计算当前悬浮窗中心Y坐标
        int currentCenterY = mWindowLps.y + 623 / 2;
        int targetY = (currentCenterY <= screenHeight / 2) ? 84 : 320;
        // 确定目标X坐标
        int targetX;
        if (AvmService.mIsScreen) {
            targetX = AvmService.isLeftScreen ? WINDOW_SHOW_LEFT_SCREEN : WINDOW_SHOW_RIGHT_SCREEN; // 右分屏810，左分屏50
        } else {
            targetX = AvmService.mIsStartStatus && AvmService.mMapSpeedStatus ? WINDOW_SHOW_RIGHT : AvmService.mIsStartStatus ? WINDOW_SHOW_MIDDLE : WINDOW_SHOW_LEFT; // 正常模式
        }

        // 动画过渡到目标位置
        int startX = mWindowLps.x;
        int startY = mWindowLps.y;
        if(animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            int newX = (int) (startX + (targetX - startX) * fraction);
            int newY = (int) (startY + (targetY - startY) * fraction);
            mWindowLps.x = newX;
            mWindowLps.y = newY;
            mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(), mWindowLps);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 确保最终位置准确
                mWindowLps.x = targetX;
                mWindowLps.y = targetY;
                mWindowManager.updateViewLayout(mViewCameraBinding.getRoot(), mWindowLps);
                isSnapToPosition = false;
                //mViewCameraBinding.viewFrame.setEnabled(true);
            }
        });
        animator.start();
    }

    private final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case CALIBRATION_SHOW_WINDOW:
                    KLog.w("CALIBRATION_SHOW_WINDOW");
                    mViewCameraBinding.calibration.setVisibility(VISIBLE);
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
                KLog.d(" layout2d start " + mViewCameraBinding.segmentTab.getCurrentTab());
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {
                    mViewCameraBinding.viewShow2dGroupId.setVisibility(VISIBLE);
                }

            } else {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    mViewCameraBinding.viewShow3dGroupId.setVisibility(VISIBLE);
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
                mViewCameraBinding.viewShow2dGroupId.setVisibility(GONE);
            } else {
                mViewCameraBinding.viewShow3dGroupId.setVisibility(GONE);
//                mViewCameraBinding.cameraRightRear.setVisibility(GONE);
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
        KLog.e("msg: = " + msg + "  param1: = " + param1 + "    param2: = " + param2);
        CallBackHelper.getInstance().setup(msg, param1, param2);
    }

    // 视屏流code
    public static final Set<Integer> VALID_CODES = Set.of(37, 38, 39, 40, 41, 42, 43, 44);

    //埋点
    public static void bAvmFault(int code, int param1, int param2) {
        KLog.i("bAvmFault code:"+code);
        if(VALID_CODES.contains(code)){
            KLog.i("bAvmFault is vedio code");
            //mCodeThrottler.submitCode(code);
            DataManager.writeFault(code);
        }else {
            DataManager.writeFault(code);
        }
    }

    public void setRadar(int model, int len,int gear) {

        mViewCameraBinding.rearRadarViewId.status(model, len, gear);
    }

    public void hideView() {
        KLog.i("hideView");
        mViewCameraBinding.getRoot().setVisibility(GONE);
    }

    public void showRootView() {
        KLog.i("showRootView");
        mViewCameraBinding.getRoot().setVisibility(VISIBLE);
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

    boolean cameraStatus = false;

    private void setAVMBreakdown(int msg, int param1, int param2) {

        KLog.e("msg: = " + msg + "  param1: = " + param1 + "   param2: = " + param2 +"  cameraShowType:"+cameraShowType);
//      param2 = bvavmJNI.CAMERA2_ERR_OK；
//      if (cameraBinding.getRoot().getParent() != null && cameraBinding.getRoot().isAttachedToWindow()){
//        mWindowLps.alpha = 1.0f;
//        mWindowLps.format = isSmartWin ? PixelFormat.TRANSLUCENT : PixelFormat.UNKNOWN;
//        mWindowManager.updateViewLayout(cameraBinding.getRoot(),mWindowLps);
//        KLog.e(" setAVMBreakdown mWindowLps: = " + mWindowLps);
//      }

        if (msg == bvavmJNI.BWAVM_MSG_CAMERA2_STATUS) {
            if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {//2D
                boolean isCanOpreateBreakDown = (param1 == bvavmJNI.BWAVM_FRONT_CAM_ID && cameraShowType == 0) ||
                        (param1 == bvavmJNI.BWAVM_REAR_CAM_ID && cameraShowType == 1) ||
                        (param1 == bvavmJNI.BWAVM_LEFT_CAM_ID && (cameraShowType == 2 || cameraShowType == 3 || cameraShowType == 4)) ||
                        (param1 == bvavmJNI.BWAVM_RIGHT_CAM_ID && (cameraShowType == 2 || cameraShowType == 3 || cameraShowType == 4));
                KLog.e("setAVMBreakdown isCanOpreateBreakDown: = " + isCanOpreateBreakDown);
                switch (param1) {
                    case bvavmJNI.BWAVM_FRONT_CAM_ID: {
                            if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                                mViewCameraBinding.cameraTop.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                        : R.mipmap.ic_camera_default));
                                mViewCameraBinding.cameraTop.setRotation(0);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                                    }, 200);
                                }
                            } else {
                                cameraStatus = true;
                                mViewCameraBinding.cameraTop.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown 
                                        ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                                mViewCameraBinding.cameraTop.setRotation(0);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                                    }, 200);
                                 }
                            }
                    }
                    break;
                    case bvavmJNI.BWAVM_REAR_CAM_ID: {
                            if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                                mViewCameraBinding.cameraBottom.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                        : R.mipmap.ic_camera_default));
                                mViewCameraBinding.cameraBottom.setRotation(180);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                                    }, 200);
                                }
                            } else {
                                cameraStatus = true;
                                mViewCameraBinding.cameraBottom.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown 
                                        ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                                mViewCameraBinding.cameraBottom.setRotation(180);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                                    }, 200);
                                }
                            }

                    }
                    break;
                    case bvavmJNI.BWAVM_LEFT_CAM_ID: {
                            if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                                mViewCameraBinding.cameraLift.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                        : R.mipmap.ic_camera_default));
                                mViewCameraBinding.cameraLift.setRotation(270);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                                    }, 200);
                                }
                            } else {
                                cameraStatus = true;
                                mViewCameraBinding.cameraLift.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown 
                                        ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                                mViewCameraBinding.cameraLift.setRotation(270);
                                int BWAVM_RIGHT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(3);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        if(BWAVM_RIGHT_CAM_ID == -1) {
                                            mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                                        }
                                    }, 200);
                                }
                            }
                    }
                    break;
                    case bvavmJNI.BWAVM_RIGHT_CAM_ID: {
                            if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                                mViewCameraBinding.cameraRight.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown ? R.mipmap.ic_camera_click
                                        : R.mipmap.ic_camera_default));
                                mViewCameraBinding.cameraRight.setRotation(90);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                                    }, 200);
                                }
                            } else {
                                cameraStatus = true;
                                mViewCameraBinding.cameraRight.setImageDrawable(mContext.getDrawable(isCanOpreateBreakDown 
                                        ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                                mViewCameraBinding.cameraRight.setRotation(90);
                                int BWAVM_LEFT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(2);
                                if(isCanOpreateBreakDown) {
                                    mMainHandler.postDelayed(() -> {
                                        if(BWAVM_LEFT_CAM_ID == -1) {
                                            mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                                        }
                                    }, 200);
                                }
                            }
                    }
                    break;
                    default:
                        break;
                }

            }else if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {//3D
                switch (param1) {
                    case bvavmJNI.BWAVM_FRONT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            mViewCameraBinding.cameraLeftFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraLeftFront.setRotation(135);
                            mViewCameraBinding.cameraRightFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraRightFront.setRotation(225);
                        } else {
                            mViewCameraBinding.cameraLeftFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraLeftFront.setRotation(135);
                            mViewCameraBinding.cameraRightFront.setImageDrawable(
                                    mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraRightFront.setRotation(225);
                        }
                    }
                    break;
                    case bvavmJNI.BWAVM_REAR_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            mViewCameraBinding.cameraRightRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraRightRear.setRotation(315);
                            mViewCameraBinding.cameraLeftRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraLeftRear.setRotation(45);
                        } else {
                            mViewCameraBinding.cameraRightRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraRightRear.setRotation(315);
                            mViewCameraBinding.cameraLeftRear.setImageDrawable( mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraLeftRear.setRotation(45);
                        }

                    }
                    break;
                    case bvavmJNI.BWAVM_LEFT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            mViewCameraBinding.cameraLeftFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraLeftFront.setRotation(135);
                            mViewCameraBinding.cameraLeftRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraLeftRear.setRotation(45);
                        } else {
                            mViewCameraBinding.cameraLeftFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 1 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraLeftFront.setRotation(135);
                            mViewCameraBinding.cameraLeftRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 3 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraLeftRear.setRotation(45);
                        }

                    }
                    break;
                    case bvavmJNI.BWAVM_RIGHT_CAM_ID: {
                        if (param2 == bvavmJNI.CAMERA2_ERR_OK) {
                            mViewCameraBinding.cameraRightFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraRightFront.setRotation(225);
                            mViewCameraBinding.cameraRightRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_click :R.mipmap.ic_camera_default));
                            mViewCameraBinding.cameraRightRear.setRotation(315);
                        } else {
                            mViewCameraBinding.cameraRightFront.setImageDrawable(mContext.getDrawable(camera3DShowType == 2 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraRightFront.setRotation(225);
                            mViewCameraBinding.cameraRightRear.setImageDrawable(mContext.getDrawable(camera3DShowType == 4 ? R.mipmap.ic_camera_fault : R.mipmap.ic_camera_fault_nor));
                            mViewCameraBinding.cameraRightRear.setRotation(315);
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        }

    }

    public void skinView() {
        UiModeManager uiModeManager = (UiModeManager) mContext.getSystemService(Context.UI_MODE_SERVICE);
        int uiMode = uiModeManager.getNightMode();
        KLog.e("skinView: isSmartWin " + isSmartWin);
        if (mViewCameraBinding == null) {
            return;
        }
        if (isSmartWin) {
            mViewCameraBinding.mainAvmViewRootId.setBackgroundColor(Color.TRANSPARENT);
            mViewCameraBinding.mainAvmViewRootId.invalidate();
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
                mViewCameraBinding.mainAvmViewRootId.setBackground(mContext.getDrawable(R.color.avm_bg));
                mViewCameraBinding.llCameraOpenFail.setBackgroundResource(R.drawable.selector_breakdown_bg);
                mViewCameraBinding.ivOpenFail.setImageDrawable(mContext.getDrawable(R.mipmap.info_default_56));
                mViewCameraBinding.tvOpenFail.setTextColor(mContext.getResources().getColor(R.color.test_color_D9));
                mViewCameraBinding.llSetting.setBackgroundResource(R.drawable.button_select);
                mViewCameraBinding.llBackMirror.setBackgroundResource(R.drawable.button_select);
                mViewCameraBinding.ivSetting.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_setting));
                mViewCameraBinding.ivBackMirror.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_mirror));
                mViewCameraBinding.segmentTab.setThumbDrawable(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentTab.setThumbDrawable3(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentTab.setThumbDrawable2(R.drawable.tan_selector_camera_thumb);
                mViewCameraBinding.segmentTab.setBackground(mContext.getResources().getDrawable(R.drawable.tab_selector_thumb));
                mViewCameraBinding.segmentTab.setTextSelectColor(R.color.setting_view_title_color, 0);
                mViewCameraBinding.segmentTab.setTextUnselectColor(R.color.setting_view_content_color);
                mViewCameraBinding.manualCalibration.setTextColor(mContext.getResources().getColor(R.color.setting_view_bg));
                mViewCameraBinding.automaticCalibration.setTextColor(mContext.getResources().getColor(R.color.setting_view_bg));
                mViewCameraBinding.infobook.setBackgroundResource(R.drawable.shape_bg_nor);
                mViewCameraBinding.infoTitle.setTextColor(mContext.getResources().getColor(R.color.setting_view_title_color));
                mViewCameraBinding.infoContent.setTextColor(mContext.getResources().getColor(R.color.setting_tab_unselect_color));
                mViewCameraBinding.infoOk.setTextColor(mContext.getResources().getColor(R.color.setting_view_title_color));
                mViewCameraBinding.infoOk.setBackgroundResource(R.drawable.shape_text_bg_nor);
                mViewCameraBinding.abFullClose.setBackgroundResource(R.mipmap.ic_close_default);
                mViewCameraBinding.infoBg.setBackgroundResource(R.color.setting_dialog_bg);

                mViewCameraBinding.segmentWideAngle.setThumbDrawable(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentWideAngle.setThumbDrawable3(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentWideAngle.setThumbDrawable2(R.drawable.tan_selector_camera_thumb);
                mViewCameraBinding.segmentWideAngle.setBackground(mContext.getResources().getDrawable(R.drawable.shape_seg_angle_bg));
                mViewCameraBinding.segmentWideAngle.setTextSelectColor(R.color.test_color, 1);
                mViewCameraBinding.segmentWideAngle.setTextUnselectColor(R.color.setting_tab_unselect_color);
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                // CustomToast.showToast("白天模式");
                //SkinCompatManager.getInstance().loadSkin("day",null,SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                bvavmJNI.bwSetIsDay(1);
                mViewCameraBinding.mainAvmViewRootId.setBackground(mContext.getDrawable(R.color.avm_bg_day));
                mViewCameraBinding.llCameraOpenFail.setBackgroundResource(R.drawable.selector_breakdown_bg_day);
                mViewCameraBinding.ivOpenFail.setImageDrawable(mContext.getDrawable(R.mipmap.info_default_56_day));
                mViewCameraBinding.tvOpenFail.setTextColor(mContext.getResources().getColor(R.color.test_color_0A1532));
                mViewCameraBinding.llSetting.setBackgroundResource(R.drawable.button_select_day);
                mViewCameraBinding.llBackMirror.setBackgroundResource(R.drawable.button_select_day);
                mViewCameraBinding.ivSetting.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_setting_day));
                mViewCameraBinding.ivBackMirror.setImageDrawable(mContext.getDrawable(R.drawable.button_select_iv_mirror_day));
                mViewCameraBinding.segmentTab.setThumbDrawable(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentTab.setThumbDrawable3(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentTab.setThumbDrawable2(R.drawable.tan_selector_camera_thumb_day);
                mViewCameraBinding.segmentTab.setBackground(mContext.getResources().getDrawable(R.drawable.tab_selector_thumb_day));
                mViewCameraBinding.segmentTab.setTextSelectColor(R.color.setting_view_title_color_day, 0);
                mViewCameraBinding.segmentTab.setTextUnselectColor(R.color.setting_view_content_color_day);
                mViewCameraBinding.manualCalibration.setTextColor(mContext.getResources().getColor(R.color.white));
                mViewCameraBinding.automaticCalibration.setTextColor(mContext.getResources().getColor(R.color.white));
                mViewCameraBinding.abFullClose.setBackgroundResource(R.mipmap.ic_close_default_day);

                mViewCameraBinding.infobook.setBackgroundResource(R.drawable.shape_bg_nor_day);
                mViewCameraBinding.infoTitle.setTextColor(mContext.getResources().getColor(R.color.setting_view_content_color_day));
                mViewCameraBinding.infoContent.setTextColor(mContext.getResources().getColor(R.color.setting_tab_unselect_color_day));
                mViewCameraBinding.infoOk.setTextColor(mContext.getResources().getColor(R.color.setting_view_content_color_day));
                mViewCameraBinding.infoOk.setBackgroundResource(R.drawable.shape_text_bg_nor_day);
                mViewCameraBinding.infoBg.setBackgroundResource(R.color.setting_dialog_bg_day);

                mViewCameraBinding.segmentWideAngle.setThumbDrawable(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentWideAngle.setThumbDrawable3(R.drawable.tab_selector_thumb_nor);
                mViewCameraBinding.segmentWideAngle.setThumbDrawable2(R.drawable.tan_selector_camera_thumb);
                mViewCameraBinding.segmentWideAngle.setBackground(mContext.getResources().getDrawable(R.drawable.shape_seg_angle_bg_day));
                mViewCameraBinding.segmentWideAngle.setTextSelectColor(R.color.test_color, 1);
                mViewCameraBinding.segmentWideAngle.setTextUnselectColor(R.color.setting_view_content_color_day);
                break;
        }


    }

    private void chick2DView(String type) {
        int BWAVM_FRONT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(0);
        int BWAVM_REAR_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(1);
        int BWAVM_LEFT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(2);
        int BWAVM_RIGHT_CAM_ID = BvAvmJNIHelper.getInstance().bwGetCamerastatus(3);
        switch (type) {
            case CAMERA_2_D:
                mViewCameraBinding.cameraTop.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                mViewCameraBinding.cameraTop.setRotation(0);
                mViewCameraBinding.cameraLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                mViewCameraBinding.cameraLift.setRotation(270);
                mViewCameraBinding.cameraBottom.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                mViewCameraBinding.cameraBottom.setRotation(180);
                mViewCameraBinding.cameraRight.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_default));
                mViewCameraBinding.cameraRight.setRotation(90);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_back));
                mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
                cameraShowType = 0;
                mMainHandler.post(() -> {
                    if(BWAVM_FRONT_CAM_ID==0){
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case CAMERA_2_D_TOP :
                cameraShowType = 0;
                showCameraImgStatus(mViewCameraBinding.cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(mViewCameraBinding.cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(mViewCameraBinding.cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(mViewCameraBinding.cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_front));
                mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
                mMainHandler.post(() -> {
                    if(BWAVM_FRONT_CAM_ID==0){
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case CAMERA_2_D_LIFT:
                cameraShowType = 2;
                showCameraImgStatus(mViewCameraBinding.cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(mViewCameraBinding.cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(mViewCameraBinding.cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(mViewCameraBinding.cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_right));
                if (!isSmartWin) {
                    mViewCameraBinding.cameraImageLayoutLift.setVisibility(VISIBLE);
                } else {
                    mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
                }

                mViewCameraBinding.cameraIvLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_cameraview_card_left_2));
                mMainHandler.post(() -> {
                    if(BWAVM_RIGHT_CAM_ID==0 || BWAVM_LEFT_CAM_ID ==0){
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case CAMERA_2_D_BOTTOM:
                cameraShowType = 1;
                showCameraImgStatus(mViewCameraBinding.cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(mViewCameraBinding.cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(mViewCameraBinding.cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(mViewCameraBinding.cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_back));
                mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
                mMainHandler.post(() -> {
                    if(BWAVM_REAR_CAM_ID==0){
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case CAMERA_2_D_RIGHT:
                cameraShowType = 3;
                showCameraImgStatus(mViewCameraBinding.cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(mViewCameraBinding.cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(mViewCameraBinding.cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(mViewCameraBinding.cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_right));
                if (!isSmartWin) {
                    mViewCameraBinding.cameraImageLayoutLift.setVisibility(VISIBLE);
                } else {
                    mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
                }
                mViewCameraBinding.cameraIvLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_cameraview_card_left_2));
                mMainHandler.post(() -> {
                    if(BWAVM_RIGHT_CAM_ID==0 || BWAVM_LEFT_CAM_ID ==0){
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case CAMERA_2_D_LIFT_RIGHT:
                cameraShowType = 4;
                showCameraImgStatus(mViewCameraBinding.cameraTop,0,BWAVM_FRONT_CAM_ID,0,true);
                showCameraImgStatus(mViewCameraBinding.cameraBottom,180,BWAVM_REAR_CAM_ID,1,true);
                showCameraImgStatus(mViewCameraBinding.cameraLift,270,BWAVM_LEFT_CAM_ID,2,true);
                showCameraImgStatus(mViewCameraBinding.cameraRight,90,BWAVM_RIGHT_CAM_ID,3,true);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_right));
                if (!isSmartWin) {
                    mViewCameraBinding.cameraImageLayoutLift.setVisibility(VISIBLE);
                } else {
                    mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
                }
                mViewCameraBinding.cameraIvLift.setImageDrawable(mContext.getDrawable(R.mipmap.ic_cameraview_card_left_2));
                mMainHandler.post(() -> {
                    if(BWAVM_RIGHT_CAM_ID==0 || BWAVM_LEFT_CAM_ID ==0){
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.GONE);
                    }else{
                        mViewCameraBinding.cameraBreakdown.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }

    public void chick3DView(String type) {
        mViewCameraBinding.cameraImageLayoutLift.setVisibility(GONE);
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
        switch (type) {
            case CAMERA_3_D_REAR:
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_back));
                break;
            case CAMERA_3_D:
                camera3DShowType = -1;
                showCameraImgStatus(mViewCameraBinding.cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(mViewCameraBinding.cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightRear,315,rightRearStatus,4,false);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_front));
                break;
            case CAMERA_3_D_LEFT_FRONT:
                camera3DShowType = 1;
                showCameraImgStatus(mViewCameraBinding.cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(mViewCameraBinding.cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightRear,315,rightRearStatus,4,false);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_leftfront));
                break;
            case CAMERA_3_D_RIGHT_FRONT:
                camera3DShowType = 2;
                showCameraImgStatus(mViewCameraBinding.cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(mViewCameraBinding.cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightRear,315,rightRearStatus,4,false);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_rightfront));
                break;
            case CAMERA_3_D_LEFT_REAR:
                camera3DShowType = 3;
                showCameraImgStatus(mViewCameraBinding.cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(mViewCameraBinding.cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightRear,315,rightRearStatus,4,false);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_leftback));
                break;
            case CAMERA_3_D_RIGHT_REAR:
                camera3DShowType = 4;
                showCameraImgStatus(mViewCameraBinding.cameraLeftFront,135,leftFrontStatus,1,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightFront,225,rightFrontStatus,2,false);
                showCameraImgStatus(mViewCameraBinding.cameraLeftRear,45,leftRearStatus,3,false);
                showCameraImgStatus(mViewCameraBinding.cameraRightRear,315,rightRearStatus,4,false);
                mViewCameraBinding.cameraIv.setImageDrawable(mContext.getDrawable(R.mipmap.ic_camera_card_rightback));
                cameraShowType = 1;
                break;
        }
    }

    /**
     * 热区点击
     */
    private void viewRedChick() {
        mViewCameraBinding.red2dTop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dTop();
                }
            }
        });
        mViewCameraBinding.red2dLift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dLift();
                }
            }
        });
        mViewCameraBinding.red2dRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dRight();
                }
            }
        });
        mViewCameraBinding.red2dBottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 0) {
                    viewModel.camera2dBottom();
                }
            }
        });
        mViewCameraBinding.red3dleftFront.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftFront();
                }
            }
        });
        mViewCameraBinding.red3dleftFront1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftFront();
                }
            }
        });
        mViewCameraBinding.red3drightFront.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dRightFront();
                }
            }
        });
        mViewCameraBinding.red3drightFront1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dRightFront();
                }
            }
        });
        mViewCameraBinding.red3dleftRear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftRear();
                }
            }
        });
        mViewCameraBinding.red3dleftRear1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dLeftRear();
                }
            }
        });
        mViewCameraBinding.red3drightRear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
                    viewModel.camera3dRightRear();
                }
            }
        });
        mViewCameraBinding.red3drightRear1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewCameraBinding.segmentTab.getCurrentTab() == 1) {
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
        TranslateAnimation showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
        showAnimation.setDuration(500);
        startAnimation(showAnimation);
        setVisibility(View.VISIBLE);
    }

    private void viewOutAnimation() {
        // 居中隐藏的动画
        TranslateAnimation hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideAnimation.setDuration(500);
        startAnimation(hideAnimation);
        setVisibility(View.GONE);
    }


    private void setViewDialog() {
        mViewCameraBinding.liftBg.setVisibility(GONE);
        if (rearviewMirrorView.getVisibility() == View.VISIBLE) {
            rearviewMirrorView.setVisibility(GONE);
            mViewCameraBinding.llBackMirror.setSelected(false);
        }
        if (settingView.getVisibility() == VISIBLE) {
            settingView.setVisibility(GONE);
            mViewCameraBinding.llSetting.setSelected(false);
        }
        if (mViewCameraBinding.infobook.getVisibility() == VISIBLE) {
            mViewCameraBinding.infobook.setVisibility(GONE);
            mViewCameraBinding.infoBg.setVisibility(GONE);
        }
    }

    public void updateTabViewIndex() {
        int outsideTabIndex = getOutsideTabIndex();
        if (outsideTabIndex != -1) segmentTab.setSelectTab(outsideTabIndex);
        KLog.d("AvmRuntime updateTabViewIndex() outsideTabIndex = " + outsideTabIndex);
        if (outsideTabIndex == 0) {
            if (CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_2D_FRONT_UNDISTORT) {
                chick2DView(CAMERA_2_D_TOP);
            } else if (CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_2D_REAR_UNDISTORT) {
                chick2DView(CAMERA_2_D_BOTTOM);
            } else if (CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_LEFT_RIGHT_FRONT
                    || CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_LEFT_RIGHT_BACK) {
                chick2DView(CAMERA_2_D_LIFT_RIGHT);
            }
        }else if(outsideTabIndex == 1){
            KLog.d("AvmRuntime updateTabViewIndex() getCameraDirection = " + CameraGLSurfaceView.getAngleOfView());
            if (CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_LEFT_REAR_3D) {
                chick3DView(CAMERA_3_D_LEFT_REAR);
            } else if (CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_RIGHT_REAR_3D) {
                chick3DView(CAMERA_3_D_RIGHT_REAR);
            } else if(CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_REAR_3D ||
                    CameraGLSurfaceView.getAngleOfView() == bvavmJNI.BW_FRONT_3D){
                chick3DView(CAMERA_3_D);
            }
        }  else if (outsideTabIndex == 2) {
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
        int viewAngle = CameraGLSurfaceView.getAngleOfView();
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
        int viewAngle = CameraGLSurfaceView.getAngleOfView();
        if (memory == DataDefine.STS_MEM_MODE_WIDE_ANGLE) {
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
