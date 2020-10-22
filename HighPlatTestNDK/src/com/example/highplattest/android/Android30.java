package com.example.highplattest.android;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android30.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20181009 
 * directory 		: 
 * description 		: 多种音频播放方式测试MediaPlayer、SoulPlayer
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20181009	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android30 extends UnitFragment
{
	public final String TAG = Android30.class.getSimpleName();
	private String TESTITEM = "多种音频播放测试";
//	private MediaPlayer mMediaPlayer;
	private Object lockObj = new Object();
	private Gui gui = new Gui(myactivity, handler);
	private SoundPool mSoundPool;
	
	public void android30()
	{
		while(true)
		{
			//JetPlayer控制游戏音乐的播放        AudioTrack用于PCM文件的播放
			int nkeyIn = gui.cls_show_msg("%s\n1.SoundPool播放方式\n2.MediaPlayer播放方式\n3.RingtoneManager播放方式\n",TESTITEM);
			switch (nkeyIn) {
			case '1':
				soundWay();
				break;
				
			case '2':
				mediaWay();
				break;
				
			case '3':
				ringWay();
				break;
				
			case ESC:
				unitEnd();
				return;
				
			default:
				break;
			}
		}

	}
	
	/**SoundPool方式播放音频*/
	@SuppressLint("NewApi") 
	private void soundWay()
	{
		mSoundPool = new SoundPool.Builder().build();
		final SoundComple soundComple = new SoundComple();
		gui.cls_printf("SoundPool方式正在播放第一个音频文件".getBytes());
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				// 加载音频文件，使用raw下的文件
				mSoundPool.load(myactivity, R.raw.input_amount, 1);
				mSoundPool.setOnLoadCompleteListener(soundComple);
			}
		}.start();
		synchronized (lockObj) {
			try {
				lockObj.wait(5*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		gui.cls_printf("SoundPool方式正在播放第二个音频文件".getBytes());
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				// 加载音频文件，使用raw下的文件
				mSoundPool.load(myactivity, R.raw.retrieve_card, 1);
				mSoundPool.setOnLoadCompleteListener(soundComple);
			}
		}.start();
		synchronized (lockObj) {
			try {
				lockObj.wait(5*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		gui.cls_printf("SoundPool方式正在播放第三个音频文件".getBytes());
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				// 加载音频文件，使用raw下的文件
				mSoundPool.load(myactivity, R.raw.akb, 1);
				mSoundPool.setOnLoadCompleteListener(soundComple);
			}
		}.start();
		synchronized (lockObj) {
			try {
				lockObj.wait(2*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		gui.cls_show_msg1_record(TAG, "android30",gScreenTime, "SoundPool方式播放音频文件测试完毕");
	}
	
	class SoundComple implements SoundPool.OnLoadCompleteListener
	{

		@Override
		public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
			soundPool.play(sampleId, 1, 1f, 0, 0, 1);
		}
		
	}
	
	/**MediaPlayer方式*/
	private void mediaWay()
	{
		final MediaListener mediaListener = new MediaListener();
		gui.cls_printf("MediaPlayer方式正在播放第一个音频文件".getBytes());
		// case1:MediaPlayer方式播放音频 不足:资源占用量较高、延迟时间较长、不支持多个音频同时播放等
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				MediaPlayer mMediaPlayer = MediaPlayer.create(myactivity, R.raw.input_amount);
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(mediaListener);
			}
		}.start();
		synchronized (lockObj) {
			try {
				lockObj.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		gui.cls_printf("MediaPlayer方式正在播放第二个音频文件".getBytes());
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				MediaPlayer mMediaPlayer = MediaPlayer.create(myactivity, R.raw.retrieve_card);
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(mediaListener);
			}
		}.start();
		synchronized (lockObj) {
			try {
				lockObj.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		gui.cls_printf("MediaPlayer方式正在播放第三个音频文件".getBytes());
		new Thread()
		{
			@Override
			public void run() {
				super.run();
				MediaPlayer mMediaPlayer = MediaPlayer.create(myactivity, R.raw.akb);
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(mediaListener);
			}
		}.start();
		synchronized (lockObj) {
			try {
				lockObj.wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		gui.cls_show_msg1_record(TAG, "android30",gScreenTime, "MediaPlayer方式播放音频文件测试完毕");
	}
	
	class MediaListener implements OnCompletionListener
	{

		@Override
		public void onCompletion(MediaPlayer mp) {
			mp.stop();
			mp.release();
			synchronized (lockObj) {
				lockObj.notify();
			}
		}
	}
	
	/***
	 * 随机播放一个Ringtone
	 */
	private void ringWay()
	{
		gui.cls_printf("随机播放铃声".getBytes());
		RingtoneManager manager = new RingtoneManager(myactivity);
		Cursor cursor = manager.getCursor();
		int count = cursor.getCount();
		int position = (int) (Math.random()*count);
		final Ringtone mRingtone = manager.getRingtone(position);
		gui.cls_show_msg1(1, "即将播放%s铃声", mRingtone.getTitle(myactivity));
		new Thread()
		{
			public void run() 
			{
				mRingtone.play();
			};
		}.start();
		synchronized (lockObj) {
			try {
				lockObj.wait(5*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mRingtone.stop();
		gui.cls_show_msg1_record(TAG, "android30",gScreenTime, "随机铃声播放完毕");
	}
	
//	/**AudioTrack方式，主要适用于流文件*/
//	private void audioWay()
//	{
//		new Thread()
//		{
//			@SuppressLint("NewApi") @Override
//			public void run() {
//				super.run();
//				// 获取最小缓冲区 
//				int bufSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
//				AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, 
//						AudioFormat.ENCODING_PCM_16BIT, bufSize*2, AudioTrack.MODE_STREAM);
//				audioTrack.setVolume(2f);
//				audioTrack.setPlaybackRate(10);
//				audioTrack.play();
//				InputStream is = myactivity.getResources().openRawResource(R.raw.input_amount);
//				byte[] buffer = new byte[bufSize*2];
//				int len;
//				try {
//					while((len = is.read(buffer, 0, buffer.length))!=-1)
//					{
//						audioTrack.write(buffer, 0, buffer.length);
//					}
//					is.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//			}
//		}.start();
//	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
