package com.example.highplattest.main;

public interface DefineListener
{
	public interface BackListener
	{
		public void onBackDown();// 重写返回键
	}

	public interface TestConfigListener
	{
		/**
		 * 测试前置动作
		 */
		public void onTestUp();
		/**
		 * 测试后置动作
		 */
		public void onTestDown();
	}
}
