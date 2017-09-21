package com.example.jianzhongchen.mymusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.example.jianzhongchen.mymusic.constants.Constants;
import com.example.jianzhongchen.mymusic.utils.MediaUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private Messenger mMessenger;
    private Timer mTimer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"onStartCommand",Toast.LENGTH_SHORT).show();
        String option = intent.getStringExtra("option");
        if (mMessenger == null) {
            mMessenger = (Messenger) intent.getExtras().get("messenger");
        }
        if ("播放".equals(option)) {
            String path = intent.getStringExtra("path");
            play(path);
        } else if ("暂停".equals(option)) {
            pause();
        } else if ("停止".equals(option)) {
            stop();
        } else if ("继续播放".equals(option)) {
            continuePlay();
        } else if ("拖动播放".equals(option)) {
            int progress = intent.getIntExtra("progress", 0);
            seekPlay(progress);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void play(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            MediaUtils.CURSTATE = Constants.STATE_PLAY;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void seekPlay(int progress) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(progress);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            MediaUtils.CURSTATE = Constants.STATE_PAUSE;
        }
    }

    public void continuePlay() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            MediaUtils.CURSTATE = Constants.STATE_PLAY;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            MediaUtils.CURSTATE = Constants.STATE_STOP;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
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
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    Message msg = Message.obtain();
                    msg.what = Constants.MSG_ONPREPARED;
                    msg.arg1 = currentPosition;
                    msg.arg2 = duration;

                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Message msg = Message.obtain();
        msg.what = Constants.MSG_COMPLETION;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
