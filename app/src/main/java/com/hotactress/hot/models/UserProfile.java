package com.hotactress.hot.models;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by shubhamagrawal on 06/07/18.
 */


@Data
public class UserProfile implements Serializable {

    String id;
    String name;
    String email;
    String status;
    String thumbImage;
    String image;
    String deviceToken;

    public static UserProfile createUserFromDataSnapshot(String id, DataSnapshot dataSnapshot){
        UserProfile u = new UserProfile();
        u.id = id;
        u.name = dataSnapshot.child("name").getValue().toString();
        u.email = dataSnapshot.child("email").getValue().toString();
        u.status = dataSnapshot.child("status").getValue().toString();
        u.thumbImage = dataSnapshot.child("thumbImage").getValue().toString();
        u.image = dataSnapshot.child("image").getValue().toString();
        u.deviceToken = dataSnapshot.child("deviceToken").getValue().toString();
        return u;
    }
}

