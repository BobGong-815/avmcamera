package com.avm.framwork.helper;

import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.WeakHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * Copyright (c) 2022 FOR_YOU GENERAL ELECTRONICS CO.,LTD. All Rights Reserved.
 *
 * @ClassName: ThreadPoolUtil 线程池的工具类 用于进行线程的管理，防止重复创建、杀死线程。
 * @Description: 多线程运行期间，如果系统不断的创建、杀死新线程，
 * 会产生过度消耗系统资源，以及过度切换线程的问题，甚至可能导致系统资源的崩溃。
 * 因此需要线程池，对线程进行管理。
 * <p>
 * 注：IO密集型（某大厂实践经验）
 * 核心线程数 = CPU核数 / （1-阻塞系数）
 * 或着
 * CPU密集型：核心线程数 = CPU核数 + 1
 * IO密集型：核心线程数 = CPU核数 * 2
 * <p>
 * *******************ThreadPoolExecutor*******************
 * 线程池五种状态：
 * RUNNING-运行状态，正常接收任务
 * SHUTDOWN-关闭状态，不接受新任务，可以处理阻塞队列任务
 * STOP-停止状态，不接受新任务，不可以处理阻塞队列任务，中断正在工作的线程
 * TIDYING-过度状态
 * TERMINATED-Game over
 * <p>
 * 方法：
 * shutdown():->SHUTDOWN-关闭状态，不接收新任务，可以处理阻塞队列任务
 * shutdownNow():->STOP-停止状态，不接受新任务，不可以处理阻塞队列任务，中断正在工作的线程
 * terminated():->TERMINATED-Game over
 * @Author: Y4924
 * @CreateDate: 2022/3/30 9:22
 */
public class ThreadPoolUtil {

    /**
     * 核心线程池的数量，同时能够执行的线程数量
     * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
     * corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1; //IO密集线程池配置
     */
    private static final int CORE_POOL_SIZE = 5;//CUP密集线程池配置Runtime.getRuntime()
    // .availableProcessors()
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    /**
     * 存活时间
     */
    private static final long KEEP_ALIVE_TIME = 3;
    /**
     * 存活时间单位
     */
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    /**
     * 阻塞队列大小
     */
    private static final int MAX_TASK_SIZE = Integer.MAX_VALUE;
    /**
     * 线程池对象
     */
    private final ThreadPoolExecutor mThreadPoolExecutor;
    /**
     * 主线程的消息队列
     */
    private final WeakHandler mWeakMainHandler = new WeakHandler(Looper.getMainLooper());
    /**
     * 子线程的消息队列，用于执行延时的子线程消息
     */
    private WeakHandler mWeakSubThreadHandler;

