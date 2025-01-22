package com.autochips.avm.viewmode;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030A_AVM_FRONT_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030B_AVM_REAR_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030C_AVM_LEFT_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030D_AVM_RIGHT_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.NFS_SYNC;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.NFS_SYNC_STATUS;
import static com.android.bvavm.bvavmJNI.SCANCODE_IR_POINT1;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESP;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.info.CameraInfo;
import com.autochips.avm.listener.ICameraViewListener;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.ui.activity.MockActivity;
import com.autochips.avm.ui.view.CameraGLSurfaceView;
import com.autochips.avm.util.CustomToast;
import com.avm.framwork.constant.CameraContracts;
import com.avm.framwork.helper.ThreadPoolUtil;
import com.avm.framwork.manager.CanManager;
import com.avm.framwork.manager.ViewSwitchManager;

import java.util.Arrays;
import java.util.Timer;

import me.goldze.mvvmhabit.utils.KLog;

public class CameraViewModel extends BaseCameraViewModel {
    private ICameraViewListener mICameraViewListener;
    private MutableLiveData<String> liveDataCamera2DTopUI;
    private MutableLiveData<String> liveDataCamera3DTopUI;
    private MutableLiveData<Integer> liveDataViewLayout;
    private MutableLiveData<String> liveDataRadarSound;
    private MutableLiveData<String> liveData2DSound, liveData3DSound;
    private MutableLiveData<CameraInfo> liveDataInfo;
    private CloseTimer closeTimer;
    private String chick2DView;
    private CameraInfo info = new CameraInfo();
    private Timer testTimer;
    //    private ViewType mHisModel;

    private HandlerThread handlerThread = new HandlerThread("HandlerThread");
    private Handler threadHandler;

    private int isCaliStatus = -1;

    private final int MSG_CALIBRATE = 1;
    private final int MSG_CREATE_CAMERA = 3;
    private final int MSG_DELETE_CAMERA = 4;
    private final int MSG_AVM_INIT = 5;
    private final int MSG_FREE_3D = 6;
    private final int MSG_WHEEL_DIRECTION = 7;
    private final int MSG_WHEEL_SPEED = 8;
    private final int MSG_DOOR_STS = 9;
    private final int MSG_LIGHT_MODEL = 10;
    private final int MSG_RADAR_ACTIVE = 11;
    private final int MSG_RADAR_EXIT = 12;
    private final int MSG_UPDATE_TRAJ_LINE_STS = 13;
    private final int MSG_SET_UNDISTORT_LEVEL = 14;
    private final int MSG_CALIBRATE_RESP = 15;
    private final int MSG_CALIBRATING = 18;
    private final int MSG_SIM_WHEEL_SPEED = 20;
    private final int MSG_SET_TRAJLINE_ENABLE = 21;
    private final int MSG_TURN_LAMP_CHANGE = 22;
    private final int MSG_VHEEL_ANGLE = 24;
    private final int MSG_CLEAR_BOTTOM = 25;
    private final int MSG_SET_BWSTATUS = 26;

