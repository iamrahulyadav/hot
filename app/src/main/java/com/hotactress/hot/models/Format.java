package com.hotactress.hot.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Format {
    String format;
    Format.Source source;
    String url;
    String ext;
    Integer audioBitRate;
    Integer fileSize;
    Integer height;
    String formatNote;

    public Format(Source source, String url, String ext, Integer audioBitRate, Integer fileSize, Integer height, String formatNote, String format) {
        this.source = source;
        this.url = url;
        this.ext = ext;
        this.audioBitRate = audioBitRate;
        this.fileSize = fileSize;
        this.height = height;
        this.formatNote = formatNote;
        this.format = format;
    }


    public enum Source{
        YOUTUBE,
        DAILYMOTION
    }

    public static List<Format> createFormatForYoutube(JSONArray jsonArray){
        List<Format> list = new ArrayList<>();
        for (int i = 0 ; i < jsonArray.length() ; i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Source source = Source.YOUTUBE;
                String url = jsonObject.has("url") ? jsonObject.getString("url"): null;
                String ext = jsonObject.has("ext") ? jsonObject.getString("ext"): null;
                Integer audioBitRate = jsonObject.has("abr") ? jsonObject.getInt("abr"): -1;
                Integer fileSize = jsonObject.has("1163506395") ? jsonObject.getInt("1163506395"): -1;
                Integer height = jsonObject.has("height") ? jsonObject.getInt("height"): -1;
                String formatNote = jsonObject.has("format_note") ? jsonObject.getString("format_note"): null;
                String format = jsonObject.has("format") ?  jsonObject.getString("format"): null;
                Format f = new Format(source, url, ext, audioBitRate, fileSize, height, formatNote, format);
                list.add(f);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public Boolean isValid(){
        return url != null && audioBitRate > 0;
    }

    public String displayString(){
        if (source == Source.YOUTUBE){
            return format;
        }
        return String.format("%sp %s", height, ext);
    }

}
