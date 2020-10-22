package com.example.highplattest.main.tools;

import java.io.IOException;

import com.example.highplattest.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

/**
 * 二维码扫描成功声音管理器
 * 
 */
public class Beep {

    public static final String TAG = Beep.class.getSimpleName();

    private Context mContext = null;

    /** 声音模式是否打开 */
    private boolean beepEnabled = true;

    /** 震动模式是否打开 */
    private boolean vibrateEnabled = true;

    /** 震动时长 */
    private long vibrateDuration = 200L;

    /**
     * 构造函数
     * 
     * @param activity
     */
    public Beep(Context context) {

        mContext = context;
        ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /** 返回声音模式 */
    public boolean isBeepEnabled() {
        return beepEnabled;
    }

    /** 设置声音模式 */
    public void setBeepEnabled(boolean beepEnabled) {
        this.beepEnabled = beepEnabled;
    }

    /** 返回震动模式 */
    public boolean isVibrateEnabled() {
        return vibrateEnabled;
    }

    /** 设置震动模式 */
    public void setVibrateEnabled(boolean vibrateEnabled) {
        this.vibrateEnabled = vibrateEnabled;
    }

    /**
     * 震动并播放声音
     */
    public synchronized void playAndVibrate() {

        if (beepEnabled) {
            Log.d(TAG, "__playBeepSound");
            play();
        }
        if (vibrateEnabled) {
            Vibrator v = null;
            // 获取震动服务
            v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                // 设置手机振动的毫秒数
                v.vibrate(vibrateDuration);
            }
        }
    }

    /**
     * 播放声音
     * 
     * @return
     */
    public MediaPlayer play() {

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });
        // 绑定异常监听器
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.w(TAG, "__Failed to beep " + what + ", " + extra);
                // possibly media player error, so release and recreate
                mp.stop();
                mp.release();
                return true;
            }
        });
        try {
            AssetFileDescriptor file = null;
            file = mContext.getResources().openRawResourceFd(R.raw.btn_sound);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
            } finally {
                file.close();
            }
            mediaPlayer.setVolume(0.10f, 0.10f);
            mediaPlayer.prepare();
            mediaPlayer.start();
            return mediaPlayer;
        } catch (IOException e) {
            Log.w(TAG, e);
            mediaPlayer.release();
            return null;
        }
    }

}