package com.autochips.avm.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.autochips.avm.R;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.service.AvmRuntime;
import com.autochips.avm.viewmode.CameraViewModel;

import androidx.annotation.Nullable;
import me.goldze.mvvmhabit.utils.KLog;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_FLMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_FRMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_PAS_FLDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_PAS_FRDistance;

/**
 * 雷达状态显示- 车头显示
 */
public class RadarStatusFrontView extends View {

    private Bitmap bmRadar1_red_1;
    private Bitmap bmRadar1_red_2;
    private Bitmap bmRadar1_red_3;
    private Bitmap bmRadar1_red_4;

    private Bitmap bmRadar2_orange_1;
    private Bitmap bmRadar2_orange_2;
    private Bitmap bmRadar2_orange_3;
    private Bitmap bmRadar2_orange_4;

    private Bitmap bmRadar3_yellow_1;
    private Bitmap bmRadar3_yellow_2;
    private Bitmap bmRadar3_yellow_3;
    private Bitmap bmRadar3_yellow_4;

    private Bitmap bmRadar4_green_1;
    private Bitmap bmRadar4_green_2;
    private Bitmap bmRadar4_green_3;
    private Bitmap bmRadar4_green_4;
    private Paint paint = new Paint();

    private boolean isShowRed1 = false;
    private boolean isShowOrange1 = false;
    private boolean isShowYellow1 = false;
    private boolean isShowGreen1 = false;


    private boolean isShowRed2 = false;
    private boolean isShowOrange2 = false;
    private boolean isShowYellow2 = false;
    private boolean isShowGreen2 = false;


    private boolean isShowRed3 = false;
    private boolean isShowOrange3 = false;
    private boolean isShowYellow3 = false;
    private boolean isShowGreen3 = false;

    private boolean isShowRed4 = false;
    private boolean isShowOrange4 = false;
    private boolean isShowYellow4 = false;
    private boolean isShowGreen4 = false;


    private CameraViewModel viewModel;

