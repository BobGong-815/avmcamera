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

    public final static int STS_FV_STATE_NON = 1;
    public final static int STS_FV_STATE_LEFT_CARD = 2;
    public final static int STS_FV_STATE_PASSIVE_DUAL_CARD = 3;
    public final static int STS_FV_STATE_ACTIVE_DUAL_CARD = 4;
    public final static int STS_FV_STATE_OVER_SPEED_EXIT = 5;

    public final static int STS_GEAR_P = 10; //当前档位 P
//    public final static int GEAR_P_STOP = 11;
    public final static int STS_GEAR_R = 12; //当前档位 R
    public final static int STS_GEAR_R_STOP = 13; //停车时档位 R
    public final static int STS_GEAR_R_LOW_RATE = 14; //低速30Km的倒档
    public final static int STS_GEAR_D = 15; //执行档位 D
    public final static int STS_GEAR_D_STOP = 16; //停止时档位 直行档
    public final static int STS_GEAR_D_LOW_RATE = 17; //低于30km/h的直行档
    public final static int STS_GEAR_D_MOVING = 18; // 运行D档
    public final static int STS_GEAR_N = 19; //当前档位 N 档
    public final static int STS_GEAR_N_STOP = 20;  //当前停车时 N档
    public final static int STS_GEAR_N_LOW_RATE = 21; //低速空挡
    public final static int STS_GEAR_N_MOVING = 22;  //空挡运行

//    public final static int STS_SENSOR_NONE = 30;
    public final static int STS_SENSOR_TURN_LAMP = 31;
    public final static int STS_SENSOR_TURN_LAMP_RIGHT = 32;
    public final static int STS_SENSOR_TURN_LAMP_LEFT = 33;
    public final static int STS_SENSOR_RADAR = 34;
    public final static int STS_SENSOR_RADAR_TURN_LAMP = 35;
    public final static int STS_DOUBLE_BLINK = 36;
    public final static int STS_REDUCE_SPEED_RESUME = 37; // 降速后需要恢复

    public final static int STS_MEM_MODE_2D = 50;
    public final static int STS_MEM_MODE_3D = 51;
    public final static int STS_MEM_MODE_WIDE_ANGLE = 52;

    public final static int EVT_TURN_LAMP_ACTIVE = 70;
    public final static int EVT_TURN_LAMP_RESET_ACTIVE = 71;
    public final static int EVT_TURN_LAMP_R_ACTIVE = 72;
    public final static int EVT_TURN_LAMP_L_ACTIVE = 73;
    public final static int EVT_TURN_LAMP_RESET = 74;
    public final static int EVT_RADAR_ACTIVE = 75;
    public final static int EVT_RADAR_TURN_LAMP_ACTIVE = 76;
    public final static int EVT_RADAR_RESET = 77;//障碍物消失
    public final static int EVT_SHIFT_R = 78;
    public final static int EVT_SHIFT_P = 79;
    public final static int EVT_SHIFT_D = 80;
    public final static int EVT_SHIFT_N = 81;
    public final static int EVT_SHIFT_RVC_N = 82;
    public final static int EVT_SHIFT_RVC_D = 83;
    public final static int EVT_SHIFT_P_30S = 84;
    public final static int EVT_SHIFT_D_OVER_SPEED = 85;
    public final static int EVT_SHIFT_N_OVER_SPEED = 86;
    public final static int EVT_USER_CLICK_ENTER = 87;
    public final static int EVT_ACTIVE_EXIT = 88;
    public final static int EVT_OVER_SPEED = 89;  //超速
    public final static int EVT_REDUCE_SPEED = 90; // 小卡片退出降速
