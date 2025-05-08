package com.autochips.avm.ui.activity;

import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.BR;
import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.databinding.ActivityMockBinding;
import com.autochips.avm.viewmode.MockViewModel;

import java.util.Random;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.KLog;

/**
 * 模拟测试功能
 */
public class MockActivity extends BaseActivity<ActivityMockBinding, MockViewModel> {
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 1;
    private long cameraObj = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    private MockViewModel rearviewMirrorModel;

    @Override
    public void initParam() {
        super.initParam();

    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_mock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.d("AVM app 启动 MockActivity ");
       // getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        //getWindow().getAttributes().type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //openCamera();
        //skinView();y
        int type = new Random().nextInt(2);
        KLog.d("AVM app 启动 MockActivity showType " + type);
        //CameraShowTypeHelper.getInstance().showType(1);
        mHandler.postDelayed(() -> {
                    openAvm();
                }
                , 500);
        finish();
    }

    @Override
    public void initData() {
        super.initData();
        Log.i("AVM", " 显示AVM initData： ");
    }

    private void openAvm() {
        Intent mIntent = new Intent();
        mIntent.setClassName("com.autochips.avm", "com.autochips.avm.service.AvmService");
        mIntent.setAction("com.android.avm.action.AvmStart");
        mIntent.putExtra("avm_start", 1);
        AvmApp.getInstance().startService(mIntent);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {

    }

    private void openCamera() {
        if (isCamera2Device()) {
            RequestCamera();
        } else {
            KLog.e("CameraSample", "Found legacy camera device, this sample needs camera2 device");
        }

    }


    private boolean isCamera2Device() {
        CameraManager camMgr = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        boolean camera2Dev = true;
        try {
            String[] cameraIds = camMgr.getCameraIdList();
            if (cameraIds.length != 0) {
                for (String id : cameraIds) {
                    CameraCharacteristics characteristics = camMgr.getCameraCharacteristics(id);
                    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY &&
                            facing == LENS_FACING_BACK) {
                        camera2Dev = false;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            camera2Dev = false;
        }
        return camera2Dev;
    }

    public void RequestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE_CAMERA);
            return;
        }

        cameraObj = bvavmJNI.bwCreateCamera("com/autochips/avm/ui/activity/MockActivity", "onBVAVMMessage");
        KLog.e("CameraSample", "bvavmJNI.bwCreateCamera()");
    }

    protected void onDestroy() {
//        bvavmJNI.avmDeInit();
//        bvavmJNI.bwDeleteCamera(cameraObj);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        /*
         * if any permission failed, the sample could not play
         */
        if (PERMISSION_REQUEST_CODE_CAMERA != requestCode) {
            super.onRequestPermissionsResult(requestCode,
                    permissions,
                    grantResults);
            return;
        }

        if (grantResults.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Thread initCamera = new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cameraObj = bvavmJNI.bwCreateCamera("com/autochips/avm/ui/activity/MockActivity", "onBVAVMMessage");
                        }
                    });
                }
            });
            initCamera.start();
        }
    }

}
