package com.autochips.avm.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.android.bvavm.bvavmJNI;
import com.autochips.avm.app.AvmApp;
import com.autochips.avm.helper.BvAvmJNIHelper;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import me.goldze.mvvmhabit.utils.KLog;

import static android.opengl.GLES10.GL_TRUE;

public class TGLSurfaceView extends GLSurfaceView {

    public static final String TAG = "Ray OpenGL ES";
    public float roateTri;
    public float roateQuad;

    public TGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public TGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
//        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//        getHolder().setFormat(PixelFormat.TRANSLUCENT);
//        setEGLContextClientVersion(3);
//        setEGLConfigChooser(new MyConfigChooser());
        setRenderer(new OpenGLRender(1));
    }

    public class OpenGLRender implements Renderer{
        int one = 0x10000;//系统将取坐标值的高16位
        // public IntBuffer triangleBuffer = IntBuffer.wrap(new int[]{
// 0,one,0,
// -one,-one,0,
// one,-one,0});
        int [] triangleArray ={
                0,one,0,
                -one,-one,0,
                one,-one,0};

        int [] colorArray = {
                one,0,0,one,
                0,one,0,one,
                0,0,one,one};

        int [] quaterArray = {
                one,one,0,
                -one,one,0,
                one,-one,0,
                -one,-one,0};

        private int INDEX;

        OpenGLRender(int index) {this.INDEX = index; }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//            bvavmJNI.avmInit();
//            bvavmJNI.bwCreateCamera("com/autochips/avm/ui/view/CameraView","onBVAVMMessage");

// 初始化工作、设置顶点坐标的值等
// 告诉系统对透视进行修正，会使透视图看起来好看点
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
// 启用阴影平滑
//gl.glShadeModel(GL10.GL_SMOOTH);
// 黑色背景
            gl.glClearColor(0, 0, 0, 0);//红，绿，蓝，apaha
// 启动阴影平滑
            gl.glShadeModel(GL10.GL_SMOOTH);
// 设置深度缓存
            gl.glClearDepthf(1.0f);
// 启用深度测试
            gl.glEnable(GL10.GL_DEPTH_TEST);
// 所作深度测试的类型
            gl.glDepthFunc(GL10.GL_LEQUAL);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {// 3D图形的画布，画布的宽度，画布的高度
// 当窗口大小发生改变时系统将调用 onSurfaceChanged方法，可以在该方法中设置 OpenGL场景大小
// 设置视口大小、投影类型、矩阵模式、切换投影类型等

            Log.i(TAG, "onSurfaceChanged width:"+width+" height:"+height);

// 试图的长宽比例必须和投影坐标x,y轴的刻度比例相同，否则绘制的刻度比例将变形
            float ratio = (float) width / height;
////// 视口的宽高度和画布的宽高度相同-设置OpenGL场景的大小
            gl.glViewport(0, 0, width, height);
// 改为投影矩阵模式
            gl.glMatrixMode(GL10.GL_PROJECTION);//投影矩阵负责为场景增加透视度
// 设为单位矩阵-重置投影矩阵
            gl.glLoadIdentity();//此方法相当于我们手机的重置功能，它将所选择的矩阵状态恢复成原始状态
//x,y轴的刻度之比为ratio，投影坐标没有实际的单位，只是将视口的大小映射成相应的坐标刻度
// 最后两个参数是near和far的值，实际Z轴是指向地面方向，因此在这里Z轴的可视刻度范围是：小于-1，不小于-10
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);//后两个参数-场景中所能绘制深度的起点和终点
// 设为模型视图(观察)矩阵-以便可以使用相应的方法设置模型(被观察的物体)和试图(观察点的位置，默认在坐标原点)
            gl.glMatrixMode(GL10.GL_MODELVIEW);//指明任何新的变换将会影响
// 重置模型观察矩阵
            gl.glLoadIdentity();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
// 开关的操作、绘制3D图形、设置顶点坐标等
            Log.i(TAG, "onDrawFrame");
////////////////////必须清除屏幕、否则上次绘制的图形不会自动消失///////////////
            roateTri += 0.5f;
            roateQuad -= 0.5f;
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glLoadIdentity();

// 设置背景颜色
            gl.glClearColor(0.5f, 0.5f, 0.5f, 0.2f);
// 设置深度缓存-默认
//gl.glClearDepthf(1.0f);

////////////////////////////////三角形绘制-移动、旋转、着色，开启关闭开关///////////////////////////////////////
// 将当前位置向Z轴负方向移6个单位
// 移动当前中心点，左移1.5单位，并移入屏幕10.0，y不变
// 注意：屏幕内移动的单位数必须小于前面我们通过
// glFrustumf方法所设置的最远距离，否则显示不出来。
// 腰围OpenGL设置一个顶点数组，故需要告诉OpenGL要设置
// 顶点这个功能。
            gl.glTranslatef(-1.5f, 0.0f, -4.0f);

// 设置某无题沿着指定的轴旋转
// 参数1：旋转的角度
// 后三个参数共同决定旋转的方向
// 注意：要在画图前，使用旋转
            gl.glRotatef(roateTri, 0.0f, 1.0f, 0.0f);

// 开启顶点方式绘制-允许使用顶点方式绘制图形-使用顶点坐标绘制图形时必须使用glEnableClientState方法打开允许使用顶点坐标状态
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
// 装载用于绘制图形的顶点坐标
// 设置三角形
// 参数1：描述顶点的尺寸，本例中使用X,Y,Z坐标系，所以是3
// 参数2：描述顶点的类型，本例中数据是固定的，所以使用了GL_FIXED表示固定顶点
// 参数3：描述步长
// 参数4：顶点缓存，即我们创建的顶点数组
            gl.glVertexPointer(3, GL10.GL_FIXED, 0, bufferUtil(triangleArray));

// 开启颜色渲染功能
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
// 设置颜色,平滑着色
            gl.glColorPointer(4, GL10.GL_FIXED, 0, bufferUtil(colorArray));

//开启OpenGL ES光源，没有颜色
            gl.glEnable(GL10.GL_LIGHTING);
//gl.glDisable(GL10.GL_LIGHTING);

// 开始绘制
// 参数1：绘制模式，GL_TRIANGLES：表示绘制三角形
// 参数2：开始位置 - id
// 参数3：要绘制的顶点计数
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

// 将当前矩阵设为单位矩阵-如果在第二次移位时想从坐标系原点开始，就需要将当前矩阵恢复到单位矩阵
// 重置当前的模型观察矩阵
            gl.glLoadIdentity();

// 禁止使用顶点方式绘制图形
//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
// 关闭颜色渲染
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

/////////////////////////////////////矩形绘制-同上//////////////////////////////////
// 右移1.5单位，并移入屏幕6.0
            gl.glTranslatef(1.5f, 0.0f, -6.0f);
// 与三角形的旋转方式相反
            if (INDEX == 1)
                gl.glRotatef(roateQuad, 0.0f, 0.0f, 1.0f);//顺时针方向旋转
            else
                gl.glRotatef(roateQuad, 0.0f, 0.0f, -1.0f);//顺时针方向旋转
// 开启颜色渲染功能
            gl.glEnableClientState(GL10.GL_COLOR_BUFFER_BIT);
// 设置颜色，单调着色 （r,g,b,a）
            gl.glColor4f(0.5f, 0.2f, 0.8f, 0.7f);
// 设置和绘制正方形
            gl.glVertexPointer(3, GL10.GL_FIXED, 0, bufferUtil(quaterArray));
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

//            bvavmJNI.avmRender2(bvavmJNI.BW_FRONT_3D);

// 关闭颜色渲染
            gl.glDisableClientState(GL10.GL_COLOR_BUFFER_BIT);
// 关闭顶点方式
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glFinish();

        }

        /*
         * OpenGL 是一个非常底层的画图接口，它所使用的缓冲区存储结构是和我们的 java 程序中不相同的。
         * Java 是大端字节序(BigEdian)，而 OpenGL 所需要的数据是小端字节序(LittleEdian)。
         * 所以，我们在将 Java 的缓冲区转化为 OpenGL 可用的缓冲区时需要作一些工作。建立buff的方法如下
         * */
        public Buffer bufferUtil(int []arr){ // float型也可用该方法指定
            IntBuffer mBuffer ;

// 先初始化buffer,数组的长度*4,因为一个int占4个字节
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
// 数组排列用nativeOrder
            byteBuffer.order(ByteOrder.nativeOrder());

            mBuffer = byteBuffer.asIntBuffer();
            mBuffer.put(arr);
            mBuffer.position(0);

            return mBuffer;
        }
    }

    public class MyConfigChooser implements GLSurfaceView.EGLConfigChooser {

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] attribs = new int[]{
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 0,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_SAMPLE_BUFFERS, GL_TRUE,//打开多采样抗锯齿
                    EGL10.EGL_SAMPLES, 4,  // 采样数
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attribs, configs, 1, configCounts);
            KLog.d("chooseConfig() display is " + display);
            if (configCounts[0] == 0) {
                // Failed! Error handling.
                return null;
            } else {
                return configs[0];
            }
        }
    }

}