    private ThreadPoolUtil() {
        mThreadPoolExecutor = new ThreadPoolExecutor(
                //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                CORE_POOL_SIZE,
                // 然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                //缓冲队列，用于存放等待任务，Linked的先进先出
                new LinkedBlockingQueue<>(),
                new DefaultThreadFactory(Thread.NORM_PRIORITY, "thread-pool-"),
                new ThreadPoolExecutor.AbortPolicy() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                        super.rejectedExecution(r, e);

                    }
                }
        );

        //创建一个线程,线程名字：handler-thread
        HandlerThread subHandlerThread = new HandlerThread("subThread-ThreadPoolUtil");
        subHandlerThread.start();
        Looper looper = subHandlerThread.getLooper();
        if (looper != null) {
            mWeakSubThreadHandler = new WeakHandler(looper);//创建一个子线程handler用于延迟子线程消息
        } else {
            Log.e("ThreadPoolUtil","subHandlerThread.getLooper() = null");
        }
        //打印看看cpu多少核
        //        availableProcessors();
    }

    public static ThreadPoolUtil getInstance() {
        return SingletonHolder.sInstance;
    }

    //    /**
    //     * 获取可用线程数
    //     *
    //     * @return 线程数
    //     */
    //    private static int availableProcessors() {
    //        int cpus = Runtime.getRuntime().availableProcessors();
    //        if (Build.VERSION.SDK_INT < 17) {
    //            cpus = Math.max(getCoreCountPre17(), cpus);
    //        }
    //        KLog.w("cpus number = " + cpus);
    //        return cpus;
    //    }

    //    /**
    //     * Build.VERSION.SDK_INT < 17获取CPU核心数的方法
    //     *
    //     * @return 获取CPU核心数的方法
    //     */
    //    private static int getCoreCountPre17() {
    //        File[] cpus = null;
    //        StrictMode.ThreadPolicy originalPolicy = StrictMode.allowThreadDiskReads();
    //
    //        try {
    //            File cpuInfo = new File("/sys/devices/system/cpu/");
    //            final Pattern cpuNamePattern = Pattern.compile("cpu[0-9]+");
    //            cpus = cpuInfo.listFiles(new FilenameFilter() {
    //                public boolean accept(File file, String s) {
    //                    return cpuNamePattern.matcher(s).matches();
    //                }
    //            });
    //        } catch (Throwable var7) {
    //            KLog.e("[ getCoreCountPre17 ] - Failed to calculate accurate cpu count");
    //            var7.printStackTrace();
    //        } finally {
    //            StrictMode.setThreadPolicy(originalPolicy);
    //        }
    //
    //        return Math.max(1, cpus != null ? cpus.length : 0);
    //    }

    /**
     * 执行任务
     *
     * @param runnable 任务
     */
    public void execute(Runnable runnable) {
        if (runnable != null) {
            mThreadPoolExecutor.execute(runnable);
        }
    }

    /**
     * 移除任务
     *
     * @param runnable 任务
     */
    public void remove(Runnable runnable) {
        if (runnable != null) {
            mThreadPoolExecutor.remove(runnable);
        }
    }

    /**
     * 切换到主线程
     *
     * @param runnable Runnable
     */
    public void runOnUiThread(Runnable runnable) {
        runOnUiThread(runnable, 0);
    }

    /**
     * 延时切换到主线程
     *
     * @param runnable Runnable
     * @param delayed  时长 Millis
     */
    public void runOnUiThread(Runnable runnable, long delayed) {
        mWeakMainHandler.postDelayed(runnable, delayed);
    }

    /**
     * 延时切换到子线程
     *
     * @param runnable Runnable
     * @param delayed  时长 Millis
     */
    public void runOnSubThreadDelayed(Runnable runnable, long delayed) {
        if (mWeakSubThreadHandler == null) {
            Log.e("ThreadPoolUtil","mWeakSubThreadHandler == null , so return");
            return;
        }
        mWeakSubThreadHandler.postDelayed(runnable, delayed);
    }

    /**
     * 清空所有handler,防止内存泄漏
     */
    public void removeAllHandlerAndShutdownThreadPool() {
        mWeakSubThreadHandler.removeCallbacksAndMessages(null);
        mWeakMainHandler.removeCallbacksAndMessages(null);
        mThreadPoolExecutor.shutdownNow();
    }

    /**
     * 静态内部类的方式实现单例模式
     * 加载说明：
     * 由于 JVM 在加载外部类的过程中, 是不会加载静态内部类的,
     * 只有内部类的属性/方法被调用时才会被加载, 并初始化其静态属性。
     * 静态属性由于被 static 修饰，保证只被实例化一次，并且严格保证实例化顺序。
     * 第一次加载ThreadPoolUtil类时不会去初始化sInstance，只有第一次调用getInstance()，虚拟机加载SingletonHolder
     * 并初始化sInstance，这样不仅能确保线程安全，也能保证 ThreadPoolUtil 类的唯一性。
     */
    private static class SingletonHolder {
        private static final ThreadPoolUtil sInstance = new ThreadPoolUtil();
    }

    /**
     * 线程工程
     * <p>
     * 参数-
     * name：线程名称，可以重复，若没有指定会自动生成。
     * id：线程ID，一个正long值，创建线程时指定，终生不变，线程终结时ID可以复用。
     * priority：线程优先级，取值为1到10，线程优先级越高，执行的可能越大，若运行环境不支持优先级分10级，如只支持5级，那么设置5和设置6有可能是一样的。
     * state：线程状态，Thread.State枚举类型，有NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED 5种。
     * ThreadGroup：所属线程组，一个线程必然有所属线程组。
     * UncaughtExceptionHandler：未捕获异常时的处理器，默认没有，线程出现错误后会立即终止当前线程运行，并打印错误。
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        //线程池的计数
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        //线程的计数
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            Thread currentThread = Thread.currentThread();
            if (currentThread != null) {
                this.group = currentThread.getThreadGroup();
            } else {
                Log.e("ThreadPoolUtil","DefaultThreadFactory - currentThread = null");
            }
            this.namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            // 返回True该线程就是守护线程
            // 守护线程应该永远不去访问固有资源，如：数据库、文件等。因为它会在任何时候甚至在一个操作的中间发生中断。
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }
            thread.setPriority(threadPriority);//线程优先级
            return thread;
        }
    }

}
