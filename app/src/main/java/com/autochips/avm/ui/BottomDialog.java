package com.autochips.avm.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.autochips.avm.R;
import com.autochips.avm.databinding.ViewBottomTextBinding;

public class BottomDialog extends Dialog {
private  Context mContext;
  private WindowManager mWindowManager;
  protected WindowManager.LayoutParams layoutParams;
  private ViewBottomTextBinding textView;
  @SuppressLint("ResourceAsColor")
  public BottomDialog(@NonNull Context context) {
    super(context);

    mContext = context;
    textView = ViewBottomTextBinding.inflate(LayoutInflater.from(mContext),null,false);
    layoutParams = new WindowManager.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
      PixelFormat.TRANSLUCENT);
    layoutParams.gravity = Gravity.BOTTOM;
    layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
      | WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

  }

  @Override
  public void show() {
//    if (!isShowing()){
//      if (textView.getRoot().isAttachedToWindow()){
//        mWindowManager.updateViewLayout(textView.getRoot(), layoutParams);
//        return;
//      }
//
//      mWindowManager.addView(textView.getRoot(), layoutParams);
//    }
  }

  @Override
  public void dismiss() {
//    if (textView != null)
//     mWindowManager.removeView(textView.getRoot());
//    super.dismiss();
  }
}
