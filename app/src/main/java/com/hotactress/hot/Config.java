package com.hotactress.hot;

import android.os.Environment;

public class Config {
    private static Config instance;
    public static String YOUTUBE_API_KEY="AIzaSyAAHawn3ItAopDRDqdHIOyUykCHZ1XWD1k";
    public static String APP_HOME_DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getPath() + "Download/HotApp";

    private Config(){

    }



    public static Config getInstance() {

        if (instance == null){
            instance = new Config();
        }
        return instance;
    }
}
