package com.example.highplattest.android;

import java.io.File;
import java.util.HashMap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.VideoView;
import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;

/************************************************************************
 * 
 * module 			: Android模块
 * file name 		: Android34.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20191105
 * directory 		: 视频播放测试
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android34 extends UnitFragment {
	private final String TESTITEM =  "视频播放测试";
	private String fileName=Android34.class.getSimpleName();
	private int ret ;
	String text;
	private SeekBar seekBar;
	private Button btnstart,btnstop,btnresume,btnpause;
	private VideoView videoView;
	String videopath="sdcard/video1.mp4";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.video_player, container, false);
		videoView= view.findViewById(R.id.videoView1);
		btnstart=view.findViewById(R.id.start);
		btnstop=view.findViewById(R.id.stop);
		btnpause=view.findViewById(R.id.pause);
		btnresume=view.findViewById(R.id.resume);
		seekBar=view.findViewById(R.id.seekBar1);
		 seekBar.setOnSeekBarChangeListener(change);
		 btnstart.setOnClickListener(click);
		 btnstop.setOnClickListener(click);
		 btnresume.setOnClickListener(click);
		 btnpause.setOnClickListener(click);
		return view;
	}
	private Gui gui = new Gui(myactivity, handler);
	
	 OnSeekBarChangeListener change=new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
            // 当进度条停止修改的时候触发
             // 取得当前进度条的刻度
             int progress = seekBar.getProgress();
            if (videoView != null && videoView.isPlaying()) {
                // 设置当前播放的位置
            	videoView.seekTo(progress);
           }
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	
	View.OnClickListener click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.start:
				play(0);
				break;
			case R.id.stop:
				stop();
				break;
			case R.id.pause:
				pause();
				break;
			case R.id.resume:
				replay();
				break;
			default:
				break;
			}
			
		}

		private void replay() {
			// TODO Auto-generated method stub
		    if (videoView != null && videoView.isPlaying()) {
	        	videoView.seekTo(0);
		           
		        }
		    play(0);
		}

		private void pause() {
			// TODO Auto-generated method stub
	        if (videoView != null && videoView.isPlaying()) {
	        	videoView.pause();
		           
		        }else {
		        	videoView.start();
				}
		}

		private void stop() {
			// TODO Auto-generated method stub
			        if (videoView != null && videoView.isPlaying()) {
			        	videoView.stopPlayback();
				           
				        }
			
		}

		private void play(int i) {
			// TODO Auto-generated method stub
		 videoView.setVideoPath(videopath);
		 videoView.start();
			
		}
	};
	
	
	public  void android34(){
		
		
	}

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
