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
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_SELECT_STATE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_UINM_TURN_LIGHT_SW_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_WHEEL_DIRE_SPEED;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_HIGH_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_LOW_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CABIN_DOOR_OPEN_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_FRONT_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LEFT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_Distance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_REAR_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_RIGHT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_CHIME_PAS_WARNTONE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_DRIVERDOORAJARST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_PassengerDoorAjarSt;
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
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_VCU_GEAR_LVL_DISP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.MIRROR_FOLD_UNFOLD_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.NFS_SYNC_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.POWER_PARKING_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_VCU_BRKPEDPST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.VEHICLE_SPEED;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DESK_HAZARD_LAMP_STATUS;


import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D;
import static com.avm.framwork.manager.ViewSwitchManager.CAMERA_3_D_REAR;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.bvavm.bvavmJNI;
import com.android.wm.shell.splitscreen.ISplitScreenCallback;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.data.Monitor;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.info.AutoStatusRequBean;
import com.autochips.avm.ui.activity.MainActivity;
import com.autochips.avm.ui.view.CameraGLSurfaceView;
import com.autochips.avm.util.CustomToast;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.GlobalSetting;
import com.autochips.avm.util.ServiceUtils;
import com.autochips.avm.util.SystemProperties;
import com.avm.framwork.constant.CameraContracts;
import com.avm.framwork.helper.ThreadPoolUtil;
import com.avm.framwork.manager.CanManager;
import com.google.gson.Gson;
import com.gxa.car.splitscreenmanager.ServiceConnectCallback;
import com.gxa.car.splitscreenmanager.SplitScreenManager;
import com.iflytek.autofly.mapsdk.BlJsonProtocolManager;
import com.iflytek.autofly.mapsdk.IJsonProtocolReceive;
import com.iflytek.autofly.mapsdk.bean.navi.TbtBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Arrays;

import gxa.car.power.data.CarPowerData;
import gxa.car.power.data.CarPowerSignalStatus;
import gxa.car.power.data.CarPowerWorkModeStatus;
import gxa.car.power.listener.CarPowerEventListener;
import gxa.car.power.manager.CarPowerManager;
import me.goldze.mvvmhabit.utils.KLog;

public class AvmService extends Service implements AvmRuntime.ActionListener {
    // adb shell am broadcast -a action.syncore.EOL.mode

    public static int MSG_ACTION_ENTER = 1;
    public static int MSG_ACTION_EXIT = 2;
    public static int MSG_CR_CAMERA = 3;
    public static int MSG_DEL_CAMERA = 4;
    public static int MSG_CLOSE_RVC = 5;
    private final int MSG_TURN_LAMP_CHANGE = 22;
    private final int MSG_END_DOUBLE_BLINK = 23;
    private final int MSG_CALIBRATION_CHECK_RESP = 31;
    public static int mRvcState = 0x0;

    private final String BR_GEAR_STATUS = "com.avm.define.GEAR_STATUS";
    private final String BR_TURN_LAMP_STATUS = "com.avm.define.EVT_TURN_LAMP_STS";
    private final String BR_TEST = "com.avm.define.TEST";
    private final String MENU_KEY = "com.gxatek.cockpit.systemUi.ALL_MENU_CLICK";
    private String exit_action = "action.syncore.EOL.mode";
    private MyBroadcastReceiver broadcastReceiver = new MyBroadcastReceiver();
    public Monitor sMonitor;
    private Handler mHandler;
    public boolean IS_AY5 = false;
    private CarPowerManager mCarPowerManager;
    public static boolean mIsStartStatus = false;//记录地图是否开始导航
    public static boolean mMapSpeedStatus = false;//记录地图是否有弹出限速图标
    public static boolean mIsScreen = false; // 记录是否为分屏
    public static boolean isLeftScreen = false;//是否为左边的分屏显示全景
    private SplitScreenManager mSplitScreenManager;

    private final boolean EXIT_CLOSE_CAMERA_FLAG = false;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        isExitAction = false;
        KLog.d("AVM服务 [onCreate]");
        //sMonitor = new Monitor();
//        mHandler.postDelayed(()->  AvmApp.getInstance().getViewBottom().dismissView("初始化-底部窗口"),20);
        SystemProperties.setGlobal("avm_state", 0);
        initData();
        // 注册信号监听

        AvmRuntime.self().init(this);
        AvmRuntime.self().registerActionListener(this);

        CanManager.getInstance().init(this);
        CanManager.getInstance().registerSignalListener(mOnSignalValueChangedListener);
        mCarPowerManager = CarPowerManager.getInstance(this, new CarPowerEventListener() {
            @Override
            public void onCarPowerWorkModeChangeEvent(CarPowerWorkModeStatus carPowerWorkModeStatus) {
                KLog.i("onCarPowerWorkModeChangeEvent getCarPowerWorkModeStatusEnum:"+carPowerWorkModeStatus.getCarPowerWorkModeStatusEnum().getVal());
                if (carPowerWorkModeStatus.getCarPowerWorkModeStatusEnum().getVal() == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_DEEP_SLEEP.getVal()) {
                    //进⼊STR
                    KLog.i("[mCarPowerManager]  进⼊STR");
                    mHandler.removeMessages(MSG_CR_CAMERA);
                    mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
                } else if (carPowerWorkModeStatus.getCarPowerWorkModeStatusEnum().getVal() == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal()) {
                    KLog.i("[mCarPowerManager]  半功能  释放资源,释放摄像头");
                    mHandler.removeMessages(MSG_CR_CAMERA);
                    mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
                } else if (carPowerWorkModeStatus.getCarPowerWorkModeStatusEnum().getVal() == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_FULL.getVal()) {
                    //全功能，退出STR 恢复录⾳，恢复录摄像头
                    KLog.i("[mCarPowerManager]  全功能，退出STR 恢复录⾳，恢复录摄像头");
                    mHandler.removeMessages(MSG_DEL_CAMERA);
                    mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                }
            }

            @Override
            public void onCarPowerSignalChangeEvent(CarPowerSignalStatus carPowerSignalStatus) {

            }

            @Override
            public void onCarPowerServiceConnected() {
                //power服务连接成功，调⽤power接⼝要保证在服务连接成功后调⽤
                int currentCarPowerMode = mCarPowerManager.getCurrentWorkMode().getCarPowerWorkModeStatusEnum().getVal();
                KLog.i("[onCarPowerServiceConnected]  currentCarPowerMode:"+currentCarPowerMode);
                if (currentCarPowerMode == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_DEEP_SLEEP.getVal()) {
                    //进⼊STR
                    KLog.i("[onCarPowerServiceConnected]  进⼊STR");
                    AvmRuntime.self().artificialExit(true);
                    mHandler.removeMessages(MSG_CR_CAMERA);
                    mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
                } else if (currentCarPowerMode == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal()) {
                    KLog.i("[onCarPowerServiceConnected]  半功能  释放资源,释放摄像头");
                    DataManager.writeFault(DataConstant.Code.GET_IN_STR);
                    AvmRuntime.self().artificialExit(true);
                    mHandler.removeMessages(MSG_CR_CAMERA);
                    mHandler.sendEmptyMessage(MSG_DEL_CAMERA);
                } else if (currentCarPowerMode == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_FULL.getVal()) {
                    //全功能，退出STR 恢复录⾳，恢复录摄像头
                    KLog.i("[onCarPowerServiceConnected]  全功能，退出STR 恢复录⾳，恢复录摄像头");
                    DataManager.writeFault(DataConstant.Code.GET_OUT_STR);
                    mHandler.removeMessages(MSG_DEL_CAMERA);
                    mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                }
            }

            @Override
            public void onCarPowerServiceDisconnected() {
                //power服务断开连接
                KLog.i("[onCarPowerServiceDisconnected]  服务断开连接");
            }

            @Override
            public void onCarPowerEvent(int i, int i1, CarPowerData carPowerData) {

            }
        });
        mCarPowerManager.connect();
        IntentFilter filter = new IntentFilter();
        filter.addAction(exit_action);
        filter.addAction(BR_GEAR_STATUS);
        filter.addAction(BR_TEST);
        filter.addAction(BR_TURN_LAMP_STATUS);
        filter.addAction(MENU_KEY);
        registerReceiver(broadcastReceiver, filter);
        isExitAction = false;
        KLog.d("启动----service_123  "+fishTh);

//        set("rvc_exit_flag", "1");

//        File fifoFile = new File("/avm_config/rvc_bvavm_fifo");
//        RandomAccessFile fifo = null;
//        try {
//            fifo = new RandomAccessFile(fifoFile, "rw");
//            fifo.writeBytes("Hello, FIFO!\n"); // 写入数据到FIFO
//            fifo.close(); // 关闭FIFO以供其他进程读取
//            KLog.d("AVM_DEBUG", "1111111111111111111");
//        } catch (IOException e) {
//            e.printStackTrace();
//            KLog.d("AVM_DEBUG", e.toString());
//        }

