/**
 * Author: gongXiaoBo
 * Date: 2024/7/16 16:16
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.autochips.avm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.SystemProperties;
import com.autochips.avm.util.TestDefine;
import com.avm.framwork.manager.CanManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.AVM_SELECT_STATE;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.VEHICLE_SPEED;

/**
 * @Author: gongXiaoBo
 * @Date: 2024/7/16 16:16
 * @Description: 描述
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class AvmRuntime {

    private static volatile AvmRuntime instance;

    //    private CfgItem current;
    private Context mContext;
    private DataSts dataSts;
    private List<CfgItem> configTable;
    private Thread thread;
    private byte[] syncObj = new byte[0];
    private List<ActionListener> actionListeners;
    private List<ActionListener> toRemoves;
    private final float SPEED_THRESHOLD = 30.f;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceive(intent);
        }
    };

    private AvmRuntime() { }

    public void init(Context context) {
//        current = new CfgItem();
        mContext = context;
        dataSts = new DataSts();
        configTable = new ArrayList<>();
        //非全景(状态0)
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-1-1
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE, DataDefine.EVT_RADAR_ACTIVE, DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-1-2
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-1-3
                new int[]{DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE, DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE, DataDefine.EVT_RADAR_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-1-4
                new int[]{DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE, DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-1-5
                new int[]{DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE, DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-2-1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_R,DataDefine.EVT_SHIFT_RVC_D,DataDefine.EVT_SHIFT_RVC_N},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
//        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-2-2
//                new int[]{DataDefine.GEAR_P},
//                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_TURN_LAMP},
//                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
//                new int[]{DataDefine.EVT_SHIFT_R},
//                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-1
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-2
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-3
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-7
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_MOVING, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_MOVING},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-8
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_MOVING, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_MOVING},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-9
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_MOVING, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_MOVING},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-10
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_MOVING, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_MOVING},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
//        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-11
//                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_MOVING, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_MOVING},
//                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
//                new int[]{DataDefine.MEM_MODE_3D},
//                new int[]{DataDefine.EVT_ACTIVE_ENTER},
//                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-12
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_MOVING, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_MOVING},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-13
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-14
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-15
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-16
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-17
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-17
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-3-18
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_ENTER},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE},
                null,
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD}));
//        CfgItem cfgItem = configTable.get(configTable.size()-1);
//        cfgItem.addEventAction2(new int[] {DataDefine.EVT_OVER_SPEED}, new int[] {DataDefine.FV_STATE_NON});
//        cfgItem.addEventAction3(new int[] {DataDefine.EVT_REDUCE_SPEED}, new int[] {DataDefine.FV_STATE_LEFT_CARD});
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 2
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE},
                null,
                new int[]{DataDefine.EVT_RADAR_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD}));
//        cfgItem = configTable.get(configTable.size()-1);
//        cfgItem.addEventAction2(new int[] {DataDefine.EVT_OVER_SPEED}, new int[] {DataDefine.FV_STATE_NON});
//        cfgItem.addEventAction3(new int[] {DataDefine.EVT_REDUCE_SPEED}, new int[] {DataDefine.FV_STATE_LEFT_CARD});
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                null,
                new int[]{DataDefine.EVT_TURN_LAMP_RESET_ACTIVE},
                new int[]{DataDefine.ACT_EXIT}));
        // ---------------------降速
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_REDUCE_SPEED1},
                new int[]{DataDefine.ACT_LEFT_CARD}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_REDUCE_SPEED2},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_REDUCE_SPEED2},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_REDUCE_SPEED2},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_REDUCE_SPEED2},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_REDUCE_SPEED2},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 3
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_REDUCE_SPEED2},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_REAR}));
        // 降速 ---------------------

        // 左卡片0.5
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 1
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        // 超速退出
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 3
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 4
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 5
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 6
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 7
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 8
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_RADAR_RESET},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2-1-9
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2-1-10
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2-1-11
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2-1-12
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET, DataDefine.EVT_RADAR_RESET},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2-1-13
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2-1-14
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2-1-14
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_RADAR_RESET},
                new int[]{DataDefine.ACT_EXIT}));

        // 左卡片
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 1
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 2
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 3
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 4
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 5
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 6
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 7
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 8
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 8
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 9
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 10
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 11
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 11
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 12
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 13
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_N_STOP},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 14
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_N_STOP},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 15
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_N_STOP},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 16
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 17
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,// 18
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW
                }));

        // 被动双卡片
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 2
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 4
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_D_OVER_SPEED, DataDefine.EVT_SHIFT_N_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 5
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 6
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 7
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_N_OVER_SPEED, DataDefine.EVT_SHIFT_D_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 8
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 9
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 10
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_D_OVER_SPEED, DataDefine.EVT_SHIFT_N_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 11
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
//        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 12
//                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
//                new int[]{DataDefine.SENSOR_NONE},
//                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
//                new int[]{DataDefine.EVT_KEEP_30S},
//                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 13
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 14
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_D_OVER_SPEED, DataDefine.EVT_SHIFT_N_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 15
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 16
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 17
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_D_OVER_SPEED, DataDefine.EVT_SHIFT_N_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 18
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 19
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 20
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 21
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-1
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-2
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-3
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-4
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-5
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-6
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-7
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-8
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-9
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-9
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-10
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-11
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-12
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_KEEP}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-13
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-14
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-14
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-15
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-16
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-17
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-18
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-19
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-21
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-21
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-22
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-23
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-24
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_KEEP}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-25
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-26
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 3-2-26
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        // 超速退出
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_OVER_SPEED},
                new int[]{DataDefine.ACT_EXIT}));

        // 主动双卡片
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 2
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 3
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 5
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 6
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 7
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 8
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 9
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_ACTIVE_EXIT},
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-1
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-2
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_REAR_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-3
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-4
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-5
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-6
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-7
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-8
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-9
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-9
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-10
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-11
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-12
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_KEEP}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-13
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-14
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-14
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-15
                new int[]{DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-16
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-17
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-18
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-19
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-20
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-21
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-21
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-22
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-23
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-24
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_KEEP}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-25
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-26
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-26
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 4-2-27
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));

        // add 2024-8-31
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_N, DataDefine.GEAR_D},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));

        // 2024-9-4
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_R},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));

        //20240905
        configTable.add(new CfgItem(DataDefine.FV_STATE_LEFT_CARD,  //当前显示全景状态
                new int[]{DataDefine.GEAR_N, DataDefine.GEAR_P, DataDefine.GEAR_D}, //支持显示的档位
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},//传感器状态
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D}, //记忆模式状态可执行
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},//事件
                new int[]{DataDefine.ACT_EXIT}));//执行动作

        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-1-1
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_NON,// 1-1-1
                new int[]{DataDefine.GEAR_P, DataDefine.GEAR_D, DataDefine.GEAR_N},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_P},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_P},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));

        // -----  如果设置了P档30s延时退出，会走到这里来
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_P},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_R},
                new int[]{DataDefine.SENSOR_NONE},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        // ---- 如果设置了P档30s延时退出，会走到这里来 --- //

        // ----- P档30s后退出 -- //
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,
                new int[]{DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR, DataDefine.SENSOR_RADAR_TURN_LAMP, DataDefine.SENSOR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_3D, DataDefine.MEM_MODE_2D, DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P_30S},
                new int[]{DataDefine.ACT_EXIT}));

        // 2d 3d切换视角后，挂挡恢复
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,//
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_PASSIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));

        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,//
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP, DataDefine.SENSOR_RADAR_TURN_LAMP},
                new int[]{DataDefine.MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(DataDefine.FV_STATE_ACTIVE_DUAL_CARD,// 1
                new int[]{DataDefine.GEAR_D, DataDefine.GEAR_N, DataDefine.GEAR_R, DataDefine.GEAR_P},
                new int[]{DataDefine.SENSOR_NONE, DataDefine.SENSOR_RADAR},
                new int[]{DataDefine.MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N},
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT}));

        KLog.d("configTable.size is " + configTable.size());

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (syncObj) {
                        try {
                            syncObj.wait(30100);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }

                        handleEvent();
                    }
                }
            }
        });
        thread.start();
    }

    public static AvmRuntime self() {
        if (instance == null) instance = new AvmRuntime();

        return instance;
    }

//    public void init(Context context) { mContext = context; }

    public void registerBroadcast(Context mContext) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TestDefine.CARD_STATUS);
        intentFilter.addAction(TestDefine.GEAR_STATUS);
        intentFilter.addAction(TestDefine.CURR_SPEED);
        intentFilter.addAction(TestDefine.EVT_TURN_LAMP_STS);
        intentFilter.addAction(TestDefine.EVT_RADAR_ACTIVE);
        intentFilter.addAction(TestDefine.EVT_CALIBRATION_START);
        intentFilter.addAction(TestDefine.SIM_WHEEL_SPEED);
        intentFilter.addAction(TestDefine.TEST_CALIBRATE);
        intentFilter.addAction(TestDefine.TEST_CALIBRATE_RESP);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void turnLampChange(int direction) {
        if (direction == 0) {
            synchronized (syncObj) {
                if (dataSts.sensors[0] == DataDefine.SENSOR_TURN_LAMP || dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                    // 有时不在转向激活状态，也会收到转向复位信号
                    setOverExitFlag(0);
                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_RESET);
                    dataSts.turnLampResetTime = System.currentTimeMillis();
                    dataSts.lastChangeTime = System.currentTimeMillis();

                    syncObj.notify();
                }
            }
        } else if (direction == 1 || direction == 3) {
            synchronized (syncObj) {
//                if ((System.currentTimeMillis() - dataSts.turnLampResetTime) > 800) {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
//                } else {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE);
//                }
                dataSts.lastSensorSrc = 1;
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_L_ACTIVE);
                if (dataSts.sensors[0] == DataDefine.SENSOR_RADAR) {
                    dataSts.events.add(DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE);
                }
                dataSts.lastChangeTime = System.currentTimeMillis();

                syncObj.notify();
            }
        } else if (direction == 2 || direction == 4) {
            synchronized (syncObj) {
//                if ((System.currentTimeMillis() - dataSts.turnLampResetTime) > 800) {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
//                } else {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE);
//                }
                dataSts.lastSensorSrc = 1;
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_R_ACTIVE);
                if (dataSts.sensors[0] == DataDefine.SENSOR_RADAR) {
                    dataSts.events.add(DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE);
                }
                dataSts.lastChangeTime = System.currentTimeMillis();

                syncObj.notify();
            }
        }
    }

    public void radarChange(boolean active) {
//        Log.d("AvmRuntime", Log.getStackTraceString(new Throwable()));
//        KLog.d("radarChange() : " + active + " currSpeed is " + (dataSts==null?0:dataSts.currSpeed));
        if (active != dataSts.radarAlive) {
            KLog.d("radarChange() : " + active);
        }

        if (active) {
            synchronized (syncObj) {
                if (dataSts.gears[0] == DataDefine.GEAR_P) {
                    KLog.d("Filter radar active when GEAR_P.");
                    return;
                }
//                if (dataSts.sensors[0] == DataDefine.SENSOR_RADAR || dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
//                    return;
//                }

                dataSts.lastSensorSrc = 2;
                if (dataSts.sensors[0] == DataDefine.SENSOR_TURN_LAMP || dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                    dataSts.events.add(DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE);
                }
                dataSts.events.add(DataDefine.EVT_RADAR_ACTIVE);
                dataSts.radarAlive = true;
                dataSts.lastChangeTime = System.currentTimeMillis();

                syncObj.notify();
            }
        } else {
            synchronized (syncObj) {
                if (dataSts.sensors[0] == DataDefine.SENSOR_RADAR || dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                    dataSts.events.add(DataDefine.EVT_RADAR_RESET);
                    dataSts.radarAlive = false;
                    dataSts.lastChangeTime = System.currentTimeMillis();

                    syncObj.notify();
                }
            }
        }
    }

    public void speedChange(float value) {
        //KLog.d("speedChange value is " + value);
        boolean flag = false;
        synchronized (syncObj) {
            dataSts.currSpeed = CameraViewModelHelper.mpsToKmh((Float) value);
            CameraViewModelHelper.getInstance().setSpeedValue(dataSts.currSpeed);
            CameraViewModelHelper.getInstance().setTransparentIndexTab();
            if (dataSts.overSpeedSts) {
                boolean isNormalSpeed = dataSts.currSpeed < 30 && !dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET) &&
                        dataSts.fvSts[0] == DataDefine.FV_STATE_NON && dataSts.otherOverSpeedSts;
                if (dataSts.currSpeed < 25 || isNormalSpeed/*SPEED_THRESHOLD*/) {
                    if (getOverExitFlag() == 1) {
                        dataSts.events.add(DataDefine.EVT_REDUCE_SPEED1);
                    } else if (getOverExitFlag() == 2) {
                        dataSts.events.add(DataDefine.EVT_REDUCE_SPEED2);
                    }
                    dataSts.overSpeedSts = false;
                    flag = true;
                }
            } else {
                dataSts.otherOverSpeedSts = dataSts.currSpeed > 30 && !dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET) &&
                        dataSts.fvSts[0] == DataDefine.FV_STATE_NON;
                if (dataSts.currSpeed > 35 || dataSts.otherOverSpeedSts/*SPEED_THRESHOLD*/) {
                    dataSts.events.add(DataDefine.EVT_OVER_SPEED);
                    dataSts.overSpeedSts = true;
                    flag = true;
                }
            }
            //KLog.d("speedChange value is " + dataSts.currSpeed +" flag:"+flag);
            if (flag) {
                syncObj.notify();
            }
        }
    }

    public void gearChange(int gear) {
        synchronized (syncObj) {
            KLog.d("gear change -> " + gear);
            if (dataSts.currSpeed == -1) {
                float speed = CanManager.getInstance().getFloatStatus(VEHICLE_SPEED, 0);
                dataSts.currSpeed = speed * 3.6f;
            }
            KLog.d("speed is " + dataSts.currSpeed);
            if (gear == 1) { // D
                dataSts.events.add(DataDefine.EVT_SHIFT_D);
            } else if (gear == 2) {// N
                dataSts.events.add(DataDefine.EVT_SHIFT_N);
            } else if (gear == 3) {// R
                dataSts.events.add(DataDefine.EVT_SHIFT_R);
            } else if (gear == 4) {// P
                dataSts.events.add(DataDefine.EVT_ACTIVE_EXIT);
                dataSts.events.add(DataDefine.EVT_SHIFT_P);
            }
            setOverExitFlag(0);
            dataSts.sensorBlockPExit = false;
            dataSts.delayBlockExit = false;
            dataSts.lastChangeTime = System.currentTimeMillis();
            syncObj.notify();
        }
    }

    public void artificialEnter() {
        synchronized (syncObj) {
            KLog.d(" artificialEnter(). ");
            setOverExitFlag(0);
            dataSts.events.add(DataDefine.EVT_ACTIVE_ENTER);
            dataSts.lastChangeTime = System.currentTimeMillis();

            syncObj.notify();
        }
    }

    public void artificialExit() {
        synchronized (syncObj) {
            KLog.d(" artificialExit(). ");
            if (BvAvmJNIHelper.CAMERA_TYPE == bvavmJNI.PROJ_AY5_T_ID) {
                setOverExitFlag(0);
                dataSts.events.add(DataDefine.EVT_ACTIVE_EXIT);
            } else {
                if (dataSts.gears[0] != DataDefine.GEAR_R) {
                    setOverExitFlag(0);
                    dataSts.events.add(DataDefine.EVT_ACTIVE_EXIT);
                }
            }
            dataSts.lastChangeTime = System.currentTimeMillis();

            syncObj.notify();
        }
    }

    public void userTap() {
        if (dataSts != null) {
            KLog.d("userTap()");
            synchronized (syncObj) {
                dataSts.lastChangeTime = System.currentTimeMillis();
                syncObj.notify();
            }
        }
    }

    public void updateChangeTime() {
        if (dataSts != null) dataSts.lastChangeTime = System.currentTimeMillis();
    }

    public void onViewAngleChanged(int viewAngle) {
        KLog.d(" onViewAngleChanged for " + viewAngle);
//        Log.d("AvmRuntime", Log.getStackTraceString(new Throwable()));
    }

    public boolean isShift2R() {
        return dataSts.extEvents.contains(DataDefine.EVT_SHIFT_R);
    }

    public void clickLeftCard() {
        synchronized (syncObj) {
            KLog.d(" clickLeftCard(). ");
            dataSts.events.add(DataDefine.EVT_CLICK_LEFT_CARD);
            dataSts.lastChangeTime = System.currentTimeMillis();

            syncObj.notify();
        }
    }

    public void setMemoryMode(int memory) {
//        Log.d("getOutsideTabIndex", Log.getStackTraceString(new Throwable()));
//        if (memory == DataDefine.MEM_MODE_2D && dataSts.extEvents.contains(DataDefine.EVT_SHIFT_R)) {
//            KLog.w("Shift R reject to MEM_MODE_2D.");
//            return;
//        }
        if (dataSts != null) {
            KLog.d(" setMemoryMode : " + DataDefine.id2String(memory));
            dataSts.memory = memory;
            dataSts.writeMemoryMode(memory);

            synchronized (syncObj) {
                if (dataSts.memory == DataDefine.MEM_MODE_2D) {
                    dataSts.events.add(DataDefine.EVT_SWITCH_2_2D);
                    dataSts.lastChangeTime = System.currentTimeMillis();

                    syncObj.notify();
                } else if (dataSts.memory == DataDefine.MEM_MODE_3D) {
                    dataSts.events.add(DataDefine.EVT_SWITCH_2_3D);
                    dataSts.lastChangeTime = System.currentTimeMillis();

                    syncObj.notify();
                } else if (dataSts.memory == DataDefine.MEM_MODE_WIDE_ANGLE) {
                    dataSts.events.add(DataDefine.EVT_SWITCH_2_WIDE_ANGLE);
                    dataSts.lastChangeTime = System.currentTimeMillis();

                    syncObj.notify();
                }
            }
        }
//        SPUtils.getInstance().put("runtime_memory", dataSts.memory);
    }

    public void onBroadcastReceive(Intent intent) {
        KLog.d(this + " onReceive : " + intent.getAction());
        if (intent.getAction().equals(TestDefine.CURR_SPEED)) {
            int kk = intent.getIntExtra("value", 0);
            speedChange(kk);
        } else if (intent.getAction().equals(TestDefine.EVT_TURN_LAMP_STS)) {
            int kk = intent.getIntExtra("value", 0);
            KLog.d("turn lamp value is " + kk);
            turnLampChange(kk);
        } else if (intent.getAction().equals(TestDefine.GEAR_STATUS)) {
            int gear = intent.getIntExtra("value", 0);
            gearChange(gear);
//            if (gear == 3) {
//                bvavmJNI.bwSetCarIsDgear(0);
//                bvavmJNI.bwSetCarIsBack((byte) 1);
//            } else if (gear == 1) {
//                bvavmJNI.bwSetCarIsDgear(1);
//                bvavmJNI.bwSetCarIsBack((byte) 0);
//            } else {
//                bvavmJNI.bwSetCarIsDgear(0);
//                bvavmJNI.bwSetCarIsBack((byte) 0);
//            }
            BvAvmJNIHelper.getInstance().updateTrajLineStatus(gear);
        } else if (intent.getAction().equals(TestDefine.EVT_RADAR_ACTIVE)) {
            int kk = intent.getIntExtra("value", 0);
            radarChange(kk == 1);
        } else if (intent.getAction().equals(TestDefine.EVT_CALIBRATION_START)) {
            AvmApp.getInstance().getCameraView().getViewModel().setManualCalibration1();
        } else if (intent.getAction().equals(TestDefine.SIM_WHEEL_SPEED)) {
            AvmApp.getInstance().getCameraView().getViewModel().simWheelSpeed();
        } else if (intent.getAction().equals(TestDefine.TEST_CALIBRATE)) {
            AvmApp.getInstance().getCameraView().getViewModel().setStartCalibration();
        } else if (intent.getAction().equals(TestDefine.TEST_CALIBRATE_RESP)) {
            AvmApp.getInstance().getCameraView().getViewModel().callCalibrateResp();
        }
    }

    public void registerActionListener(ActionListener listener) {
        if (actionListeners == null) actionListeners = new ArrayList<>();
        synchronized (syncObj) {
            actionListeners.add(listener);
        }
    }

    public void unregisterActionListener(ActionListener listener) {
        synchronized (syncObj) {
            if (toRemoves == null) toRemoves = new ArrayList<>();
            toRemoves.add(listener);
        }
    }

    public boolean isRearGearSts() {
        if (dataSts == null) return false;
        return dataSts.gears[0] == DataDefine.GEAR_R;
    }

    public boolean isParkGearSts() {
        return dataSts.gears[0] == DataDefine.GEAR_P;
    }

    public boolean isDriveGearSts() {
        return dataSts.gears[0] == DataDefine.GEAR_D;
    }

    public boolean isNullGearSts() {
        return dataSts.gears[0] == DataDefine.GEAR_N;
    }

    public void setOverExitFlag(int flag) {
        KLog.d("setOverExitFlag : " + flag);
        if (dataSts != null) {
            dataSts.overExitFlag = flag;
        }
    }

    public void setRadarPauseFlag(boolean flag) {
        KLog.d("setRadarPauseFlag : " + flag);
        if (dataSts != null) {
            dataSts.radarPause = flag;
        }
    }

    public int getOverExitFlag() {
        if (dataSts != null) return dataSts.overExitFlag;

        return 0;
    }

    public int getFullSceneSts() {
        return dataSts.fvSts[0];
    }

    public int getTurnDirect() {
        return dataSts.sensors[1];
    }

    public float getCurrentSped() {
        if (dataSts != null)
            return dataSts.currSpeed;
        return 0;
    }

    public List<Integer> getEvents() {
        return dataSts.extEvents;
    }

    public void setFullSceneSts(int sts) {
        dataSts.fvSts[0] = sts;
    }

    public int getMemoryType() {
        if (dataSts != null) return dataSts.memory;
        return DataDefine.MEM_MODE_2D;
    }

    /*
     * 该函数内部需要更新当前配置
     * */
    private void actionEnter(int act) {
        if (actionListeners != null) {
            for (ActionListener listener : actionListeners)
                listener.onEnter(act);
        }
    }

    private void actionExit(int act) {
        if (actionListeners != null) {
            for (ActionListener listener : actionListeners)
                listener.onExit(act);
        }
    }

    private void handleEvent() {
//        KLog.d("(System.currentTimeMillis() - dataSts.lastChangeTime) is " + (System.currentTimeMillis() - dataSts.lastChangeTime) + " , timing30sFlag = " + dataSts.timing30sFlag);
        if ((System.currentTimeMillis() - dataSts.lastChangeTime) > 30000) {
            if (dataSts.timing30sFlag) {// 开了P档延时30s退出，且avm显示的时候，挂了P档
                dataSts.timing30sFlag = false;
                KLog.d("--------------------- EVT_SHIFT_P_30S. ");
                dataSts.events.add(DataDefine.EVT_SHIFT_P_30S);
            }
//            dataSts.events.add(DataDefine.EVT_KEEP_30S);
        }
        KLog.d("DataSts is " + dataSts.toString());

        dataSts.extEvents.clear();
        dataSts.extEvents.addAll(dataSts.events);
        boolean flag = false;
        for (CfgItem cfgItem : configTable) {
            int[] actions = cfgItem.matchAction(dataSts);
            if (actions != null) {
                if (dataSts.events.contains(DataDefine.EVT_SHIFT_P)) {
                    if (SystemProperties.get("pExit").equals("1") && actions[0] == DataDefine.ACT_EXIT) {
                        if (dataSts.fvSts[0] == DataDefine.FV_STATE_LEFT_CARD && dataSts.sensors[0] == DataDefine.SENSOR_RADAR) {
                            //
                        } else {
                            KLog.w("break for shift P delay 30s exit.");
                            dataSts.delayBlockExit = true;
                            continue;
                        }
                    }
                }
                if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_ACTIVE)
                    || (dataSts.events.contains(DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE) && dataSts.sensors[0] == DataDefine.SENSOR_RADAR)) {
                    if (SystemProperties.get("signalActivates").equals("0") && dataSts.fvSts[0] == DataDefine.FV_STATE_NON) {
                        KLog.w("break for show avm 转向激活全景 不显示");
                        break;
                    }
                    if (dataSts.currSpeed > SPEED_THRESHOLD && dataSts.fvSts[0] == DataDefine.FV_STATE_NON && actions[0] == DataDefine.ACT_LEFT_CARD) {
                        KLog.w("速度大于30，转向及雷达不激活左卡片");
                        setOverExitFlag(1);//速度降下来的时候需要恢复
                        break;
                    }
                }
                if (dataSts.events.contains(DataDefine.EVT_RADAR_ACTIVE)
                        || (dataSts.events.contains(DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE) && dataSts.sensors[0] == DataDefine.SENSOR_TURN_LAMP)) {
                    if (dataSts.radarPause) {
                        KLog.w("雷达暂停激活左卡片中。。。");
                        break;
                    }
                }
                if (cfgItem.actions[0] == DataDefine.ACT_EXIT
                        && cfgItem.fvState == DataDefine.FV_STATE_LEFT_CARD
                        && dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET)
                        && dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                    KLog.w("转向灯复位的时候，雷达仍然处于激活状态，不退出左卡片。。。");
                    break;
                }
                flag = true;
                KLog.d("find match cfg : " + cfgItem);
                if (dataSts.actions != null) {
                    for (int a : dataSts.actions) {
                        actionExit(a);
                    }
                }
                for (int i = actions.length - 1; i >= 0; i--) {//倒序
                    if (actions[i] == DataDefine.ACT_EXIT) {
                        onExit();
                    }
                    actionEnter(actions[i]);
                }
