package com.hotactress.hot.adapters;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.activities.ChatActivity;
import com.hotactress.hot.models.Messages;
import com.hotactress.hot.utils.FirebaseUtil;
import com.hotactress.hot.utils.GetTimeAgo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private static final String TAG = MessageAdapter.class.getSimpleName();
    private List<Messages> mMessageList;
    private Activity activity;

    public MessageAdapter(List<Messages> mMessageList, Activity activity) {
        this.mMessageList = mMessageList;
        this.activity = activity;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);
        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, timeText;
        public CircleImageView profileImage;
        public TextView displayName;
        public PhotoView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            timeText = (TextView) view.findViewById(R.id.time_text_layout);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (PhotoView) view.findViewById(R.id.message_image_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        final Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();

        FirebaseUtil.getUsersRefForUser(from_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumbImage").getValue().toString();

                viewHolder.displayName.setText(name);
                Picasso.get().load(image)
                        .placeholder(R.mipmap.default_avatar).into(viewHolder.profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

        viewHolder.timeText.setText(GetTimeAgo.getTimeAgo(c.getTime()));
        if(message_type.equals("text")) {
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
        } else {

            DisplayMetrics dis = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dis);

            int height = dis.heightPixels;
            int width = dis.widthPixels;

            viewHolder.messageImage.setMinimumHeight(height);
            viewHolder.messageImage.setMinimumWidth(width);
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.VISIBLE);

            Picasso.get().load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.mipmap.default_avatar).into(viewHolder.messageImage, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(c.getMessage()).placeholder(R.mipmap.default_avatar).into(viewHolder.messageImage);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}