        // 快速启动
        if(AvmApp.getInstance().getCameraView() !=null)
            AvmApp.getInstance().getCameraView().updateWind(0.0f,2);
//          mHandler.postDelayed(()->{
//              CanManager.getInstance().startConnect((v -> {
//                  if(AvmApp.getInstance().getCameraView() !=null)
//                  AvmApp.getInstance().getCameraView().dismissView("初始化关闭......");
//                  CameraViewModelHelper.getInstance().initActive();
//                  //BvAvmJNIHelper.getInstance().initCanset();
//              }));
//          },0);

        Gson gson = new Gson();
        BlJsonProtocolManager.getInstance().init(this, new IJsonProtocolReceive() {
            @Override
            public void received(String result, int aidlBindState) {
                KLog.d("BlJsonProtocolManager  result:"+result);
                // 1. 先解析 protocolId

                if (result == null) return;

                try {
                    JSONObject root = new JSONObject(result);
                    int protocolId = root.getInt("protocolId");
                    if(protocolId == 300200) {
                        AutoStatusRequBean autoStatusRequBean = gson.fromJson(result, AutoStatusRequBean.class);
                        if (autoStatusRequBean != null) {
                            KLog.d("BlJsonProtocolManager  result: autoStatusRequBean：" + autoStatusRequBean);
                        }
                        if (autoStatusRequBean != null && autoStatusRequBean.getData() != null) {
                            int autoStatus = autoStatusRequBean.getData().getAutoStatus();
                            KLog.i("BlJsonProtocolManager  autoStatusRequBean:" + autoStatus);
                            if (autoStatus == 16) {
                                mIsStartStatus = true;
                            } else if (autoStatus == 17) {
                                mIsStartStatus = false;
                            }
                            //开始导航
                            if (AvmApp.getInstance().getCameraView() != null && AvmApp.getInstance().getCameraView().isSmartWin) {
                                //小卡片显示中，需要小卡片移动
                                changeScreenDirection();
                            }
                        }
                    }else if(protocolId == 300407){
                        TbtBean tbtBean = gson.fromJson(result, TbtBean.class);
                        KLog.d("BlJsonProtocolManager  result: tbtBean：" + tbtBean);
                        if (tbtBean != null) {
                            int cameraType = tbtBean.getCameraType();
                            KLog.i("BlJsonProtocolManager  tbtBean:" + cameraType);
                            if (cameraType == 8) {
                                mMapSpeedStatus = true;
                            } else if (cameraType == 9) {
                                mMapSpeedStatus = false;
                            }
                            //弹出收回区间限速
                            if (AvmApp.getInstance().getCameraView() != null && AvmApp.getInstance().getCameraView().isSmartWin) {
                                //小卡片显示中，需要小卡片移动
                                changeScreenDirection();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void notifyState() {

            }
        });
        mSplitScreenManager = SplitScreenManager.getInstance();
        mSplitScreenManager.init(this,SplitScreenManager.AUTO_RECONNECTED);
        mSplitScreenManager.setServiceConnectCallback(new ServiceConnectCallback() {
            @Override
            public void onServiceConnected() {
                KLog.i("SplitScreenManager  onServiceConnected");
                registerSplitScreenCallback();
            }

            @Override
            public void onServiceDisconnected() {
                KLog.i("SplitScreenManager  onServiceDisconnected");
            }
        });

        mHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                // 处理收到的消息
                // 处理收到的消息
                KLog.i("AvmRuntime Msg.what ............ " + msg.what + " ... msg.arg1 ......." + DataDefine.id2String(msg.arg1));
                Intent intent = null;
                if(AvmApp.getInstance().getCameraView() == null){
                    KLog.d("AvmApp", "初始化还未获取到配置码 avm is null ");
                    return;
                }
                if (msg.what == MSG_ACTION_ENTER) {
                    switch (msg.arg1) {
                        case DataDefine.ACT_AERIAL_VIEW:
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_BIRD_3D);
                            break;
                        case DataDefine.ACT_2D_REAR_VIEW:
                            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_REAR_UNDISTORT);
                            break;
                        case DataDefine.ACT_2D_FRONT_OUTLINE:
                            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_LEFT_RIGHT_FRONT);
                            break;
                        case DataDefine.ACT_2D_FRONT_VIEW:
 //                           bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_FRONT_UNDISTORT);
                            AvmApp.getInstance().getCameraView().getViewModel().to2DUpView();
                            break;
                        case DataDefine.ACT_2D_REAR_OUTLINE:
                            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_MANUAL_FRONT);
                            break;
                        case DataDefine.ACT_3D_FRONT_VIEW:
                            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_REAR_3D); //确定FRONT 对应REAR
                            break;
                        case DataDefine.ACT_3D_REAR_VIEW:
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_FRONT_3D);
                            break;
                        case DataDefine.ACT_3D_RIGHT_REAR:
                            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_RIGHT_REAR_3D);
                            break;
                        case DataDefine.ACT_3D_LEFT_REAR:
                            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_LEFT_REAR_3D);
                            break;
                        case DataDefine.ACT_WIDE_ANGLE_REAR:
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_REAR_120);
                            break;
                        case DataDefine.ACT_WIDE_ANGLE_FRONT:
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_FRONT_120);
                            break;