    public void setViewModel(CameraViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public RadarStatusFrontView(Context context) {
        super(context);
        init();
    }

    public RadarStatusFrontView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarStatusFrontView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private  int fRRLen = 180;
    private  int mildLen = 180;
    private  int mirdLen = 180;
    private  int fRLLen = 180;
    public void status(int vehicleId, int len ) {

        switch (vehicleId) {
            case CLUSTER_PAS_PAS_FRDistance:// 前右
                fRRLen = len;
                isShowRed1 = isShowBitmap30(len);
                isShowOrange1 = isShowBitmap60(len);
                showRadarBtn();
                invalidate();
                break;
            case CLUSTER_PAS_PAS_FLDistance:// 前左
                fRLLen = len;
                isShowRed4 = isShowBitmap30(len);
                isShowOrange4 = isShowBitmap60(len);
                showRadarBtn();
                invalidate();
                break;
            case CLUSTER_PAS_FRMidDistance://前右中
                mirdLen = len;
                isShowRed2 = isShowBitmap30(len);
                isShowOrange2 = isShowBitmap60(len);
                isShowYellow2 = isShowBitmap90(len);
                isShowGreen2 = isShowBitmap110(len);
                showRadarBtn();
                invalidate();
                break;
            case CLUSTER_PAS_FLMidDistance://（前左中） 60
                mildLen = len;
                isShowRed3 = isShowBitmap30(len);
                isShowOrange3 = isShowBitmap60(len);
                isShowYellow3 = isShowBitmap90(len);
                isShowGreen3 = isShowBitmap110(len);
                showRadarBtn();
                invalidate();
                break;
        }
    }

    // 修复雷达距离最小显示
    private  void showRadarBtn(){
        int radLen = 110 ;
        if (viewModel == null)return;
        radLen= Math.min(radLen,fRLLen);
        radLen= Math.min(radLen,fRRLen);
        radLen= Math.min(radLen,mildLen);
        radLen= Math.min(radLen,mirdLen);
        KLog.d(radLen+" 雷达距离最小："+radLen );
        if (radLen>30 && radLen <= 90){
            viewModel.getInfo().setRadarFrontDistance(radLen+"cm");
        }else if(radLen > 0 && radLen <= 30){
            viewModel.getInfo().setRadarFrontDistance(AvmApp.getInstance().getString(R.string.camera_please_park));
        }else {
            viewModel.getInfo().setRadarFrontDistance("");
        }
    }

    private boolean isShowBitmap30(int len) {
        boolean bl = len > 0 && len <= 30;
        return bl;
    }

    private boolean isShowBitmap60(int len) {
        return len > 30 && len <= 60;
    }

    private boolean isShowBitmap90(int len) {
        boolean bl = len > 60 && len <= 90;
        return bl;
    }

    private boolean isShowBitmap110(int len) {
        return len > 90 && len <= 110;
    }


    private void init() {
        // 初始化图片资源
        bmRadar1_red_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_fred_1);
        bmRadar1_red_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_fred_2);
        bmRadar1_red_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_fred_3);
        bmRadar1_red_4 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_fred_4);

        bmRadar2_orange_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_forange_1);
        bmRadar2_orange_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_forange_2);
        bmRadar2_orange_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_forange_3);
        bmRadar2_orange_4 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_forange_4);

        bmRadar3_yellow_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_fyellow_1);
        bmRadar3_yellow_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_fyellow_2);
        bmRadar3_yellow_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_fyellow_3);
        bmRadar3_yellow_4 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_fyellow_4);

        bmRadar4_green_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_fgreen_1);
        bmRadar4_green_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_fgreen_2);
        bmRadar4_green_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_fgreen_3);
        bmRadar4_green_4 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_fgreen_4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取 View 的宽度和高度
        int width = getWidth();
        int height = getHeight();

        // 绘制图片
        if (bmRadar1_red_1 != null) {
            float scaleX = (float) width / bmRadar1_red_1.getWidth();
            float scaleY = (float) height / bmRadar1_red_1.getHeight();
            float scale = Math.max(scaleX, scaleY);
            int scaledWidth = (int) (bmRadar1_red_1.getWidth() * scale);
            int scaledHeight = (int) (bmRadar1_red_1.getHeight() * scale);
            int left = 20;
            int top = 150 ;

            canvasBitmap(canvas, isShowRed1, bmRadar1_red_1);
            canvasBitmap(canvas, isShowOrange1, bmRadar2_orange_1);
            canvasBitmap(canvas, isShowYellow1, bmRadar3_yellow_1);
            canvasBitmap(canvas, isShowGreen1, bmRadar4_green_1);

            canvasBitmap(canvas, isShowRed2, bmRadar1_red_2);
            canvasBitmap(canvas, isShowOrange2, bmRadar2_orange_2);
            canvasBitmap(canvas, isShowYellow2, bmRadar3_yellow_2);
            canvasBitmap(canvas, isShowGreen2, bmRadar4_green_2);

            canvasBitmap(canvas, isShowRed3, bmRadar1_red_3);
            canvasBitmap(canvas, isShowOrange3, bmRadar2_orange_3);
            canvasBitmap(canvas, isShowYellow3, bmRadar3_yellow_3);
            canvasBitmap(canvas, isShowGreen3, bmRadar4_green_3);

            canvasBitmap(canvas, isShowRed4, bmRadar1_red_4);
            canvasBitmap(canvas, isShowOrange4, bmRadar2_orange_4);
            canvasBitmap(canvas, isShowYellow4, bmRadar3_yellow_4);
            canvasBitmap(canvas, isShowGreen4, bmRadar4_green_4);
        }
    }

    private void canvasBitmap(Canvas canvas, boolean isShow, Bitmap bitmap) {
        if (isShow) {
            canvas.drawBitmap(bitmap, null, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), paint);
        }
    }
}