//                for (int a : actions) {
//                    actionEnter(a);
//                }
                dataSts.actions = actions;

                if (cfgItem.actions[0] == DataDefine.ACT_EXIT && cfgItem.events[0] == DataDefine.EVT_OVER_SPEED) {
                    if (cfgItem.fvState == DataDefine.FV_STATE_LEFT_CARD) {
                        setOverExitFlag(1);
                    } else if (cfgItem.fvState == DataDefine.FV_STATE_PASSIVE_DUAL_CARD) {
                        setOverExitFlag(2);
                    }
                }
                if (cfgItem.actions[0] == DataDefine.ACT_EXIT
                        && cfgItem.fvState == DataDefine.FV_STATE_LEFT_CARD
                        && dataSts.events.contains(DataDefine.EVT_ACTIVE_EXIT)
                        && (dataSts.sensors[0] == DataDefine.SENSOR_RADAR || (dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP && dataSts.lastSensorSrc == 2))) {
                    if (!dataSts.events.contains(DataDefine.EVT_SHIFT_P)) setRadarPauseFlag(true);
                }
                if (cfgItem.actions[0] == DataDefine.ACT_PASSIVE_DUAL_CARD) {
                    dataSts.fvSts[0] = DataDefine.FV_STATE_PASSIVE_DUAL_CARD;
                    dataSts.fvSts[1] = DataDefine.INVALID;
                } else if (cfgItem.actions[0] == DataDefine.ACT_ACTIVE_DUAL_CARD) {
                    dataSts.fvSts[0] = DataDefine.FV_STATE_ACTIVE_DUAL_CARD;
                    dataSts.fvSts[1] = DataDefine.INVALID;
                } else if (cfgItem.actions[0] == DataDefine.ACT_EXIT) {
                    dataSts.fvSts[0] = DataDefine.FV_STATE_NON;
                    dataSts.fvSts[1] = DataDefine.INVALID;
                } else if (cfgItem.actions[0] == DataDefine.ACT_LEFT_CARD) {
                    dataSts.fvSts[0] = DataDefine.FV_STATE_LEFT_CARD;
                    dataSts.fvSts[1] = DataDefine.INVALID;
                }

                break;
            }
        }

        if (dataSts.events.contains(DataDefine.EVT_SHIFT_D)) { // D
            dataSts.gears[0] = DataDefine.GEAR_D;
            if (dataSts.currSpeed == 0.f) {
                dataSts.gears[1] = DataDefine.GEAR_D_STOP;
                dataSts.gears[2] = DataDefine.INVALID;
            } else if (dataSts.currSpeed < SPEED_THRESHOLD) {
                dataSts.gears[1] = DataDefine.GEAR_D_LOW_RATE;
                dataSts.gears[2] = DataDefine.GEAR_D_MOVING;
            } else {
                dataSts.gears[1] = DataDefine.INVALID;
                dataSts.gears[2] = DataDefine.GEAR_D_MOVING;
            }
        } else if (dataSts.events.contains(DataDefine.EVT_SHIFT_N)) {// N
            dataSts.gears[0] = DataDefine.GEAR_N;
            if (dataSts.currSpeed == 0.f) {
                dataSts.gears[1] = DataDefine.GEAR_N_STOP;
                dataSts.gears[2] = DataDefine.INVALID;
            } else if (dataSts.currSpeed < SPEED_THRESHOLD) {
                dataSts.gears[1] = DataDefine.GEAR_N_LOW_RATE;
                dataSts.gears[2] = DataDefine.GEAR_N_MOVING;
            } else {
                dataSts.gears[1] = DataDefine.INVALID;
                dataSts.gears[2] = DataDefine.GEAR_N_MOVING;
            }
        } else if (dataSts.events.contains(DataDefine.EVT_SHIFT_R)) {// R
            dataSts.gears[0] = DataDefine.GEAR_R;
            if (dataSts.currSpeed == 0.f) {
                dataSts.gears[1] = DataDefine.GEAR_R_STOP;
                dataSts.gears[2] = DataDefine.INVALID;
            } else if (dataSts.currSpeed < SPEED_THRESHOLD) {
                dataSts.gears[1] = DataDefine.GEAR_R_LOW_RATE;
                dataSts.gears[2] = DataDefine.INVALID;
            } else {
                dataSts.gears[1] = DataDefine.INVALID;
                dataSts.gears[2] = DataDefine.INVALID;
            }
        } else if (dataSts.events.contains(DataDefine.EVT_SHIFT_P)) {// P
            dataSts.gears[0] = DataDefine.GEAR_P;
            dataSts.gears[1] = DataDefine.INVALID;
            dataSts.gears[2] = DataDefine.INVALID;
        }

        if (dataSts.events.contains(DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE)) {
            dataSts.sensors[0] = DataDefine.SENSOR_RADAR_TURN_LAMP;
            if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_L_ACTIVE)) {
                dataSts.sensors[1] = DataDefine.SENSOR_TURN_LAMP_LEFT;
            } else if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_R_ACTIVE)) {
                dataSts.sensors[1] = DataDefine.SENSOR_TURN_LAMP_RIGHT;
            }
