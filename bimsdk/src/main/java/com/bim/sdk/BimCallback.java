package com.bim.sdk;

public interface BimCallback {

    /**
     * code
     *     404 服务端断开
     *     200 服务端已连接
     *     0 服务端回复的消息
     * msg
     *     服务端回复的json格式的消息内容
     * */
    void onResponse(int code, String msg);
    void onResponse(byte type, byte[] data);
    void onResponse(CmdSet cmdset);

}
