
##信号相关：
{
    BW_VIEW_POWER_OFF = 0x0,//关机视图

    BW_FRONT_3D = 0x01, //3D前视图				//ok
    BW_RIGHT_FRONT_3D = 0x02,  //3D右前视图 //OK
    BW_RIGHT_3D = 0x03,  //3D右视图				ok
    BW_RIGHT_REAR_3D = 0x04,  //3D后右视图	ok
    BW_REAR_3D = 0x05,  //3D后			ok
    BW_LEFT_REAR_3D = 0x06,  //3D左后    ok
    BW_LEFT_3D = 0x07, //3D左		ok
    BW_LEFT_FRONT_3D = 0x08, //3D左前 ok
   	BW_DPMM_FRONT_3D = 0x09, //3D前	ok
   	BW_AUTO_ROTATE_3D = 0x0A, //3D自动旋转
    BW_DPMM_LEFT_3D = 0x0B, //3D左 ok
    BW_DPMM_RIGHT_3D = 0x0C, //3D右 ok
    BW_FREE_3D = 0x0D,		//3D自由控制视图


    BW_2D_FRONT = 0x10,  //全景图+前单视图 ok
    BW_2D_REAR = 0x11,  //全景图+后单视图 ok
    BW_2D_LEFT = 0x12,  //全景图+左3D视图 ok
    BW_2D_RIGHT = 0x13,  //全景图+右3D视图 ok

    BW_DPMM_LEFT_2D = 0x14,  //全景图+左3D视图 ok
    BW_DPMM_RIGHT_2D = 0x15,  //全景图+右3D视图 ok

    BW_LEFT_RGIHT_FRONT = 0x16,  //全景图+左右前视图
    BW_LEFT_RGIHT_BACK = 0x17,  //全景图+左右后视图
    BW_2D_FRONT_120 = 0x18,  //全景图+前广角视图
    BW_2D_REAR_120 = 0x19, //全景图+后广角视图

    BW_2D_MANUAL_FRONT = 0x1A, //手动标定描点前
    BW_2D_MANUAL_REAR = 0x1B, //手动标定描点后
    BW_2D_MANUAL_LEFT = 0x1C, //手动标定描点左
    BW_2D_MANUAL_RIGHT = 0x1D, //手动标定描点右
}

acc 信号：
BCM_BCAN_2 0000025D  // 点火状态 ACC ON
HVAC_SIDE_MIRROR_HEAT，后视镜
http://aospxref.com/android-12.0.0_r3/xref/hardware/interfaces/automotive/vehicle/2.0/IVehicle.hal

## Can信号对照映射
   倒车档位：
    android key: CLUSTER_VCU_GEAR_LVL_DISP
    dbc 表： GW_VCU_11_B   0x38B  VCU_CrntGearLvl_P
   value ：
       0x4 ='P' Park gear;
       0x3 ='R' Reverse gear;
       0x2 ='N'Neutral gear;
       0x1 = 'D' Drive gear;
   
## 雷达： 343  GWM_PAS_1_B
        CLUSTER_PAS_RRMidDistance
       PAS_PCSDisplayReq  AVM_PAS_PCSDISPLAYREQ:// 雷达触发全景
## 雷达警告声： 081  ACU_5_B
## 转向转角： 33D  GWM_EPS_2_B
## 小窗口 盲区 082 ACU_2_A_B
      右边：ACU_SVM_RightBlindViewReg
      左边：ACU_SVM_LefBlindViewReg
## 语音打开/关闭 04B ACU_23_B
    ACU_VR_SVM_Switch 1 or 0
## 转向灯-激活  184 GW_MFS_1_B   
    UINM_TurnLightSwSt 


