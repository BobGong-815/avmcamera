package com.autochips.avm.data;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dec：处理视频流上报埋点，1分钟内不同的数据只能上报一次
* */
public class CodeThrottlerHelper {
    // 视屏流code
    public static final Set<Integer> VALID_CODES = Set.of(37, 38, 39, 40, 41, 42, 43, 44);
    // 存储每个code的最后上报时间戳,考虑同步问题
    private final ConcurrentHashMap<Integer, Long> lastReportMap = new ConcurrentHashMap<>(8);
    private static final long THROTTLE_INTERVAL = 60_000; // 1分钟
    private final ThrottleCallback callback;

    public CodeThrottlerHelper(ThrottleCallback callback) {
        this.callback = callback;
    }

    //记录提交
    public void submitCode(int code) {
        //获取当前事件
        final long currentTime = System.currentTimeMillis();
        //记录时间，判断1分分钟内之上报一次
        lastReportMap.compute(code, (key, lastTime) -> {
            if (lastTime == null || (currentTime - lastTime) >= THROTTLE_INTERVAL) {
                // 触发上报
                if (callback != null) {
                    callback.onReport(code);
                }
                return currentTime;
            }
            // 未到上报间隔
            return lastTime;
        });
    }

    // 回调接口
    public interface ThrottleCallback {
        void onReport(int code);
    }
}
