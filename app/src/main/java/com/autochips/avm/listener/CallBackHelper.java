package com.autochips.avm.listener;

public class CallBackHelper {
    private static CallBackHelper instance;

    public static CallBackHelper getInstance() {
        if (instance == null)
            instance = new CallBackHelper();
        return instance;
    }

    private CallBackInterface callBackInterface;


    public void setCallBackInterface(CallBackInterface callBackInterface) {
        this.callBackInterface = callBackInterface;
    }

    public void setup(int msg, int param1, int param2) {
        if (callBackInterface != null)
            callBackInterface.setup(msg,param1,param2);
    }
}
