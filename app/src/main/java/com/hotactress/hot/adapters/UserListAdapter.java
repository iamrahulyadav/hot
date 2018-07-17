package com.hotactress.hot.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotactress.hot.R;
import com.hotactress.hot.activities.ProfileActivity;
import com.hotactress.hot.activities.UsersActivity;
import com.hotactress.hot.models.UserProfile;
import com.hotactress.hot.utils.Gen;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by shubham on 19/12/17.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

    public List<UserProfile> usersList;
    private Activity activity;
    private static final String TAG = UserListAdapter.class.getSimpleName();


    public UserListAdapter(List<UserProfile> usersList, Activity activity){
        this.usersList = usersList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder usersViewHolder, int position) {
        final UserProfile user = usersList.get(position);

        usersViewHolder.setDisplayName(user.getName());
        usersViewHolder.setUserStatus(user.getStatus());
        usersViewHolder.setUserImage(user.getThumbImage());

        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.startActivity(activity, false, ProfileActivity.class, "user_id", user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserStatus(String status){
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image){
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.mipmap.default_avatar).into(userImageView);
        }
    }
}
