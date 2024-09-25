package com.autochips.avm.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.autochips.avm.R;
import com.autochips.avm.databinding.ViewBottomBinding;
import com.autochips.avm.helper.BvAvmJNIHelper;


import me.goldze.mvvmhabit.utils.KLog;

/**
 * 暂时废弃 ，通过其他方法添加底部窗口
 */
@SuppressLint("ResourceAsColor")
public class CameraViewBottom extends View {
    protected WindowManager.LayoutParams mWindowLps;//window的属性
    private WindowManager mWindowManager;
    private Context mContext;
    ViewBottomBinding cameraBinding;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public CameraViewBottom(Context context) {
        super(context);
        mContext = getContext();
        cameraBinding = ViewBottomBinding.inflate(LayoutInflater.from(mContext), null, false);
        initWindow();
    }

    @SuppressLint("WrongConstant")
    private void initWindow() {
        mWindowLps = new WindowManager.LayoutParams();
        mWindowLps.gravity = Gravity.CENTER;
        //临时注释让全屏显示，否则摄像头画面显示不全
        mWindowLps.alpha = 1.0f;//1.0f不透明
        mWindowLps.x = 120;
        mWindowLps.format = PixelFormat.TRANSLUCENT;
        mWindowLps.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showSmartWin() {
        mWindowLps.alpha = 0.0f;
        mWindowLps.gravity = Gravity.LEFT | Gravity.CENTER;
        mWindowLps.width = mContext.getResources().getDimensionPixelSize(R.dimen.screen_width_smart) - 200;
        mWindowLps.height = mContext.getResources().getDimensionPixelSize(R.dimen.screen_height_smart) - 300;
        mWindowLps.x = 200;
        mWindowLps.format = PixelFormat.TRANSLUCENT;
//        updateWind(0.0f,"showSmartWin");
        index = 0;
        mHandler.removeCallbacksAndMessages("showViewToken");
        showView();
    }


  public boolean isSmartWin = false;
  private boolean isFullWin = false;

  public void showFullWin(String  postion) {
      if (isFullWin){
        return;
      }

      isFullWin = true;
        KLog.d(postion+" postiont底部透明："+BvAvmJNIHelper.isAvmDeInit);
        mWindowLps.gravity = Gravity.CENTER;
        mWindowLps.width = mContext.getResources().getDimensionPixelSize(R.dimen.screen_width) - 80;
        mWindowLps.height = mContext.getResources().getDimensionPixelSize(R.dimen.screen_height) - 200;
        mWindowLps.format = PixelFormat.UNKNOWN;
        mWindowLps.x = 200;
        mWindowLps.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        mWindowLps.alpha = 0.0f;
//        updateWind(0.0f,"showFullWin");
        index = 0;
        mHandler.removeCallbacksAndMessages("showViewToken");
         showView();


    }
    private  int index = 0;
    private  void  showView(){
        int time = 300;
        if (BvAvmJNIHelper.isAvmDeInit){
           time = 50;
        }
        int finalTime = time;
        mHandler.postDelayed(()-> {
            KLog.d(CameraGLSurfaceView.glStatus + "显示底部window "+ finalTime +" CameraView.isIsShowing()"+CameraView.isIsShowing());
            if (CameraView.isIsShowing() && CameraGLSurfaceView.glStatus == 1 ){
                updateWind(1.0f,"showView");
                return;
            }
            if (index < 15){
              showView();
            }
           index += 1;
        },"showViewToken",time);
    }

    public void showInit() {
        mWindowLps.gravity = Gravity.LEFT;
        mWindowLps.width = 2;
        mWindowLps.height = mContext.getResources().getDimensionPixelSize(R.dimen.screen_height);
        mWindowLps.format = PixelFormat.TRANSLUCENT;
        mWindowLps.alpha = 0.0f;
        mWindowLps.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        boolean attachedToWindow = cameraBinding.getRoot().isAttachedToWindow();
        clearFocus();
        KLog.e("更新窗口 bottom_updateWind alpha showInit："+attachedToWindow);
        if (!attachedToWindow) {//如果已经添加view
            mWindowManager.addView(cameraBinding.getRoot(), mWindowLps);
        }
    }

    /**
     * 更新窗口
     */
    public void updateWind(float alpha ,String position) {
            mWindowLps.alpha = alpha;

             mWindowLps.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
           mHandler.postDelayed(()->{
               boolean attachedToWindow = cameraBinding.getRoot().isAttachedToWindow();
               clearFocus();
               KLog.e(position +" 更新窗口 bottom_updateWind alpha："+attachedToWindow);
               if (attachedToWindow) {//如果已经添加view
                   mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);
               } else {
                   mWindowManager.addView(cameraBinding.getRoot(), mWindowLps);
               }
           },20);



    }

  /**
   * 更新窗口
   */
  public void updateWind0() {
    mWindowLps.alpha = 1.0f;
    mWindowLps.width = 2;
    mWindowLps.height = 2;
    mWindowLps.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    boolean attachedToWindow = cameraBinding.getRoot().isAttachedToWindow();
    clearFocus();
    if (attachedToWindow) {//如果已经添加view
      mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);
    } else {
      mWindowManager.addView(cameraBinding.getRoot(), mWindowLps);
    }



  }






    public void dismissView(String position) {
      if (CameraView.isIsShowing()){
        return;
      }
        boolean attachedToWindow = cameraBinding.getRoot().isAttachedToWindow();
        KLog.d(position + "底部 dismissView attached = " + attachedToWindow);
        isFullWin = false;
        isSmartWin = false;
        mWindowLps.x = 120;
        mWindowLps.y = 0;
        mWindowLps.gravity = Gravity.LEFT;
        if (attachedToWindow) {//如果已经添加view
            mWindowLps.alpha = 0.0f;
            mWindowLps.width = 1;
            mWindowLps.height = 1;
            mWindowLps.flags =
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowManager.updateViewLayout(cameraBinding.getRoot(), mWindowLps);
//            mWindowManager.removeView(mViewCameraBinding.getRoot());

        }

    }
}
