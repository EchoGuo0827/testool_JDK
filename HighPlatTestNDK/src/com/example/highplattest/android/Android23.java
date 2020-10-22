package com.example.highplattest.android;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android23.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180829 
 * directory 		: 
 * description 		: 测试Android原生6.0确认凭证接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180829 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Android23 extends UnitFragment {
	public final String TAG = Android23.class.getSimpleName();
	private String TESTITEM = "确认凭证原生接口测试(A7 )";
	private Gui gui = new Gui(myactivity, handler);
	private static final String KEY_NAME = "my_key";//我们的钥匙在Android钥匙商店的别名。
    private static final int AUTHENTICATION_DURATION_SECONDS = 10;//设置多少秒后重新验证身份
    private static final byte[] SECRET_BYTE_ARRAY = new byte[] {1, 2, 3, 4, 5, 6};
    private KeyguardManager mKeyguardManager;
    private boolean is=false;
    private KeyStore keyStore=null;
    SecretKey secretKey =null;
    Cipher cipher =null;
    
    public void android23()
    {
    	if(Build.VERSION.SDK_INT>Build.VERSION_CODES.N)
    	{
    		try {
    			testAndroid23();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "android23", gKeepTimeErr, "line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			}
    	}
    	else
    	{
    		gui.cls_show_msg1_record(TAG, "android23", gKeepTimeErr, "SDK版本低于24，不支持该案例");
    	}
    }
	
    @TargetApi(24)
	private void testAndroid23(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		mKeyguardManager = (KeyguardManager) myactivity.getSystemService(Context.KEYGUARD_SERVICE);
        //检测是否设置安全密码或者指纹，有无安全保护。
        if (!mKeyguardManager.isKeyguardSecure()) 
        {
        	gui.cls_show_msg1(2,"去设置->安全录入密码或者指纹");
            return;
        }
        createKey();
        gui.cls_show_msg("已成功生成一个密钥，请锁屏后解锁，操作完成后点击任意键继续");
        if(!tryEncrypt())//验证凭据是否有效
        {
			gui.cls_show_msg1_record(TAG, "android23", gKeepTimeErr, "line %d:%s验证异常", Tools.getLineInfo(), TESTITEM);
            return;
        }else
        {
        	gui.cls_show_msg1(2,"证验证通过后,"+AUTHENTICATION_DURATION_SECONDS+"秒之内不用验证(锁屏秒数重置),请耐心等待"+AUTHENTICATION_DURATION_SECONDS+"s,等待其超时验证失效后重新验证");
            SystemClock.sleep(AUTHENTICATION_DURATION_SECONDS*1000);
            showAuthenticationScreen(); 
        }

		
	}
	
	// 生成一个密钥来解密支付凭证，令牌等。
	public void createKey()
    {
        try 
        {
            KeyStore androidKeyStore = KeyStore.getInstance("AndroidKeyStore");
            androidKeyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
 
            //设置Android KeyStore中出现密钥的条目的别名，以及Builder的构造函数中的约束（目的）
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    //要求用户在过去30秒内解锁,6.0新api
                    .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_DURATION_SECONDS)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (Exception e) 
        {
            throw new RuntimeException("生成密钥失败", e);
        }
    }
    /**
     * 尝试使用{@link #createKey}中生成的密钥加密某些数据，只有在用户刚刚通过设备凭据进行身份验证时，该数据才有效。
     */
    private boolean tryEncrypt() {
    	is=false;
        try 
        {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            // 尝试加密某些东西，只有用户在最后一次认证30秒内进行身份验证，才能工作。超过30秒后在操作就会报异常
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipher.doFinal(SECRET_BYTE_ARRAY);
            //如果用户最近已通过身份验证，您将到达这里。
           is=true;
        } catch (UserNotAuthenticatedException e) {
        	gui.cls_show_msg1(2, "用户未验证或验证失效："+ e.getMessage());
        } catch (KeyPermanentlyInvalidatedException e) {
            //如果在生成密钥后锁定屏幕已被禁用或复位，则会发生这种情况。
        	gui.cls_show_msg1(2, "密钥因锁屏或被禁用或被复位而失效了："+ e.getMessage());
        } catch (Exception e) {
        	gui.cls_show_msg1(2, "异常："+ e.getMessage());
//            throw new RuntimeException(e);
        }
		return is;
    }
    private void showAuthenticationScreen() 
    {
        //创建“确认凭据”屏幕。 您可以自定义标题和说明。
        //如果您将其留空，我们将为您提供一个通用的
        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent("确认凭证", "请再次输入正确的密码");
        if (intent != null) 
            myactivity.startActivityForResult(intent, 1);
    }
   
    

    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) 
		{
            if (resultCode == Activity.RESULT_OK) 
            {
                if (tryEncrypt()) 
                	gui.cls_show_msg1_record(TAG, "android23", gKeepTimeErr,"%s测试通过", TESTITEM);
            } else 
            {
    			gui.cls_show_msg1_record(TAG, "android23", gKeepTimeErr, "line %d:%s验证异常", Tools.getLineInfo(), TESTITEM);
            }
            
		}

	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