//            dataSts.sensors[2] = DataDefine.SENSOR_RADAR;
//            dataSts.sensors[3] = DataDefine.SENSOR_RADAR_TURN_LAMP;
        } else if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_ACTIVE)
                || dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE)) {
            dataSts.sensors[0] = DataDefine.SENSOR_TURN_LAMP;
            if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_L_ACTIVE)) {
                dataSts.sensors[1] = DataDefine.SENSOR_TURN_LAMP_LEFT;
            } else if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_R_ACTIVE)) {
                dataSts.sensors[1] = DataDefine.SENSOR_TURN_LAMP_RIGHT;
            }
//            dataSts.sensors[2] = DataDefine.SENSOR_NONE;
//            dataSts.sensors[3] = DataDefine.SENSOR_NONE;
        } else if (dataSts.events.contains(DataDefine.EVT_RADAR_ACTIVE)) {
            dataSts.sensors[0] = DataDefine.SENSOR_RADAR;
            dataSts.sensors[1] = DataDefine.SENSOR_NONE;
//            dataSts.sensors[2] = DataDefine.SENSOR_RADAR;
//            dataSts.sensors[3] = DataDefine.SENSOR_NONE;
        } else if (dataSts.events.contains(DataDefine.EVT_RADAR_RESET)) {
            if (dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                dataSts.sensors[0] = DataDefine.SENSOR_TURN_LAMP;
            } else {
                dataSts.sensors[0] = DataDefine.SENSOR_NONE;
                dataSts.sensors[1] = DataDefine.SENSOR_NONE;
            }
//            dataSts.sensors[2] = DataDefine.SENSOR_NONE;
//            dataSts.sensors[3] = DataDefine.SENSOR_NONE;
        } else if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET)) {
            if (dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                dataSts.sensors[0] = DataDefine.SENSOR_RADAR;
                dataSts.sensors[1] = DataDefine.SENSOR_NONE;
            } else {
                dataSts.sensors[0] = DataDefine.SENSOR_NONE;
                dataSts.sensors[1] = DataDefine.SENSOR_NONE;
            }
        }

        if (dataSts.events.contains(DataDefine.EVT_SHIFT_R)
                || dataSts.events.contains(DataDefine.EVT_SHIFT_D)
                || dataSts.events.contains(DataDefine.EVT_SHIFT_P)
                || dataSts.events.contains(DataDefine.EVT_SHIFT_N)) {
            for (ActionListener listener : actionListeners) {
                listener.onGearNoAct(dataSts.gears[0], flag);
            }
        }

        updateTiming30sFlag();

        dataSts.events.clear();
        if (toRemoves != null) {
            actionListeners.removeAll(toRemoves);
            toRemoves = null;
        }
        KLog.d("handleEvent() end.");
    }

    public void updateTiming30sFlag() {
        // 变更 timing30sFlag 逻辑
        if (dataSts.events.size() > 0) {
            if (dataSts.events.contains(DataDefine.EVT_SWITCH_2_2D)
                || dataSts.events.contains(DataDefine.EVT_SWITCH_2_3D)
                || dataSts.events.contains(DataDefine.EVT_SWITCH_2_WIDE_ANGLE)) {
                return;
            }

            if (dataSts.events.contains(DataDefine.EVT_SHIFT_N) || dataSts.events.contains(DataDefine.EVT_SHIFT_D)) {
                if (dataSts.fvSts[0] == DataDefine.FV_STATE_LEFT_CARD || dataSts.fvSts[0] == DataDefine.FV_STATE_PASSIVE_DUAL_CARD) {
                    if (dataSts.sensors[0] == DataDefine.SENSOR_NONE) {
                        dataSts.timing30sFlag = true;
                        KLog.d("set timing30sFlag for EVT_SHIFT_ND.");
                        return;
                    }  else {
                        dataSts.sensorBlockPExit = true;
                    }
                }
            }
            if (dataSts.events.contains(DataDefine.EVT_SHIFT_P) && SystemProperties.get("pExit").equals("1") && dataSts.fvSts[0] != DataDefine.FV_STATE_NON) {
                if (dataSts.sensors[0] == DataDefine.SENSOR_NONE || dataSts.sensors[0] == DataDefine.SENSOR_RADAR) {
                    dataSts.timing30sFlag = true;
                    KLog.d("set timing30sFlag for EVT_SHIFT_P.");
                    return;
                } else {
                    dataSts.sensorBlockPExit = true;
                }
            }
            if (dataSts.events.contains(DataDefine.EVT_RADAR_RESET) && dataSts.fvSts[0] != DataDefine.FV_STATE_NON) {
                if (dataSts.timing30sFlag/*主动AVM，雷达激活的情况下挂P档*/) {//正在30s计时
                    return;
                }
                if (dataSts.sensors[0] == DataDefine.SENSOR_NONE) {
                    if (dataSts.fvSts[0] == DataDefine.FV_STATE_LEFT_CARD || dataSts.fvSts[0] == DataDefine.FV_STATE_PASSIVE_DUAL_CARD) {
                        if (dataSts.gears[0] != DataDefine.GEAR_R) {
                            dataSts.timing30sFlag = true;
                            KLog.d("set timing30sFlag for EVT_RADAR_RESET.");
                            return;
                        }
                    }
                    if (dataSts.fvSts[0] == DataDefine.FV_STATE_ACTIVE_DUAL_CARD) {
                        if (dataSts.sensorBlockPExit || dataSts.delayBlockExit) {
                            dataSts.delayBlockExit = false;
                            dataSts.sensorBlockPExit = false;
                            dataSts.timing30sFlag = true;
                            KLog.d("set timing30sFlag for EVT_RADAR_RESET.");
                            return;
                        }
                    }
                }
            }
            if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET) && dataSts.fvSts[0] != DataDefine.FV_STATE_NON) {
                if (dataSts.timing30sFlag/*主动AVM，转向激活的情况下挂P档*/) {//正在30s计时
                    return;
                }
                if (dataSts.sensors[0] == DataDefine.SENSOR_NONE) {
                    if (dataSts.fvSts[0] == DataDefine.FV_STATE_LEFT_CARD || dataSts.fvSts[0] == DataDefine.FV_STATE_PASSIVE_DUAL_CARD) {
                        if (dataSts.gears[0] != DataDefine.GEAR_R) {
                            dataSts.timing30sFlag = true;
                            KLog.d("set timing30sFlag for EVT_TURN_LAMP_RESET.");
                            return;
                        }
                    }
                    if (dataSts.fvSts[0] == DataDefine.FV_STATE_ACTIVE_DUAL_CARD) {
                        if (dataSts.sensorBlockPExit || dataSts.delayBlockExit) {
                            dataSts.sensorBlockPExit = false;
                            dataSts.delayBlockExit = false;
                            dataSts.timing30sFlag = true;
                            KLog.d("set timing30sFlag for EVT_TURN_LAMP_RESET.");
                            return;
                        }
                    }
                }

            }

            if (dataSts.timing30sFlag) {
                if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_ACTIVE)
                    || dataSts.events.contains(DataDefine.EVT_RADAR_ACTIVE)) {
                    KLog.d("Turn lamp active or radar active blocked 30s exit.");
                    dataSts.sensorBlockPExit = true;
                }
            }
            dataSts.timing30sFlag = false;
            KLog.d("+++ set time30sFlag to false.");
        }
    }

    private void onExit() {
        dataSts.delayBlockExit = false;
        dataSts.sensorBlockPExit = false;
    }

    class DataSts {
        float currSpeed = -1;
        int overExitFlag; // 0 none, 1 left_card exit, 2 full_screen exit
        boolean sensorBlockPExit = false;
        boolean delayBlockExit = false;
        boolean radarPause;
        boolean radarAlive;
        boolean turnLampAlive;
        boolean overSpeedSts; //超速状态
        boolean otherOverSpeedSts = false;//其他超速状态
        long lastChangeTime; //上次变更时间
        long turnLampResetTime;
        boolean timing30sFlag; // 30s计时标志
        int lastSensorSrc = 0; // 0 none, 1 turn lamp, 2 radar

        int[] fvSts; // 全景状态
        int[] gears; // 档位
        int[] sensors; // 雷达 转向灯
        List<Integer> events; // 挂挡、雷达切换、转向灯切换
        List<Integer> extEvents;
        int memory; // 记忆模式
        int[] actions; // 切换全景状态 切换视角

        DataSts() {
            radarPause = false;
            radarAlive = false;
            turnLampAlive = false;
            timing30sFlag = false;
            overExitFlag = 0;

            fvSts = new int[2];
            fvSts[0] = DataDefine.FV_STATE_NON;
            fvSts[1] = DataDefine.INVALID;
            sensors = new int[2];
            sensors[0] = DataDefine.SENSOR_NONE; // turn lamp, radar, radar_turn_lamp
            sensors[1] = DataDefine.SENSOR_NONE; // l or r turn lamp
//            sensors[2] = DataDefine.SENSOR_NONE; // radar
//            sensors[3] = DataDefine.SENSOR_NONE; // radar & turn lamp
            gears = new int[3];
            gears[0] = DataDefine.GEAR_P;
            gears[1] = DataDefine.INVALID;
            gears[2] = DataDefine.INVALID;
            memory = readMemoryMode();
            events = new ArrayList<>();
            extEvents = new ArrayList<>();
        }

        private void writeMemoryMode(int mem) {
            try {
                FileOutputStream fos = new FileOutputStream(mContext.getDataDir() + "/AvmRuntime.txt");
                fos.write(String.valueOf(mem).getBytes());
                fos.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        private int readMemoryMode() {
            try {
                byte[] buffer = new byte[8];
                FileInputStream fis = new FileInputStream(mContext.getDataDir() + "/AvmRuntime.txt");
                int len = fis.read(buffer);
                fis.close();
                if (len > 0) {
                    String str = new String(buffer, 0, len);
                    return Integer.decode(str);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
            }

            return DataDefine.MEM_MODE_2D;
        }

        @Override
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("DataSts (")
                    .append("\n delayBlockExit = " + delayBlockExit)
                    .append("\n, sensorBlockPExit = " + sensorBlockPExit)
                    .append("\n, timing30sFlag = " + timing30sFlag)
                    .append("\n, overExitFlag = " + overExitFlag)
                    .append("\n, current Speed = " + currSpeed)
                    .append("\n, radarAlive = " + radarAlive)
                    .append("\n, turnLampAlive = " + turnLampAlive)
                    .append("\n, overSpeedSts = " + overSpeedSts)
                    .append("\n, otherOverSpeedSts = " + otherOverSpeedSts)
                    .append("\n, lastChangeTime = " + lastChangeTime);
            for (int i = 0; i < fvSts.length; i++) {
                stringBuffer.append("\n, fvSts[" + i + "] = " + DataDefine.id2String(fvSts[i]));
            }
            for (int i = 0; i < gears.length; i++) {
                stringBuffer.append("\n, gears[" + i + "] = " + DataDefine.id2String(gears[i]));
            }
            for (int i = 0; i < sensors.length; i++) {
                stringBuffer.append("\n, sensors[" + i + "] = " + DataDefine.id2String(sensors[i]));
            }
            for (int i = 0; i < events.size(); i++) {
                stringBuffer.append("\n, events[" + i + "] = " + DataDefine.id2String(events.get(i)));
            }
            stringBuffer.append("\n, memory is " + DataDefine.id2String(memory));
            stringBuffer.append(" )");
            return stringBuffer.toString();
        }
    }

    class CfgItem {
        private int fvState;
        private int[] gears;
        private int[] sensorSts;
        private int[] memories;
        private int[] events;
        private int[] actions;
//        private int[] events2;
//        private int[] actions2;
//        private int[] events3;
//        private int[] actions3;

        CfgItem(int fvState, int[] gears, int[] activeSrcs, int[] memories, int[] events, int[] actions) {
            this.fvState = fvState;
            this.gears = gears;
            this.sensorSts = activeSrcs;
            this.memories = memories;
            this.events = events;
            this.actions = actions;
        }

//        void addEventAction2(int[] events, int[] actions) {
//            this.events2 = events;
//            this.actions2 = actions;
//        }
//
//        void addEventAction3(int[] events, int[] actions) {
//            this.events3 = events;
//            this.actions3 = actions;
//        }

        int[] matchAction(DataSts dataSts) {
            if (!fill(dataSts.fvSts, fvState)) return null;
            if (!fill(dataSts.gears, gears)) return null;
            if (!fill(dataSts.sensors, sensorSts)) return null;
            if (memories != null && !fill(dataSts.memory, memories)) return null;
            if (dataSts.events.size() == 0 || !fill(dataSts.events, events)) return null;
//            if (dataSts.events.size() > 1) {
//                if (dataSts.events.size() > 2) {
//                    if (fill(dataSts.events.get(2), events3)) return actions3;
//                }
//                if (fill(dataSts.events.get(1), events2)) return actions2;
//            }
//            if (other.fvState != other.fvState) return false;
//            if (!fill(other.gears, gears)) return false;
//            if (!fill(other.activeSrcs, activeSrcs)) return false;
//            if (memories != null) if (!fill(other.memories, memories)) return false;
//            if (!fill(other.events, events)) return false;

            return actions;
        }

        boolean fill(int[] sub, int[] owner) {//sub只要有一项在owner里面，则满足条件
            for (int s : sub) {
                for (int o : owner) {
                    if (s == o) return true;
                }
            }

            return false;
        }

        boolean fill(int sub, int[] owner) {
            for (int o : owner) {
                if (sub == o) return true;
            }

            return false;
        }

        boolean fill(int[] sub, int owner) {
            for (int s : sub) {
                if (s == owner) return true;
            }

            return false;
        }

        // owner只要满足subs之一，即为真
        boolean fill(List<Integer> subs, int[] owner) {
            for (int s : subs) {
                for (int o : owner) {
                    if (s == o) return true;
                }
            }

            return false;
        }

        @Override
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("CfgItem (")
                    .append("fvState = " + DataDefine.id2String(fvState));
            for (int i = 0; i < gears.length; i++) {
                stringBuffer.append("\n, gears[" + i + "] = " + DataDefine.id2String(gears[i]));
            }
            for (int i = 0; i < sensorSts.length; i++) {
                stringBuffer.append("\n, sensorSts[" + i + "] = " + DataDefine.id2String(sensorSts[i]));
            }
            for (int i = 0; i < memories.length; i++) {
                stringBuffer.append("\n, memories[" + i + "] = " + DataDefine.id2String(memories[i]));
            }
            for (int i = 0; i < events.length; i++) {
                stringBuffer.append("\n, events[" + i + "] = " + DataDefine.id2String(events[i]));
            }
            for (int i = 0; i < actions.length; i++) {
                stringBuffer.append("\n, actions[" + i + "] = " + DataDefine.id2String(actions[i]));
            }
            stringBuffer.append(" )");

            return stringBuffer.toString();
        }

    }

    public interface ActionListener {
        void onEnter(int act);

        void onExit(int act);

        void onGearNoAct(int gear, boolean handleFlag);
    }

}