//                        case DataDefine.ACT_2D_FRONT_UNDISTORT:
//                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_FRONT_UNDISTORT);
//                            AvmApp.getInstance().getCameraView().getViewModel().to2DUpView();
//                            break;
                        case DataDefine.ACT_3D_LEFT_FRONT:
                            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_LEFT_FRONT_3D);
                            break;
                        case DataDefine.ACT_3D_RIGHT_FRONT:
                            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_RIGHT_FRONT_3D);
                            break;
                        case DataDefine.ACT_EXIT:
                            KLog.i("avmService____ ACT_EXIT_CARD........0");
                            //CameraShowTypeHelper.getInstance().exitActivity();
                            SystemProperties.setGlobal("avm_displaymode", 0);
                            SystemProperties.setGlobal("avm_state", 0);
                            AvmApp.getInstance().getCameraView().dismissView(null);
                            if (EXIT_CLOSE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_CR_CAMERA);
                                mHandler.sendEmptyMessageDelayed(MSG_DEL_CAMERA, 500);
                            }
                            //mAvmManager.sendAvmState(0);
                            break;
                        case DataDefine.ACT_LEFT_CARD:
                            if (EXIT_CLOSE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_DEL_CAMERA);
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            } else {
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            }
                            if(AvmRuntime.self().isTurnActiveSts()) {
                                DataManager.writeFault(DataConstant.Code.ACTIVI_LIGHT);
                            }
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_BIRD_3D);
                            SystemProperties.setGlobal("avm_displaymode", 2);
                            SystemProperties.setGlobal("avm_state", 1);
                            AvmApp.getInstance().getCameraView().showSmartWin();
//                            if (isActAndWindowMode) {
//                                if (!AvmRuntime.self().isRearGearSts()) {
//                                    intent = new Intent(AvmService.this, MainActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//                            }
                            break;
                        case DataDefine.ACT_PASSIVE_DUAL_CARD:
                            if (EXIT_CLOSE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_DEL_CAMERA);
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            } else {
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            }
                            if(AvmRuntime.self().isRearGearSts()){
                                DataManager.writeFault(DataConstant.Code.ACTIVI_RGEAR);
                            }
                            AvmRuntime.self().setRadarPauseFlag(false);
                            SystemProperties.setGlobal("avm_displaymode", 1);
                            SystemProperties.setGlobal("avm_state", 1);
                            AvmApp.getInstance().getCameraView().showFullWin();
                            if (!AvmRuntime.self().isRearGearSts()) {
                                intent = new Intent(AvmService.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            break;
                        case DataDefine.ACT_ACTIVE_DUAL_CARD:
                            if (EXIT_CLOSE_CAMERA_FLAG) {
                                mHandler.removeMessages(MSG_DEL_CAMERA);
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            } else {
                                mHandler.sendEmptyMessage(MSG_CR_CAMERA);
                            }
                            AvmRuntime.self().setRadarPauseFlag(false);
                            SystemProperties.setGlobal("avm_displaymode", 1);
                            SystemProperties.setGlobal("avm_state", 1);
                            AvmApp.getInstance().getCameraView().showFullWin();
                            if (!AvmRuntime.self().isRearGearSts()) {
                                intent = new Intent(AvmService.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            break;
                        case DataDefine.ACT_KEEP:
                            break;
                        case DataDefine.ACT_2D_LR_FRONT:
                            KLog.i("ACT_2D_LR................  ");
                            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_LEFT_RIGHT_FRONT);
                            break;
                        case DataDefine.ACT_2D_LR_REAR:
                            bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_LEFT_RIGHT_BACK);
                            break;
                        case DataDefine.EVT_CLICK_LEFT_CARD:
                            KLog.i("EVT_CLICK_LEFT_CARD................  ");
                            CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_FRONT_120);
                            break;
                        case DataDefine.ACT_PREV_VIEW_ANGLE:
                            if (CameraGLSurfaceView.getLAngleOfView() != bvavmJNI.BW_VIEW_POWER_OFF && CameraGLSurfaceView.getLAngleOfView() != bvavmJNI.BW_BIRD_3D) {
                                CameraGLSurfaceView.setAngleOfView2(CameraGLSurfaceView.getLAngleOfView());
                            } else {
                                if (AvmRuntime.self().getMemoryType() == DataDefine.STS_MEM_MODE_2D) {
                                    CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_FRONT_UNDISTORT);
                                    bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                                } else if (AvmRuntime.self().getMemoryType() == DataDefine.STS_MEM_MODE_3D) {
                                    CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_FRONT_3D);
                                } else if (AvmRuntime.self().getMemoryType() == DataDefine.STS_MEM_MODE_WIDE_ANGLE) {
                                    CameraGLSurfaceView.setAngleOfView2(bvavmJNI.BW_2D_FRONT_120);
                                }
                            }
                            break;
                        case DataDefine.ACT_REFRESH_TAB_INDEX:
                            AvmApp.getInstance().getCameraView().updateTabViewIndex();
                            break;
                        case DataDefine.ACT_REFRESH_2D_TOP_VIEW:
                            AvmApp.getInstance().getCameraView().getViewModel().to2DUpView();
                            break;
                        case DataDefine.ACT_REFRESH_2D_LEFT_VIEW:
                            AvmApp.getInstance().getCameraView().getViewModel().to2DLeftView();
                            break;
                        case DataDefine.ACT_REFRESH_2D_RIGHT_VIEW:
                            AvmApp.getInstance().getCameraView().getViewModel().to2DRightView();
                            break;
                        case DataDefine.ACT_REFRESH_2D_BOTTOM_VIEW:
                            AvmApp.getInstance().getCameraView().getViewModel().to2DBottomView();
                            break;
//                        case DataDefine.ACT_REFRESH_LAND_TRANSPARENCY:
//                            CameraViewModelHelper.getInstance().setTransparentIndexTab2(msg.arg2);
//                            break;
                        case DataDefine.ACT_REFRESH_3D_LEFT_FRONT:
                            AvmApp.getInstance().getCameraView().getViewModel().to3DLeftFront();
                            break;
                        case DataDefine.ACT_REFRESH_3D_LEFT_REAR:
                            AvmApp.getInstance().getCameraView().getViewModel().to3DLeftRear();
                            break;
                        case DataDefine.ACT_REFRESH_3D_RIGHT_REAR:
                            AvmApp.getInstance().getCameraView().getViewModel().to3DRightRear();
                            break;
                        case DataDefine.ACT_REFRESH_3D_RIGHT_FRONT:
                            AvmApp.getInstance().getCameraView().getViewModel().to3DRightFront();
                            break;
                        case DataDefine.ACT_REFRESH_3D_FRONT:
                            AvmApp.getInstance().getCameraView().chick3DView(CAMERA_3_D);
                            break;
                        case DataDefine.ACT_REFRESH_3D_REAR:
                            AvmApp.getInstance().getCameraView().chick3DView(CAMERA_3_D_REAR);
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
                    BvAvmJNIHelper.getInstance().bwDeleteCamera();
