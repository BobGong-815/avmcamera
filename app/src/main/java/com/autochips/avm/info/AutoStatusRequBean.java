package com.autochips.avm.info;

import com.google.gson.annotations.SerializedName;

public class AutoStatusRequBean {
    @SerializedName("data")
    private Data data;
    private String message;
    private boolean needResponse;
    private int needShowMap;
    private int protocolId;
    private String requestCode;
    private String requestType;
    private int statusCode;
    // Getters and Setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNeedResponse() {
        return needResponse;
    }

    public void setNeedResponse(boolean needResponse) {
        this.needResponse = needResponse;
    }

    public int getNeedShowMap() {
        return needShowMap;
    }

    public void setNeedShowMap(int needShowMap) {
        this.needShowMap = needShowMap;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "AutoStatusRequBean{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", needResponse=" + needResponse +
                ", needShowMap=" + needShowMap +
                ", protocolId=" + protocolId +
                ", requestCode='" + requestCode + '\'' +
                ", requestType='" + requestType + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}


