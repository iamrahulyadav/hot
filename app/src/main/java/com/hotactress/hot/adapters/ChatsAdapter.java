package com.hotactress.hot.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.activities.ChatActivity;
import com.hotactress.hot.models.Conv;
import com.hotactress.hot.models.UserProfile;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by shubham on 19/12/17.
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{

    public List<Conv> convList;
    private static final String TAG = ChatsAdapter.class.getSimpleName();
    Activity activity;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;


    public ChatsAdapter(List<Conv> convList, String userId, Activity activity){
        this.convList = convList;
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference("messages").child(userId);
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder convViewHolder, int position) {
        final Conv conv = convList.get(position);
        final String list_user_id = conv.getId();

        Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String type = dataSnapshot.child("type").getValue().toString();
                String data = dataSnapshot.child("message").getValue().toString();

                if(!type.equals("text")) {
                    data = "[IMAGE]";
                }
                convViewHolder.setMessage(data, conv.isSeen());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final UserProfile userProfile = UserProfile.createUserFromDataSnapshot(list_user_id, dataSnapshot);

                if(dataSnapshot.hasChild("online")) {
                    String userOnline = dataSnapshot.child("online").getValue().toString();
                    convViewHolder.setUserOnline(userOnline);
                }

                convViewHolder.setName(userProfile.getName());
                convViewHolder.setUserImage(userProfile.getThumbImage());

                convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chatIntent = new Intent(activity, ChatActivity.class);
                        byte []someByteArray = SerializationUtils.serialize(userProfile);
                        chatIntent.putExtra("user_serialized", someByteArray);
                        chatIntent.putExtra("user_id", list_user_id);
                        chatIntent.putExtra("user_name", userProfile.getName());
                        activity.startActivity(chatIntent);
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
        return convList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMessage(String message, boolean isSeen){
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(message);
            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }
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
                userOnlineView.setVisibility(View.GONE);
            }
        }
    }
}