//                    if (JNI_IN_THREAD_FLAG) {
//                        AvmApp.getInstance().getCameraView().getViewModel().deleteCamera();
//                    } else {
//                        BvAvmJNIHelper.getInstance().bwDeleteCamera();
//                    }
                } else if(msg.what == MSG_CLOSE_RVC) {
                    KLog.i("avmService close rvc ");
                    CanManager.getInstance().setIntProperty(AVM_SELECT_STATE,0, 0x2);
                } else if (msg.what == MSG_TURN_LAMP_CHANGE) {
                    AvmRuntime.self().turnLampChange(msg.arg1);
                } else if (msg.what == MSG_CALIBRATION_CHECK_RESP) {
                    AvmApp.getInstance().getCameraView().calibrationBack();
                    DataManager.writeFault(DataConstant.Code.BD_START_RESULT);
                }
            }
        };

        mHandler.postDelayed(()->{

            KLog.d("注册完成--55 --service_123 "+fishTh);
            fishTh = 1;
        },11*1000);

//        observer();
    }

    private void registerSplitScreenCallback() {
        mSplitScreenManager.registerSplitScreenCallback(new ISplitScreenCallback.Stub(){

            //分屏区域⼤⼩改变，stage： 0、表⽰地图所在的区域； 1、表⽰⾮地图所在的区域； 1
            //rect：区域的⼤⼩、位置
            @Override
            public void onStageBoundsChanged(int stage, Rect rect) {
                KLog.i("SplitScreenManager  onStageBoundsChanged stage:"+stage +" rect"+rect.width());
            }


            //分屏位置交换回调，stage：0、表⽰地图所在的区域； 1、表⽰⾮地图所在的区域；
            // position: 0、表⽰左侧区域； 1、表⽰右侧区域；
            @Override
            public void onStagePositionChanged(int stage, int position) {
                KLog.i("SplitScreenManager  onStagePositionChanged stage:"+stage +" position:"+position);
                if (mIsScreen) {
                    if (stage == 0) {
                        if (position == 0) {
                            //此时表示地图在左边
                            isLeftScreen = false;
                            changeScreenDirection();
                        } else if (position == 1) {
                            //此时表示地图在右侧区域
                            isLeftScreen = true;
                            changeScreenDirection();
                        }
                    }
                }
            }

            // 是否分屏状态监听：true、分屏状态；false、退出分屏状态
            @Override
            public void onSplitScreenStateChanged(boolean isScreenState) {
                KLog.i("SplitScreenManager  onSplitScreenStateChanged:"+isScreenState);
                mIsScreen = isScreenState;
                changeScreenDirection();
            }

            // 分屏区域栈状态改变回调： taskId、所在栈的id;
            // stage: 0、表⽰地图所在的区域； 1、表⽰⾮地图所在的区域;
            // visible: 是否可⻅
            @Override
            public void onTaskStageChanged(int taskId, int stage, boolean visible) {
                KLog.i("SplitScreenManager  onTaskStageChanged taskId:"+taskId+" stage:"+stage +" visible:"+visible);
            }

            /** 通知地图分屏按钮是否显⽰,返回结果如下：
            [{
             "packageName": "com.android.xxxx", //包名
             "showOrHide": 1 // 1 show; 2 hide
             }, {
             "packageName": "com.android.xxxx",
             "showOrHide": 1
             }]
            其中packageName为应⽤包名，showOrHide表⽰显⽰隐藏：1、为显⽰ 2、为隐藏*/
            @Override
            public void onAppViewShowOrHideChanged(String json) {
                KLog.i("SplitScreenManager  SplitScreenManager json:"+json);
            }


            /**
             INVALID_VALUE = -1;
             LAUNCHER_SCENE_CAR = 0; //⻋模场景
             LAUNCHER_SCENE_MAP = 1; //地图场景：地图、地图+SR分屏场景
             LAUNCHER_SCENE_WALLPAPER = 2; // 壁纸场景： Launcher
             SCENE_APP = 3; // app场景：全屏app、分屏app
             SCENE_SR = 4; // sr全屏场景
             */
            @Override
            public void onScenesChanged(int scene) {
                KLog.i("SplitScreenManager  onScenesChanged scene:"+scene +"mIsScreen:"+mIsScreen);
            }
        });
    }

    //更改显示位置
    private void changeScreenDirection(){
        KLog.i("changeScreenDirection  mIsStartStatus:"+mIsStartStatus +" isLeftScreen:"+isLeftScreen +" mIsScreen:"+mIsScreen);
        if (AvmApp.getInstance().getCameraView() != null
                && AvmApp.getInstance().getCameraView().isSmartWin
                && !AvmApp.getInstance().getCameraView().isFirstOpen) {
            //存在左右分屏切换，需要更改吸附位置
            mHandler.post(() -> AvmApp.getInstance().getCameraView().snapToPosition());
        }
    }

    public   static  boolean isCalibration = false;


    private void initData() {
//        SystemProperties.set("settingPathLine","1");  // 轨迹线
//        SystemProperties.set("activatedPanorama","1"); // 雷达激活
//        SystemProperties.set("signalActivates","1"); // // 转向激活
//        int activatedPanorama = SystemProperties.getInt("settingPathLine", -1);
//        if (activatedPanorama == -1) {
//            SystemProperties.set("settingPathLine", "1");
//        }
//        int signalActivates = SystemProperties.getInt("signalActivates", -1);
//        if (signalActivates == -1) {
//            SystemProperties.set("signalActivates", "1");
//        }
    }

  /**
   * R档测试-关闭-打开-d档
   */
  private  void  testModel(){
    reverse(3);
    mHandler.postDelayed(()->{
      CameraViewModelHelper.getInstance().dismissView(false, 0, "0");
      mHandler.postDelayed(()->{
        CameraViewModelHelper.getInstance().showView(true);
        mHandler.postDelayed(()->{
          reverse(1);
        },5000);
      },1000);
    },3000);
  }

    private void onValueChangedListener(int vehicleId, Object value) {
        if(AvmApp.getInstance().getCameraView() == null){
            KLog.d("AvmApp", "avm is null  vehicleId & value："+vehicleId +" :"+value);
            return;
        }

        if (vehicleId == AVM_UINM_TURN_LIGHT_SW_ST) { //转向激活
            KLog.d(" TurnLamp 转向 vehicleId = " + vehicleId + "  ,value = " + value);
            if (value instanceof Integer) {
                int intValue = (int) value;
                turnLampSwSts = intValue;
                //                turnLampChangeTime = System.currentTimeMillis();
                if (intValue == 0) {
                    turnLampChange(intValue, 800);
                } else {
                    turnLampChange(intValue, 0);
                }
            }
        } else if (vehicleId == CLUSTER_LEFT_TURN_LAMP || vehicleId == CLUSTER_RIGHT_TURN_LAMP) {//左边转向灯闪s
            if (!(value instanceof Integer)) return;
            if (vehicleId == CLUSTER_LEFT_TURN_LAMP) {
                KLog.d(" TurnLamp 左边转向灯闪 , value = " + value + " , (leftTurnLChangeTime-rightTurnLChangeTime) = " + (leftTurnLChangeTime-rightTurnLChangeTime) + " , turnLampSwSts = " + turnLampSwSts);
            } else {
                KLog.d(" TurnLamp 右边转向灯闪 , value = " + value + " , (leftTurnLChangeTime-rightTurnLChangeTime) = " + (leftTurnLChangeTime-rightTurnLChangeTime) + " , turnLampSwSts = " + turnLampSwSts);
            }
//            AvmRuntime.self().inputTurnValue((Integer) value, System.currentTimeMillis());
//            if (AvmRuntime.self().isDoubleBlink()) {
//                KLog.d("TurnLamp 判断为双闪.");
//                AvmRuntime.self().doubleBlink();
//                endDoubleBlink();
//            } else {
//                turnLampChange(0, 800);
//                AvmRuntime.self().updateChangeTime();
//            }

            if (AvmRuntime.self().isDoubleBlink()) {
                //
            } else {
                turnLampChange(0, 800);
                AvmRuntime.self().updateChangeTime();
            }

//            leftTurnLChangeTime = System.currentTimeMillis();
//            leftTurnLValue = (int) value;
//            KLog.d(" TurnLamp 左边转向灯闪 , value = " + value + " , (leftTurnLChangeTime-rightTurnLChangeTime) = " + (leftTurnLChangeTime-rightTurnLChangeTime) + " , turnLampSwSts = " + turnLampSwSts + " , doubleBlinkStep = " + doubleBlinkStep);
//            if ((leftTurnLChangeTime-rightTurnLChangeTime) < 500) { //双闪
////                KLog.d("TurnLamp 判断为双闪.");
//                if (doubleBlinkStep == 3) {
//                    AvmRuntime.self().doubleBlink();
//                } else {
//                    doubleBlinkStep++;
//                }
//                return;
//            }
//            doubleBlinkStep = 0;
//            turnLampChange(0, 800);
//            AvmRuntime.self().updateChangeTime();
        } /*else if (vehicleId == CLUSTER_RIGHT_TURN_LAMP) {//右边转向灯闪
            if (!(value instanceof Integer)) return;

            rightTurnLChangeTime = System.currentTimeMillis();
            rightTurnLValue = (int) value;
            KLog.d("TurnLamp 右边转向灯闪 , value = " + value + " , (rightTurnLChangeTime-leftTurnLChangeTime) " + (rightTurnLChangeTime-leftTurnLChangeTime) + " , turnLampSwSts = " + turnLampSwSts + " , doubleBlinkStep = " + doubleBlinkStep);
            if ((rightTurnLChangeTime-leftTurnLChangeTime) < 500) { //双闪
                KLog.d("TurnLamp 判断为双闪.");
                if (doubleBlinkStep == 3) {
                    AvmRuntime.self().doubleBlink();
                } else {
                    doubleBlinkStep++;
                }
                return;
            }
            doubleBlinkStep = 0;
            turnLampChange(0, 800);
            AvmRuntime.self().updateChangeTime();
        }*/ else if (vehicleId == DESK_HAZARD_LAMP_STATUS) {
            KLog.d(" 双闪 vehicleId = " + vehicleId + "  ,value = " + value);
            if (value instanceof Integer) {
                int intValue = (int) value;
                if (intValue == 1) {
                    AvmRuntime.self().doubleBlinkEvt(DataDefine.EVT_ON_DOUBLE_BLINK);
                } else if (intValue == 0) {
                    AvmRuntime.self().doubleBlinkEvt(DataDefine.EVT_OFF_DOUBLE_BLINK);
                }
            }
        } else if (vehicleId == VEHICLE_SPEED) {// 车速
            //KLog.d(" 车速 vehicleId = " + vehicleId + "  ,value = " + value);
            if (value instanceof Float) {
                AvmRuntime.self().speedChange((Float) value);
            }
            CameraViewModelHelper.getInstance().setSpeed((Float) value);
        } else if (vehicleId == SETTINGS_VCU_BRKPEDPST) {//刹车踏板

        } else if (vehicleId == AVM_SAS_STEERING_ANGLE) {//转角值
            CameraViewModelHelper.getInstance().angleSteel();
        } else if (vehicleId == MIRROR_FOLD_UNFOLD_STATUS) { // 后视镜折叠
            KLog.d(" 后视镜折叠 ....  ..... " + value);
            CameraViewModelHelper.getInstance().mirrorFoldUnFoldStatus(value);
        } else if (vehicleId == SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE) { // 后视下翻
            CameraViewModelHelper.getInstance().mirrorAutomaticStatus();
        } else if (vehicleId == CLUSTER_VCU_GEAR_LVL_DISP) {// 挡位
            KLog.d(" 挡位 vehicleId = " + vehicleId + "  ,value = " + value);
            if (value instanceof Integer) {
                int gear = (int) value;// 第一次开机后的默认值
                if (gear == 0) return;
                AvmRuntime.self().gearChange(gear);
                BvAvmJNIHelper.getInstance().updateTrajLineStatus(gear);
                //BvAvmJNIHelper.getInstance().updateTrajLineStatus(gear);
                if (gear == 4) {
                    BvAvmJNIHelper.getInstance().bwClearCarBottomImage();
                    //BvAvmJNIHelper.getInstance().bwClearCarBottomImage();
                }

                if (BvAvmJNIHelper.isAvmDeInit) {
                    reverse((int) value);
                } else {
                    KLog.d("初始化未成功 ，过滤挡位");
                }
            }
        }else if(vehicleId == NFS_SYNC_STATUS){
            KLog.i(" 标定结果 NFS_SYNC_STATUS： " + vehicleId + "  ,value = " + value);
            if (value instanceof Integer) {
                int sync_status = (int) value;
                if(sync_status == 1){
                    byte[] arrBack = {0x00, 0x00, 0x00, 0x00};
                    CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
                    CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_success));
                    KLog.i("标定-DIAG_31 app 标定成功 ");
                    DataManager.writeFault(DataConstant.Code.BD_SUCCESS);
                    DataManager.writeFault(DataConstant.Code.SJ_SAVE_SUCCESS);
                }
            }
        }

        setDoorStatus(vehicleId, value);
        setFlWheelStatus(vehicleId, value);
        setLight(vehicleId, value);
        setRadar(vehicleId, value);
        setCalibration(vehicleId, value);
    }

    private int mDirection = -1;
    public void turnLampChange(int direction, long delay) {
        if (direction != 0) {
            mHandler.removeMessages(MSG_TURN_LAMP_CHANGE);
            AvmRuntime.self().turnLampChange(direction);
        } else {
            mHandler.removeMessages(MSG_TURN_LAMP_CHANGE);
            Message message = Message.obtain();
            message.what = MSG_TURN_LAMP_CHANGE;
            message.arg1 = direction;
            mHandler.sendMessageDelayed(message, delay);
            mDirection = direction;
        }
    }

