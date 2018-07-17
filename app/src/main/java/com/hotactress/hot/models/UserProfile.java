package com.hotactress.hot.models;

import lombok.Data;

/**
 * Created by shubhamagrawal on 06/07/18.
 */


@Data
public class UserProfile {

    String id;
    String name;
    String email;
    String status;
    String thumbImage;
    String image;
    String deviceToken;
}

