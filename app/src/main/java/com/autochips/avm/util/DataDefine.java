/**
 * Author: gongXiaoBo
 * Date: 2024/7/16 13:42
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.autochips.avm.util;

/**
 *
 *
 * @Author: gongXiaoBo
 * @Date: 2024/7/16 13:42
 * @Description: 描述
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class DataDefine {

    public final static int INVALID = -1;

    public final static int FV_STATE_NON = 1;
    public final static int FV_STATE_LEFT_CARD = 2;
    public final static int FV_STATE_PASSIVE_DUAL_CARD = 3;
    public final static int FV_STATE_ACTIVE_DUAL_CARD = 4;
    public final static int FV_STATE_OVER_SPEED_EXIT = 5;
    public final static int FV_STATE_ALL = 6;

    public final static int GEAR_P = 10; //当前档位 P
//    public final static int GEAR_P_STOP = 11;
    public final static int GEAR_R = 12; //当前档位 R
    public final static int GEAR_R_STOP = 13; //停车时档位 R
    public final static int GEAR_R_LOW_RATE = 14; //低速30Km的倒档
    public final static int GEAR_D = 15; //执行档位 D
    public final static int GEAR_D_STOP = 16; //停止时档位 直行档
    public final static int GEAR_D_LOW_RATE = 17; //低于30km/h的直行档
    public final static int GEAR_D_MOVING = 18; // 运行D档
    public final static int GEAR_N = 19; //当前档位 N 档
    public final static int GEAR_N_STOP = 20;  //当前停车时 N档
    public final static int GEAR_N_LOW_RATE = 21; //低速空挡
    public final static int GEAR_N_MOVING = 22;  //空挡运行

    public final static int SENSOR_NONE = 30;
    public final static int SENSOR_TURN_LAMP = 31;
    public final static int SENSOR_TURN_LAMP_RIGHT = 32;
    public final static int SENSOR_TURN_LAMP_LEFT = 33;
    public final static int SENSOR_RADAR = 34;
    public final static int SENSOR_RADAR_TURN_LAMP = 35;
//    public final static int ACTIVE_SRC_MANUAL = 33;

//    public final static int TURN_DIRECT_NON = 40;
//    public final static int TURN_DIRECT_LEFT = 41;
//    public final static int TURN_DIRECT_RIGHT = 42;

    public final static int MEM_MODE_2D = 50;
    public final static int MEM_MODE_3D = 51;
    public final static int MEM_MODE_WIDE_ANGLE = 52;

    public final static int EVT_TURN_LAMP_ACTIVE = 70;
    public final static int EVT_TURN_LAMP_RESET_ACTIVE = 71;
    public final static int EVT_TURN_LAMP_R_ACTIVE = 72;
    public final static int EVT_TURN_LAMP_L_ACTIVE = 73;
    public final static int EVT_TURN_LAMP_RESET = 74;
    public final static int EVT_RADAR_ACTIVE = 75;
    public final static int EVT_RADAR_TURN_LAMP_ACTIVE = 95;
    public final static int EVT_RADAR_RESET = 96;//障碍物消失
    public final static int EVT_SHIFT_R = 76;
    public final static int EVT_SHIFT_P = 77;
    public final static int EVT_SHIFT_D = 78;
    public final static int EVT_SHIFT_N = 79;
    public final static int EVT_SHIFT_RVC_N = 97;
    public final static int EVT_SHIFT_RVC_D = 98;
    public final static int EVT_SHIFT_P_30S = 80;
    public final static int EVT_SHIFT_D_OVER_SPEED = 81;
    public final static int EVT_SHIFT_N_OVER_SPEED = 82;
    public final static int EVT_ACTIVE_ENTER = 83;
    public final static int EVT_ACTIVE_EXIT = 84;
    public final static int EVT_OVER_SPEED = 85;  //超速
    public final static int EVT_REDUCE_SPEED1 = 86; // 小卡片退出降速
    public final static int EVT_REDUCE_SPEED2 = 87; // 全屏退出降速
//    public final static int EVT_OBSTACLE_DISAPPEAR = 87;
    public final static int EVT_CLICK_LEFT_CARD = 88;
    public final static int EVT_KEEP_30S = 89;
    public final static int EVT_SWITCH_2_2D = 90;
    public final static int EVT_SWITCH_2_3D = 91;
    public final static int EVT_SWITCH_2_WIDE_ANGLE = 92;
    public final static int EVT_CLICK_CAMERA_ICON = 93;
    public final static int EVT_TOUCH_TAP = 94;
    public final static int EVT_USER_CLICK_EXIT = 100;

    public final static int ACT_AERIAL_VIEW = 300; //鸟瞰图
    public final static int ACT_2D_REAR_VIEW = 301;//后视图
    public final static int ACT_2D_FRONT_VIEW = 302;//前视图
    public final static int ACT_2D_FRONT_OUTLINE = 303;
    public final static int ACT_2D_REAR_OUTLINE = 304;
    public final static int ACT_2D_LR = 305;
    public final static int ACT_3D_REAR_VIEW = 306;
    public final static int ACT_3D_FRONT_VIEW = 307;
    public final static int ACT_3D_LEFT_REAR = 308;
    public final static int ACT_3D_RIGHT_REAR = 309;
    public final static int ACT_WIDE_ANGLE_REAR = 310;
    public final static int ACT_WIDE_ANGLE_FRONT = 311;
    public final static int ACT_EXIT = 312;
    public final static int ACT_LEFT_CARD = 313;
    public final static int ACT_PASSIVE_DUAL_CARD = 314;
    public final static int ACT_ACTIVE_DUAL_CARD = 315;
    public final static int ACT_KEEP = 316;
    public final static int ACT_PREV_VIEW_ANGLE = 317;

    public static String id2String(int id) {
        switch (id) {
            case INVALID:
                return "INVALID";
            case FV_STATE_NON:
                return "FV_STATE_NON";
            case FV_STATE_LEFT_CARD:
                return "FV_STATE_LEFT_CARD";
            case FV_STATE_PASSIVE_DUAL_CARD:
                return "FV_STATE_PASSIVE_DUAL_CARD";
            case FV_STATE_ACTIVE_DUAL_CARD:
                return "FV_STATE_ACTIVE_DUAL_CARD";
            case GEAR_P:
                return "GEAR_P";
//            case GEAR_P_STOP:
//                return "GEAR_P_STOP";
            case GEAR_R:
                return "GEAR_R";
            case GEAR_R_STOP:
                return "GEAR_R_STOP";
            case GEAR_R_LOW_RATE:
                return "GEAR_R_LOW_RATE";
            case GEAR_D:
                return "GEAR_D";
            case GEAR_D_STOP:
                return "GEAR_D_STOP";
            case GEAR_D_LOW_RATE:
                return "GEAR_D_LOW_RATE";
            case GEAR_N:
                return "GEAR_N";
            case GEAR_N_STOP:
                return "GEAR_N_STOP";
            case GEAR_N_LOW_RATE:
                return "GEAR_N_LOW_RATE";
            case SENSOR_NONE:
                return "SENSOR_NONE";
            case EVT_TURN_LAMP_RESET_ACTIVE:
                return "EVT_TURN_LAMP_RESET_ACTIVE";
            case SENSOR_TURN_LAMP:
                return "SENSOR_TURN_LAMP";
            case SENSOR_TURN_LAMP_LEFT:
                return "SENSOR_TURN_LAMP_LEFT";
            case SENSOR_TURN_LAMP_RIGHT:
                return "SENSOR_TURN_LAMP_RIGHT";
            case SENSOR_RADAR:
                return "SENSOR_RADAR";
            case MEM_MODE_2D:
                return "MEM_MODE_2D";
            case MEM_MODE_3D:
                return "MEM_MODE_3D";
            case MEM_MODE_WIDE_ANGLE:
                return "MEM_MODE_WIDE_ANGLE";
            case EVT_TURN_LAMP_ACTIVE:
                return "EVT_TURN_LAMP_ACTIVE";
            case EVT_TURN_LAMP_L_ACTIVE:
                return "EVT_TURN_LAMP_L_ACTIVE";
            case EVT_TURN_LAMP_R_ACTIVE:
                return "EVT_TURN_LAMP_R_ACTIVE";
            case EVT_RADAR_ACTIVE:
                return "EVT_RADAR_ACTIVE";
            case EVT_TURN_LAMP_RESET:
                return "EVT_TURN_LAMP_RESET";
//            case EVT_RADAR_RESET_ACTIVE:
//                return "EVT_RADAR_RESET_ACTIVE";
            case EVT_SHIFT_R:
                return "EVT_SHIFT_R";
            case EVT_SHIFT_P:
                return "EVT_SHIFT_P";
            case EVT_SHIFT_D:
                return "EVT_SHIFT_D";
            case EVT_SHIFT_N:
                return "EVT_SHIFT_N";
            case EVT_SHIFT_P_30S:
                return "EVT_SHIFT_P_30S";
            case EVT_SHIFT_D_OVER_SPEED:
                return "EVT_SHIFT_D_OVER_SPEED";
            case EVT_SHIFT_N_OVER_SPEED:
                return "EVT_SHIFT_N_OVER_SPEED";
            case EVT_ACTIVE_ENTER:
                return "EVT_ACTIVE_ENTER";
            case EVT_ACTIVE_EXIT:
                return "EVT_ACTIVE_EXIT";
            case EVT_OVER_SPEED:
                return "EVT_OVER_SPEED";
            case EVT_REDUCE_SPEED1:
                return "EVT_REDUCE_SPEED1";
            case EVT_REDUCE_SPEED2:
                return "EVT_REDUCE_SPEED2";
//            case EVT_OBSTACLE_DISAPPEAR:
//                return "EVT_OBSTACLE_DISAPPEAR";
            case EVT_CLICK_LEFT_CARD:
                return "EVT_CLICK_LEFT_CARD";
            case EVT_KEEP_30S:
                return "EVT_KEEP_30S";
            case EVT_SWITCH_2_2D:
                return "EVT_SWITCH_2_2D";
            case EVT_SWITCH_2_3D:
                return "EVT_SWITCH_2_3D";
            case EVT_SWITCH_2_WIDE_ANGLE:
                return "EVT_SWITCH_2_WIDE_ANGLE";
            case EVT_CLICK_CAMERA_ICON:
                return "EVT_CLICK_CAMERA_ICON";
            case ACT_AERIAL_VIEW:
                return "ACT_AERIAL_VIEW";
            case ACT_2D_REAR_VIEW:
                return "ACT_2D_REAR_VIEW";
            case ACT_2D_FRONT_OUTLINE:
                return "ACT_2D_FRONT_OUTLINE";
            case ACT_2D_REAR_OUTLINE:
                return "ACT_2D_REAR_OUTLINE";
            case ACT_3D_REAR_VIEW:
                return "ACT_3D_REAR_VIEW";
            case ACT_3D_FRONT_VIEW:
                return "ACT_3D_FRONT_VIEW";
            case ACT_3D_LEFT_REAR:
                return "ACT_3D_LEFT_REAR";
            case ACT_3D_RIGHT_REAR:
                return "ACT_3D_RIGHT_REAR";
            case ACT_WIDE_ANGLE_REAR:
                return "ACT_WIDE_ANGLE_REAR";
            case ACT_WIDE_ANGLE_FRONT:
                return "ACT_WIDE_ANGLE_FRONT";
            case ACT_2D_FRONT_VIEW:
                return "ACT_2D_FRONT_VIEW";
            case ACT_EXIT:
                return "ACT_EXIT";
            case ACT_LEFT_CARD:
                return "ACT_LEFT_CARD";
            case ACT_PASSIVE_DUAL_CARD:
                return "ACT_PASSIVE_DUAL_CARD";
            case ACT_KEEP:
                return "ACT_KEEP";
            case ACT_ACTIVE_DUAL_CARD:
                return "ACT_ACTIVE_DUAL_CARD";
            case ACT_2D_LR:
                return "ACT_2D_LR";
            case ACT_PREV_VIEW_ANGLE:
                return "ACT_PREV_VIEW_ANGLE";
            case SENSOR_RADAR_TURN_LAMP:
                return "SENSOR_RADAR_TURN_LAMP";
            case EVT_RADAR_TURN_LAMP_ACTIVE:
                return "EVT_RADAR_TURN_LAMP_ACTIVE";
            case EVT_RADAR_RESET:
                return "EVT_RADAR_RESET";
            case EVT_TOUCH_TAP:
                return "EVT_TOUCH_TAP";
            case GEAR_D_MOVING:
                return "GEAR_D_MOVING";
            case GEAR_N_MOVING:
                return "GEAR_N_MOVING";
            case EVT_USER_CLICK_EXIT:
                return "EVT_USER_CLICK_EXIT";

        }

        return String.valueOf(id);
    }

}