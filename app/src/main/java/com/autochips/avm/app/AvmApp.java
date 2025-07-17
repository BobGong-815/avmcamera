package com.autochips.avm.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.R;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.ui.activity.MainActivity;
import com.autochips.avm.ui.view.CameraView;
import com.autochips.avm.util.ServiceUtils;
import com.autochips.avm.util.SystemProperties;

import java.util.Random;

import gxa.car.engineModeSdk.ConfigManager;
import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;

public class AvmApp extends BaseApplication implements Thread.UncaughtExceptionHandler {
    private CameraView mCameraView;
    public static int mAvmRvcState;//0隐藏 ，1、显示,-1、异常
    private static AvmApp mAvmApp;
    //    private CameraViewBottom viewBottom;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    //还未经验证的vechielplatForm value
    private int IS_AY5T = 103;
    private int IS_AY5G = 112; //左陀
    private int IS_AY5G_R = 112; //右陀 已经获取的值 112
    public static boolean ISAY5T = false;
    private boolean ISAY5G = false;
    private boolean ISAY5G_R = false;
    public static int EEA = 0;//平台,2表示3.0平台，0,1表示2.5平台
    private boolean IsOutsidebackmirrorautofoldswitch = true; //后视镜倒车下翻开关是否存在
    public static int OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH = 1;//0、无配置后视镜下翻 ，1、有配置后视镜下翻
    public static int OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH = 1;//0、无配置后视镜折叠 ，1、有配置后视镜折叠

    public volatile boolean isRight = true; // 默认非右陀,打包时根据修改该配置传入是否传入左右舵车型id

    public static AvmApp getInstance() {
        return mAvmApp;
    }

//    public CameraViewBottom getViewBottom() {
//        return viewBottom;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig(this);
        KLog.d("AVM app 启动 ActivityLifecycleCallbacks onCreate");
        //数据埋点
        bvavmJNI.bwDataEmbedding("com/autochips/avm/ui/view/CameraView","bAvmFault");
        mAvmApp = this;
        //是否开启打印日志
        KLog.init(true);

        KLog.d("[onCreate]");
        //连接信号服务
        SystemProperties.setGlobal("avm_state", 0);
        //获取车型
        //initConfig(mAvmApp);
        //初始化全局异常崩溃
        initCrash();
        //初始化摄像头画面数据
        //bvavmJNI.avmInit();
        //初始化AVM首页弹窗
        //数据埋点
        //DataManager.init(this);
//        Thread.setDefaultUncaughtExceptionHandler(this);
        ServiceUtils.startCaptureService(this, AvmService.class);
//        mHandler.postDelayed(()->{
//            closeAndShowAvm();
//        },10000);
    }

    private boolean isFirstOpen = true;
    private void closeAndShowAvm(){
        KLog.i("closeAndShowAvm isFirstOpen:" + isFirstOpen);
        Intent intent = new Intent();
        if(isFirstOpen){
            intent.setAction("action.syncore.FOPEN.mode");
        }else {
            if (mCameraView.isShowing) {
                intent.setAction("action.syncore.CLOSE.mode");
            } else {
                intent.setAction("action.syncore.OPEN.mode");
            }
        }
        sendBroadcast(intent);
        Random random = new Random();
        int randSecond = random.nextInt(2700)+300;
        KLog.d("AvmApp randSecond:"+randSecond);
        mHandler.postDelayed(() -> {
            isFirstOpen = false;
            closeAndShowAvm();
        }, isFirstOpen ? 10000 : randSecond);
    }

    private void initConfig(Context context) {
        ConfigManager configManager = ConfigManager.getInstance(context);
        configManager.registerInitListener(isConnect -> {
            if (isConnect) {
                int vehicalplatform = configManager.getVehicleplatform();
                int rudderCfg = configManager.getRudderCfg();
                EEA = configManager.getEEA();
                KLog.i("Avmapp....注册完成 ... " + rudderCfg+" 平台:"+EEA);
                int outsidebackmirrorbackupdownswitch = configManager.getOutsidebackmirrorbackupdownswitch();
                int outsidebackmirrorautofoldswitch = configManager.getOutsidebackmirrorautofoldswitch();
                Log.i("AvmApp","注册完成---- outsidebackmirrorbackupdownswitch:"+outsidebackmirrorbackupdownswitch);
                Log.i("AvmApp","注册完成---- outsidebackmirrorautofoldswitch:"+outsidebackmirrorautofoldswitch);
                OUTSIDE_BACKMIRROR_BACKDOWN_SWITCH = outsidebackmirrorbackupdownswitch;
                OUTSIDE_BACKMIRROR_AUTOFOLD_SWITCH = outsidebackmirrorautofoldswitch;
                //AY5T AY5G左陀  AY5右陀
                if (mCameraView == null) {
                    if (rudderCfg == 1) {
                        isRight = true;
                        BvAvmJNIHelper.getInstance().bwSetProjectID(bvavmJNI.PROJ_AY5_GR_ID);
                    } else {
                        isRight = false;
                        BvAvmJNIHelper.getInstance().bwSetProjectID(bvavmJNI.PROJ_AY5_G_ID);
                    }
                    Intent intentService =  new Intent(context, AvmService.class);
                    intentService.putExtra("initCam","init");
                    context.startService(intentService);
                }
            }else {
                KLog.i("Avmapp....还未连接成功 ...");
            }
        });
    }

    public CameraView getCameraView() {
        return mCameraView;
    }

    public void createCameraView(){
        mCameraView = new CameraView(this);
    }
    private void initCrash() {
        CaocConfig.Builder.create().backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
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
        KLog.d(t.getName() + "Thread = " + e.getMessage());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        KLog.d("AVMAPP    =    onConfigurationChanged");
        getCameraView().skinView();
    }

}
