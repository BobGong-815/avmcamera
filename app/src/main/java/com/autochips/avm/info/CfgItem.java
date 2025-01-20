package com.autochips.avm.info;

import androidx.annotation.NonNull;

import com.autochips.avm.service.DataSts;
import com.autochips.avm.util.DataDefine;

import java.util.List;

public class CfgItem {
    public int fvState;
    public int[] gears;
    public int[] sensorSts;
    public int[] memories;
    public int[] switchs;
    public int[] events;
    public int[] actions;

    public CfgItem(int fvState, int[] gears, int[] activeSrcs, int[] memories, int[] switchs, int[] events, int[] actions) {
        this.fvState = fvState;
        this.gears = gears;
        this.sensorSts = activeSrcs;
        this.memories = memories;
        this.switchs = switchs;
        this.events = events;
        this.actions = actions;
    }

    public int[] matchAction(DataSts dataSts) {
        if (!fill(dataSts.fvSts, fvState)) return null;
        if (!fill(dataSts.gears, gears)) return null;
        if (!fill(dataSts.sensors, sensorSts)) return null;
        if (!andfill(dataSts.switchs, switchs)) return null;
        if (memories != null && !fill(dataSts.memory, memories)) return null;
        if (dataSts.events.size() == 0 || !fill(dataSts.events, events)) return null;
        return actions;
    }

    boolean fill(int[] sub, int[] owner) {//sub只要有一项在owner里面，则满足条件
        for (int s : sub) {
            for (int o : owner) {
                if (s == o) return true;
            }
        }
        return false;
    }

    boolean fill(int sub, int[] owner) {
        for (int o : owner) {
            if (sub == o) return true;
        }

        return false;
    }

    boolean fill(int[] sub, int owner) {
        for (int s : sub) {
            if (s == owner) return true;
        }

        return false;
    }

    // owner只要满足subs之一，即为真
    boolean fill(List<Integer> subs, int[] owner) {
        for (int s : subs) {
            for (int o : owner) {
                if (s == o) return true;
            }
        }

        return false;
    }

    // owner只要满足subs之一，即为真
    boolean andfill(List<Integer> switchs, int[] owner) {
        for (int o : owner) {
            if(owner[0] == DataDefine.SWITCH_NONE){
                return false;
            }else if(switchs.contains(o)) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CfgItem (")
                .append("fvState = " + DataDefine.id2String(fvState));
        for (int i = 0; i < gears.length; i++) {
            stringBuffer.append("\n, gears[" + i + "] = " + DataDefine.id2String(gears[i]));
        }
        for (int i = 0; i < sensorSts.length; i++) {
            stringBuffer.append("\n, sensorSts[" + i + "] = " + DataDefine.id2String(sensorSts[i]));
        }
        for (int i = 0; i < memories.length; i++) {
            stringBuffer.append("\n, memories[" + i + "] = " + DataDefine.id2String(memories[i]));
        }
        for (int i = 0; i < events.length; i++) {
            stringBuffer.append("\n, events[" + i + "] = " + DataDefine.id2String(events[i]));
        }
        for (int i = 0; i < actions.length; i++) {
            stringBuffer.append("\n, actions[" + i + "] = " + DataDefine.id2String(actions[i]));
        }
        stringBuffer.append(" )");
        return stringBuffer.toString();
    }

}
