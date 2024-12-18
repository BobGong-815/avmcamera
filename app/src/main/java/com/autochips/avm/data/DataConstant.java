package com.autochips.avm.data;

import com.autochips.avm.R;

import me.goldze.mvvmhabit.utils.ResourcesUtils;

public class DataConstant {

    public static class  Event{
        public static final String SDK_INIT_FAILURE      = "app_init_failure";
        public static final String STREAM_OPEN_FAILURE   = "stream_open_failure";
        public static final String STREAM_START_FAILURE  = "stream_start_failure";
        public static final String ALGORITHM_FAILURE     = "algorithm_failure";
        public static final String RENDER_FAILURE        = "render_failure";
        public static final String STREAM_STOP_FAILURE   = "stream_stop_failure";
        public static final String STREAM_CLOSE_FAILURE  = "stream_close_failure";
        public static final String DATA_SUCCESS          = "data_success";
        public static final String DATA_FAILURE          = "data_failure";
        public static final String CALIBRATION_SUCCESS   = "calibration_success";
        public static final String CALIBRATION_FAILURE   = "calibration_failure";
        public static final String STREAM_OPEN_SUCCESS   = "stream_open_success";
        public static final String STREAM_START_SUCCESS  = "stream_start_success";
        public static final String ALGORITHM_SUCCESS     = "algorithm_success";
        public static final String STREAM_STOP_SUCCESS   = "stream_stop_success";
        public static final String STREAM_CLOSE_SUCCESS  = "stream_close_success";
        public static final String RENDER_FAILURE_RETRY  = "render_failure_retry";
        public static final String ALGORITHM_FAILURE_RETRY= "algorithm_failure_retry";
    }

    public static class Resources{
        public static final String INIT_FAILURE_STRING   = ResourcesUtils.getString(R.string.init_failure_string);
        public static final String INIT_FAILURE_REASON   = ResourcesUtils.getString(R.string.init_failure_reason);
        public static final String INIT_FAILURE_DETAIL   = ResourcesUtils.getString(R.string.init_failure_detail);
        public static final String UNKONWN               = ResourcesUtils.getString(R.string.unkonwn);

        public static final String STREAM_OPEN_FAILURE_STRING   = ResourcesUtils.getString(R.string.stream_open_failure_string);
        public static final String STREAM_OPEN_FAILURE_REASON   = ResourcesUtils.getString(R.string.stream_open_failure_reason);
        public static final String STREAM_OPEN_FAILURE_DETAIL   = ResourcesUtils.getString(R.string.stream_open_failure_detail);

        public static final String STREAM_START_FAILURE_STRING   = ResourcesUtils.getString(R.string.stream_start_failure_string);
        public static final String STREAM_START_FAILURE_REASON   = ResourcesUtils.getString(R.string.stream_start_failure_reason);
        public static final String STREAM_START_FAILURE_DETAIL   = ResourcesUtils.getString(R.string.stream_start_failure_detail);

        public static final String ALGORITHM_FAILURE_STRING      = ResourcesUtils.getString(R.string.algorithm_failure_string);
        public static final String ALGORITHM_FAILURE_REASON      = ResourcesUtils.getString(R.string.algorithm_failure_reason);
        public static final String ALGORITHM_FAILURE_DETAIL      = ResourcesUtils.getString(R.string.algorithm_failure_detail);

        public static final String RENDER_FAILURE_STRING         = ResourcesUtils.getString(R.string.render_failure_string);
        public static final String RENDER_FAILURE_REASON         = ResourcesUtils.getString(R.string.render_failure_reason);
        public static final String RENDER_FAILURE_DETAIL         = ResourcesUtils.getString(R.string.render_failure_detail);

        public static final String STREAM_STOP_FAILURE_STRING    = ResourcesUtils.getString(R.string.stream_stop_failure_string);
        public static final String STREAM_STOP_FAILURE_REASON    = ResourcesUtils.getString(R.string.stream_stop_failure_reason);
        public static final String STREAM_STOP_FAILURE_DETAIL    = ResourcesUtils.getString(R.string.stream_stop_failure_detail);

        public static final String STREAM_CLOSE_FAILURE_STRING   = ResourcesUtils.getString(R.string.stream_close_failure_string);
        public static final String STREAM_CLOSE_FAILURE_REASON   = ResourcesUtils.getString(R.string.stream_close_failure_reason);
        public static final String STREAM_CLOSE_FAILURE_DETAIL   = ResourcesUtils.getString(R.string.stream_close_failure_detail);

