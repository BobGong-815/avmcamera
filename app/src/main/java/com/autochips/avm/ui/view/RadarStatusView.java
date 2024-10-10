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

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RLDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RLMidDistance;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_PAS_RRDistance;

/**
 * 雷达状态显示
 */
public class RadarStatusView extends View {

    private static final String TAG = RadarStatusView.class.getName();
    private Bitmap bmRadar1_red_1;
    private Bitmap bmRadar1_red_2;
    private Bitmap bmRadar1_red_3;

    private Bitmap bmRadar2_orange_1;
    private Bitmap bmRadar2_orange_2;
    private Bitmap bmRadar2_orange_3;

    private Bitmap bmRadar3_yellow_1;
    private Bitmap bmRadar3_yellow_2;
    private Bitmap bmRadar3_yellow_3;

    private Bitmap bmRadar4_green_1;
    private Bitmap bmRadar4_green_2;
    private Bitmap bmRadar4_green_3;
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

    private CameraViewModel viewModel;

    public void setViewModel(CameraViewModel viewModel) {
        this.viewModel = viewModel;

    }



    private  int rRRLen = 180;
    private  int mildLen = 180;
    private  int rRLLen = 180;
    public void status(int vehicleId, int len) {
        switch (vehicleId) {

            case CLUSTER_PAS_RLMidDistance:// 后左中
              mildLen = len;
                isShowRed2 = isShowBitmap30(len);
                isShowOrange2 = isShowBitmap60(len);
                isShowYellow2 = isShowBitmap90(len);
                isShowGreen2 = isShowBitmap150(len);
                showRadarBtn();
                invalidate();
                break;
            case CLUSTER_PAS_RRDistance:// 后右
                rRRLen = len;
                isShowRed3 = isShowBitmap30(len);
                isShowOrange3 = isShowBitmap60(len);
                isShowYellow3 = isShowBitmap90(len);
                isShowGreen3 = isShowBitmap150(len);
                showRadarBtn();
                invalidate();
                break;
            case CLUSTER_PAS_RLDistance://后左
                rRLLen = len;
                isShowRed1 = isShowBitmap30(len);
                isShowOrange1 = isShowBitmap60(len);
                isShowYellow1 = isShowBitmap90(len);
                isShowGreen1 = isShowBitmap150(len);
                showRadarBtn();
                invalidate();
                break;

        }

    }

    private boolean isShowBitmap30(int len) {
        boolean bl = len > 0 && len <= 30;
        //if (bl)
            //ToastUtils.showLong("请停车 30CM");
            //viewModel.getInfo().setRadarDistance("请停车");
        return bl && AvmRuntime.self().isRearGearSts();

    }

    private boolean isShowBitmap60(int len) {
        return len > 30 && len <= 60 && AvmRuntime.self().isRearGearSts();

    }

    private boolean isShowBitmap90(int len) {
        boolean bl = len > 60 && len <= 90;
        return bl  && AvmRuntime.self().isRearGearSts();
    }

    private boolean isShowBitmap150(int len) {
        return len > 90 && len <= 150  && AvmRuntime.self().isRearGearSts();
    }

    /**
     * 是否显示雷达距离，需要根据是否雷达盾牌而决定
     * @return
     */
    private boolean isShowTextTip(){
        if (isShowRed1 || isShowOrange1 ||isShowYellow1){
            return true;
        }
        if (isShowRed2 || isShowOrange2 ||isShowYellow2){
            return true;
        }
        if (isShowRed3 || isShowOrange3 ||isShowYellow3){
            return true;
        }
        return false;

    }


    // 修复雷达距离最小显示
    private  void showRadarBtn(){
        int radLen = 150 ;
        if (viewModel == null )return;
        if (!AvmRuntime.self().isRearGearSts()){
            viewModel.getInfo().setRadarDistance("");
            viewModel.getInfo().setShowRadarBtn(false);
            return;
        }
        radLen= Math.min(radLen,rRLLen);
        radLen= Math.min(radLen,rRRLen);
        radLen= Math.min(radLen,mildLen);
        KLog.d(radLen+" 雷达距离最小："+radLen );
        viewModel.getInfo().setShowRadarBtn(radLen <= 90);// 是否显示雷达距离
        if (radLen>30 && radLen <= 90){
            viewModel.getInfo().setRadarDistance(radLen+"cm");
        }else if(radLen > 0 && radLen <= 30){
            viewModel.getInfo().setRadarDistance(AvmApp.getInstance().getString(R.string.camera_please_park));
        }else {
            viewModel.getInfo().setRadarDistance("");
        }
    }

    public RadarStatusView(Context context) {
        super(context);
        init();
    }

    public RadarStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();


    }

    private void init() {
        // 初始化图片资源
        bmRadar1_red_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_red_1);
        bmRadar1_red_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_red_2);
        bmRadar1_red_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar1_red_3);

        bmRadar2_orange_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_orange_1);
        bmRadar2_orange_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_orange_2);
        bmRadar2_orange_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar2_orange_3);

        bmRadar3_yellow_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_yellow_1);
        bmRadar3_yellow_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_yellow_2);
        bmRadar3_yellow_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar3_yellow_3);

        bmRadar4_green_1 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_green_1);
        bmRadar4_green_2 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_green_2);
        bmRadar4_green_3 = BitmapFactory.decodeResource(getResources(), R.mipmap.radar4_green_3);
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
            int top = 150;

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


        }
    }

    private void canvasBitmap(Canvas canvas, boolean isShow, Bitmap bitmap) {
        if (isShow) {
            canvas.drawBitmap(bitmap, null, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), paint);
        }


    }
}
