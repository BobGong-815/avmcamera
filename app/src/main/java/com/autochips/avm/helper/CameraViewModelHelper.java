package com.autochips.avm.helper;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_UINM_TURN_LIGHT_SW_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_FRONT_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LEFT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_REAR_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_RIGHT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_RESP;
import static com.avm.framwork.constant.CameraContracts.DOOR_HOOD;
import static com.avm.framwork.constant.CameraContracts.DOOR_REAR;
import static com.avm.framwork.constant.CameraContracts.ROW_1_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_1_RIGHT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_RIGHT;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.ASSIST_DRIVE_PAS_BUTTON_PRESS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FLWHEEL_ROTATED_DIR;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_EL_REVERSE_LIGHT_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_EL_SIDE_BRKLIGHT_CTRL_CMD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_SAS_STEERING_ANGLE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_WHEEL_DIRE_SPEED;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_HIGH_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_LOW_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CABIN_DOOR_OPEN_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_VCU_GEAR_LVL_DISP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.POWER_PARKING_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.VEHICLE_SPEED;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.ui.view.CameraView;
import com.autochips.avm.util.CustomToast;
import com.autochips.avm.util.RearviewToast;
import com.autochips.avm.util.SystemProperties;
import com.avm.framwork.manager.CanManager;

import java.util.Arrays;

import me.goldze.mvvmhabit.utils.KLog;


/**
 * 管理各个视角的状态和页面切换
 */
public class CameraViewModelHelper {

    private static final String TAG = CameraViewModelHelper.class.getName();
    private static CameraViewModelHelper instance;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isReverse = false;// 是否进入倒车
    private boolean isReverseIn = false;// 是否进入倒车
    private boolean isReverseInByTurn = false;// 是否进入倒车控制转向退出，如果进入，则不能延时500关闭
    private boolean isTurn = false;// 是否进入转向
    private boolean isRadarActive = false;// 是否进入雷达激活
    private int isRadarActiveTow = 2;// 是否能再次激活雷达，如果手动关闭了则不能再次激活，除非用户主动进入一次；
    private int turnValue = -1;// 转向-左/右转
    private float speedValue = 0f;// 车速

    public static boolean isClick = false; // 是否手动进入

    private boolean isCalibrateRunning = false;

    private boolean isCloseTrunk = false; // 是否手动关闭转向，如果手动关闭后，不可以在进入转向激活，或者雷达激活
    private boolean mIsFirstOpen = true;//判断是否第一次打开

    /**
     * 0x4 ='P' Park gear;
     * 0x3 ='R' Reverse gear;
     * 0x2 ='N'Neutral gear;
     * 0x1 = 'D' Drive gear;
     */
    public static int valGear = 0;

    public CameraViewModelHelper() {
    }

    public static CameraViewModelHelper getInstance() {
        if (instance == null) instance = new CameraViewModelHelper();
        return instance;
    }

    /**
     * 是否手动，或语音进入
     *
     * @param click
     */
    public void setClick(boolean click) {
        isClick = click;
    }

    public void setRadarActiveTow(boolean activeTow) {// 手动关闭
        if (isRadarActive) isRadarActiveTow = 0;
        if (isTurn) {
            isTurn = false;
            isCloseTrunk = activeTow;
        }
    }


    public void showView(boolean isClick) {
        KLog.d("dismissView 进入AVM: " + isClick);
        if (mIsFirstOpen) {
            Log.i(TAG, "showComm mIsFirstOpen");
            mIsFirstOpen = false;
        } else {
            Log.i(TAG, "bwCreateCamera open");
            if (AvmApp.getInstance().isRight){
                BvAvmJNIHelper.getInstance().bwCreateCameraShow("com/autochips/avm/ui/view/CameraViewRight", "onBVAVMMessage");
            }else{
                BvAvmJNIHelper.getInstance().bwCreateCameraShow("com/autochips/avm/ui/view/CameraView", "onBVAVMMessage");
            }

        }
        this.isClick = isClick;
        if (isClick) {
            isRadarActiveTow = 1;
            isCloseTrunk = false; // 手动进入后
//            reverse(valGear);
        }
//        mHandler.removeCallbacksAndMessages("setRunning");
        showFullWin();

    }


