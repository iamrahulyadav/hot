package com.hotactress.hot.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.activities.ChatMainActivity;
import com.hotactress.hot.activities.UsersActivity;
import com.hotactress.hot.adapters.ChatsAdapter;
import com.hotactress.hot.adapters.FriendsAdapter;
import com.hotactress.hot.models.Conv;
import com.hotactress.hot.models.Friend;
import com.hotactress.hot.utils.Gen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;
    private List<Conv> convList;
    ChatsAdapter chatsAdapter;
    private static final String TAG = ChatsFragment.class.getSimpleName();


    private String mCurrent_user_id;
    private View mMainView;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        convList = new ArrayList<>();
        chatsAdapter = new ChatsAdapter(convList, mCurrent_user_id, getActivity());

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);
        mConvList.setAdapter(chatsAdapter);

        View.OnClickListener goToAllUsersActivity = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.startActivity(getActivity(), false, UsersActivity.class );
            }
        };

        mMainView.findViewById(R.id.chat_no_result_layout).setOnClickListener(goToAllUsersActivity);

        mConvDatabase.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Conv> newConvList = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    Conv conv = child.getValue(Conv.class);
                    conv.setId(child.getKey());
                    newConvList.add(conv);
                }

                // notify data change
                convList.removeAll(convList);
                convList.addAll(newConvList);
                if(convList.size() == 0) {
                    mMainView.findViewById(R.id.chat_no_result_layout).setVisibility(View.VISIBLE);
                    mConvList.setVisibility(View.GONE);
                } else {
                    mConvList.setVisibility(View.VISIBLE);
                    mMainView.findViewById(R.id.chat_no_result_layout).setVisibility(View.GONE);
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

        // Inflate the layout for this fragment
        return mMainView;
    }
}