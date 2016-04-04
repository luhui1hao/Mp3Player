package com.example.luhui1hao.ioutils;

import android.os.Environment;
import android.util.Log;

import com.example.luhui1hao.model.Mp3Info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhui1hao on 2015/12/5.
 */
public class FileUtils {
    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    /**
     * 获取当前外部存储设备的目录
     */
    public static String getSDCARD_ROOT() {
        return SDCARD_ROOT;
    }

    /**
     * 在SD卡上创建目录
     */
    public File createSDDir(String dirName) {
        File dir = new File(SDCARD_ROOT + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 在SD卡上创建文件
     */
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDCARD_ROOT + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public static boolean isFileExist(String dirName, String fileName) {
        Log.d("mars.download.FileUtils", "isFileExist has run");
        File file = new File(SDCARD_ROOT + dirName + fileName);
        return file.exists();
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public static boolean isFileExist2(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public File write2SDFromInput(String dirName, String fileName,
                                  InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(dirName);
            file = creatSDFile(dirName + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String path) throws Exception {
        File file = new File(path);
        return file.delete();
    }

    /**
     * 读取目录中的Mp3文件的名字和大小
     */
    public static List<Mp3Info> getMp3Files(String path) {
        List<Mp3Info> dMp3Infos = new ArrayList<Mp3Info>();
        File file = new File(SDCARD_ROOT + File.separator + path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".mp3")) {
                Mp3Info mp3Info = new Mp3Info();
                //写入mp3文件的路径
                mp3Info.setMp3Path(files[i].getPath());
                //写入mp3的名字
                mp3Info.setMp3Name(files[i].getName());
                //处理文件大小数据
                long mySize = (long) ((files[i].length() / 1024 / 10.24 + 5) / 10);
                mp3Info.setMp3Size(mySize / 10.0 + "M");

                //一下操作是不合理的，应该予以改正

                String temp[] = mp3Info.getMp3Name().split("\\.");
                String eLrcName = temp[0] + ".lrc";
                //写入lrc文件的路径
                mp3Info.setLrcPath(SDCARD_ROOT + File.separator + path + eLrcName);
                if (isFileExist("aMp3/", eLrcName)) {
                    mp3Info.setLrcName(eLrcName);
                }
                dMp3Infos.add(mp3Info);
            }
        }
        return dMp3Infos;
    }

    public String readLrc(String path) {
        System.out.println(path);
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * @param path
     * @param lists
     * @return 用于遍历SD卡，找出mp3文件
     */
    public List<Mp3Info> traverseSDCard(String path, List<Mp3Info> lists) {
        List<Mp3Info> mLists = lists;
        //根据路径创建File对象
        File file = new File(path);
        //列出这个目录下的所有文件或目录
        File[] files = file.listFiles();
        //遍历它，如果是目录则继续遍历，如果是文件则看是否是mp3文件
        for (File ffile : files) {
            //如果是目录，继续遍历
            if (ffile.isDirectory()) {
                mLists = traverseSDCard(ffile.getPath(), mLists);
            } else {
                //看是不是以.mp3结尾的文件
                if (ffile.getName().endsWith(".mp3")) {
                    Mp3Info mp3Info = new Mp3Info();
                    //写入它的路径
                    mp3Info.setMp3Path(ffile.getPath());
                    mp3Info.setMp3Name(ffile.getName());
                    //处理文件大小数据
                    int mySize = (int) ((ffile.length() / 1024 / 10.24 + 5) / 10);
                    mp3Info.setMp3Size(mySize / 10.0 + "M");

                    mLists.add(mp3Info);
                }
            }
        }
        return mLists;
    }

    /**
     * 获取文件的大小,以M为单位，精确到小数点后一位
     */
    public static double getFileLength(String path){
        File file = new File(path);
        double length = ((int)((file.length() / 1024 / 10.24 + 5) / 10)) / 10.0;//目的是精确到小数点后一位，所以要乘10
        return length;
    }
}
