package com.example.highplattest.main.bean;

import java.text.MessageFormat;
import com.example.highplattest.main.tools.LoggerUtil;

/**
 * 异常控制类
 * @author zhengxq
 * 2016-4-6 下午3:52:04
 */
public class ApplicationExceptionBean extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public int code;
	
	public ApplicationExceptionBean(int code, String msg, Throwable e) {
		super(msg, e);
		this.code = code;
	}
	
	public ApplicationExceptionBean(int code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
	
	@Override
	public String getLocalizedMessage() {
		LoggerUtil.d("002="+"getLocalizedMessage"+super.getLocalizedMessage());
		return MessageFormat.format("{0},{1}", this.code,super.getLocalizedMessage());
	}

}
