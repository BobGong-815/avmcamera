package com.autochips.avm.service;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.ASSIST_DRIVE_PAS_BUTTON_PRESS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FLWHEEL_ROTATED_DIR;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FLWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FRWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_RLWHEEL_ROTATED_DIR;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_RLWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_RRWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_EL_REVERSE_LIGHT_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_EL_SIDE_BRKLIGHT_CTRL_CMD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_FLS_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_FRS_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_PAS_SYSTEMTYPE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RADAR_ALARM_ACOUSTIC_SWITCH;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RL_MIDSNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RL_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RR_MIDSNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RR_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RSL_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RSR_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_SAS_STEERING_ANGLE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_UINM_TURN_LIGHT_SW_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_WHEEL_DIRE_SPEED;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_HIGH_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_LOW_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CABIN_DOOR_OPEN_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_CHIME_PAS_WARNTONE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_FRONT_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_DRIVERDOORAJARST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_PassengerDoorAjarSt;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LEFT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_Distance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_FRONT_DISTANCE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_FLMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_FRMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_FSLSideDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_FSRSideDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_PAS_FLDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_PAS_FRDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_PAS_RSLSideDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_PAS_RSRSideDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RLDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RLMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RRDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RRMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_REAR_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_RIGHT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_VCU_GEAR_LVL_DISP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.MIRROR_FOLD_UNFOLD_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.POWER_PARKING_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_VCU_BRKPEDPST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.VEHICLE_SPEED;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_SELECT_STATE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_REQ;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.ui.activity.MainActivity;
import com.autochips.avm.ui.view.CameraGLSurfaceView;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.ServiceUtils;
import com.autochips.avm.util.SystemProperties;
import com.avm.framwork.constant.CameraContracts;
import com.avm.framwork.helper.ThreadPoolUtil;
import com.avm.framwork.manager.CanManager;
import com.gxa.lib.car.VehicleVendorProperty;
import com.gxa.service.camera.AvmManager;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import gxa.car.power.data.CarPowerData;
import gxa.car.power.data.CarPowerSignalStatus;
import gxa.car.power.data.CarPowerWorkModeStatus;
import gxa.car.power.listener.CarPowerEventListener;
import gxa.car.power.manager.CarPowerManager;
import gxa.car.utils.VehicleHelper;
import me.goldze.mvvmhabit.utils.KLog;

public class AvmService extends Service implements AvmRuntime.ActionListener {

    public final static boolean JNI_IN_THREAD_FLAG = true;
    public final static boolean DELETE_CAMERA_FLAG = true;

    public static int MSG_ACTION_ENTER = 1;
    public static int MSG_ACTION_EXIT = 2;
    public static int MSG_CR_CAMERA = 3;
    public static int MSG_DEL_CAMERA = 4;
    public static int MSG_CLOSE_RVC = 5;
    public static int mRvcState = 0x0;

    private String exit_action = "action.syncore.EOL.mode";
    private String open_act = "action.syncore.OPEN.mode";
    private String close_act = "action.syncore.CLOSE.mode";
    private String first_open_act = "action.syncore.FOPEN.mode";
    private MyBroadcastReceiver broadcastReceiver = new MyBroadcastReceiver();
    private Handler mHandler;
    public static boolean isCalibration = false;
    public boolean isActAndWindowMode = false; //act + window 模式

    private boolean isFirstEnter = true;
    private AvmManager mAvmManager;
    public static final int ID_POWER_EXIT_BOOT_ANIMATION_REQ = 0x0C01;
    public static final int POWER_EXIT_BOOT_ANIMATION_REQ_ID = ID_POWER_EXIT_BOOT_ANIMATION_REQ |
            VehicleVendorProperty.VehiclePropertyGroup.VENDOR |
            VehicleVendorProperty.VehiclePropertyType.INT32 |
            VehicleVendorProperty.VehicleArea.GLOBAL;

    private static final String TAG = "checkFileTask";
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isFirstTimeOut = false;//第一个任务是否已超时
    private boolean isSecondTimeOut = false;//第二个任务是否已超时
    public static int mCarPowerWorkModeStatus = CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_FULL.getVal();//默认是全功能