##  轮速： BCS_4_B 29B
    左前轮前进方向，透明底盘功能需要用此信号		 AVM_BCS_FLWHEEL_ROTATED_DIR
    右前轮前进方向，透明底盘功能需要用此信号	     AVM_BCS_FRWHEEL_ROTATED_DIR
    左后轮前进方向，透明底盘功能需要用此信号		 AVM_BCS_RLWHEEL_ROTATED_DIR
    右后轮前进方向，透明底盘功能需要用此信号		 AVM_BCS_RRWHEEL_ROTATED_DIR
    左前轮轮速，透明底盘功能需要用此信号		     AVM_BCS_FLWHEEL_SPD
    右前轮轮速，透明底盘功能需要用此信号		     AVM_BCS_FRWHEEL_SPD
    左后轮轮速，透明底盘功能需要用此信号		     AVM_BCS_RLWHEEL_SPD
    右后轮轮速，透明底盘功能需要用此信号		     AVM_BCS_RRWHEEL_SPD

 ## 车门：BCM_BCAN_1 318
     LCK 是IHU 主动控制，所以不需要Pcan发送；
     BCM ：是ECU 发送 ，需要Pcan模拟发送；
     LCK_DriverDoorAjarSt	左前车门状态，3D车模随动功能需要用此信号		CLUSTER_LCK_DRIVERDOORAJARST
     BCM_DriverDoorAjarSt	 左前车门状态，3D车模随动功能需要用此信号  	CABIN_DOOR_OPEN_STATUS
     LCK_PassengerDoorAjarSt 右前车门状态，3D车模随动功能需要用此信号		CLUSTER_LCK_PassengerDoorAjarSt
     BCM_PsngrDoorAjarSt	左前车门状态，3D车模随动功能需要用此信号	    CABIN_DOOR_OPEN_STATUS
     LCK_RearLeftDoorAjarSt	左后车门状态，3D车模随动功能需要用此信号		CABIN_DOOR_OPEN_STATUS
     BCM_RLDoorAjarSt 左前车门状态，3D车模随动功能需要用此信号		    CABIN_DOOR_OPEN_STATUS
     LCK_RearRightDoorAjarSt	右后车门状态，3D车模随动功能需要用此信号	CABIN_DOOR_OPEN_STATUS
     BCM_RRDoorAjarSt	 左前车门状态，3D车模随动功能需要用此信号	         CABIN_DOOR_OPEN_STATUS
     LCK_TrunkAjarSt		尾门状态，3D车模随动功能需要用此信号		     CABIN_DOOR_OPEN_STATUS
     BCM_TrunkAjarSt	左前车门状态，3D车模随动功能需要用此信号 	        CABIN_DOOR_OPEN_STATUS

    mRegisterId.add(CLUSTER_LCK_DRIVERDOORAJARST);//左前车门状态

    ## 激活条件:
      1、主动激活：
         手动点击、物理按键、语音唤起
        条件： 车速 <= 30km/h
      2、被动进入：
       R档、转向灯、雷达激活；
       2.1 转向灯激活：AVM_UINM_TURN_LIGHT_SW_ST  GW_MFS_1_B 184
            MFS_TurnchangeLmpSwtSt or CCU_MFS_1 UINM_TurnLightSwSt
         条件： 车速 <= 30km/h,退出：回正转向灯/车速>30km/h,保持800ms关闭页面

      2.2 雷达激活：
       条件： 车速 <= 30km/h，D或N档（EPB off）；
       前角雷达和侧面雷达 <=60cm;
       前中雷达 <= 90cm
        障碍物消失，系统3s后发出： PCS_ACU_DisplayReq=0x0:No Request 退出；
        手动退出：
         接收到：PCS_PAS_ConsumerReq=0x1:Request to cancer 不再接受雷达激活，
           除非被唤起，并挂入D/R挡；

        2.3 R档激活：
         条件： 车速 <= 30km/h；任意挡位到R档；
          退出： 切换到D/N挡30S后无操作退出全景；

         其他信号：
         后视镜下翻， DCM_1 3E6 DCM_OSRMAngleAdjFuncCfgSt； 
           关闭状态：
            点击按钮： 发送ACU_MSM_OSRMAngleAdjFuncCfg 参数：0x4，展开后视镜
             IDC 2s后上报：FLDCM_OSRMAngleAdjFuncCfgSt和FRDCM_OSRMAngleAdjFuncCfgSt 展开 ，按钮显示关闭并高亮
           打开状态：
               点击按钮： 发送ACU_MSM_OSRMAngleAdjFuncCfg 参数：0x1,下翻后视镜
                IDC 2s后上报：FLDCM_OSRMAngleAdjFuncCfgSt、FRDCM_OSRMAngleAdjFuncCfgSt ，下翻,按钮显示打开并置灰色；
         后视镜折叠 ：
            IHU下发信号：
             ACU_ORVMOperationReq
             ACU_ORVMOperationReqVD
           上报信号 FRDCM_1 3E5：
             FLDCM_MirrorFoldUnfoldSt
             FRDCM_MirrorFoldUnfoldSt

