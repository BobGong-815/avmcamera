package com.avm.framwork.constant;

import android.car.VehicleAreaDoor;

public class CameraContracts {
    //视图模式定义
    private  static  byte  BW_VIEW_POWER_OFF = 0x0;//关机视图

    private  static  byte BW_FRONT_3D = 0x01; //全景图+3D前视图
    private  static  byte  BW_RIGHT_FRONT_3D = 0x02; //全景图+3D右前视图
    private  static  byte BW_RIGHT_3D = 0x03;  //全景图+3D右视图
    private  static  byte BW_RIGHT_REAR_3D= 0x04;  //全景图+3D后右视图
    private  static  byte  BW_REAR_3D = 0x05; //全景图+3D后视图
    private  static  byte BW_LEFT_REAR_3D = 0x06;  //全景图+3D左后视图
    private  static  byte  BW_LEFT_3D = 0x07; //全景图+3D左视图
    private  static  byte BW_LEFT_FRONT_3D = 0x08; //全景图+3D左前视图
    private  static  byte BW_DPMM_FRONT_3D = 0x09;//全景图+3D前视图（与BW_FRONT_3D相同，可不用）
    private  static  byte BW_DPMM_LEFT_3D = 0x0B; //全景图+左3D视图（与BW_2D_LEFT、BW_DPMM_LEFT_2D相同，可不用）
    private  static  byte  BW_DPMM_RIGHT_3D = 0x0C; //全景图+右3D视图（与BW_2D_RIGHT、BW_DPMM_RIGHT_2D相同，可不用）

    private  static  byte  BW_2D_FRONT = 0x10;  //全景图+前单视图
    private  static  byte BW_2D_REAR = 0x11;  //全景图+后单视图
    private  static  byte BW_2D_LEFT = 0x12;  //全景图+左3D视图（客户要求左视图2D变3D）
    private  static  byte BW_2D_RIGHT = 0x13;  //全景图+右3D视图（客户要求右视图2D变3D）

    private  static  byte BW_DPMM_LEFT_2D = 0x14;  //全景图+左3D视图（与BW_2D_LEFT、BW_DPMM_LEFT_3D相同，可不用）
    private  static  byte BW_DPMM_RIGHT_2D = 0x15;  //全景图+右3D视图（与BW_2D_RIGHT、BW_DPMM_RIGHT_3D相同，可不用）

    private  static  byte BW_LEFT_RGIHT_2D = 0x16;  //全景图+左右视图
    private  static  byte BW_FREE_3D = 0x17;  //全景图+3D自由控制视图
    private  static  byte BW_2D_FRONT_120 = 0x18;  //全景图+前广角视图
    private  static  byte  BW_2D_REAR_120 = 0x19; //全景图+后广角视图

    private  static  byte  BW_2D_MANUAL_FRONT = 0x1A; //手动标定描点前
    private  static  byte BW_2D_MANUAL_REAR = 0x1B; //手动标定描点后
    private  static  byte BW_2D_MANUAL_LEFT = 0x1C; //手动标定描点左
    private  static  byte BW_2D_MANUAL_RIGHT = 0x1D; //手动标定描点右

    /**
     * Function Describe:右前车窗控制  右后车窗控制 左前车窗控制 左后车窗控制 天窗/遮阳帘控制
     *      *      * areaID:VehicleAreaWindow::ROW_1_RIGHT, VehicleAreaWindow::ROW_2_RIGHT, VehicleAreaWindow::ROW_1_LEFT, VehicleAreaWindow::ROW_2_LEFT, VehicleAreaWindow::ROOF_TOP_1
     *      *      * d
     *      VehicleAreaDoor
     */
    public  static  final  int ROW_1_LEFT = VehicleAreaDoor.DOOR_ROW_1_LEFT;
    public  static  final  int ROW_2_LEFT = VehicleAreaDoor.DOOR_ROW_2_LEFT;
    public  static  final  int ROW_1_RIGHT = VehicleAreaDoor.DOOR_ROW_1_RIGHT;
    public  static  final  int ROW_2_RIGHT = VehicleAreaDoor.DOOR_ROW_2_RIGHT;
    public  static  final  int DOOR_REAR = VehicleAreaDoor.DOOR_REAR;
    public  static  final  int DOOR_HOOD = VehicleAreaDoor.DOOR_HOOD;



    public static final int  UNDISTORTLEVEL = 60;//2D上下视角去畸变参数，范围是0~100，0表示原图，100表示最大去畸变图

}
