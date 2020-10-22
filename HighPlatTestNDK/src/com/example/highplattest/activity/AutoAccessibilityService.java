package com.example.highplattest.activity;

import java.util.List;

import com.example.highplattest.main.tools.LoggerUtil;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
/**
 * 无障碍服务 
 * @author zhangxinj
 *
 */
public class AutoAccessibilityService extends AccessibilityService{

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		 int eventType = event.getEventType();
		 switch (eventType) {
		 case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			 LoggerUtil.e("监听到事件");
			 runTest();
			 break;
		 }
	}

	@Override
	public void onInterrupt() {
		
	}
	public void runTest(){
		AccessibilityNodeInfo rootNode = getRootInActiveWindow(); 
		SystemClock.sleep(1000);
		findEditText(rootNode,"com.example.autoaccessibilitytest:id/login_name","highPlatTest");
		SystemClock.sleep(1000);
		findEditText(rootNode,"com.example.autoaccessibilitytest:id/login_pwd","highPlatTest123456");
		SystemClock.sleep(1000);
		login();
	}
	/**
	 * 查找EditText，并输入内容
	 * @param rootNode
	 * @param id
	 * @param content
	 * @return
	 */
	private boolean findEditText(AccessibilityNodeInfo rootNode,String id, String content) {
		 List<AccessibilityNodeInfo> m=rootNode.findAccessibilityNodeInfosByViewId(id);
		 AccessibilityNodeInfo nodeInfo=m.get(0);
		 Bundle arguments = new Bundle();  
		 arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);  
		 nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);  
		 return true;
	}
	/**
	 * 模拟按钮点击事件
	 */
	public void login(){
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo != null) {
			List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("登陆");
			if (list != null && list.size() > 0) {
				for (AccessibilityNodeInfo n : list) {
					if (n.getClassName().equals("android.widget.Button")&& n.isEnabled()) {
						n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					}
				}
			}
		}
	}
}
