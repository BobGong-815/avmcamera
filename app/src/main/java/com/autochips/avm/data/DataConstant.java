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


        private static final String getFaultCode(int type){
            return "0x" + Integer.toHexString((0x03 << 24) + (0x12 << 16) + (type << 4) + 0x02);
        }
    }

}
