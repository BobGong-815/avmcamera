package com.autochips.avm.util;

public class Utils {

    public static boolean contain(int[] ints, int value) {
        for (int ii : ints) {
            if (ii == value) {
                return true;
            }
        }

        return false;
    }

}
