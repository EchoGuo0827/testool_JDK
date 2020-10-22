package com.example.highplattest.main.tools;

import java.io.IOException;

import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum.Nfc_Card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
@SuppressLint("NewApi")
public class NfcTool implements NDK
{
	NfcAdapter nfcAdapter;
	Activity activity;
	// 返回接口
	NfcAdapter.ReaderCallback nfcCallBack;
	NfcA nfcA;
	NfcB nfcB;
	MifareClassic mifareClassic;
	private int apduLen;
	/**
	 * 初始化ReaderCallback接口
	 */
	public void initCallBack()
	{
		Log.d("eric_chen", "进入-------initCallBack");
		 nfcCallBack = new NfcAdapter.ReaderCallback() 
		 {

			@Override
			public void onTagDiscovered(Tag tag) 
			{
				Log.d("eric_chen", "进入-------onTagDiscovered");
				String[] s = tag.getTechList();
				if(s[0].equals(NfcA.class.getName()))
				{
					try 
					{
						Log.d("eric_chen", "进入-------onTagDiscovered-----nfcA");
						nfcA = NfcA.get(tag);
						nfcA.connect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(s[0].equals(NfcB.class.getName()))
				{
					try 
					{
						Log.d("eric_chen", "进入-------onTagDiscovered-----nfcB");
						nfcB = NfcB.get(tag);
						nfcB.connect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(s[0].equals(MifareClassic.class.getName()))
				{
					try 
					{
						Log.d("eric_chen", "进入-------onTagDiscovered-----nfcM1");
						mifareClassic = MifareClassic.get(tag);
						mifareClassic.connect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		};
	}
	
	
	public NfcTool(Activity activity)
	{
		this.activity = activity;
		nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		Log.d("eric_chen", "初始化----");
		initCallBack();
	}
	
	public void nfcConnect2(int readFlag){
		Bundle option = new Bundle();
		option.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
		nfcAdapter.enableReaderMode(activity, nfcCallBack, readFlag, option);
		SystemClock.sleep(500);
	}
	
	
	/**
	 * 连接操作
	 * @param readFlag 滤波，设置放置的卡类型
	 * @return
	 */
	public int nfcConnect(int readFlag)
	{
		int ret = NDK_ERR;
		Bundle option = new Bundle();
		option.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
		nfcAdapter.enableReaderMode(activity, nfcCallBack, readFlag, option);
		SystemClock.sleep(500);
		switch (readFlag) 
		{
		case NfcAdapter.FLAG_READER_NFC_A:
			if(nfcA!=null)
				ret = NDK_OK;
			break;
			
		case NfcAdapter.FLAG_READER_NFC_B:
			if(nfcB!=null)
				ret = NDK_OK;
			break;
			
		case NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK:
			if(mifareClassic!=null)
				ret = NDK_OK;
			break;

		default:
			break;
		}
		return ret;
	}
	
	/**
	 * 进行NFC卡操作
	 * @param nfc_Card
	 * @return
	 * @throws IOException
	 */
	public int nfcRw(Nfc_Card nfc_Card) throws IOException
	{
		byte[] data ={0x00,(byte) 0x84,0x00,0x00,0x08};
		byte[] DATA16 = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
		byte[] code1 = {(byte) 0x90,0x00};
		byte[] readData = new byte[16];
		byte[] respCode = new byte[2];
		byte[] back;
		int ret = NDK_ERR;
		
		switch (nfc_Card) 
		{
		case NFC_A:
			back = nfcA.transceive(data);
			// 设置apdu长度
			setApduLen(back.length);
			if(back.length==0)
			{
				ret = NDK_ERR;
				break;
			}
			System.arraycopy(back, back.length-2, respCode, 0, 2);
			LoggerUtil.i(ISOUtils.hexString(respCode));
			if(Tools.memcmp(respCode, code1, 2))
				ret = NDK_OK;
			else if((respCode[0]&0x60)==0x60)// 不支持APDU命令给个专门的返回值
				ret = NFC_NO_APDU;
			break;
			
		case NFC_B:
			back = nfcB.transceive(data);
			// 设置apdu长度
			setApduLen(back.length);
			if(back.length==0)
			{
				ret = NDK_ERR;
				break;
			}
			System.arraycopy(back, back.length-2, respCode, 0, 2);
			LoggerUtil.i(ISOUtils.hexString(respCode));
			if(Tools.memcmp(respCode, code1, 2))
				ret = NDK_OK;
			else if((respCode[0]&0x60)==0x60)// 不支持APDU命令给个专门的返回值
				ret = NFC_NO_APDU;
			break;
			
		case NFC_M1:
			mifareClassic.writeBlock(60, DATA16);
			readData = mifareClassic.readBlock(60);
			if(Tools.memcmp(readData, DATA16, 16))
				ret = NDK_OK;
			
			break;
			
		default:
			break;
		}
		return ret;
	}
	
	/**
	 * 断开NFC卡连接
	 */
	public void nfcDisEnableMode()
	{
		if(nfcAdapter!=null)
			nfcAdapter.disableReaderMode(activity);
	}
	
	/**
	 * 获取NFC的APDU的长度
	 */
	public int getApduLen()
	{
		return apduLen;
	}
	
	public void setApduLen(int len)
	{
		apduLen = len;
	}
}
