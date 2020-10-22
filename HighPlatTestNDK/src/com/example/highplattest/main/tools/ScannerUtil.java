package com.example.highplattest.main.tools;

import android.newland.os.NlBuild;

public class ScannerUtil 
{
	public final int SCAN_BACK = 0;
	public final int SCAN_FRONT = 1;
	
	public boolean isSupScanCode(int cameraId)
	{
		switch (cameraId) 
		{
		case SCAN_FRONT:
			if(isSupportFunction("1.1.12", cameraId))
			{
				String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
				if(config!=null&&config.length()>=10)
				{
					String cameraType = config.substring(8, 10);
					if("01".equals(cameraType)||"03".equals(cameraType))
						return true;
					
				}
				return false;
			}
			break;
			
		case SCAN_BACK:
			if(isSupportFunction("1.1.12", cameraId))
			{
				String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
				if(config!=null&&config.length()>=10)
				{
					String cameraType = config.substring(8, 10);
					if("10".equals(cameraType)||"11".equals(cameraType))
						return true;
					
				}
				return false;
			}

		}
		return false;
	}
	
	private boolean isSupportFunction(String requestVersion,int cameraId)
	{
		// 硬件识别码
		if("SA1".equals(NlBuild.VERSION.NL_HARDWARE_ID))
		{
			if(cameraId==SCAN_BACK)//3g设备不支持后置zxing以及nls扫码
				return false;
			else
			{
				String version = NlBuild.VERSION.NL_FIRMWARE;
				version = version.replaceAll("V", "").replace("T", "");
				return requestVersion.compareToIgnoreCase(version)<=0;
			}
		}
		return true;
	}
	
	/**
	 * 判断是否支持软解码
	 * @return
	 */
	public boolean isSupNlsScanCode()
	{
		if(isSupportFunction("1.1.12", 0))
		{
			// 硬件配置码
			String config = NlBuild.VERSION.NL_HARDWARE_CONFIG;
			if(config!=null&&config.length()>=10)
			{
				String cameraType = config.substring(8,10);
				if("03".equals(cameraType)||"12".equals(cameraType))
					return true;
				return false;
			}
		}
		return false;
	}
}