    private void showFullWin() {
        KLog.d("进入全屏页面 showFullWin");
        AvmApp.getInstance().getCameraView().showFullWin();
    }

    /**
     * 根据条件判断是否需要关闭窗口
     * 手动进入的，就手动关闭
     */
    public void dismissView(boolean isClick, int time, String position) {
        KLog.d(position + " dismissView关闭 " + isClick);
        this.isClick = isClick;
        if (isClick) {
            return;
        }
        KLog.d(position + " 测试 ");
        if (isCalibrateRunning) {
            KLog.d(" dismissView关闭  isCalibrateRunning: true 进入标定；");
            CustomToast.showToast("正在标定.......");
            return;
        }
        BvAvmJNIHelper.getInstance().bwDeleteCamera();
        mHandler.postDelayed(() -> {
            AvmApp.getInstance().getCameraView().dismissView(position);
            this.isClick = false;
            isTurn = false;
            isReverse = false;
            isReverseInByTurn = false;
            isReverseToTurnStats = false;// R档进入转向模式
            isSmartView = false;
//            mHandler.removeCallbacks(getmRun);//  只要关闭，就要把所有定时器移除
            mHandler.removeCallbacksAndMessages("setRunning");
            mHandler.removeCallbacksAndMessages("close_token");
            mHandler.removeCallbacksAndMessages("close_N");
        }, "close_token", time);

        CustomToast.cancelToast();
        RearviewToast.getInstance().cancelToast();
    }

    public int getSpeedValue() {
        return (int) speedValue;
    }