//    public final static int EVT_OBSTACLE_DISAPPEAR = 87;
    public final static int EVT_CLICK_LEFT_CARD = 92;
    public final static int EVT_KEEP_30S = 93;
    public final static int EVT_SWITCH_2_2D = 94;
    public final static int EVT_SWITCH_2_3D = 95;
    public final static int EVT_SWITCH_2_WIDE_ANGLE = 96;
    public final static int EVT_CLICK_CAMERA_ICON = 97;
    public final static int EVT_TOUCH_TAP = 98;
    public final static int EVT_WIDE_ANGLE_FRONT = 99;
    public final static int EVT_WIDE_ANGLE_REAR = 100;
    public final static int EVT_WIDE_ANGLE_FRONT_WHEEL = 101;
    public final static int EVT_WIDE_ANGLE_REAR_WHEEL = 102;
    public final static int EVT_USER_CLICK_EXIT = 103;
    public final static int EVT_CLICK_2D_TOP = 104;
    public final static int EVT_CLICK_2D_BOTTOM = 105;
    public final static int EVT_CLICK_2D_LEFT = 106;
    public final static int EVT_CLICK_2D_RIGHT = 107;
    public final static int EVT_CLICK_3D_LT = 108;
    public final static int EVT_CLICK_3D_RT = 109;
    public final static int EVT_CLICK_3D_LB = 110;
    public final static int EVT_CLICK_3D_RB = 111;
    public final static int EVT_ON_DOUBLE_BLINK = 112; // 开双闪
    public final static int EVT_OFF_DOUBLE_BLINK = 113; // 开双闪

    public final static int SWITCH_RADAR_ACTIVE = 200;
    public final static int SWITCH_TURN_LAMP_ACTIVE = 201;
    public final static int SWITCH_DELAY_30S_EXIT = 202;

    public final static int ACT_AERIAL_VIEW = 300; //鸟瞰图
    public final static int ACT_2D_REAR_VIEW = 301;//后视图
    public final static int ACT_2D_FRONT_VIEW = 302;//前视图
    public final static int ACT_2D_FRONT_OUTLINE = 303;
    public final static int ACT_2D_REAR_OUTLINE = 304;
    public final static int ACT_2D_LR_FRONT = 305;
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
//    public final static int ACT_2D_FRONT_UNDISTORT = 318;
    public final static int ACT_2D_LR_REAR = 319;
    public final static int ACT_3D_LEFT_FRONT = 320;
    public final static int ACT_3D_RIGHT_FRONT = 321;
    public final static int ACT_2D_TOP = 322;

    public final static int ACT_ENABLE_TIMING = 330;
    public final static int ACT_DISABLE_TIMING = 331;
    public final static int ACT_REFRESH_TAB_INDEX = 332;
    public final static int ACT_REFRESH_2D_TOP_VIEW = 333;
    public final static int ACT_REFRESH_2D_LEFT_VIEW = 334;
    public final static int ACT_REFRESH_2D_RIGHT_VIEW = 335;
    public final static int ACT_REFRESH_2D_BOTTOM_VIEW = 336;
    public final static int ACT_REFRESH_3D_LEFT_FRONT = 337;
    public final static int ACT_REFRESH_3D_LEFT_REAR = 338;
    public final static int ACT_REFRESH_3D_RIGHT_FRONT = 339;
    public final static int ACT_REFRESH_3D_RIGHT_REAR = 340;
    public final static int ACT_REFRESH_LAND_TRANSPARENCY = 341;
    public final static int ACT_ACTIVE_2_PASSIVE = 342;
    public final static int ACT_REFRESH_3D_FRONT = 343;
    public final static int ACT_REFRESH_3D_REAR = 344;
    public final static int ACT_CONTINUE = 350;

    public static String id2String(int id) {
        switch (id) {
            case INVALID:
                return "INVALID";
            case STS_FV_STATE_NON:
                return "STS_FV_STATE_NON";
            case STS_FV_STATE_LEFT_CARD:
                return "STS_FV_STATE_LEFT_CARD";
            case STS_FV_STATE_PASSIVE_DUAL_CARD:
                return "STS_FV_STATE_PASSIVE_DUAL_CARD";
            case STS_FV_STATE_ACTIVE_DUAL_CARD:
                return "STS_FV_STATE_ACTIVE_DUAL_CARD";
            case STS_GEAR_P:
                return "STS_GEAR_P";
//            case GEAR_P_STOP:
//                return "GEAR_P_STOP";
            case STS_GEAR_R:
                return "STS_GEAR_R";
            case STS_GEAR_R_STOP:
                return "STS_GEAR_R_STOP";
            case STS_GEAR_R_LOW_RATE:
                return "STS_GEAR_R_LOW_RATE";
            case STS_GEAR_D:
                return "STS_GEAR_D";
            case STS_GEAR_D_STOP:
                return "STS_GEAR_D_STOP";
            case STS_GEAR_D_LOW_RATE:
                return "STS_GEAR_D_LOW_RATE";
            case STS_GEAR_N:
                return "STS_GEAR_N";
            case STS_GEAR_N_STOP:
                return "STS_GEAR_N_STOP";
            case STS_GEAR_N_LOW_RATE:
                return "STS_GEAR_N_LOW_RATE";
//            case STS_SENSOR_NONE:
//                return "STS_SENSOR_NONE";
            case EVT_TURN_LAMP_RESET_ACTIVE:
                return "EVT_TURN_LAMP_RESET_ACTIVE";
            case STS_SENSOR_TURN_LAMP:
                return "STS_SENSOR_TURN_LAMP";
            case STS_SENSOR_TURN_LAMP_LEFT:
                return "STS_SENSOR_TURN_LAMP_LEFT";
            case STS_SENSOR_TURN_LAMP_RIGHT:
                return "STS_SENSOR_TURN_LAMP_RIGHT";
            case STS_SENSOR_RADAR:
                return "STS_SENSOR_RADAR";
            case STS_MEM_MODE_2D:
                return "STS_MEM_MODE_2D";
            case STS_MEM_MODE_3D:
                return "STS_MEM_MODE_3D";
            case STS_MEM_MODE_WIDE_ANGLE:
                return "STS_MEM_MODE_WIDE_ANGLE";
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
            case EVT_USER_CLICK_ENTER:
                return "EVT_USER_CLICK_ENTER";
            case EVT_ACTIVE_EXIT:
                return "EVT_ACTIVE_EXIT";
            case EVT_OVER_SPEED:
                return "EVT_OVER_SPEED";
            case EVT_REDUCE_SPEED:
                return "EVT_REDUCE_SPEED";
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
            case ACT_2D_LR_FRONT:
                return "ACT_2D_LR_FRONT";
            case ACT_PREV_VIEW_ANGLE:
                return "ACT_PREV_VIEW_ANGLE";
            case STS_SENSOR_RADAR_TURN_LAMP:
                return "SENSOR_RADAR_TURN_LAMP";
            case EVT_RADAR_TURN_LAMP_ACTIVE:
                return "EVT_RADAR_TURN_LAMP_ACTIVE";
            case EVT_RADAR_RESET:
                return "EVT_RADAR_RESET";
            case EVT_TOUCH_TAP:
                return "EVT_TOUCH_TAP";
            case STS_GEAR_D_MOVING:
                return "STS_GEAR_D_MOVING";
            case STS_GEAR_N_MOVING:
                return "STS_GEAR_N_MOVING";
            case EVT_USER_CLICK_EXIT:
                return "EVT_USER_CLICK_EXIT";
            case ACT_ENABLE_TIMING:
                return "ACT_ENABLE_TIMING";
            case ACT_DISABLE_TIMING:
                return "ACT_DISABLE_TIMING";
//            case ACT_2D_FRONT_UNDISTORT:
//                return "ACT_2D_FRONT_UNDISTORT";
            case EVT_WIDE_ANGLE_FRONT:
                return "EVT_WIDE_ANGLE_FRONT";
            case EVT_CLICK_2D_TOP:
                return "EVT_CLICK_2D_TOP";
            case EVT_CLICK_2D_BOTTOM:
                return "EVT_CLICK_2D_BOTTOM";
            case EVT_CLICK_2D_LEFT:
                return "EVT_CLICK_2D_LEFT";
            case EVT_CLICK_2D_RIGHT:
                return "EVT_CLICK_2D_RIGHT";
            case EVT_CLICK_3D_LT:
                return "EVT_CLICK_3D_LT";
            case EVT_CLICK_3D_RT:
                return "EVT_CLICK_3D_RT";
            case EVT_CLICK_3D_LB:
                return "EVT_CLICK_3D_LB";
            case EVT_CLICK_3D_RB:
                return "EVT_CLICK_3D_RB";
            case ACT_2D_LR_REAR:
                return "ACT_2D_LR_REAR";
            case ACT_3D_LEFT_FRONT:
                return "ACT_3D_LEFT_FRONT";
            case ACT_3D_RIGHT_FRONT:
                return "ACT_3D_RIGHT_FRONT";
            case ACT_2D_TOP:
                return "ACT_2D_TOP";
            case EVT_ON_DOUBLE_BLINK:
                return "EVT_ON_DOUBLE_BLINK";
            case EVT_OFF_DOUBLE_BLINK:
                return "EVT_OFF_DOUBLE_BLINK";
            case STS_DOUBLE_BLINK:
                return "STS_DOUBLE_BLINK";
            case STS_REDUCE_SPEED_RESUME:
                return "STS_REDUCE_SPEED_RESUME";
            case ACT_REFRESH_TAB_INDEX:
                return "ACT_REFRESH_TAB_INDEX";
            case SWITCH_RADAR_ACTIVE:
                return "SWITCH_RADAR_ACTIVE";
            case SWITCH_DELAY_30S_EXIT:
                return "SWITCH_DELAY_30S_EXIT";
            case SWITCH_TURN_LAMP_ACTIVE:
                return "SWITCH_TURN_LAMP_ACTIVE";
            case ACT_CONTINUE:
                return "ACT_CONTINUE";
            case ACT_REFRESH_LAND_TRANSPARENCY:
                return "ACT_REFRESH_LAND_TRANSPARENCY";
            case ACT_ACTIVE_2_PASSIVE:
                return "ACT_ACTIVE_2_PASSIVE";
            case ACT_REFRESH_2D_LEFT_VIEW:
                return "ACT_REFRESH_2D_LEFT_VIEW";
            case ACT_REFRESH_2D_RIGHT_VIEW:
                return "ACT_REFRESH_2D_RIGHT_VIEW";
            case ACT_REFRESH_2D_TOP_VIEW:
                return "ACT_REFRESH_2D_TOP_VIEW";
            case ACT_REFRESH_2D_BOTTOM_VIEW:
                return "ACT_REFRESH_2D_BOTTOM_VIEW";
            case ACT_REFRESH_3D_LEFT_REAR:
                return "ACT_REFRESH_3D_LEFT_REAR";
        }

        return String.valueOf(id);
    }

}