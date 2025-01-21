adb  root
adb  remount
adb  shell rm -rf /system/app/GuangDaAVMCamera/GuangDaAVMCamera.apk
adb uninstall com.autochips.avm
#adb push  ./AVMCamera_V1.4.2_20240702.apk  /system/app/GuangDaAVMCamera/GuangDaAVMCamera.apk
#adb shell am start -n "com.autochips.avm/com.autochips.avm.ui.activity.MockActivity"

#pause

