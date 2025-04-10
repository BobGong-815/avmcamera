package com.avm.framwork.manager;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_FLS_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_FRS_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RL_MIDSNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RL_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RR_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RSL_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RSR_SNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_YAW_RATE_AND_VEH_ACCEL;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_Distance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_STOP_ON_BRK_REMIND_SIGNAL;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_WARNING_SOUND;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.NFS_SYNC_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.VEHICLE_SPEED;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.ASSIST_DRIVE_PAS_BUTTON_PRESS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AUDIO_WARNING_SOURCE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FLWHEEL_ROTATED_DIR;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FLWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FRWHEEL_ROTATED_DIR;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_FRWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_RLWHEEL_ROTATED_DIR;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_RLWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_RRWHEEL_ROTATED_DIR;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_BCS_RRWHEEL_SPD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_EL_REVERSE_LIGHT_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_EL_SIDE_BRKLIGHT_CTRL_CMD;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_PCS_FRONTCAMERAFAIL;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_PCS_PASBUTTONPRESS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RR_MIDSNS_ERR_FLAG;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_SAS_STEERING_ANGLE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_UINM_TURN_LIGHT_SW_ST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_WHEEL_DIRE_SPEED;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_HIGH_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.BCM_LOW_BEAM_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CABIN_DOOR_OPEN_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_FRONT_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LEFT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_REAR_FOG_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_RIGHT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_CHIME_PAS_WARNTONE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_DOOR_LOCK_WARNING;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_FL_TURN_LAMP_FAULT;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_FR_TURN_LAMP_FAULT;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_DRIVERDOORAJARST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_PassengerDoorAjarSt;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_ML_TURN_LAMP_FAULT;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_MR_TURN_LAMP_FAULT;
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
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_RIGHT_TURN_LAMP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_RL_TURN_LAMP_FAULT;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_VCU_GEAR_LVL_DISP;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.EMS_FIL_VEH_LAL_ACCEL;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.EMS_FIL_VEH_LONG_ACCEL;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.HVAC_SIDE_MIRROR_HEAT;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.MIRROR_FOLD_UNFOLD_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_OUTER_REARVIEW_MIRROR_FOLDS_AUTOMATIC;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_TRUN_TO_MODE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.SETTINGS_VCU_BRKPEDPST;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_RADAR_ALARM_ACOUSTIC_SWITCH;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.POWER_PARKING_LAMP;
import static com.avm.framwork.constant.CameraContracts.ROW_1_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_1_RIGHT;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.WeakHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CanManager implements Handler.Callback {
    private String TAG = "BaseWorkStateManager";
    private Car mCar;
    private CarPropertyManager mCarPropertyManager;
    private List<Integer> mRegisterId = new ArrayList<>();//需要监听的信号列表
    private Context mContext;
    protected WeakHandler mSubWeakHandler;//子线程弱handler

    private static volatile CanManager mModel;

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());//主线程handler
    private final List<onSignalValueChangedListener> mListener = new LinkedList<>();
    private  View.OnClickListener listener;


    public static CanManager getInstance() {
        if (mModel == null) {
            synchronized (CanManager.class) {
                if (mModel == null) {
                    mModel = new CanManager();
                }
            }
        }
        return mModel;
    }

    /**
     * car 服务
     */
    private final ServiceConnection mCarConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "car - onServiceConnected ComponentName " + componentName.getClassName());
            mCarPropertyManager = (CarPropertyManager) mCar.getCarManager(Car.PROPERTY_SERVICE);
            boolean registerStatus = registerCarDataChangeCallBack(mRegisterId);
            Log.i(TAG, "registerStatus = " + registerStatus);

            listener.onClick(null);


            //初始化逻辑问题编写
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    protected CanManager() {
        HandlerThread workStateHandlerThread = new HandlerThread("WorkStateManager");
        workStateHandlerThread.start();
        Looper looper = workStateHandlerThread.getLooper();
        if (looper == null) {
            Log.e("BaseWorkStateManager", "workStateHandlerThread.getLooper() = null");
        } else {
            mSubWeakHandler = new WeakHandler(looper, this);
        }
    }

    /***
     * EMS_FIL_VEH_LAL_ACCEL EMS_FIL_VEH_LONG_ACCEL 合成  AVM_YAW_RATE_AND_VEH_ACCEL
     * CLUSTER_FL_TURN_LAMP_FAULT CLUSTER_MR_TURN_LAMP_FAULT
     * CLUSTER_LEFT_TURN_LAMP_FAULT_ST CLUSTER_LEFT_TURN_LAMP_FAULT_ST
     * CLUSTER_MR_TURN_LAMP_FAULT CLUSTER_RL_TURN_LAMP_FAULT
     * CLUSTER_DOOR_LOCK_WARNING CLUSTER_LCK_DRIVERDOORAJARST
     * CLUSTER_CHIME_PAS_WARNTONE AVM_EL_REVERSE_LIGHT_ST
     * HVAC_SIDE_MIRROR_HEAT AVM_BCS_RLWHEEL_ROTATED_DIR AY5G250无信号
     * */
    public void init(Context context) {
        mContext = context;
        mRegisterId.add(AVM_SAS_STEERING_ANGLE);// 方向盘转角 EPS_SteeringAngle  、、收到
//        mRegisterId.add(AVM_PAS_PCSDISPLAYREQ);// 雷达触发全景显示请求
//        mRegisterId.add(VEHICLE_AT_LEVEL_INSTRUMENT);// 档位
        mRegisterId.add(AVM_YAW_RATE_AND_VEH_ACCEL);//mRegisterId.add(EMS_FIL_VEH_LAL_ACCEL);// 横向加速度
//        mRegisterId.add(EMS_FIL_VEH_LONG_ACCEL);// 纵向加速度
        mRegisterId.add(SETTINGS_TRUN_TO_MODE);// 转向模式
        mRegisterId.add(AUDIO_WARNING_SOURCE);// 灯光预警


        //转向灯
        mRegisterId.add(AVM_UINM_TURN_LIGHT_SW_ST);// 转向灯开关状态  激活全景
        mRegisterId.add(CLUSTER_LEFT_TURN_LAMP);// 左转向指示灯状态
        mRegisterId.add(CLUSTER_RIGHT_TURN_LAMP);// 右转向指示灯状态
//        mRegisterId.add(CLUSTER_FL_TURN_LAMP_FAULT);// 左前转向灯故障状态
//        mRegisterId.add(CLUSTER_FR_TURN_LAMP_FAULT);// 前转向灯故障状态
//        mRegisterId.add(CLUSTER_LEFT_TURN_LAMP_FAULT_ST);// 左转向灯报警信号
//        mRegisterId.add(CLUSTER_ML_TURN_LAMP_FAULT);// 中左转向灯故障状态
//        mRegisterId.add(CLUSTER_MR_TURN_LAMP_FAULT);// 中右转向灯故障状态
        mRegisterId.add(CLUSTER_RIGHT_TURN_LAMP);// 右转向指示灯状态
//        mRegisterId.add(CLUSTER_RL_TURN_LAMP_FAULT);// 左后转向灯故障状态

        // 车门:
//        mRegisterId.add(CLUSTER_DOOR_LOCK_WARNING);//
//        mRegisterId.add(CLUSTER_LCK_DRIVERDOORAJARST);//

        //轮速
        mRegisterId.add(AVM_WHEEL_DIRE_SPEED);

        //后视镜
        mRegisterId.add(SETTINGS_OUTER_REARVIEW_MIRROR_FOLDS_AUTOMATIC);//外后视镜自动折叠

        // 车速
        mRegisterId.add(VEHICLE_SPEED);//  dbc发送 验证通过
        // 雷达提示音 待确定
        mRegisterId.add(CLUSTER_WARNING_SOUND);//mRegisterId.add(AVM_PCS_PASBUTTONPRESS);
        //雷达报警
        mRegisterId.add(AVM_RADAR_ALARM_ACOUSTIC_SWITCH);
        //雷达报警音状态
//        mRegisterId.add(CLUSTER_CHIME_PAS_WARNTONE);
        //P档信号
//        mRegisterId.add(CLUSTER_GEAR_P_STATE);
        //摄像头故障
//        mRegisterId.add(AVM_PCS_FRONTCAMERAFAIL);//AY5T有
        //刹车踏板
        mRegisterId.add(CLUSTER_STOP_ON_BRK_REMIND_SIGNAL);//mRegisterId.add(SETTINGS_VCU_BRKPEDPST);

        //远光灯
        mRegisterId.add(BCM_HIGH_BEAM_STATUS);// 收到
        //近光灯
        mRegisterId.add(BCM_LOW_BEAM_STATUS);// 收到

        //刹车灯
        mRegisterId.add(AVM_EL_SIDE_BRKLIGHT_CTRL_CMD);
        //位置灯
        mRegisterId.add(POWER_PARKING_LAMP);
        //倒车灯
//        mRegisterId.add(AVM_EL_REVERSE_LIGHT_ST);
        //前雾灯
        mRegisterId.add(CLUSTER_FRONT_FOG_LAMP);
        mRegisterId.add(CLUSTER_REAR_FOG_LAMP);
//        mRegisterId.add(HVAC_SIDE_MIRROR_HEAT);
        mRegisterId.add(CLUSTER_VCU_GEAR_LVL_DISP);// 倒车R档： GW_VCU_10_B   0x38B   VCU_GearLvIDisp
//          startConnect();
//

        // 车轮速
        mRegisterId.add(AVM_BCS_FLWHEEL_ROTATED_DIR);//左前轮前进方向 // 557843572
//        mRegisterId.add(AVM_BCS_FRWHEEL_ROTATED_DIR);//右前轮前进方向 // 557843575
//        mRegisterId.add(AVM_BCS_RLWHEEL_ROTATED_DIR);//左后轮前进方向 // 557843578
//        mRegisterId.add(AVM_BCS_RRWHEEL_ROTATED_DIR);//右后轮前进方向 // 557843581
//        mRegisterId.add(AVM_BCS_FLWHEEL_SPD);//左前轮轮速  //收到 559940725
//        mRegisterId.add(AVM_BCS_FRWHEEL_SPD);//右前轮轮速   //收到 559940728
//        mRegisterId.add(AVM_BCS_RLWHEEL_SPD);//左后轮轮速  //收到 559940731
//        mRegisterId.add(AVM_BCS_RRWHEEL_SPD);//右后轮轮速 // 收到 559940734

        //mRegisterId.add(BCS_FLWheelSpd)

        mRegisterId.add(CLUSTER_LCK_DRIVERDOORAJARST);//左前车门状态 //557861862
        mRegisterId.add(CABIN_DOOR_OPEN_STATUS);//引擎(前舱)盖 // 641729089
        mRegisterId.add(CLUSTER_LCK_PassengerDoorAjarSt);//右前车门状态 // 557845468
        mRegisterId.add(CLUSTER_PAS_RLMidDistance);
        mRegisterId.add(CLUSTER_PAS_RLMidDistance);
        mRegisterId.add(CLUSTER_PAS_RLMidDistance);       // RLM雷达距离(后左中)
        mRegisterId.add(CLUSTER_PAS_RRMidDistance);       // RLM雷达距离(后右中)
        mRegisterId.add(CLUSTER_PAS_RRDistance);          // RR雷达距离（后右
        mRegisterId.add(CLUSTER_PAS_RLDistance);          // RL雷达距离（后左
        mRegisterId.add(ASSIST_DRIVE_PAS_BUTTON_PRESS);   //  雷达报警声状态
        mRegisterId.add(CLUSTER_PAS_PAS_RSLSideDistance); //  RSLS雷达距离（后左侧(预留)
        mRegisterId.add(CLUSTER_PAS_PAS_RSRSideDistance); //  RSRS雷达距离（后右侧）(预留)
        mRegisterId.add(CLUSTER_PAS_FSLSideDistance); //   FSLS雷达距离（前左侧）(预留)
        mRegisterId.add(CLUSTER_PAS_FSRSideDistance); //   FSRS雷达距离（前右侧）(预留)
        mRegisterId.add(CLUSTER_PAS_PAS_FRDistance); //    FR雷达距离（前右）(预留)
        mRegisterId.add(CLUSTER_PAS_PAS_FLDistance); //    FL雷达距离（前左）(预留)
        mRegisterId.add(CLUSTER_PAS_FRMidDistance); //     FRM雷达距离（前右中）(预留)
        mRegisterId.add(CLUSTER_PAS_FLMidDistance); //     FLM雷达距离（前左中）(预留)
        mRegisterId.add(CLUSTER_PAS_Distance); //    12个雷达合一个信号

        // 雷达故障
        mRegisterId.add(AVM_RR_MIDSNS_ERR_FLAG);    //     后右中
        mRegisterId.add(AVM_RL_MIDSNS_ERR_FLAG);    //     后左中
        mRegisterId.add(AVM_RR_SNS_ERR_FLAG);    //       后右
        mRegisterId.add(AVM_RL_SNS_ERR_FLAG);    //       后左
        mRegisterId.add(AVM_RSL_SNS_ERR_FLAG);    //
        mRegisterId.add(AVM_RSR_SNS_ERR_FLAG);    //
        mRegisterId.add(AVM_FRS_SNS_ERR_FLAG);    //
        mRegisterId.add(AVM_FLS_SNS_ERR_FLAG);    //


        mRegisterId.add(MIRROR_FOLD_UNFOLD_STATUS);    //   后视镜折叠
        mRegisterId.add(SETTINGS_OUTER_REARVIEW_MIRROR_RETREATS_AUTOMATIC_VALUE);    //   后视镜下翻

        //标定
        mRegisterId.add(DIAG_22_0305_AVM_SYSTEM_CALIBRATTION_INFO_REQ);
        mRegisterId.add(DIAG_31_3801_AVM_ENTER_CALIBRATION_REQ);//进入下线标定请求
        mRegisterId.add(DIAG_31_3801_AVM_ENTER_CALIBRATION_RESULT_REQ);//进入下线标定结果请求
        mRegisterId.add(DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_REQ);//标定预检查请求
        mRegisterId.add(DIAG_31_3802_AVM_CALIBRATION_PRE_CHECK_RESULT_REQ);//标定预结果结果请求
        mRegisterId.add(DIAG_31_3803_AVM_START_CALIBRATION_REQ);//开始标定请求
        mRegisterId.add(DIAG_31_3803_AVM_START_CALIBRATION_RESULT_REQ);//开始标定结果请求
        mRegisterId.add(DIAG_31_3806_AVM_CALIBRATION_CHECK_REQ);//下线标定检查
        mRegisterId.add(DIAG_31_3806_AVM_CALIBRATION_CHECK_RESULT_REQ);//下线标定检查结果请求
        mRegisterId.add(DIAG_31_380D_AVM_READ_FAIL_REASON_REQ);//读取标定失败原因请求
        mRegisterId.add(DIAG_31_380D_AVM_READ_FAIL_REASON_RESULT_REQ);//读取标定失败原因结果请求

        mRegisterId.add(NFS_SYNC_STATUS);
    }

    public void startConnect(View.OnClickListener listener) {
        this.listener = listener;
        if (mSubWeakHandler != null) {
            //这里主要是想连接bcm，但不注册监听。等快速倒车结束后才注册监听
            //比如开机一直是快速倒车，那么我连接了bcm，一直等到结束快速倒车后，就注册bcm监听，这样子就比较快
            connectCar();
        } else {
            Log.e(TAG, "mSubWeakHandler = null");
        }
    }

    /**
     * 连接Car服务
     */
    private void connectCar() {
        Log.e(TAG, "连接Car服务");
        mCar = Car.createCar(mContext, mCarConnection);//连接服务
        if (mCar != null) {//连接服务成功
            Log.i(TAG, "connectCar success");
            mCar.connect();
        } else {//注册监听失败，则重新注册
            Log.w(TAG, "connectCar false , so reconnect");
            mSubWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectCar();
                }
            }, 1000);
        }
    }


    @Override
    public boolean handleMessage(@NonNull Message message) {
        return false;
    }

    /**
     * 获取返回值为float类型的接口(VehiclePropertyType中的类型为float)
     *
     * @param propId :填receive表格中的，Android中自定义Key
     * @param area   :填receive表格中的：Area value
     * @return float
     */
    public float getFloatStatus(int propId, int area) {
        if (mCar != null && mCar.isConnected()) {
            return mCarPropertyManager.getFloatProperty(propId, area);
        } else {
            Log.w(TAG, "get float failed");
        }
        return -1;
    }

    /**
     * 获取返回值为int类型的接口(VehiclePropertyType中的类型为int)
     *
     * @param propId :填receive表格中的，Android中自定义Key
     * @param area   :填receive表格中的：Area value
     * @return float
     */
    public int getIntStatus(int propId, int area) {
        if (mCar != null && mCar.isConnected()) {
            return mCarPropertyManager.getIntProperty(propId, area);
        } else {
            Log.w(TAG, "get int failed");
        }
        return -1;
    }
    /**
     * 获取返回值为int类型的接口(VehiclePropertyType中的类型为int)
     *
     * @param propId :填receive表格中的，Android中自定义Key
     * @param area   :填receive表格中的：Area value
     * @return float
     */
    public int[] getIntArray(int propId, int area) {
        if (mCar != null && mCar.isConnected()) {
            return mCarPropertyManager.getIntArrayProperty(propId, area);
        } else {
            Log.w(TAG, "get int failed");
        }
        return new int[]{};
    }

    public void setIntProperty(int propId, int area, int val) {
        Log.w(TAG, String.format("app_AVM_setIntProperty propId: %s  area:%s val:%s ",propId,area,val));
        if (mCar != null && mCar.isConnected()) {
            mCarPropertyManager.setIntProperty(propId, area, val);
        } else {
            Log.w(TAG, "set int failed");
        }
    }

    public void setIntArray(int propId, int area, Integer[] intArray ) {

        Log.w(TAG, String.format("DIAG_31_ app_AVM_setIntProperty propId: %s  area:%s val:%s ",propId,area, Arrays.toString(intArray)));
        if (mCar != null && mCar.isConnected()) {
            mCarPropertyManager.setProperty(Integer[].class,propId, area,intArray);
        } else {
            Log.w(TAG, " DIAG_31_ set int failed");
        }
    }

    public void setByteArray(int propId, int area, byte[] byteArray ) {

        Log.w(TAG, String.format("DIAG_31_ app_AVM_setByteProperty propId: %s  area:%s val:%s ",propId,area, Arrays.toString(byteArray)));
        if (mCar != null && mCar.isConnected()) {
            mCarPropertyManager.setProperty(byte[].class,propId, area,byteArray);
        } else {
            Log.w(TAG, " DIAG_31_ set byte failed");
        }
    }



    /********************
     * 注册回调的ID
     * @param ids 监听的id
     * @return boolean
     */
    private boolean registerCarDataChangeCallBack(List<Integer> ids) {
        Log.d(TAG, "AVMTest Door status of right front door : " + getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_1_RIGHT));
        Log.d(TAG, "AVMTest Door status of left front door : " + getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_1_LEFT));
        Log.d(TAG, "AVMTest gear status : " + getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0));
        Log.d(TAG, "AVMTest car speed is " + getFloatStatus(VEHICLE_SPEED, 0));
        Log.d(TAG, "AVMTest turn lamp stats is " + getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, 0));
        //Log.d(TAG, "AVMTest gear status : " + getIntStatus())


        if (mCar != null && mCar.isConnected()) {
            if (ids != null) {
                int len = ids.size();
                for (int i = 0; i < len; i++) {
                    if (mCarPropertyManager != null) {
                        boolean isRegister = mCarPropertyManager.registerCallback(mCarPropertyEventCallback, ids.get(i),
                                CarPropertyManager.SENSOR_RATE_ONCHANGE);
                        if (!isRegister) {
                            Log.w(TAG, " register fail , " + ids.get(i));
                        } else {
                            Log.w(TAG, " register success , " + ids.get(i));
                        }
                    } else {
                        Log.w(TAG, "mCarPropertyManager == null , please check!!");
                    }
                }
                return true;
            }
        } else {
            Log.w(TAG, "register failed");
        }
        return false;
    }

    /*******************************
     * can协议信号 监听回调状态
     */
    private final CarPropertyManager.CarPropertyEventCallback mCarPropertyEventCallback =
            new CarPropertyManager.CarPropertyEventCallback() {

                @Override
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    int propertyId = carPropertyValue.getPropertyId();
                    Object value = carPropertyValue.getValue();
                    int status = carPropertyValue.getStatus();
                    if (propertyId == CLUSTER_VCU_GEAR_LVL_DISP && status == 1) {
                        Log.v(TAG, "无效信号 propertyId =" + propertyId);
                        return;
                    }
                    onSignalValveChanged(propertyId, value);
                }
                @Override
                public void onErrorEvent(int i, int i1) {
                    Log.e(TAG, "收到信号上报返回错误 ii =" + i + ",value-i1 =" + i1);
                }
            };


    private void onSignalValveChanged(int vehicleId, Object value) {
        mMainHandler.post(() -> {
            for (onSignalValueChangedListener l : mListener) {
                l.onValueChangedListener(vehicleId, value);
            }
        });
    }

    public void registerSignalListener(onSignalValueChangedListener listener) {
        mListener.add(listener);
    }

    public void unRegisterSignalListener(onSignalValueChangedListener listener) {
        mListener.remove(listener);
    }


    public interface onSignalValueChangedListener {

        void onValueChangedListener(int vehicleId, Object value);
    }
}
