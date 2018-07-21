package com.hotactress.hot;

import android.os.Environment;

public class Config {
    private static Config instance;
    public static final String YOUTUBE_API_KEY="AIzaSyAAHawn3ItAopDRDqdHIOyUykCHZ1XWD1k";
    public static final String APP_HOME_DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getPath() + "Download/HotApp";
    public static final String VIDEO_META_URL = "https://videoapis.lolmenow.com//api/v1/urls/format";

    private Config(){

    }



    public static Config getInstance() {

        if (instance == null){
            instance = new Config();
        }
        return instance;
    }

}
