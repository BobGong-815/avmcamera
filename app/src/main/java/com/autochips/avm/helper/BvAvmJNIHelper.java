package com.autochips.avm.helper;

import android.app.UiModeManager;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.BuildConfig;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.ui.view.CameraView;
import com.autochips.avm.util.SystemProperties;
import com.avm.framwork.helper.ThreadPoolUtil;
import com.avm.framwork.manager.CanManager;

import gxa.car.power.manager.CarPowerManager;
import me.goldze.mvvmhabit.http.interceptor.logging.Logger;
import me.goldze.mvvmhabit.utils.KLog;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_UINM_TURN_LIGHT_SW_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_HIGH_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_DRIVERDOORAJARST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_VCU_GEAR_LVL_DISP;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;

import java.util.Arrays;

/**
 * 视频接口管理类
 */
public class BvAvmJNIHelper {

    //public static int cameraType =bvavmJNI.PROJ_AY5_ID ;// 车型选配，分不同的车型进行打包

    private static BvAvmJNIHelper instance;
    private boolean isActive = false; // 是否激活 或打开AVM
    public int avmInit = -1;
    private boolean mIsOpenCameraStatus = true;//摄像头打开失败，默认是正常打开
    public static boolean turnIsPgear = false;//第一次初始化进入时是否是转向跟p挡
    private  long  bwInitValue = -1;
    private boolean isCaninitSucess = false;//can信号初始化成功
    private  boolean isCalibration = false;
    private int openCameraTime = 0;//打开摄像头次数
    public static  boolean isAvmDeInit = false;
    private View.OnClickListener listener ;
    private byte[] syncObj = new byte[0];
    public boolean isCloseingCamrea = false;//是否正在关闭摄像头
    public static BvAvmJNIHelper getInstance() {
        synchronized (BvAvmJNIHelper.class) {
            if (instance == null) {
                instance = new BvAvmJNIHelper();
            }
        }
        return instance;
    }

    public static boolean isIsAvmDeInit() {
        return isAvmDeInit;
    }

    public void setCallback(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void onClick(){
        if (listener  != null){
            listener.onClick(null);
        }
    }
    public View.OnClickListener getListener() {
        return listener;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    public int avmInit(Context context) {
        if (isActive)return 0;
        isActive = true;
       int res = bvavmJNI.avmInit();
        DataManager.writeFault(res == 1 ? DataConstant.Code.INIT_SUCCESS : DataConstant.Code.INIT_FAIL);
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        int uiMode = uiModeManager.getNightMode();
        switch (uiMode) {
            case UiModeManager.MODE_NIGHT_YES:
                KLog.e("黑夜模式");
                bvavmJNI.bwSetIsDay(0);
                break;
            case UiModeManager.MODE_NIGHT_NO:
                KLog.e("白天模式");
                bvavmJNI.bwSetIsDay(1);
                break;
        }

        CarPowerManager carPowerManager = CarPowerManager.getInstance(context, null);
        if(carPowerManager.isServiceConnected()) {
            isCaninitSucess = true;
            int gear = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);

            //        int vcuGearValue = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);//挡位
            int doorValue = CanManager.getInstance().getIntStatus(CLUSTER_LCK_DRIVERDOORAJARST, 0);//车门
            int lightValue = CanManager.getInstance().getIntStatus(BCM_HIGH_BEAM_STATUS, 0);//灯光
            int turnVaule = CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, 0);
            //        CameraViewModelHelper.getInstance().reverse(vcuGearValue);
            CameraViewModelHelper.getInstance().doorStatus((Integer) doorValue);
            CameraViewModelHelper.getInstance().showLight3DModel(BCM_HIGH_BEAM_STATUS, (Integer) lightValue);
            if(AvmApp.mAvmRvcState != 1 && gear != 3) {
                if(turnVaule == 1 || turnVaule == 2){
                    if(gear == 4) {
                        //判断是否要立即退出
                        turnIsPgear = true;
                    }
                    Log.i("BvAvmJNIHelper","turnIsPgear:"+turnIsPgear);
                    mHandler.post(() -> CameraViewModelHelper.getInstance().turnActive(turnVaule,false));
                }
            }
            bwSetTrajLineStatus(gear);
        }else {
            Log.i("BvAvmJNIHelper","car is not connect");
        }
        //setIndexTab();
//        bvavmJNI.bwNotifyRVC(0);
        isAvmDeInit = true;
        return res;
    }

