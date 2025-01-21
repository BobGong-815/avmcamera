package com.autochips.avm.viewmode;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.autochips.avm.app.AvmApp;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;


public class MockViewModel extends BaseViewModel {
    public MockViewModel(@NonNull Application application) {
        super(application);
    }


    //全局异常捕获
    public BindingCommand exceptionClick = new BindingCommand(() -> {
        //伪造一个异常
        Integer.parseInt("goldze");
    });

    //打开avm
    public BindingCommand openAvmClick = new BindingCommand(() -> {
        //1:  SystemUI跳转打开AVM首页
        //2:  方控mode打开AVM首页
        //3:  语音唤醒打开AVM首页
        //10: 关闭AVM首页
        Intent mIntent = new Intent();
        mIntent.setClassName("com.autochips.avm", "com.autochips.avm.service.AvmService");
        mIntent.setAction("com.android.avm.action.AvmStart");
        mIntent.putExtra("avm_start", 1);
        AvmApp.getInstance().startService(mIntent);

    });
    //跳转设置
    public BindingCommand settingClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {

        }
    });
}
