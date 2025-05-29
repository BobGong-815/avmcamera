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

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.em.ViewType;
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
    public int turnValue = -1;// 转向-左/右转
    private float speedValue = 0f;// 车速
    public boolean turnRSet = false;//记录是否转向中出现R档设置
    public boolean isPGearShowSmart = false;//记录是否为p挡，转向激活进入小卡片
    public boolean isRGearShowSmart = false;//记录是否为R挡，转向激活进入小卡片
    public boolean isPGearShowSmartDismiss = false;//记录是否为p挡，转向激活进入小卡片,进入全景允许计时关闭

    private boolean isRunning = false; // 是否在操作

    public boolean isClick = false; // 是否手动进入
    public boolean isCloseClick = false; // 是否手动退出
    private boolean isCalibrateRunning = false;

    private boolean isCloseTrunk = false; // 是否手动关闭转向，如果手动关闭后，不可以在进入转向激活，或者雷达激活
    private boolean mIsFirstOpen = true;//判断是否第一次打开
    private long mCurrentTime = 0;//记录显示转向0的时间
    public boolean gdNotChangeView = false;//记忆广角不切换视角成2d后视


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
        if (instance == null)
            instance = new CameraViewModelHelper();
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
        if (isRadarActive)
            isRadarActiveTow = 0;
        if (isTurn) {
            isTurn = false;
            isCloseTrunk = activeTow;
        }

    }
    private boolean isPGear = false;

    public void showView(boolean isClick) {
        KLog.d("showView 进入AVM: " + isClick);
        this.isClick = isClick;
        if (isClick) {
            isPGear = valGear == 4;
            isRadarActiveTow = 1;
            isCloseTrunk = false; // 手动进入后
//            reverse(valGear);
            if(isPGear){
                KLog.d("isPGear 进入AVM: " + isPGear);
                mHandler.removeCallbacksAndMessages("close_N");
                mHandler.removeCallbacksAndMessages("setRunning");
            }
            if(valGear == 3 && AvmApp.getInstance().getCameraView().viewPosition == 2){
                KLog.d("R挡主动进入，记忆为广角，不响应转向视角切换");
                gdNotChangeView = true;
            }
        }
//        mHandler.removeCallbacksAndMessages("setRunning");
        showFullWin();
        if(!isPGear) {
            setRunning(true);
        }
    }


    private void showFullWin() {
        KLog.d("进入全屏页面 showFullWin");
        AvmApp.getInstance().getCameraView().showFullWin();
    }

    public void openAndClose(){
        mHandler.postDelayed(()->{
            for (int i = 0 ;i<1000;i++){
                KLog.d("创建 openAndClose time："+i);
                long camreaStatus = BvAvmJNIHelper.getInstance().camreaStatus();
                boolean isOpenCamera = (camreaStatus != 0 && camreaStatus !=-1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isOpenCamera){
                    //已经开启
                    BvAvmJNIHelper.getInstance().bwDeleteCamera();
                }else {
                    BvAvmJNIHelper.getInstance().bwCreateCamera("com/autochips/avm/ui/view/CameraView","onBVAVMMessage");
                }
            }
        },2000);
    }

    //打开摄像头
    public void openCamera(){
        if (mIsFirstOpen) {
            Log.i(TAG, "showComm mIsFirstOpen");
            mIsFirstOpen = false;
        } else {
            Log.i(TAG, "bwCreateCamera open");
            //BvAvmJNIHelper.getInstance().bwCreateCameraShow("com/autochips/avm/ui/view/CameraView", "onBVAVMMessage");
        }
    }

    //关闭摄像头
    public void closeCamera(){
        KLog.d("closeCamera");
        BvAvmJNIHelper.getInstance().bwDeleteCamera();
    }

    /**
     * 根据条件判断是否需要关闭窗口
     * 手动进入的，就手动关闭
     */
    public void dismissView(boolean isClick, int time, String position) {
        if(AvmApp.mAvmRvcState== 1 && AvmApp.getInstance().getCameraView().isFullWin){
            KLog.d("dismissView  is rvc  show ");
            return;
        }
//        if(isRvcIn){
//            KLog.d("dismissView rvc is show ");
//            isRvcIn = false;
//            return;
//        }

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
        mHandler.postDelayed(() -> {
            AvmApp.getInstance().getCameraView().dismissView(position);
            KLog.d(position + " dismissView关闭 " + isClick);
            this.isClick = false;
            isTurn = false;
            isRunning = false;
              isReverse = false;
              isReverseInByTurn = false;
              isReverseToTurnStats = false;// R档进入转向模式
            isSmartView = false;
            mHandler.removeCallbacks(getmRun);//  只要关闭，就要把所有定时器移除
            mHandler.removeCallbacksAndMessages("setRunning");
            mHandler.removeCallbacksAndMessages("close_token");
            mHandler.removeCallbacksAndMessages("close_N");
        }, "close_token", time);

        CustomToast.cancelToast();
        RearviewToast.getInstance().cancelToast();
    }

    public void gearExit(int value) {
        // P档退出 0 立即   1 30S
        int pExit = SystemProperties.getInt("pExit", 0);
        KLog.d(value + " pExit = " + pExit);
        KLog.d(" isTurn = " + isTurn);

        KLog.d(value + " value valGear = " + isRunning);

        if (value == 1 || value == 2) {
            BvAvmJNIHelper.getInstance().bwSetTrajLineStatus(0);
            KLog.d("valGear rvc  AvmApp.mAvmRvcState："+AvmApp.mAvmRvcState);
            if (AvmApp.mAvmRvcState == 1 && valGear != 4) {
                //todo
                KLog.d("valGear rvc  isopen");
                isRvcAndRgear = true;
                showFullWin();
            }
            setViewModel(ViewType.gear_D);
            mHandler.removeCallbacksAndMessages("close_N");
            mHandler.removeCallbacks(getmRun);
            mHandler.removeCallbacksAndMessages("setRunning");
            mHandler.postDelayed(() -> {
//                if (isRunning) {
//                    isRunning = false;
//                    return;
//                }
                if (isTurn || isReverse) {
                    return;
                }
                dismissView(isClick, 0, "d1");
            }, "close_N", 30 * 1000);
        } else if (value == 4) {
            setViewModel(ViewType.gear_P);
            isCloseTrunk = false; // 重置手动关闭转向
            isRunning = false;//客户挂了P档后，不会在操作
            KLog.d("dismissView isClick:" + isClick);
            KLog.d("P档 isReverse:" + isReverse + " isRunning: " + isRunning + " isTurn: " + isTurn);
            mHandler.removeCallbacksAndMessages("close_N");
            mHandler.removeCallbacks(getmRun);
            mHandler.removeCallbacksAndMessages("setRunning");
            if(BvAvmJNIHelper.turnIsPgear){
                BvAvmJNIHelper.turnIsPgear = false;
                KLog.d("dismissView turnIsPgear not close");
                return;
            }
            if(AvmApp.getInstance().getCameraView().isSmartWin && CameraView.isShowing){
                //左卡片时，切p挡退出
                KLog.d("dismissView 左卡片时，切p挡l退出");
                dismissView(false, 0, "smallPExit");
                return;
            }
            mHandler.postDelayed(() -> {
                if (isReverse) return;
                //这里加了isTurn判断是因为这个BUG，D档下，软按键打开全景，设置P档30秒退出，然后切换P档再打开转向灯，30秒后，全景会关闭，应保持激活
                if (isRunning ) {
                    isRunning = false;
                    return;
                }
                if (pExit != 0 && isTurn){ // 正在转向的时候，P档设置立即关闭，则就关闭 ，否则不关闭
                    isRunning = false;
                    return;
                }
                dismissView(false, 0, "d2");
            }, "close_N", pExit == 0 ? 0 : 30 * 1000);

        } else {
            setViewModel(ViewType.gear_N);
            //30S 延迟30S退出
            dismissView(isClick, 0, "d3");
        }
    }

  public int getSpeedValue() {
    return (int) speedValue;
  }

  /**
   * 设置车底透明
   * @param speedValue
   */
  private  boolean isTransparent = false;
  private int mLastSetPosition = -1;//记录已设置的车底透视位置
  public void setTransparentIndexTab() {
    if (!CameraView.isShowing){
      return;
    }
    int position = SystemProperties.getInt("settingRadarActivatedPanorama", 0);
//    if (!isTransparent){
//       position =speedValue > 0.3 ? position : 0 ;
//    }
      if (speedValue <= 0.3 && position != 0) {
          return;
      }
//    if(position == 0 || speedValue < 1){
//        //表示未激活不透明
//        isTransparent = false;
//    }
//    if (speedValue > 1 && position != 0){
//      isTransparent = true;
//    }else {
//      if (valGear == 4){
//        isTransparent = false;
//      }
//    }
      if(position == mLastSetPosition){
          return;
      }
    //KLog.d("车速： speedValue "+speedValue +" isTransparent:"+isTransparent +" position:"+position);
    if (position == 0) {
      bvavmJNI.bwSetCarBottomStatus((byte) 0);
      bvavmJNI.bwSetCarTransparency(1f);
    } else if (position == 1) {
      bvavmJNI.bwSetCarBottomStatus((byte) 1);
      bvavmJNI.bwSetCarTransparency(0.3f);
    } else if (position == 2) {
      bvavmJNI.bwSetCarBottomStatus((byte) 1);
      bvavmJNI.bwSetCarTransparency(0.15f);
    } else {
      bvavmJNI.bwSetCarBottomStatus((byte) 1);
      bvavmJNI.bwSetCarTransparency(0.05f);
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

    public void setSpeed(float val) {
        speedValue = mpsToKmh(val);
//        speedValue = 3;// 测试透明地盘
        //KLog.d("车速："+val+"   isSpeedModel: "+isSpeedModel+"   speedValue: "+speedValue+"   turnValue: "+turnValue+ "  isClick:  "+isClick+"  版本号： "+ ServiceUtils.getVersionName());
        int turn = CanManager.getInstance().getIntStatus(AVM_UINM_TURN_LIGHT_SW_ST, ROW_1_LEFT);
        //KLog.d("车速：判断当前转向turn: "+turn);
      setTransparentIndexTab();



//      SystemProperties.set("settingRadarActivatedPanorama",  String.valueOf(position));

        if (speedValue > 30){
            isSpeedModel = true;
        }

        if (speedValue <= 30 && isSpeedModel ) {// 小于30 的时候，判断条件看是否满足转向激活
            KLog.d("车速：判断条件看是否满足转向激活  "+turn);
            if(turn == 1||turn == 2){
                KLog.d("车速： turnActive "+turn);
                //turnActive(turn);
                turnSpeedValue = -1;
                isSpeedModel = false;


                mHandler.removeCallbacksAndMessages("setRunning");
                mHandler.removeCallbacks(getmRun);
                mHandler.removeCallbacksAndMessages("close_N");
                //if (!isTurn) {
                    // 转向灯激活全景开关打开，且车速《20，右转向灯打开 557843113
                    int signalActivates = SystemProperties.getInt("signalActivates", 0);
                    if (signalActivates == 1) {
                        KLog.d("车速： 激活视图 :"+isCloseClick);
                        if(!isCloseClick) {
                            //只有非主动关闭的才允许打开左卡片
                            KLog.d("车速非主动关闭才退出 ");
                            isPGearShowSmart = false;
                            isRGearShowSmart = false;
                            smartActive(turn);
                        }
                    }
               // }
                isTurn = true;
                turnSpeedValue = -1;
                //isSpeedModel = false;// 如果转向进入，则需要取消车速过高模式
                //setViewModel(turn == 1 ? ViewType.gear_Left : ViewType.gear_Right);
            }
        } else if (speedValue > 30 &&!isClick && valGear != 3 && CameraView.isShowing) {// 车速过高，非点击进去关闭AVM
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
        //KLog.d("车速：");
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
    public  int count = 0;
    public boolean isRvcIn = false;
    public boolean isRvcAndRgear = false;//rvc已开启，并且首次进来
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
//        if(value != 3){
//            //TODO 在 R 档下，进入广角模式，视角为后广角，切换 NPD 档之后视角为前广角，此后只要不切换2D/3D模式，再挂R档就只切后广角，应为2D后视
//            if(gdNotChangeView) {
//                gdNotChangeView = false;
//            }
//        }
        if(value == 4) {//p档
            //重置透明底盘
            KLog.d(" valGear之前非p档 ");
            bvavmJNI.bwClearCarBottomImage();
//            if(AvmApp.mAvmRvcState == 1) {
//                KLog.d("p挡 rvc关闭不显示avm mAvmRvcState:"+AvmApp.mAvmRvcState);
//                AvmApp.mAvmRvcState = 0;
//            }
            if (isRvcAndRgear && AvmApp.mAvmRvcState == 1) {
                KLog.d("CMH当前表示进入时R挡并且rvc开启valGear :");
                KLog.d(" 获取当前挡位valGear 是p挡 此时不显示avm");
                AvmApp.mAvmRvcState = 0;
                isRvcAndRgear = false;
                AvmApp.getInstance().getCameraView().dismissView("valGear 是p挡，rvc被p挡关闭");
                return;
            }
        }else {
            isPGear = false;
        }
        isRvcAndRgear = false;
        valGear = value;
        if (valGear == 1 && isRadarActiveTow == 1) isRadarActiveTow = 2;
        KLog.d(valGear + " valGear倒车档位：" + isReverse);
        if (valGear == 3) {
            boolean canShowFull = true;
            if(AvmApp.mAvmRvcState == 1){
                //rvc以启动
                KLog.d(" rvc关闭没 再次确认");
                int[] rvcStatus = new int[1];
                rvcStatus[0] = 0;
                bvavmJNI.bwGetRVCStatus(rvcStatus);
                AvmApp.mAvmRvcState = rvcStatus[0];
                KLog.d("[valGear] mAvmRvcState:"+AvmApp.mAvmRvcState);
                if(AvmApp.mAvmRvcState != 1){
                    KLog.d("[valGear] 此处表示rvc被关闭，但并非应用去关闭，不继续往下执行R挡显示逻辑");
                    return;
                }
                //此处表明rvc已开启，并且进来了R挡
                isRvcAndRgear = true;
            }
//             AvmApp.getInstance().getViewBottom().updateWind0();
            isReverseInByTurn = true;
            BvAvmJNIHelper.getInstance().bwSetTrajLineStatus(3);
            if (isReverse) {
                KLog.d(" valGear倒车档位： 我进来了啊");
                setViewModel(ViewType.ReverseIn);
                return;// 防止多次触发
            }
            isReverseIn = true;
            isReverse = true;
            isRadarActiveTow = 2;
            mHandler.removeCallbacksAndMessages("setRunning");
            showFullWin();
            DataManager.writeFault(DataConstant.Code.ACTIVI_RGEAR);
            KLog.d(" valGear倒车档位： 我是这里进来的");
            setViewModel(ViewType.ReverseIn);
        } else {
            KLog.d(isReverse + " value valGear_isReverse = " + isRunning);

            if (isReverseIn) {// 是否从倒车进入
//                isClick = false;
                isRunning = false;
            }
            isReverse = false;
            //不管是手动进来，只要触发了转向及挡位，都按转向跟挡位逻辑修改
            //this.isClick = false;
            if (isTurn && turnValue != -1) {
                // 进入转向激活
                reverseToTurn();
            } else {
                gearExit(valGear);
            }
            isReverseIn = false;


        }
      count = 1;
//        if (index == 0) {
//            //如了R档，其他挡位都退出RVC
//            KLog.d(" 第一次启动P档  resRvc  = " + index);
//           mHandler.postDelayed(()->{
//               int resRvc =  bvavmJNI.bwNotifyRVC(0);
//               KLog.d(" 第一次启动P档  关闭resRvc  = " + resRvc);
//           },2000);
//            index++;
//        }
    }

    private boolean isReverseToTurnStats = false; // 是否是倒车进入转向模式
    private int index = 0;
    private boolean mIsThtihty = false;//记录是否已倒计时30

    private void reverseToTurn() {
        if (valGear == 4) { // p档
            setViewModel(ViewType.gear_P);
            int pExit = SystemProperties.getInt("pExit", 0);
            KLog.d(" 是否进入转向pExit = " + pExit);
            if (pExit == 0) {
                gearExit(valGear); // 如果R档退出，设置了立即关闭，则立即关闭
                return;
            } else {// 否则进入转向
                isReverseInByTurn = false;
                if (isReverseIn) // 倒车进入的时候才会定时
                  isReverseToTurnStats = true;
                turnActive(turnValue);
            }


        } else if (valGear == 1 || valGear == 2) {// D/N档
          setViewModel(ViewType.gear_D);
          if (isReverseIn)
            isReverseToTurnStats = true;
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
        KLog.d(value + " 转向激活 turnActive isClose = " + AvmApp.getInstance().getCameraView().isFullWin + " isReverse:" + isReverse +" valGear : " + valGear );
//        if (value == 0 && turnValue > 0){
//            turnExit(0);
//        }

        /*if (value < 1) {
            return;
        }*/
        if(value == 0){
            turnValue = value;
        }
        turnRSet = false;
        if(value == 0 && mCurrentTime == 0){
            mCurrentTime = System.currentTimeMillis();
            mHandler.postDelayed(() -> {
                KLog.d(" 转向激活 delayed do 0 ");
                turnActive(0);
            },"turnDealyClose",800);
            return;
        }else {
           if(mCurrentTime != 0){
               if(System.currentTimeMillis() - mCurrentTime < 800){
                   KLog.d(" 转向激活 turnActive  is same remove 0 ");
                   mHandler.removeCallbacksAndMessages("turnDealyClose");
               }
           }
           mCurrentTime = 0;
        }

        if (isReverse && AvmApp.getInstance().getCameraView().isFullWin && !(valGear == 3 && AvmApp.getInstance().getCameraView().viewPosition == 1)) {// 步骤： 转向-R档-关闭转向 此时需要重置 转向状态
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
        } else if (!isReverseToTurnStats && CameraView.isShowing && value!=0) {
            KLog.d(isReverseToTurnStats + " isReverseToTurnStats");
            return;
        }
        if (isCloseTrunk) {
            KLog.d(isCloseTrunk + " isCloseTrunk");
            return;
        }

//        mHandler.removeCallbacks(getmRun);
        KLog.d(isReverse + " isReverse转向激活  =移除run ");

        float speed = getSpeed();
        KLog.d(value + " value 转向激活 isTurn = " + isTurn + " isReverse :" + isReverse);
        //不管是手动进来，只要触发了转向及挡位，都按转向跟挡位逻辑修改
        //this.isClick = false;
        // 右向向打开 1 打开  0 关闭
        if (speed < 30 && (value == 1 || value == 2)) {
          KLog.d(value + " value removeCallbacksAndMessages 进入转向 = " + isTurn + " isReverse :" + isReverse);
            mHandler.removeCallbacksAndMessages("setRunning");
            mHandler.removeCallbacks(getmRun);
            mHandler.removeCallbacksAndMessages("close_N");
               isTurn = true;
            isRGearShowSmart = false;
            if (!AvmApp.getInstance().getCameraView().isFullWin) {
                // 转向灯激活全景开关打开，且车速《20，右转向灯打开 557843113
                int signalActivates = SystemProperties.getInt("signalActivates", 0);
                if (signalActivates == 1) {
                    if(valGear == 4) {
                        isPGearShowSmart = true;
                    }
                    if(valGear == 3){
                        //r挡转向激活左卡片
                        isRGearShowSmart = true;
                    }
                    smartActive(value);
                }
            }

            turnSpeedValue = -1;
            //isSpeedModel = false;// 如果转向进入，则需要取消车速过高模式
           if (valGear == 3){
             isReverse = true;
             isReverseIn = true;
             isRadarActiveTow = 2;
             isReverseInByTurn = true;
             turnRSet = true;
             setViewModel(ViewType.ReverseIn);
             return;
           }
            setViewModel(value == 1 ? ViewType.gear_Left : ViewType.gear_Right);
        }else if(speed > 30 && (value == 1 || value == 2)){
            KLog.d(" 转向超速导致未激活，即使之前手动关闭仍可激活");
            isCloseClick = false;
        } else if (value == 0) {
            KLog.d(turnValue + " 转向激活 被退出：" + speed+"   isTurn: "+isTurn);
//            setViewModel(ViewType.gear_turn_exit);
            turnSpeedValue = -1;
            //isSpeedModel = false; // 转向关闭的时候，需要置空车速过高模式
            isTurn = false;
            if (valGear != 3){
              setViewModel(ViewType.gear_turn_exit);
            }
            if(valGear  == 4&&!AvmApp.getInstance().getCameraView().isSmartWin){
                KLog.d(  " 这里仅仅只是在全屏的时候从其他档挂入P档要重新计时才能进入");
                if(!isPGear) {
                    setRunning(true);
                }
                if(isPGearShowSmartDismiss){
                    KLog.d(  " 记录是否为p挡，转向激活进入小卡片,进入全景允许计时关闭");
                    isPGearShowSmartDismiss = false;
                    setRunning();
                }
            }else{
                KLog.d(  "其他视图跟转向操作都走这里");
                turnExit(value);
            }

        }
    }

    /**
     * 转向进入
     * R 档
     * 转向回正
     */

    private Runnable getmRun = new Runnable() {

        @Override
        public void run() {
            KLog.d(isTurn + " isTurn：退出转向 removeCallbacksAndMessages   isReverse: " + isReverse + "isRunning:  " + isRunning + "    isReverseInByTurn: " + isReverseInByTurn);
            if (isTurn || isRunning) {
              if ( isReverse && AvmApp.getInstance().getCameraView().isFullWin) {
                  mIsThtihty = false;
                  return;
              }
            }
            isTurn = false;
            turnValue = -1;
//            setViewModel(ViewType.gear_turn_exit);
            if (isReverseInByTurn && AvmApp.getInstance().getCameraView().isFullWin && !mIsThtihty) {
                setRunning(true);
                return;
            }
            mIsThtihty = false;
            if (valGear != 3)
             setViewModel(ViewType.gear_turn_exit);
            dismissView(isClick, 0, "转向延时500ms退出 d5");
        }
    };

    public void turnExit(int value) {

        KLog.d(  " isRunning： "+isRunning+"   isReverseToTurnStats: "+isReverseToTurnStats);
        long time = 30 * 1000;
        if (isRunning && AvmApp.getInstance().getCameraView().isFullWin) {
            time = 30 * 1000;
        }

        if (isReverseToTurnStats) {
            time = 30 * 1000;
        } else if (valGear != 3){
            setViewModel(ViewType.gear_turn_exit);
        }
        //if(valGear  == 1||valGear==2&&!AvmApp.getInstance().getCameraView().isSmartWin){
//        if(AvmApp.getInstance().getCameraView().isFullWin){
//            //D/R档如果转向回正又是小卡片就要立刻关闭小卡片
//            //转向灯回正，只要是小卡片在就立即关闭小卡片
//            isRunning= false;
//             time = 50;
//        }
        if(AvmApp.getInstance().getCameraView().isSmartWin){
            time = 0;
        }
        KLog.d(  " time： "+time+"  valGear: "+valGear+ " 退出转向计算时间 removeCallbacksAndMessages  isSmartWin: "+AvmApp.getInstance().getCameraView().isSmartWin);
        isReverseToTurnStats = false;
        mHandler.removeCallbacks(getmRun);
        mIsThtihty = time == 30000;
        mHandler.postDelayed(getmRun, time);
    }


    /**
     * 雷达激活
     *
     * @param value
     */
    public void radarActive(int value) {
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
                isPGearShowSmart = false;
                isRGearShowSmart = false;
                smartActive(value);
            } else {
                radarExit(value);
            }

        }
    }

    public void radarExit(int value) {
        KLog.d(isRadarActive + " 雷达退出：" + value);
        if (isRadarActive) {
            isRadarActive = false;
            dismissView(isClick, 3 * 1000, "d6");
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
        AvmApp.getInstance().getCameraView().showSmartWin();
        DataManager.writeFault(DataConstant.Code.ACTIVI_LIGHT);
    }


    /**
     * 车门状态开关
     * Function Describe:右前车窗控制  右后车窗控制 左前车窗控制 左后车窗控制 天窗/遮阳帘控制
     * * areaID:VehicleAreaWindow::ROW_1_RIGHT, VehicleAreaWindow::ROW_2_RIGHT,
     * VehicleAreaWindow::ROW_1_LEFT, VehicleAreaWindow::ROW_2_LEFT, VehicleAreaWindow::ROOF_TOP_1
     *
     * @param val
     */
    public void doorStatus(int val) {
        Log.i(TAG, val + " AvmService 门的状态DOOR_HOOD: " + CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, DOOR_HOOD));
        /*设置车模上四个车门和后备箱开合状态，顺序分别为左前门、右前门、左后门、右后门，后备箱，左后视镜、右后视镜、前车盖*/
        int doors[] = {CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_1_LEFT),
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_1_RIGHT)
                , CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_2_LEFT)
                , CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, ROW_2_RIGHT),
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, DOOR_REAR),
                0,
                0,
                CanManager.getInstance().getIntStatus(CABIN_DOOR_OPEN_STATUS, DOOR_HOOD),
        };
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
        String msg = val == 0 ?
                AvmApp.getInstance().getString(R.string.radar_sound_open)
                : AvmApp.getInstance().getString(R.string.radar_sound_close);
        CustomToast.showToast(msg);
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
//        KLog.d("轮速：" +  Arrays.toString((int[]) object));
        Object object1 = CanManager.getInstance().getIntArray(AVM_WHEEL_DIRE_SPEED, 0);
        int flFont = CanManager.getInstance().getIntStatus(AVM_BCS_FLWHEEL_ROTATED_DIR, 0);// 左前轮
        int arr[] = {};
        //KLog.d("轮速-flFont：" + flFont);
        //bvavmJNI.bwSetFourWheelSpeed(flFont, 0, 0, 0);
        if (object1 instanceof int[]) {
            arr = (int[]) object1;
            //KLog.d("轮速：" + Arrays.toString(arr));
            if (arr.length >= 7) {
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
        if(AvmApp.VEHICLE_PLATFORM == AvmApp.IS_AY5){
            status = (float) (((status + 540.0) / 1080.0) * 72.0 - 36.0);
        }
        bvavmJNI.bwSetWheelAngle(status * -1);
    }

    //3D车模灯光交互
    public void showLight3DModel(int type, int value) {
//        KLog.d("3D 灯光showLight3DModel： type:" + type + " value: " + value);
        int highBeamStatus = CanManager.getInstance().getIntStatus(BCM_HIGH_BEAM_STATUS, 0);//远光灯
        int lowBeamStatus = CanManager.getInstance().getIntStatus(BCM_LOW_BEAM_STATUS, 0);//近光灯
        int brakeLight = CanManager.getInstance().getIntStatus(AVM_EL_SIDE_BRKLIGHT_CTRL_CMD, 0);//刹车灯
        int reverseLight = CanManager.getInstance().getIntStatus(AVM_EL_REVERSE_LIGHT_ST, 0);//倒车灯
        int frontFogLamp = CanManager.getInstance().getIntStatus(CLUSTER_FRONT_FOG_LAMP, 0);//前雾灯
        int leftFogLamp = CanManager.getInstance().getIntStatus(CLUSTER_LEFT_TURN_LAMP, 0);//左转向灯
        int rightFogLamp = CanManager.getInstance().getIntStatus(CLUSTER_RIGHT_TURN_LAMP, 0);//右转向灯
        int parkingLamp = CanManager.getInstance().getIntStatus(POWER_PARKING_LAMP, 0);//示宽灯、位置灯
        int readFog = CanManager.getInstance().getIntStatus(CLUSTER_REAR_FOG_LAMP, 0);//后雾灯

//        KLog.d("3D 灯光 highBeamStatus:" + highBeamStatus);
//        KLog.d("3D 灯光 lowBeamStatus:" + lowBeamStatus);
//        KLog.d("3D 灯光 brakeLight:" + brakeLight);
//        KLog.d("3D 灯光 frontFogLamp:" + frontFogLamp);
//        KLog.d("3D 灯光 leftFogLamp:" + leftFogLamp);
//        KLog.d("3D 灯光 rightFogLamp:" + rightFogLamp);
//        KLog.d("3D 灯光 parkingLamp:" + parkingLamp);
//        KLog.d("3D 灯光 readFog 后雾灯:" + readFog);


        //bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) frontFogLamp, (byte) parkingLamp, (byte) 0, (byte) 0, (byte) brakeLight);
       /* bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 0, (byte) brakeLight);
        if (leftFogLamp == 1) {
            bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 2, (byte) brakeLight);
        }
        if (rightFogLamp == 1) {
            bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 3, (byte) brakeLight);
        }*/
        //50毫秒内如果左右转向都有，就视为双闪
        mHandler.postDelayed(() -> {
            if (leftFogLamp == 1 && rightFogLamp == 1) {
                if(AvmApp.VEHICLE_PLATFORM != AvmApp.IS_AY5){
                    int[] lightArray1 = {highBeamStatus, lowBeamStatus, readFog, parkingLamp, 0, 1, brakeLight, reverseLight};
                    bvavmJNI.bwSetLampStatus2(lightArray1);
                }else {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 1, (byte) brakeLight);
                }
            } else if (leftFogLamp == 0 && rightFogLamp == 0) {
                //KLog.d("3D 灯光 leftFogLamp bwSetLampStatus 之前:" + leftFogLamp);
                if(AvmApp.VEHICLE_PLATFORM != AvmApp.IS_AY5){
                    int[] lightArray0 = {highBeamStatus, lowBeamStatus, readFog, parkingLamp, 0, 0, brakeLight, reverseLight};
                    bvavmJNI.bwSetLampStatus2(lightArray0);
                }else {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 0, (byte) brakeLight);
                }

            } else if (leftFogLamp == 1 && rightFogLamp == 0) {
                //KLog.d("3D 灯光 leftFogLamp bwSetLampStatus show之前:" + leftFogLamp);
                if(AvmApp.VEHICLE_PLATFORM != AvmApp.IS_AY5){
                    int[] lightArray2 = {highBeamStatus, lowBeamStatus, readFog, parkingLamp, 0, 2, brakeLight, reverseLight};
                    bvavmJNI.bwSetLampStatus2(lightArray2);
                }else {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 2, (byte) brakeLight);
                }
            } else if (leftFogLamp == 0 && rightFogLamp == 1) {
                if(AvmApp.VEHICLE_PLATFORM != AvmApp.IS_AY5){
                    int[] lightArray3 = {highBeamStatus, lowBeamStatus, readFog, parkingLamp, 0, 3, brakeLight, reverseLight};
                    bvavmJNI.bwSetLampStatus2(lightArray3);
                }else {
                    bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) readFog, (byte) parkingLamp, (byte) 0, (byte) 3, (byte) brakeLight);
                }
            }

        }, "setLight", 50);

        // bvavmJNI.bwSetLampStatus((byte) highBeamStatus, (byte) lowBeamStatus, (byte) reverseLight,(byte) parkingLamp,(byte) 0,(byte) 0,(byte)brakeLight);

    }

    //标定错误返回码
    public void setAutomaticCalibration(int value) {
        byte[] errorBack = {0x00, 0x0C, 0x00, 0x00};
        if (value == 0x00) {//成功
            KLog.d("Calibration return: " + AvmApp.getInstance().getString(R.string.camera_success));
            CustomToast.showToast(AvmApp.getInstance().getString(R.string.camera_success));
        }/* else if(value == -1){//此处可能是标定成功，但又返回了错误状态
            KLog.d("Calibration return mcu fail" + );
            errorBack = new byte[]{0x00, 0x00, 0x00, 0x00};
        }*/ else if (value == 0x01) {//前视标定失败
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
     * 是否有操作，如果有操作，R档退出的时候，30s不退出
     *
     * @param running
     */
    public void setRunning(boolean running) {
        isRunning = running;
        KLog.i("是否有操作:" + isRunning);
        mHandler.removeCallbacksAndMessages("setRunning");
        mHandler.removeCallbacksAndMessages("close_N");
        mHandler.removeCallbacksAndMessages("close_token");
        mHandler.removeCallbacks(getmRun);
        mHandler.postDelayed(() -> {
            KLog.i("setRunning:" + isRadarActive +" isTurn:"+isTurn +" isReverse:"+isReverse
            + "isClick :"+ isClick +" valGear:"+valGear +" isPGear:"+isPGear);
            if(!isClick){
                isPGear = false;
            }
            if (isRadarActive || isTurn || isReverse || (isClick && valGear != 4) || isPGear)
                return;
            dismissView(false, 0, "d7");
        }, "setRunning", 30 * 1000);

    }

    public void setRunning(){
        //点击左卡片直接进入倒计时关闭
        KLog.d("点击左卡片直接进入倒计时关闭");
        mHandler.removeCallbacksAndMessages("close_N");
        mHandler.removeCallbacks(getmRun);
        mHandler.removeCallbacksAndMessages("close_token");
        mHandler.removeCallbacksAndMessages("setRunning");
        mHandler.postDelayed(() -> {
            dismissView(false, 0, "d7");
        }, "setRunning", 30 * 1000);
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

    public void setViewModel(ViewType type) {
        KLog.d(valGear + " valGear挡位模式 valGear :" + type);
        AvmApp.getInstance().getCameraView().viewShowStatus(type,0);
    }

    /**
     * 快速启动时，延时2s 触发AVM 打开；
     */
    public void initActive() {
        int  gear_R = CanManager.getInstance().getIntStatus(CLUSTER_VCU_GEAR_LVL_DISP, 0);
        KLog.d(valGear + " 注册完成 获取挡位信息  valGear :" + gear_R);
        if(gear_R == 1 || gear_R == 2){
            if (AvmApp.mAvmRvcState == 1 && BvAvmJNIHelper.isAvmDeInit) {
                KLog.d(gear_R + "rvc已开启获取挡位信息N/D开启应用");
               reverse(gear_R);
            }
        }else if(gear_R == 4){
            KLog.d(gear_R + "p挡 rvc关闭不显示avm");
            valGear = gear_R;
            AvmApp.mAvmRvcState = 0;
        }
        if (AvmService.isCalibration){
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
