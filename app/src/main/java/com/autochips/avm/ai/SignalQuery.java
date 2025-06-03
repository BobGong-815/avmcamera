package com.autochips.avm.ai;

import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CABIN_DOOR_OPEN_STATUS;
import static android.hardware.automotive.vehicle.V2_0.SyncoreVehicleProperty.CLUSTER_LCK_DRIVERDOORAJARST;

import static com.avm.framwork.constant.CameraContracts.DOOR_HOOD;
import static com.avm.framwork.constant.CameraContracts.DOOR_REAR;
import static com.avm.framwork.constant.CameraContracts.ROW_1_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_1_RIGHT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_LEFT;
import static com.avm.framwork.constant.CameraContracts.ROW_2_RIGHT;

import android.util.Log;

import com.bumptech.glide.load.Option;

import java.util.ArrayList;
import java.util.List;

public class SignalQuery {

    public List<CheckItem> itemList;

    public String[] constantStrings = new String[] {
            Constant.ID_REARVIEW_MIRROR_FOLD,
            Constant.ID_REARVIEW_MIRROR_FLIP_DOWN,
            Constant.ID_STEERING_ANGLE,
            Constant.ID_RADAR_DISTANCE,
            Constant.ID_TURN_LIGHT,
            Constant.ID_CAR_DOOR,
            Constant.ID_GEAR,
            Constant.ID_LIGHT,
            Constant.ID_WHEEL_SPEED,
            Constant.ID_CAR_SPEED,
            Constant.ID_QUERY,
            Constant.ID_SETUP,
            Constant.ID_SIGNAL,
    };

    public SignalQuery() {
        itemList = new ArrayList<>();
        itemList.add(new CheckItem(
                new String[]{Constant.ID_CAR_DOOR, Constant.ID_SIGNAL}, (byte) 1));
        itemList.get(itemList.size()-1).addSignal(new int[] {
                CABIN_DOOR_OPEN_STATUS,
                ROW_1_LEFT
        });
        itemList.get(itemList.size()-1).addSignal(new int[] {
                CABIN_DOOR_OPEN_STATUS,
                ROW_1_RIGHT
        });
        itemList.get(itemList.size()-1).addSignal(new int[] {
                CABIN_DOOR_OPEN_STATUS,
                ROW_2_LEFT
        });
        itemList.get(itemList.size()-1).addSignal(new int[] {
                CABIN_DOOR_OPEN_STATUS,
                ROW_2_RIGHT
        });
        itemList.get(itemList.size()-1).addSignal(new int[] {
                CABIN_DOOR_OPEN_STATUS,
                DOOR_REAR
        });
        itemList.get(itemList.size()-1).addSignal(new int[] {
                CABIN_DOOR_OPEN_STATUS,
                DOOR_HOOD
        });
//        itemList.add(new CheckItem(
//                new String[]{Constant.ID_CAR_DOOR},
//                new int[] {CLUSTER_LCK_DRIVERDOORAJARST}));
    }

    public String work(String input) {
        StringBuffer stringBuffer = new StringBuffer();

        List<String> hearStrings = new ArrayList<>();
        for (String string : constantStrings) {
            if (input.contains(string)) {
                hearStrings.add(string);
                int idx = input.indexOf(string);
                if (idx == 0) {
                    input = input.substring(string.length());
                } else if (idx+string.length() == input.length()) {
                    input = input.substring(0, idx+1);
                } else {
                    String str0 = input.substring(0, idx+1);
                    String str1 = input.substring(idx+string.length());
                    input = str0+str1;
                }
            }
        }

        Log.d("AVM_DEBUG", "hearStrings is " + hearStrings);
        if (hearStrings.size() == 0) {
            return "";
        }

        for (CheckItem checkItem : itemList) {
            if (checkItem.match(hearStrings)) {
                stringBuffer.append(checkItem.query());
            }
        }

        return stringBuffer.toString();
    }



}
