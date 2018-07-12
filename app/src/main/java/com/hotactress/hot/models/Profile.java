package com.hotactress.hot.models;

import lombok.Data;

/**
 * Created by shubhamagrawal on 06/07/18.
 */


@Data
public class Profile {

    String aid;
    String image;
//    Double aspect;
    String name;
    String profile;
    String time;
    String title;
    String titleid;

    public String getImageId(){
        return image.split("=")[1];
    }

//    public void setAspect(Object aspect) {
//        if(aspect == null) {
//            this.aspect = 1.0;
//        } else {
//            this.aspect = Double.valueOf(aspect.toString());
//        }
//    }
}