    public void initCanset(){
        Log.e("initCanset"," initCanset 版本:"+ BuildConfig.VERSION_NAME +" isAvmDeInit:"+isAvmDeInit +" isCaninitSucess:"+isCaninitSucess);
        if(isAvmDeInit && !isCaninitSucess){
            isCaninitSucess = true;
            int gear = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);
            int doorValue = CanManager.getInstance().getIntStatus(CLUSTER_LCK_DRIVERDOORAJARST, 0);//车门
            int lightValue = CanManager.getInstance().getIntStatus(BCM_HIGH_BEAM_STATUS, 0);//灯光
            CameraViewModelHelper.getInstance().doorStatus((Integer) doorValue);
            CameraViewModelHelper.getInstance().showLight3DModel(BCM_HIGH_BEAM_STATUS, (Integer) lightValue);
            bwSetTrajLineStatus(gear);
        }
    }

    ///*获取摄像头状态，camID表示摄像头ID:0是前摄像头，1是后摄像头，2是左摄像头，3是右摄像头，返回值:0表示图像正常，-1表示无图像*/
    public int bwGetCamerastatus(int cType){
        int status = bvavmJNI.bwGetCameraStatus(cType);
        KLog.e(" bwGetCamerastatus cType: " + cType + " status:"+status);
        return status;
    }

    private void setIndexTab(){
        int position = SystemProperties.getInt("settingRadarActivatedPanorama", -1);
        KLog.e(position+" setIndexTab 设置透明底盘-初始化后调用: " + position);
        if (position == -1)return;
        if (position == 0) {
            bvavmJNI.bwSetCarBottomStatus((byte) 0);
            bvavmJNI.bwSetCarTransparency( 1f);
        }else if(position == 1){
            bvavmJNI.bwSetCarBottomStatus((byte) 1);
            bvavmJNI.bwSetCarTransparency(0.3f);
        } else if(position == 2){
            bvavmJNI.bwSetCarBottomStatus((byte) 1);
            bvavmJNI.bwSetCarTransparency( 0.15f);
        }else {
            bvavmJNI.bwSetCarBottomStatus((byte) 1);
            bvavmJNI.bwSetCarTransparency(0.05f);
        }
    }



    public void avmDeInit() {
        isActive = false;
        mHandler.postDelayed(()->{
            int res =  bvavmJNI.avmDeInit();
            KLog.d("释放摄像头 avmDeInit res="+res);
            bwDeleteCamera();
        },500);

    }



    public void setCalibration(boolean calibration) {
        isCalibration = calibration;
    }

    public int avmRender2(int sCameraDirection) {
        if (!isActive)
            return 0;
      if (isCalibration) {
          return  bvavmJNI.avmRender(sCameraDirection);
      }
        return bvavmJNI.avmRender2(sCameraDirection);
    }

    public boolean isActive() {
        return isActive;
    }

    public long bwCreateCamera(String path, String met) {
        synchronized (syncObj) {
            //      if (!isActive)
            //        return 0;
            if (bwInitValue != -1 && bwInitValue != 0)
                return bwInitValue;
            KLog.d("创建 bwCreateCamera：" + bwInitValue);
            bwInitValue = bvavmJNI.bwCreateCamera(path, met);
            //        if(openCameraTime < 5 && bwInitValue == 0) {
            //            openCameraTime += 1;
            //            bwCreateCamera(path,met);
            //            KLog.d("reload bwCreateCamera："+openCameraTime);
            //        }else {
            //            if(openCameraTime == 5) {
            //                KLog.d("bwCreateCamera mIsOpenCameraStatus:" + mIsOpenCameraStatus);
            //                mIsOpenCameraStatus = false;
            //                mHandler.post(()->{
            //                    AvmApp.getInstance().getCameraView().openCameraError(true);
            //                });
            //                openCameraTime = 0;
            //            }else {
            //                mHandler.post(()->{
            //                    AvmApp.getInstance().getCameraView().openCameraError(false);
            //                });
            //            }
            //        }
            if (bwInitValue != 0) {
                isCloseingCamrea = false;
            }
            //isAvmDeInit = true;
            return bwInitValue;
        }
    }

    public boolean getOpenCameraStatus(){
        return mIsOpenCameraStatus;
    }

    public long bwCreateCameraShow(String path, String met) {
        KLog.d("bwCreateCamera bwInitValue :"+bwInitValue);
        if (bwInitValue !=-1 && bwInitValue != 0) {
            return bwInitValue;
        }
        KLog.d("bwCreateCamera create");
        return bwInitValue = bvavmJNI.bwCreateCamera(path, met);
    }

    public void bwDeleteCamera() {
        synchronized (syncObj) {
            KLog.d(bwInitValue + " bwDeleteCamera释放：" + bwInitValue);
            if (bwInitValue == -1 || bwInitValue == 0) {
                return;
            }
            isCloseingCamrea = true;
            KLog.d("bwDeleteCamera正在释放");
            //isAvmDeInit = false;
            bvavmJNI.bwDeleteCamera(bwInitValue);
            bwInitValue = 0;
            isCloseingCamrea = false;
            KLog.d("bwDeleteCamera释放摄像头完成：");
            return;
        }
    }

    public void bwSetProjID(int cameraType){
        KLog.d("bwSetProjID cameraType："+cameraType);
        bvavmJNI.bwSetProjID(cameraType);
    }

    public long camreaStatus(){
        return bwInitValue;
    }

    public void bwSet3DfreeFlag(int stat){
        bvavmJNI.bwSet3DfreeFlag(stat);
    }



    public int bwSetCarDoorStatus(int[] doors) {
        return bvavmJNI.bwSetCarDoorStatus(doors);
    }


    /*1 设置是否处于D档状态，1为是，0为否*/