//    public void endDoubleBlink() {
//        mHandler.removeMessages(MSG_END_DOUBLE_BLINK);
//        mHandler.sendEmptyMessageDelayed(MSG_END_DOUBLE_BLINK, 600);
//    }

    public void setWheelAngle(Object value) {
        if(value instanceof Float) {
            BvAvmJNIHelper.getInstance().bwSetWheelAngle((float)value);
        }
    }

    @Override
    public void onEnter(int act, int parm) {
        KLog.d("onEnter -> action : " + DataDefine.id2String(act));
        Message message = Message.obtain();
        message.what = MSG_ACTION_ENTER;
        message.arg1 = act;
        message.arg2 = parm;
        mHandler.sendMessage(message);
    }

    @Override
    public void onExit(int act) {
        KLog.d("onExit -> action : " + DataDefine.id2String(act));
    }

    @Override
    public void onGearNoAct(int gear, boolean handleFlag) {

    }

    @Override
    public void userTap() {

    }

    public class AvmServiceIBinder extends Binder {


    }

    private int gearValue = 0;

    public void openNotView(Context context) {
        String id = context.getPackageName();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(id, "AVM", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setShowBadge(true);

            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            builder.setChannelId(id);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.radar4_green_3);
        builder.setContentIntent(pendingIntent)
                .setLargeIcon(bitmap)
                .setContentTitle("")
                .setSmallIcon(R.mipmap.ic_back_mirror)
                .setContentText("")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.COLOR_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(101054, notification);
        } else {

        }

    }
     private  int fishTh = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d(flags + "[onStartCommand]" + startId);
        //adb指令模拟启动service带参数调试功能
        //adb shell am start-service -n com.autochips.avm/.service3.AvmService --ei avm_onclick 1


        if(AvmApp.getInstance().getCameraView() ==null){
            KLog.d(flags + "[onStartCommand] AvmApp view is not init");
            return START_STICKY;
        }
        if (intent != null) {
            int cancleScreen = intent.getIntExtra("cancleScreen", -1);
            int changeScreen = intent.getIntExtra("changeScreen", -1);
            if(cancleScreen != -1 || changeScreen != -1){
                if(cancleScreen != -1){
                    mIsStartStatus = !mIsStartStatus;
                }
                if(changeScreen != -1){
                    isLeftScreen = !isLeftScreen;
                }
                changeScreenDirection();
                return START_STICKY;
            }
            int avm_onclick = intent.getIntExtra("avm_start", -1);
            KLog.d("[onStartCommand] avm_start = " + avm_onclick);
            switch (avm_onclick) {
                case 1: //SystemUI跳转打开AVM首页
                case 2: //方控mode打开AVM首页
                case 3: //语音唤醒打开AVM首页
                case 4: //语音唤醒打开AVM首页
                    fishTh = 1;
                    AvmRuntime.self().artificialEnter();
//                    AvmApp.getInstance().getCameraView().showSmartWin();
//                  testModel();
                    DataManager.writeFault(avm_onclick == 1 ? DataConstant.Code.CLICK_IN_SUI :
                            avm_onclick == 2 ? DataConstant.Code.CLICK_IN_FK : DataConstant.Code.CLICK_IN_SPEECH);
                    break;
                case 0://关闭AVM首页
                    int  gear_R = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);
                    KLog.d(gear_R +" gear_R 启动---3-service_123  关闭AVM首页 "+fishTh);
                    if (fishTh == 0){// 防止开机自动首次被关闭
                        fishTh = 1;
                        if (gear_R == 3)
                           break;
                    }

                    AvmRuntime.self().artificialExit(true);
                    break;

                case 11://启动调试activity页面
                    break;
            }
        } else {
            KLog.w("[intent == null]");
        }



        return START_STICKY;
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
    public void onDestroy() {
        if (!isExitAction){
            BvAvmJNIHelper.getInstance().avmDeInit();
        }
        SplitScreenManager.getInstance().deInit();
        AvmApp.getInstance().getCameraView().removeView();
        ThreadPoolUtil.getInstance().removeAllHandlerAndShutdownThreadPool();
        CanManager.getInstance().unRegisterSignalListener(mOnSignalValueChangedListener);
//        if (!isExitAction)
//          ServiceUtils.startCaptureService(this,AvmService.class);
        super.onDestroy();
        KLog.d("[onDestroy]");
        unregisterReceiver(broadcastReceiver);
    }

    private  int  count = 0;

    int turnLampSwSts = -1;
    int leftTurnLValue = -1;
    int rightTurnLValue = -1;
    long leftTurnLChangeTime = 0;
    long rightTurnLChangeTime = 0;

    private int leftLightStPt = 0;//左转灯光 0表示不亮 1表示亮起,控制2.5处理
    private int rightLightStpt = 0;//右转灯光 0表示不亮 1表示亮起,控制2.5处理
    private final CanManager.onSignalValueChangedListener mOnSignalValueChangedListener = this::onValueChangedListener;

    private  void reverse(int value){
        gearValue = value;
        CameraViewModelHelper.getInstance().reverse(gearValue);

    }

    private void turnExit(Integer value) {
        CameraViewModelHelper.getInstance().turnExit(value);

    }


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
                CameraViewModelHelper.getInstance().doorStatus((Integer) value);
                break;
        }
    }

