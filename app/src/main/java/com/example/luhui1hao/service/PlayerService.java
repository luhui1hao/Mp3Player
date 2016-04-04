package com.example.luhui1hao.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.luhui1hao.model.Mp3Info;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhui1hao on 2015/12/16.
 */
public class PlayerService extends Service {
    public static final String TAG = "PlayerService";
    private List<Mp3Info> mp3Infos = new ArrayList<>();
    private int position;
    private Mp3Info mp3Info;
    private Mp3Info currentMp3Info = new Mp3Info();
    private MediaPlayer mediaPlayer;
    private StartReceiver startReceiver;
    private PauseReceiver pauseReceiver;
    private ShangyishouReceiver shangyishouReceiver;
    private XiayishouReceiver xiayishouReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        //绑定播放广播接收器
        IntentFilter startFilter = new IntentFilter();
        startFilter.addAction("com.example.luhui1hao.START");
        startReceiver = new StartReceiver();
        registerReceiver(startReceiver, startFilter);
        //绑定暂停广播接收器
        IntentFilter pauseFilter = new IntentFilter();
        pauseFilter.addAction("com.example.luhui1hao.PAUSE");
        pauseReceiver = new PauseReceiver();
        registerReceiver(pauseReceiver, pauseFilter);
        //绑定上一首广播接收器
        IntentFilter shangyishouFilter = new IntentFilter();
        shangyishouFilter.addAction("com.example.luhui1hao.SHANGYISHOU");
        shangyishouReceiver = new ShangyishouReceiver();
        registerReceiver(shangyishouReceiver, shangyishouFilter);
        //绑定下一首广播接收器
        IntentFilter xiayishouFilter = new IntentFilter();
        xiayishouFilter.addAction("com.example.luhui1hao.XIAYISHOU");
        xiayishouReceiver = new XiayishouReceiver();
        registerReceiver(xiayishouReceiver, xiayishouFilter);
        Log.e(TAG, "onCreate has run");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取Mp3Info对象
        mp3Infos = (List<Mp3Info>)intent.getSerializableExtra("mp3Infos");
        //获取position对象
        position = intent.getIntExtra("position", 0);
        //得到传入的的mp3Info对象
        mp3Info = mp3Infos.get(position);
        //设置MediaPlayer
        setMediaPlayer(mp3Infos, position);

        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaPlayer() {
        // 创建MediaPlayer
        mediaPlayer = new MediaPlayer();
    }

    private void setMediaPlayer(List<Mp3Info> mp3Infos, int position) {
        //如果mp3的名字一致，则维持原状
        //否则，停止原来的歌，播放现在的歌
        Log.e(TAG, mp3Info + "\n" + currentMp3Info);
        if(mp3Info.getMp3Name().equals(currentMp3Info.getMp3Name())){

        }else{
            mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                File file = new File(mp3Info.getMp3Path());
                if (file.exists()) {
                    mediaPlayer.setDataSource(file.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获得当前的Mp3Info对象
            currentMp3Info = mp3Infos.get(position);
        }
    }

    class StartReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }

    class PauseReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    class ShangyishouReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //先释放掉当前的歌曲
            mediaPlayer.stop();
            mediaPlayer.reset();
            if(position==0){
                position = mp3Infos.size() - 1;
            }else{
                position--;
            }
            //获得当前的Mp3Info对象
            currentMp3Info = mp3Infos.get(position);
            try {
                File file = new File(mp3Infos.get(position).getMp3Path());
                if (file.exists()) {
                    mediaPlayer.setDataSource(file.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class XiayishouReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //先释放掉当前的歌曲
            mediaPlayer.stop();
            mediaPlayer.reset();
            if(position==mp3Infos.size()-1){
                position = 0;
            }else{
                position++;
            }
            //获得当前的Mp3Info对象
            currentMp3Info = mp3Infos.get(position);
            try {
                File file = new File(mp3Infos.get(position).getMp3Path());
                if (file.exists()) {
                    mediaPlayer.setDataSource(file.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(startReceiver);
        unregisterReceiver(pauseReceiver);
        unregisterReceiver(shangyishouReceiver);
        unregisterReceiver(xiayishouReceiver);
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
