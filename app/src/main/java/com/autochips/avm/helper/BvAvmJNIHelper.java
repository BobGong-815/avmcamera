package com.autochips.avm.helper;

import android.app.UiModeManager;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.util.SystemProperties;
import com.avm.framwork.manager.CanManager;

import me.goldze.mvvmhabit.utils.KLog;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_HIGH_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_DRIVERDOORAJARST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_VCU_GEAR_LVL_DISP;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;

import java.util.Arrays;

/**
 * 视频接口管理类
 */
public class BvAvmJNIHelper {

    public static int CAMERA_TYPE = bvavmJNI.PROJ_AY5_T_ID; // 车型选配，分不同的车型进行打包

    private static BvAvmJNIHelper instance;
    private boolean isActive = false; // 是否激活 或打开AVM

    private long bwInitValue = -1;
    private byte[] syncObj = new byte[0];
    private boolean isCalibration = false;

    public static boolean isAvmDeInit = false;
    private View.OnClickListener listener;

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

    public void onClick() {
        if (listener != null) {
            listener.onClick(null);
        }
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public int getCameraType() { return CAMERA_TYPE; }

    public void bwSetProjectID(int type){
        KLog.i("setProjectID ...............  " + type);
        synchronized (syncObj) {
            CAMERA_TYPE = type;
            bvavmJNI.bwSetProjID(CAMERA_TYPE);
        }
    }

    public int avmInit(Context context) {
        synchronized (syncObj) {
            if (isActive) return 0;
            isActive = true;
            int res = bvavmJNI.avmInit();
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

            int gear = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);

//        int vcuGearValue = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);//挡位
            int doorValue = CanManager.getInstance().getIntStatus(CLUSTER_LCK_DRIVERDOORAJARST, 0);//车门
            int lightValue = CanManager.getInstance().getIntStatus(BCM_HIGH_BEAM_STATUS, 0);//灯光

//        CameraViewModelHelper.getInstance().reverse(vcuGearValue);
            CameraViewModelHelper.getInstance().doorStatus(-1, (Integer) doorValue);
            CameraViewModelHelper.getInstance().showLight3DModel(BCM_HIGH_BEAM_STATUS, (Integer) lightValue);
            updateTrajLineStatus(gear);
            updateTransparentChassis();
//        bvavmJNI.bwNotifyRVC(0);
            isAvmDeInit = true;
            return res;
        }
    }

