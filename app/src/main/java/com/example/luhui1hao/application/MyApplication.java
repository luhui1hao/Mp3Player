package com.example.luhui1hao.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by luhui1hao on 2015/12/5.
 */
public class MyApplication extends Application {
    private static Context context;
    public static String BASE_URL = "http://192.168.3.12:8080/mp3/";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

    public static void setURL(String IP_ADDRESS){
        BASE_URL = "http://" + IP_ADDRESS + ":8080/mp3/";
    }
}
