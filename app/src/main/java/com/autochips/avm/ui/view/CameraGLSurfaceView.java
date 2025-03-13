package com.autochips.avm.ui.view;

import static android.opengl.GLES10.GL_TRUE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.data.DataConstant;
import com.autochips.avm.data.DataManager;
import com.autochips.avm.helper.BvAvmJNIHelper;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.util.SystemProperties;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import me.goldze.mvvmhabit.utils.KLog;

/**
 * 视频渲染
 */
public class CameraGLSurfaceView extends GLSurfaceView {

    public static int doCalibrateNum = 0;
    public static volatile int lastCeameraDirection = bvavmJNI.BW_VIEW_POWER_OFF;
    private static volatile int sCameraDirection = bvavmJNI.BW_VIEW_POWER_OFF;
    private int nowShowDirection = -1;//当前显示视图

    private Renderer renderer;
    private static final int MSG_RENDER = 1;
    private static final long FRAME_INTERVAL_MS = 30;

    private static final int SHOW_BOTTOM = 2;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case SHOW_BOTTOM:
                    BvAvmJNIHelper.getInstance().onClick();
                    break;
                case MSG_RENDER:
                    // 触发onDrawFrame
                    requestRender();
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


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startFrameCallback();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopFrameCallback();
    }

    private void startFrameCallback() {
        //启动渲染循环
        handler.sendEmptyMessage(MSG_RENDER);
    }

    private void stopFrameCallback() {
        //停止渲染循环
        handler.removeMessages(MSG_RENDER);
    }

    private boolean isOpenCamera = false;

    private void initData() {
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLContextClientVersion(3);
        setEGLConfigChooser(new MyConfigChooser());
        setRenderer(renderer = new Renderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // 手动控制渲染
    }

    public void stopSurface() {
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public static int glStatus = 0;

    private int cnt = 0;
    private class Renderer implements GLSurfaceView.Renderer {

        public void onDrawFrame(GL10 gl) {
            long camreaStatus = BvAvmJNIHelper.getInstance().camreaStatus();
            isOpenCamera = (camreaStatus != 0 && camreaStatus != -1);
            if (AvmService.isExitAction && !isOpenCamera) {
                return;
            }
            //修改摄像头画面数据
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // 设置清除颜色为透明
            if (glStatus == 0) {
                handler.sendEmptyMessageDelayed(SHOW_BOTTOM, 0);
            }
            if(nowShowDirection != sCameraDirection) {
                KLog.d("sCameraDirection 视图=" + sCameraDirection);
                nowShowDirection = sCameraDirection;
            }
                if (doCalibrateNum > 0) {
                    KLog.d("doCalibrateNum = " + doCalibrateNum);
                    if (doCalibrateNum == 1) {
                        int renderResult1 = BvAvmJNIHelper.getInstance().avmRender2(bvavmJNI.BW_2D_FRONT_UNDISTORT);
                        AvmApp.getInstance().getCameraView().getViewModel().callCalibrate(1);
                        if(renderResult1 == -1){
                            DataManager.writeFault(DataConstant.Code.SF_FAIL);
                            DataManager.writeFault(DataConstant.Code.TX_FAIL);
                        }
                    }

                    doCalibrateNum--;
                } else {
                    if (CameraView.isIsShowing() && sCameraDirection != bvavmJNI.BW_VIEW_POWER_OFF) {
                        int renderResult2 = BvAvmJNIHelper.getInstance().avmRender2(sCameraDirection);
                        if(renderResult2 == -1){
                            DataManager.writeFault(DataConstant.Code.SF_FAIL);
                            DataManager.writeFault(DataConstant.Code.TX_FAIL);
                        }
                    }
                }
            handler.sendEmptyMessageDelayed(MSG_RENDER, FRAME_INTERVAL_MS); // 继续下一帧
            glStatus = 1;
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            KLog.d("onSurfaceChanged width=" + width + " height=" + height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            KLog.d("ActivityLifecycleCallbacks onSurfaceCreated 创建画布");
            //初始化摄像头画面数据
            BvAvmJNIHelper.getInstance().avmInit(getContext());
            KLog.d("ActivityLifecycleCallbacks onSurfaceCreated 创建画布结束");
            int settingPathLine = SystemProperties.getInt("settingPathLine", -1);
//            bvavmJNI.bwSetTrajLineStatus((byte) settingPathLine);
            if (AvmApp.getInstance().getCameraView() != null) AvmApp.getInstance().getCameraView().getViewModel().setTrajLineEnable((byte) settingPathLine);
//            setIndexTab();
//            bvavmJNI.bwNotifyRVC(0);
            KLog.d(" valGear 结束RVC-1 resRvc  handler：了");
//          handler.postDelayed(()->  bvavmJNI.bwNotifyRVC(0),2000);
            // CameraViewModelHelper.getInstance().setTransparentIndexTab();
//            if (!AvmService.JNI_IN_THREAD_FLAG) {
//                if (BvAvmJNIHelper.getInstance().isCamera2Device()) {
//                    BvAvmJNIHelper.getInstance().bwCreateCamera("com/autochips/avm/ui/view/CameraView", "onBVAVMMessage");
//                    isOpenCamera = true;
//                }
//            }
//            if (BvAvmJNIHelper.getInstance().isCamera2Device()) {
//                BvAvmJNIHelper.getInstance().bwCreateCamera("com/autochips/avm/ui/view/CameraView", "onBVAVMMessage");
//                isOpenCamera = true;
//            }

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
                    EGL10.EGL_ALPHA_SIZE, 8,// 8 改为0  为透字问题
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

    public static void setAngleOfView(int value) {
        KLog.i(" setAngleOfView .... value is " + value + " , sCameraDirection is " + sCameraDirection);
        if (value != sCameraDirection) {
            lastCeameraDirection = sCameraDirection;
            sCameraDirection = value;
            AvmRuntime.self().onViewAngleChanged(sCameraDirection);
        }
    }

    public static int getCameraDirection() {
        return sCameraDirection;
    }
}
