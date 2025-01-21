package com.autochips.avm.data;


import android.content.Context;
import android.util.EventLog;

import com.gxatek.cockpit.datamining.client.DataMiningClientManager;
import com.gxatek.cockpit.datamining.client.reporter.CommonReporter;
import com.gxatek.cockpit.datamining.sdk.data.AppEvent;
import com.gxatek.cockpit.datamining.sdk.data.ReporterType;

import java.util.HashMap;

import me.goldze.mvvmhabit.utils.KLog;


public class DataManager{
    public static final String TAG = "DataManager";
    /**
     * 初始化数据埋点
     * @param context
     */
    public static void init(Context context){
        DataMiningClientManager.getInstance().init(context,false);
    }

    /**
     * AVM SDK初始化失败
     */
    public static void initSDKFailure(){
        final HashMap<String, String> hashMap = getParameter(DataConstant.Code.SDK_INIT_FAILURE,
                DataConstant.Resources.INIT_FAILURE_STRING,
                DataConstant.Resources.INIT_FAILURE_REASON,
                DataConstant.Resources.INIT_FAILURE_DETAIL);
        getCommonReporter().reportAppEvent(new AppEvent(DataConstant.Event.SDK_INIT_FAILURE, hashMap));
    }

    /**
     * 视频流打开成功
     */
    public static void streamOpenSuccess(String code){
        realReport(code,
                DataConstant.Event.STREAM_OPEN_SUCCESS,
                DataConstant.Resources.STREAM_OPEN_SUCCESS_STRING,
                DataConstant.Resources.STREAM_OPEN_SUCCESS_REASON,
                DataConstant.Resources.STREAM_OPEN_SUCCESS_DETAIL);
    }

    /**
     * 视频流打开失败
     */
    public static void streamOpenFailure(String code){
        realReport(code,
                DataConstant.Event.STREAM_OPEN_FAILURE,
                DataConstant.Resources.STREAM_OPEN_FAILURE_STRING,
                DataConstant.Resources.STREAM_OPEN_FAILURE_REASON,
                DataConstant.Resources.STREAM_OPEN_FAILURE_DETAIL);
    }

    /**
     * 视频流启动成功
     */
    public static void streamStartSuccess(String code){
        realReport(code,
                DataConstant.Event.STREAM_START_SUCCESS,
                DataConstant.Resources.STREAM_START_SUCCESS_STRING,
                DataConstant.Resources.STREAM_START_SUCCESS_REASON,
                DataConstant.Resources.STREAM_START_SUCCESS_DETAIL);
    }

    /**
     * 视频流启动失败
     */
    public static void streamStartFailure(String code){
        realReport(code,
                DataConstant.Event.STREAM_START_FAILURE,
                DataConstant.Resources.STREAM_START_FAILURE_STRING,
                DataConstant.Resources.STREAM_START_FAILURE_REASON,
                DataConstant.Resources.STREAM_START_FAILURE_DETAIL);
    }

    /**
     * 算法处理成功
     */
    public static void algorithmFailureRetry(){
        realReport(DataConstant.Code.ALGORITHM_FAILURE_RETRY,
                DataConstant.Event.ALGORITHM_FAILURE_RETRY,
                DataConstant.Resources.ALGORITHM_FAILURE_RETRY_STRING,
                DataConstant.Resources.ALGORITHM_FAILURE_RETRY_REASON,
                DataConstant.Resources.ALGORITHM_FAILURE_RETRY_DETAIL);
    }

    /**
     * 算法处理失败
     */
    public static void algorithmFailure(){
        realReport(DataConstant.Code.ALGORITHM_FAILURE,
                DataConstant.Event.ALGORITHM_FAILURE,
                DataConstant.Resources.ALGORITHM_FAILURE_STRING,
                DataConstant.Resources.ALGORITHM_FAILURE_REASON,
                DataConstant.Resources.ALGORITHM_FAILURE_DETAIL);
    }


    /**
     * 渲染失败后重试
     */
    public static void renderFailureRetry(){
        realReport(DataConstant.Code.RENDER_FAILURE_RETRY,
                DataConstant.Event.RENDER_FAILURE_RETRY,
                DataConstant.Resources.RENDER_FAILURE_RETRY_STRING,
                DataConstant.Resources.RENDER_FAILURE_RETRY_REASON,
                DataConstant.Resources.RENDER_FAILURE_RETRY_DETAIL);
    }
    /**
     * 渲染失败
     */
    public static void renderFailure(){
        realReport(DataConstant.Code.RENDER_FAILURE,
                DataConstant.Event.RENDER_FAILURE,
                DataConstant.Resources.RENDER_FAILURE_STRING,
                DataConstant.Resources.RENDER_FAILURE_REASON,
                DataConstant.Resources.RENDER_FAILURE_DETAIL);
    }


