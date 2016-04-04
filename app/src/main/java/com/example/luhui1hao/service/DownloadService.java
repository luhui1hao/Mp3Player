package com.example.luhui1hao.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.luhui1hao.R;
import com.example.luhui1hao.application.MyApplication;
import com.example.luhui1hao.download.HttpDownloader;
import com.example.luhui1hao.ioutils.FileUtils;
import com.example.luhui1hao.model.Mp3Info;

import java.io.File;

/**
 * Created by luhui1hao on 2015/12/5.
 */
public class DownloadService extends Service {
    public static final String TAG = "DownLoadService";
    private Mp3Info mp3Info = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("DownLoadService----------------------------");
        mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

        //判断歌曲是否已存在
        if(FileUtils.isFileExist("aMp3/", mp3Info.getMp3Name())){
            Toast.makeText(MyApplication.getContext(), "已下载过该歌曲", Toast.LENGTH_SHORT).show();
        }else {//如果不存在，再进行下载
            //用于下载的线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String mp3Url = MyApplication.BASE_URL
                            + mp3Info.getMp3Name();
                    String urlStr = MyApplication.BASE_URL
                            + mp3Info.getLrcName();
                    HttpDownloader httpDownloader = new HttpDownloader();
                    int mp3Result = httpDownloader.downFile(mp3Url, "aMp3/",
                            mp3Info.getMp3Name());
                    int lrcResult = httpDownloader.downFile(urlStr, "aMp3/",
                            mp3Info.getLrcName());
                    String resultMessage = null;
                /*
				 * switch (lrcResult) { case 0: resultMessage = "文件下载成功"; break;
				 * case 1: resultMessage = "文件已经存在"; break; case -1:
				 * resultMessage = "文件下载失败"; break; }
				 */
                    // 进行Notification提醒
                }
            }).start();
            //更新下载进度的线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int DOWNLOAD_NOTIFICATION_ID = (int)(Thread.currentThread().getId());
                    int progress = 0;
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(DownloadService.this)
                            .setAutoCancel(true)
                            .setWhen(System.currentTimeMillis())
                            .setOngoing(false)
                            .setSmallIcon(R.drawable.download)
                            .setContentTitle("等待下载")
                            .setContentText("进度：")
                            .setTicker("开始下载")
                            .setProgress(100, progress, false);

                    nm.notify(DOWNLOAD_NOTIFICATION_ID, builder.build());
                    while (progress < 100) {
                        //获取已下载的大小并乘以10
                        int currentMp3LengthM10 = (int) ((FileUtils.getFileLength(FileUtils.getSDCARD_ROOT() + "aMp3/" + mp3Info.getMp3Name())) * 10);
                        Log.e(TAG, "currentMp3LengthM10 is " + currentMp3LengthM10 + "");
                        //获取文件原本的大小并乘以10
                        String[] temp = mp3Info.getMp3Size().split("[M]");
                        int mp3LengthM10 = (int) (Double.valueOf(temp[0]) * 10);
                        //计算百分比，不四舍五入了
                        progress = (int) ((1.0 * currentMp3LengthM10 / mp3LengthM10) * 100);
                        builder.setProgress(100, progress, false)
                                .setContentTitle("下载中")
                                .setContentText("进度：" + progress + "%");
                        nm.notify(DOWNLOAD_NOTIFICATION_ID, builder.build());
                        try {
                            // Sleep for 1 seconds
                            Thread.sleep(1 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "progress is " + progress + "\n" + "currentMp3LengthM10 is " + currentMp3LengthM10 + "\n" + "mp3LengthM10 is " + mp3LengthM10);
                    }
                    //设置下载完成后的通知
                    builder.setContentTitle("下载完成")
                            .setSmallIcon(R.drawable.cloud_done)
                            .setDefaults(Notification.DEFAULT_SOUND);
                    nm.notify(DOWNLOAD_NOTIFICATION_ID, builder.build());
                    //下载完成发送广播
                    Intent intent = new Intent("com.example.luhui1hao.OBTAINED");
                    sendBroadcast(intent);
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
