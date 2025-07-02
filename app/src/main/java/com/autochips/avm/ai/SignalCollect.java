package com.autochips.avm.ai;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CABIN_DOOR_OPEN_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.REARVIEW_MIRROR_ADJUSTMENT;


import static com.autochips.avm.ai.Signal.OP_READ;
import static com.autochips.avm.ai.Signal.TYPE_BOOLEAN;
import static com.autochips.avm.ai.Signal.TYPE_BYTE;
import static com.autochips.avm.ai.Signal.TYPE_FLOAT;
import static com.autochips.avm.ai.Signal.TYPE_BOOLEAN;
import static com.autochips.avm.ai.Signal.TYPE_BYTE_ARRAY;
import static com.autochips.avm.ai.Signal.TYPE_INT;
import static com.autochips.avm.ai.Signal.TYPE_INT_ARRAY;

import static com.autochips.avm.ai.Signal.OP_RW;
import static com.avm.framwork.constant.CameraContracts.DOOR_HOOD;
import static com.avm.framwork.constant.CameraContracts.DOOR_REAR;
import static com.avm.framwork.constant.CameraContracts.ROW_1_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_1_RIGHT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_RIGHT;

import java.util.ArrayList;
import java.util.List;

public class SignalCollect {

    private List<Signal> signals;

    SignalCollect() {
        signals = new ArrayList<>();
        Signal signal;
        Signal signal_sub;
        signal = new Signal(REARVIEW_MIRROR_ADJUSTMENT, OP_RW, TYPE_INT_ARRAY, new Integer[]{1, 9}, new Integer[]{1, 10});
        signals.add(signal);

        signal = new Signal(CABIN_DOOR_OPEN_STATUS, OP_READ, TYPE_INT, null, null);
        signal_sub = new Signal(ROW_1_LEFT, OP_READ, TYPE_INT, 1, 0);
        signal.addArea(signal_sub);
        signal_sub = new Signal(ROW_1_RIGHT, OP_READ, TYPE_INT, 1, 0);
        signal.addArea(signal_sub);
        signal_sub = new Signal(ROW_2_LEFT, OP_READ, TYPE_INT, 1, 0);
        signal.addArea(signal_sub);
        signal_sub = new Signal(ROW_2_RIGHT, OP_READ, TYPE_INT, 1, 0);
        signal.addArea(signal_sub);
        signal_sub = new Signal(DOOR_REAR, OP_READ, TYPE_INT, 1, 0);
        signal.addArea(signal_sub);
        signal_sub = new Signal(DOOR_HOOD, OP_READ, TYPE_INT, 1, 0);
        signal.addArea(signal_sub);
        signals.add(signal);
    }

    public String work(String input) {
        StringBuffer stringBuffer = new StringBuffer();

        return stringBuffer.toString();
    }

}
