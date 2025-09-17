package com.autochips.avm.service;

import android.os.Handler;

import com.bim.sdk.BimCallback;
import com.bim.sdk.CmdSet;

public class BimInterface implements BimCallback {

    BimInterface(Handler handler) {

    }

    @Override
    public void onResponse(int code, String msg) {

    }

    @Override
    public void onResponse(byte type, byte[] data) {

    }

    @Override
    public void onResponse(CmdSet cmdset) {

    }
}
