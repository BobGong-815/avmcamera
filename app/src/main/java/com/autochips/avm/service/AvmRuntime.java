/**
 * Author: gongXiaoBo
 * Date: 2024/7/16 16:16
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.autochips.avm.service;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.VEHICLE_SPEED;

import android.content.Context;
import android.provider.Settings;

import com.autochips.avm.ai.Constant;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.ui.view.CameraGLSurfaceView;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.GlobalSetting;
import com.autochips.avm.util.StorageUtil;
import com.autochips.avm.util.SystemProperties;
import com.avm.framwork.manager.CanManager;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.Utils;

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

    private long SECOND_OF_30 = 30100;

    //    private CfgItem current;
    private Context mContext;
    private DataSts dataSts;
    private TurnRecord turnRecord;
    private List<CfgItem> configTable;
    private Thread thread;
    private byte[] syncObj = new byte[0];
    private List<ActionListener> actionListeners;
    private List<ActionListener> toRemoves;
    private final float SPEED_THRESHOLD = 30.f;
    private long WAIT_TIMEOUT = SECOND_OF_30;

    private AvmRuntime() { }

    public void init(Context context) {
//        current = new CfgItem();
        mContext = context;
        StorageUtil.self().init(context);
        dataSts = new DataSts();
        turnRecord = new TurnRecord();
        readGlobalSetting();
        configTable = new ArrayList<>();
        //非全景(状态0)
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_LEFT_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD, DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD},// 任何情况下，点x都可以退出AVM
                null,
                null,
                null,
                new int[]{DataDefine.EVT_OVER_SPEED, DataDefine.EVT_REDUCE_SPEED},
                null,
                new int[]{DataDefine.ACT_CONTINUE, DataDefine.ACT_REFRESH_LAND_TRANSPARENCY}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},// 任何情况下，点x都可以退出AVM
                new int[] {DataDefine.STS_GEAR_D, DataDefine.STS_GEAR_N},
                null,
                null,
                new int[]{DataDefine.EVT_SHIFT_P},
                null,
                new int[]{DataDefine.ACT_CONTINUE, DataDefine.ACT_ACTIVE_2_PASSIVE}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},// 任何情况下，点x都可以退出AVM
                new int[] {DataDefine.STS_GEAR_D, DataDefine.STS_GEAR_N, DataDefine.STS_GEAR_P},
                null,
                null,
                new int[]{DataDefine.EVT_SHIFT_R},
                null,
                new int[]{DataDefine.ACT_CONTINUE, DataDefine.ACT_ACTIVE_2_PASSIVE}));
        configTable.add(new CfgItem(null,// 任何情况下，点x都可以退出AVM
                null,
                null,
                null,
                new int[]{DataDefine.EVT_USER_CLICK_EXIT},
                null,
                new int[]{DataDefine.ACT_EXIT, DataDefine.ACT_DISABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_NON},
                null,
                null,
                null,
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE, DataDefine.EVT_TURN_LAMP_RESET_ACTIVE},
                new int[] {DataDefine.SWITCH_TURN_LAMP_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_NON},
                null,
                null,
                null,
                new int[]{DataDefine.EVT_RADAR_ACTIVE},
                new int[] {DataDefine.SWITCH_RADAR_ACTIVE},
                new int[]{DataDefine.ACT_LEFT_CARD, DataDefine.ACT_AERIAL_VIEW}));
        configTable.add(new CfgItem(null,
                null,
                null,
                null,
                new int[]{DataDefine.EVT_SHIFT_R,DataDefine.EVT_SHIFT_RVC_D,DataDefine.EVT_SHIFT_RVC_N},
                null,
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW, DataDefine.ACT_DISABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_NON},// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_USER_CLICK_ENTER},
                null,
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_NON},// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_USER_CLICK_ENTER},
                null,
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_LEFT_CARD},// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_LEFT_CARD},// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(null,
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[]{DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_USER_CLICK_ENTER, DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_2D_FRONT_VIEW, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(null,
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[]{DataDefine.STS_SENSOR_TURN_LAMP_LEFT},
                new int[]{DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_USER_CLICK_ENTER, DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_LEFT_REAR, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(null,
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[]{DataDefine.STS_SENSOR_TURN_LAMP_RIGHT},
                new int[]{DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_USER_CLICK_ENTER, DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_RIGHT_REAR, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_NON, DataDefine.STS_FV_STATE_LEFT_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[]{DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_USER_CLICK_ENTER, DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_3D_FRONT_VIEW, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_LEFT_CARD},// 4
                null,
                null,
                null,
                new int[]{DataDefine.EVT_TURN_LAMP_RESET, DataDefine.EVT_DOUBLE_BLINK, DataDefine.EVT_SHIFT_P},
                null,
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_NON, DataDefine.STS_FV_STATE_LEFT_CARD},
                null,
                null,
                new int[]{DataDefine.STS_MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_USER_CLICK_ENTER, DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_ACTIVE_DUAL_CARD, DataDefine.ACT_WIDE_ANGLE_FRONT, DataDefine.ACT_ENABLE_TIMING}));

        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD},// 1
                null,
                null,
                null,
                new int[]{DataDefine.EVT_SHIFT_P},
                new int[] {-1*DataDefine.SWITCH_DELAY_30S_EXIT},
                new int[]{DataDefine.ACT_EXIT, DataDefine.ACT_DISABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD},// 1
                new int[]{-1*DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_SHIFT_P_30S},
                null,
                new int[]{DataDefine.ACT_EXIT, DataDefine.ACT_DISABLE_TIMING}));


        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                null,
                new int[]{DataDefine.STS_SENSOR_TURN_LAMP_LEFT},
                new int[] {DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_P},
                null,
                new int[]{DataDefine.ACT_REFRESH_TAB_INDEX, DataDefine.ACT_3D_LEFT_REAR, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                null,
                new int[]{DataDefine.STS_SENSOR_TURN_LAMP_RIGHT},
                new int[] {DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_P},
                null,
                new int[]{DataDefine.ACT_REFRESH_TAB_INDEX, DataDefine.ACT_3D_RIGHT_REAR, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},// 1
                new int[]{DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                null,
                new int[]{DataDefine.ACT_REFRESH_TAB_INDEX, DataDefine.ACT_3D_FRONT_VIEW, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},// 1
                new int[]{DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_WIDE_ANGLE},
                new int[]{DataDefine.EVT_SHIFT_P, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_D},
                null,
                new int[]{DataDefine.ACT_REFRESH_TAB_INDEX, DataDefine.ACT_WIDE_ANGLE_FRONT, DataDefine.ACT_ENABLE_TIMING}));

        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_LEFT},
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_P},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_LEFT_VIEW, DataDefine.ACT_2D_LR_FRONT, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_RIGHT},
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_P},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_RIGHT_VIEW, DataDefine.ACT_2D_LR_FRONT, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_LEFT_VIEW, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_RIGHT_VIEW, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_TOP_VIEW, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                null,
                null,
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_SHIFT_D, DataDefine.EVT_SHIFT_N, DataDefine.EVT_SHIFT_P},
                null,
                new int[]{DataDefine.ACT_2D_FRONT_VIEW, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_ACTIVE, DataDefine.EVT_TURN_LAMP_RESET_ACTIVE},
                null,
                new int[]{DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_LEFT_VIEW, DataDefine.ACT_2D_LR_REAR, DataDefine.ACT_ENABLE_TIMING}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_2D},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_RIGHT_VIEW, DataDefine.ACT_2D_LR_REAR, DataDefine.ACT_ENABLE_TIMING}));

        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[]{-1*DataDefine.STS_GEAR_R},
                new int[]{DataDefine.STS_SENSOR_TURN_LAMP_LEFT},
                null,
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                null,
                new int[]{DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[]{-1*DataDefine.STS_GEAR_R},
                new int[]{DataDefine.STS_SENSOR_TURN_LAMP_RIGHT},
                null,
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                null,
                new int[]{DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_L_ACTIVE},
                null,
                new int[]{DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_R_ACTIVE},
                null,
                new int[]{DataDefine.ACT_3D_RIGHT_REAR}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD, DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD},
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                new int[] {DataDefine.STS_MEM_MODE_3D},
                new int[]{DataDefine.EVT_TURN_LAMP_RESET, DataDefine.EVT_TURN_LAMP_RESET_ACTIVE},
                null,
                new int[]{DataDefine.ACT_3D_FRONT_VIEW}));

        // speed about
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_LEFT_CARD, DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD},
                null,
                null,
                null,
                new int[]{DataDefine.EVT_OVER_SPEED},
                null,
                new int[]{DataDefine.ACT_EXIT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_NON},
                null,
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP},
                null,
                new int[]{DataDefine.EVT_REDUCE_SPEED},
                new int[] {DataDefine.STS_REDUCE_SPEED_RESUME},
                new int[]{DataDefine.ACT_LEFT_CARD}));

        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD, DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD},// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_LEFT},
                null,
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_LEFT_VIEW, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD, DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD},// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                new int[] {DataDefine.STS_SENSOR_TURN_LAMP_RIGHT},
                null,
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_RIGHT_VIEW, DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(new int[] {DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD, DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD},// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_TOP_VIEW, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(null,// 1
                new int[] {DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_SWITCH_2_2D},
                null,
                new int[]{DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(null,// 1
                new int[] {DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_LEFT_CARD},
                null,
                new int[]{DataDefine.ACT_PASSIVE_DUAL_CARD, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(null,// 1
                null,
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_2D_TOP},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_TOP_VIEW, DataDefine.ACT_2D_FRONT_VIEW}));
        configTable.add(new CfgItem(null,// 1
                null,
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_2D_BOTTOM},
                null,
                new int[]{DataDefine.ACT_REFRESH_2D_BOTTOM_VIEW, DataDefine.ACT_2D_REAR_VIEW}));
        configTable.add(new CfgItem(null,// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                null,
                new int[]{DataDefine.ACT_3D_FRONT_VIEW}));
        configTable.add(new CfgItem(null,// 1
                new int[] {DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_SWITCH_2_3D},
                null,
                new int[]{DataDefine.ACT_3D_REAR_VIEW}));
        configTable.add(new CfgItem(null,// 1
                new int[] {-1*DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE, DataDefine.EVT_WIDE_ANGLE_FRONT},
                null,
                new int[]{DataDefine.ACT_REFRESH_TAB_INDEX, DataDefine.ACT_WIDE_ANGLE_FRONT}));
        configTable.add(new CfgItem(null,// 1
                new int[] {DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_SWITCH_2_WIDE_ANGLE, DataDefine.EVT_WIDE_ANGLE_REAR},
                null,
                new int[]{DataDefine.ACT_REFRESH_TAB_INDEX, DataDefine.ACT_WIDE_ANGLE_REAR}));
        configTable.add(new CfgItem(null,// 1
                null,
                null,
                null,
                new int[]{DataDefine.EVT_WIDE_ANGLE_REAR},
                null,
                new int[]{DataDefine.ACT_WIDE_ANGLE_REAR}));
        configTable.add(new CfgItem(null,
                null,
                null,
                null,
                new int[]{DataDefine.EVT_WIDE_ANGLE_REAR_WHEEL},
                null,
                new int[]{DataDefine.ACT_2D_LR_REAR}));
        configTable.add(new CfgItem(null,
                new int[] {DataDefine.STS_GEAR_R},
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_2D_LEFT, DataDefine.EVT_CLICK_2D_RIGHT},
                null,
                new int[]{DataDefine.ACT_2D_LR_REAR}));
        configTable.add(new CfgItem(null,
                null,
                null,
                null,
                new int[]{DataDefine.EVT_WIDE_ANGLE_FRONT_WHEEL, DataDefine.EVT_CLICK_2D_LEFT, DataDefine.EVT_CLICK_2D_RIGHT},
                null,
                new int[]{DataDefine.ACT_2D_LR_FRONT}));
        configTable.add(new CfgItem(null,
                null,
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_3D_LT},
                null,
                new int[]{DataDefine.ACT_3D_LEFT_FRONT}));
        configTable.add(new CfgItem(null,
                null,
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_3D_RT},
                null,
                new int[]{DataDefine.ACT_3D_RIGHT_FRONT}));
        configTable.add(new CfgItem(null,
                null,
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_3D_LB},
                null,
                new int[]{DataDefine.ACT_3D_LEFT_REAR}));
        configTable.add(new CfgItem(null,
                null,
                null,
                null,
                new int[]{DataDefine.EVT_CLICK_3D_RB},
                null,
                new int[]{DataDefine.ACT_3D_RIGHT_REAR}));

        KLog.d("configTable.size is " + configTable.size());

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (syncObj) {
                        try {
                            syncObj.wait(WAIT_TIMEOUT);
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

    public void turnLampChange(int direction) {
        KLog.d("turnLampChange : " + direction);
        if (direction == 0) {
            synchronized (syncObj) {
                if (dataSts.sensors.contains(DataDefine.STS_SENSOR_TURN_LAMP)) {
                    // 有时不在转向激活状态，也会收到转向复位信号
                    setOverExitFlag(0);
                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_RESET);
                    dataSts.turnLampResetTime = System.currentTimeMillis();
                    dataSts.lastChangeTime = System.currentTimeMillis();

                    syncObj.notify();
                }
            }
        } else if (direction == 1) {
            synchronized (syncObj) {
//                if ((System.currentTimeMillis() - dataSts.turnLampResetTime) > 800) {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
//                } else {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE);
//                }
                readGlobalSetting();

                dataSts.lastSensorSrc = 1;
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_L_ACTIVE);
                dataSts.lastChangeTime = System.currentTimeMillis();

                syncObj.notify();
            }
        } else if (direction == 2) {
            synchronized (syncObj) {
//                if ((System.currentTimeMillis() - dataSts.turnLampResetTime) > 800) {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
//                } else {
//                    dataSts.events.add(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE);
//                }
                readGlobalSetting();

                dataSts.lastSensorSrc = 1;
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_ACTIVE);
                dataSts.events.add(DataDefine.EVT_TURN_LAMP_R_ACTIVE);
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
//                if (dataSts.sensors[0] == DataDefine.SENSOR_RADAR || dataSts.sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
//                    return;
//                }

                dataSts.lastSensorSrc = 2;
                dataSts.events.add(DataDefine.EVT_RADAR_ACTIVE);
                dataSts.radarAlive = true;
                dataSts.lastChangeTime = System.currentTimeMillis();

                syncObj.notify();
            }
        } else {
            synchronized (syncObj) {
                if (dataSts.sensors.contains(DataDefine.STS_SENSOR_RADAR)) {
                    dataSts.events.add(DataDefine.EVT_RADAR_RESET);
                    dataSts.radarAlive = false;
                    dataSts.lastChangeTime = System.currentTimeMillis();

                    syncObj.notify();
                }
            }
        }
    }

    public void speedChange(float value) {
//        KLog.d("speedChange value is " + value);
        boolean flag = false;
        synchronized (syncObj) {
            dataSts.currSpeed = CameraViewModelHelper.mpsToKmh((Float) value);
            if (dataSts.overSpeedSts) {
                if (dataSts.currSpeed < 25) {
                    dataSts.events.add(DataDefine.EVT_REDUCE_SPEED);
                    dataSts.overSpeedSts = false;
                    flag = true;
                }
            } else {
                if (dataSts.currSpeed > 30) {
                    dataSts.events.add(DataDefine.EVT_OVER_SPEED);
                    dataSts.overSpeedSts = true;
                    flag = true;
                }
            }
            if (flag) {
                syncObj.notify();
            }
        }
    }

    public void gearChange(int gear) {
        synchronized (syncObj) {
            if (dataSts.currGear == gear) {
                return;
            }
            KLog.d("gear change -> " + gear);
            dataSts.currGear = gear;
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
            dataSts.events.add(DataDefine.EVT_USER_CLICK_ENTER);
            dataSts.lastChangeTime = System.currentTimeMillis();

            syncObj.notify();
        }
    }

    public void artificialExit(boolean userClick) {
        synchronized (syncObj) {
            KLog.d(" artificialExit(). ");
            if (userClick) {
                dataSts.events.add(DataDefine.EVT_USER_CLICK_EXIT);
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

    public void inputTurnValue(int value, long now) {
        turnRecord.inputValue(value, now);
    }

    public boolean isDoubleBlink() {
        return turnRecord.isDoubleBlink();
    }

    public boolean isShift2R() {
        return dataSts.extEvents.contains(DataDefine.EVT_SHIFT_R);
    }

//    public void clickLeftCard() {
//        synchronized (syncObj) {
//            KLog.d(" clickLeftCard(). ");
//            dataSts.events.add(DataDefine.EVT_CLICK_LEFT_CARD);
//            dataSts.lastChangeTime = System.currentTimeMillis();
//
//            syncObj.notify();
//        }
//    }

    private long doubleBlinkTime = 0;
    public void doubleBlink() {
        doubleBlinkTime = System.currentTimeMillis();
        if (dataSts != null) {
            synchronized (syncObj) {
                if (!dataSts.sensors.contains(DataDefine.STS_DOUBLE_BLINK)) {
                    dataSts.events.add(DataDefine.EVT_DOUBLE_BLINK);

                    syncObj.notify();
                }
            }
        }
    }

    public void userClick(int evt) {
//        Log.d("getOutsideTabIndex", Log.getStackTraceString(new Throwable()));
//        if (memory == DataDefine.MEM_MODE_2D && dataSts.extEvents.contains(DataDefine.EVT_SHIFT_R)) {
//            KLog.w("Shift R reject to MEM_MODE_2D.");
//            return;
//        }
        if (dataSts != null) {
            synchronized (syncObj) {
                dataSts.events.add(evt);
                dataSts.lastChangeTime = System.currentTimeMillis();

                syncObj.notify();
            }
        }
//        SPUtils.getInstance().put("runtime_memory", dataSts.memory);
    }


    public void registerActionListener(ActionListener listener) {
        if (actionListeners == null) actionListeners = new ArrayList<>();
        synchronized (syncObj) {
            actionListeners.add(listener);
        }
    }

    public void readGlobalSetting() {
        dataSts.switches.clear();

        try {
            int value = Settings.Global.getInt(AvmApp.getInstance().getContentResolver(), GlobalSetting.AVM_SETTING_RADAR_ACTIVATION);
            if (value == 1) {
                dataSts.switches.add(DataDefine.SWITCH_RADAR_ACTIVE);
            }
        } catch (Settings.SettingNotFoundException e) {
            dataSts.switches.add(DataDefine.SWITCH_RADAR_ACTIVE);
        }

        try {
            int value = Settings.Global.getInt(AvmApp.getInstance().getContentResolver(), GlobalSetting.AVM_SETTING_TURN_LIGHT_ACTIVATION);
            if (value == 1) {
                dataSts.switches.add(DataDefine.SWITCH_TURN_LAMP_ACTIVE);
            }
        } catch (Settings.SettingNotFoundException e) {
            dataSts.switches.add(DataDefine.SWITCH_TURN_LAMP_ACTIVE);
        }

        try {
            int value = Settings.Global.getInt(AvmApp.getInstance().getContentResolver(), GlobalSetting.AVM_SETTING_EXIT_P);
            if (value == 1) {
                dataSts.switches.add(DataDefine.SWITCH_DELAY_30S_EXIT);
            }
        } catch (Settings.SettingNotFoundException e) {
            dataSts.switches.add(DataDefine.SWITCH_DELAY_30S_EXIT);
        }

        KLog.d("dataSts.switches : " + dataSts.switches);
    }

    public void updateGlobalSetting(String key, boolean value) {
        if (key.equals(GlobalSetting.AVM_SETTING_RADAR_ACTIVATION)) {
            if (value) {
                if (!dataSts.switches.contains(DataDefine.SWITCH_RADAR_ACTIVE)) {
                    dataSts.switches.add(DataDefine.SWITCH_RADAR_ACTIVE);
                }
            } else {
                dataSts.switches.remove(Integer.valueOf(DataDefine.SWITCH_RADAR_ACTIVE));
            }
        } else if (key.equals(GlobalSetting.AVM_SETTING_TURN_LIGHT_ACTIVATION)) {
            if (value) {
                if (!dataSts.switches.contains(DataDefine.SWITCH_TURN_LAMP_ACTIVE)) {
                    dataSts.switches.add(DataDefine.SWITCH_TURN_LAMP_ACTIVE);
                }
            } else {
                dataSts.switches.remove(Integer.valueOf(DataDefine.SWITCH_TURN_LAMP_ACTIVE));
            }
        } else if (key.equals(GlobalSetting.AVM_SETTING_EXIT_P)) {
            if (value) {
                if (!dataSts.switches.contains(DataDefine.SWITCH_DELAY_30S_EXIT)) {
                    dataSts.switches.add(DataDefine.SWITCH_DELAY_30S_EXIT);
                }
            } else {
                dataSts.switches.remove(Integer.valueOf(DataDefine.SWITCH_DELAY_30S_EXIT));
            }
        }

        KLog.d("dataSts.switches : " + dataSts.switches);
    }

    public void unregisterActionListener(ActionListener listener) {
        synchronized (syncObj) {
            if (toRemoves == null) toRemoves = new ArrayList<>();
            toRemoves.add(listener);
        }
    }

    public boolean isRearGearSts() {
        if (dataSts == null) return false;
        return dataSts.gears[0] == DataDefine.STS_GEAR_R;
    }

    public boolean isActiveExit() {
        if (dataSts == null) return false;
        return dataSts.extEvents.contains(DataDefine.EVT_ACTIVE_EXIT);
    }

    public boolean isTurnActiveSts() {
        if (dataSts == null) return false;
        return dataSts.lastSensorSrc == 1;
    }

    public boolean isParkGearSts() {
        return dataSts.gears[0] == DataDefine.STS_GEAR_P;
    }

    public boolean isDriveGearSts() {
        return dataSts.gears[0] == DataDefine.STS_GEAR_D;
    }

    public boolean isNullGearSts() {
        return dataSts.gears[0] == DataDefine.STS_GEAR_N;
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
        return DataDefine.STS_MEM_MODE_2D;
    }

    /*
     * 该函数内部需要更新当前配置
     * */
    private void actionEnter(int act) {
        if (dataSts.switches.contains(DataDefine.SWITCH_DELAY_30S_EXIT)) {
            if (act == DataDefine.ACT_ENABLE_TIMING) {
                dataSts.timing30sFlag = true;
            } else if (act == DataDefine.ACT_DISABLE_TIMING) {
                dataSts.timing30sFlag = false;
            }
        }

        if (act == DataDefine.ACT_ACTIVE_DUAL_CARD
                || act == DataDefine.ACT_PASSIVE_DUAL_CARD
                || act == DataDefine.ACT_LEFT_CARD) {
            readGlobalSetting();
            WAIT_TIMEOUT = SECOND_OF_30;
        } else if (act == DataDefine.ACT_EXIT) {
            WAIT_TIMEOUT = 10*SECOND_OF_30;
        } else if (act == DataDefine.ACT_ACTIVE_2_PASSIVE) {
            dataSts.fvSts[0] = DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD;
        }

        if (act == DataDefine.ACT_EXIT) {
            if (dataSts.events.contains(DataDefine.EVT_OVER_SPEED) && dataSts.fvSts[0] == DataDefine.STS_FV_STATE_LEFT_CARD) {
                KLog.d("增加 降速需要恢复");
                dataSts.switches.add(DataDefine.STS_REDUCE_SPEED_RESUME);
            }
        } else {
            if (dataSts.switches.contains(DataDefine.STS_REDUCE_SPEED_RESUME)) {
                KLog.d("去掉 降速需要恢复");
                dataSts.switches.remove(Integer.valueOf(DataDefine.STS_REDUCE_SPEED_RESUME));
            }
        }

        int parm = 0;
        if (dataSts.events.contains(DataDefine.EVT_OVER_SPEED)) {
            parm = DataDefine.EVT_OVER_SPEED;
        } else if (dataSts.events.contains(DataDefine.EVT_REDUCE_SPEED)) {
            parm = DataDefine.EVT_REDUCE_SPEED;
        }
        if (actionListeners != null) {
            for (ActionListener listener : actionListeners) {
                listener.onEnter(act, parm);
            }
        }
    }