    /**
     * 视频流停止成功
     */
    public static void streamStopSuccess(String code){
        realReport(code,
                DataConstant.Event.STREAM_STOP_SUCCESS,
                DataConstant.Resources.STREAM_STOP_SUCCESS_STRING,
                DataConstant.Resources.STREAM_STOP_SUCCESS_REASON,
                DataConstant.Resources.STREAM_STOP_SUCCESS_DETAIL);
    }
    /**
     * 视频流停止失败
     */
    public static void streamStopFailure(String code){
        realReport(code,
                DataConstant.Event.STREAM_STOP_FAILURE,
                DataConstant.Resources.STREAM_STOP_FAILURE_STRING,
                DataConstant.Resources.STREAM_STOP_FAILURE_REASON,
                DataConstant.Resources.STREAM_STOP_FAILURE_DETAIL);
    }

    /**
     * 视频流关闭成功
     */
    public static void streamCloseSuccess(String code){
        realReport(code,
                DataConstant.Event.STREAM_CLOSE_SUCCESS,
                DataConstant.Resources.STREAM_CLOSE_SUCCESS_STRING,
                DataConstant.Resources.STREAM_CLOSE_SUCCESS_REASON,
                DataConstant.Resources.STREAM_CLOSE_SUCCESS_DETAIL);
    }
    /**
     * 视频流关闭失败
     */
    public static void streamCloseFailure(String code){
        realReport(code,
                DataConstant.Event.STREAM_CLOSE_FAILURE,
                DataConstant.Resources.STREAM_CLOSE_FAILURE_STRING,
                DataConstant.Resources.STREAM_CLOSE_FAILURE_REASON,
                DataConstant.Resources.STREAM_CLOSE_FAILURE_DETAIL);
    }

    /**
     * 保存数据成功
     */
    public static void dataSuccess(){
        realReport(DataConstant.Code.DATA_SUCCESS,
                DataConstant.Event.DATA_SUCCESS,
                DataConstant.Resources.DATA_SUCCESS_STRING,
                DataConstant.Resources.DATA_SUCCESS_REASON,
                DataConstant.Resources.DATA_SUCCESS_REASON);
    }

    /**
     * 保存数据失败
     */
    public static void dataFailure(){
        realReport(DataConstant.Code.DATA_FAILURE,
                DataConstant.Event.DATA_FAILURE,
                DataConstant.Resources.DATA_FAILURE_STRING,
                DataConstant.Resources.DATA_FAILURE_REASON,
                DataConstant.Resources.DATA_FAILURE_REASON);
    }
    /**
     * 标定成功
     */
    public static void calibrationSuccess(){
        realReport(DataConstant.Code.CALIBRATION_SUCCESS,
                DataConstant.Event.CALIBRATION_SUCCESS,
                DataConstant.Resources.CALIBRATION_SUCCESS_STRING,
                DataConstant.Resources.CALIBRATION_SUCCESS_REASON,
                DataConstant.Resources.CALIBRATION_SUCCESS_REASON);
    }

    /**
     * 标定失败
     */
    public static void calibrationFailure(){
        realReport(DataConstant.Code.CALIBRATION_FAILURE,
                DataConstant.Event.CALIBRATION_FAILURE,
                DataConstant.Resources.CALIBRATION_FAILURE_STRING,
                DataConstant.Resources.CALIBRATION_FAILURE_REASON,
                DataConstant.Resources.CALIBRATION_FAILURE_REASON);
    }

    public static void bAvmFault(int code, int param1, int param2){
        KLog.i("bAvmFault code:"+code);
        writeFault(code);
    }

    /**
     * 埋点
    * */
    public static void writeFault(int code){
        FaultInfo faultInfo = new FaultInfo();
        faultInfo.faultCode = code;
        int tag = EventLog.getTagCode("data_mining");
        faultInfo.timestamp = System.currentTimeMillis();
        KLog.i("faultInfo code:"+code+" time:"+faultInfo.timestamp);
        if(tag < 0){
            tag = FaultInfo.DEFAULT_TAG;
        }
        EventLog.writeEvent(tag,faultInfo.tag,faultInfo.sysId,faultInfo.appId,faultInfo.timestamp,faultInfo.faultCode,faultInfo.faultString,faultInfo.faultReason,faultInfo.faultDetail);
    }

    private static void realReport(String code, String event, String str, String reason, String detail){
        HashMap<String, String> hashMap = getParameter(code, str, reason, detail);
        getCommonReporter().reportAppEvent(new AppEvent(event, hashMap));
    }


    private static  HashMap getParameter(String code,String str,String reason,String detail){
        final HashMap<String, String> hashMap = new HashMap<>();
        KLog.d(TAG,"fault_code="+code);
        hashMap.put("tag",String.valueOf(3));
        hashMap.put("fault_time_stamp",String.valueOf(System.currentTimeMillis()));
        hashMap.put("fault_code",code);
        hashMap.put("fault_string",str);
        hashMap.put("fault_reason",reason);
        hashMap.put("fault_detail",detail);
        return hashMap;
    }

    private static  CommonReporter getCommonReporter(){
        return (CommonReporter) DataMiningClientManager.getInstance()
                .getReporter(ReporterType.COMMON);
    }
}