    public AvmService() { }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        //启动加载读写文件任务
        startTask();
    }

    @SuppressLint("HandlerLeak")
    private void initAvm() {
        isExitAction = false;
        KLog.d("AVM服务 [onCreate]");
        AvmApp.getInstance().createCameraView();
        Log.d("AVM", "Board : " + Build.BOARD);
        initDefault();
        AvmRuntime.self().init(this);
        AvmRuntime.self().registerBroadcast(this);
        AvmRuntime.self().registerActionListener(this);

        mAvmManager = AvmManager.getInstance(this);
        CanManager.getInstance().init(this);
        CanManager.getInstance().registerSignalListener(mOnSignalValueChangedListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(exit_action);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(open_act);
        filter.addAction(close_act);
        filter.addAction(first_open_act);
        registerReceiver(broadcastReceiver, filter);

        CarPowerManager mCarPowerManager = CarPowerManager.getInstance(this, new CarPowerEventListener() {
            @Override
            public void onCarPowerWorkModeChangeEvent(CarPowerWorkModeStatus carPowerWorkModeStatus) {

                mCarPowerWorkModeStatus = carPowerWorkModeStatus.getCarPowerWorkModeStatusEnum().getVal();
                KLog.v("[mCarPowerManager]  mCarPowerWorkModeStatus:"+mCarPowerWorkModeStatus);
                if (mCarPowerWorkModeStatus == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_DEEP_SLEEP.getVal()) {
                    //进⼊STR
                    KLog.v("[mCarPowerManager]  进⼊STR");
                    mHandler.removeMessages(MSG_CR_CAMERA);
                    mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
                } else if (mCarPowerWorkModeStatus == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal()) {
                    KLog.v("[mCarPowerManager]  半功能  释放资源,释放摄像头");
                    DataManager.writeFault(DataConstant.Code.GET_IN_STR);
                    mHandler.removeMessages(MSG_CR_CAMERA);
                    mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
                    //半功能需要退出全景
                    AvmRuntime.self().artificialExit();
                } else if (mCarPowerWorkModeStatus == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_FULL.getVal()) {
                    //全功能，退出STR 恢复录⾳，恢复录摄像头
                    KLog.v("[mCarPowerManager]  全功能，退出STR 恢复录⾳，恢复录摄像头");
                    DataManager.writeFault(DataConstant.Code.GET_OUT_STR);
                    if (AvmRuntime.self().getFullSceneSts() != DataDefine.FV_STATE_NON) {
                        mHandler.removeMessages(MSG_DEL_CAMERA);
                        mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                    }
                }
            }

            @Override
            public void onCarPowerSignalChangeEvent(CarPowerSignalStatus carPowerSignalStatus) {

            }

            @Override
            public void onCarPowerServiceConnected() {
                //power服务连接成功，调⽤power接⼝要保证在服务连接成功后调⽤

            }

            @Override
            public void onCarPowerServiceDisconnected() {
                //power服务断开连接
            }

            @Override
            public void onCarPowerEvent(int i, int i1, CarPowerData carPowerData) {

            }
        });
        mCarPowerManager.connect();

        mHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                // 处理收到的消息
                // 处理收到的消息
                KLog.i("Msg.what ............ " + msg.what + " ... msg.arg1 ......." + msg.arg1);
                Intent intent = null;
                if(AvmApp.getInstance().getCameraView() == null){
                    KLog.d("AvmApp", "初始化还未获取到配置码 avm is null ");
                    return;
                }
                if (msg.what == MSG_ACTION_ENTER) {
                    switch (msg.arg1) {
                        case DataDefine.ACT_AERIAL_VIEW:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
                            break;
                        case DataDefine.ACT_2D_REAR_VIEW:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_UNDISTORT);
                            AvmApp.getInstance().getCameraView().getViewModel().setUndistortLevel();
                            break;
                        case DataDefine.ACT_2D_FRONT_OUTLINE:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_RIGHT_FRONT);
                            AvmApp.getInstance().getCameraView().getViewModel().setUndistortLevel();
                            break;
                        case DataDefine.ACT_2D_FRONT_VIEW:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_UNDISTORT);
                            AvmApp.getInstance().getCameraView().getViewModel().setUndistortLevel();
                            break;
                        case DataDefine.ACT_2D_REAR_OUTLINE:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_MANUAL_FRONT);
                            break;
                        case DataDefine.ACT_3D_FRONT_VIEW:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_REAR_3D); //确定FRONT 对应REAR
                            break;
                        case DataDefine.ACT_3D_REAR_VIEW:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_FRONT_3D);
                            break;
                        case DataDefine.ACT_3D_RIGHT_REAR:
                            AvmApp.getInstance().getCameraView().getViewModel().reset3D();
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_RIGHT_REAR_3D);
                            break;
                        case DataDefine.ACT_3D_LEFT_REAR:
                            AvmApp.getInstance().getCameraView().getViewModel().reset3D();
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_REAR_3D);
                            break;
                        case DataDefine.ACT_WIDE_ANGLE_REAR:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_120);
                            break;
                        case DataDefine.ACT_WIDE_ANGLE_FRONT:
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
                            break;
                        case DataDefine.ACT_EXIT:
                            KLog.i("avmService____ ACT_EXIT_CARD........0");
                            //CameraShowTypeHelper.getInstance().exitActivity();
                            AvmApp.getInstance().getCameraView().dismissView(null);
                            SystemProperties.setGlobal("avm_state", 0);
                            mAvmManager.sendAvmState(0);
                            BvAvmJNIHelper.getInstance().bwClearCarBottomImage();
                            if (DELETE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_CR_CAMERA);
                                mHandler.sendEmptyMessageDelayed(MSG_DEL_CAMERA, 500);
                            }
                            break;
                        case DataDefine.ACT_LEFT_CARD:
                            if(mCarPowerWorkModeStatus == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal()){
                                KLog.v("ACT_LEFT_CARD 半功能不启动全景");
                                return;
                            }
                            KLog.i("avmService____ ACT_LEFT_CARD........+ isAvmDeInit " + BvAvmJNIHelper.isAvmDeInit);
                            if (DELETE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_DEL_CAMERA);
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            }
                            if(AvmRuntime.self().isTurnActiveSts()) {
                                DataManager.writeFault(DataConstant.Code.ACTIVI_LIGHT);
                            }
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_BIRD_3D);
                            AvmApp.getInstance().getCameraView().showSmartWin();
                            SystemProperties.setGlobal("avm_state", 1);
                            mAvmManager.sendAvmState(1);