    /**
     * 设置车底透明
     *
     * @param speedValue
     */
    private boolean isTransparent = false;
    private int mLastSetPosition = -1;//记录已设置的车底透视位置
    public void setTransparentIndexTab() {
        if (!CameraView.isShowing) {
            return;
        }
        int position = SystemProperties.getInt("settingRadarActivatedPanorama", 0);
//        KLog.i("车速speedVakye ............ " + speedValue);
//        if (!isTransparent) {
//            position = speedValue > 1 ? position : 0;
//        }
//        if (speedValue > 1) {
//            isTransparent = true;
//        } else {
//            if (valGear == 4) {
//                isTransparent = false;
//            }
//            if (speedValue==0){
//                isTransparent = false;
//            }
//        }
        if (speedValue <= 0.3 && position != 0) {
            return;
        }
        //        KLog.i("position .... " + position);
        if(position == mLastSetPosition){
            return;
        }

        if (position == 0) {
//            bvavmJNI.bwSetCarBottomStatus((byte) 0);
//            bvavmJNI.bwSetCarTransparency(1f);
            BvAvmJNIHelper.getInstance().bwSetCarBottomStatus((byte) 0);
            BvAvmJNIHelper.getInstance().bwSetCarTransparency(1.f);
        } else if (position == 1) {
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.3f);
            BvAvmJNIHelper.getInstance().bwSetCarBottomStatus((byte) 1);
            BvAvmJNIHelper.getInstance().bwSetCarTransparency(0.3f);
        } else if (position == 2) {
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.15f);
            BvAvmJNIHelper.getInstance().bwSetCarBottomStatus((byte) 1);
            BvAvmJNIHelper.getInstance().bwSetCarTransparency(0.15f);
        } else {
//            bvavmJNI.bwSetCarBottomStatus((byte) 1);
//            bvavmJNI.bwSetCarTransparency(0.05f);
            BvAvmJNIHelper.getInstance().bwSetCarBottomStatus((byte) 1);
            BvAvmJNIHelper.getInstance().bwSetCarTransparency(0.05f);
        }
        mLastSetPosition = position;
    }

    /**
     * 车速
     *
     * @param val
     */
    //退出高速模式
    private boolean isSpeedModel = false;
    private int turnSpeedValue = -1;

    public void setSpeedValue(float speedValue) {
        this.speedValue = speedValue;
    }

    public void setSpeed(float val) {
        speedValue = mpsToKmh(val);
//        speedValue = 3;// 测试透明地盘
        //KLog.d("车速："+val+"   isSpeedModel: "+isSpeedModel+"   speedValue: "+speedValue+"   turnValue: "+turnValue+ "  isClick:  "+isClick+"  版本号： "+ ServiceUtils.getVersionName());
        int turn = CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT);
        //KLog.d("车速：判断当前转向turn: "+turn);
        setTransparentIndexTab();
        if (speedValue <= 30 && isSpeedModel) {// 小于30 的时候，判断条件看是否满足转向激活
            KLog.d("车速：判断条件看是否满足转向激活  " + turn);
            if (turn == 1 || turn == 2) {
                KLog.d("车速： turnActive " + turn);
                //turnActive(turn);
                turnSpeedValue = -1;
                isSpeedModel = false;


                mHandler.removeCallbacksAndMessages("setRunning");
                mHandler.removeCallbacksAndMessages("close_N");
                //if (!isTurn) {
                // 转向灯激活全景开关打开，且车速《20，右转向灯打开 557843113
                int signalActivates = SystemProperties.getInt("signalActivates", 0);
                if (signalActivates == 1) {
                    KLog.d("车速： 激活视图 ");
                    smartActive(turn);
                }
                // }
                isTurn = true;
                turnSpeedValue = -1;
                //isSpeedModel = false;// 如果转向进入，则需要取消车速过高模式
                //setViewModel(turn == 1 ? ViewType.gear_Left : ViewType.gear_Right);
            }
        } else if (speedValue > 30 && !isClick) {// 车速过高，非点击进去关闭AVM
            isSpeedModel = true;
            turnSpeedValue = turnValue;
            //turnValue = -1;
            isTurn = false;
            //turnExit(1);
            /*isReverse = false;
            isRunning = false;
            mHandler.removeCallbacks(getmRun);
            mHandler.postDelayed(getmRun, 0);*/
            dismissView(isClick, 0, "车速大于30立即退出");
        }
    }

    public float getSpeed() {
        KLog.d("车速：");
        float speed = CanManager.getInstance().getFloatStatus(VEHICLE_SPEED, 0);
        return speedValue = mpsToKmh(speed);
    }

    public static float mpsToKmh(float speedInMps) {
        return speedInMps * 3.6f;
    }

    /**
     * 倒车档位
     *
     * @param value
     */
    public int count = 0;

    public void reverse(int value) {
        if (value <= 0) {
            KLog.d(valGear + " valGear value：" + isReverse);
            return;
        }
        if (valGear == value) {
            KLog.d(value + " value挡位相同valGear = " + valGear);
//            if (valGear == 3){
//              showFullWin(); // 解决第一次进不了R挡的问题
//            }
            return;
        }
        valGear = value;
        if (valGear == 1 && isRadarActiveTow == 1) isRadarActiveTow = 2;
        KLog.d(valGear + " valGear倒车档位：" + isReverse);
        if (valGear == 3) {
//             AvmApp.getInstance().getViewBottom().updateWind0();
            isReverseInByTurn = true;
            BvAvmJNIHelper.getInstance().updateTrajLineStatus(3);
            if (isReverse) {
                KLog.d(" valGear倒车档位： 我进来了啊");
                setViewModel();
                return;// 防止多次触发
            }
            isReverseIn = true;
            isReverse = true;
            isRadarActiveTow = 2;
            mHandler.removeCallbacksAndMessages("setRunning");
            showFullWin();
            KLog.d(" valGear倒车档位： 我是这里进来的");
            setViewModel();
        } else {
            isReverse = false;
            //不管是手动进来，只要触发了转向及挡位，都按转向跟挡位逻辑修改
            this.isClick = false;
            if (isTurn && turnValue != -1) {
                // 进入转向激活
                reverseToTurn();
            }
            isReverseIn = false;


        }
        count = 1;
        if (index == 0) {
            //如了R档，其他挡位都退出RVC
            KLog.d(" 第一次启动P档  resRvc  = " + index);
            mHandler.postDelayed(() -> {
//                int resRvc = bvavmJNI.bwNotifyRVC(0);
                int resRvc = BvAvmJNIHelper.getInstance().bwNotifyRVC(0);
                KLog.d(" 第一次启动P档  关闭resRvc  = " + resRvc);
            }, 1000);
            index++;
        }
    }

    private boolean isReverseToTurnStats = false; // 是否是倒车进入转向模式
    private int index = 0;

    private void reverseToTurn() {
        if (valGear == 4) { // p档
            int pExit = SystemProperties.getInt("pExit", 0);
            KLog.d(" 是否进入转向pExit = " + pExit);
            if (pExit == 0) {
                return;
            } else {// 否则进入转向
                isReverseInByTurn = false;
                if (isReverseIn) // 倒车进入的时候才会定时
                    isReverseToTurnStats = true;
                turnActive(turnValue);
            }


        } else if (valGear == 1 || valGear == 2) {// D/N档
            if (isReverseIn) isReverseToTurnStats = true;
            isReverseInByTurn = false;
            turnActive(turnValue);
        }

    }

    private Runnable mRun = new Runnable() {
        @Override
        public void run() {

        }
    };

    /**
     * 转向状态
     *
     * @param val
     */
    private void turnStatus(int val) {
        mHandler.postDelayed(mRun, 35);


    }


    /**
     * 转向激活 557843113
     *
     * @param value 1：left 2:right
     */
    public void turnActive(int value) {
        KLog.d(value + " 转向激活 turnActive isClose = " + AvmApp.getInstance().getCameraView().isFullWin + " isReverse:" + isReverse + " valGear : " + valGear);

        if (isReverse && AvmApp.getInstance().getCameraView().isFullWin) {// 步骤： 转向-R档-关闭转向 此时需要重置 转向状态
            if (value != 1 && value != 2) {
                if ((isTurn || turnValue > 0)) {
                    isTurn = false;
                    turnValue = -1;
                    isReverseToTurnStats = false;
                }
            } else {
                isCloseTrunk = false;
                turnValue = value;
                isTurn = true;
            }

            return;
        }
        KLog.d(value + " 转向激活信号参数 = " + isCloseTrunk + " isTurn:" + isTurn);
        if (turnValue != value) {
            isCloseTrunk = false;
            turnValue = value;
        } else if (!isReverseToTurnStats) {
            return;
        }
        if (isCloseTrunk) {
            return;
        }

//        mHandler.removeCallbacks(getmRun);
        KLog.d(isReverse + " isReverse转向激活  =移除run ");

        float speed = getSpeed();
        KLog.d(value + " value 转向激活 isTurn = " + isTurn + " isReverse :" + isReverse);
        //不管是手动进来，只要触发了转向及挡位，都按转向跟挡位逻辑修改
        this.isClick = false;
        // 右向向打开 1 打开  0 关闭
        if (speed < 30 && (value == 1 || value == 2)) {
            KLog.d(value + " value removeCallbacksAndMessages 进入转向 = " + isTurn + " isReverse :" + isReverse);
            mHandler.removeCallbacksAndMessages("setRunning");
            mHandler.removeCallbacksAndMessages("close_N");
            isTurn = true;
            if (!AvmApp.getInstance().getCameraView().isFullWin) {
                // 转向灯激活全景开关打开，且车速《20，右转向灯打开 557843113
                int signalActivates = SystemProperties.getInt("signalActivates", 0);
                if (signalActivates == 1) {
                    smartActive(value);
                }
            }

            turnSpeedValue = -1;
            //isSpeedModel = false;// 如果转向进入，则需要取消车速过高模式
            if (valGear == 3) {
                isReverse = true;
                isReverseIn = true;
                isRadarActiveTow = 2;
                isReverseInByTurn = true;
                setViewModel();
                return;
            }
            setViewModel();
        } else if (value == 0) {
            KLog.d(turnValue + " 转向激活 被退出：" + speed + "   isTurn: " + isTurn);
//            setViewModel(ViewType.gear_turn_exit);
            turnSpeedValue = -1;
            //isSpeedModel = false; // 转向关闭的时候，需要置空车速过高模式
            isTurn = false;
            if (valGear != 3) {
                setViewModel();
            }
            if (valGear == 4 && !AvmApp.getInstance().getCameraView().isSmartWin) {
                KLog.d(" 这里仅仅只是在全屏的时候从其他档挂入P档要重新计时才能进入");
            } else {
                KLog.d("其他视图跟转向操作都走这里");
                turnExit(value);
            }

        }
    }

    /**
     * 转向进入
     * R 档
     * 转向回正
     */

    /*
    private Runnable getmRun = new Runnable() {

        @Override
        public void run() {
            KLog.d(isTurn + " isTurn：退出转向 removeCallbacksAndMessages   isReverse: " + isReverse + "isRunning:  " + isRunning + "    isReverseInByTurn: " + isReverseInByTurn);
            if (isTurn || isRunning) {
              if ( isReverse && AvmApp.getInstance().getCameraView().isFullWin)
                return;
            }
            isTurn = false;
            turnValue = -1;
//            setViewModel(ViewType.gear_turn_exit);
            if (isReverseInByTurn && AvmApp.getInstance().getCameraView().isFullWin) {
                setRunning(true);
                return;
            }
            if (valGear != 3)
             setViewModel(ViewType.gear_turn_exit);
            dismissView(isClick, 0, "转向延时500ms退出 d5");
        }
    };

     */
    public void turnExit(int value) {
        if (valGear != 3) {
            setViewModel();
        }
    }


    /**
     * 雷达激活
     *
     * @param value
     */
    public void radarActive(int value) {
        if (CameraView.isShowing) {
            return;
        }
        KLog.d(isRadarActiveTow + " radarActive 雷达距离 = " + value);
        if (value >= 0 && isRadarActiveTow == 2) {
            // 雷达激活全景开关打开，且车速《12，且 雷达检测到障碍物
            int activatedPanorama = SystemProperties.getInt("activatedPanorama", 2);
            KLog.d("activatedPanorama = " + activatedPanorama);
            if (activatedPanorama != 1) {
                KLog.d("activatedPanorama is close");
                return;
            }
            float speed = getSpeed();
            KLog.d("speed = " + speed);
            if (speed <= 30) {
                isRadarActive = true;
                smartActive(value);
            } else {
                radarExit(value);
            }

        }
    }

    public void setIsRadarActiveTow(){
        //主动点击关闭雷达不能激活，设置关闭雷达可以激活
        isRadarActiveTow  = 2;
    }

    public void radarExit(int value) {
        KLog.d(isRadarActive + " 雷达退出：" + value);
        if (isRadarActive) {
            isRadarActive = false;
//            AvmRuntime.self().radarChange(false);
            //dismissView(isClick, 3 * 1000, "d6");
        }

    }

    /**
     * 盲区小窗口激活
     *
     * @param value
     */
    //判断小卡片是否还存在
    public boolean isSmartView = false;

    public void smartActive(int value) {
        isSmartView = true;
//        AvmRuntime.self().radarChange(true);
        //AvmApp.getInstance().getCameraView().showSmartWin();
    }


    /**
     * 车门状态开关
     * Function Describe:右前车窗控制  右后车窗控制 左前车窗控制 左后车窗控制 天窗/遮阳帘控制
     * * areaID:VehicleAreaWindow::ROW_1_RIGHT, VehicleAreaWindow::ROW_2_RIGHT,
     * VehicleAreaWindow::ROW_1_LEFT, VehicleAreaWindow::ROW_2_LEFT, VehicleAreaWindow::ROOF_TOP_1
     *
     * @param val
     */
    public void doorStatus(int vehicleId, int val) {
        Log.i(TAG, val + " AvmService 门的状态DOOR_HOOD: " + CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, DOOR_HOOD)
        + " isRight:"+AvmApp.getInstance().isRight+" EEA:"+AvmApp.EEA);
        boolean isRightEEA = AvmApp.getInstance().isRight && (AvmApp.EEA == 1 || AvmApp.EEA == 0);//2.5平台并且是右舵，信号相反
        /*设置车模上四个车门和后备箱开合状态，顺序分别为左前门、右前门、左后门、右后门，后备箱，左后视镜、右后视镜、前车盖*/
        int doors[] = {CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, isRightEEA ? ROW_1_RIGHT : ROW_1_LEFT),
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, isRightEEA ? ROW_1_LEFT : ROW_1_RIGHT),
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_2_LEFT),
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_2_RIGHT),
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, DOOR_REAR),
               /* vehicleId == MIRROR_FOLD_UNFOLD_STATUS ? (val == 1 ? 1 : 0) : 0,
                vehicleId == MIRROR_FOLD_UNFOLD_STATUS ? (val == 1 ? 1 : 0) : 0,*/
                0, //后视镜车模不折叠 2024 0827
                0,
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, DOOR_HOOD),};
        Log.i(TAG, "AvmService 门的状态:" + Arrays.toString(doors));
        BvAvmJNIHelper.getInstance().bwSetCarDoorStatus(doors);
    }

    /**
     * 雷达提示音音开关
     * Function Describe:需将用户是否点击的状态告知雷达
     *
     * @param val
     */
    public void radarSoundStatus(int val, int status) {
        Log.i(TAG, val + "雷达 AvmService 雷达提示音音开关: " + status);
        CanManager.getInstance().setIntProperty(ASSIST_DRIVE_PAS_BUTTON_PRESS, 0, val);
        String msg = status == 0 ? AvmApp.getInstance().getString(R.string.radar_sound_close) : AvmApp.getInstance().getString(R.string.radar_sound_open);
        if(AvmApp.getInstance().getCameraView().isSmartWin){
            CustomToast.showToast(msg);
        }else {
            RearviewToast.getInstance().showToast(msg);
        }
    }

    /**
     * 后视镜折叠
     * 1 folded 折叠
     * 2 Unfolded 展开
     */
    public void mirrorFoldUnFoldStatus(Object statusVal) {
//     int status =  CanManager.getInstance().getIntStatus(MIRROR_FOLD_UNFOLD_STATUS, 0); //后视镜折叠
        KLog.d("后视镜折叠：" + statusVal);
        int status = Integer.valueOf(statusVal.toString());
        if (status == 0) return;
//        CustomToast.showToast("后视镜折叠: " + status);

    }

    /**
     * 后视镜下翻
     * 1 左边
     * 2 右边
     * 3 二者都下翻
     */
    public void mirrorAutomaticStatus() {
        int status = CanManager.getInstance().getIntStatus(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE, 0); //后视镜折叠
        KLog.d("后视镜下翻：" + status);
        AvmApp.getInstance().getCameraView().viewRearStatus(status);
        String strStatus = "左右两边下翻";
        if (status == 1) {
            strStatus = "左边下翻";
        } else if (status == 2) {
            strStatus = "右边下翻";
        }
        if (status == 0) return;
//        CustomToast.showToast("后视镜" + strStatus);

    }

    /**
     * 轮速
     * float 类型
     * AVM_WHEEL_DIRE_SPEED（0x06F2）：
     * [BCS_FLWheelSpd,
     * BCS_FRWheelRotatedDirection,
     * BCS_FRWheelSpd,
     * BCS_RLWheelRotatedDirection,
     * BCS_RLWheelSpd,
     * BCS_RRWheelRotatedDirection,
     * BCS_RRWheelSpd]
     * <p>
     * AVM_BCS_FLWHEEL_ROTATED_DIR（0x0474）：BCS_FLWheelRotatedDirection
     */
    public void flWheelSpd(Object object) {
//        KLog.d("wheel speed 轮速：" +  Arrays.toString((int[]) object));
        Object object1 = CanManager.getInstance().getIntArray(AVM_WHEEL_DIRE_SPEED, 0);
//        int flFont = CanManager.getInstance().getIntStatus(AVM_BCS_FLWHEEL_ROTATED_DIR, 0);// 左前轮
//        int arr[] = {};
        //KLog.d("轮速-flFont：" + flFont);
//        if (object1 != null) KLog.d("wheel speed 轮速-flFont：" + object1.getClass());
        bvavmJNI.bwSetFourWheelSpeed(0, 0, 0, 0);
        if (object1 != null && object1 instanceof int[]) {
            int[] arr = (int[]) object1;
//            KLog.d("轮速：" + Arrays.toString(arr));
            if (arr.length >= 7) {
                //KLog.d("轮速：arr.length  >=7" + Arrays.toString(arr));
                bvavmJNI.bwSetFourWheelSpeed(arr[0], arr[2], arr[4], arr[6]);
            }
        }
    }

    /**
     * 轮速方向
     */
    public void flWheelDir() {
        int flDir = CanManager.getInstance().getIntStatus(AVM_BCS_FLWHEEL_ROTATED_DIR, 0);
        //KLog.d("轮速方向：" + flDir);
    }

    /**
     * 方向盘转角
     */
    public void angleSteel() {
        float status = CanManager.getInstance().getFloatStatus(AVM_SAS_STEERING_ANGLE, 0); //转角
        //KLog.d("方向盘转角：" + status);
        //文档是780，但是方向盘打死之后是530
//      KLog.d("轮速方向-status：" + status);
        if (status > 540) {
            status = 540;
        } else if (status < -540) {
            status = -540;
        }
        status = (float) (((status + 540.0) / 1080.0) * 72.0 - 36.0);
        bvavmJNI.bwSetWheelAngle(status * -1);
    }

    //3D车模灯光交互
    public void showLight3DModel(int type, int value) {
        KLog.d("3D 灯光showLight3DModel： type:" + type + " value: " + value);
        int highBeamStatus = CanManager.getInstance().getIntStatus(BCM_HIGH_BEAM_STATUS, 0);//远光灯
        int lowBeamStatus = CanManager.getInstance().getIntStatus(BCM_LOW_BEAM_STATUS, 0);//近光灯
        int brakeLight = CanManager.getInstance().getIntStatus(AVM_EL_SIDE_BRKLIGHT_CTRL_CMD, 0);//刹车灯
        int reverseLight = CanManager.getInstance().getIntStatus(AVM_EL_REVERSE_LIGHT_ST, 0);//倒车灯
        int frontFogLamp = CanManager.getInstance().getIntStatus(CLUSTER_FRONT_FOG_LAMP, 0);//前雾灯
        int leftFogLamp = CanManager.getInstance().getIntStatus(CLUSTER_LEFT_TURN_LAMP, 0);//左转向灯
        int rightFogLamp = CanManager.getInstance().getIntStatus(CLUSTER_RIGHT_TURN_LAMP, 0);//右转向灯
        int parkingLamp = CanManager.getInstance().getIntStatus(POWER_PARKING_LAMP, 0);//示宽灯、位置灯s
        int readFog = CanManager.getInstance().getIntStatus(CLUSTER_REAR_FOG_LAMP, 0);//后雾灯

        int[] lamp = new int[8];
        lamp[0] = highBeamStatus; //远光灯  0 关 1 开
        lamp[1] = lowBeamStatus;//  近光灯 0 关 1 开
        lamp[2] = readFog; //雾 灯  0关 1 开
        lamp[3] = parkingLamp; //位置灯 0关 1 开
        lamp[4] = highBeamStatus; //日间行车灯  车辆暂时无法点击 需调......
        lamp[5] = 0; //转向灯
        lamp[6] = brakeLight;//刹车灯
        lamp[7] = reverseLight;//倒车灯

        //50毫秒内如果左右转向都有，就视为双闪
        mHandler.postDelayed(() -> {
            if (leftFogLamp == 1 && rightFogLamp == 1) {
                if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_ID) {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 1, (byte) brakeLight);
                } else if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_T_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_G_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_GR_ID) {
                    lamp[5] = 1;
                    bvavmJNI.bwSetLampStatus2(lamp);
                }
            } else if (leftFogLamp == 0 && rightFogLamp == 0) {
                if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_ID) {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 0, (byte) brakeLight);
                } else if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_T_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_G_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_GR_ID) {
                    lamp[5] = 0;
                    bvavmJNI.bwSetLampStatus2(lamp);
                }
            } else if (leftFogLamp == 1 && rightFogLamp == 0) {
                if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_ID) {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 2, (byte) brakeLight);
                } else if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_T_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_G_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_GR_ID) {
                    lamp[5] = 2;
                    bvavmJNI.bwSetLampStatus2(lamp);
                }
            } else if (leftFogLamp == 0 && rightFogLamp == 1) {
                if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_ID) {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 3, (byte) brakeLight);
                } else if (BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_T_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_G_ID
                        || BvAvmJNIHelper.getInstance().getCameraType() == bvavmJNI.PROJ_AY5_GR_ID) {
                    lamp[5] = 3;
                    bvavmJNI.bwSetLampStatus2(lamp);
                }
            }
        }, "setRunning", 50);
    }

    //标定错误返回码
    public void setAutomaticCalibration(int value) {
        byte[] errorBack = {0x00, 0x0C, 0x00, 0x00};
        if (value == 0x00) {//成功
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_success));
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_success));
        } else if (value == 0x01) {//前视标定失败
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_error_1));
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_error_1));
            errorBack = new byte[]{0x00, 0x0C, 0x00, 0x00};
        } else if (value == 0x02) {//后视标定失败
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_error_2));
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_error_2));
            errorBack = new byte[]{0x00, 0x0D, 0x00, 0x00};
        } else if (value == 0x04) {//左视标定失败
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_error_3));
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_error_3));
            errorBack = new byte[]{0x00, 0x0E, 0x00, 0x00};
        } else if (value == 0x08) {//右视标定失败
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_error_4));
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_error_4));
            errorBack = new byte[]{0x00, 0x0F, 0x00, 0x00};
        } else if (value == 0x03) {//前后视标定失败
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_error_5));
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_error_5));
            errorBack = new byte[]{0x00, 0x0C, 0x0D, 0x00};
        } else if (value == 0x0F) {//前后左右视标定失败
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_error_6));
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_error_6));
            errorBack = new byte[]{0x00, 0x0C, 0x0F, 0x00};
        }
        CanManager.getInstance().setByteArray(DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_RESP, 0, errorBack);
    }

    /**
     * 是否在标定
     *
     * @param running
     */
    public void setCalibrateRunning(boolean running) {
        this.isCalibrateRunning = running;

    }

    public boolean isCalibrateRunning() {
        return isCalibrateRunning;
    }

    public void setViewModel() {
        AvmApp.getInstance().getCameraView().viewShowStatus();
    }

    /**
     * 快速启动时，延时2s 触发AVM 打开；
     */
    public void initActive() {
        int gear_R = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);
        KLog.d(valGear + " 注册完成 获取挡位信息  valGear :" + gear_R);
        if (AvmService.isCalibration) {
            return;
        }
//      AvmApp.getInstance().getCameraView().updateWind(0.0f,2);
//        if (gear_R != 3) {
//
//        }else {
//            reverse(gear_R);
//        }
    }

    /**
     * 30s 关闭
     */
    private void close30s() {
        downTimer.cancel();
        downTimer.start();
    }

    private int closeTime = 30 * 1000;
    private int mCountdownInterval = 2000;
    private CountDownTimer downTimer = new CountDownTimer(closeTime, mCountdownInterval) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
//            dismissView();

        }
    };

}
