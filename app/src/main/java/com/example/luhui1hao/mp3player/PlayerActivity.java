package com.example.luhui1hao.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.luhui1hao.R;
import com.example.luhui1hao.ioutils.FileUtils;
import com.example.luhui1hao.model.Mp3Info;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhui1hao on 2015/12/4.
 */
public class PlayerActivity extends Activity {
    private MediaPlayer mediaPlayer;
    private TextView tv;
    private static final int START = 1;
    private static final int PAUSE = 0;
    private int currentState = START;
    private Animation anim, animQuit;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置界面布局
        setContentView(R.layout.player);
        //创建监听器对象
        BtnListener btnListener = new BtnListener();
        // 获取按钮并绑定监听器
        findViewById(R.id.bofang_btn).setOnClickListener(btnListener);
        findViewById(R.id.shangyishou).setOnClickListener(btnListener);
        findViewById(R.id.xiayishou).setOnClickListener(btnListener);

       /* tv = (TextView) findViewById(R.id.lrc);
        FileUtils fileUtils = new FileUtils();
        String lrcStr = fileUtils.readLrc(FileUtils.getSDCARD_ROOT() + "aMp3/" + mp3Info.getLrcName());
        tv.setText(lrcStr);*/
        //载入动画
        anim = AnimationUtils.loadAnimation(this, R.anim.anim);
        animQuit = AnimationUtils.loadAnimation(this, R.anim.anim_quit);
        animQuit.setFillAfter(true);
        //执行动画
        layout = (RelativeLayout) findViewById(R.id.player_layout);
        layout.startAnimation(anim);
    }

    private void initMediaPlayer(Mp3Info mp3Info) {
        try {
            File file = new File(mp3Info.getMp3Path());
            if (file.exists()) {
                // 创建MediaPlayer
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bofang_btn:
                    //发送播放或暂停广播
                    if (currentState == START) {
                        Intent startBroadcastIntent = new Intent("com.example.luhui1hao.PAUSE");
                        sendBroadcast(startBroadcastIntent);
                        //将图标替换成播放
                        v.setBackground(getResources().getDrawable(R.drawable._start));
                        currentState = PAUSE;
                    } else if (currentState == PAUSE) {
                        Intent pauseBroadcastIntent = new Intent("com.example.luhui1hao.START");
                        sendBroadcast(pauseBroadcastIntent);
                        //将图标替换成暂停
                        v.setBackground(getResources().getDrawable(R.drawable._pause));
                        currentState = START;
                    }
                    break;
                case R.id.shangyishou:
                    //发送“上一首”广播
                    Intent shangyishouBroadcastIntent = new Intent("com.example.luhui1hao.SHANGYISHOU");
                    sendBroadcast(shangyishouBroadcastIntent);
                    break;
                case R.id.xiayishou:
                    //发送“下一首”广播
                    Intent xiayishouBroadcastIntent = new Intent("com.example.luhui1hao.XIAYISHOU");
                    sendBroadcast(xiayishouBroadcastIntent);
                    break;
            }

               /* case R.id.start:
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                    break;
                case R.id.pause:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break;
                case R.id.stop:
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.prepare();
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                    break;*/
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
/* 返回键 */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //layout.startAnimation(animQuit);
            finish();
        }
        return false;
    }

}
