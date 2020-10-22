package com.example.highplattest.android;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.content.Context;
import android.media.AudioManager;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android7.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180411 
 * directory 		: 
 * description 		: 测试Android原生声音接口
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180411 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android7 extends UnitFragment {
	public final String TAG = Android7.class.getSimpleName();
	private String TESTITEM = "声音接口测试";
	private Gui gui = new Gui(myactivity, handler);
		
	public void android7(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		  AudioManager mAudioManager = (AudioManager)myactivity.getSystemService(Context.AUDIO_SERVICE);
		  
		  //测试前置，获取媒体音量、铃声音量的当前音量    
		  int currentVolume1 = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  
		  int currentVolume2 = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);  
		  
		  //case1：将媒体音量、铃声音量设置为最小音量0
	      mAudioManager.setStreamVolume (AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
	      mAudioManager.setStreamVolume (AudioManager.STREAM_RING,0, AudioManager.FLAG_PLAY_SOUND);

          if (gui.cls_show_msg("查看设置--声音中的媒体音量和铃声音量和铃声音量是否为最小音量，[确认]是，[其他]否") != ENTER) 
          {
        	  gui.cls_show_msg1_record(TAG, "android7", gKeepTimeErr, "line %d:%s设置媒体音量为最小效果异常", Tools.getLineInfo(), TESTITEM);
  		  }
          
          //case2：将媒体音量、铃声音量设置为最大音量
        //获取媒体音量、铃声音量的最大音量    
		  int maxVolume1 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  
		  int maxVolume2 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);  
	      mAudioManager.setStreamVolume (AudioManager.STREAM_MUSIC, maxVolume1, AudioManager.FLAG_PLAY_SOUND);
	      mAudioManager.setStreamVolume (AudioManager.STREAM_RING,maxVolume2, AudioManager.FLAG_PLAY_SOUND);

          if (gui.cls_show_msg("查看设置--声音中的媒体音量和铃声音量是否为最大音量，[确认]是，[其他]否") != ENTER) 
          {
        	  gui.cls_show_msg1_record(TAG, "android7", gKeepTimeErr, "line %d:%s设置媒体音量和铃声音量为最大效果异常", Tools.getLineInfo(), TESTITEM);
  		  }
          //测试后置，恢复为之前设置的音量大小
          mAudioManager.setStreamVolume (AudioManager.STREAM_MUSIC, currentVolume1, AudioManager.FLAG_PLAY_SOUND);
	      mAudioManager.setStreamVolume (AudioManager.STREAM_RING,currentVolume2, AudioManager.FLAG_PLAY_SOUND);
          
//	      mAudioManager.setStreamVolume (AudioManager.STREAM_ALARM, 30, AudioManager.FLAG_PLAY_SOUND);
//	      mAudioManager.setStreamVolume (AudioManager.STREAM_NOTIFICATION, 30, AudioManager.FLAG_PLAY_SOUND);
//	      mAudioManager.setStreamVolume (AudioManager.STREAM_SYSTEM, 30, AudioManager.FLAG_PLAY_SOUND);
//	      mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 30, AudioManager.FLAG_PLAY_SOUND);

	      
	      gui.cls_show_msg1_record(TAG, "android7",gScreenTime,"%s测试通过", TESTITEM);
	}


	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
