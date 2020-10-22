package com.example.highplattest.main.tools;

import android.annotation.SuppressLint;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DesTool 
{
//	private final static String TRANSFORMATION = "DES/CBC/PKCS5Padding";//DES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
//	 private final static String IVPARAMETERSPEC = "01020304";////初始化向量参数，AES 为16bytes. DES 为8bytes.
	private static final String Algorithm = "DESede";
	private static final String hexString="0123456789ABCDEF";
//	private static final String SHA1PRNG = "SHA1PRNG";//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
	
	 /**
     * 													 
     * @param keybyte  加密密钥，长度为16字节
     * @param src 	  字节数组(根据给定的字节数组构造一个密钥。 )
     * @return
     */
    @SuppressLint("TrulyRandom")
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            // 根据给定的字节数组和算法构造一个密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
    
    /**
     * 
     * @param keybyte 密钥
     * @param src	    需要解密的数据
     * @return
     */
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
    	
    	/*try
    	{
    		Cipher cipher = Cipher.getInstance("DESede"); 
        	DESKeySpec desKeySpec = new DESKeySpec(keybyte); 
        	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede"); 
        	SecretKey secretKey = keyFactory.generateSecret(desKeySpec); 
        	cipher.init(Cipher.ENCRYPT_MODE, secretKey); 

        	return cipher.doFinal(src); 
    	}
    	catch (Exception e) 
    	{
    		e.printStackTrace();
		}
    	return null;*/
    	
       try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 解密
            Cipher c1 = Cipher.getInstance("DESede/ECB/NoPadding");
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
    
    
    /**
    * 加密
    * @param datasource byte[]
    * @param password byte[]
    * @return byte[]
    */
    public static byte[] encrypt( byte[] key,byte[] data) { 
    try{
    SecureRandom random = new SecureRandom();
    DESKeySpec desKey = new DESKeySpec(key);
    //创建一个密匙工厂，然后用它把DESKeySpec转换成
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
    SecretKey securekey = keyFactory.generateSecret(desKey);
    //Cipher对象实际完成加密操作
    Cipher cipher = Cipher.getInstance("DES");
    //用密匙初始化Cipher对象
    cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
    //现在，获取数据并加密
    //正式执行加密操作
    return cipher.doFinal(data);
    }catch(Throwable e){
    e.printStackTrace();
    }
    return null;
    }

    
    
    
    /**
     * 字符串转为16进制
     * @param str
     * @return
     */
    public static String encode(String str) 
    { 
        //根据默认编码获取字节数组 
        byte[] bytes=str.getBytes(); 
        StringBuilder sb=new StringBuilder(bytes.length*2); 

        //将字节数组中每个字节拆解成2位16进制整数 
        for(int i=0;i<bytes.length;i++) 
        { 
            sb.append(hexString.charAt((bytes[i]&0xf0)>>4)); 
            sb.append(hexString.charAt((bytes[i]&0x0f)>>0)); 
        } 
        return sb.toString(); 
    } 
    /**
     * 
     * @param bytes
     * @return
     * 将16进制数字解码成字符串,适用于所有字符（包括中文） 
     */ 
    public static String decode(String bytes) 
    { 
        ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2); 
        //将每2位16进制整数组装成一个字节 
        for(int i=0;i<bytes.length();i+=2) 
            baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1)))); 
        return new String(baos.toByteArray()); 
    } 

    // 转换成十六进制字符串
    @SuppressLint("DefaultLocale")
	public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = hs + ":";
        }
        return hs.toUpperCase();
    }
}
