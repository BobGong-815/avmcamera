package com.autochips.avm.listener;

public interface OnTabSelectListener {
    void onTabSelect(int position,boolean fromUser);
    void onTabSameSelect(int position,boolean fromUser);
}
