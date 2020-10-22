package com.newland.hid;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbManager {
    static {
        System.loadLibrary("UsbManager");
    }

    /**
     * 读操作 读至返回结果
     * @param out 输出结果
     * @return
     * 成功：>0 结果长度
     * 失败: 0 停止  -1 过程中usb拔出
     * */
    private static native int jniRead(byte[] out);

    /**
     * 关闭串口
     * */
    private static native int jniRelease();


    /**
     * 写操作
     * @param in 写入数据
     * @return 写入长度
     * */
    private static native int jniWrite(byte[] in);


    /**
     * 打开串口
     * @param path 打开路径
     * */
    private static native int jniOpen(byte[] path);

    /**
     * 停止读操作
     * */
    private static native void jniStopRead();

    /**
     * 串口是否已打开
     * @return 节点句柄
     * */
    private static native int jniIsOpen();

    private static native int jniConfig(int rate);


    public static int setConfig(int rate){
        return jniConfig(rate);
    }

    private static String path;
    public static String getPath(){
        return path;
    }
    /**
     * 打开串口
     * @return 打开成功
     * */
    public static boolean open(String devPath) {
        path = devPath;/*"/dev/" + getCommName();*/
        return jniOpen(path.getBytes())>0;
    }

    /**
     * 读串口
     * 直接返回结果
     * @return 读结果
     * */
    public static int read(byte[] out){
        return jniRead(out);
    }


    /**
     * 写串口
     * @param text 输入字符
     * @return 写入成功
     * */
    public static int write(String text)throws UnsupportedEncodingException {
        return jniWrite(text.getBytes("GBK"));
    }

    /**
     * @return 串口是否打开
     * */
    public static boolean isOpen(){
        return jniIsOpen()==1;
    }

    /**
     * 关串口
     * */
    public static boolean close(){
        return jniRelease()==0;
    }

    /**
     * 停止读
     * */
    public static void stopRead(){
        jniStopRead();
    }

    private static final String USB_SERIAL = "/sys/bus/usb-serial/devices";

    /**
     * 获取串口名
     * */
    private static String getCommName() {
//        File usbSerial = new File(USB_SERIAL);
//        File[] devices = usbSerial.listFiles();
//        String devicename = "unknown";
//        if (devices != null && devices.length != 0) {
//            devicename = devices[0].getName();
//            return devicename;
//        } else {
//            return "ttyS9";
//        }
        return "ttyS9";
    }



    /**
     * 获取时间戳
     */
    public static String getDateFormat() {
        long time = System.currentTimeMillis();
        int mill = (int) (time % 1000);
        int totalSeconds = (int) (time / 1000);
        totalSeconds += 8 * 60 * 60;
        totalSeconds = totalSeconds % (24 * 60 * 60);
        //int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        int seconds = totalSeconds % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d:%03d", hours, minutes, seconds, mill);
    }

    public final static String USB_ACTION = "android.hardware.usb.action.USB_STATE";
    /**
     * USB状态标志
     * */
    public static boolean isUsbIn = false;
    /**
     * USB状态监听
     * */
    public static BroadcastReceiver usbListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (USB_ACTION.equals(action)){
                isUsbIn = intent.getBooleanExtra("connected",false);
            }
        }
    };

}