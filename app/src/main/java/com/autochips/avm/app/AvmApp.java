package com.autochips.avm.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.BuildConfig;
import com.autochips.avm.R;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.ui.activity.MainActivity;
import com.autochips.avm.ui.view.CameraView;
import com.autochips.avm.util.ServiceUtils;
import com.autochips.avm.util.SystemProperties;
import com.avm.framwork.manager.CanManager;

import gxa.car.engineModeSdk.ConfigManager;
import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;
//import skin.support.SkinCompatManager;
//import skin.support.app.SkinAppCompatViewInflater;
//import skin.support.app.SkinCardViewInflater;
//import skin.support.constraint.app.SkinConstraintViewInflater;
//import skin.support.design.app.SkinMaterialViewInflater;

public class AvmApp extends BaseApplication implements Thread.UncaughtExceptionHandler {
    private CameraView mCameraView;
    private static AvmApp mAvmApp;
    public static int mAvmRvcState;//0隐藏 ，1、显示,-1、异常
    public static int OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH = 1;//0、无配置后视镜下翻 ，1、有配置后视镜下翻
    public static int OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH = 1;//0、无配置后视镜折叠 ，1、有配置后视镜折叠
//    private CameraViewBottom viewBottom;
    private int IS_AY5 = 0x67;
    private int IS_AY3 = 0x66;
    public static boolean ISAY5 = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static AvmApp getInstance() {
        return mAvmApp;
    }

//    public CameraViewBottom getViewBottom() {
//        return viewBottom;
//    }

    @Override
    public void onCreate() {

        super.onCreate();
        KLog.d("AVM app 启动 ActivityLifecycleCallbacks onCreate:"+ BuildConfig.VERSION_NAME);
        mAvmApp = this;
        //数据埋点
        bvavmJNI.bwDataEmbedding("com/autochips/avm/ui/view/CameraView","bAvmFault");
//        DataManager.init(this);
//        AppHandler.getInstance().onApplicationCreate();
        initConfig(mAvmApp);
        //是否开启打印日志
        KLog.init(true);
        // 暂时废弃
//        viewBottom = new CameraViewBottom(this);
//        viewBottom.showInit();
        KLog.d("[onCreate]");
        //连接信号服务
        SystemProperties.setGlobal("avm_state", 0);
        //初始化全局异常崩溃
        initCrash();
        //初始化摄像头画面数据
        //bvavmJNI.avmInit();
        //初始化AVM首页弹窗
        int[] rvcStatus = new int[1];
        rvcStatus[0] = 0;
        bvavmJNI.bwGetRVCStatus(rvcStatus);
        mAvmRvcState = rvcStatus[0];
        KLog.d("[onCreate] mAvmRvcState:"+mAvmRvcState);
        if(mAvmRvcState != 1 && mAvmRvcState != -1){
            bvavmJNI.bwNotifyRVC(0);
        }
//        SkinCompatManager.withoutActivity(this)
//                .addInflater(new SkinAppCompatViewInflater())           // 基础控件换肤初始化
//                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
//                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
//                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
//                .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
//                .setSkinWindowBackgroundEnable(true)                   // 关闭windowBackground换肤，默认打开[可选]
//                .loadSkin();
        Thread.setDefaultUncaughtExceptionHandler(this);
        ServiceUtils.startCaptureService(this,AvmService.class);
//        mHandler.postDelayed(()->{
//
//            mCameraView.updateWind(0.0f,2);
//        },3000);


        DataManager.writeFault(DataConstant.Code.COMMING_APP);
    }

    private void initConfig(Context context) {
        ConfigManager configManager = ConfigManager.getInstance(context);
        configManager.registerInitListener(isConnect -> {
            //获取配置码
            if(isConnect){
                int vehicleplatform = configManager.getVehicleplatform();
                int outsidebackmirrorbackupdownswitch = configManager.getOutsidebackmirrorbackupdownswitch();
                int outsidebackmirrorautofoldswitch = configManager.getOutsidebackmirrorautofoldswitch();
                Log.i("AvmApp","注册完成---- getVehicleplatform:"+vehicleplatform);
                Log.i("AvmApp","注册完成---- outsidebackmirrorbackupdownswitch:"+outsidebackmirrorbackupdownswitch);
                Log.i("AvmApp","注册完成---- outsidebackmirrorautofoldswitch:"+outsidebackmirrorautofoldswitch);
                OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH = outsidebackmirrorbackupdownswitch;
                OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH = outsidebackmirrorautofoldswitch;
                if(vehicleplatform == IS_AY5){
                    ISAY5 = true;
                    BvAvmJNIHelper.getInstance().bwSetProjID(bvavmJNI.PROJ_AY5_ID);
                }else if(vehicleplatform == IS_AY3){
                    ISAY5 = false;
                    BvAvmJNIHelper.getInstance().bwSetProjID(bvavmJNI.PROJ_AY3_ID);
                }
                mHandler.post(()->{
                    if(mCameraView == null) {
                        mCameraView = new CameraView(context);
                        Intent intent = new Intent(AvmApp.getInstance(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        AvmApp.getInstance().startActivity(intent);
                        mCameraView.updateWind(0.0f, 2);
                        CanManager.getInstance().startConnect((v -> {
                            mCameraView.dismissView("初始化关闭......");
                            CameraViewModelHelper.getInstance().initActive();
                        }));
                    }
                });
            }else {
                Log.i("AvmApp","registerInitListener---- fail");
            }
        });
    }

    public CameraView getCameraView() {
        return mCameraView;
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                //                .restartActivity(MainActivity.class) //重新启动后的activity
                //                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
                //                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    //Android中Application的onTerminate()函数只是用来在Android设备的模拟器中，如果application退出才会回调。
    //但是，在产品级（即运行在Android真机设备）应用App，不会再整个App退出时候回调这个onTerminate()函数。
    @Override
    public void onTerminate() {
        super.onTerminate();
        KLog.d("");
    }

    @Override
    public void onLowMemory() {//在低内存时调用
        super.onLowMemory();
        KLog.d("");
    }

    @Override
    public void onTrimMemory(int level) {//系统会根据不同的内存状态来回调
        super.onTrimMemory(level);
        KLog.d("level = " + level);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        KLog.d(t.getName() +"Thread = " + e.getMessage());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        KLog.d("AVMAPP    =    onConfigurationChanged" );
        getCameraView().skinView();
    }

}
