package com.autochips.avm.viewmode;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030A_AVM_FRONT_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030B_AVM_REAR_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030C_AVM_LEFT_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_030D_AVM_RIGHT_CAMERA_PARA_RESP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP;
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
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.em.ViewType;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.info.CameraInfo;
import com.autochips.avm.ui.view.CameraGLSurfaceView;
import com.autochips.avm.ui.view.CameraView;
import com.autochips.avm.util.CustomToast;
import com.avm.framwork.constant.CameraContracts;
import com.avm.framwork.helper.ThreadPoolUtil;
import com.avm.framwork.manager.CanManager;
import com.avm.framwork.manager.ViewSwitchManager;

import java.util.Arrays;
import java.util.Timer;

import me.goldze.mvvmhabit.utils.KLog;

public class CameraViewModel extends BaseCameraViewModel {
    private CameraView.ICameraViewListener mICameraViewListener;
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
    private ViewType mHisModel;

    private int isCaliStatus = -1;


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
    }

    public ViewType getmHisModel() {
        return mHisModel;
    }

    public void setmHisModel(ViewType mHisModel) {
        this.mHisModel = mHisModel;
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
        setRunning(false);
        CameraViewModelHelper.getInstance().setRadarActiveTow(true);
        CameraViewModelHelper.getInstance().dismissView(false, 0, "click");
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
    }


    /**
     * 是否在操作
     *
     * @param running
     */
    public void setRunning(boolean running) {
        CameraViewModelHelper.getInstance().setRunning(running);
    }


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
    private  boolean isOpenSettingDialog = false;
    public void openSettingDialog() {
        KLog.i("openSettingDialog");
        mICameraViewListener.setBtnSettingSelect(isOpenSettingDialog = !isOpenSettingDialog);
        setRunning(true);
    }

    /**
     * 打开后视镜弹窗
     */
    private  boolean isOpenBackMirrorDialog = false;
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

    public void setCameraViewListener(CameraView.ICameraViewListener iCameraViewListener) {
        mICameraViewListener = iCameraViewListener;
    }

    public void removeCameraViewListener() {
        mICameraViewListener = null;
    }

    //手动标记
    public void setManualCalibration() {
        KLog.i("setManualCalibration");
        ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
            bvavmJNI.bwStartCalibrate(0);
            BvAvmJNIHelper.getInstance().setCalibration(true);
        }, 100);
//        CustomToast.showToast(AvmApp.getInstance().getString(R.string.manual_calibration_success));
        info.setShowCaliDemo(true);
        mICameraViewListener.setCalibrationSelect(false);
        setCalibrateRunning(true);
        setRunning(true);
    }

    //自动标定
    public void setManualCalibration1() {
        KLog.i("setManualCalibration");
        info.setShowCaliView(true);
        ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
            bvavmJNI.bwStartCalibrate(1);
            info.setShowCaliView(false);
            BvAvmJNIHelper.getInstance().setCalibration(false);
        }, 5000);
        CustomToast.showToast(AvmApp.getInstance().getString(R.string.manual_calibration_success));
        setRunning(true);
    }


    public void setCalibration1() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_MANUAL_FRONT;
        CustomToast.showToast("标定-前");
        setRunning(true);
    }

    public void setCalibration2() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_MANUAL_REAR;
        CustomToast.showToast("标定-后");
        setRunning(true);
    }

    public void setCalibration3() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_MANUAL_LEFT;
        CustomToast.showToast("标定-左");
        setRunning(true);
    }

    public void setCalibration4() {
        KLog.i("setManualCalibration");
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_MANUAL_RIGHT;
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
        info.setShowCaliView(true);
        info.setShowCaliBtnText("自动标定中,请勿关闭....");
        ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
            isCaliStatus = bvavmJNI.bwStartCalibrate(1);
            if (isCaliStatus == 0) {
                info.setShowCaliBtnText("标定成功,点击重启AVM");
            } else {
                info.setShowCaliBtnText("标定失败!");
            }
            CameraViewModelHelper.getInstance().setCalibrateRunning(false);
            BvAvmJNIHelper.getInstance().setCalibration(false);
            CameraViewModelHelper.getInstance().setAutomaticCalibration(isCaliStatus);

        }, 0);
        mICameraViewListener.setCalibrationSelect(false);
    }

    private  boolean isDIAGCalibration = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    //信号过来后，开始标定
    public void setStartCalibration() {

       /* if (!BvAvmJNIHelper.getInstance().isActive()){
            return;
        }*/
        if (isDIAGCalibration){
            KLog.i("DIAG_31 正在标定中。。。。。");
            return;
        }
       KLog.i("DIAG_31 标定----setStartCalibration 准备标定 "+isDIAGCalibration);
//        ThreadPoolUtil.getInstance().runOnSubThreadDelayed(() -> {
            isDIAGCalibration = true;

      KLog.i("DIAG_31 标定----setStartCalibration 进入正在标定中 "+isDIAGCalibration);
      mHandler.postDelayed(()->{
        if (isCaliStatus != -1){
          KLog.i( "标定 DIAG_31 app mHandler 结束-标定完成。  "+isCaliStatus);
          mHandler.removeCallbacksAndMessages("token12345");
        }else {
          KLog.i( "标定 DIAG_31 app mHandler 正在标定中。。。。  "+isCaliStatus);
        }
      },"token12345",300);
      KLog.i("DIAG_31 标定----setStartCalibration 进入正在标定中-bwStartCalibrate  "+isDIAGCalibration);
            byte[] arrBack = {0x00, 0x00,0x00,0x00};
            CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESP, 0, arrBack);
            isCaliStatus = bvavmJNI.bwStartCalibrate(1);
