package com.hotactress.hot.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.MyApplication;
import com.hotactress.hot.R;
import com.hotactress.hot.activities.ChatActivity;
import com.hotactress.hot.activities.ProfileActivity;
import com.hotactress.hot.models.Friend;
import com.hotactress.hot.models.Profile;
import com.hotactress.hot.models.UserProfile;
import com.hotactress.hot.utils.FirebaseUtil;
import com.hotactress.hot.utils.Gen;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by shubham on 19/12/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{

    public List<Friend> friendList;
    private static final String TAG = FriendsAdapter.class.getSimpleName();
    Activity activity;

    public FriendsAdapter(List<Friend> friendList, Activity activity){
        this.friendList = friendList;
        this.activity = activity;
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
        FirebaseUtil.getUsersRefForUser(friend.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserProfile user = dataSnapshot.getValue(UserProfile.class);
                friendViewHolder.setName(user.getName());
                friendViewHolder.setUserImage(user.getThumbImage());
                friendViewHolder.setUserOnline(friend.getDate());

                friendViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Click Event for each item.
                                if(i == 0){
                                    Gen.startActivity(activity, false, ProfileActivity.class, "user_id", friend.getId());
                                } else if(i == 1){
                                    Intent chatIntent = new Intent(activity, ChatActivity.class);
                                    chatIntent.putExtra("user_id", friend.getId());
                                    chatIntent.putExtra("user_name", user.getName());
                                    activity.startActivity(chatIntent);
                                }
                            }
                        });

                        builder.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        public void setUserImage(String thumb_image){
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