## 车速-BCS_2_B  260
     key:VEHICLE_SPEED

 ## 灯光
     BCM_BCAN_2 25D 通
 ## 泊车图片：
 ## D:\project\AVM01\AVM&RVC\06_泊车影像_1920x1080\06_泊车影像\01_全景影像\01_切图\06_Parking\图标类（PNG）\泊车影像\倒车雷达距离提示


## 显示手动标定按钮
    终端命令：（1：显示，0：隐藏）
        写入：  adb shell settings put system avm.calibrate  1
       查询：   adb shell settings get system avm.calibrate


系统权限操作：
  adb  root
  adb  remount
  adb rm -rf /system/app/SYGuagDaAvmCamera/SYGuagDaAvmCamera.apk
  adb reboot
  adb push /system/app/SYGuagDaAvmCamera/SYGuagDaAvmCamera.apk

 注意：
1、要以SYGuagDaAvmCamera.apk 命名；
2、如果, adb remount 执行提示错误，则需要执行 adb  disable-verity；然后重启车机，在执行adb remount ，才有操作apk文件的权限；
3、U盘升级，执行adb  adb   enable-verity 后重启车机，插上U盘；
4、用包名查找apk push的目录输入命令：
    adb shell "pm path com.autochips.avm"


adb shell am start -n com.autochips.avm/com.autochips.avm.ui.activity.MockActivity


退出逻辑：
 1、主动退出
 1.1 avm上的关闭按钮
 1.2 软按钮P键关闭
 1.3 home键返回关闭
 1.4 语音：关闭全景影像
 以上均通过Service参数0 来关闭，代码如下
        Intent mIntent = new Intent();
        mIntent.setClassName("com.autochips.avm", "com.autochips.avm.service.AvmService");
        mIntent.setAction("com.android.avm.action.AvmStart");
        mIntent.putExtra("avm_start", 0);
        AvmApp.getInstance().startService(mIntent);
2、被动退出：
  R挡被动激活后切P档，如设置立即退出，则立即退出，如设置30s后，则延时3s自动退出，前提无操作事件；
  R挡被动激活，切D、N档，延时30s退出；
  转向被动激活--退出转向，延时600ms 退出；
 
 以上两种关闭方式，最终会触发WindowManager接口；
 关闭的时候，设置window 的with和height 为0 ，再调通过mWindowManager.updateViewLayout()接口把window更新；
 显示的时候，则设置设置window 的with和height 指定的尺寸，再调mWindowManager.updateViewLayout()接口把window更新窗口，达到显示页面的效果；

adb shell "echo performance >/sys/devices/platform/fb000000.qpu/devfreg/fb000000.qpu/governor" adb shell "do date >> /sdcard/gpuinfo.txt;while true;do cat sys/class/devfreq/fb000000.gpu/load >>/sdcard/gpuinfo.txt;sleep 1;done"
## 性能测试
 ### 冷启动/热启动
    清理缓存数据：adb shell pm clear com.autochips.avm
    停止进程：adb shell am force-stop com.autochips.avm
    启动app：adb shell am start -W com.xxx.mian/$package.home.ui.activity.HomeFragmentActivity


标定日志过滤：

0x2170328b
VehicleHalServer

## RVC 测试
    /avm_config/gd_rvc

## 创建一个AnimatorSet对象
#### 创建一个AnimatorSet对象
    AnimatorSet animatorSet = new AnimatorSet();

 #### 创建一个ObjectAnimator对象，对View的alpha属性进行动画操作
    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f);
    alphaAnimator.setDuration(500);
    alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); //InOut slow插值器

#### 创建一个ObjectAnimator对象，对View的translationY属性进行动画操作，坐标根据最终位置调整
    ObjectAnimator tranYAnimator= ObjectAnimator.ofFloat(mRecyclerView, "translationY", 200f, 0f);
    tranYAnimator.setDuration(500);
    tranYAnimator.setInterpolator(new DecelerateInterpolator());//Out slow插值器

#### 将两个ObjectAnimator添加到AnimatorSet中
    animatorSet.playTogether(alphaAnimator, tranYAnimator);
#### 开始动画
    animatorSet.start();







   
   