//                            if (isActAndWindowMode) {
//                                if (!AvmRuntime.self().isRearGearSts()) {
//                                    intent = new Intent(AvmService.this, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//                            }
                            break;
                        case DataDefine.ACT_PASSIVE_DUAL_CARD:
                            if(mCarPowerWorkModeStatus == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal()){
                                KLog.v("ACT_PASSIVE_DUAL_CARD 半功能不启动全景");
                                return;
                            }
                            KLog.i("avmService____ ACT_PASSIVE_DUAL_CARD........+ isAvmDeInit " + BvAvmJNIHelper.isAvmDeInit);
                            if (DELETE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_DEL_CAMERA);
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            }
                            if(AvmRuntime.self().isRearGearSts()){
                                DataManager.writeFault(DataConstant.Code.ACTIVI_RGEAR);
                            }
                            AvmRuntime.self().setRadarPauseFlag(false);
                            AvmApp.getInstance().getCameraView().showFullWin();
                            SystemProperties.setGlobal("avm_state", 1);
                            mAvmManager.sendAvmState(1);
                            if (isActAndWindowMode) {
                                if (!AvmRuntime.self().isRearGearSts()) {
                                    intent = new Intent(AvmService.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                            if(mRvcState == 0x0){
                                //rvc 开启状态
                                mRvcState = 0x2;
                                CanManager.getInstance().setIntProperty(POWER_EXIT_BOOT_ANIMATION_REQ_ID, VehicleHelper.VEHICLE_AREA_TYPE_GLOBAL, 1);
                                mHandler.sendEmptyMessageDelayed(MSG_CLOSE_RVC,2000);
                            }
                            break;
                        case DataDefine.ACT_ACTIVE_DUAL_CARD:
                            if(mCarPowerWorkModeStatus == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal()){
                                KLog.v("ACT_ACTIVE_DUAL_CARD 半功能不启动全景");
                                return;
                            }
                            KLog.i("avmService____ ACT_ACTVE_DUAL_CARD........+ isAvmDeInit " + BvAvmJNIHelper.isAvmDeInit);
                            AvmRuntime.self().setRadarPauseFlag(false);
                            AvmApp.getInstance().getCameraView().showFullWin();
                            SystemProperties.setGlobal("avm_state", 1);
                            mAvmManager.sendAvmState(1);
                            if (DELETE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_DEL_CAMERA);
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            }
                            if (isActAndWindowMode) {
                                if (!AvmRuntime.self().isRearGearSts()) {
                                    intent = new Intent(AvmService.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                            break;
                        case DataDefine.ACT_KEEP:
                            break;
                        case DataDefine.ACT_2D_LR:
                            KLog.i("ACT_2D_LR................  ");
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_RIGHT_FRONT);
                            break;
                        case DataDefine.EVT_CLICK_LEFT_CARD:
                            KLog.i("EVT_CLICK_LEFT_CARD................  ");
                            CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
                            break;
                        case DataDefine.ACT_PREV_VIEW_ANGLE:
                            if (CameraGLSurfaceView.lastCeameraDirection != bvavmJNI.BW_VIEW_POWER_OFF && CameraGLSurfaceView.lastCeameraDirection != bvavmJNI.BW_BIRD_3D) {
                                CameraGLSurfaceView.setAngleOfView(CameraGLSurfaceView.lastCeameraDirection);
                            } else {
                                if (AvmRuntime.self().getMemoryType() == DataDefine.MEM_MODE_2D) {
                                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_UNDISTORT);
                                    bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                                } else if (AvmRuntime.self().getMemoryType() == DataDefine.MEM_MODE_3D) {
                                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_FRONT_3D);
                                } else if (AvmRuntime.self().getMemoryType() == DataDefine.MEM_MODE_WIDE_ANGLE) {
                                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_120);
                                }
                            }
                            break;
                    }
                } else if (msg.what == MSG_CR_CAMERA) {
                    BvAvmJNIHelper.getInstance().bwCreateCamera("com/autochips/avm/ui/view/CameraView", "onBVAVMMessage");
//                    if (JNI_IN_THREAD_FLAG) {
//                        AvmApp.getInstance().getCameraView().getViewModel().createCamera();
//                    } else {
//                        if (!BvAvmJNIHelper.isAvmDeInit) {
//                            BvAvmJNIHelper.getInstance().bwCreateCamera("com/autochips/avm/ui/view/CameraView", "onBVAVMMessage");
//                        }
//                    }
                } else if (msg.what == MSG_DEL_CAMERA) {
                    AvmApp.getInstance().getCameraView().getViewModel().turnResetChange();
                    BvAvmJNIHelper.getInstance().bwDeleteCamera();
//                    if (JNI_IN_THREAD_FLAG) {
//                        AvmApp.getInstance().getCameraView().getViewModel().deleteCamera();
//                    } else {
//                        BvAvmJNIHelper.getInstance().bwDeleteCamera();
//                    }
                } else if(msg.what == MSG_CLOSE_RVC) {
                    KLog.i("avmService close rvc ");
                    CanManager.getInstance().setIntProperty(AVM_SELECT_STATE,0, 0x2);
                }
            }
        };

        //快速启动
        if(AvmApp.getInstance().getCameraView()!=null)
            AvmApp.getInstance().getCameraView().updateWind(0.0f, 2);
        mHandler.postDelayed(() -> {
            if(AvmApp.getInstance().getCameraView()!=null)
                AvmApp.getInstance().getCameraView().dismissView("初始化关闭......");
            CanManager.getInstance().startConnect((v -> {
                KLog.d("注册完成----fishTh ");
                CameraViewModelHelper.getInstance().initActive();
                //发送avm初始化状态
                CanManager.getInstance().setIntProperty(AVM_SELECT_STATE,0,0x0);
            }));
        }, 0);
        mHandler.sendEmptyMessageDelayed(MSG_DEL_CAMERA, 15 * 1000);
    }

    private void startTask() {
        // 第一个子线程任务
        Future<?> firstTaskFuture = executorService.submit(() -> {
            try {
                int i = bvavmJNI.bwSetParamsXML(BvAvmJNIHelper.CAMERA_TYPE, 0);
                KLog.i(TAG+"第一个任务完成:"+i);
                if(!isFirstTimeOut) {
                    mainHandler.post(this::initAvm);
                }
                if(i == -1){
                    KLog.i(TAG+"第一个任务读取不到文件，执行第二个文件查询");
                    bvavmJNI.bwSetParamsXML(BvAvmJNIHelper.CAMERA_TYPE,1);
                }
            } catch (Exception e) {
                KLog.e(TAG+"第一个任务被中断"+e);
            }
        });

        // 设置第一个任务的超时时间为500毫秒
        try {
            firstTaskFuture.get(300, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            KLog.i(TAG+"第一个任务超时，开始执行第二个任务");
            isFirstTimeOut = true;
            firstTaskFuture.cancel(true); // 取消第一个任务

            // 第二个子线程任务
            Future<?> secondTaskFuture = executorService.submit(() -> {
                try {
                    bvavmJNI.bwSetParamsXML(BvAvmJNIHelper.CAMERA_TYPE,1);
                    KLog.i(TAG+"第二个任务完成");
                    if(!isSecondTimeOut) {
                        mainHandler.post(this::initAvm);
                    }
                } catch (Exception ex) {
                    KLog.e(TAG+"第二个任务被中断");
                }
            });

            // 设置第二个任务的超时时间为500毫秒
            try {
                secondTaskFuture.get(200, TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                KLog.e(TAG+"第二个任务超时，转为主线程执行操作");
                isSecondTimeOut = true;
                secondTaskFuture.cancel(true); // 取消第二个任务
                mainHandler.post(this::initAvm);
            } catch (Exception ex) {
                KLog.e(TAG+"第二个任务异常"+ ex);
            }
        } catch (Exception e) {
            KLog.e(TAG+"第一个任务异常"+e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d("[onBind]");
        return new AvmServiceIBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d("[onUnbind]");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d(flags + "[onStartCommand]" + startId + ", version is " + ServiceUtils.getVersionName());
        //adb指令模拟启动service带参数调试功能
        if(AvmApp.getInstance().getCameraView() == null){
            KLog.d("AvmApp", "avm is null ");
            return START_STICKY;
        }
        //adb shell am start-service -n com.autochips.avm/.service.AvmService --ei avm_onclick 1
        if (intent != null) {
            int avm_onclick = intent.getIntExtra("avm_start", -1);
            int avm_state = SystemProperties.getGlobalInt("avm_state", -1);
            KLog.d("[onStartCommand] avm_start = " + avm_onclick + " , avm_state is " + avm_state);
            if (avm_onclick != -1) {//-1表示是通过AS启动的
                if (avm_state == 0) {
                    if(mCarPowerWorkModeStatus == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal()){
                        KLog.v("[onStartCommand] 半功能不启动全景");
                        return START_STICKY;
                    }
                    AvmRuntime.self().artificialEnter();
                    if (isFirstEnter) { //avm首次被占用摄像头被释放，onCreate bwDele;后 点击进行创建
                        mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                        isFirstEnter = false;
                    }
                    DataManager.writeFault(avm_onclick == 1 ? DataConstant.Code.CLICK_IN_SUI :
                            avm_onclick == 2 ? DataConstant.Code.CLICK_IN_FK : DataConstant.Code.CLICK_IN_SPEECH);
                } else if (avm_state == 1) {
                    AvmRuntime.self().artificialExit();
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (!isExitAction) {
            BvAvmJNIHelper.getInstance().avmDeInit();
        }
        executorService.shutdown();
        AvmApp.getInstance().getCameraView().removeView();
        ThreadPoolUtil.getInstance().removeAllHandlerAndShutdownThreadPool();
        CanManager.getInstance().unRegisterSignalListener(mOnSignalValueChangedListener);
//        if (!isExitAction)
//          ServiceUtils.startCaptureService(this,AvmService.class);
        super.onDestroy();
        KLog.d("[onDestroy]");
        unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onEnter(int act) {
        KLog.d("onEnter -> action : " + DataDefine.id2String(act));
        Message message = Message.obtain();
        message.what = MSG_ACTION_ENTER;
        message.arg1 = act;
        mHandler.sendMessage(message);
    }

    @Override
    public void onExit(int act) {
        KLog.d("onExit -> action : " + DataDefine.id2String(act));
    }

    @Override
    public void onGearNoAct(int gear, boolean handleFlag ) {
        KLog.i("onGearNoAct ...  " + gear);
        //处理档位变更逻辑
        switch (gear) {
            case DataDefine.GEAR_P: //驻车档
                break;
            case DataDefine.GEAR_N: //空挡
                break;
            case DataDefine.GEAR_D: //执行档
                break;
            case DataDefine.GEAR_R: //倒档
                break;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!handleFlag) AvmApp.getInstance().getCameraView().viewShowStatus();
                AvmApp.getInstance().getCameraView().setCurrentGear(gear);
            }
        });
    }

    // 设置默认配置
    private void initDefault() {
        int avmInstalled = SystemProperties.getInt("avm_installed", 0);
        KLog.d("avmInstalled = " + avmInstalled);
        if (avmInstalled == 0) {
            SystemProperties.set("avm_installed", "1");
            SystemProperties.set("settingPathLine", "1");
            SystemProperties.set("signalActivates","1");
        }
    }

    int turnLampSwSts = -1;
    long leftTurnLChangeTime = 0;
    long rightTurnLChangeTime = 0;
    private final CanManager.onSignalValueChangedListener mOnSignalValueChangedListener = (vehicleId, value) -> {
        if(AvmApp.getInstance().getCameraView() == null){
            KLog.d("AvmApp", "avm is null  vehicleId & value："+vehicleId +" :"+value);
            return;
        }
        if (vehicleId == AVM_UINM_TURN_LIGHT_SW_ST) { //转向激活
            KLog.d(" 转向 vehicleId = " + vehicleId + "  ,value = " + value);
            if (value instanceof Integer) {
                int intValue = (int) value;
                turnLampSwSts = intValue;
                //                turnLampChangeTime = System.currentTimeMillis();
                if (intValue == 0) {
                    AvmApp.getInstance().getCameraView().getViewModel().turnLampChange(intValue, 800);
                } else {
                    AvmApp.getInstance().getCameraView().getViewModel().turnLampChange(intValue, 0);
                }
            }
        } else if (vehicleId == CLUSTER_LEFT_TURN_LAMP) {//左边转向灯闪s
            leftTurnLChangeTime = System.currentTimeMillis();
            KLog.d(" 转向 左边转向灯闪 , value = " + value + " , (leftTurnLChangeTime-rightTurnLChangeTime) = " + (leftTurnLChangeTime-rightTurnLChangeTime));
            long delay = 800;
            if (turnLampSwSts == 0) {
                if ((leftTurnLChangeTime-rightTurnLChangeTime) < 500) { //双闪
                    delay = 0;
                }
                AvmApp.getInstance().getCameraView().getViewModel().turnLampChange(0, delay);
            }
            AvmRuntime.self().updateChangeTime();
        } else if (vehicleId == CLUSTER_RIGHT_TURN_LAMP) {//右边转向灯闪
            rightTurnLChangeTime = System.currentTimeMillis();
            KLog.d(" 右边转向灯闪 , value = " + value + " , (rightTurnLChangeTime-leftTurnLChangeTime) " + (rightTurnLChangeTime-leftTurnLChangeTime));
            long delay = 800;
            if (turnLampSwSts == 0) {
                if ((rightTurnLChangeTime-leftTurnLChangeTime) < 500) { //双闪
                    delay = 0;
                }
                AvmApp.getInstance().getCameraView().getViewModel().turnLampChange(0, delay);
            }
            AvmRuntime.self().updateChangeTime();
        } else if (vehicleId == VEHICLE_SPEED) {// 车速
            //KLog.d(" 车速 vehicleId = " + vehicleId + "  ,value = " + value);
            if (value instanceof Float) {
                AvmRuntime.self().speedChange((Float) value);
            }
        } else if (vehicleId == SETTINGS_VCU_BRKPEDPST) {//刹车踏板

        } else if (vehicleId == AVM_SAS_STEERING_ANGLE) {//转角值
//            KLog.d(" wheel angle , value = " + value);
            if (value instanceof Float) {
                float angle = (float) value;
                //错误转角不下发
                if(angle > 5000 || angle < -5000){
                    angle = 0.0f;
                }
                if (angle > 540) {
                    angle = 540.0f;
                } else if (angle < -540) {
                    angle = -540.0f;
                }
                //angle = (float) (((angle + 540.0) / 1080.0) * 72.0 - 36.0);
//                bvavmJNI.bwSetWheelAngle(angle * -1);
                if (AvmRuntime.self().getFullSceneSts() != DataDefine.FV_STATE_NON) {
                    AvmApp.getInstance().getCameraView().getViewModel().setWheelAngle(angle * -1);
                }
                //if (AvmRuntime.self().getFullSceneSts() != DataDefine.FV_STATE_NON) BvAvmJNIHelper.getInstance().bwSetWheelAngle(angle * -1);
            }
        } else if (vehicleId == MIRROR_FOLD_UNFOLD_STATUS) { // 后视镜折叠
            KLog.d(" 后视镜折叠 ....  ..... " + value);
        } else if (vehicleId == SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE) { // 后视下翻
            CameraViewModelHelper.getInstance().mirrorAutomaticStatus();
        } else if (vehicleId == CLUSTER_VCU_GEAR_LVL_DISP) {// 挡位
            KLog.d(" 挡位 vehicleId = " + vehicleId + "  ,value = " + value);
            if (value instanceof Integer) {
                int gear = (int) value;// 第一次开机后的默认值
                if (gear == 0) return;
                AvmRuntime.self().gearChange(gear);
                AvmApp.getInstance().getCameraView().getViewModel().updateTrajLineStatus(gear);
                //BvAvmJNIHelper.getInstance().updateTrajLineStatus(gear);
                if (gear == 4) {
                    AvmApp.getInstance().getCameraView().getViewModel().bwClearCarBottomImage();
                    //BvAvmJNIHelper.getInstance().bwClearCarBottomImage();
                }
            }
        }

        setDoorStatus(vehicleId, value);
        setFlWheelStatus(vehicleId, value);
        setLight(vehicleId, value);
        setRadar(vehicleId, value);
        setCalibration(vehicleId, value);
    };

    /**
     * 车门状态
     *
     * @param vehicleId
     * @param value
     */
    private void setDoorStatus(int vehicleId, Object value) {
        switch (vehicleId) {
            case CLUSTER_LCK_DRIVERDOORAJARST://车门状态
            case CABIN_DOOR_OPEN_STATUS://引擎(前舱)盖
            case CLUSTER_LCK_PassengerDoorAjarSt://车门状态
            case MIRROR_FOLD_UNFOLD_STATUS:  //后视镜状态  //取消车模的折叠 2024 08 27

                if (JNI_IN_THREAD_FLAG) {
                    AvmApp.getInstance().getCameraView().getViewModel().setDoorStatus(vehicleId, (Integer) value);
                } else {
                    CameraViewModelHelper.getInstance().doorStatus(vehicleId, (Integer) value);
                }

                break;
        }
    }

    private void setFlWheelStatus(int vehicleId, Object object) {
        switch (vehicleId) {
            case AVM_BCS_RLWHEEL_ROTATED_DIR://右前轮前进方向
            case AVM_BCS_FLWHEEL_ROTATED_DIR://左前轮前进方向
//                CameraViewModelHelper.getInstance().flWheelDir();

                if (JNI_IN_THREAD_FLAG) {
                    if (AvmRuntime.self().getFullSceneSts() != DataDefine.FV_STATE_NON) AvmApp.getInstance().getCameraView().getViewModel().setWheelSpeed(object);
                } else {
                    CameraViewModelHelper.getInstance().flWheelSpd(object);
                }

                break;
            case AVM_BCS_FLWHEEL_SPD://左后轮轮速
            case AVM_BCS_RLWHEEL_SPD://右后轮轮速
            case AVM_BCS_RRWHEEL_SPD:
            case AVM_BCS_FRWHEEL_SPD:
            case AVM_WHEEL_DIRE_SPEED:
                if (JNI_IN_THREAD_FLAG) {
                    if (AvmRuntime.self().getFullSceneSts() != DataDefine.FV_STATE_NON) AvmApp.getInstance().getCameraView().getViewModel().setWheelSpeed(object);
                } else {
                    CameraViewModelHelper.getInstance().flWheelSpd(object);
                }
                break;

        }
    }

    private void setLight(int vehicleId, Object status) {
        switch (vehicleId) {
            case BCM_HIGH_BEAM_STATUS://远光灯
            case BCM_LOW_BEAM_STATUS://近光灯
            case AVM_EL_SIDE_BRKLIGHT_CTRL_CMD://刹车灯
            case CLUSTER_FRONT_FOG_LAMP://前车灯
            case AVM_EL_REVERSE_LIGHT_ST://倒车车灯---尾灯
            case POWER_PARKING_LAMP://位置灯等于示宽灯
            case CLUSTER_RIGHT_TURN_LAMP://右转向灯
            case CLUSTER_LEFT_TURN_LAMP://左转向灯
            case CLUSTER_REAR_FOG_LAMP://后雾灯

                if (JNI_IN_THREAD_FLAG) {
                    AvmApp.getInstance().getCameraView().getViewModel().setLightModel(vehicleId, (Integer) status);
                } else {
                    CameraViewModelHelper.getInstance().showLight3DModel(vehicleId, (Integer) status);
                }

                break;
        }

    }

    private boolean isRadarFront60 = false;
    private boolean isRadarFront110 = false;

    private boolean isCallRRadarSound = false;//后雷达音是否在播报
    private boolean isCallFRadarSound = false;//前雷达音是否在播报
    private void setRadar(int vehicleId, Object object) {

        if ((vehicleId == CLUSTER_PAS_Distance || vehicleId == CLUSTER_PAS_FRONT_DISTANCE)  && object instanceof Integer[]) {
            //后雷达信号
            Integer[] arr = (Integer[]) object;// [0x00 ]
            if (arr.length == 0) {
                KLog.e("Radar param length is 0.");
                return;
            }
            KLog.i(arr.length + "  length 雷达检测距离CLUSTER_PAS_Distance ： " + vehicleId + "  value   " + Arrays.toString(arr));
            //后雷达
            int mil = arr[1];
            int right = arr[2];
            int left = arr[3];
            AvmApp.getInstance().getCameraView().setRadar(CLUSTER_PAS_RLDistance, left);
            AvmApp.getInstance().getCameraView().setRadar(CLUSTER_PAS_RLMidDistance, mil);
            AvmApp.getInstance().getCameraView().setRadar(CLUSTER_PAS_RRDistance, right);
            return;
        }

        if (!(object instanceof Integer)) {
            return;
        }
        int status = (int) object;

        switch (vehicleId) {

            case CLUSTER_PAS_FSLSideDistance://前左侧）
            case CLUSTER_PAS_FSRSideDistance: //（前右侧）
            case CLUSTER_PAS_PAS_FRDistance://（前右） 60
            case CLUSTER_PAS_PAS_FLDistance://（前左）
            case CLUSTER_PAS_PAS_RSLSideDistance:// 后侧左
            case CLUSTER_PAS_PAS_RSRSideDistance:// 后侧右
                KLog.d("信号监听 vehicleId = " + vehicleId + "  ,value = " + status);
                if (status < 1) return;
                if (status <= 60 && (AvmRuntime.self().isDriveGearSts() || AvmRuntime.self().isNullGearSts())) {
                    isRadarFront60 = true;

                    if (JNI_IN_THREAD_FLAG) {
                        AvmApp.getInstance().getCameraView().getViewModel().setRadarActive(status);
                    } else {
                        CameraViewModelHelper.getInstance().radarActive(status);
                    }

                } else if (isRadarFront60) {
                    isRadarFront60 = false;
                    if (!isRadarFront110) {
                        if (JNI_IN_THREAD_FLAG) {
                            AvmApp.getInstance().getCameraView().getViewModel().setRadarExit(status);
                        } else {
                            CameraViewModelHelper.getInstance().radarExit(status);
                        }
                    }
                }
                break;

            case CLUSTER_PAS_FRMidDistance://（前右中）
            case CLUSTER_PAS_FLMidDistance://（前左中）
                if (status < 1) return;

                if (status <= 110 && (AvmRuntime.self().isDriveGearSts() || AvmRuntime.self().isNullGearSts())) {
                    isRadarFront110 = true;

                    if (JNI_IN_THREAD_FLAG) {
                        AvmApp.getInstance().getCameraView().getViewModel().setRadarActive(status);
                    } else {
                        CameraViewModelHelper.getInstance().radarActive(status);
                    }

                } else {
                    isRadarFront110 = false;
                    if (!isRadarFront60) {
                        if (JNI_IN_THREAD_FLAG) {
                            AvmApp.getInstance().getCameraView().getViewModel().setRadarExit(status);
                        } else {
                            CameraViewModelHelper.getInstance().radarExit(status);
                        }
                    }
                }
                break;
            case CLUSTER_PAS_RLMidDistance:// 后左中
            case CLUSTER_PAS_RRMidDistance:// 后中
            case CLUSTER_PAS_RRDistance:// 后右
            case CLUSTER_PAS_RLDistance://后左
                AvmApp.getInstance().getCameraView().setRadar(vehicleId, status);
                break;
            case ASSIST_DRIVE_PAS_BUTTON_PRESS:// 雷达报警声
            case CLUSTER_CHIME_PAS_WARNTONE://雷达报警音状态
                //AvmApp.getInstance().getCameraView().showRadarSoundView(status);
                break;
            case AVM_RADAR_ALARM_ACOUSTIC_SWITCH:// 雷达故障报警
                //AvmApp.getInstance().getCameraView().showParkingAssistView(status);
                break;
            case AVM_RL_MIDSNS_ERR_FLAG:    //     后左中
                AvmApp.getInstance().getCameraView().setRadarFailStatus(2, status);
                break;
            case AVM_RR_MIDSNS_ERR_FLAG:   //     后右中
                AvmApp.getInstance().getCameraView().setRadarFailStatus(3, status);
                break;
            case AVM_RR_SNS_ERR_FLAG: //       后右
                AvmApp.getInstance().getCameraView().setRadarFailStatus(4, status);
                break;
            case AVM_RSL_SNS_ERR_FLAG:    //       后左
            case AVM_RSR_SNS_ERR_FLAG:    //       后左
            case AVM_FRS_SNS_ERR_FLAG:    //       后左
            case AVM_FLS_SNS_ERR_FLAG:    //       后左
            case AVM_RL_SNS_ERR_FLAG:    //       后左
                AvmApp.getInstance().getCameraView().setRadarFailStatus(1, status);
                break;
            case AVM_PAS_SYSTEMTYPE:    //       雷达系统故障，没有找到相关UI
                AvmApp.getInstance().getCameraView().setRadarFailStatus(1, status);
                AvmApp.getInstance().getCameraView().setRadarFailStatus(2, status);
                AvmApp.getInstance().getCameraView().setRadarFailStatus(3, status);
                AvmApp.getInstance().getCameraView().setRadarFailStatus(4, status);
                break;
        }

    }

    /**
     * 标定
     *
     * @param vehicleId
     * @param value
     */
    private boolean isInt3801 = false;
    private boolean isInt3801_2 = false;

    public void setCalibration(int vehicleId, Object value) {
        if (value instanceof byte[]) {
            byte[] arr = (byte[]) value;// [0x00 ]
            KLog.i(arr.length + "  length 标定-byte----vehicleId: " + vehicleId + "  value   " + Arrays.toString(arr) + "  版本号： " + ServiceUtils.getVersionName());

            if (vehicleId == DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_REQ) {
                KLog.i("步骤 0  标定-信息请求:DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_REQ:" + Arrays.toString(arr));
                CanManager.getInstance().setByteArray(DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP, 0, new byte[] {1});
                return;
            }

            byte[] arrBack = {0x00, 0x00, 0x00, 0x00};
            if (arr.length < 2) {
                KLog.e(vehicleId + " DIAG_31_ vehicleId 标定-进入下线标定请求 value 长度错误:" + Arrays.toString(arr));
                return;
            }
            switch (vehicleId) {
                case DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ://进入下线标定请求
                    KLog.i(" 步骤 1  标定-进入下线标定请求:DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ:" + Arrays.toString(arr) + "  版本号： " + ServiceUtils.getVersionName());
                    if (arr.length != 4) {
                        return;
                    }

                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_UNDISTORT);
                    AvmRuntime.self().artificialEnter();

                    getCalStatus(vehicleId, "DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ");
                    isInt3801 = true;
//                    CameraViewModelHelper.getInstance().showView(true);
                    byte[] finalArrBack = arrBack;
                    CanManager.getInstance().setByteArray(DIAG_31_3801_AVM_ENTER_CALIBRATION_RESP, 0, finalArrBack);
                    DataManager.writeFault(DataConstant.Code.BD_IN);
                    //                    mHandler.postDelayed(() -> {
//                                CameraViewModelHelper.getInstance().dismissView(false, 0, "DIAG_31 标定关闭");
//                            }
//                            , 2000);
                    break;
                case DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ://进入下线标定结果请求
                    KLog.i("步骤 2  标定-进入下线标定结果请求:DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ:" + Arrays.toString(arr));
                    if (arr.length != 4) {
                        return;
                    }
                    arrBack = new byte[]{0x00, 0x01, 0x00, 0x00};
                    getCalStatus(vehicleId, "DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ");
                    if (!isInt3801) {
                        KLog.i("标定-未进入:DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ");
                        arrBack = new byte[]{0x01, 0x01, 0x00, 0x00};
                    }
                    isInt3801_2 = true;

                    CanManager.getInstance().setByteArray(DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_IN_RESULE);
                    break;
                case DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ://标定预检查请求
                    KLog.i("步骤 3  标定-标定预检查请求:DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ:" + Arrays.toString(arr));
                    if (arr.length != 4) {
                        return;
                    }
                    if (getCalStatus(vehicleId, "DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ") != 3) {
                        KLog.i("标定-未进入:DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ");
//                        arrBack = new byte[]{0x01, 0x00, 0x00, 0x00};
                    }
                    if (!isInt3801) {
                        KLog.i("标定-步骤 3失败 未进入:步骤 1 ");
                        arrBack = new byte[]{0x01, 0x01, 0x00, 0x00};
                    }
                    CanManager.getInstance().setByteArray(DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_CHECK);
                    break;
                case DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ://标定预结果结果请求
                    KLog.i("步骤 4 标定-标定预结果结果请求:DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ:" + Arrays.toString(arr));
                    if (arr.length < 2) {
                        return;
                    }
                    arrBack = new byte[]{0x00, 0x00, 0x00, 0x00};
                    if (getCalStatus(vehicleId, "查询结果") != 4) {
                    }
                    CanManager.getInstance().setByteArray(DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_CHECK_RESULT);
                    break;
                case DIAG_31_3803_AVM_START_CALIBRATION_REQ://开始标定请求
                    KLog.i("步骤 5  标定-开始标定请求:DIAG_31_3803_AVM_START_CALIBRATION_REQ:" + Arrays.toString(arr));


                    //CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP, 0, arrBack);
//                    CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, new byte[] {0x00, 0x00, 0x00, 0x00});
//                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_success));

                    if (arr.length != 8) {
                        return;
                    }

                    if (AvmService.JNI_IN_THREAD_FLAG) {
                        // 向上位机回复已开始标定
                        CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESP, 0, new byte[] {0x00, 0x00, 0x00, 0x00});
                        AvmApp.getInstance().getCameraView().getViewModel().callCalibrate(1);
                    } else {
                        AvmApp.getInstance().getCameraView().startCalibration();
                    }
                    DataManager.writeFault(DataConstant.Code.BD_START);
                    break;
                case DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ://开始标定结果请求
                    KLog.i("步骤 6 标定-开始标定结果请求:DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ:" + Arrays.toString(arr));
                    //arrBack = new byte[]{0x00, 0x00, 0x00, 0x00};
                    //CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
                    if (arr.length != 4) {
                        return;
                    }
                    AvmApp.getInstance().getCameraView().calibrationBack();
                    DataManager.writeFault(DataConstant.Code.BD_START_RESULT);
                    break;
                case DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ://下线标定检查

                    KLog.i("步骤 7  标定-下线标定检查:DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ:" + Arrays.toString(arr));
                    if (arr.length < 2) {
                        return;
                    }

                    CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_IN_CHECK);
                    break;
                case DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_REQ://下线标定检查结果请求
                    KLog.i("步骤 8 标定-下线标定检查结果请求:DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_REQ:" + Arrays.toString(arr));
                    if (arr.length != 4) {
                        return;
                    }
                    arrBack = new byte[]{0x00, 0x02, 0x00, 0x00};

                    CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP, 0, arrBack);
                    //AvmApp.getInstance().getCameraView().calibrationBack();
                    DataManager.writeFault(DataConstant.Code.BD_IN_CHECK_RESULT);
                    break;
                case DIAG_31_380D_AVM_READ_FAIL_REASON_REQ://读取标定失败原因请求
                    KLog.i(" 步骤 9 标定-读取标定失败原因请求:DIAG_31_380D_AVM_READ_FAIL_REASON_REQ:" + Arrays.toString(arr));
                    arrBack = new byte[]{0x00, 0x00, 0x00, 0x00};

                    //arrBack = new byte[]{0x00};
                    if (arr.length < 2) {
                        return;
                    }

                    CanManager.getInstance().setByteArray(DIAG_31_380D_AVM_READ_FAIL_REASON_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_FALUT);
                    break;
                case DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ://读取标定失败原因结果请求
                    KLog.i("步骤 10 标定-读取标定失败原因结果请求:DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ:" + Arrays.toString(arr));
                    if (arr.length != 4) {
                        if (arr.length == 1 && arr[0] == -128) {
                            //
                        } else {
                            return;
                        }
                    }
                    AvmApp.getInstance().getCameraView().calibrationError();
                    DataManager.writeFault(DataConstant.Code.BD_FALUT_RESULT);
                    break;
            }
        }

    }

    private int calStatus = 0;

    public int getCalStatus(int vehicleId, String status) {
        calStatus += 1;
        isCalibration = true;
        KLog.d(calStatus + "  calStatus 标定状态status -: " + status + " ，vehicleId: " + vehicleId);
        mHandler.removeCallbacksAndMessages("calStatus");
        mHandler.postDelayed(() -> {
            if (calStatus != -1) {
                KLog.d("标定状态超时--重置状态-: " + calStatus);
                calStatus = 0;
                isCalibration = false;
            }
        }, 30000);
        return calStatus;
    }

    public static boolean isExitAction = false;// 工程模式退出

    /**
     * 终端测试 命令： adb shell am broadcast -a action.syncore.EOL.mode -o
     */
    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            KLog.i("接收工程模式的广播:" + action);
            if (action.equals(exit_action)) {
                KLog.i("接收工程模式的广播结束全景影像后需要重启车机:" + action);
                isExitAction = true;
                CameraViewModelHelper.getInstance().dismissView(false, 0, "0");
                mHandler.removeMessages(MSG_CR_CAMERA);
                mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
//                System.exit(0);
            } else if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
                KLog.i("语言切换 。。。。。。。。:" + action);
                System.exit(0);
            } else if(action.equals(open_act)){
                KLog.i("AvmApp","open avm ");
                AvmApp.getInstance().getCameraView().showFullWin();
                SystemProperties.setGlobal("avm_state", 1);
                mAvmManager.sendAvmState(1);
                if (DELETE_CAMERA_FLAG) {
                    mHandler.removeMessages(MSG_DEL_CAMERA);
                    mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                }
            }else if(action.equals(close_act)){
                KLog.i("AvmApp","close avm ");
                AvmApp.getInstance().getCameraView().dismissView(null);
                SystemProperties.setGlobal("avm_state", 0);
                mAvmManager.sendAvmState(0);
                if (DELETE_CAMERA_FLAG) {
                    mHandler.removeMessages(MSG_CR_CAMERA);
                    mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
                }
            }else if(action.equals(first_open_act)){
                KLog.i("首次打开avm");
                AvmRuntime.self().artificialEnter();
                if (isFirstEnter) { //avm首次被占用摄像头被释放，onCreate bwDele;后 点击进行创建
                    mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                    isFirstEnter = false;
                }
            }
        }
    }

    public class AvmServiceIBinder extends Binder {
    }

}