//    private void actionExit(int act) {
//        if (actionListeners != null) {
//            for (ActionListener listener : actionListeners) {
//                listener.onExit(act);
//            }
//        }
//    }

    private void handleEvent() {
        KLog.d("(System.currentTimeMillis() - dataSts.lastChangeTime) is " + (System.currentTimeMillis() - dataSts.lastChangeTime) + " , timing30sFlag = " + dataSts.timing30sFlag);
        if ((System.currentTimeMillis() - dataSts.lastChangeTime) > 30000) {
            if (dataSts.timing30sFlag) {// 开了P档延时30s退出，且avm显示的时候，挂了P档
                dataSts.timing30sFlag = false;
                KLog.d("--------------------- EVT_SHIFT_P_30S. ");
                dataSts.events.add(DataDefine.EVT_SHIFT_P_30S);
            }
//            dataSts.events.add(DataDefine.EVT_KEEP_30S);
        }
        if (!dataSts.events.contains(DataDefine.EVT_TOUCH_TAP)) {
            KLog.d("DataSts is " + dataSts.toString());
        }

        dataSts.extEvents.clear();
        dataSts.extEvents.addAll(dataSts.events);
        boolean flag = false;
        for (CfgItem cfgItem : configTable) {
            int[] actions = cfgItem.matchAction(dataSts);
            if (actions != null) {
                KLog.d("find match cfg : " + cfgItem);
//                if (dataSts.actions != null) {
//                    for (int a : dataSts.actions) {
//                        actionExit(a);
//                    }
//                }
                boolean continueFlag = false;
                for (int i = actions.length - 1; i >= 0; i--) {//倒序
                    if (actions[i] == DataDefine.ACT_CONTINUE) {
                        continueFlag = true;
                        break;
                    }
                    actionEnter(actions[i]);
                }
                if (continueFlag) {
                    KLog.d("continue.");
                    continue;
                }
//                for (int a : actions) {
//                    actionEnter(a);
//                }
                dataSts.actions = actions;

                if (cfgItem.actions[0] == DataDefine.ACT_EXIT && cfgItem.events[0] == DataDefine.EVT_OVER_SPEED) {
                    if (cfgItem.fvStates != null && cfgItem.fvStates[0] == DataDefine.STS_FV_STATE_LEFT_CARD) {
                        setOverExitFlag(1);
                    } else if (cfgItem.fvStates[0] == DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD) {
                        setOverExitFlag(2);
                    }
                }
                if (cfgItem.actions[0] == DataDefine.ACT_PASSIVE_DUAL_CARD) {
                    dataSts.fvSts[0] = DataDefine.STS_FV_STATE_PASSIVE_DUAL_CARD;
                } else if (cfgItem.actions[0] == DataDefine.ACT_ACTIVE_DUAL_CARD) {
                    dataSts.fvSts[0] = DataDefine.STS_FV_STATE_ACTIVE_DUAL_CARD;
                } else if (cfgItem.actions[0] == DataDefine.ACT_EXIT) {
                    dataSts.fvSts[0] = DataDefine.STS_FV_STATE_NON;
                } else if (cfgItem.actions[0] == DataDefine.ACT_LEFT_CARD) {
                    dataSts.fvSts[0] = DataDefine.STS_FV_STATE_LEFT_CARD;
                }

                break;
            }
        }

        if (dataSts.events.contains(DataDefine.EVT_SHIFT_D)) { // D
            dataSts.gears[0] = DataDefine.STS_GEAR_D;
            if (dataSts.currSpeed == 0.f) {
                dataSts.gears[1] = DataDefine.STS_GEAR_D_STOP;
                dataSts.gears[2] = DataDefine.INVALID;
            } else if (dataSts.currSpeed < SPEED_THRESHOLD) {
                dataSts.gears[1] = DataDefine.STS_GEAR_D_LOW_RATE;
                dataSts.gears[2] = DataDefine.STS_GEAR_D_MOVING;
            } else {
                dataSts.gears[1] = DataDefine.INVALID;
                dataSts.gears[2] = DataDefine.STS_GEAR_D_MOVING;
            }
        } else if (dataSts.events.contains(DataDefine.EVT_SHIFT_N)) {// N
            dataSts.gears[0] = DataDefine.STS_GEAR_N;
            if (dataSts.currSpeed == 0.f) {
                dataSts.gears[1] = DataDefine.STS_GEAR_N_STOP;
                dataSts.gears[2] = DataDefine.INVALID;
            } else if (dataSts.currSpeed < SPEED_THRESHOLD) {
                dataSts.gears[1] = DataDefine.STS_GEAR_N_LOW_RATE;
                dataSts.gears[2] = DataDefine.STS_GEAR_N_MOVING;
            } else {
                dataSts.gears[1] = DataDefine.INVALID;
                dataSts.gears[2] = DataDefine.STS_GEAR_N_MOVING;
            }
        } else if (dataSts.events.contains(DataDefine.EVT_SHIFT_R)) {// R
            dataSts.gears[0] = DataDefine.STS_GEAR_R;
            if (dataSts.currSpeed == 0.f) {
                dataSts.gears[1] = DataDefine.STS_GEAR_R_STOP;
                dataSts.gears[2] = DataDefine.INVALID;
            } else if (dataSts.currSpeed < SPEED_THRESHOLD) {
                dataSts.gears[1] = DataDefine.STS_GEAR_R_LOW_RATE;
                dataSts.gears[2] = DataDefine.INVALID;
            } else {
                dataSts.gears[1] = DataDefine.INVALID;
                dataSts.gears[2] = DataDefine.INVALID;
            }
        } else if (dataSts.events.contains(DataDefine.EVT_SHIFT_P)) {// P
            dataSts.gears[0] = DataDefine.STS_GEAR_P;
            dataSts.gears[1] = DataDefine.INVALID;
            dataSts.gears[2] = DataDefine.INVALID;
        }

        if (dataSts.events.contains(DataDefine.EVT_DOUBLE_BLINK)) {
            if (!dataSts.sensors.contains(DataDefine.STS_DOUBLE_BLINK)) {
                KLog.d("进入双闪");
                dataSts.sensors.add(DataDefine.STS_DOUBLE_BLINK);
            }
        } else {
            if (System.currentTimeMillis()-doubleBlinkTime > 600) {
                if (dataSts.sensors.contains(DataDefine.STS_DOUBLE_BLINK)) {
                    KLog.d("退出双闪");
                    dataSts.sensors.remove(Integer.valueOf(DataDefine.STS_DOUBLE_BLINK));
                }
            }
        }

        if (dataSts.events.contains(DataDefine.EVT_SWITCH_2_2D)) {
            dataSts.memory = DataDefine.STS_MEM_MODE_2D;
        } else if (dataSts.events.contains(DataDefine.EVT_SWITCH_2_3D)) {
            dataSts.memory = DataDefine.STS_MEM_MODE_3D;
        } else if (dataSts.events.contains(DataDefine.EVT_SWITCH_2_WIDE_ANGLE)) {
            dataSts.memory = DataDefine.STS_MEM_MODE_WIDE_ANGLE;
        }

        if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_ACTIVE)
                || dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE)) {
            if (!dataSts.sensors.contains(DataDefine.STS_SENSOR_TURN_LAMP)) {
                dataSts.sensors.add(DataDefine.STS_SENSOR_TURN_LAMP);
            }
            if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_L_ACTIVE)) {
                if (!dataSts.sensors.contains(DataDefine.STS_SENSOR_TURN_LAMP_LEFT)) {
                    dataSts.sensors.add(DataDefine.STS_SENSOR_TURN_LAMP_LEFT);
                }
            } else if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_R_ACTIVE)) {
                if (!dataSts.sensors.contains(DataDefine.STS_SENSOR_TURN_LAMP_RIGHT)) {
                    dataSts.sensors.add(DataDefine.STS_SENSOR_TURN_LAMP_RIGHT);
                }
            }
        } else if (dataSts.events.contains(DataDefine.EVT_RADAR_ACTIVE)) {
            if (!dataSts.sensors.contains(DataDefine.STS_SENSOR_RADAR)) {
                dataSts.sensors.add(DataDefine.STS_SENSOR_RADAR);
            }
        } else if (dataSts.events.contains(DataDefine.EVT_RADAR_RESET)) {
            dataSts.sensors.remove(Integer.valueOf(DataDefine.STS_SENSOR_RADAR));
        } else if (dataSts.events.contains(DataDefine.EVT_TURN_LAMP_RESET)) {
            dataSts.sensors.remove(Integer.valueOf(DataDefine.STS_SENSOR_TURN_LAMP));
            dataSts.sensors.remove(Integer.valueOf(DataDefine.STS_SENSOR_TURN_LAMP_LEFT));
            dataSts.sensors.remove(Integer.valueOf(DataDefine.STS_SENSOR_TURN_LAMP_RIGHT));
        }

        if (dataSts.events.contains(DataDefine.EVT_SHIFT_R)
                || dataSts.events.contains(DataDefine.EVT_SHIFT_D)
                || dataSts.events.contains(DataDefine.EVT_SHIFT_P)
                || dataSts.events.contains(DataDefine.EVT_SHIFT_N)) {
            for (ActionListener listener : actionListeners) {
                listener.onGearNoAct(dataSts.gears[0], flag);
            }
        }

