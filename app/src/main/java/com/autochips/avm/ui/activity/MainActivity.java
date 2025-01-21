package com.autochips.avm.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.helper.CameraViewModelHelper;
import com.autochips.avm.ui.view.CameraView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.goldze.mvvmhabit.utils.KLog;

public class MainActivity extends AppCompatActivity{

    private final Runnable runnable = this::updateLayer;

    public static MainActivity inStance;

    private CameraViewModelHelper.ChangeListener changeListener = new CameraViewModelHelper.ChangeListener() {
        @Override
        public void gearChange(int gear) {
            KLog.v("MainActivity", "MainActivity::gearChange():"+gear);
            if(gear == 3){
                //r档不需要act,解绑act
                unBindWindow();
            }else {
                if(AvmApp.getInstance().getCameraView().isFullWin){
                    bindWindow();
                }
            }
        }

        @Override
        public void viewChange() {
            KLog.v("MainActivity", "MainActivity::viewChange()");
            unBindWindow();
            //关闭
            finish();
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        inStance = this;
        setContentView(R.layout.activity_main);
        findViewById(R.id.parent_view).setOnTouchListener((view, motionEvent) -> {
            View windowView = AvmApp.getInstance().getCameraView().getRootView();
            windowView.dispatchTouchEvent(motionEvent);
            return true;
        });

        CameraViewModelHelper.getInstance().setChangeListener(changeListener);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        KLog.v("MainActivity", "MainActivity::onRestart()");
    }

    protected void onResume() {
        super.onResume();
        KLog.v("MainActivity", "MainActivity::onResume()");
        getWindow().getDecorView().postDelayed(runnable, 0);
    }

    protected void onStop() {
        super.onStop();
        //finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        changeListener = null;
        CameraViewModelHelper.getInstance().setChangeListener(null);
        KLog.v("MainActivity", "onDestroy() start read Surface control. FvSts is " + CameraView.isShowing);
        if (AvmApp.getInstance().getCameraView().getRootView() != null) {
            KLog.v("MainActivity", "CameraView.windowSurfaceControl is " + CameraView.windowSurfaceControl);
            if (CameraView.windowSurfaceControl != null) {
                setSCLayer(CameraView.windowSurfaceControl, 0);
            }
        }
    }

    public void onEnter(int act) {
        finish();
    }

    public void onExit(int act) {
    }

    public boolean updateLayer() {
        KLog.v("MainActivity", "isRearGearSts : " + CameraViewModelHelper.valGear + " , getFullSceneSts is " + AvmApp.getInstance().getCameraView().isSmartWin);
        if (CameraViewModelHelper.valGear == 3 || AvmApp.getInstance().getCameraView().isSmartWin) {
            return unBindWindow();
        } else {
            return bindWindow();
        }
    }

    private boolean bindWindow() {
        KLog.v("MainActivity", "start read Surface control.");
        if (CameraView.windowSurfaceControl == null)
            CameraView.windowSurfaceControl = getSurfaceControl(AvmApp.getInstance().getCameraView().getRootView());
        KLog.v("MainActivity", "widnowSurfaceControl is " + CameraView.windowSurfaceControl);
        SurfaceControl mySurfaceControl = getSurfaceControl();
        KLog.v("MainActivity", "mySurfaceControl is " + mySurfaceControl);
        if (CameraView.windowSurfaceControl != null && mySurfaceControl != null) {
            setRelativeLayer(CameraView.windowSurfaceControl, mySurfaceControl);//设置层级与act同级
            return true;
        } else {
            return false;
        }
    }

    //解绑
    private boolean unBindWindow() {
        KLog.v("MainActivity", "start read Surface control.");
        if (CameraView.windowSurfaceControl == null)
            CameraView.windowSurfaceControl = getSurfaceControl(AvmApp.getInstance().getCameraView().getRootView());
        KLog.v("MainActivity", "widnowSurfaceControl is " + CameraView.windowSurfaceControl);
        if (CameraView.windowSurfaceControl != null) {
            setSCLayer(CameraView.windowSurfaceControl, 0); //取消绑定
            return true;
        } else {
            return false;
        }
    }

    private SurfaceControl getSurfaceControl(View overlayView) {
        Class<?> viewClassOvelay = null;
        try {
            //拿到window
            viewClassOvelay = Class.forName("android.view.View");
            KLog.v("MainActivity", "viewClassOvelay is " + viewClassOvelay);
            // 反射获取ViewRootImpl对象
            @SuppressLint("DiscouragedPrivateApi") Method getViewRootImplMethod =
                    viewClassOvelay.getDeclaredMethod("getViewRootImpl");
            getViewRootImplMethod.setAccessible(true);
            Object viewRootImpl = getViewRootImplMethod.invoke(overlayView);
            KLog.v("MainActivity", "viewRootImpl is " + viewRootImpl);
            if (viewRootImpl == null) return null;
            // 反射获取反射获取ViewRootImpl对象的SurfaceControl对象
            Class<?> viewRootImplclass = Class.forName("android.view.ViewRootImpl");
            KLog.v("MainActivity", "viewRootImplclass is " + viewRootImplclass);
            @SuppressLint("BlockedPrivateApi") Method getSurfaceControlMethod =
                    viewRootImplclass.getDeclaredMethod("getSurfaceControl");
            getSurfaceControlMethod.setAccessible(true);
            SurfaceControl overlaySurfaceControl = (SurfaceControl) getSurfaceControlMethod.invoke(viewRootImpl);
            return overlaySurfaceControl;
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SurfaceControl getSurfaceControl() {
        SurfaceControl currenActivitySurfaceControl = null;
        try {
            Window window = getWindow();
            KLog.v("MainActivity", "window is " + window);
            View decorView = window.getDecorView();
            KLog.v("MainActivity", "decorView is " + decorView);
            Class<?> viewClass = Class.forName("android.view.View");
            // 反射获取ViewRootImpl对象
            @SuppressLint("DiscouragedPrivateApi") Method getViewRootImplMethod = viewClass.getDeclaredMethod("getViewRootImpl");
            getViewRootImplMethod.setAccessible(true);
            Object viewRootImpl = getViewRootImplMethod.invoke(decorView);
            KLog.v("MainActivity", "viewRootImpl is " + viewRootImpl);
            if (viewRootImpl == null) return null;

            @SuppressLint("PrivateApi") Class<?> viewRootImplclass = Class.forName("android.view.ViewRootImpl");
            // 反射获取SurfaceControl对象
            @SuppressLint("BlockedPrivateApi") Method getSurfaceControlMethod =
                    viewRootImplclass.getDeclaredMethod("getSurfaceControl");
            getSurfaceControlMethod.setAccessible(true);
            currenActivitySurfaceControl = (SurfaceControl) getSurfaceControlMethod.invoke(viewRootImpl);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
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
            transaction.apply();

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 调用setRelativeLayer方法
                transaction = (SurfaceControl.Transaction) setRelativeLayerMethod.invoke(transactionObject,
                        windowSC, z);
                transaction.apply();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}