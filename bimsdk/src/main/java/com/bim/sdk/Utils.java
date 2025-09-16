package com.bim.sdk;

public class Utils {

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[3] = (byte) ((value >> 24) & 0xFF);
        return src;
    }

    public static int byteToInt(byte[] bytes, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = i * 8;
            value += (bytes[i+offset] & 0xFF) << shift;
        }
        return value;
    }

    public static String printByteArray(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte bb : bytes) {
            stringBuffer.append("0x").append(Integer.toHexString(bb)).append(" ");
        }
        return stringBuffer.toString();
    }

}
