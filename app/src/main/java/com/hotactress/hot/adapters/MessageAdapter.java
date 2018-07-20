package com.hotactress.hot.adapters;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.models.Messages;
import com.hotactress.hot.models.UserProfile;
import com.hotactress.hot.utils.FirebaseUtil;
import com.hotactress.hot.utils.GetTimeAgo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    private static final String TAG = MessageAdapter.class.getSimpleName();
    private List<Messages> mMessageList;
    private Activity activity;
    private String currentUserId;
    private UserProfile otherUser;

    public MessageAdapter(List<Messages> mMessageList, Activity activity, String currentUserId, UserProfile otherUser) {
        this.mMessageList = mMessageList;
        this.activity = activity;
        this.currentUserId = currentUserId;
        this.otherUser = otherUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        if (viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_sent_message, parent, false);
            return new SentMessageHolder(view);
        }else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_received_message, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

//    public class MessageViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView messageText, timeText;
//        public CircleImageView profileImage;
//        public TextView displayName;
//        public PhotoView messageImage;
//
//        public MessageViewHolder(View view) {
//            super(view);
//
//            timeText = (TextView) view.findViewById(R.id.time_text_layout);
//            messageText = (TextView) view.findViewById(R.id.message_text_layout);
//            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
//            displayName = (TextView) view.findViewById(R.id.name_text_layout);
//            messageImage = (PhotoView) view.findViewById(R.id.message_image_layout);
//
//        }
//    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {

        final Messages message = mMessageList.get(i);


        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
        }


//        FirebaseUtil.getUsersRefForUser(message.getFrom()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String name = dataSnapshot.child("name").getValue().toString();
//                String image = dataSnapshot.child("thumbImage").getValue().toString();
//
//                viewHolder.displayName.setText(name);
//                Picasso.get().load(image)
//                        .placeholder(R.mipmap.default_avatar).into(viewHolder.profileImage);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, databaseError.getMessage());
//            }
//        });

//        viewHolder.timeText.setText(GetTimeAgo.getTimeAgo(c.getTime()));
//        if(message_type.equals("text")) {
//            viewHolder.messageText.setText(c.getMessage());
//            viewHolder.messageImage.setVisibility(View.GONE);
//            viewHolder.messageText.setVisibility(View.VISIBLE);
//        } else {
//
//            DisplayMetrics dis = new DisplayMetrics();
//            activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
//
//            int height = dis.heightPixels;
//            int width = dis.widthPixels;
//
//            viewHolder.messageImage.setMinimumHeight(height);
//            viewHolder.messageImage.setMinimumWidth(width);
//            viewHolder.messageText.setVisibility(View.GONE);
//            viewHolder.messageImage.setVisibility(View.VISIBLE);
//
//            Picasso.get().load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE)
//                    .placeholder(R.mipmap.default_avatar).into(viewHolder.messageImage, new Callback() {
//                @Override
//                public void onSuccess() {
//
//                }
//                @Override
//                public void onError(Exception e) {
//                    Picasso.get().load(c.getMessage()).placeholder(R.mipmap.default_avatar).into(viewHolder.messageImage);
//                }
//            });
//        }
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = mMessageList.get(position);

        if (message.getFrom().equals(currentUserId)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        CircleImageView profileImage;
        LinearLayout imagePlaceHolderLayout;
        ImageView messageImage;


        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.chat_item_sent_text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            imagePlaceHolderLayout = itemView.findViewById(R.id.chat_item_received_image_layout_id);
            profileImage = (CircleImageView) itemView.findViewById(R.id.image_message_profile);
            messageImage = itemView.findViewById(R.id.chat_item_received_image_view_id);

        }

        void bind(final Messages message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(GetTimeAgo.getTimeAgo(message.getTime()));
            Picasso.get().load(otherUser.getThumbImage())
                            .placeholder(R.mipmap.default_avatar).into(profileImage);

                    nameText.setText(otherUser.getName());
                    nameText.setText(otherUser.getName());

//            FirebaseUtil.getUsersRefForUser(message.getFrom()).addValueEventListener(new ValueEventListener() {
//                  @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    String name = dataSnapshot.child("name").getValue().toString();
//                    String image = dataSnapshot.child("thumbImage").getValue().toString();
//                    nameText.setText(name);
////                    viewHolder.displayName.setText(name);
//                    Picasso.get().load(image)
//                            .placeholder(R.mipmap.default_avatar).into(profileImage);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d(TAG, databaseError.getMessage());
//                }
//            });

            if (message.getType().equals("text")){
                imagePlaceHolderLayout.setVisibility(View.GONE);
            }else{
                messageText.setVisibility(View.GONE);
                imagePlaceHolderLayout.setVisibility(View.VISIBLE);
                int width = imagePlaceHolderLayout.getMeasuredWidth();
                ViewGroup.LayoutParams layoutParams = imagePlaceHolderLayout.getLayoutParams();
                layoutParams.width = layoutParams.height = width;
                imagePlaceHolderLayout.setLayoutParams(layoutParams);

                Picasso.get().load(message.getMessage()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.mipmap.default_avatar).into(messageImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(message.getMessage()).placeholder(R.mipmap.default_avatar).into(messageImage);
                    }
                });

            }

            // Insert the profile image from the URL into the ImageView.
//            Picasso.get().load(messag)
//            Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        LinearLayout imagePlaceHolderLayout;
        CircleImageView profileImage;
        ImageView messageImage;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.chat_item_sent_text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.chat_item_sent_text_message_time);
            imagePlaceHolderLayout = itemView.findViewById(R.id.chat_item_sent_image_layout_id);
            messageImage = itemView.findViewById(R.id.chat_item_sent_image_view_id);

//            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
//            profileImage = (CircleImageView) itemView.findViewById(R.id.image_message_profile);
        }


        void bind(final Messages message) {
            messageText.setText(message.getMessage());

            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(GetTimeAgo.getTimeAgo(message.getTime()));
            messageText.setText(message.getMessage());

            if (message.getType().equals("text")){
                imagePlaceHolderLayout.setVisibility(View.GONE);
            }else{
                DisplayMetrics dis = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
                int width = dis.widthPixels;
                messageText.setVisibility(View.GONE);
                imagePlaceHolderLayout.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams = imagePlaceHolderLayout.getLayoutParams();
                layoutParams.height = width - 100;
                imagePlaceHolderLayout.setLayoutParams(layoutParams);


                Picasso.get().load(message.getMessage()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.mipmap.default_avatar).into(messageImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(message.getMessage()).placeholder(R.mipmap.default_avatar).into(messageImage);
                    }
                });

            }

        }
    }

}