        public static final String DATA_SUCCESS_STRING           = ResourcesUtils.getString(R.string.data_success_string);
        public static final String DATA_SUCCESS_REASON           = ResourcesUtils.getString(R.string.data_success_reason);
        public static final String DATA_SUCCESS_DETAIL           = ResourcesUtils.getString(R.string.data_success_detail);

        public static final String DATA_FAILURE_STRING           = ResourcesUtils.getString(R.string.data_failure_string);
        public static final String DATA_FAILURE_REASON           = ResourcesUtils.getString(R.string.data_failure_reason);
        public static final String DATA_FAILURE_DETAIL           = ResourcesUtils.getString(R.string.data_failure_detail);

        public static final String CALIBRATION_FAILURE_STRING    = ResourcesUtils.getString(R.string.calibration_failure_string);
        public static final String CALIBRATION_FAILURE_REASON    = ResourcesUtils.getString(R.string.calibration_failure_reason);
        public static final String CALIBRATION_FAILURE_DETAIL    = ResourcesUtils.getString(R.string.calibration_failure_detail);

        public static final String CALIBRATION_SUCCESS_STRING    = ResourcesUtils.getString(R.string.calibration_success_string);
        public static final String CALIBRATION_SUCCESS_REASON    = ResourcesUtils.getString(R.string.calibration_success_reason);
        public static final String CALIBRATION_SUCCESS_DETAIL    = ResourcesUtils.getString(R.string.calibration_success_detail);

        public static final String STREAM_OPEN_SUCCESS_STRING    = ResourcesUtils.getString(R.string.stream_open_failure_string);
        public static final String STREAM_OPEN_SUCCESS_REASON    = ResourcesUtils.getString(R.string.stream_open_failure_reason);
        public static final String STREAM_OPEN_SUCCESS_DETAIL    = ResourcesUtils.getString(R.string.stream_open_failure_detail);

        public static final String STREAM_START_SUCCESS_STRING   = ResourcesUtils.getString(R.string.stream_start_success_string);
        public static final String STREAM_START_SUCCESS_REASON   = ResourcesUtils.getString(R.string.stream_start_success_reason);
        public static final String STREAM_START_SUCCESS_DETAIL   = ResourcesUtils.getString(R.string.stream_start_success_detail);

        public static final String ALGORITHM_FAILURE_RETRY_STRING= ResourcesUtils.getString(R.string.algorithm_failure_retry_string);
        public static final String ALGORITHM_FAILURE_RETRY_REASON= ResourcesUtils.getString(R.string.algorithm_failure_retry_reason);
        public static final String ALGORITHM_FAILURE_RETRY_DETAIL= ResourcesUtils.getString(R.string.algorithm_failure_retry_string);

        public static final String STREAM_STOP_SUCCESS_STRING    = ResourcesUtils.getString(R.string.stream_stop_success_string);
        public static final String STREAM_STOP_SUCCESS_REASON    = ResourcesUtils.getString(R.string.stream_stop_success_reason);
        public static final String STREAM_STOP_SUCCESS_DETAIL    = ResourcesUtils.getString(R.string.stream_stop_success_detail);

        public static final String STREAM_CLOSE_SUCCESS_STRING   = ResourcesUtils.getString(R.string.stream_close_success_string);
        public static final String STREAM_CLOSE_SUCCESS_REASON   = ResourcesUtils.getString(R.string.stream_close_success_reason);
        public static final String STREAM_CLOSE_SUCCESS_DETAIL   = ResourcesUtils.getString(R.string.stream_close_success_detail);

        public static final String RENDER_FAILURE_RETRY_STRING   = ResourcesUtils.getString(R.string.render_failure_retry_string);
        public static final String RENDER_FAILURE_RETRY_REASON   = ResourcesUtils.getString(R.string.render_failure_retry_reason);
        public static final String RENDER_FAILURE_RETRY_DETAIL   = ResourcesUtils.getString(R.string.render_failure_retry_detail);
    }



    public static class Code{
        /**
         * SDK初始化失败
         */
        public static final String SDK_INIT_FAILURE         = getFaultCode(0x01);

