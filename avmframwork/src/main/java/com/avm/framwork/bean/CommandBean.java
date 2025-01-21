package com.avm.framwork.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CommandBean implements Parcelable {
    private String id;
    private String sub;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sub);
    }

    protected CommandBean(Parcel in) {
        id = in.readString();
        sub = in.readString();
    }

    public static final Creator<CommandBean> CREATOR = new Creator<CommandBean>() {
        @Override
        public CommandBean createFromParcel(Parcel in) {
            return new CommandBean(in);
        }

        @Override
        public CommandBean[] newArray(int size) {
            return new CommandBean[size];
        }
    };

    @Override
    public String toString() {
        return "CommandBean{" +
                "id='" + id + '\'' +
                ", sub='" + sub + '\'' +
                '}';
    }
}