    public CameraViewModel() {
        liveDataCamera2DTopUI = new MutableLiveData<>();
        liveDataCamera3DTopUI = new MutableLiveData<>();
        liveData2DSound = new MutableLiveData<>();
        liveData3DSound = new MutableLiveData<>();
        liveDataViewLayout = new MutableLiveData<>();
        liveDataRadarSound = new MutableLiveData<>();
        liveDataInfo = new MutableLiveData<>();
        info.setVersionName(getVersionName());
        info.setShowCaliDemo(false);
        liveDataInfo.postValue(info);

        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                KLog.i("handleMessage : " + msg.what + " , " + msg.arg1 + " , " + msg.arg2);
                if (msg.what == MSG_CALIBRATE) {
                    KLog.i("标定 handle MSG_CALIBRATE.");
                    threadHandler.sendEmptyMessage(MSG_CALIBRATING);
                    BvAvmJNIHelper.getInstance().setCalibration(true);
                    isCaliStatus = -1;
                    isCaliStatus = bvavmJNI.bwStartCalibrate(msg.arg1);
                    KLog.i("标定 bwStartCalibrate ret is " + isCaliStatus);
                    BvAvmJNIHelper.getInstance().setCalibration(false);
                    // isCaliStatus 返回值
                    // 0 成功
                    // 2 后视图标定失败
                    // 4 左视图标定失败
                    // 8 右视图标定失败
                    threadHandler.removeMessages(MSG_CALIBRATING);
                } else if (msg.what == MSG_CALIBRATE_RESP) {
                    KLog.i("标定 handle MSG_CALIBRATE_RESP.");
                    AvmApp.getInstance().getCameraView().calibrationBack();
                } else if (msg.what == MSG_CREATE_CAMERA) {
                    BvAvmJNIHelper.getInstance().bwCreateCamera("com/autochips/avm/ui/view/CameraView", "onBVAVMMessage");
                } else if (msg.what == MSG_DELETE_CAMERA) {
                    BvAvmJNIHelper.getInstance().bwDeleteCamera();
                } else if (msg.what == MSG_AVM_INIT) {
                    BvAvmJNIHelper.getInstance().avmInit(AvmApp.getInstance().getApplicationContext());
                } else if (msg.what == MSG_FREE_3D) {
                    BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
                } else if (msg.what == MSG_WHEEL_DIRECTION) {
                    CameraViewModelHelper.getInstance().flWheelDir();
                } else if (msg.what == MSG_WHEEL_SPEED) {
                    CameraViewModelHelper.getInstance().flWheelSpd(msg.obj);
                } else if (msg.what == MSG_DOOR_STS) {
                    CameraViewModelHelper.getInstance().doorStatus(msg.arg1, msg.arg2);
                } else if (msg.what == MSG_LIGHT_MODEL) {
                    CameraViewModelHelper.getInstance().showLight3DModel(msg.arg1, msg.arg2);
                } else if (msg.what == MSG_RADAR_ACTIVE) {
                    CameraViewModelHelper.getInstance().radarActive(msg.arg1);
                } else if(msg.what == MSG_VHEEL_ANGLE){
                    if(msg.obj instanceof Float) {
                        BvAvmJNIHelper.getInstance().bwSetWheelAngle((float)msg.obj);
                    }
                } else if(msg.what == MSG_CLEAR_BOTTOM){
                    BvAvmJNIHelper.getInstance().bwClearCarBottomImage();
                } else if (msg.what == MSG_SET_BWSTATUS) {
                    bvavmJNI.bwSetRVCStatus(msg.arg1);
                } else if (msg.what == MSG_RADAR_EXIT) {
                    CameraViewModelHelper.getInstance().radarExit(msg.arg1);
                } else if (msg.what == MSG_UPDATE_TRAJ_LINE_STS) {
                    BvAvmJNIHelper.getInstance().updateTrajLineStatus(msg.arg1);
                } else if (msg.what == MSG_SET_TRAJLINE_ENABLE) {
                    BvAvmJNIHelper.getInstance().bwSetTrajLineStatus((byte) msg.arg1);
                } else if (msg.what == MSG_SET_UNDISTORT_LEVEL) {
                    bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
                } else if (msg.what == MSG_TURN_LAMP_CHANGE) {
                    AvmRuntime.self().turnLampChange(msg.arg1);
                } else if (msg.what == MSG_SIM_WHEEL_SPEED) {
                    if (msg.arg1 == 1) {
                        bvavmJNI.bwSetFourWheelSpeed(msg.arg2, msg.arg2, msg.arg2, msg.arg2);
                        if (msg.arg2 == 30) {
                            Message message = Message.obtain();
                            message.what = MSG_SIM_WHEEL_SPEED;
                            message.arg1 = 2;
                            message.arg2 = msg.arg2-1;
                            threadHandler.sendMessageDelayed(message, 200);
                        } else {
                            Message message = Message.obtain();
                            message.what = MSG_SIM_WHEEL_SPEED;
                            message.arg1 = 1;
                            message.arg2 = msg.arg2+1;
                            threadHandler.sendMessageDelayed(message, 200);
                        }
                    } else if (msg.arg1 == 2) {
                        bvavmJNI.bwSetFourWheelSpeed(msg.arg2, msg.arg2, msg.arg2, msg.arg2);
                        if (msg.arg2 > 0) {
                            Message message = Message.obtain();
                            message.what = MSG_SIM_WHEEL_SPEED;
                            message.arg1 = 2;
                            message.arg2 = msg.arg2-1;
                            threadHandler.sendMessageDelayed(message, 200);
                        }
                    }
                }
            }
        };

    }

    public MutableLiveData<String> getLiveDataCamera2DTopUI() {
        return liveDataCamera2DTopUI;
    }

    public MutableLiveData<String> getLiveDataCamera2D() {
        return liveData2DSound;
    }

    public MutableLiveData<String> getLiveDataCamera3D() {
        return liveData3DSound;
    }

    public MutableLiveData<String> getLiveDataCamera3DTopUI() {
        return liveDataCamera3DTopUI;
    }

    public void setLiveDataCamera2DTopUI(String index) {
        liveDataCamera2DTopUI.postValue(index);
    }

    public MutableLiveData<Integer> getLiveDataViewLayout() {
        return liveDataViewLayout;
    }

    public MutableLiveData<String> getLiveDataRadarSound() {
        return liveDataRadarSound;
    }

    public CameraInfo getInfo() {
        return info;
    }

    /**
     * 关闭AVM首页
     */
    public void closeAvm() {// 手动关闭
        KLog.i("closeAvm");
        AvmRuntime.self().artificialExit();
//        setRunning(false);
//        CameraViewModelHelper.getInstance().setRadarActiveTow(true);
//        CameraViewModelHelper.getInstance().dismissView(false, 0, "2");
//
//        if (AvmService.JNI_IN_THREAD_FLAG) {
//            reset3D();
//        } else {
//            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
//        }
    }


    /**
     * 是否在操作
     *
     * @param running
     */
    public void setRunning(boolean running) { }


    /**
     * 是否在标定
     *
     * @param running
     */
    public void setCalibrateRunning(boolean running) {
        CameraViewModelHelper.getInstance().setCalibrateRunning(running);
    }

    /**
     * 打开设置弹窗
     */
    private boolean isOpenSettingDialog = false;

    public void openSettingDialog() {
        KLog.i("openSettingDialog");
        mICameraViewListener.setBtnSettingSelect(isOpenSettingDialog = !isOpenSettingDialog);
        setRunning(true);
    }

    /**
     * 打开后视镜弹窗
     */
    private boolean isOpenBackMirrorDialog = false;

    public void openBackMirrorDialog() {
        KLog.i("openBackMirrorDialog");
        mICameraViewListener.setBtnRearMirrorSelect(isOpenBackMirrorDialog = !isOpenBackMirrorDialog);
        setRunning(true);
    }

    /**
     * 获取版本号
     */
    public String getVersionName() {
        KLog.i("getVersionName");
        String versionName = "";
        try {
            PackageManager packageManager = AvmApp.getInstance().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(AvmApp.getInstance().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public void setCameraViewListener(ICameraViewListener iCameraViewListener) {
        mICameraViewListener = iCameraViewListener;
    }

    public void removeCameraViewListener() {
        mICameraViewListener = null;
    }

    //手动标记
    public void setManualCalibration() {
        KLog.i("setManualCalibration");
        Log.d("Cal", "setManualCalibration()");

        if (AvmService.JNI_IN_THREAD_FLAG) {
            callCalibrate(0);
        } else {
            ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
                bvavmJNI.bwStartCalibrate(0);
                BvAvmJNIHelper.getInstance().setCalibration(true);
            }, 100);
        }

        //        CustomToast.showToast(AvmApp.getInstance().getString(R.string.manual_calibration_success));
        info.setShowCaliDemo(true);
        mICameraViewListener.setCalibrationSelect(false);
        setCalibrateRunning(true);
        setRunning(true);
    }

    //自动标定
    public void setManualCalibration1() {
        KLog.i("setManualCalibration");
        Log.d("Cal", "setManualCalibration1()");
        if (AvmService.JNI_IN_THREAD_FLAG) {
            callCalibrate(1);
            info.setShowCaliView(true);
            ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
                info.setShowCaliView(false);
                mICameraViewListener.setCalibrationSelect(false);
            }, 5000);
        } else {
            info.setShowCaliView(true);
            ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
                int ret = bvavmJNI.bwStartCalibrate(1);
                info.setShowCaliView(false);
                BvAvmJNIHelper.getInstance().setCalibration(false);
                // ret 返回值
                // 0 成功
                // 2 后视图标定失败
                // 4 左视图标定失败
                // 8 右视图标定失败
                Log.d("Cal", "calibrate ret is " + ret);
                mICameraViewListener.setCalibrationSelect(false);
            }, 5000);
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.manual_calibration_success));
            setRunning(true);
        }
    }


    public void setCalibration1() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_MANUAL_FRONT);
        CustomToast.showToast("标定-前");
        setRunning(true);
    }

    public void setCalibration2() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_MANUAL_REAR);
        CustomToast.showToast("标定-后");
        setRunning(true);
    }

    public void setCalibration3() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_MANUAL_LEFT);
        CustomToast.showToast("标定-左");
        setRunning(true);
    }

    public void setCalibration4() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_MANUAL_RIGHT);
        CustomToast.showToast("标定-右");
        setRunning(true);
    }

    //自动标记
    public void setAutomaticCalibration() {
        setRunning(true);
        setCalibrateRunning(true);
        KLog.i("setAutomaticCalibration");
        //int value = bvavmJNI.bwStartCalibrate(1);
        //延迟一秒去获取这个value
        if (AvmService.JNI_IN_THREAD_FLAG) {
            info.setShowCaliView(true);
            info.setShowCaliBtnText("自动标定中,请勿关闭....");
            callCalibrate(1);
            ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
                if (isCaliStatus == 0) {
                    info.setShowCaliBtnText("标定成功,点击重启AVM");
                } else {
                    info.setShowCaliBtnText("标定失败! - " + isCaliStatus);
                }
                CameraViewModelHelper.getInstance().setAutomaticCalibration(isCaliStatus);
            }, 5000);
            mICameraViewListener.setCalibrationSelect(false);
        } else {
            info.setShowCaliView(true);
            info.setShowCaliBtnText("自动标定中,请勿关闭....");
            ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
                isCaliStatus = bvavmJNI.bwStartCalibrate(1);
                Log.d("Cal", "bwStartCalibrate() isCaliStatus = " + isCaliStatus);
                if (isCaliStatus == 0) {
                    info.setShowCaliBtnText("标定成功,点击重启AVM");
                } else {
                    info.setShowCaliBtnText("标定失败! - " + isCaliStatus);
                }
                BvAvmJNIHelper.getInstance().setCalibration(false);
                CameraViewModelHelper.getInstance().setAutomaticCalibration(isCaliStatus);

            }, 0);
            mICameraViewListener.setCalibrationSelect(false);
        }
    }

    private boolean isDIAGCalibration = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    //信号过来后，开始标定
    public void setStartCalibration() {

       /* if (!BvAvmJNIHelper.getInstance().isActive()){
            return;
        }*/
        if (isDIAGCalibration) {
            KLog.i("DIAG_31 正在标定中。。。。。");
            return;
        }
        KLog.i("DIAG_31 标定----setStartCalibration 准备标定 " + isDIAGCalibration);
//        ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
        isDIAGCalibration = true;

        KLog.i("DIAG_31 标定----setStartCalibration 进入正在标定中 " + isDIAGCalibration);
//        mHandler.postDelayed(() -> {
//            if (isCaliStatus != -1) {
//                KLog.i("标定 DIAG_31 app mHandler 结束-标定完成。  " + isCaliStatus);
//                mHandler.removeCallbacksAndMessages("token12345");
//            } else {
//                KLog.i("标定 DIAG_31 app mHandler 正在标定中。。。。  " + isCaliStatus);
//            }
//        }, "token12345", 300);
        KLog.i("DIAG_31 标定----setStartCalibration 进入正在标定中-bwStartCalibrate  " + isDIAGCalibration);
        byte[] arrBack = {0x00, 0x00, 0x00, 0x00};
        CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESP, 0, arrBack);

        isCaliStatus = bvavmJNI.bwStartCalibrate(1);
        KLog.i("标定 DIAG_31 000 app bwStartCalibrate 结束-标定完成。  " + isCaliStatus);
        if (isCaliStatus == 0) {
            try {
                Thread.sleep(2000);
                CanManager.getInstance().setIntProperty(NFS_SYNC,  0, 1);
                int nfs_sts = CanManager.getInstance().getIntStatus(NFS_SYNC_STATUS, 0);
                KLog.i("Read NFS STATUS is " + nfs_sts);
            } catch (InterruptedException exception) {
                KLog.e(exception.toString());
            }

        }
    }

    //收到请求，看是否标定成功
    public void calibrationBack() {
        Log.d("AVM", Log.getStackTraceString(new Throwable()));
        /*if (!BvAvmJNIHelper.getInstance().isActive()){
            return;
        }*/
        KLog.i("DIAG_31_ isCaliStatus: " + isCaliStatus);
       /* if (!isDIAGCalibration){
            KLog.i("DIAG_31_标定未开始isCaliStatus:calibrationBack ");
            return;
        }*/

        if (isCaliStatus == -1) {//未收到反馈
            byte[] arrBack = {0x02, 0x00,0x00,0x00};
            CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
            KLog.i("标定-DIAG_31 app 未收到反馈：isCaliStatus " + isCaliStatus);
        } else if (isCaliStatus == 0) {//成功
            //byte[] arrBack = {0x00, 0x02,0x00,0x00};
            try {
                CanManager.getInstance().setIntProperty(NFS_SYNC, 0, 1);
                int nfs_sts = CanManager.getInstance().getIntStatus(NFS_SYNC_STATUS, 0);
                KLog.i("标定 Read NFS STATUS is " + nfs_sts);
                Thread.sleep(500);
            } catch (InterruptedException exception) {
                KLog.e(exception.toString());
            }
            byte[] arrBack = {0x00, 0x00, 0x00, 0x00};
            //CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP, 0, arrBack);
            CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_success));
