package com.autochips.avm.ai;

import java.util.ArrayList;
import java.util.List;

public class Signal {

    public static final byte OP_READ = 0;
    public static final byte OP_WRITE = 1;
    public static final byte OP_RW = 2;

    public static final byte TYPE_INT = 1;
    public static final byte TYPE_BYTE = 2;
    public static final byte TYPE_FLOAT = 3;
    public static final byte TYPE_BOOLEAN = 4;
    public static final byte TYPE_BYTE_ARRAY = 5;
    public static final byte TYPE_INT_ARRAY = 6;

    private int key; // 信号值
    private byte op; // 0 rw, 1 r, 2 w
    private Object value;
    private byte valueType; // 0 int, 1 byte, 2 float, 3 boolean, 4 byte[], 5 int[]
    private List<Signal> areas;
    private Object onValue;
    private Object offValue;

    Signal(int key, byte op, byte valueType, Object onValue, Object offValue) {
        this.key = key;
        this.op = op;
        this.valueType = valueType;
        this.onValue = onValue;
        this.offValue = offValue;
    }

    public void addArea(Signal signal) {
        if (areas == null) {
            areas = new ArrayList<>();
        }
        areas.add(signal);
    }

}
