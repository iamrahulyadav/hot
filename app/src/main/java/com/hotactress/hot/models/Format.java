package com.hotactress.hot.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Format implements Serializable {

    private static final BigInteger BYTE = BigInteger.valueOf(1);
    private static final BigInteger KILO_BYTE = BigInteger.valueOf(1000);
    private static final BigInteger MEGA_BYTE = BigInteger.valueOf(1000000);
    private static final BigInteger GIGA_BYTE = BigInteger.valueOf(1000000000);

    String format;
    Format.Source source;
    String url;
    String ext;
    Integer audioBitRate;
    BigInteger fileSize;
    Integer height;
    String formatNote;

    public Format(Source source, String url, String ext, Integer audioBitRate, BigInteger fileSize, Integer height, String formatNote, String format) {
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
                BigInteger fileSize = new BigInteger(jsonObject.has("filesize") ? jsonObject.getString("filesize"): "-1");
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

    public Boolean isValidVideo() { return isValid() && height > 0; };

    public String displayString(){
        if (source == Source.YOUTUBE){
//            return format;
            return String.format("%s %sp  -- %s", ext, height, readableFileSize());
        }
        return String.format("%sp %s  ~%s", height, ext, readableFileSize());
    }

    public String readableFileSize(){

        if (fileSize.compareTo(BigInteger.ONE) < 0)
            return "";
        if (fileSize.compareTo(BYTE) < 0)
            return String.format("%s B", fileSize.divide(BYTE).intValue());
        else if (fileSize.compareTo(KILO_BYTE) > 0 && fileSize.compareTo(MEGA_BYTE) < 0)
            return String.format("%s KB", fileSize.divide(KILO_BYTE));
        else if (fileSize.compareTo(MEGA_BYTE) > 0 && fileSize.compareTo(GIGA_BYTE) < 0)
            return String.format("%s MB", fileSize.divide(MEGA_BYTE));
        else return String.format("%s GB", fileSize.divide(GIGA_BYTE));
    }

}
