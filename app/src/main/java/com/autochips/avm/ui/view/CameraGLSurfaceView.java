package com.autochips.avm.ui.view;

import static android.opengl.GLES10.GL_TRUE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.util.GlobalSetting;
import com.autochips.avm.util.SystemProperties;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import gxa.car.power.manager.CarPowerManager;
import me.goldze.mvvmhabit.utils.KLog;

/**
 * 视频渲染
 */
public class CameraGLSurfaceView extends GLSurfaceView {

    public static volatile int sCameraDirection = bvavmJNI.BW_FRONT_3D;
    private int nowShowDirection = -1;//当前显示视图
     Renderer renderer;
    {
        renderer = new Renderer();
    }

    private  static  final int SHOW_BOTTOM = 2;
    private  Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what){
                case SHOW_BOTTOM:
                    BvAvmJNIHelper.getInstance().onClick();
                    break;

            }
        }
    };

    public CameraGLSurfaceView(Context context) {
        super(context);
        KLog.d("CameraGLSurfaceView");
        initData();
        setZOrderOnTop(true);
    }

    public CameraGLSurfaceView(Context context, AttributeSet set) {
        super(context);
        KLog.d("CameraGLSurfaceView2");
        initData();
    }

    private  boolean isOpenCamera = false;
    private void initData() {

        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLContextClientVersion(3);
        setEGLConfigChooser(new MyConfigChooser());
        setRenderer(renderer);

    }

    public static void setAngleOfView(int value) {
        //sCameraDirection = value;
    }

    public static void setAngleOfView2(int value) {
        sCameraDirection = value;
    }

    public static int getAngleOfView() {
        return sCameraDirection;
    }

    public static int getLAngleOfView() {
        return sCameraDirection;
    }

    public void stopSurface(){
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
  public static int glStatus = 0;
    private class Renderer implements GLSurfaceView.Renderer {

        public void onDrawFrame(GL10 gl) {
            if(!BvAvmJNIHelper.isAvmDeInit) {
                KLog.d("is not init");
                return;
            }
            long camreaStatus = BvAvmJNIHelper.getInstance().camreaStatus();
            isOpenCamera = (camreaStatus != 0 && camreaStatus !=-1);
            if (AvmService.isExitAction && !isOpenCamera){
                return;
            }
            //修改摄像头画面数据
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            if(!isOpenCamera){
                return;
            }
            //修改摄像头画面数据
            //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


            if(BvAvmJNIHelper.getInstance().isCloseingCamrea){
                KLog.d("onDrawFrame camrea is Closeing");
                return;
            }
//            if(AvmApp.mAvmRvcState == 1){
//                KLog.d("rvc is showing");
//                return;
//            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (glStatus == 0){
                handler.sendEmptyMessageDelayed(SHOW_BOTTOM,0);
            }
            if(CameraView.isIsShowing()) {
                if(nowShowDirection != sCameraDirection) {
                    KLog.d("sCameraDirection 视图=" + sCameraDirection);
                    nowShowDirection = sCameraDirection;
                }
                int renderResult = BvAvmJNIHelper.getInstance().avmRender2(sCameraDirection);
                if(renderResult == -1){
                    DataManager.writeFault(DataConstant.Code.SF_FAIL);
                    DataManager.writeFault(DataConstant.Code.TX_FAIL);
                }
            }


          glStatus = 1;

        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            KLog.d("sCameraDirection width=" +width+ " height=" + height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // 设置清除颜色为透明
            KLog.i("ActivityLifecycleCallbacks onSurfaceCreated 创建画布");
            //初始化摄像头画面数据

            BvAvmJNIHelper.getInstance().avmInit(getContext());
            KLog.i("ActivityLifecycleCallbacks onSurfaceCreated 创建画布结束");
            try {
                int settingPathLine = Settings.Global.getInt(getContext().getContentResolver(), GlobalSetting.AVM_SETTING_TRAJECTORY);
                bvavmJNI.bwSetTrajLineStatus((byte)settingPathLine);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

//            setIndexTab();
//            bvavmJNI.bwNotifyRVC(0);
            KLog.d(" valGear 结束RVC-1 resRvc  handler：了");
//          handler.postDelayed(()->  bvavmJNI.bwNotifyRVC(0),2000);
          CameraViewModelHelper.getInstance().setTransparentIndexTab();
            CarPowerManager carPowerManager = CarPowerManager.getInstance(getContext(), null);
            boolean iscanOpen = true;//是否可以打开摄像头
//          if(BvAvmJNIHelper.getInstance().isCamera2Device()){
//            BvAvmJNIHelper.getInstance().bwCreateCamera("com/autochips/avm/ui/view/CameraView","onBVAVMMessage");
//            isOpenCamera = true;
//          }
//            boolean serviceConnected = carPowerManager.isServiceConnected();
//            KLog.i(" is serviceConnected:"+serviceConnected);
//            int currentCarPowerMode = -1;
//            if (carPowerManager.isServiceConnected()) {
//                currentCarPowerMode = carPowerManager.getCurrentCarPowerMode();
//                boolean isStr = currentCarPowerMode == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_DEEP_SLEEP.getVal() ||
//                        currentCarPowerMode == CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum.CAR_POWER_WORKMODE_REQUEST_ON_DISPLAY_OFF.getVal();
//                if(isStr){
//                    //半功能休眠状态下要关闭摄像头
//                    BvAvmJNIHelper.getInstance().bwDeleteCamera();
//                }
//                iscanOpen = !isStr;
//                KLog.i("gl currentCarPowerMode:" + currentCarPowerMode +" iscanOpen:"+iscanOpen);
//            }
//          if(!serviceConnected || currentCarPowerMode != CarPowerWorkModeStatus.CarPowerWorkModeStatusEnum
//                  .CAR_POWER_WORKMODE_REQUEST_ON_FULL.getVal()){
//              BvAvmJNIHelper.getInstance().bwDeleteCamera();
//          }
          //AvmApp.getInstance().getCameraView().notifyRvc();
        }
    }

    private void setIndexTab(){
        int position = SystemProperties.getInt("settingRadarActivatedPanorama", -1);
        KLog.e(position+" setIndexTab 设置透明底盘-初始化后调用: " + position);
        if (position == -1)return;
        if (position == 0) {
            bvavmJNI.bwSetCarBottomStatus((byte) 0);
            bvavmJNI.bwSetCarTransparency( 1f);
        }else if(position == 1){
            bvavmJNI.bwSetCarBottomStatus((byte) 1);
            bvavmJNI.bwSetCarTransparency(0.3f);
        } else if(position == 2){
            bvavmJNI.bwSetCarBottomStatus((byte) 1);
            bvavmJNI.bwSetCarTransparency( 0.15f);
        }else {
            bvavmJNI.bwSetCarBottomStatus((byte) 1);
            bvavmJNI.bwSetCarTransparency(0.05f);
        }
    }



    public class MyConfigChooser implements EGLConfigChooser {

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] attribs = new int[]{
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_SAMPLE_BUFFERS, GL_TRUE,//打开多采样抗锯齿
                    EGL10.EGL_SAMPLES, 4,  // 采样数
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

            if (configCounts[0] == 0) {
                // Failed! Error handling.
                return null;
            } else {
                return configs[0];
            }
        }
    }
}
