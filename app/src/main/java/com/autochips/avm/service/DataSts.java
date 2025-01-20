package com.autochips.avm.service;

import android.content.Context;

import com.autochips.avm.util.DataDefine;
import com.autochips.avm.util.SystemProperties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSts {
    float currSpeed = -1;
    int overExitFlag; // 0 none, 1 left_card exit, 2 full_screen exit
    boolean sensorBlockPExit = false;
    boolean delayBlockExit = false;
    boolean radarPause;
    boolean radarAlive;
    boolean turnLampAlive;
    boolean otherOverSpeedSts = false;//其他超速状态
    long lastChangeTime; //上次变更时间
    long turnLampResetTime;
    boolean timing30sFlag; // 30s计时标志
    int lastSensorSrc = 0; // 0 none, 1 turn lamp, 2 radar

    public int[] fvSts; // 全景状态
    public int[] gears; // 档位
    public int[] sensors; // 雷达 转向灯
    public List<Integer> switchs;//开关状态
    public List<Integer> events; // 挂挡、雷达切换、转向灯切换
    List<Integer> extEvents;
    public int memory; // 记忆模式
    int[] actions; // 切换全景状态 切换视角
    private final Context mContext;

    public DataSts(Context context) {
        radarPause = false;
        radarAlive = false;
        turnLampAlive = false;
        timing30sFlag = false;
        overExitFlag = 0;

        fvSts = new int[2];
        fvSts[0] = DataDefine.FV_STATE_NON;
        fvSts[1] = DataDefine.INVALID;
        sensors = new int[2];
        sensors[0] = DataDefine.SENSOR_NONE; // turn lamp, radar, radar_turn_lamp
        sensors[1] = DataDefine.SENSOR_NONE; // l or r turn lamp
//            sensors[2] = DataDefine.SENSOR_NONE; // radar
//            sensors[3] = DataDefine.SENSOR_NONE; // radar & turn lamp
        gears = new int[3];
        gears[0] = DataDefine.GEAR_P;
        gears[1] = DataDefine.INVALID;
        gears[2] = DataDefine.INVALID;
        memory = readMemoryMode();
        switchs = new ArrayList<>();
        //30s 延迟开关是开 or 关
        switchs.add(SystemProperties.get("pExit").equals("1") ? DataDefine.SWITCH_DELAY_30S : -1 * DataDefine.SWITCH_DELAY_30S);
        events = new ArrayList<>();
        extEvents = new ArrayList<>();
        mContext = context;
    }

    // 记录传感器状态
    public void updateSensors() {
        if (events.contains(DataDefine.EVT_RADAR_TURN_LAMP_ACTIVE)) {
            sensors[0] = DataDefine.SENSOR_RADAR_TURN_LAMP;
            setTurnLampSensor(checkTurnLampEvent());
        } else if (events.contains(DataDefine.EVT_TURN_LAMP_ACTIVE) || events.contains(DataDefine.EVT_TURN_LAMP_RESET_ACTIVE)) {
            setTurnLampSensor(checkTurnLampEvent());
        } else if (events.contains(DataDefine.EVT_RADAR_ACTIVE)) {
            sensors[0] = DataDefine.SENSOR_RADAR;
            sensors[1] = DataDefine.SENSOR_NONE;
        } else if (events.contains(DataDefine.EVT_RADAR_RESET)) {
            if (sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                sensors[0] = DataDefine.SENSOR_TURN_LAMP;
            } else {
                //重置传感器状态
                resetSensors();
            }
        } else if (events.contains(DataDefine.EVT_TURN_LAMP_RESET)) {
            if (sensors[0] == DataDefine.SENSOR_RADAR_TURN_LAMP) {
                sensors[0] = DataDefine.SENSOR_RADAR;
                sensors[1] = DataDefine.SENSOR_NONE;
            } else {
                //重置传感器状态
                resetSensors();
            }
        }
    }

    //设置传感器转向灯状态
    private void setTurnLampSensor(int lampState) {
        sensors[0] = DataDefine.SENSOR_TURN_LAMP;
        if (lampState == DataDefine.EVT_TURN_LAMP_L_ACTIVE) {
            sensors[1] = DataDefine.SENSOR_TURN_LAMP_LEFT;
        } else if (lampState == DataDefine.EVT_TURN_LAMP_R_ACTIVE) {
            sensors[1] = DataDefine.SENSOR_TURN_LAMP_RIGHT;
        } else {
            sensors[1] = DataDefine.SENSOR_NONE; // 默认情况，如果没有明确指定左右转向灯状态，则设置为NONE
        }
    }

    // 辅助方法，用于检查转向灯事件并返回状态
    private int checkTurnLampEvent() {
        if (events.contains(DataDefine.EVT_TURN_LAMP_L_ACTIVE)) {
            return DataDefine.EVT_TURN_LAMP_L_ACTIVE;
        } else if (events.contains(DataDefine.EVT_TURN_LAMP_R_ACTIVE)) {
            return DataDefine.EVT_TURN_LAMP_R_ACTIVE;
        }
        return DataDefine.SENSOR_NONE; // 如果没有明确的转向灯事件，则返回NONE
    }

    // 辅助方法，用于重置传感器状态
    private void resetSensors() {
        sensors[0] = DataDefine.SENSOR_NONE;
        sensors[1] = DataDefine.SENSOR_NONE;
    }

    //更新档位状态
    private void setGearBasedOnSpeed(int gearBase, float currSpeed, int stopGear, int lowRateGear, int movingGear) {
        gears[0] = gearBase;
        if (currSpeed == 0.f) {
            gears[1] = stopGear;
            gears[2] = DataDefine.INVALID;
        } else if (currSpeed < AvmRuntime.SPEED_THRESHOLD) {
            gears[1] = lowRateGear;
            gears[2] = movingGear != DataDefine.INVALID ? movingGear : gearBase; // 如果movingGear不是INVALID，则使用它，否则使用gearBase
        } else {
            gears[1] = DataDefine.INVALID;
            gears[2] = movingGear != DataDefine.INVALID ? movingGear : DataDefine.INVALID; // 同上，但这里默认是INVALID
        }
    }

    //记录档位状态
    public void updateGear() {
        if (events.contains(DataDefine.EVT_SHIFT_D)) { // D
            setGearBasedOnSpeed(DataDefine.GEAR_D, currSpeed, DataDefine.GEAR_D_STOP, DataDefine.GEAR_D_LOW_RATE, DataDefine.GEAR_D_MOVING);
        } else if (events.contains(DataDefine.EVT_SHIFT_N)) {// N
            setGearBasedOnSpeed(DataDefine.GEAR_N, currSpeed, DataDefine.GEAR_N_STOP, DataDefine.GEAR_N_LOW_RATE, DataDefine.GEAR_N_MOVING);
        } else if (events.contains(DataDefine.EVT_SHIFT_R)) {// R
            setGearBasedOnSpeed(DataDefine.GEAR_R, currSpeed, DataDefine.GEAR_R_STOP, DataDefine.GEAR_R_LOW_RATE, DataDefine.INVALID); // 注意这里R档没有MOVING状态
        } else if (events.contains(DataDefine.EVT_SHIFT_P)) {// P
            gears[0] = DataDefine.GEAR_P;
            gears[1] = DataDefine.INVALID;
            gears[2] = DataDefine.INVALID;
        }
    }

    public void writeMemoryMode(int mem) {
        try {
            FileOutputStream fos = new FileOutputStream(mContext.getDataDir() + "/AvmRuntime.txt");
            fos.write(String.valueOf(mem).getBytes());
            fos.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private int readMemoryMode() {
        try {
            byte[] buffer = new byte[8];
            FileInputStream fis = new FileInputStream(mContext.getDataDir() + "/AvmRuntime.txt");
            int len = fis.read(buffer);
            fis.close();
            if (len > 0) {
                String str = new String(buffer, 0, len);
                return Integer.decode(str);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        }

        return DataDefine.MEM_MODE_2D;
    }

    //设置全景状态
    protected void setFvState(int actions) {
        if (actions == DataDefine.ACT_PASSIVE_DUAL_CARD) {
            fvSts[0] = DataDefine.FV_STATE_PASSIVE_DUAL_CARD;
            fvSts[1] = DataDefine.INVALID;
        } else if (actions == DataDefine.ACT_ACTIVE_DUAL_CARD) {
            fvSts[0] = DataDefine.FV_STATE_ACTIVE_DUAL_CARD;
            fvSts[1] = DataDefine.INVALID;
        } else if (actions == DataDefine.ACT_EXIT) {
            fvSts[0] = DataDefine.FV_STATE_NON;
            fvSts[1] = DataDefine.INVALID;
        } else if (actions == DataDefine.ACT_LEFT_CARD) {
            fvSts[0] = DataDefine.FV_STATE_LEFT_CARD;
            fvSts[1] = DataDefine.INVALID;
        }
    }

    //设置当前挡位事件
    public void setGearEvent(int gear) {
        if (gear == 1) { // D
            events.add(DataDefine.EVT_SHIFT_D);
        } else if (gear == 2) {// N
            events.add(DataDefine.EVT_SHIFT_N);
        } else if (gear == 3) {// R
            events.add(DataDefine.EVT_SHIFT_R);
        } else if (gear == 4) {// P
            events.add(DataDefine.EVT_ACTIVE_EXIT);
            events.add(DataDefine.EVT_SHIFT_P);
        }
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("DataSts (")
                .append("\n delayBlockExit = " + delayBlockExit)
                .append("\n, sensorBlockPExit = " + sensorBlockPExit)
                .append("\n, timing30sFlag = " + timing30sFlag)
                .append("\n, overExitFlag = " + overExitFlag)
                .append("\n, current Speed = " + currSpeed)
                .append("\n, radarAlive = " + radarAlive)
                .append("\n, turnLampAlive = " + turnLampAlive)
                .append("\n, otherOverSpeedSts = " + otherOverSpeedSts)
                .append("\n, lastChangeTime = " + lastChangeTime);
        for (int i = 0; i < fvSts.length; i++) {
            stringBuffer.append("\n, fvSts[" + i + "] = " + DataDefine.id2String(fvSts[i]));
        }
        for (int i = 0; i < gears.length; i++) {
            stringBuffer.append("\n, gears[" + i + "] = " + DataDefine.id2String(gears[i]));
        }
        for (int i = 0; i < sensors.length; i++) {
            stringBuffer.append("\n, sensors[" + i + "] = " + DataDefine.id2String(sensors[i]));
        }
        for (int i = 0; i < events.size(); i++) {
            stringBuffer.append("\n, events[" + i + "] = " + DataDefine.id2String(events.get(i)));
        }
        stringBuffer.append("\n, memory is " + DataDefine.id2String(memory));
        stringBuffer.append(" )");
        return stringBuffer.toString();
    }

    public void setOnGearNoActActionListener(List<AvmRuntime.ActionListener> actionListeners, boolean flag) {
        if (events.contains(DataDefine.EVT_SHIFT_R)
                || events.contains(DataDefine.EVT_SHIFT_D)
                || events.contains(DataDefine.EVT_SHIFT_P)
                || events.contains(DataDefine.EVT_SHIFT_N)) {
            for (AvmRuntime.ActionListener listener : actionListeners) {
                listener.onGearNoAct(gears[0], flag);
            }
        }
    }

    //设置速度激活 开关
    public void setSpeedSwicthOpen(boolean isOpen) {
        if(isOpen){
            if (switchs.contains(DataDefine.SWITCH_SPEED_EXIT)){
                switchs.remove(DataDefine.SWITCH_SPEED_EXIT);
            }else if(!switchs.contains(DataDefine.SWITCH_SPEED_ACTIVE)){
                switchs.add(DataDefine.SWITCH_SPEED_ACTIVE);
            }
        }else {
            if (switchs.contains(DataDefine.SWITCH_SPEED_ACTIVE)){
                switchs.remove(DataDefine.SWITCH_SPEED_ACTIVE);
            }else if(!switchs.contains(DataDefine.SWITCH_SPEED_EXIT)){
                switchs.add(DataDefine.SWITCH_SPEED_EXIT);
            }
        }
    }

    public boolean getSpeedSwicthOpen() {
       return switchs.contains(DataDefine.SWITCH_SPEED_ACTIVE);
    }

    //更新30秒延迟开关
    public void updateSwitchDelay() {
        if(switchs.contains(DataDefine.SWITCH_DELAY_30S)){
            switchs.remove(DataDefine.SWITCH_DELAY_30S);
        }else if (switchs.contains(-1 * DataDefine.SWITCH_DELAY_30S)){
            switchs.remove(-1 * DataDefine.SWITCH_DELAY_30S);
        }
        switchs.add(SystemProperties.get("pExit").equals("1") ? DataDefine.SWITCH_DELAY_30S : -1 * DataDefine.SWITCH_DELAY_30S);
    }
}
