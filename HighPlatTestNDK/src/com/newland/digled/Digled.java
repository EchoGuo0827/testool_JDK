package com.newland.digled;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lin
 * @version 2018/7/2 0002
 */
public class Digled {

    static {
        System.loadLibrary("Digled");
    }

    private static native int JNI_PosDledVer(byte[] buf);

    private static native int JNI_PosShowDigLed(byte[] buf);


    private static native int JNI_PosDledBright(int bright);

    private static native int JNI_PosDledClr();
    /**
     * 获取驱动版本
     * @param buf
     * @return
     */
    public int digLedVer(byte[] buf){
        if (buf == null){
            return -2;
        }
        if (buf.length < 28){
            return -2;
        }
        return JNI_PosDledVer(buf);
    }

    Pattern mPattern = Pattern.compile("([0-9]|\\.)*");
    /**
     * 显示应用层传递下来的字符数字
     * @param num
     * @return
     */
    public int showDigLed(String num){
        if (num == null || "".equals(num)){
            return -2;
        }
        Matcher matcher = mPattern.matcher(num);
        if (!matcher.matches()) {
            return -2;
        }

        byte[] numBuffer = new byte[num.getBytes().length+1];
        System.arraycopy(num.getBytes(), 0, numBuffer, 0, numBuffer.length-1);
        return JNI_PosShowDigLed(numBuffer);
    }

    /**
     * 设置亮度及开启扫描
     * @param bright 0-7
     * @return
     */
    public int brightDigLed(int bright){
        return JNI_PosDledBright(bright);
    }


    /**
     * 数码管全灭
     * @return
     */
    public int clrDigLed(){return JNI_PosDledClr();}
}
