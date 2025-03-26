package com.autochips.avm.info;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("autoStatus")
    private int autoStatus;
    @SerializedName("resultCode")
    private int resultCode;

    // Getters and Setters
    public int getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(int autoStatus) {
        this.autoStatus = autoStatus;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
