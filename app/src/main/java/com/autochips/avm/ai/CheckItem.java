package com.autochips.avm.ai;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CABIN_DOOR_OPEN_STATUS;
import static com.avm.framwork.constant.CameraContracts.DOOR_HOOD;
import static com.avm.framwork.constant.CameraContracts.DOOR_REAR;
import static com.avm.framwork.constant.CameraContracts.ROW_1_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_1_RIGHT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_RIGHT;

import com.avm.framwork.manager.CanManager;

import java.util.ArrayList;
import java.util.List;

public class CheckItem {

    public String[] items;
    public byte valueType;
    public List<int[]> signalComb;

    CheckItem(String[] items, byte valueType) {
        this.items = items;
        this.valueType = valueType;
    }

    public void addSignal(int[] signals) {
        if (signalComb == null) signalComb = new ArrayList<>();
        signalComb.add(signals);
    }

    public boolean match(List<String> hearList) {
        boolean ret = true;

        for (String string : items) {
            if (!hearList.contains(string)) {
                ret = false;
                break;
            }
        }

        return ret;
    }

    public String query() {
        StringBuffer stringBuffer = new StringBuffer();

        for (int[] ints : signalComb) {
            if (ints.length == 1) {

            } else if (ints.length == 2) {
                if (valueType == 1) {
                    int value = CanManager.getInstance().getIntStatus(ints[0], ints[1]);
                    stringBuffer.append(signal2Text(ints[1]))
                            .append(" ")
                            .append(value)
                            .append("\n");
                }
            }
        }

        return stringBuffer.toString();
    }

    private String signal2Text(int signal) {
        switch (signal) {
            case DOOR_HOOD:
                return "车盖";
            case DOOR_REAR:
                return "车箱";
            case ROW_2_RIGHT:
                return "后排右侧车门";
            case ROW_2_LEFT:
                return "后排左侧车门";
            case ROW_1_RIGHT:
                return "前排右侧车门";
            case ROW_1_LEFT:
                return "前排左侧车门";
        }
        return "";
    }

}
