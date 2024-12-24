package com.autochips.avm.ui.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;

import com.autochips.avm.R;
import com.autochips.avm.listener.OnTabInvalidSelectListener;
import com.autochips.avm.listener.OnTabSelectListener;
import com.autochips.avm.util.TestDefine;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.KLog;

@RequiresApi(api = Build.VERSION_CODES.R)
public class SegmentTabLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = SegmentTabLayout.class.getSimpleName();
    private final Context mContext;
    private int[] mTitles;
    private int[] mTabDrawables;
    private final LinearLayout mTabsContainer;
    private int mCurrentTab = -1;
    private int mLastTab;
    private int mTabCount;
    /**
     * 用于绘制显示器
     */
    private final Rect mIndicatorRect = new Rect();

    private float mTabWidth;

    private boolean mIndicatorAnimEnable = true;

    //是否是初始化值
    private boolean mIsInitialized = false;

    private boolean isUse = true;

    private boolean isNeedCallbackUpdateUI;
    /**
     * title
     */
    private float mTextsize;
    private int mTextSelectColor;
    private int mTextUnselectColor;
    private int mTextUnClickColor;
    private int mTextUnableColor;
    private int mHeight;
    private int[] resIds;
    private int isBold = 0;

    /**
     * anim
     */
    private final ValueAnimator mValueAnimator;

    private Runnable r;
    private final Handler handler = new Handler(Looper.myLooper());
    private ValueAnimator colorAnimation;
    private ValueAnimator colorAnimationLastTag;
    private int mThumbDrawable;
    private int mThumbDrawable2;
    private int mThumbDrawable3;
    private boolean enable = true;
    //是否是纯文本选择框
    private boolean mIsPlainText = true;
    //是否是竖向排列
    private boolean mIsVertical = false;


    public SegmentTabLayout(Context context) {
        this(context, null, 0);
    }

    public SegmentTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);


        obtainAttributes(context, attrs);

        if (mIsVertical) {
            // 设置为垂直方向
            mTabsContainer.setOrientation(LinearLayout.VERTICAL);
        } else {
            // 设置为水平方向
            mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        }

        //get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android",
                "layout_height");

        //create ViewPager
        if (height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "")) {
        } else if (height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
        } else {
            int[] systemAttrs = {android.R.attr.layout_height};
            TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
            mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            a.recycle();
        }
        mValueAnimator = ValueAnimator.ofObject(new PointEvaluator(), mLastP, mCurrentP);
        mValueAnimator.addUpdateListener(this);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SegmentTabLayout);
        mIndicatorAnimEnable =
                ta.getBoolean(R.styleable.SegmentTabLayout_tl_indicator_anim_enable, false);
        mTextsize = ta.getDimension(R.styleable.SegmentTabLayout_tl_textsize, 28);
        mTextSelectColor = ta.getResourceId(R.styleable.SegmentTabLayout_tl_textSelectColor,
                mTextSelectColor);
        mTextUnselectColor = ta.getResourceId(R.styleable.SegmentTabLayout_tl_textUnselectColor,
                mTextUnselectColor);
        mTextUnClickColor = ta.getResourceId(R.styleable.SegmentTabLayout_tl_textUnClickColor,
                mTextUnClickColor);
        mTextUnableColor = ta.getResourceId(R.styleable.SegmentTabLayout_tl_textUnableColor,
                mTextUnableColor);
        mTabWidth = ta.getDimension(R.styleable.SegmentTabLayout_tl_tab_width, dp2px(-1));

        mThumbDrawable = ta.getResourceId(R.styleable.SegmentTabLayout_tl_thumb_drawable, 0);
        mThumbDrawable2 = ta.getResourceId(R.styleable.SegmentTabLayout_tl_thumb_drawable_2, 0);
        mThumbDrawable3 = ta.getResourceId(R.styleable.SegmentTabLayout_tl_thumb_drawable_3, 0);
        mIsPlainText = ta.getBoolean(R.styleable.SegmentTabLayout_tl_tab_is_plain_text, true);
        mIsVertical = ta.getBoolean(R.styleable.SegmentTabLayout_tl_tab_is_vertical, false);
        ta.recycle();
    }

    public void setTabData(int[] titles) {
        KLog.d("setTabData Titles length  =  " + titles.length);
        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be NULL or EMPTY !");
        }
        this.mTitles = titles;
        notifyDataSetChanged();
    }

    /**
     * 设置tab的drawable
     *
     * @param tabDrawables drawable资源 id 数组
     */
    public void setTabDrawable(@NonNull int[] tabDrawables) {

        if (tabDrawables.length == 0) {
            KLog.d("Titles can not be NULL or EMPTY ");
            throw new IllegalStateException("Titles can not be NULL or EMPTY !");

        }
        KLog.d("setTabData Titles length  = setTabDrawable " + Arrays.toString(tabDrawables));
        this.mTabDrawables = tabDrawables;
        notifyDataSetChanged();
    }


    public LinearLayout getTabsContainer() {
        return mTabsContainer;
    }

    /**
     * 更新数据
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mTitles.length;
        View tabView;
        KLog.d("isPlainText----- " + mIsPlainText);
        for (int i = 0; i < mTabCount; i++) {
            if (mIsPlainText) {
                tabView = View.inflate(mContext, R.layout.layout_tab_segment, null);
            } else {
                tabView = View.inflate(mContext, R.layout.layout_image_tab_segment, null);
            }

            tabView.setTag(i);
            tabView.setAlpha(1.0f);
            tabView.setEnabled(true);
            addTab(i, tabView);

        }

        updateTabStyles();
    }

    /**
     * 创建并添加tab
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("CheckResult")
    private void addTab(final int position, View tabView) {
        View tab_title = tabView.findViewById(R.id.tab_title);

        if (tab_title instanceof TextView) {
            TextView tv_tab_title = (TextView) tab_title;
            addTabDrawable(position, tabView.findViewById(R.id.tab_drawable));
            tv_tab_title.setText(mTitles[position]);
            tv_tab_title.setIncludeFontPadding(false);
            tv_tab_title.setLineSpacing(0.5f,0.7f);
            CharSequence sequence = "2D影像::parkimage_home_2DImage";
            if (mTitles[position] == R.string.camera_2d) {
                sequence = "2D影像::parkimage_home_2DImage";
            } else if (mTitles[position] == R.string.camera_3d) {
                sequence = "3D影像::parkimage_home_3DImage";
            } else if (mTitles[position] == R.string.camera_wide_angle) {
                sequence = "广角视图::parkimage_home_wideAngleView";
            } else if (mTitles[position] == R.string.setting_at_once) {
                sequence = "立刻::parkimage_set_now";
            } else if (mTitles[position] == R.string.setting_30_seconds) {
                sequence = "30秒后::parkimage_set_30s";
            }
            tv_tab_title.setStateDescription(sequence);
        } else if (tab_title instanceof ImageView) {
            ImageView iv_tab_title = (ImageView) tab_title;
            iv_tab_title.setImageDrawable(mContext.getDrawable(mTitles[position]));
        }
        RxView.clicks(tabView)
                .throttleFirst(800L, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        onClick(tabView);
                    }
                });
        //if (enable && i == mCurrentTab) {
        tabView.post(new Runnable() {
            @Override
            public void run() {
                // 获取当前的布局参数
                ViewGroup.LayoutParams params = tabView.getLayoutParams();
                if (isBold == 5 && params instanceof MarginLayoutParams) {

                    // 应用新的边距参数
                    tabView.setLayoutParams(getTabMarginParams(position,params));
                    return;
                  }
                    // 修改布局参数中的边距值
                    if (params instanceof MarginLayoutParams) {
                        MarginLayoutParams marginParams = (MarginLayoutParams) params;
                        marginParams.topMargin = 3; // 上边距
                        marginParams.bottomMargin = 3; // 下边距
                        marginParams.leftMargin = 3; // 左边距
                        marginParams.rightMargin = 3; // 右边距

                    // 应用新的边距参数
                    tabView.setLayoutParams(marginParams);
                }



            }
        });

        //  }

        tab_title.setAccessibilityDelegate(new AccessibilityDelegate() {
            @Override
            public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onInitializeAccessibilityEvent(host, event);
                event.setChecked(isChecked());
                event.setEventType(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCheckable(true);
                info.setChecked(isChecked());
            }
        });

        /** 每一个Tab的布局参数 */
        LinearLayout.LayoutParams lp_tab = getTabLayoutParams();
        tabView.setTag(position);
        /*if(isBold==5){
            mTabsContainer.post(new Runnable() {
                @Override
                public void run() {
                    mTabsContainer.setBackgroundResource(R.drawable.tab_segment_selector_thumb);
                }
            });

        }*/
        mTabsContainer.addView(tabView, position, lp_tab);
    }

    @Override
    public  void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getId() == R.id.segment_tab) {
            View parentView = (View) getParent();
            if (parentView.getId() == R.id.layoutSettingId) {
//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction(TestDefine.VIEW_MODE);
//                mContext.registerReceiver(broadcastReceiver, intentFilter);
            }
        }
    }


    private  MarginLayoutParams  getTabMarginParams(int position,ViewGroup.LayoutParams params){
        MarginLayoutParams marginParams = (MarginLayoutParams) params;
        marginParams.topMargin = 0; // 上边距
        marginParams.bottomMargin = 0; // 下边距
        if (position == 0){
            marginParams.leftMargin = 0; // 左边距
            marginParams.rightMargin = 0; // 右边距
        }else if(position== 3){
            marginParams.leftMargin = -32; // 左边距
            marginParams.rightMargin = 0; // 右边距
        }else if(position == 1){
            marginParams.leftMargin = -36; // 左边距
            marginParams.rightMargin = -2; // 右边距
        }else {
            marginParams.leftMargin = -34; // 左边距
            marginParams.rightMargin = -4; // 右边距
        }
        return marginParams;
    }


    private boolean isChecked() {
        return true;
    }

    private void addTabDrawable(int position, ImageView tabDrawable) {
        if (mTabDrawables == null || mTabDrawables.length <= position) {
            return;
        }
        tabDrawable.setVisibility(VISIBLE);
        int mTabDrawable = mTabDrawables[position];
        KLog.d("drawable = " + mTabDrawable);
    }

    private LinearLayout.LayoutParams getTabLayoutParams() {
        if (mIsVertical) {
            return new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mTabWidth);
        } else {
            return new LinearLayout.LayoutParams((int) mTabWidth, LayoutParams.MATCH_PARENT);
        }
    }

    private boolean mIsOnclick = false;
    private void onClick(View view) {
        KLog.d(TAG, "CLICK enable :" + enable + "isUse:" + isUse);
        mIsOnclick = true;
        int position = (Integer) view.getTag();

        if (!enable) {
            if (mInvalidSelectListener != null) {
                mInvalidSelectListener.onTabInvalidSelect(position);
            }
            KLog.d(TAG, "NO CLICK ");
            return;
        }

        if (!isUse) {
            KLog.d(TAG, "NO CLICK ");
            return;
        }

        if (mCurrentTab != position) {

            if (!isNeedCallbackUpdateUI) {
                setCurrentTab(position);
            }
            if (mListener != null) {
                mListener.onTabSelect(position, true);
            }
        } else {
            KLog.d(TAG, "CLICK enable :--onTabSameSelect--" + position);
            if (mListener != null) {
                mListener.onTabSameSelect(position, true);
            }
        }
    }

    public void setTabSelect(){
        mIsOnclick = true;
    }

    private void updateTabStyles() {
//        KLog.d(TAG,
//                "updateTabStyles:" + mTextSelectColor + "----isPlainText-----" + mIsPlainText +
//                        "-------mTabCount--------" + mTabCount);
        for (int i = 0; i < mTabCount; i++) {
          int position = i;
            View tabView = mTabsContainer.getChildAt(i);
            View tab_title = tabView.findViewById(R.id.tab_title);
            if (tab_title instanceof TextView) {
                TextView tv_tab_title = (TextView) tab_title;
                if (enable) {
                    tv_tab_title.setTextColor(mContext.getResources().getColor(i == mCurrentTab ?
                            mTextSelectColor : mTextUnselectColor));
                    if (i == mCurrentTab && isBold == 1) {
                        tv_tab_title.post(new Runnable() {
                            @Override
                            public void run() {
                                Typeface typeface = tv_tab_title.getTypeface();
                                int style = Typeface.BOLD;
                                tv_tab_title.setTypeface(null, style);
                            }
                        });
                        tabView.setBackground(mContext.getDrawable(enable && i == mCurrentTab ?
                                mThumbDrawable2 : mThumbDrawable));
                        //tv_tab_title.setTypeface(null, Typeface.BOLD);
                    } else {
                        tv_tab_title.post(new Runnable() {
                            @Override
                            public void run() {
                                Typeface typeface = tv_tab_title.getTypeface();
                                int style = Typeface.NORMAL; // 或者 Typeface.NORMAL
                                tv_tab_title.setTypeface(null, style);
                            }
                        });
                        if(i==0||i==mTabCount){
                            tabView.setBackground(mContext.getDrawable(enable && i == mCurrentTab ?
                                    mThumbDrawable2 : mThumbDrawable));
                        }else{
                            tabView.setBackground(mContext.getDrawable(enable && i == mCurrentTab ?
                                    mThumbDrawable2 : mThumbDrawable));
                        }
                        //tv_tab_title.setTypeface(null, Typeface.NORMAL);
                    }

                } else {
                    tv_tab_title.setTextColor(mContext.getResources().getColor(mTextUnableColor));
                    tv_tab_title.setTypeface(null, Typeface.NORMAL);
                }
                tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextsize);
                updateTabDrawable(i, tabView.findViewById(R.id.tab_drawable));
            } else if (tab_title instanceof ImageView) {
                ImageView iv_tab_title = (ImageView) tab_title;
                iv_tab_title.setImageDrawable(mContext.getDrawable(i == mCurrentTab ? resIds[i] :
                        mTitles[i]));
            }

          /*  tabView.setBackground(mContext.getDrawable(enable && i == mCurrentTab ?
                    mThumbDrawable2 : mThumbDrawable));*/

            if(isBold==5){
                int finalI = i;
                tabView.post(new Runnable() {
                    @Override
                    public void run() {
                        // 获取当前的布局参数
                        ViewGroup.LayoutParams params = tabView.getLayoutParams();

                        // 修改布局参数中的边距值
                        if (params instanceof MarginLayoutParams) {
                            // 应用新的边距参数
                            tabView.setLayoutParams(getTabMarginParams(position,params));
                        }

                    }
                });
            }

        }
    }


    private  void  setTagMarginParams(int position){

    }

    private void updateTabDrawable(int i, ImageView imageView) {
        //        KLog.d(TAG, "updateTabDrawable:" + i);
        if (mTabDrawables == null || mTabDrawables.length <= i) {
            return;
        }
        Drawable drawable = mContext.getDrawable(mTabDrawables[i]);

        if (i == mCurrentTab) {
            Drawable wrap = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrap, mContext.getResources().getColor(mTextSelectColor));
            drawable = wrap;
        } else {
            Drawable wrap = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrap, mContext.getResources().getColor(mTextUnselectColor));
            drawable = wrap;
        }
        imageView.setImageDrawable(drawable);
    }


    private void calcOffset(final int CurrentTab) {

        final View lastTabView = mTabsContainer.getChildAt(this.mLastTab);
        KLog.d(TAG, "lastTabView: " + lastTabView);
        if (lastTabView != null) {
            mLastP.left = lastTabView.getLeft();
            mLastP.right = lastTabView.getRight();
        } else {
            mLastP.left = 0;
            mLastP.right = 0;
        }

        if (this.mCurrentTab == -1) {
            mCurrentP.left = mLastP.left;
            mCurrentP.right = mLastP.right;
        } else {
            final View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
            if (currentTabView != null) {
                mCurrentP.left = currentTabView.getLeft();
                mCurrentP.right = currentTabView.getRight();
            } else {
                mCurrentP.left = 0;
                mCurrentP.right = 0;
            }

        }

        if (mLastP.left == mCurrentP.left && mLastP.right == mCurrentP.right || this.mCurrentTab == -1) {
            //            Log.d("AAA", "calcOffset if");
            invalidate();
        } else {
            //            Log.d("AAA", "calcOffset else");
            mValueAnimator.setObjectValues(mLastP, mCurrentP);
            //减速运动
            mValueAnimator.setInterpolator(new DecelerateInterpolator());
            mValueAnimator.setDuration(250);

            //            Log.d("AAA", "mTextUnselectColor = " + mTextUnselectColor +
            //            "mTextSelectColor = " + mTextSelectColor);
            if (colorAnimation == null) {
                colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                        mContext.getResources().getColor(mTextUnselectColor),
                        mContext.getResources().getColor(mTextSelectColor));
                colorAnimation.setInterpolator(new DecelerateInterpolator());
            }

            if (colorAnimationLastTag == null) {
                colorAnimationLastTag = ValueAnimator.ofObject(new ArgbEvaluator(),
                        mContext.getResources().getColor(mTextSelectColor),
                        mContext.getResources().getColor(mTextUnselectColor));
            }

            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                    KLog.d(TAG, "onAnimationStart: " + mLastTab);

                    if (r == null) {
                        r = new Runnable() {
                            @Override
                            public void run() {
                                if (colorAnimationLastTag != null) {
                                    colorAnimationLastTag.removeAllListeners();
                                    colorAnimationLastTag.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animator) {
                                            //tab_titleLastTag.setTextColor((Integer) animator
                                            // .getAnimatedValue());
                                        }
                                    });
                                    colorAnimationLastTag.setDuration(162);
                                    colorAnimationLastTag.cancel();
                                    colorAnimationLastTag.start();
                                }

                                if (colorAnimation != null) {
                                    colorAnimation.removeAllListeners();
                                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animator) {
                                            // tab_titles.setTextColor((Integer) animator
                                            // .getAnimatedValue());
                                        }
                                    });
                                    colorAnimation.setDuration(350);
                                    colorAnimation.cancel();
                                    colorAnimation.start();
                                }

                            }
                        };
                    }
                    handler.removeCallbacks(r);
                    handler.post(r);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    KLog.d(TAG, "onAnimationEnd: " + CurrentTab);
                    updateTabStyles();
                    mValueAnimator.removeListener(this);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            mValueAnimator.start();
        }
    }

    private void calcOffsetNoAnimator() {

        final View lastTabView = mTabsContainer.getChildAt(this.mLastTab);
        //        Log.d(TAG, "calcOffsetNoAnimator: lastTabView   " + lastTabView);

        //        Log.d(TAG, "calcOffsetNoAnimator: this.mLastTab  " + this.mLastTab);
        if (lastTabView != null) {
            mLastP.left = lastTabView.getLeft();
            mLastP.right = lastTabView.getRight();
        } else {
            mLastP.left = 0;
            mLastP.right = 0;
        }

        if (this.mCurrentTab == -1) {
            mCurrentP.left = mLastP.left;
            mCurrentP.right = mLastP.right;
        } else {
            View currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
            //            Log.d("calcOffsetNoAnimator", "currentTabView---" + currentTabView);
            if (currentTabView != null) {
                mCurrentP.left = currentTabView.getLeft();
                mCurrentP.right = currentTabView.getRight();
                //                Log.d("calcOffsetNoAnimator", "currentTabView.getLeft()---" +
                //                currentTabView.getLeft());
                //                Log.d("calcOffsetNoAnimator", "currentTabView.getRight()---" +
                //                currentTabView.getRight());
            } else {
                mCurrentP.left = 0;
                mCurrentP.right = 0;
            }

        }
        //计算选中项坐标
        calcIndicatorRect();
        //更新文案样式
        updateTabStyles();

        invalidate();
    }

    private void calcIndicatorRect() {
        View currentTabView;
        float left = 0;
        float right = 0;
        if (mCurrentTab == -1) {
            currentTabView = mTabsContainer.getChildAt(this.mLastTab);
        } else {
            currentTabView = mTabsContainer.getChildAt(this.mCurrentTab);
        }

        //        Log.d("calcOffsetNoAnimator", "currentTabView---" + currentTabView);
        if (currentTabView != null) {
            left = currentTabView.getLeft();
            right = currentTabView.getRight();
            //            Log.d("calcOffsetNoAnimator", "currentTabView.left---" + left +
            //            "--currentTabView.right--" + right);
        }

        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;

        //        Log.d("calcOffsetNoAnimator", "mIndicatorRect.left---" + mIndicatorRect.left +
        //        "--mIndicatorRect.right--" + mIndicatorRect.right);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        IndicatorPoint p = (IndicatorPoint) animation.getAnimatedValue();
        mIndicatorRect.left = (int) p.left;
        mIndicatorRect.right = (int) p.right;
        invalidate();
    }

    private boolean mIsFirstDraw = true;

    @SuppressLint("WrongConstant")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //        Log.d(TAG, "onDraw: =" + mTabCount);
        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        //draw indicator line
        if (mIndicatorAnimEnable) {
            //            Log.d(TAG, "mIndicatorAnimEnable--" + 1);
            if (mIsFirstDraw) {
                mIsFirstDraw = false;
                //                Log.d(TAG, "mIndicatorAnimEnable--" + 3);
                calcIndicatorRect();
            }
        } else {
            //            Log.d(TAG, "mIndicatorAnimEnable--" + 2);
            calcIndicatorRect();
        }

        if(mIsOnclick) {
            mIsOnclick = false;
            updateTabStyles();
        }
    }

    //setter and getter
    private void setCurrentTab(int currentTab) {
        KLog.d(TAG, "setCurrentTab: " + currentTab);
        KLog.d(TAG, "lastTab: " + mLastTab);
        mIsOnclick = true;
        if (mCurrentTab >= 0) {
            mLastTab = this.mCurrentTab;
        }
        this.mCurrentTab = currentTab;

        if (mIsInitialized) {
            mIsInitialized = false;
            calcOffsetNoAnimator();
        } else {
            if (mIndicatorAnimEnable) {
                calcOffset(currentTab);
            } else {
                invalidate();
            }
        }
    }

    public void setSelectTab(int tab) {
        if (tab >= -1 && tab < mTitles.length) {
            setCurrentTab(tab);
            if (mListener != null) {
                mListener.onTabSelect(tab, false);
            }
        }
    }

    public void setTypeBackground(boolean enable) {

        float alpha = enable ? 1f : 0.15f;

        for (int i = 0; i < mTabCount; i++) {
            View tabView = mTabsContainer.getChildAt(i);
            View tab_title = tabView.findViewById(R.id.tab_title);
            tab_title.setAlpha(alpha);
        }

    }

    public void setSelectTabFromUser(int tab) {
        if (tab >= 0 && tab < mTitles.length) {
            setCurrentTab(tab);
            if (mListener != null) {
                mListener.onTabSelect(tab, true);
            }
        }
    }

    public void setEnable(boolean enable) {
        KLog.d(TAG, "enable:" + enable);
        this.enable = enable;
        postInvalidate();
    }

    public void setIsPlainText(boolean isPlainText) {
        KLog.d(TAG, "isPlainText:" + isPlainText);
        this.mIsPlainText = isPlainText;
    }

    public void setResIds(int[] resIds) {
        KLog.d(TAG, "resId:" + resIds);
        this.resIds = resIds;
    }

    public void setTabWidth(float tabWidth) {
        this.mTabWidth = tabWidth;//dp2px(tabWidth);
        updateTabStyles();
    }

    public void setTextSelectColor(@ColorRes int textSelectColor, int isBold) {
        this.mTextSelectColor = textSelectColor;
        this.isBold = isBold;
        colorAnimationLastTag = null;
        KLog.d("AAA", "setTextSelectColor mTextSelectColor = " + mTextSelectColor);
        updateTabStyles();
    }

    public void setTextUnselectColor(@ColorRes int textUnselectColor) {
        this.mTextUnselectColor = textUnselectColor;
        colorAnimation = null;
        KLog.d("AAA", "setTextUnselectColor mTextUnselectColor = " + mTextUnselectColor);
        updateTabStyles();
    }

    public void setThumbDrawable(@DrawableRes int drawable) {
        mThumbDrawable = drawable;
        invalidate();
    }

    public void setThumbDrawable2(@DrawableRes int drawable) {
        mThumbDrawable2 = drawable;
        invalidate();
    }
    public void setThumbDrawable3(@DrawableRes int drawable) {
        mThumbDrawable3 = drawable;
        invalidate();
    }

    public void setInitializeType(boolean mIsInitialized) {
        this.mIsInitialized = mIsInitialized;
    }

    public int getTabCount() {
        return mTabCount;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }


    public int getTextSelectColor() {
        return mTextSelectColor;
    }

    public int getTextUnselectColor() {
        return mTextUnselectColor;
    }

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    private OnTabInvalidSelectListener mInvalidSelectListener;

    public void setOnTabInvalidSelectListener(OnTabInvalidSelectListener mInvalidSelectListener) {
        this.mInvalidSelectListener = mInvalidSelectListener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    class IndicatorPoint {
        public float left;
        public float right;
    }

    private IndicatorPoint mCurrentP = new IndicatorPoint();
    private IndicatorPoint mLastP = new IndicatorPoint();

    class PointEvaluator implements TypeEvaluator<IndicatorPoint> {
        @Override
        public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue,
                                       IndicatorPoint endValue) {
            float left = startValue.left + fraction * (endValue.left - startValue.left);
            float right = startValue.right + fraction * (endValue.right - startValue.right);
            IndicatorPoint point = new IndicatorPoint();
            point.left = left;
            point.right = right;
            return point;
        }
    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public void isNeedCallbackUpdateUI(boolean need) {
        isNeedCallbackUpdateUI = need;
    }

    public void setUse(boolean use) {
        isUse = use;
    }
}
