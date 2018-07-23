package com.hotactress.hot.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hotactress.hot.utils.Gen;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Video implements Serializable {
    public String videoId;
    public String title;
    public String category;

    public String[] getThumbnailUrls(){
        return Gen.getYoutubeCaptionUrls(this.videoId);
    }
    public String randomThumbnailUrl(){
        String []s = Gen.getYoutubeCaptionUrls(this.videoId);
        return s[new Random().nextInt(s.length)];
    }

    public Video(){}

    public Video(String videoId, String title, String category){
        this.videoId = videoId;
        this.title = title;
        this.category = category;
    }

//    public static List<Video> getData(){
//        List<Video> videos = new ArrayList<>();
//        videos.add(new Video("9NUXCAZs5VU", "Kunal Kamra | Stand-Up Comedy Part 1 (2018)", "Humour & Sattire"));
//        videos.add(new Video("LNSQ-nx_IAI", "Shut Up Ya Kunal - Episode 2 : Congress", "Humour & Sattire"));
//        videos.add(new Video("tVxQ9-HvVFU", "Shut Up Ya Kunal - Episode 3 : JNU Students", "Humour & Sattire"));
//        videos.add(new Video("DWOvKzMn344", "Lage Raho Munnabhai – Only the Funny Scenes", "Humour & Sattire"));
//        videos.add(new Video("5QDKX5ExXqM", "Hulchul | Hindi Movies 2016", "Humour & Sattire"));
//        videos.add(new Video("jEDgcZ_wFjo", "Jaane Tu Ya Jaane Na 2006", "Humour & Sattire"));
//        videos.add(new Video("n2rr1P8rHig", "Paani Pilaai Jao, Te Qawwali Karwai Jao", "Humour & Sattire"));
//        videos.add(new Video("uEwjePUgw_g", "Kapil welcomes Navjot Kaur Sidhu to the show", "Humour & Sattire"));
//        return videos;
//    }

    public String youtubeUrl(){
        return Gen.getYoutubeUrlForId(this.videoId);
    }

    public String getUrl(){
        return youtubeUrl();
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(videoId);
//        dest.writeString(title);
//        dest.writeString(category);
//    }

    public String getDownloadbleFilename(){
        return String.format("%s_%s", title, DateTime.now().toDateTimeISO());
    }
}
