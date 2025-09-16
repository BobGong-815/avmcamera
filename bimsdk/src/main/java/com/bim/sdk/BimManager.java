package com.bim.sdk;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BimManager implements BimCallback , LlmListener {

    private static BimManager self_;
    private BimCallback bimCallback;
    private Logger LOGGER;
    private List<String> messages;
    private List<RespMsg> parseResults;

    private BimManager() {
        LOGGER = new Logger("BIM", "BimManager");
        messages = new ArrayList<>();
        parseResults = new ArrayList<>();
        CommuModule.self().init();
    }

    public static BimManager self() {
        if (self_ == null) {
            self_ = new BimManager();
        }

        return self_;
    }

    public void connect(String ipAddress, BimCallback bimCallback) {
        this.bimCallback = bimCallback;
        CommuModule.self().connect(ipAddress, this);
    }

    public void init() {
        new Thread(() -> {
            while (true) {
                synchronized (parseResults) {
                    try {
                        parseResults.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                            LOGGER.d("parseResults is " + parseResults);
                }

                RespMsg respMsg;
                while (true) {
                    synchronized (parseResults) {
                        if (parseResults.size() > 0) {
                            respMsg = parseResults.remove(0);
                        } else {
                            break;
                        }
                    }
                    if (respMsg != null && respMsg.type == 2) {
                        String string = new String(respMsg.data, 0, respMsg.data.length-1);
//                                LOGGER.d("000 string is " + string);
                        Gson gson = new Gson();
                        CmdSet cmdSet = gson.fromJson(string, CmdSet.class);
                        for (Cmd cmd : cmdSet.result) {
//                                LOGGER.d("current cmd : " + cmd);
                            /*if (cmd.key.equals("query") && cmd.param0.equals("weather")) {
                                String result = TianXing.weatherQuery(cmd.param1, Integer.decode(cmd.param2));
                                LOGGER.d("weather query result : " + result);
//                                    if (result != null) {
//                                        sendCmd2Server(cmd.no, "feedback", "success", result, null, null);
//                                    } else {
//                                        sendCmd2Server(cmd.no, "feedback", "fail", null, null, null);
//                                    }
                            } else */
                            if (cmd.key.equals("query") && cmd.param0.equals("llm")) {

                            } else if (cmd.key.equals("unknown") && cmd.param0.equals("goods")) {

                            } else if (cmd.key.equals("unknown") && cmd.param0.equals("input")) {

                            } else if (cmd.key.equals("state") && cmd.param0.equals("work")) {

                            } else {

                            }
                        }
                        if (bimCallback != null) {
                            bimCallback.onResponse(cmdSet);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void onResponse(int code, String msg) {
        if (code == 0) {
            synchronized (messages) {
                messages.add(msg);
                messages.notify();
            }
        } else {
            if (bimCallback != null) {
                bimCallback.onResponse(code, msg);
            }
        }
    }

    @Override
    public void onResponse(byte type, byte[] data) {
        synchronized (parseResults) {
            parseResults.add(new RespMsg(type, data));
            parseResults.notify();
        }
        if (bimCallback != null) {
            bimCallback.onResponse(type, data);
        }
    }

    @Override
    public void onResponse(CmdSet cmdset) {

    }

    public void inputText(String text) {
        sendBin2Server((byte) 1, text.getBytes());
    }

    public void sendBin2Server(byte type, byte[] data) {
        CommuModule.self().sendData2Server(type, data);
    }

    public void sendCmd2Server(int no, String key, String param0, String param1, String param2, String param3) {
        Cmd cmd = new Cmd(no, key, param0, param1, param2, param3);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(cmd);
        CommuModule.self().sendStr2Server((byte) 2, jsonStr);
    }

    public void wakeUp() {
        CommuModule.self().sendStr2Server((byte) 1, "你好小趣");
    }

    public void toSleep() {
        CommuModule.self().sendStr2Server((byte) 1,"睡觉");
    }

    public void sendStr2Server(String text) {
        CommuModule.self().sendStr2Server((byte) 1, text);
    }

//    public void sendBin2Server(byte[] array) {
//        byte[] byteArray = new byte[BYTES_PREFIX.length+array.length];
//        System.arraycopy(BYTES_PREFIX, 0, byteArray, 0, BYTES_PREFIX.length);
//        System.arraycopy(array, 0, byteArray, BYTES_PREFIX.length, array.length);
//        CommuModule.self().sendBin2Server(byteArray);
//    }

    @Override
    public void onLlmContent(int no, String text) {
        LOGGER.d("llm recv : " + text);
        sendCmd2Server(no, "feedback", "success", text, null, null);
    }

    @Override
    public void onLlmError(String text) {

    }

    class RespMsg {
        byte type; // 1 string , 2 json
        byte[] data;

        RespMsg(byte type, byte[] data) {
            this.type = type;
            this.data = data;
        }
    }

}
