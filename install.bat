adb  root
adb  remount
adb  shell rm -rf /system/app/GuangDaAVMCamera/GuangDaAVMCamera.apk
adb uninstall com.autochips.avm
adb push  ./AVMCamera.apk  /system/app/GuangDaAVMCamera/GuangDaAVMCamera.apk
adb shell ls /system/app/GuangDaAVMCamera/
READ adb shell am start -n "com.autochips.avm/com.autochips.avm.ui.activity.MockActivity"

pause

