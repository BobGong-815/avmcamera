package com.autochips.avm.em;

// 工作模式
public enum WorkStatusType {
    NONE_IN("主动进入（手动/语音）"),
    TURN_IN("转向进入"),
    TURN_OUT("转向退出"),
    REVERSE_IN("倒车进入"),
    REVERSE_OUT("倒车退出");
    String statusName;

    WorkStatusType(String statusName) {
        this.statusName = statusName;
    }
}