//        }, 0);
      KLog.i( "标定 DIAG_31 app bwStartCalibrate 结束-标定完成。  "+isCaliStatus);

    }

    //收到请求，看是否标定成功
    public void calibrationBack() {
        /*if (!BvAvmJNIHelper.getInstance().isActive()){
            return;
        }*/
        KLog.i("DIAG_31_ isCaliStatus: "+isCaliStatus);
       /* if (!isDIAGCalibration){
            KLog.i("DIAG_31_标定未开始isCaliStatus:calibrationBack ");
            return;
        }*/

        if (isCaliStatus == -1) {//未收到反馈
            //byte[] arrBack = {0x02, 0x02,0x00,0x00};
            //CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP, 0, arrBack);
            KLog.i( "标定-DIAG_31 app 未收到反馈：isCaliStatus "+isCaliStatus);
        } else if (isCaliStatus == 0) {//成功
            //byte[] arrBack = {0x00, 0x02,0x00,0x00};
            byte[] arrBack = {0x00, 0x00,0x00,0x00};
            //CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP, 0, arrBack);
            CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_success));
            isCaliStatus = -1;
            isDIAGCalibration = false;
            DataManager.writeFault(DataConstant.Code.BD_SUCCESS);
            DataManager.writeFault(DataConstant.Code.SJ_SAVE_SUCCESS);
            KLog.i( "标定-DIAG_31 app 标定成功：isCaliStatus "+isCaliStatus);
            calibrationInspect(1);
        }else{//标定失败
            //byte[] arrBack = {0x01, 0x02,0x00,0x00};
            byte[] arrBack = {0x00, 0x01,0x00,0x00};
            //CanManager.getInstance().setByteArray(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_RESP, 0, arrBack);
            CanManager.getInstance().setByteArray(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_RESP, 0, arrBack);
            isDIAGCalibration = false;
            DataManager.writeFault(DataConstant.Code.BD_FAIL);
            DataManager.writeFault(DataConstant.Code.SJ_SAVE_FAIL);
            KLog.i( "标定-DIAG_31 app 标定失败：isCaliStatus "+isCaliStatus);
            calibrationInspect(0);
        }
    }

    /**
     * 标定流程第二部，标定检测
     */
    private void calibrationInspect(int value){
        KLog.i( "标定-0305 标定监测结果：value "+value);
        if(value==1){//标定成功
            byte[] arrBack = new byte[]{0x01, 0x00, 0x00, 0x00,0x00};
            KLog.i("全景标定状态检查-成功: DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP"+Arrays.toString(arrBack) );
            CanManager.getInstance().setByteArray(DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP, 0, arrBack);
        }else{//标定失败
            byte[] arrBack = new byte[]{0x00, 0x00, 0x00, 0x00,0x00};
            KLog.i("全景标定状态检查-失败: DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP"+Arrays.toString(arrBack) );
            CanManager.getInstance().setByteArray(DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_RESP, 0, arrBack);
        }
       byte[] calibrationValue = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00
        ,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        KLog.i("全景标定状态检查-摄像头数值： "+calibrationValue.length+"  value: "+Arrays.toString(calibrationValue) );
        CanManager.getInstance().setByteArray(DIAG_22_030A_AVM_FRONT_CAMERA_PARA_RESP, 0, calibrationValue);
        CanManager.getInstance().setByteArray(DIAG_22_030B_AVM_REAR_CAMERA_PARA_RESP, 0, calibrationValue);
        CanManager.getInstance().setByteArray(DIAG_22_030C_AVM_LEFT_CAMERA_PARA_RESP, 0, calibrationValue);
        CanManager.getInstance().setByteArray(DIAG_22_030D_AVM_RIGHT_CAMERA_PARA_RESP, 0, calibrationValue);
    }

    public void calibrationBackError(){
        /*if (!BvAvmJNIHelper.getInstance().isActive()){
            return;
        }*/
//        if (isCaliStatus == -1){
//            KLog.i("calibrationBackError:标定未完成 ");
//            return;
//        }
        KLog.i("calibrationBackError: "+isCaliStatus);
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
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_FRONT_UNDISTORT;
        bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_TOP);
        setRunning(true);
        KLog.i("上视角：" + CameraGLSurfaceView.sCameraDirection);
    }

    //2D 左视角
    public void camera2dLift() {
        setRunning(true);
        //if(!TextUtils.isEmpty(chick2DView)&&chick2DView.equals(ViewSwitchManager.CAMERA_2_D_RIGHT)){
        //左右视图
        CameraGLSurfaceView.sCameraDirection = mHisModel ==  ViewType.ReverseIn ? bvavmJNI.BW_LEFT_RIGHT_BACK : bvavmJNI.BW_LEFT_RIGHT_FRONT;
        chick2DView = ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT;
        // liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT);
       /* }else{
            //左视图
            CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_LEFT;
            chick2DView = ViewSwitchManager.CAMERA_2_D_LIFT;
            liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT);
        }*/
        KLog.i("左边视角：" + CameraGLSurfaceView.sCameraDirection);


    }

    //2D 下视角
    public void camera2dBottom() {
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        setRunning(true);
        chick2DView = ViewSwitchManager.CAMERA_2_D_BOTTOM;
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_REAR_UNDISTORT;
        bvavmJNI.bwSetUndistortLevel(CameraContracts.UNDISTORTLEVEL, CameraContracts.UNDISTORTLEVEL);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_BOTTOM);
        KLog.i("下视角：" + CameraGLSurfaceView.sCameraDirection);

        //判断轨迹线有没有打开，打开就显示2D轨迹线


    }

    //2D 右视角
    public void camera2dRight() {
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);
        setRunning(true);
        //if(!TextUtils.isEmpty(chick2DView) &&chick2DView.equals(ViewSwitchManager.CAMERA_2_D_LIFT)){
        //左右视图
        CameraGLSurfaceView.sCameraDirection =mHisModel == ViewType.ReverseIn ? bvavmJNI.BW_LEFT_RIGHT_BACK : bvavmJNI.BW_LEFT_RIGHT_FRONT;//  改成前轮视角
        chick2DView = ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT;
        // liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_LIFT_RIGHT);
        liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_RIGHT);
       /* }else {
            chick2DView = ViewSwitchManager.CAMERA_2_D_RIGHT;
            CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_2D_RIGHT;
            liveDataCamera2DTopUI.postValue(ViewSwitchManager.CAMERA_2_D_RIGHT);
        }*/
        KLog.i("右视角：" + CameraGLSurfaceView.sCameraDirection);
    }

    //3D 左前
    public void camera3dLeftFront() {
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);//复位3D
        setRunning(true);
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_LEFT_FRONT_3D;
        liveDataCamera3DTopUI.postValue(ViewSwitchManager.CAMERA_3_D_LEFT_FRONT);
    }

    //3D 右前
    public void camera3dRightFront() {
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);//复位3D
        setRunning(true);
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_RIGHT_FRONT_3D;
        liveDataCamera3DTopUI.postValue(ViewSwitchManager.CAMERA_3_D_RIGHT_FRONT);

    }

    //3D 左后
    public void camera3dLeftRear() {
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);//复位3D
        setRunning(true);
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_LEFT_REAR_3D;
        liveDataCamera3DTopUI.postValue(ViewSwitchManager.CAMERA_3_D_LEFT_REAR);
    }

    //3D 左后
    public void camera3dRightRear() {
        BvAvmJNIHelper.getInstance().bwSet3DfreeFlag(0);//复位3D
        setRunning(true);
        CameraGLSurfaceView.sCameraDirection = bvavmJNI.BW_RIGHT_REAR_3D;
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