//    private  final  Handler mHandler = new Handler(Looper.getMainLooper());

    private  int calStatus = 0;

    public int getCalStatus(int vehicleId,String status) {
        calStatus +=1;
        isCalibration = true;
        KLog.d( calStatus+ "  calStatus 标定状态status -: "+status+" ，vehicleId: "+vehicleId );
        mHandler.removeCallbacksAndMessages("calStatus");
        mHandler.postDelayed(()->{
            if (calStatus != -1){
                KLog.d("标定状态超时--重置状态-: "+calStatus);
                calStatus = 0;
                isCalibration = false;
            }
        },30000);
        return calStatus;
    }

    /**
     * 标定
     *
     * @param vehicleId
     * @param value
     */
    private  boolean isInt3801 = false;
    private  boolean isInt3801_2 = false;
    public void setCalibration(int vehicleId, Object value) {
        if (value instanceof byte[]) {
            byte[] arr = (byte[]) value;// [0x00 ]
            KLog.i(arr.length +"  length 标定-byte----vehicleId: " + vehicleId + "  value   " + Arrays.toString(arr)+ "  版本号： "+ServiceUtils.getVersionName());

            byte[] arrBack = {0x00, 0x00, 0x00, 0x00};
            if (arr.length < 2 ) {
                KLog.e(vehicleId +" DIAG_31_ vehicleId 标定-进入下线标定请求 value 长度错误:" + Arrays.toString(arr));
                return;
            }
            switch (vehicleId) {
                case DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ://进入下线标定请求
                    KLog.i(" 步骤 1  标定-进入下线标定请求:DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ:" + Arrays.toString(arr)+ "  版本号： "+ServiceUtils.getVersionName());
                    if (arr.length != 4) {
                        return;
                    }

                    CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_UNDISTORT);
                    AvmRuntime.self().artificialEnter();

                    getCalStatus(vehicleId,"DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ");
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
                     getCalStatus(vehicleId,"DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ");
                    if ( !isInt3801){
                        KLog.i("标定-未进入:DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ" );
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
                    if ( getCalStatus(vehicleId,"DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ") != 3){
                        KLog.i("标定-未进入:DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ" );
//                        arrBack = new byte[]{0x01, 0x00, 0x00, 0x00};
                    }
                    if ( !isInt3801){
                      KLog.i("标定-步骤 3失败 未进入:步骤 1 " );
                       arrBack = new byte[]{0x01, 0x01, 0x00, 0x00};
                    }
                    CanManager.getInstance().setByteArray(DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_CHECK);
                    break;
                case DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ://标定预结果结果请求
                    KLog.i("步骤 4 标定-标定预结果结果请求:DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ:" + Arrays.toString(arr));
                    if (arr.length < 2 ) {
                        return;
                    }
                    arrBack = new byte[]{0x00, 0x00, 0x00, 0x00};
                    if ( getCalStatus(vehicleId,"查询结果") != 4){
                    }
                    CanManager.getInstance().setByteArray(DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_CHECK_RESULT);
                    break;
                case DIAG_31_3803_AVM_START_CALIBRATION_REQ://开始标定请求
                    KLog.i("步骤 5  标定-开始标定请求:DIAG_31_3803_AVM_START_CALIBRATION_REQ:" + Arrays.toString(arr));
                    if (arr.length != 8) {
                        return;
                    }

                    AvmApp.getInstance().getCameraView().startCalibration();
                    DataManager.writeFault(DataConstant.Code.BD_START);
                    break;
                case DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ://开始标定结果请求
                    KLog.i("步骤 6 标定-开始标定结果请求:DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ:" + Arrays.toString(arr));
                    //arrBack = new byte[]{0x00, 0x00, 0x00, 0x00};
                    //CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
                    if (arr.length != 4) {
                        return;
                    }

                    mHandler.removeMessages(MSG_CALIBRATION_CHECK_RESP);
                    mHandler.sendEmptyMessageDelayed(MSG_CALIBRATION_CHECK_RESP, 3000);
                    break;
                case DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ://下线标定检查

                    KLog.i("步骤 7  标定-下线标定检查:DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ:" + Arrays.toString(arr));
                    if (arr.length < 2 ) {
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
                    DataManager.writeFault(DataConstant.Code.BD_IN_CHECK_RESULT);
                    //AvmApp.getInstance().getCameraView().calibrationBack();
                    break;
                case DIAG_31_380D_AVM_READ_FAIL_REASON_REQ://读取标定失败原因请求
                    KLog.i(" 步骤 9 标定-读取标定失败原因请求:DIAG_31_380D_AVM_READ_FAIL_REASON_REQ:" + Arrays.toString(arr));
                    arrBack = new byte[]{0x00, 0x00, 0x00, 0x00};

                    //arrBack = new byte[]{0x00};
                    if (arr.length < 2 ) {
                        return;
                    }

                    CanManager.getInstance().setByteArray(DIAG_31_380D_AVM_READ_FAIL_REASON_RESP, 0, arrBack);
                    DataManager.writeFault(DataConstant.Code.BD_FALUT);
                    break;
                case DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ://读取标定失败原因结果请求
                    KLog.i("步骤 10 标定-读取标定失败原因结果请求:DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ:" + Arrays.toString(arr));
                    if (arr.length != 4) {
                        return;
                    }
                    AvmApp.getInstance().getCameraView().calibrationError();
                    DataManager.writeFault(DataConstant.Code.BD_FALUT_RESULT);
                    break;
            }
        }

    }

    private void setFlWheelStatus(int vehicleId, Object object) {
        switch (vehicleId) {
            case AVM_BCS_RLWHEEL_ROTATED_DIR://右前轮前进方向
            case AVM_BCS_FLWHEEL_ROTATED_DIR://左前轮前进方向
                CameraViewModelHelper.getInstance().flWheelDir();
                CameraViewModelHelper.getInstance().flWheelSpd(object);
                break;
            case AVM_BCS_FLWHEEL_SPD://左后轮轮速
            case AVM_BCS_RLWHEEL_SPD://右后轮轮速
            case AVM_BCS_RRWHEEL_SPD:
            case AVM_BCS_FRWHEEL_SPD:
            case AVM_WHEEL_DIRE_SPEED:
                CameraViewModelHelper.getInstance().flWheelSpd(object);
                break;

        }
    }

    //    FSLS雷达距离（前左侧）(预留)	CLUSTER_PAS_FSLSideDistance
//    FSRS雷达距离（前右侧）(预留)	CLUSTER_PAS_FSRSideDistance
//    FR雷达距离（前右）(预留)	CLUSTER_PAS_PAS_FRDistance
//    FL雷达距离（前左）(预留)	CLUSTER_PAS_PAS_FLDistance
//    FRM雷达距离（前右中）(预留)	CLUSTER_PAS_FRMidDistance
//    FLM雷达距离（前左中）(预留)	CLUSTER_PAS_FLMidDistance
//    RRM雷达距离（右中）(预留)	AVM_RR_MIDSNS_ERR_FLAG
    private boolean isRadarFront60 = false;
    private boolean isRadarFront110 = false;

    private void setRadar(int vehicleId, Object object) {

      if (vehicleId == CLUSTER_PAS_Distance && object instanceof Integer[]){
          Integer[] arr = (Integer[]) object;// [0x00 ]
          KLog.i(arr.length + "  length 雷达检测距离CLUSTER_PAS_Distance ： " + vehicleId + "  value   " + Arrays.toString(arr) );
          int right = arr[2];
          int mil = arr[1];
          int left = arr[3];
          AvmApp.getInstance().getCameraView().setRadar(CLUSTER_PAS_RLDistance, left,gearValue);
          AvmApp.getInstance().getCameraView().setRadar(CLUSTER_PAS_RLMidDistance, mil,gearValue);
          AvmApp.getInstance().getCameraView().setRadar(CLUSTER_PAS_RRDistance, right,gearValue);
          return;
      }

      if (!(object instanceof Integer)) {
            return;
        }
        int status = (int) object;
//      KLog.d("信号监听 vehicleId = " + vehicleId + "  ,value = " + status);
        switch (vehicleId) {
            case CLUSTER_PAS_FSLSideDistance://前左侧）
            case CLUSTER_PAS_FSRSideDistance: //（前右侧）
            case CLUSTER_PAS_PAS_FRDistance://（前右） 60
            case CLUSTER_PAS_PAS_FLDistance://（前左）
            case CLUSTER_PAS_PAS_RSLSideDistance:// 后侧左
            case CLUSTER_PAS_PAS_RSRSideDistance:// 后侧右
                if (status < 1) return;
                AvmApp.getInstance().getCameraView().setRadar(vehicleId, status,gearValue);
                if (status <= 60 && (gearValue == 1 || gearValue == 2)) {
                    isRadarFront60 = true;
                    CameraViewModelHelper.getInstance().radarActive(status);
                } else if (isRadarFront60) {
                    isRadarFront60 = false;
                    if (!isRadarFront110)
                        CameraViewModelHelper.getInstance().radarExit(status);
                }
                break;

            case CLUSTER_PAS_FRMidDistance://（前右中）
            case CLUSTER_PAS_FLMidDistance://（前左中）
                if (status < 1) return;
                AvmApp.getInstance().getCameraView().setRadar(vehicleId, status,gearValue);

                if (status <= 110 && (gearValue == 1 || gearValue == 2)) {
                    isRadarFront110 = true;
                    CameraViewModelHelper.getInstance().radarActive(status);
                } else {
                    isRadarFront110 = false;
                    if (!isRadarFront60)
                        CameraViewModelHelper.getInstance().radarExit(status);
                }
                break;
            case CLUSTER_PAS_RLMidDistance:// 后左中
            case CLUSTER_PAS_RRMidDistance:// 后中
            case CLUSTER_PAS_RRDistance:// 后右
            case CLUSTER_PAS_RLDistance://后左
                AvmApp.getInstance().getCameraView().setRadar(vehicleId, status,gearValue);
                break;
            case ASSIST_DRIVE_PAS_BUTTON_PRESS:// 雷达报警声
            case CLUSTER_CHIME_PAS_WARNTONE://雷达报警音状态
                 //AvmApp.getInstance().getCameraView().showRadarSoundView(status);
                break;
            case AVM_RADAR_ALARM_ACOUSTIC_SWITCH:// 雷达故障报警
                AvmApp.getInstance().getCameraView().showParkingAssistView(status);
                break;
            case AVM_RR_MIDSNS_ERR_FLAG:   //     后右中
            case AVM_RL_MIDSNS_ERR_FLAG:    //     后左中
                AvmApp.getInstance().getCameraView().setRadarFailStatus(2,status);
                break;
            case AVM_RR_SNS_ERR_FLAG: //       后右
                AvmApp.getInstance().getCameraView().setRadarFailStatus(4,status);
                break;
          case AVM_RSL_SNS_ERR_FLAG:    //       后左
          case AVM_RSR_SNS_ERR_FLAG:    //       后左
          case AVM_FRS_SNS_ERR_FLAG:    //       后左
          case AVM_FLS_SNS_ERR_FLAG:    //       后左
          case AVM_RL_SNS_ERR_FLAG:    //       后左
                AvmApp.getInstance().getCameraView().setRadarFailStatus(1,status);
                break;
          case AVM_PAS_SYSTEMTYPE:    //       雷达系统故障，没有找到相关UI
                AvmApp.getInstance().getCameraView().setRadarFailStatus(1,status);
                AvmApp.getInstance().getCameraView().setRadarFailStatus(2,status);
                AvmApp.getInstance().getCameraView().setRadarFailStatus(4,status);
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
                CameraViewModelHelper.getInstance().showLight3DModel(vehicleId, (Integer) status);
                break;
        }

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
                BvAvmJNIHelper.getInstance().bwDeleteCamera();
//                System.exit(0);

            } else if (action.equals(BR_GEAR_STATUS)) {
                int gearValue = intent.getIntExtra("value", -1);
                if (BvAvmJNIHelper.isAvmDeInit) {
                    reverse(gearValue);
                } else {
                    KLog.d("初始化未成功 ，过滤挡位");
                }
            } else if (action.equals(BR_TEST)) {
                int testValue = intent.getIntExtra("value", -1);
                KLog.d("test value is " + testValue);
                if (testValue == 0) {
                    Intent mainIntent = new Intent(AvmApp.getInstance(), MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                } else if (testValue == 3) {
                    AvmApp.getInstance().getCameraView().showSmartWin();
                } else if (testValue == 4) {
                    BvAvmJNIHelper.getInstance().bwSetCarDoorStatus(new int[] {
                            1,0,1,0,1,0,1,0
                    });
                } else if (testValue == 5) {
                    BvAvmJNIHelper.getInstance().bwSetCarDoorStatus(new int[] {
                            0,0,0,0,0,0,0,0
                    });
                } else if (testValue == 10) {
                    int signal = intent.getIntExtra("signal", -1);
                    CameraGLSurfaceView.setAngleOfView2(signal);
                } else if (testValue == 11) {
                    int speedValue = intent.getIntExtra("speed", -1);
                    KLog.d("AvmRuntime recv speed : " + speedValue);
                    AvmRuntime.self().speedChange(speedValue);
                    CameraViewModelHelper.getInstance().setSpeed(speedValue);
                } else if (testValue == 12) {
                    changeScreenDirection();
                } else if (testValue == 13) {
                    AvmApp.getInstance().getCameraView().getViewModel().setAutomaticCalibration();
                } else if (testValue == 14) {
                    try {
                        File file = new File("/avm_config/rvc_bvavm_fifo");
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write("exit".getBytes());
                        fileOutputStream.close();
                    } catch (IOException ioException) {
                        KLog.e("AVM_DEBUG " + ioException.toString());
                    }
                } else if (testValue == 100) {
                    int vehicleId = intent.getIntExtra("vehicleId", -1);
                    int vehicleValue = intent.getIntExtra("vehicleValue", -1);
                    onValueChangedListener(vehicleId, vehicleValue);
                }
            } else if (action.equals(BR_TURN_LAMP_STATUS)) {
                int value = intent.getIntExtra("value", -1);
                if (BvAvmJNIHelper.isAvmDeInit) {
                    CameraViewModelHelper.getInstance().turnActive(value, false);
                } else {
                    KLog.d("初始化未成功 ，过滤转向");
                }
            } else if (action.equals(MENU_KEY)) {
                boolean visible = intent.getBooleanExtra("visible", false);
                if (visible) {
                    CameraViewModelHelper.getInstance().dismissView(false, 0, "click");
                }
            }

        }
    }

//    public static void set(String key, String value) {
//        Class<?> SysProp = null;
//        Method method = null;
//        try {
//            SysProp = Class.forName("android.os.SystemProperties");
//            method = SysProp.getMethod("set", String.class, String.class);
//            method.invoke(null, key, value);
//        } catch (Exception e) {
//            Log.e("AVM_DEBUG","read SystemProperties error",e);
//        }
//    }

}