//    public static native int bwSetCarIsDgear(int flag);
//    /*2 设置轨迹线开关，0是关闭，1是打开*/
//    public static native int bwSetTrajLineStatus(byte flag);
//    /*3 设置车辆是否处于倒车状态，1为倒车，0为静止或者前进*/
//    public static native int bwSetCarIsBack(byte flag);
    public  void  bwSetTrajLineStatus(int gear){
        // 轨迹线需要 bwSetTrajLineStatus(byte flag); 开启
        if (gear == 3){// R档
            bvavmJNI.bwSetCarIsDgear(0);
            bvavmJNI.bwSetCarIsBack((byte) 1);
        }else  {
            bvavmJNI.bwSetCarIsDgear(1);
            bvavmJNI.bwSetCarIsBack((byte) 0);
        }
    }


  public boolean isCamera2Device() {
    CameraManager camMgr = (CameraManager) AvmApp.getInstance().getSystemService(Context.CAMERA_SERVICE);
    boolean camera2Dev = true;
    try {
      String[] cameraIds = camMgr.getCameraIdList();

      if (cameraIds.length != 0 ) {
        KLog.d("摄像头："+ Arrays.toString(cameraIds));
        for (String id : cameraIds) {
          CameraCharacteristics characteristics = camMgr.getCameraCharacteristics(id);
          int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
          int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
          if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY &&
            facing == LENS_FACING_BACK) {
            camera2Dev =  false;
          }
        }
      }
    } catch (CameraAccessException e) {
      e.printStackTrace();
      KLog.e(" error摄像头："+e.getMessage());
      camera2Dev = false;
    }
    return camera2Dev;
  }

}
