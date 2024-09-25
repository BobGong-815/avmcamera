package com.autochips.avm.em;
// 视角显示的类型
public enum ViewType {
    other("其他模式倒车视角"),
    ReverseIn("R档-进入倒车视角"),
    ReverseOut("R档-退出倒车视角"),
    gear_D("D档-车视角"),
    gear_N("N档-车视角"),
    gear_P("N档-车视角"),
    gear_Left("转向-左"),
    gear_Right("转向-右"),
    gear_turn_exit("转向-推出"),
    ;
    private  String mStatus ;// 当前视角状态
    private  int vehicleId ;

    ViewType(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
}
