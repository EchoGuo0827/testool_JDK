package com.quectel.jni;

/**
 * Created by zhaocaiguang on 2017/8/8.
 */

public class QuecJNI {
    static {
        System.loadLibrary("QuecModule");
    }
    public static native int wifiProbe(byte[] a);//传输数据
    public static native int openNode();//打开节点-1表示失败，0成功
    public static native int closeNode();//关闭节点
    public static native int transferSwitch(int o);
    public static native void setNetPort(int o); //��ʼ��ǰ������port, Ĭ��29, 
}
