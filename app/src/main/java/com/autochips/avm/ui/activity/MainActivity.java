package com.autochips.avm.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.service.AvmService;
import com.autochips.avm.ui.view.CameraView;
import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.SystemProperties;
import com.gxa.service.camera.AvmManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.goldze.mvvmhabit.utils.KLog;

public class MainActivity extends AppCompatActivity implements AvmRuntime.ActionListener {
    private static final String TAG = "MainActivityAVM";
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateLayer();
        }
    };

    private static boolean isOnResume = false;
    private static final int SEND_AVM_STATE = 1001;

    public static MainActivity inStance;
    private static final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "MainActivity::handleMessage():"+msg.what);
            if (msg.what == SEND_AVM_STATE){
                if (CameraView.isShowing) {
                    AvmService.mCanSendAvmStateIsActivity = false;
                    AvmService.mCanSendAvmState = false;
                    int avm_state = SystemProperties.getGlobalInt("avm_state", -1);
                    Log.d(TAG, "MainActivity::handleMessage() avm_state:"+avm_state);
                    if(avm_state != 1) {
                        SystemProperties.setGlobal("avm_state", 1);
                        AvmManager.getInstance(AvmApp.getInstance()).sendAvmState(1);
                    }
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        inStance = this;
        setContentView(R.layout.activity_main);
        findViewById(R.id.parent_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                View windowView = AvmApp.getInstance().getCameraView().getRootView();
                windowView.dispatchTouchEvent(motionEvent);
                return true;
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("AvmRuntime", "MainActivity::onRestart()");
    }

    protected void onResume() {
        super.onResume();
        Log.d("AvmRuntime", "MainActivity::onResume()");
        isOnResume = true;
        AvmApp.getInstance().getCameraView().showRootView();
        if(AvmService.mCanSendAvmStateIsActivity){
            mHandler.sendEmptyMessageAtTime(SEND_AVM_STATE,CameraView.isShowing ? 0 : 100);
        }
        AvmRuntime.self().registerActionListener(MainActivity.this);
        getWindow().getDecorView().postDelayed(runnable, 0);
    }

    @Override
    protected void onPause() {
        isOnResume = false;
        super.onPause();
        AvmApp.getInstance().getCameraView().hideView();
        mHandler.removeCallbacksAndMessages("setRelativeLayer");
        Log.d(TAG, "MainActivity::onPause()");
    }

    protected void onStop() {
        super.onStop();
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d("AvmRuntime", "onDestroy() start read Surface control. FvSts is " + AvmRuntime.self().getFullSceneSts());
        mHandler.removeCallbacksAndMessages("setRelativeLayer");
        if (AvmRuntime.self().getFullSceneSts() != DataDefine.FV_STATE_NON) {
            AvmRuntime.self().artificialExit();
        }
        if (AvmApp.getInstance().getCameraView().getRootView() != null) {
            Log.d("AvmRuntime", "CameraView.windowSurfaceControl is " + CameraView.windowSurfaceControl);
            if (CameraView.windowSurfaceControl != null) {
                setSCLayer(CameraView.windowSurfaceControl, 0);
            }
        }
    }

    @Override
    public void onEnter(int act) {
        if (act == DataDefine.ACT_EXIT) {
            AvmRuntime.self().unregisterActionListener(this);

            AvmRuntime.self().setFullSceneSts(DataDefine.FV_STATE_NON);

            finish();
        }
    }

    @Override
    public void onExit(int act) {
    }

    @Override
    public void onGearNoAct(int gear, boolean handleFlag) {
        updateLayer();
    }

    private boolean updateLayer() {
        Log.d("AvmRuntime", "isRearGearSts : " + AvmRuntime.self().isRearGearSts() + " , getFullSceneSts is " + AvmRuntime.self().getFullSceneSts());
        if (AvmRuntime.self().isRearGearSts() || AvmRuntime.self().getFullSceneSts() == DataDefine.FV_STATE_LEFT_CARD) {
            Log.d("AvmRuntime", "start read Surface control.");
            if (CameraView.windowSurfaceControl == null)
                CameraView.windowSurfaceControl = getSurfaceControl(AvmApp.getInstance().getCameraView().getRootView());
            Log.d("AvmRuntime", "widnowSurfaceControl is " + CameraView.windowSurfaceControl);
            if (CameraView.windowSurfaceControl != null) {
                setSCLayer(CameraView.windowSurfaceControl, 0); //取消绑定
                return true;
            } else {
                return false;
            }
        } else {
            if(!isOnResume){
                Log.d(TAG, "act is not show");
                return false;
            }
            Log.d("AvmRuntime", "start read Surface control.");
            if (CameraView.windowSurfaceControl == null)
                CameraView.windowSurfaceControl = getSurfaceControl(AvmApp.getInstance().getCameraView().getRootView());
            Log.d("AvmRuntime", "widnowSurfaceControl is " + CameraView.windowSurfaceControl);
            SurfaceControl mySurfaceControl = getSurfaceControl();
            Log.d("AvmRuntime", "mySurfaceControl is " + mySurfaceControl);
            mHandler.removeCallbacksAndMessages("setRelativeLayer");
            if (CameraView.windowSurfaceControl != null && mySurfaceControl != null) {
                Log.d(TAG, "MainActivity::setRelativeLayer()");
                mHandler.postDelayed(()->{
                    if (CameraView.windowSurfaceControl != null && isOnResume) {
                        //规避存在窗口退出，act还未销毁导致的消息未发送
                        mHandler.sendEmptyMessage(SEND_AVM_STATE);
                        Log.d(TAG, "MainActivity::setRelativeLayer() true");
                        setRelativeLayer(CameraView.windowSurfaceControl, mySurfaceControl);//设置层级与act同级
                    }
                },"setRelativeLayer",300);
                return true;
            } else {
                return false;
            }
        }
    }

    private SurfaceControl getSurfaceControl(View overlayView) {
        Class<?> viewClassOvelay = null;
        try {
            //拿到window
            viewClassOvelay = Class.forName("android.view.View");
            Log.d("AvmRuntime", "viewClassOvelay is " + viewClassOvelay);
            // 反射获取ViewRootImpl对象
            @SuppressLint("DiscouragedPrivateApi") Method getViewRootImplMethod =
                    viewClassOvelay.getDeclaredMethod("getViewRootImpl");
            getViewRootImplMethod.setAccessible(true);
            Object viewRootImpl = getViewRootImplMethod.invoke(overlayView);
            Log.d("AvmRuntime", "viewRootImpl is " + viewRootImpl);
            if (viewRootImpl == null) return null;
            // 反射获取反射获取ViewRootImpl对象的SurfaceControl对象
            Class<?> viewRootImplclass = Class.forName("android.view.ViewRootImpl");
            Log.d("AvmRuntime", "viewRootImplclass is " + viewRootImplclass);
            @SuppressLint("BlockedPrivateApi") Method getSurfaceControlMethod =
                    viewRootImplclass.getDeclaredMethod("getSurfaceControl");
            getSurfaceControlMethod.setAccessible(true);
            SurfaceControl overlaySurfaceControl = (SurfaceControl) getSurfaceControlMethod.invoke(viewRootImpl);
            return overlaySurfaceControl;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SurfaceControl getSurfaceControl() {
        SurfaceControl currenActivitySurfaceControl = null;
        try {
            Window window = getWindow();
            Log.d("AvmRuntime", "window is " + window);
            View decorView = window.getDecorView();
            Log.d("AvmRuntime", "decorView is " + decorView);
            Class<?> viewClass = Class.forName("android.view.View");
            // 反射获取ViewRootImpl对象
            Method getViewRootImplMethod = viewClass.getDeclaredMethod("getViewRootImpl");
            getViewRootImplMethod.setAccessible(true);
            Object viewRootImpl = getViewRootImplMethod.invoke(decorView);
            Log.d("AvmRuntime", "viewRootImpl is " + viewRootImpl);
            if (viewRootImpl == null) return null;

            Class<?> viewRootImplclass = Class.forName("android.view.ViewRootImpl");
            // 反射获取SurfaceControl对象
            @SuppressLint("BlockedPrivateApi") Method getSurfaceControlMethod =
                    viewRootImplclass.getDeclaredMethod("getSurfaceControl");
            getSurfaceControlMethod.setAccessible(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                currenActivitySurfaceControl = (SurfaceControl) getSurfaceControlMethod.invoke(viewRootImpl);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return currenActivitySurfaceControl;
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private void setRelativeLayer(SurfaceControl windowSC, SurfaceControl activitySC) {
        try {
            // 获取SurfaceControl类
            Class<?> surfaceControlClass = Class.forName("android.view.SurfaceControl$Transaction");

            // 获取setRelativeLayer方法
            Method setRelativeLayerMethod = null;
            setRelativeLayerMethod = surfaceControlClass.getDeclaredMethod("setRelativeLayer",
                    SurfaceControl.class, SurfaceControl.class, int.class);
            // 设置setRelativeLayer方法的可访问性（如果是私有方法）
            setRelativeLayerMethod.setAccessible(true);

            //构建SurfaceControl$Transactio对象
            Class<?> transactionClass = Class.forName("android.view.SurfaceControl$Transaction");
            Object transactionObject = transactionClass.newInstance();

            SurfaceControl.Transaction transaction = null;
            // 调用setRelativeLayer方法
            transaction = (SurfaceControl.Transaction) setRelativeLayerMethod.invoke(transactionObject,
                    windowSC, activitySC, -1);
            assert transaction != null;
            transaction.apply();
            transaction.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private void setSCLayer(SurfaceControl windowSC, int z) {
        try {
            // 获取SurfaceControl类
            Class<?> surfaceControlClass = Class.forName("android.view.SurfaceControl$Transaction");

            // 获取setRelativeLayer方法
            Method setRelativeLayerMethod = null;
            setRelativeLayerMethod = surfaceControlClass.getDeclaredMethod("setLayer",
                    SurfaceControl.class, int.class);
            // 设置setRelativeLayer方法的可访问性（如果是私有方法）
            setRelativeLayerMethod.setAccessible(true);

            //构建SurfaceControl$Transactio对象
            Class<?> transactionClass = Class.forName("android.view.SurfaceControl$Transaction");
            Object transactionObject = transactionClass.newInstance();

            SurfaceControl.Transaction transaction = null;
            // 调用setRelativeLayer方法
            transaction = (SurfaceControl.Transaction) setRelativeLayerMethod.invoke(transactionObject,
                    windowSC, z);
            assert transaction != null;
            transaction.apply();
            transaction.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}