//        updateTiming30sFlag();

        dataSts.events.clear();
        if (toRemoves != null) {
            actionListeners.removeAll(toRemoves);
            toRemoves = null;
        }
        KLog.d("handleEvent() end.");
    }

//    private void onExit() {
//        dataSts.delayBlockExit = false;
//        dataSts.sensorBlockPExit = false;
//    }

    class DataSts {
        int currGear = -1;
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
        List<Integer> sensors; // 雷达 转向灯
        List<Integer> events; // 挂挡、雷达切换、转向灯切换
        List<Integer> extEvents;
        int memory; // 记忆模式
        List<Integer> switches;
        int[] actions; // 切换全景状态 切换视角

        DataSts() {
            radarPause = false;
            radarAlive = false;
            turnLampAlive = false;
            timing30sFlag = false;
            overExitFlag = 0;

            fvSts = new int[1];
            fvSts[0] = DataDefine.STS_FV_STATE_NON;
            sensors = new ArrayList<>();
//            sensors[2] = DataDefine.SENSOR_NONE; // radar
//            sensors[3] = DataDefine.SENSOR_NONE; // radar & turn lamp
            gears = new int[3];
            gears[0] = DataDefine.STS_GEAR_P;
            gears[1] = DataDefine.INVALID;
            gears[2] = DataDefine.INVALID;
            memory = readMemoryMode();
            switches = new ArrayList<>();
            events = new ArrayList<>();
            extEvents = new ArrayList<>();
        }

        private void writeMemoryMode(int mem) {
            StorageUtil.self().writeInt("mem_mode", mem);
        }

        private int readMemoryMode() {
            return StorageUtil.self().readInt("mem_mode", DataDefine.STS_MEM_MODE_2D);
        }

        @Override
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("DataSts (")
                    .append("\n viewOfAngle = " + CameraGLSurfaceView.getAngleOfView())
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
            for (int i = 0; i < sensors.size(); i++) {
                stringBuffer.append("\n, sensors[" + i + "] = " + DataDefine.id2String(sensors.get(i)));
            }
            for (int i = 0; i < switches.size(); i++) {
                stringBuffer.append("\n, switches[" + i + "] = " + DataDefine.id2String(switches.get(i)));
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
        private int[] fvStates;
        private int[] gears;
        private int[] sensorSts;
        private int[] memories;
        private int[] events;
        private int[] switches;
        private int[] actions;

        CfgItem(int[] fvStates, int[] gears, int[] sensorSts, int[] memories, int[] events, int[] switches, int[] actions) {
            this.fvStates = fvStates;
            this.gears = gears;
            this.sensorSts = sensorSts;
            this.memories = memories;
            this.events = events;
            this.switches = switches;
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
            if (!fill(dataSts.fvSts, fvStates)) return null;
            if (!fill(dataSts.gears, gears)) return null;
            if (!fill(dataSts.sensors, sensorSts)) return null;
            if (!fill(dataSts.memory, memories)) return null;
            if (!fill(dataSts.events, events)) return null;
            if (!switchMatch(dataSts.switches, switches)) return null;
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

        boolean switchMatch(List<Integer> switchSts, int[] switchCfg) {
            if (switchCfg == null) return true;

            for (int sw : switches) {
                if (sw > 0) {
                    if (!switchSts.contains(sw)) {
                        return false;
                    }
                } else {
                    if (switchSts.contains(-1*sw)) {
                        return false;
                    }
                }
            }

            return true;
        }

        boolean fill(int[] sub, int[] owner) {//sub只要有一项在owner里面，则满足条件
            if (owner == null) return true;

            for (int oo : owner) {
                if (contain(sub, oo)) {
                    return true;
                }
            }

            return false;
        }

        boolean fill(int sub, int[] owner) {
            if (owner == null) return true;

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
            if (owner == null) return true;

            for (int s : subs) {
                for (int o : owner) {
                    if (s == o) return true;
                }
            }

            return false;
        }

        public boolean contain(int[] list, int value) {
            for (int i : list) {
                if (value > 0) {
                    if (i == value) return true;
                } else {
                    if (i == -1*value) return false;
                }
            }

            return value>0?false:true;
        }

        @Override
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("CfgItem (");
            if (fvStates != null) {
                for (int i = 0; i < fvStates.length; i++) {
                    stringBuffer.append("\n, fvStates[" + i + "] = " + DataDefine.id2String(fvStates[i]));
                }
            }
            if (gears != null) {
                for (int i = 0; i < gears.length; i++) {
                    stringBuffer.append("\n, gears[" + i + "] = " + DataDefine.id2String(gears[i]));
                }
            }
            if (sensorSts != null) {
                for (int i = 0; i < sensorSts.length; i++) {
                    stringBuffer.append("\n, sensorSts[" + i + "] = " + DataDefine.id2String(sensorSts[i]));
                }
            }
            if (memories != null) {
                for (int i = 0; i < memories.length; i++) {
                    stringBuffer.append("\n, memories[" + i + "] = " + DataDefine.id2String(memories[i]));
                }
            }
            if (events != null) {
                for (int i = 0; i < events.length; i++) {
                    stringBuffer.append("\n, events[" + i + "] = " + DataDefine.id2String(events[i]));
                }
            }
            if (switches != null) {
                for (int i = 0; i < switches.length; i++) {
                    stringBuffer.append("\n, switches[" + i + "] = " + DataDefine.id2String(switches[i]));
                }
            }
            if (actions != null) {
                for (int i = 0; i < actions.length; i++) {
                    stringBuffer.append("\n, actions[" + i + "] = " + DataDefine.id2String(actions[i]));
                }
            }
            stringBuffer.append(" )");

            return stringBuffer.toString();
        }

    }

    class TurnRecord {
        int valueIdx;
        int[] values;
        long updateTime;

        TurnRecord() {
            values = new int[] {-1,-1,-1,-1};
            updateTime = 0;
        }

        void inputValue(int value, long now) {
            if (now - updateTime > 500) {
                valueIdx = 0;
                values[0] = -1;
                values[1] = -1;
                values[2] = -1;
                values[3] = -1;
            } else {
                valueIdx = (valueIdx+1)%values.length;
            }
            values[valueIdx] = value;
            KLog.d("TurnRecord : " + values[0] + " " + values[1] + " " + values[2] + " " + values[3] + " -- " + (now-updateTime));
            updateTime = now;
        }

        boolean isDoubleBlink() {
            if (values[0] == 1 && values[1] == 1 && values[2] == 0 && values[3] == 0) {
                return true;
            } else if (values[0] == 1 && values[1] == 0 && values[2] == 0 && values[3] == 1) {
                return true;
            } else if (values[0] == 0 && values[1] == 0 && values[2] == 1 && values[3] == 1) {
                return true;
            } else if (values[0] == 0 && values[1] == 1 && values[2] == 1 && values[3] == 0) {
                return true;
            }

            return false;
        }

    }

    public interface ActionListener {
        void onEnter(int act, int parm);

        void onExit(int act);

        void onGearNoAct(int gear, boolean handleFlag);
    }

}