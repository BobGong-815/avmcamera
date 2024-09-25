package com.autochips.avm.helper;

import com.autochips.avm.em.WorkStatusType;

/**
 * 页面类型 ，进入的页面是哪个
 */
public class ViewPageHelper {

    private static ViewPageHelper instance;
    private WorkStatusType hisType = WorkStatusType.NONE_IN;

    public static ViewPageHelper getInstance() {
        if (instance == null) instance = new ViewPageHelper();
        return instance;
    }

    /**
     * @param newType 进入新模式
     */
    public void workViewPage(WorkStatusType newType) {
        hisType = newType;
        switch (newType) {
            case NONE_IN:
                noneIn();
                break;
            case TURN_IN:
                turnIn();
                break;
            case TURN_OUT:
                turnOut();
                break;
            case REVERSE_IN:
                reverseIn();
                break;
            case REVERSE_OUT:
                reverseOut();
                break;
        }

    }

    /**
     * 手动进入
     */
    private void noneIn() {

    }


    /**
     * 主动退出
     */
    private void noneOut() {

    }


    /**
     * 转向进入
     */
    private void turnIn() {

    }

    /**
     * 转向退出
     */
    private void turnOut() {

    }


    /**
     * 倒车进入
     */
    private void reverseIn() {

    }

    /**
     * 倒车退出
     */
    private void reverseOut() {

    }
}
