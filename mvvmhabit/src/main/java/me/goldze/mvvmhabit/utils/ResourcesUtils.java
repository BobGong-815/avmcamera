package me.goldze.mvvmhabit.utils;

public class ResourcesUtils {
    //获取本地文字
    public static String getString(int textid) {
        return Utils.getContext().getResources().getString(textid);
    }

}