        /**
         * 视频流打开失败
         */
        public static final String STREAM_OPEN_FAILURE_OX04 = getFaultCode(0x04);
        public static final String STREAM_OPEN_FAILURE_OX05 = getFaultCode(0x05);
        public static final String STREAM_OPEN_FAILURE_OX06 = getFaultCode(0x06);
        public static final String STREAM_OPEN_FAILURE_OX07 = getFaultCode(0x07);

        /**
         * 视频流启动失败
         */
        public static final String STREAM_START_FAILURE_OX08 = getFaultCode(0x08);
        public static final String STREAM_START_FAILURE_OX09 = getFaultCode(0x09);
        public static final String STREAM_START_FAILURE_OX0A = getFaultCode(0x0A);
        public static final String STREAM_START_FAILURE_OX0B = getFaultCode(0x0B);

        /**
         * 视频流停止失败
         */
        public static final String STREAM_STOP_FAILURE_OX13 = getFaultCode(0x13);
        public static final String STREAM_STOP_FAILURE_OX14 = getFaultCode(0x14);
        public static final String STREAM_STOP_FAILURE_OX15 = getFaultCode(0x15);
        public static final String STREAM_STOP_FAILURE_OX16 = getFaultCode(0x16);

        /**
         * 视频流关闭失败
         */
        public static final String STREAM_CLOSE_FAILURE_OX17= getFaultCode(0x17);
        public static final String STREAM_CLOSE_FAILURE_OX18= getFaultCode(0x18);
        public static final String STREAM_CLOSE_FAILURE_OX19= getFaultCode(0x19);
        public static final String STREAM_CLOSE_FAILURE_OX1A= getFaultCode(0x1A);
        /**
         * 算法处理失败
         */
        public static final String ALGORITHM_FAILURE= getFaultCode(0x10);
        /**
         * 渲染失败
         */
        public static final String RENDER_FAILURE   = getFaultCode(0x11);

        /**
         * 数据保存成功
         */
        public static final String DATA_SUCCESS     = getFaultCode(0x31);
        /**
         * 数据保存失败
         */
        public static final String DATA_FAILURE     = getFaultCode(0x32);

        /**
         * 标定成功
         */
        public static final String CALIBRATION_SUCCESS= getFaultCode(0x31);
        /**
         * 标定失败
         */
        public static final String CALIBRATION_FAILURE= getFaultCode(0x32);

        /**
         * 视频流打开成功
         */
        public static final String STREAM_OPEN_SUCCESS_OX60 = getFaultCode(0x60);
        public static final String STREAM_OPEN_SUCCESS_OX61 = getFaultCode(0x61);
        public static final String STREAM_OPEN_SUCCESS_OX62 = getFaultCode(0x62);
        public static final String STREAM_OPEN_SUCCESS_OX63 = getFaultCode(0x63);

        /**
         * 视频流启动成功
         */
        public static final String STREAM_START_SUCCESS_OX64 = getFaultCode(0x64);
        public static final String STREAM_START_SUCCESS_OX65 = getFaultCode(0x65);
        public static final String STREAM_START_SUCCESS_OX66 = getFaultCode(0x66);
        public static final String STREAM_START_SUCCESS_OX67 = getFaultCode(0x67);

        /**
         * 视频流停止成功
         */
        public static final String STREAM_STOP_SUCCESS_OX6A = getFaultCode(0x6A);
        public static final String STREAM_STOP_SUCCESS_OX6B = getFaultCode(0x6B);
        public static final String STREAM_STOP_SUCCESS_OX6C = getFaultCode(0x6C);
        public static final String STREAM_STOP_SUCCESS_OX6D = getFaultCode(0x6D);

        /**
         * 视频流关闭成功
         */
        public static final String STREAM_CLOSE_FAILURE_OX6E= getFaultCode(0x6E);
        public static final String STREAM_CLOSE_FAILURE_OX6F= getFaultCode(0x6F);
        public static final String STREAM_CLOSE_FAILURE_OX70= getFaultCode(0x70);
        public static final String STREAM_CLOSE_FAILURE_OX71= getFaultCode(0x71);

        /**
         * 算法处理失败后retry
         */
        public static final String ALGORITHM_FAILURE_RETRY = getFaultCode(0x68);
        /**
         * 渲染失败后retry
         */
        public static final String RENDER_FAILURE_RETRY    = getFaultCode(0x69);