    public void updateTransparentChassis() {
        int position = SystemProperties.getInt("settingRadarActivatedPanorama", -1);
        KLog.e(position + " setIndexTab 设置透明底盘-初始化后调用: " + position);
        if (position == -1) return;
        if (position == 0) {
//            bvavmJNI.bwSetCarBottomStatus((byte) 0);
//            bvavmJNI.bwSetCarTransparency(1f);
            bwSetCarBottomStatus((byte) 0);
            bwSetCarTransparency(1.f);
        } else if (position == 1) {
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.3f);
            bwSetCarBottomStatus((byte) 1);
            bwSetCarTransparency(0.3f);
        } else if (position == 2) {
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.15f);
            bwSetCarBottomStatus((byte) 1);
            bwSetCarTransparency(0.15f);
        } else {
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.05f);
            bwSetCarBottomStatus((byte) 1);
            bwSetCarTransparency(0.05f);
        }
    }

    public void avmDeInit() {
        synchronized (syncObj) {
            isActive = false;
            mHandler.postDelayed(() -> {
                int res = bvavmJNI.avmDeInit();
                KLog.d("释放摄像头 avmDeInit res=" + res);
                bwDeleteCamera();
            }, 500);
        }
    }

    public void setCalibration(boolean calibration) {
        isCalibration = calibration;
    }

    public int avmRender2(int sCameraDirection) {
//        KLog.d("avmRender2：" + sCameraDirection);
        synchronized (syncObj) {
            if (!isActive)
                return 0;
//        if (isCalibration) {
//            return bvavmJNI.avmRender(sCameraDirection);
//        }
            if (bwInitValue != -1 && bwInitValue != 0) return bvavmJNI.avmRender2(sCameraDirection);

            return 0;
        }
    }

    private byte chassisSts = -1;
    public void bwSetCarBottomStatus(byte value) {
        if (chassisSts != value) {
            chassisSts = value;
            synchronized (syncObj) {
                bvavmJNI.bwSetCarBottomStatus(chassisSts);
            }
        }
    }

    private float transparencyValue = -1.f;
    public void bwSetCarTransparency(float value) {
        if (transparencyValue != value) {
            transparencyValue = value;
            synchronized (syncObj) {
                bvavmJNI.bwSetCarTransparency(transparencyValue);
            }
        }
    }

    public void bwClearCarBottomImage() {
        synchronized (syncObj) {
            bvavmJNI.bwClearCarBottomImage();
        }
    }

    public long bwCreateCamera(String path, String met) {
        synchronized (syncObj) {
            if (!isActive) return 0;
            if (bwInitValue != -1 && bwInitValue != 0) return bwInitValue;
            KLog.d("创建 bwCreateCamera：" + bwInitValue);
            return bwInitValue = bvavmJNI.bwCreateCamera(path, met);
        }
    }

    public long bwCreateCameraShow(String path, String met) {
        synchronized (syncObj) {
            KLog.d("bwCreateCamera bwInitValue :" + bwInitValue);
            if (bwInitValue != -1 && bwInitValue != 0) {
                return bwInitValue;
            }
            KLog.d("bwCreateCamera create");

            return bwInitValue = bvavmJNI.bwCreateCamera(path, met);
        }
    }

    public void bwDeleteCamera() {
        synchronized (syncObj) {
            KLog.d(bwInitValue + " bwDeleteCamera释放：" + bwInitValue);
            if (bwInitValue == -1 || bwInitValue == 0) {
                return;
            }
            isAvmDeInit = false;

            bvavmJNI.bwDeleteCamera(bwInitValue);
            bwInitValue = 0;

            KLog.d("释放摄像头完成：");
            return;
        }
    }

    public long camreaStatus() {
        return bwInitValue;
    }

    public int bwNotifyRVC(int value) {
        synchronized (syncObj) {
            return bvavmJNI.bwNotifyRVC(value);
        }
    }

    public void bwSet3DfreeFlag(int stat) {
        synchronized (syncObj) {
            bvavmJNI.bwSet3DfreeFlag(stat);
        }
    }

    public int bwSetCarDoorStatus(int[] doors) {
        synchronized (syncObj) {
            return bvavmJNI.bwSetCarDoorStatus(doors);
        }
    }

    private float wheelAngle = -1.f;
    public void bwSetWheelAngle(float angle) {
        if (wheelAngle != angle) {
            wheelAngle = angle;
            synchronized (syncObj) {
                KLog.d("bwSetWheelAngle : " + angle);
                bvavmJNI.bwSetWheelAngle(angle);
            }
        }
    }
    /*1 设置是否处于D档状态，1为是，0为否*/
//    public static native int bwSetCarIsDgear(int flag);
//    /*2 设置轨迹线开关，0是关闭，1是打开*/
//    public static native int bwSetTrajLineStatus(byte flag);
//    /*3 设置车辆是否处于倒车状态，1为倒车，0为静止或者前进*/
//    public static native int bwSetCarIsBack(byte flag);
    public void updateTrajLineStatus(int gear) {
        // 轨迹线需要 bwSetTrajLineStatus(byte flag); 开启
        synchronized (syncObj) {
            if (gear == 3) {// R档
                bvavmJNI.bwSetCarIsDgear(0);
                bvavmJNI.bwSetCarIsBack((byte) 1);
            } else {
                bvavmJNI.bwSetCarIsDgear(1);
                bvavmJNI.bwSetCarIsBack((byte) 0);
            }
        }
    }

    private byte trajLineEnable = -1;
    public void bwSetTrajLineStatus(byte value) {
        synchronized (syncObj) {
            if (value != trajLineEnable) {
                trajLineEnable = value;
                bvavmJNI.bwSetTrajLineStatus(value);
            }
        }
    }


    public boolean isCamera2Device() {
        CameraManager camMgr = (CameraManager) AvmApp.getInstance().getSystemService(Context.CAMERA_SERVICE);
        boolean camera2Dev = true;
        try {
            String[] cameraIds = camMgr.getCameraIdList();

            if (cameraIds.length != 0) {
                KLog.d("摄像头：" + Arrays.toString(cameraIds));
                for (String id : cameraIds) {
                    CameraCharacteristics characteristics = camMgr.getCameraCharacteristics(id);
                    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY &&
                            facing == LENS_FACING_BACK) {
                        camera2Dev = false;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            KLog.e(" error摄像头：" + e.getMessage());
            camera2Dev = false;
        }
        return camera2Dev;
    }

}