//            isCaliStatus = -1;
            isDIAGCalibration = false;
            KLog.i("标定-DIAG_31 app 标定成功：isCaliStatus " + isCaliStatus);
            DataManager.writeFault(DataConstant.Code.BD_SUCCESS);
            DataManager.writeFault(DataConstant.Code.SJ_SAVE_SUCCESS);
            calibrationInspect(1);
        } else {//标定失败
            //byte[] arrBack = {0x01, 0x02,0x00,0x00};
            byte[] arrBack = {0x00, 0x01, 0x00, 0x00};
            //CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP, 0, arrBack);
            CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
            isDIAGCalibration = false;
            KLog.i("标定-DIAG_31 app 标定失败：isCaliStatus " + isCaliStatus);
            DataManager.writeFault(DataConstant.Code.BD_FAIL);
            DataManager.writeFault(DataConstant.Code.SJ_SAVE_FAIL);
            calibrationInspect(0);
        }
    }

    /**
     * 标定流程第二部，标定检测
     */
    private void calibrationInspect(int value) {
        KLog.i("标定-0305 标定监测结果：value " + value);
        if (value == 1) {//标定成功
            byte[] arrBack = new byte[]{0x01, 0x00, 0x00, 0x00, 0x00};
            KLog.i("全景标定状态检查-成功: DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP" + Arrays.toString(arrBack));
            CanManager.getInstance().setByteArray(DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP, 0, arrBack);
        } else {//标定失败
            byte[] arrBack = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00};
            KLog.i("全景标定状态检查-失败: DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP" + Arrays.toString(arrBack));
            CanManager.getInstance().setByteArray(DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP, 0, arrBack);
        }
        byte[] calibrationValue = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        KLog.i("全景标定状态检查-摄像头数值： " + calibrationValue.length + "  value: " + Arrays.toString(calibrationValue));
        CanManager.getInstance().setByteArray(DIAG_22_030A_AVM_FRONT_CAMERA_PARA_RESP, 0, calibrationValue);
        CanManager.getInstance().setByteArray(DIAG_22_030B_AVM_REAR_CAMERA_PARA_RESP, 0, calibrationValue);
        CanManager.getInstance().setByteArray(DIAG_22_030C_AVM_LEFT_CAMERA_PARA_RESP, 0, calibrationValue);
        CanManager.getInstance().setByteArray(DIAG_22_030D_AVM_RIGHT_CAMERA_PARA_RESP, 0, calibrationValue);
    }

    public void calibrationBackError() {
        /*if (!BvAvmJNIHelper.getInstance().isActive()){
            return;
        }*/
//        if (isCaliStatus == -1){
//            KLog.i("calibrationBackError:标定未完成 ");
//            return;
//        }
        KLog.i("calibrationBackError: " + isCaliStatus);
        CameraViewModelHelper.getInstance().setAutomaticCalibration(isCaliStatus);
    }

    public void startTimer() {
        if (closeTimer == null)
            closeTimer = new CloseTimer(10 * 1000, 2000);
        closeTimer.cancel();
        closeTimer.start();
        setRunning(true);
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
            KLog.i("自定关闭标定页面");
            mICameraViewListener.setCalibrationSelect(false);

        }
    }

    public void onCliCKLayout(int type) {
        liveDataViewLayout.postValue(type);

    }

    //2D 上视角
    public void camera2dTop() {
        chick2DView = ViewSwitchManager.CAMERA_2_D_TOP;
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_FRONT_UNDISTORT);
        bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_TOP);
        setRunning(true);
        KLog.i("上视角：" + CameraGLSurfaceView.getCameraDirection());
    }

    //2D 左视角
    public void camera2dLift() {
        setRunning(true);
        //if(!TextUtils.isEmpty(chick2DView)&&chick2DView.equals(ViewSwitchManager.CAMERA_2_D_RIGHT)){
        //左右视图
        CameraGLSurfaceView.setAngleOfView(AvmRuntime.self().isRearGearSts() ? bvavmJNI.BW_LEFT_RIGHT_BACK : bvavmJNI.BW_LEFT_RIGHT_FRONT);
        chick2DView = ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT;
        // liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT);
       /* }else{
            //左视图
            CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_LEFT;
            chick2DView = ViewSwitchManager.CAMERA_2_D_LIFT;
            liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT);
        }*/
        KLog.i("左边视角：" + CameraGLSurfaceView.getCameraDirection());


    }

    //2D 下视角
    public void camera2dBottom() {
        if (AvmService.JNI_IN_THREAD_FLAG) {
            reset3D();
        } else {
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        }

        setRunning(true);
        chick2DView = ViewSwitchManager.CAMERA_2_D_BOTTOM;
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_2D_REAR_UNDISTORT);
        bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_BOTTOM);
        KLog.i("下视角：" + CameraGLSurfaceView.getCameraDirection());

        //判断轨迹线有没有打开，打开就显示2D轨迹线


    }

    //2D 右视角
    public void camera2dRight() {
        if (AvmService.JNI_IN_THREAD_FLAG) {
            reset3D();
        } else {
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        }
        setRunning(true);
        //if(!TextUtils.isEmpty(chick2DView) &&chick2DView.equals(ViewSwitchManager.CAMERA_2_D_LIFT)){
        //左右视图
        CameraGLSurfaceView.setAngleOfView(AvmRuntime.self().isRearGearSts() ? bvavmJNI.BW_LEFT_RIGHT_BACK : bvavmJNI.BW_LEFT_RIGHT_FRONT);//  改成前轮视角
        chick2DView = ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT;
        // liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_RIGHT);
       /* }else {
            chick2DView = ViewSwitchManager.CAMERA_2_D_RIGHT;
            CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_RIGHT;
            liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_RIGHT);
        }*/
        KLog.i("右视角：" + CameraGLSurfaceView.getCameraDirection());
    }

    //3D 左前
    public void camera3dLeftFront() {
        if (AvmService.JNI_IN_THREAD_FLAG) {
            reset3D();
        } else {
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        }
        setRunning(true);
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_FRONT_3D);
        liveDataCamera3DTopUI.postValue(ViewSwitchManager.CAMERA_3_D_LEFT_FRONT);
    }

    //3D 右前
    public void camera3dRightFront() {
        if (AvmService.JNI_IN_THREAD_FLAG) {
            reset3D();
        } else {
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        }
        setRunning(true);
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_RIGHT_FRONT_3D);
        liveDataCamera3DTopUI.postValue(ViewSwitchManager.CAMERA_3_D_RIGHT_FRONT);

    }

    //3D 左后
    public void camera3dLeftRear() {
        if (AvmService.JNI_IN_THREAD_FLAG) {
            reset3D();
        } else {
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        }
        setRunning(true);
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_LEFT_REAR_3D);
        liveDataCamera3DTopUI.postValue(ViewSwitchManager.CAMERA_3_D_LEFT_REAR);
    }

    //3D 左后
    public void camera3dRightRear() {
        if (AvmService.JNI_IN_THREAD_FLAG) {
            reset3D();
        } else {
            BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        }
        setRunning(true);
        CameraGLSurfaceView.setAngleOfView(bvavmJNI.BW_RIGHT_REAR_3D);
        liveDataCamera3DTopUI.postValue(ViewSwitchManager.CAMERA_3_D_RIGHT_REAR);
    }

    //雷达提示音
    public void cameraRadarSound() {
        setRunning(true);
        liveDataRadarSound.postValue("");
    }

    //2D车模点击事件
    public void camera2D() {
        setRunning(true);
        liveData2DSound.postValue("");
    }

    //3D车模点击事件
    public void camera3D() {
        setRunning(true);
        liveData3DSound.postValue("");
    }
    // 手动标定接口测试

    /**
     * 1）开启手动标定：bwStartCalibrate（0）
     * 2）选择摄像头：avmRender(BW_2D_MANUAL_FRONT)
     * <p>
     * 3) 选择点：bwSetIRKeyData(SCANCODE_IR_POINT1)
     * 4) 移动：bwSetIRKeyData(SCANCODE_IR_UP)
     * 5）确认：bwSetIRKeyData(SCANCODE_IR_OK)
     * 6）取消：bwSetIRKeyData(SCANCODE_IR_EXIT)
     */

    public void bwSetIRKeyPOINT() {

        bvavmJNI.bwSetIRKeyData(SCANCODE_IR_POINT1);
        CustomToast.showToast("标定-bwSetIRKeyPOINT");

    }

    public void bwSetIRKeyUp() {
        bvavmJNI.bwSetIRKeyData(SCANCODE_IR_POINT1);
        CustomToast.showToast("标定-bwSetIRKeyUp");

    }

    public void bwSetIRKeyOK() {
        bvavmJNI.bwSetIRKeyData(SCANCODE_IR_POINT1);
        CustomToast.showToast("标定-bwSetIRKeyOK");

    }

    public void bwSetIRKeyExit() {
        CustomToast.showToast("标定-bwSetIRKeyExit");
        bvavmJNI.bwSetIRKeyData(SCANCODE_IR_POINT1);
        BvAvmJNIHelper.getInstance().setCalibration(false);
        CameraViewModelHelper.getInstance().setCalibrateRunning(false);
        info.setShowCaliDemo(false);
    }

    /**
     * 关闭手动标定
     */
    public void bwClose() {
        setRunning(true);
        setCalibrateRunning(false);
    }

    /**
     * 重启app
     */
    public void bwStartActivity() {

        info.setShowCaliView(false);
        if (isCaliStatus != 0) {
            CustomToast.showToast("标定-bwSetIRKeyPOINT-失败");
            return;
        }
        
        System.exit(0);
    }

    public void callCalibrate(int value) {
        KLog.i("标定 callCalibrate().");
        if (threadHandler.hasMessages(MSG_CALIBRATING)) {
            KLog.e("标定 now is in calibrating.");
        } else {
            Message message = Message.obtain();
            message.what = MSG_CALIBRATE;
            message.arg1 = value;
            threadHandler.sendMessage(message);
        }
    }

    public void callCalibrateResp() {
        KLog.i("标定 callCalibrateResp().");
        Message message = Message.obtain();
        message.what = MSG_CALIBRATE_RESP;
        threadHandler.sendMessage(message);
    }

    private int mDirection = -1;
    public void turnLampChange(int direction, long delay) {
        threadHandler.removeMessages(MSG_TURN_LAMP_CHANGE);
        Message message = Message.obtain();
        message.what = MSG_TURN_LAMP_CHANGE;
        message.arg1 = direction;
        threadHandler.sendMessageDelayed(message, delay);
        mDirection = direction;
    }

    public void turnResetChange() {
        if(mDirection == 0) {
            KLog.d("lightChange 重置退出逻辑");
            threadHandler.removeMessages(MSG_TURN_LAMP_CHANGE);
        }
    }

    public void simWheelSpeed() {
        Message message = Message.obtain();
        message.what = MSG_SIM_WHEEL_SPEED;
        message.arg1 = 1;
        message.arg2 = 0;
        threadHandler.sendMessage(message);
    }

    public void createCamera() {
        threadHandler.sendEmptyMessage(MSG_CREATE_CAMERA);
    }

    public void deleteCamera() {
        threadHandler.sendEmptyMessage(MSG_DELETE_CAMERA);
    }

    public void reset3D() {
        threadHandler.sendEmptyMessage(MSG_FREE_3D);
    }

    public void setWheelDirection() {
        threadHandler.sendEmptyMessage(MSG_WHEEL_DIRECTION);
    }

    public void setWheelSpeed(Object object) {
        Message message = Message.obtain();
        message.what = MSG_WHEEL_SPEED;
        message.obj = object;
        threadHandler.sendMessage(message);
    }

    public void setDoorStatus(int vehicleId, int value) {
        Message message = Message.obtain();
        message.what = MSG_DOOR_STS;
        message.arg1 = vehicleId;
        message.arg2 = value;
        threadHandler.sendMessage(message);
    }

    public void setLightModel(int vehicleId, int value) {
        Message message = Message.obtain();
        message.what = MSG_LIGHT_MODEL;
        message.arg1 = vehicleId;
        message.arg2 = value;
        threadHandler.sendMessage(message);
    }

    public void setRadarActive(int value) {
        Message message = Message.obtain();
        message.what = MSG_RADAR_ACTIVE;
        message.arg1 = value;
        threadHandler.sendMessage(message);
    }

    public void setWheelAngle(Object value) {
        Message message = Message.obtain();
        message.what = MSG_VHEEL_ANGLE;
        message.obj = value;
        threadHandler.sendMessage(message);
    }

    public void setTrajLineEnable(byte value) {
        Message message = Message.obtain();
        message.what = MSG_SET_TRAJLINE_ENABLE;
        message.arg1 = value;
        threadHandler.sendMessage(message);
    }

    public void setRadarExit(int value) {
        Message message = Message.obtain();
        message.what = MSG_RADAR_EXIT;
        message.arg1 = value;
        threadHandler.sendMessage(message);
    }

    public void updateTrajLineStatus(int value) {
        Message message = Message.obtain();
        message.what = MSG_UPDATE_TRAJ_LINE_STS;
        message.arg1 = value;
        threadHandler.sendMessage(message);
    }

    public void bwClearCarBottomImage() {
        Message message = Message.obtain();
        message.what = MSG_CLEAR_BOTTOM;
        threadHandler.sendMessage(message);
    }

    public void setUndistortLevel() {
        Message message = Message.obtain();
        message.what = MSG_SET_UNDISTORT_LEVEL;
        threadHandler.sendMessage(message);
    }

    public void setBwSetRVCStatus(int status) {
        Message message = Message.obtain();
        message.what = MSG_SET_BWSTATUS;
        message.arg1 = status;
        threadHandler.sendMessage(message);
    }

    /**
     * 测试透明底盘传入假数据
     */
  /*  public void startTestTimer() {
        testTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                bvavmJNI.bwSetFourWheelSpeed(50, 50, 50, 50);
            }
        };
        testTimer.scheduleAtFixedRate(timerTask,500,500);
    }*/

   /* public void cancelTestTimer(){
        if(testTimer!=null){
            testTimer.cancel();
        }
    }*/
}
