package com.autochips.avm.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.ui.view.CameraView;
import com.autochips.avm.util.DataDefine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.goldze.mvvmhabit.utils.KLog;

public class MainActivity extends AppCompatActivity implements AvmRuntime.ActionListener {

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateLayer();
        }
    };

    public static MainActivity inStance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
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
        AvmRuntime.self().registerActionListener(MainActivity.this);
        getWindow().getDecorView().postDelayed(runnable, 0);
    }

    protected void onStop() {
        super.onStop();
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d("AvmRuntime", "onDestroy() start read Surface control. FvSts is " + AvmRuntime.self().getFullSceneSts());
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
            Log.d("AvmRuntime", "start read Surface control.");
            if (CameraView.windowSurfaceControl == null)
                CameraView.windowSurfaceControl = getSurfaceControl(AvmApp.getInstance().getCameraView().getRootView());
            Log.d("AvmRuntime", "widnowSurfaceControl is " + CameraView.windowSurfaceControl);
            SurfaceControl mySurfaceControl = getSurfaceControl();
            Log.d("AvmRuntime", "mySurfaceControl is " + mySurfaceControl);

            if (CameraView.windowSurfaceControl != null && mySurfaceControl != null) {
                setRelativeLayer(CameraView.windowSurfaceControl, mySurfaceControl);//设置层级与act同级
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 调用setRelativeLayer方法
                transaction = (SurfaceControl.Transaction) setRelativeLayerMethod.invoke(transactionObject,
                        windowSC, activitySC, -1);
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