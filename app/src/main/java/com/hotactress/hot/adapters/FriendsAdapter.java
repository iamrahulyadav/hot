package com.hotactress.hot.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotactress.hot.R;
import com.hotactress.hot.models.Friend;
import com.hotactress.hot.models.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by shubham on 19/12/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

    public List<Friend> friendList;
    private static final String TAG = FriendsAdapter.class.getSimpleName();


    public FriendsAdapter(List<Friend> friendList){
        this.friendList = friendList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder friendViewHolder, int position) {
        final Friend friend = friendList.get(position);

        friendViewHolder.setDate(friend.getDate());
//        friendViewHolder.setName();
//
//
//        usersViewHolder.setDisplayName(user.getName());
//        usersViewHolder.setUserStatus(user.getStatus());
//        usersViewHolder.setUserImage(user.getThumbImage());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);
        }

        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.mipmap.default_avatar).into(userImageView);
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);
            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
