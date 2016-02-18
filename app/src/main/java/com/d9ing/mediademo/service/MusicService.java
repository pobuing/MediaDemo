package com.d9ing.mediademo.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.d9ing.mediademo.conf.IConstants;
import com.d9ing.mediademo.utils.MediaUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 后台服务的封装
 * Created by wx on 2016/2/17.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mplayer;
    private Messenger mMessenger;
    private Timer mTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mplayer = new MediaPlayer();
        //设置资源监听
        mplayer.setOnErrorListener(this);
        mplayer.setOnPreparedListener(this);
        mplayer.setOnCompletionListener(this);
        super.onCreate();
    }

    /**
     * 服务启动的生命周期方法
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String option = intent.getStringExtra("option");
            if (mMessenger == null) {
            }
            mMessenger = ((Messenger) intent.getExtras().get("messenger"));
            Log.d("status-----------", option);
            if (option.equals("play")) {
                String path = intent.getStringExtra("path");
                play(path);
            } else if (option.equals("pause")) {
                pause();
            } else if (option.equals("continue")) {
                continuePlay();
            } else if (option.equals("stop")) {
                stopPlay();
            } else if (option.equals("seek")) {
                int progress = intent.getIntExtra("progress", -1);
                seekPlay(progress);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 销毁方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 播放音乐
     */
    public void play(String pahth) {
        try {
            mplayer.reset();
            mplayer.setDataSource(pahth);
            mplayer.prepare();
            mplayer.start();
            MediaUtils.CURSTATE = IConstants.STATE_play;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停音乐
     */
    public void pause() {
        if (mplayer != null && mplayer.isPlaying()) {
            mplayer.pause();
            MediaUtils.CURSTATE = IConstants.STATE_PAUSE;
        }
    }

    /**
     * 继续播放
     */
    public void continuePlay() {
        if (mplayer != null && !mplayer.isPlaying()) {
            mplayer.start();
            MediaUtils.CURSTATE = IConstants.STATE_play;
        }
    }

    public void stopPlay() {
        if (mplayer != null) {
            mplayer.stop();
            MediaUtils.CURSTATE = IConstants.STATE_STOP;
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    /**
     * 进度播放
     *
     * @param progress
     */
    public void seekPlay(int progress) {
        if (mplayer != null && mplayer.isPlaying()) {
            Log.d("progress", progress + "");
            mplayer.seekTo(progress);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        try {
            Message msg = Message.obtain();
            msg.what = IConstants.MSG_ONCOMPLETION;
            //使用信使发送消息
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    //准备就绪的时候告诉activity
                    int currentPosition = mplayer.getCurrentPosition();
                    int duration = mplayer.getDuration();
                    //通过handler与activity交互数据
                    Message msg = Message.obtain();
                    msg.what = IConstants.MSG_ONPREPARED;
                    msg.arg1 = currentPosition;
                    msg.arg2 = duration;
                    //使用信使发送消息
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1600);

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