        /**
         * 进入App
         * */
        public static final int COMMING_APP = 0x01;
        /**
         * 初始化成功
         * */
        public static final int INIT_SUCCESS = 0x02;
        /**
         * 初始化失败
         * */
        public static final int INIT_FAIL = 0x03;

        /**
         * 触发事件_硬按键
         * */
        public static final int CLICK_IN_FK = 0x1C;
        /**
         * 触发事件_软按键
         * */
        public static final int CLICK_IN_SUI = 0x1D;
        /**
         * 触发事件_语音
         * */
        public static final int CLICK_IN_SPEECH = 0x1E;
        /**
         * 触发事件_雷达
         * */
        public static final int ACTIVI_RADAR = 0x1F;
        /**
         * 触发事件_转向灯
         * */
        public static final int ACTIVI_LIGHT = 0x20;
        /**
         * 触发事件_*按键自定义
         * */
        public static final int ACTIVI_AJ = 0x21;
        /**
         * 触发事件_R档
         * */
        public static final int ACTIVI_RGEAR = 0x22;
        /**
         * 触发事件_N档溜车
         * */
        public static final int ACTIVI_NGEAR = 0x23;
        /**
         * 触发事件_地图
         * */
        public static final int ACTIVI_MAP = 0x24;

        /**
         * 霸屏信号显示
         * */
        public static final int BP_SHOW = 0x2d;
        /**
         * 霸屏信号隐藏
         * */
        public static final int BP_HIDE = 0x2e;
        /**
         * 视图切换到2D
         * */
        public static final int ST_2D = 0x33;
        /**
         * 视图切换到3D
         * */
        public static final int ST_3D = 0x34;
        /**
         * 视图切换到广角
         * */
        public static final int ST_ANGLE = 0x35;

        /**
         * 进入下线标定
         * */
        public static final int BD_IN = 0x36;
        /**
         * 请求进入下线标定结果
         * */
        public static final int BD_IN_RESULE = 0x37;
        /**
         * 标定预检查
         * */
        public static final int BD_CHECK = 0x38;
        /**
         * 请求标定预检查结果
         * */
        public static final int BD_CHECK_RESULT = 0x39;
        /**
         * 开始标定
         * */
        public static final int BD_START = 0x3A;
        /**
         * 请求开始标定的结果
         * */
        public static final int BD_START_RESULT = 0x3B;
        /**
         * 读取标定失败的原因
         * */
        public static final int BD_FALUT = 0x3C;
        /**
         * 请求读取失败原因结果
         * */
        public static final int BD_FALUT_RESULT = 0x3D;
        /**
         * 进入下线标定检查
         * */
        public static final int BD_IN_CHECK = 0x3E;
        /**
         * 请求下线标定检查的结果
         * */
        public static final int BD_IN_CHECK_RESULT = 0x3F;
        /**
         *标定成功
         * */
        public static final int BD_SUCCESS = 0x40;
        /**
         *标定失败
         * */
        public static final int BD_FAIL = 0x41;

        /**
         *雷达声音开
         * */
        public static final int RADAR_SOUND_OPEN = 0x42;
        /**
         *雷达声音关
         * */
        public static final int RADAR_SOUND_CLOSE = 0x43;

        /**
         *后视镜下翻开
         * */
        public static final int REARM_OPEN = 0x44;
        /**
         *后视镜下翻关
         * */
        public static final int REARM_CLOSE = 0x45;
        /**
         *收到"进STR消息"
         * */
        public static final int GET_IN_STR = 0x59;
        /**
         *收到"退STR消息"
         * */
        public static final int GET_OUT_STR = 0x5A;
        /**
         * APK打开信号
         * */
        public static final int APK_OPEN = 0x94;

        /**
         * 算法处理失败
         * */
        public static final int SF_FAIL = 0x10;
        /**
         * 图像渲染失败
         * */
        public static final int TX_FAIL = 0x11;
        /**
         * 数据保存成功(只诊断标定数据)
         * */
        public static final int SJ_SAVE_SUCCESS = 0x31;
        /**
         * 数据保存失败(只诊断标定数据)
         * */
        public static final int SJ_SAVE_FAIL = 0x32;

        private static final String getFaultCode(int type){
            return "0x" + Integer.toHexString((0x03 << 24) + (0x12 << 16) + (type << 4) + 0x02);
        }
    }

}
